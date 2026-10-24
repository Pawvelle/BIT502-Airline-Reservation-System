import java.util.Arrays;

/** A session booking for one passenger/contact and their travelling party. */
public class Booking {
    private final int bookingNumber;
    private final String passengerName;
    private final Flight flight;
    private final boolean business;
    private final int[] seatNumbers;
    private boolean active = true;

    public Booking(int bookingNumber, String passengerName, Flight flight,
            boolean business, int[] seatNumbers) {
        this.bookingNumber = bookingNumber;
        this.passengerName = passengerName;
        this.flight = flight;
        this.business = business;
        this.seatNumbers = seatNumbers.clone();
    }

    public int getBookingNumber() {
        return bookingNumber;
    }

    public boolean isActive() {
        return active;
    }

    public boolean cancel() {
        if (!active) {
            return false;
        }
        flight.releaseSeats(business, seatNumbers);
        active = false;
        return true;
    }

    public void displayBooking() {
        System.out.println("Booking number: " + bookingNumber + (active ? " [ACTIVE]" : " [CANCELLED]"));
        System.out.println("Passenger/contact: " + passengerName);
        System.out.println("Day " + flight.getDay() + " | Flight: " + flight.getFlightNumber()
                + " | " + flight.getDeparture() + " -> " + flight.getDestination());
        System.out.println("Cabin: " + (business ? "Business" : "Economy")
                + " | Seats: " + Arrays.toString(seatNumbers));
        System.out.println("------------------------------");
    }
}
