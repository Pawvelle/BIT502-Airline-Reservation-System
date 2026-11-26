import java.util.Arrays;

/**
 * Represents a round-trip passenger reservation covering an outbound flight
 * and a returning flight, along with the assigned seat numbers on each leg.
 */
public class Booking {
    private final int bookingNumber;
    private final String firstName;
    private final String lastName;
    private final Flight outboundFlight;
    private final boolean outboundBusiness;
    private final int[] outboundSeats;
    private final Flight returnFlight;
    private final boolean returnBusiness;
    private final int[] returnSeats;
    private boolean active = true;

    /**
     * Creates an active round-trip booking and clones seat arrays to preserve encapsulation.
     */
    public Booking(int bookingNumber, String firstName, String lastName,
            Flight outboundFlight, boolean outboundBusiness, int[] outboundSeats,
            Flight returnFlight, boolean returnBusiness, int[] returnSeats) {
        this.bookingNumber = bookingNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.outboundFlight = outboundFlight;
        this.outboundBusiness = outboundBusiness;
        this.outboundSeats = outboundSeats.clone();
        this.returnFlight = returnFlight;
        this.returnBusiness = returnBusiness;
        this.returnSeats = returnSeats.clone();
    }

    public int getBookingNumber() {
        return bookingNumber;
    }

    public String getPassengerFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Cancels this reservation and releases seats on both the outbound and return flights.
     *
     * @return true if the booking was active and cancelled; false if already cancelled
     */
    public boolean cancel() {
        if (!active) {
            return false;
        }
        outboundFlight.releaseSeats(outboundBusiness, outboundSeats);
        returnFlight.releaseSeats(returnBusiness, returnSeats);
        active = false;
        return true;
    }

    /** Prints a concise one-line summary for selecting a booking to cancel. */
    public String briefSummary() {
        return "#" + bookingNumber + " | " + getPassengerFullName()
                + " | " + outboundFlight.getDeparture() + " <-> " + outboundFlight.getDestination()
                + " (Days " + outboundFlight.getDay() + " & " + returnFlight.getDay() + ")";
    }

    /** Displays full details of both legs of the round-trip reservation. */
    public void displayBooking() {
        System.out.println("Booking number: " + bookingNumber + (active ? " [ACTIVE]" : " [CANCELLED]"));
        System.out.println("Passenger: " + getPassengerFullName());
        System.out.println("  Outbound: Day " + outboundFlight.getDay()
                + " | Flight " + outboundFlight.getFlightNumber()
                + " | " + outboundFlight.getDeparture() + " -> " + outboundFlight.getDestination()
                + " | Cabin: " + (outboundBusiness ? "Business" : "Economy")
                + " | Seats: " + Arrays.toString(outboundSeats));
        System.out.println("  Return:   Day " + returnFlight.getDay()
                + " | Flight " + returnFlight.getFlightNumber()
                + " | " + returnFlight.getDeparture() + " -> " + returnFlight.getDestination()
                + " | Cabin: " + (returnBusiness ? "Business" : "Economy")
                + " | Seats: " + Arrays.toString(returnSeats));
        System.out.println("------------------------------------------------------------");
    }
}
