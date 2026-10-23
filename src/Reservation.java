import java.util.ArrayList;
import java.util.Scanner;

public class Reservation {
    private Scanner input;
    private Flight[] flights;
    private ArrayList<Booking> bookings;

    public Reservation() {
        input = new Scanner(System.in);
        // Sample flights for the checkpoint demonstration.
        flights = new Flight[] {
            new Flight("NZ101", "Auckland", 3),
            new Flight("NZ202", "Wellington", 2),
            new Flight("NZ303", "Christchurch", 1)
        };
        bookings = new ArrayList<Booking>();
    }

    public static void main(String[] args) {
        Reservation system = new Reservation();
        system.run();
    }

    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Airline Reservation System ===");
            System.out.println("1. View flights");
            System.out.println("2. Book a seat");
            System.out.println("3. View bookings");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    displayFlights();
                    break;
                case "2":
                    bookSeat();
                    break;
                case "3":
                    displayBookings();
                    break;
                case "4":
                    running = false;
                    System.out.println("Thank you. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1 to 4.");
            }
        }
    }

    private void displayFlights() {
        System.out.println("\n--- Available flights ---");
        for (Flight flight : flights) {
            System.out.println(flight.getFlightNumber() + " | "
                    + flight.getDestination() + " | Seats available: "
                    + flight.getAvailableSeats());
        }
    }

    private void bookSeat() {
        displayFlights();
        System.out.print("Enter flight number (or 0 to return): ");
        String flightNumber = input.nextLine().trim();
        if (flightNumber.equals("0")) {
            return;
        }

        Flight selectedFlight = null;
        for (Flight flight : flights) {
            if (flight.getFlightNumber().equalsIgnoreCase(flightNumber)) {
                selectedFlight = flight;
                break;
            }
        }

        if (selectedFlight == null) {
            System.out.println("Flight not found. Please check the flight number.");
            return;
        }
        if (selectedFlight.getAvailableSeats() == 0) {
            System.out.println("Sorry, this flight is fully booked.");
            return;
        }

        System.out.print("Enter passenger name: ");
        String passengerName = input.nextLine().trim();
        if (passengerName.isEmpty()) {
            System.out.println("Passenger name cannot be empty. Booking not created.");
            return;
        }

        if (selectedFlight.reserveSeat()) {
            int bookingNumber = bookings.size() + 1;
            Booking booking = new Booking(bookingNumber, passengerName, selectedFlight);
            bookings.add(booking);
            System.out.println("Booking successful!");
            booking.displayBooking();
        }
    }

    private void displayBookings() {
        System.out.println("\n--- Bookings for this session ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        for (Booking booking : bookings) {
            booking.displayBooking();
        }
    }
}
