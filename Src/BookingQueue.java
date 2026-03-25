import java.util.LinkedList;
import java.util.Queue;

/**
 * BookingQueue - FIFO queue to collect Reservation requests in arrival order.
 */
public class BookingQueue {
    private final Queue<Reservation> queue = new LinkedList<>();

    /**
     * Enqueue a reservation request. Returns true if accepted.
     */
    public boolean submit(Reservation r) {
        if (r == null) return false;
        return queue.offer(r);
    }

    /**
     * Peek at the next reservation without removing.
     */
    public Reservation peek() {
        return queue.peek();
    }

    /**
     * Poll the next reservation (remove and return), used by allocation step later.
     */
    public Reservation poll() {
        return queue.poll();
    }

    /**
     * Returns true if no requests are queued.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Number of queued requests.
     */
    public int size() {
        return queue.size();
    }

    @Override
    public String toString() {
        return "BookingQueue[size=" + size() + ", next=" + peek() + "]";
    }
}
