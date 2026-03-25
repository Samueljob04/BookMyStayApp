import java.util.List;
import java.util.Map;

/**
 * ReportService - generates simple reports from booking history or allocations.
 */
public class ReportService {
    public void printBookingHistory(BookingHistory history) {
        System.out.println("--- Booking History ---");
        for (Reservation r : history.all()) {
            System.out.println(r);
        }
    }

    public void printSummary(Map<String, Integer> inventorySnapshot, BookingHistory history) {
        System.out.println("--- Summary Report ---");
        System.out.println("Inventory snapshot: " + inventorySnapshot);
        System.out.println("Total confirmed bookings: " + history.size());
    }
}
