/**
 * Represents one scheduled flight on a specific day of the month.
 * Separate boolean arrays track individual seat occupancy for Business and Economy cabins.
 */
public class Flight {
    private final int day;
    private final String flightNumber;
    private final String departure;
    private final String destination;
    private final boolean[] businessSeats;
    private final boolean[] economySeats;

    /**
     * Constructs a Flight and validates day, capacity limits, and route cities.
     *
     * @param day              day of the month (1-10)
     * @param flightNumber     unique flight code for the day
     * @param departure        departure city name
     * @param destination      destination city name
     * @param businessCapacity number of seats in Business class
     * @param economyCapacity  number of seats in Economy class
     */
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
        // False indicates an available seat; true indicates an occupied seat.
        this.businessSeats = new boolean[businessCapacity];
        this.economySeats = new boolean[economyCapacity];
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

    /** Returns the seat array corresponding to the requested cabin. */
    private boolean[] seats(boolean business) {
        return business ? businessSeats : economySeats;
    }

    /** Counts how many seats remain unoccupied in the selected cabin. */
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
     * Reserves the requested number of seats in the selected cabin.
     * Business seats are numbered 1..b and Economy seats are numbered (b+1)..(b+e).
     *
     * @param business true for Business class, false for Economy class
     * @param count    number of seats to reserve
     * @return array of assigned seat numbers, or null if insufficient seats remain
     */
    public int[] reserveSeats(boolean business, int count) {
        if (count < 1 || count > getAvailableSeats(business)) {
            return null;
        }
        int[] assigned = new int[count];
        boolean[] cabin = seats(business);
        int offset = business ? 0 : businessSeats.length;
        int next = 0;
        for (int i = 0; i < cabin.length && next < count; i++) {
            if (!cabin[i]) {
                cabin[i] = true;
                assigned[next++] = i + 1 + offset;
            }
        }
        return assigned;
    }

    /**
     * Releases previously assigned seat numbers back to available status.
     * Validates all seat numbers before mutating any state.
     *
     * @param business true for Business class, false for Economy class
     * @param assigned seat numbers to release
     */
    public void releaseSeats(boolean business, int[] assigned) {
        boolean[] cabin = seats(business);
        int offset = business ? 0 : businessSeats.length;
        if (assigned == null) {
            throw new IllegalArgumentException("Seat numbers are required.");
        }
        for (int number : assigned) {
            if (number <= offset || number > offset + cabin.length) {
                throw new IllegalArgumentException("Seat number is outside the selected cabin: " + number);
            }
        }
        for (int number : assigned) {
            cabin[number - offset - 1] = false;
        }
    }

    /** Formats a one-line summary of the flight and remaining seats per cabin. */
    public String summary() {
        return "Day " + day + " | " + flightNumber + " | " + departure + " -> "
                + destination + " | Business: " + getAvailableSeats(true)
                + " | Economy: " + getAvailableSeats(false);
    }
}
