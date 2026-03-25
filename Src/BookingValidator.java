/**
 * BookingValidator - validates Reservation objects and system constraints before processing.
 */
public class BookingValidator {
    public void validateReservation(Reservation r, RoomInventory inventory) throws ValidationException {
        if (r == null) throw new ValidationException("Reservation is null");
        if (r.getGuestName() == null || r.getGuestName().trim().isEmpty()) throw new ValidationException("Guest name required");
        if (r.getRoomType() == null || r.getRoomType().trim().isEmpty()) throw new ValidationException("Room type required");
        if (r.getNights() <= 0) throw new ValidationException("Nights must be positive");
        if (inventory == null) throw new ValidationException("Inventory not available");
        if (inventory.getAvailability(r.getRoomType()) <= 0) throw new ValidationException("Requested room type not available");
    }
}
