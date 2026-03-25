import java.util.Stack;

/**
 * CancellationService - validates cancellations and performs rollback.
 */
public class CancellationService {
    private final Stack<String> releasedRoomIds = new Stack<>();

    /**
     * Cancel a confirmed reservation by roomId: validate exists, remove allocation, increment inventory, record rollback.
     * Returns true if cancellation succeeded.
     */
    public boolean cancel(String roomId, BookingService bookingService, RoomInventory inventory, BookingHistory history) {
        if (roomId == null || bookingService == null || inventory == null || history == null) return false;
        Reservation r = bookingService.getReservationForRoomId(roomId);
        if (r == null) return false; // unknown roomId

        String type = bookingService.findTypeByRoomId(roomId);
        if (type == null) return false;

        // record rollback id
        releasedRoomIds.push(roomId);

        // remove allocation and increment inventory
        boolean removed = bookingService.removeAllocation(roomId);
        if (!removed) return false;

        inventory.updateAvailability(type, +1);

        // remove from history if present
        // naive removal by identity/equality
        try {
            history.all().remove(r);
        } catch (UnsupportedOperationException u) {
            // history returns unmodifiable list; use reflection-free approach: assume history has no remove API
        }
        return true;
    }

    public Stack<String> rollbackStack() {
        return releasedRoomIds;
    }
}
