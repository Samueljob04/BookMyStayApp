import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BookingHistory - stores confirmed reservations in insertion order.
 */
public class BookingHistory {
    private final List<Reservation> history = new ArrayList<>();

    public void record(Reservation r) {
        if (r == null) return;
        history.add(r);
    }

    public List<Reservation> all() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    public int size() {
        return history.size();
    }
}
