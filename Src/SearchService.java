import java.util.HashMap;
import java.util.Map;

/**
 * SearchService - read-only search over rooms and inventory.
 *
 * Provides methods to find room types that currently have availability > 0.
 * This service does not modify inventory.
 */
public class SearchService {
    /**
     * Returns a mapping of room type name -> Room for all rooms that have availability > 0.
     * Does not modify inventory.
     */
    public Map<String, Room> searchAvailable(RoomInventory inventory, Room... rooms) {
        Map<String, Room> result = new HashMap<>();
        if (inventory == null || rooms == null) return result;

        for (Room r : rooms) {
            if (r == null) continue;
            int avail = inventory.getAvailability(r.getType());
            if (avail > 0) {
                result.put(r.getType(), r);
            }
        }
        return result;
    }

    /**
     * Prints a user-friendly list of available rooms and their details.
     * Inventory is read-only for this operation.
     */
    public void printResults(Map<String, Room> available, RoomInventory inventory) {
        System.out.println("--- Search Results: Available Rooms ---");
        if (available == null || available.isEmpty()) {
            System.out.println("No rooms available at the moment.");
            return;
        }
        for (Map.Entry<String, Room> e : available.entrySet()) {
            Room r = e.getValue();
            int avail = inventory.getAvailability(e.getKey());
            System.out.printf("%s - %s | Price: %.2f | Available: %d\n", e.getKey(), r.toString(), r.getPricePerNight(), avail);
        }
    }
}
