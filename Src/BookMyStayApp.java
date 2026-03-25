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
