import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * BookingService - processes queued booking requests, assigns unique room IDs,
 * updates inventory and prevents double-booking.
 */
public class BookingService {
    private final Map<String, Set<String>> allocatedByType = new HashMap<>();
    private final Set<String> allAllocatedIds = new HashSet<>();
    private final Map<String, Integer> counters = new HashMap<>();
    private final Map<String, Reservation> allocationMap = new HashMap<>();

    private synchronized String generateRoomId(String roomType) {
        if (roomType == null) roomType = "ROOM";
        String base = roomType.replaceAll("\\s+", "").toUpperCase();
        int n = counters.getOrDefault(base, 0) + 1;
        counters.put(base, n);
        String id = base + "-" + String.format("%03d", n);
        if (allAllocatedIds.contains(id)) {
            id = base + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        allAllocatedIds.add(id);
        allocatedByType.computeIfAbsent(roomType, k -> new HashSet<>()).add(id);
        return id;
    }

    /**
     * Thread-safe single allocation API. Returns assigned roomId or null if unavailable.
     */
    public synchronized String allocate(Reservation r, RoomInventory inventory) {
        if (r == null || inventory == null) return null;
        String type = r.getRoomType();
        int avail = inventory.getAvailability(type);
        if (avail <= 0) return null;
        String roomId = generateRoomId(type);
        // update inventory (inventory may be non-thread-safe, so we keep update inside synchronized block)
        inventory.updateAvailability(type, -1);
        allocationMap.put(roomId, r);
        return roomId;
    }

    /**
     * Process the booking queue in FIFO order. For each reservation:
     * - if availability exists, allocate a unique room ID and decrement inventory
     * - otherwise, leave the reservation unconfirmed (mapped to null)
     * Returns a linked map preserving the request order mapping Reservation -> assignedRoomId (or null)
     */
    public Map<Reservation, String> processQueue(BookingQueue queue, RoomInventory inventory) {
        Map<Reservation, String> result = new LinkedHashMap<>();
        if (queue == null || inventory == null) return result;

        while (!queue.isEmpty()) {
            Reservation r = queue.poll();
            if (r == null) continue;
            String type = r.getRoomType();
            int avail = inventory.getAvailability(type);
            if (avail > 0) {
                String roomId = generateRoomId(type);
                inventory.updateAvailability(type, -1);
                allocationMap.put(roomId, r);
                result.put(r, roomId);
            } else {
                result.put(r, null);
            }
        }
        return result;
    }

    public Map<String, Set<String>> getAllocations() {
        Map<String, Set<String>> copy = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : allocatedByType.entrySet()) {
            copy.put(e.getKey(), Collections.unmodifiableSet(e.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    /**
     * Returns a copy of roomId -> Reservation allocation map.
     */
    public Map<String, Reservation> getAllocationMap() {
        return Collections.unmodifiableMap(new HashMap<>(allocationMap));
    }

    /**
     * Restore allocations from persistent state. Replaces current allocation state.
     */
    public synchronized void restoreAllocations(Map<String, Set<String>> allocations, Map<String, Reservation> allocMap) {
        allocatedByType.clear();
        allAllocatedIds.clear();
        allocationMap.clear();
        counters.clear();
        if (allocations != null) {
            for (Map.Entry<String, Set<String>> e : allocations.entrySet()) {
                String type = e.getKey();
                Set<String> ids = new HashSet<>(e.getValue());
                allocatedByType.put(type, ids);
                for (String id : ids) {
                    allAllocatedIds.add(id);
                    // attempt to seed counters from ids like TYPE-###
                    int dash = id.lastIndexOf('-');
                    if (dash > 0 && dash < id.length()-1) {
                        String suffix = id.substring(dash+1);
                        try {
                            int n = Integer.parseInt(suffix);
                            String base = id.substring(0, dash);
                            int existing = counters.getOrDefault(base, 0);
                            counters.put(base, Math.max(existing, n));
                        } catch (NumberFormatException ex) {
                            // ignore
                        }
                    }
                }
            }
        }
        if (allocMap != null) allocationMap.putAll(allocMap);
    }

    /**
     * Find reservation associated with a room id, if any.
     */
    public Reservation getReservationForRoomId(String roomId) {
        return allocationMap.get(roomId);
    }

    /**
     * Find room type for a room id by consulting the reservation mapping.
     */
    public String findTypeByRoomId(String roomId) {
        Reservation r = allocationMap.get(roomId);
        return r != null ? r.getRoomType() : null;
    }

    /**
     * Remove an allocation by room id. Returns true if removed.
     */
    public synchronized boolean removeAllocation(String roomId) {
        if (roomId == null) return false;
        Reservation r = allocationMap.remove(roomId);
        if (r == null) return false;
        allAllocatedIds.remove(roomId);
        Set<String> set = allocatedByType.get(r.getRoomType());
        if (set != null) set.remove(roomId);
        return true;
    }
}
