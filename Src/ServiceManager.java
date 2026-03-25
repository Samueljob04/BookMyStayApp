import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServiceManager - associates reservations (by id) with selected add-on services.
 */
public class ServiceManager {
    private final Map<String, List<Service>> servicesByReservation = new HashMap<>();

    public void addService(String reservationId, Service service) {
        if (reservationId == null || service == null) return;
        servicesByReservation.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }

    public List<Service> getServices(String reservationId) {
        return servicesByReservation.getOrDefault(reservationId, Collections.emptyList());
    }

    public double totalServiceCost(String reservationId) {
        double sum = 0.0;
        for (Service s : getServices(reservationId)) sum += s.getPrice();
        return sum;
    }

    public Map<String, List<Service>> snapshot() {
        Map<String, List<Service>> copy = new HashMap<>();
        for (Map.Entry<String, List<Service>> e : servicesByReservation.entrySet()) {
            copy.put(e.getKey(), Collections.unmodifiableList(new ArrayList<>(e.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }
}
