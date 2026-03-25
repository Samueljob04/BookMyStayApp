import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ConcurrentBookingDemo - simulates concurrent booking submissions and processing.
 */
public class ConcurrentBookingDemo {
    public static void runConcurrentDemo(RoomInventory inventory, BookingService bookingService) throws InterruptedException {
        if (inventory == null || bookingService == null) return;

        // shared queue
        BookingQueue queue = new BookingQueue();
        // populate with many reservations
        for (int i = 0; i < 20; i++) {
            queue.submit(new Reservation("G" + i, "Single Room", 1));
        }

        ExecutorService producers = Executors.newFixedThreadPool(4);
        // producers add to queue concurrently
        for (int i = 0; i < 4; i++) {
            producers.submit(() -> {
                for (int j = 0; j < 5; j++) queue.submit(new Reservation(Thread.currentThread().getName() + "-R" + j, "Single Room", 1));
            });
        }
        producers.shutdown();
        producers.awaitTermination(2, TimeUnit.SECONDS);

        // consumers allocate concurrently
        ExecutorService consumers = Executors.newFixedThreadPool(3);
        List<String> allocated = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            consumers.submit(() -> {
                while (!queue.isEmpty()) {
                    Reservation r = queue.poll();
                    if (r == null) continue;
                    String id = bookingService.allocate(r, inventory);
                    if (id != null) {
                        synchronized (allocated) { allocated.add(id); }
                    }
                }
            });
        }

        consumers.shutdown();
        consumers.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("Concurrent allocations done. Count: " + allocated.size());
        System.out.println("Inventory after concurrent allocations: " + inventory.snapshot());
    }
}
