/** One scheduled flight. Each cabin uses an array to track numbered seats. */
public class Flight {
    private final int day;
    private final String flightNumber;
    private final String departure;
    private final String destination;
    private final boolean[] businessSeats;
    private final boolean[] economySeats;

    public Flight(int day, String flightNumber, String departure, String destination,
            int businessCapacity, int economyCapacity) {
        if (day < 1 || day > 10 || businessCapacity < 0 || economyCapacity < 0
                || businessCapacity > 16 || economyCapacity > 16
                || businessCapacity + economyCapacity < 4
                || businessCapacity + economyCapacity > 16) {
            throw new IllegalArgumentException("Day must be 1-10 and total flight capacity must be 4-16.");
        }
        if (flightNumber == null || flightNumber.trim().isEmpty()
                || departure == null || departure.trim().isEmpty()
                || destination == null || destination.trim().isEmpty()
                || departure.trim().equalsIgnoreCase(destination.trim())) {
            throw new IllegalArgumentException("Flight number and distinct departure/destination cities are required.");
        }
        this.day = day;
        this.flightNumber = flightNumber.trim();
        this.departure = departure.trim();
        this.destination = destination.trim();
        businessSeats = new boolean[businessCapacity];
        economySeats = new boolean[economyCapacity];
    }

    public int getDay() {
        return day;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    private boolean[] seats(boolean business) {
        return business ? businessSeats : economySeats;
    }

    public int getAvailableSeats(boolean business) {
        int available = 0;
        for (boolean occupied : seats(business)) {
            if (!occupied) {
                available++;
            }
        }
        return available;
    }

    /**
     * Returns assigned seat numbers, or null without changes if capacity is
     * insufficient.
     */
    public int[] reserveSeats(boolean business, int count) {
        if (count < 1 || count > getAvailableSeats(business)) {
            return null;
        }
        int[] assigned = new int[count];
        boolean[] cabin = seats(business);
        int next = 0;
        for (int i = 0; i < cabin.length && next < count; i++) {
            if (!cabin[i]) {
                cabin[i] = true;
                assigned[next++] = i + 1 + (business ? 0 : businessSeats.length);
            }
        }
        return assigned;
    }

    public void releaseSeats(boolean business, int[] assigned) {
        boolean[] cabin = seats(business);
        int offset = business ? 0 : businessSeats.length;
        if (assigned == null) {
            throw new IllegalArgumentException("Seat numbers are required.");
        }
        // Validate the whole request before releasing any seats.
        for (int number : assigned) {
            if (number <= offset || number > offset + cabin.length) {
                throw new IllegalArgumentException("Seat number is outside the selected cabin: " + number);
            }
        }
        for (int number : assigned) {
            cabin[number - offset - 1] = false;
        }
    }

    public String summary() {
        return "Day " + day + " | " + flightNumber + " | " + departure + " -> "
                + destination + " | Business: " + getAvailableSeats(true)
                + " | Economy: " + getAvailableSeats(false);
    }
}
