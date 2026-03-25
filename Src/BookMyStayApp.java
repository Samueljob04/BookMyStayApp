/**
 * BookMyStayApp - Application entry point for the Hotel Booking System.
 *
 * Goal: Demonstrates program start, prints a welcome message and version.
 *
 * @author BookMyStay
 * @version 1.0
 */
public class BookMyStayApp {
    /**
     * Main method - JVM entry point.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        final String appName = "BookMyStayApp - Hotel Booking System";
        final String version = "v1.0";

        System.out.println("Welcome to " + appName + " " + version);
        System.out.println("Application started.\n");

        // Use Case 2: Basic Room Types & Static Availability
        SingleRoom single = new SingleRoom();
        DoubleRoom doub = new DoubleRoom();
        SuiteRoom suite = new SuiteRoom();

        // Use Case 3: Centralized Room Inventory Management
        RoomInventory inventory = new RoomInventory();
        inventory.registerRoom(single, 5);
        inventory.registerRoom(doub, 3);
        inventory.registerRoom(suite, 1);

        System.out.println("--- Room Types & Inventory (snapshot) ---");
        System.out.println(single + " -> Available: " + inventory.getAvailability(single.getType()));
        System.out.println(doub + " -> Available: " + inventory.getAvailability(doub.getType()));
        System.out.println(suite + " -> Available: " + inventory.getAvailability(suite.getType()));
        System.out.println("Inventory snapshot: " + inventory.snapshot());
        System.out.println();

        // Use Case 4: Room Search & Availability Check (read-only)
        SearchService search = new SearchService();
        var available = search.searchAvailable(inventory, single, doub, suite);
        search.printResults(available, inventory);
        System.out.println();

        // Use Case 5: Booking Request (First-Come-First-Served)
        BookingQueue bookingQueue = new BookingQueue();
        Reservation r1 = new Reservation("Alice", single.getType(), 2);
        Reservation r2 = new Reservation("Bob", doub.getType(), 3);
        Reservation r3 = new Reservation("Carol", suite.getType(), 1);

        bookingQueue.submit(r1);
        bookingQueue.submit(r2);
        bookingQueue.submit(r3);

        System.out.println("--- Booking Queue State ---");
        System.out.println(bookingQueue);
        System.out.println("All queued requests (in arrival order):");
        while (!bookingQueue.isEmpty()) {
            System.out.println("  " + bookingQueue.poll());
        }
        System.out.println();

        // Use Case 6: Reservation Confirmation & Room Allocation
        // Create a new queue to demonstrate allocation (re-queue sample reservations)
        BookingQueue allocationQueue = new BookingQueue();
        allocationQueue.submit(new Reservation("Dave", single.getType(), 2));
        allocationQueue.submit(new Reservation("Eve", single.getType(), 1));
        allocationQueue.submit(new Reservation("Frank", suite.getType(), 2));

        BookingService bookingService = new BookingService();
        Map<Reservation, String> allocations = bookingService.processQueue(allocationQueue, inventory);

        System.out.println("--- Allocation Results ---");
        for (Map.Entry<Reservation, String> e : allocations.entrySet()) {
            Reservation r = e.getKey();
            String assigned = e.getValue();
            if (assigned != null) {
                System.out.println(r.getGuestName() + " confirmed: " + r.getRoomType() + " -> " + assigned);
            } else {
                System.out.println(r.getGuestName() + " could not be allocated: " + r.getRoomType());
            }
        }
        System.out.println("Current allocations by type: " + bookingService.getAllocations());
        System.out.println("Inventory after allocation: " + inventory.snapshot());
        System.out.println();

        // Use Case 10: Booking Cancellation & Inventory Rollback
        CancellationService cancellationService = new CancellationService();
        // pick any allocated id to cancel (if exists)
        String toCancel = null;
        for (Set<String> s : bookingService.getAllocations().values()) {
            if (!s.isEmpty()) { toCancel = s.iterator().next(); break; }
        }
        if (toCancel != null) {
            System.out.println("Cancelling reservation with id: " + toCancel);
            boolean ok = cancellationService.cancel(toCancel, bookingService, inventory, bookingHistory);
            System.out.println("Cancellation success: " + ok);
            System.out.println("Inventory after cancellation: " + inventory.snapshot());
            System.out.println("Rollback stack: " + cancellationService.rollbackStack());
        } else {
            System.out.println("No allocations to cancel.");
        }
        System.out.println();

        // Use Case 11: Concurrent Booking Simulation (Thread Safety)
        // Re-register inventory for simulation
        RoomInventory simInventory = new RoomInventory();
        simInventory.registerRoom(single, 10);
        BookingService simBookingService = new BookingService();
        try {
            ConcurrentBookingDemo.runConcurrentDemo(simInventory, simBookingService);
        } catch (InterruptedException ie) {
            System.err.println("Concurrent demo interrupted: " + ie.getMessage());
        }
        System.out.println();

        // Use Case 7: Add-On Service Selection
        Service breakfast = new Service("Breakfast", 8.50);
        Service airport = new Service("Airport Pickup", 25.00);

        ServiceManager serviceManager = new ServiceManager();
        // Attach services to allocation IDs produced earlier (if any)
        for (String type : bookingService.getAllocations().keySet()) {
            for (String allocId : bookingService.getAllocations().get(type)) {
                serviceManager.addService(allocId, breakfast);
                // add airport for suite only
                if (type.toLowerCase().contains("suite")) serviceManager.addService(allocId, airport);
            }
        }

        System.out.println("--- Services by Reservation (snapshot) ---");
        System.out.println(serviceManager.snapshot());
        for (Map.Entry<String, List<Service>> e : serviceManager.snapshot().entrySet()) {
            System.out.println(e.getKey() + " total service cost: " + String.format("%.2f", serviceManager.totalServiceCost(e.getKey())));
        }
        System.out.println();

        // Run Use Case 1 logic (if present)
        // UC1 class in the same Src folder provides a run() method for the use case
        try {
            UC1.run();
        } catch (Throwable t) {
            // keep the entry simple and resilient during early development
            System.err.println("Warning: UC1 could not be executed: " + t.getMessage());
        }

        System.out.println("Application terminated.");
    }
}
