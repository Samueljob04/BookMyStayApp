/**
 * Abstract Room - defines common attributes for all room types.
 *
 * Demonstrates abstraction, encapsulation and polymorphism for Use Case 2.
 */
public abstract class Room {
    private final String type;
    private final int beds;
    private final double sizesqm;
    private final double pricePerNight;

    public Room(String type, int beds, double sizesqm, double pricePerNight) {
        this.type = type;
        this.beds = beds;
        this.sizesqm = sizesqm;
        this.pricePerNight = pricePerNight;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public double getSizesqm() {
        return sizesqm;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    @Override
    public String toString() {
        return String.format("%s (beds=%d, size=%.1fm2, price=%.2f)", type, beds, sizesqm, pricePerNight);
    }
}
