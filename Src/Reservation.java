/**
 * Reservation - represents a guest booking intent (no allocation performed here).
 */
public class Reservation {
    private final String guestName;
    private final String roomType;
    private final int nights;

    public Reservation(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }

    @Override
    public String toString() {
        return String.format("Reservation[guest=%s, room=%s, nights=%d]", guestName, roomType, nights);
    }
}
