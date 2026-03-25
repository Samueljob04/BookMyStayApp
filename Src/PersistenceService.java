import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * PersistenceService - simple file-based persistence using Java serialization.
 * Stores inventory, allocation map and booking history to a single file.
 */
public class PersistenceService {
    public static class Snapshot implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public Map<String, Integer> inventory;
        public Map<String, Set<String>> allocations;
        public Map<String, Reservation> allocMap;
        public java.util.List<Reservation> history;
    }

    public void save(String path, RoomInventory inventory, BookingService bookingService, BookingHistory history) throws Exception {
        Snapshot s = new Snapshot();
        s.inventory = new HashMap<>(inventory.snapshot());
        s.allocations = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : bookingService.getAllocations().entrySet()) s.allocations.put(e.getKey(), new HashSet<>(e.getValue()));
        s.allocMap = new HashMap<>(bookingService.getAllocationMap());
        s.history = new java.util.ArrayList<>(history.all());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(s);
        }
    }

    public Snapshot load(String path) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (Snapshot) ois.readObject();
        }
    }
}
