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

    private String generateRoomId(String roomType) {
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
}
