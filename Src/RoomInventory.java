/**
 * RoomInventory - centralized inventory management for room availability.
 *
 * Uses a HashMap<String,Integer> to map room type names to available counts.
 * Provides controlled methods to register room types, query availability and
 * update counts while keeping a single source of truth.
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RoomInventory {
    private final Map<String, Integer> inventory = new HashMap<>();

    /**
     * Register a room type with an initial available count.
     * If the room type already exists, the count will be replaced.
     */
    public void registerRoom(Room room, int initialCount) {
        if (room == null || initialCount < 0) return;
        inventory.put(room.getType(), initialCount);
    }

    /**
     * Get current availability for a room type. Returns 0 if not found.
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Set availability for a room type. Negative values are not allowed.
     */
    public void setAvailability(String roomType, int count) {
        if (roomType == null || count < 0) return;
        inventory.put(roomType, count);
    }

    /**
     * Update availability by delta (can be negative). Ensures non-negative final value.
     * Returns the new value.
     */
    public int updateAvailability(String roomType, int delta) {
        if (roomType == null) return 0;
        int current = inventory.getOrDefault(roomType, 0);
        int updated = Math.max(0, current + delta);
        inventory.put(roomType, updated);
        return updated;
    }

    /**
     * Returns an unmodifiable snapshot of the current inventory.
     */
    public Map<String, Integer> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(inventory));
    }

    @Override
    public String toString() {
        return "RoomInventory" + inventory.toString();
    }
}
