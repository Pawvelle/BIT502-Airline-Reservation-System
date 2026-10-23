public class Booking {
    private int bookingNumber;
    private String passengerName;
    private Flight flight;

    public Booking(int bookingNumber, String passengerName, Flight flight) {
        this.bookingNumber = bookingNumber;
        this.passengerName = passengerName;
        this.flight = flight;
    }

    public void displayBooking() {
        System.out.println("Booking number: " + bookingNumber);
        System.out.println("Passenger: " + passengerName);
        System.out.println("Flight: " + flight.getFlightNumber());
        System.out.println("Destination: " + flight.getDestination());
        System.out.println("------------------------------");
    }
}
