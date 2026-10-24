import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Checkpoint 2 console entry point. Paths are relative to the working
 * directory.
 */
public class Reservation {
    private final Scanner input = new Scanner(System.in);
    private Flight[] flights = new Flight[0];
    private final ArrayList<Booking> bookings = new ArrayList<Booking>();
    private int nextBookingNumber = 1;
    private boolean endOfInput;

    public static void main(String[] args) {
        Reservation system = new Reservation();
        if (args.length > 0) {
            try {
                system.flights = loadFlights(Paths.get(args[0]));
                System.out.println("Loaded " + system.flights.length + " flights from " + args[0]);
            } catch (IOException | IllegalArgumentException | SecurityException ex) {
                System.out.println("Cannot load flights: " + ex.getMessage());
                return;
            }
        }
        system.run();
    }

    /** BlueJ: construct Reservation and invoke run(), or invoke main with {}. */
    public void run() {
        while (flights.length == 0 && !endOfInput) {
            String filename = readLine("Flight file [data/flights.txt], or 0 to exit: ");
            if (filename == null || filename.equals("0")) {
                return;
            }
            if (filename.isEmpty()) {
                filename = "data/flights.txt";
            }
            try {
                flights = loadFlights(Paths.get(filename));
                System.out.println("Loaded " + flights.length + " flights from " + filename);
            } catch (IOException | IllegalArgumentException | SecurityException ex) {
                System.out.println("Cannot load flights: " + ex.getMessage());
            }
        }
        boolean running = flights.length > 0;
        while (running && !endOfInput) {
            System.out.println("\n=== Airline Reservation System ===");
            System.out.println("1. Book seats\n2. Cancel booking\n3. View flights for a day"
                    + "\n4. Availability report (all days)\n5. View bookings\n6. Export report\n7. Exit");
            String choice = readLine("Choose an option: ");
            if (choice == null) {
                break;
            }
            switch (choice) {
                case "1":
                    bookSeats();
                    break;
                case "2":
                    cancelBooking();
                    break;
                case "3":
                    displayFlights();
                    break;
                case "4":
                    for (Flight flight : flights) {
                        System.out.println(flight.summary());
                    }
                    break;
                case "5":
                    displayBookings();
                    break;
                case "6":
                    exportReport();
                    break;
                case "7":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1 to 7.");
            }
        }
        if (flights.length > 0) {
            exportReport();
        }
        System.out.println("Thank you. Goodbye!");
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) {
            endOfInput = true;
            System.out.println("\nInput ended.");
            return null;
        }
        return input.nextLine().trim();
    }

    private Integer readNumber(String prompt, int min, int max) {
        while (!endOfInput) {
            String value = readLine(prompt);
            if (value == null) {
                return null;
            }
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException ex) {
                // Retry with a range message, including for integer overflow.
            }
            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
        return null;
    }

    private void displayFlights() {
        Integer day = readNumber("Day (1-10): ", 1, 10);
        if (day == null) {
            return;
        }
        boolean found = false;
        for (Flight flight : flights) {
            if (flight.getDay() == day) {
                System.out.println(flight.summary());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No flights scheduled for that day.");
        }
    }

    private void bookSeats() {
        Integer day = readNumber("Travel day (1-10): ", 1, 10);
        if (day == null) {
            return;
        }
        String number = readLine("Flight number (or 0 to return): ");
        if (number == null || number.equals("0")) {
            return;
        }
        Flight selected = null;
        for (Flight flight : flights) {
            if (flight.getDay() == day && flight.getFlightNumber().equalsIgnoreCase(number)) {
                selected = flight;
                break;
            }
        }
        if (selected == null) {
            System.out.println("Flight not found for that day.");
            return;
        }
        String name = readLine("Passenger/contact name: ");
        if (name == null) {
            return;
        }
        if (name.isEmpty()) {
            System.out.println("Passenger name cannot be empty. Booking not created.");
            return;
        }
        Integer cabin = readNumber("Cabin (1 = Business, 2 = Economy): ", 1, 2);
        if (cabin == null) {
            return;
        }
        Integer count = readNumber("Number of seats (1-16): ", 1, 16);
        if (count == null) {
            return;
        }
        boolean business = cabin == 1;
        if (selected.getAvailableSeats(business) < count) {
            System.out.println("Not enough seats in the requested cabin.");
            if (selected.getAvailableSeats(!business) < count) {
                System.out.println("Not enough seats in either cabin. Booking not created.");
                return;
            }
            String agree;
            do {
                agree = readLine("Use " + (business ? "Economy" : "Business") + " instead? (y/n): ");
                if (agree == null || agree.equalsIgnoreCase("y") || agree.equalsIgnoreCase("n")) {
                    break;
                }
                System.out.println("Please enter y or n.");
            } while (true);
            if (agree == null || !agree.equalsIgnoreCase("y")) {
                System.out.println("Booking not created.");
                return;
            }
            business = !business;
        }
        int[] seats = selected.reserveSeats(business, count);
        Booking booking = new Booking(nextBookingNumber++, name, selected, business, seats);
        bookings.add(booking);
        System.out.println("Booking successful!");
        booking.displayBooking();
    }

    private void displayBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
        }
        for (Booking booking : bookings) {
            booking.displayBooking();
        }
    }

    private void cancelBooking() {
        Integer number = readNumber("Booking number to cancel (0 to return): ", 0, Integer.MAX_VALUE);
        if (number == null || number == 0) {
            return;
        }
        for (Booking booking : bookings) {
            if (booking.getBookingNumber() == number) {
                System.out.println(booking.cancel() ? "Booking cancelled. Seats released."
                        : "Booking was already cancelled.");
                return;
            }
        }
        System.out.println("Booking not found.");
    }

    private void exportReport() {
        Path output = Paths.get("output", "availability.txt");
        List<String> lines = new ArrayList<String>();
        lines.add("Seat availability report (remaining seats; not a reloadable booking file)");
        for (Flight flight : flights) {
            lines.add(flight.summary());
        }
        try {
            Files.createDirectories(output.getParent());
            Files.write(output, lines, StandardCharsets.UTF_8);
            System.out.println("Report saved to " + output);
        } catch (IOException | SecurityException ex) {
            System.out.println("Report could not be saved: " + ex.getMessage());
        }
    }

    /**
     * File blocks: day flightCount, followed by that many rows of
     * flightNumber departure destination businessCapacity economyCapacity.
     * Blank lines and full-line # comments are ignored; city names use underscores.
     * Validation finishes before the flight array is returned (no partial load).
     */
    public static Flight[] loadFlights(Path path) throws IOException {
        List<String> records = new ArrayList<String>();
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                records.add(trimmed);
            }
        }
        ArrayList<Flight> loaded = new ArrayList<Flight>();
        boolean[] days = new boolean[10];
        int index = 0;
        while (index < records.size()) {
            String[] header = records.get(index++).split("\\s+");
            if (header.length != 2) {
                throw new IllegalArgumentException("Expected header: day flightCount.");
            }
            int day = parseRange(header[0], 1, 10, "day");
            int count = parseRange(header[1], 1, 100, "flight count");
            if (days[day - 1]) {
                throw new IllegalArgumentException("Duplicate day block: " + day);
            }
            days[day - 1] = true;
            for (int row = 0; row < count; row++) {
                if (index >= records.size()) {
                    throw new IllegalArgumentException("Missing flight rows for day " + day);
                }
                String[] fields = records.get(index++).split("\\s+");
                if (fields.length != 5) {
                    throw new IllegalArgumentException("Each flight needs 5 fields.");
                }
                for (Flight existing : loaded) {
                    if (existing.getDay() == day && existing.getFlightNumber().equalsIgnoreCase(fields[0])) {
                        throw new IllegalArgumentException("Duplicate flight on day " + day + ": " + fields[0]);
                    }
                    if (existing.getDay() == day
                            && existing.getDeparture().equalsIgnoreCase(fields[1].replace('_', ' '))
                            && existing.getDestination().equalsIgnoreCase(fields[2].replace('_', ' '))) {
                        throw new IllegalArgumentException("Duplicate route on day " + day + ": "
                                + fields[1] + " -> " + fields[2]);
                    }
                }
                int business = parseRange(fields[3], 0, 16, "business capacity");
                int economy = parseRange(fields[4], 0, 16, "economy capacity");
                loaded.add(new Flight(day, fields[0], fields[1].replace('_', ' '),
                        fields[2].replace('_', ' '), business, economy));
            }
        }
        if (loaded.isEmpty()) {
            throw new IllegalArgumentException("Flight file is empty.");
        }
        return loaded.toArray(new Flight[loaded.size()]);
    }

    private static int parseRange(String value, int min, int max, String label) {
        try {
            int number = Integer.parseInt(value);
            if (number >= min && number <= max) {
                return number;
            }
        } catch (NumberFormatException ex) {
            // Convert malformed file values into a useful loading error.
        }
        throw new IllegalArgumentException("Invalid " + label + ": " + value + " (" + min + "-" + max + ")");
    }
}
