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
