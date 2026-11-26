import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based Airline Reservation System entry point.
 * Manages file loading, round-trip reservations, cancellations,
 * availability reporting, and output file export.
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

    /** Prompts for the flight data file and drives the main 5-option menu loop. */
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
            System.out.println("1. Reserve seats for passengers");
            System.out.println("2. Cancel a reservation");
            System.out.println("3. Display the flights available");
            System.out.println("4. Display the seat availability report");
            System.out.println("5. Exit the program");
            String choice = readLine("Choose an option (1-5): ");
            if (choice == null) {
                break;
            }
            switch (choice) {
                case "1":
                    makeReservation();
                    break;
                case "2":
                    cancelReservation();
                    break;
                case "3":
                    displayFlightsForDay();
                    break;
                case "4":
                    displayAvailabilityReport();
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1 to 5.");
            }
        }

        if (flights.length > 0) {
            exportReport();
        }
        System.out.println("Thank you. Goodbye!");
    }

    /** Reads a trimmed line from standard input and handles EOF cleanly. */
    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) {
            endOfInput = true;
            System.out.println("\nInput ended.");
            return null;
        }
        return input.nextLine().trim();
    }

    /** Reads a non-empty text field, re-prompting if left blank. */
    private String readNonEmptyText(String prompt) {
        while (!endOfInput) {
            String value = readLine(prompt);
            if (value == null) {
                return null;
            }
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
        return null;
    }

    /** Reads any valid integer, catching NumberFormatException on malformed input. */
    private Integer readInt(String prompt) {
        while (!endOfInput) {
            String value = readLine(prompt);
            if (value == null) {
                return null;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please enter a whole number.");
            }
        }
        return null;
    }

    /** Reads an integer within [min, max], catching NumberFormatException and range errors. */
    private Integer readNumber(String prompt, int min, int max) {
        while (!endOfInput) {
            Integer number = readInt(prompt);
            if (number == null) {
                return null;
            }
            if (number >= min && number <= max) {
                return number;
            }
            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
        return null;
    }

    /** Reads the desired seating class (Business or Economy). */
    private Boolean readCabinChoice() {
        while (!endOfInput) {
            String value = readLine("Class of seating (1 = Business, 2 = Economy): ");
            if (value == null) {
                return null;
            }
            if (value.equals("1") || value.equalsIgnoreCase("business") || value.equalsIgnoreCase("b")) {
                return Boolean.TRUE;
            }
            if (value.equals("2") || value.equalsIgnoreCase("economy") || value.equalsIgnoreCase("e")) {
                return Boolean.FALSE;
            }
            System.out.println("Invalid cabin choice. Enter 1 (Business) or 2 (Economy).");
        }
        return null;
    }

    /** Prompts for a valid y/n confirmation from the passenger. */
    private boolean askYesNo(String prompt) {
        while (!endOfInput) {
            String answer = readLine(prompt);
            if (answer == null) {
                return false;
            }
            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                return true;
            }
            if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("Please enter y or n.");
        }
        return false;
    }

    /** Searches the flight array for a matching day and directed city pair. */
    private Flight findFlight(int day, String departure, String destination) {
        for (Flight flight : flights) {
            if (flight.getDay() == day
                    && flight.getDeparture().equalsIgnoreCase(departure)
                    && flight.getDestination().equalsIgnoreCase(destination)) {
                return flight;
            }
        }
        return null;
    }

    /**
     * Option 1: Collects passenger and round-trip travel details, validates days,
     * routes, and seat availability (including alternate cabin offer), and reserves both legs.
     */
    private void makeReservation() {
        String firstName = readNonEmptyText("Passenger first name: ");
        if (firstName == null) {
            return;
        }
        String lastName = readNonEmptyText("Passenger last name: ");
        if (lastName == null) {
            return;
        }
        String departureCity = readNonEmptyText("Departure city: ");
        if (departureCity == null) {
            return;
        }
        String destinationCity = readNonEmptyText("Destination city: ");
        if (destinationCity == null) {
            return;
        }
        Integer leaveDay = readInt("Leaving day (1-10): ");
        if (leaveDay == null) {
            return;
        }
        Integer returnDay = readInt("Returning day (1-10): ");
        if (returnDay == null) {
            return;
        }
        Boolean requestedBusiness = readCabinChoice();
        if (requestedBusiness == null) {
            return;
        }
        Integer count = readNumber("Number of seats (1-16): ", 1, 16);
        if (count == null) {
            return;
        }

        // Validate that both days fall within 1..10 and return is not before departure.
        if (leaveDay < 1 || leaveDay > 10 || returnDay < 1 || returnDay > 10 || returnDay < leaveDay) {
            System.out.println("One of the days is invalid");
            return;
        }

        Flight outbound = findFlight(leaveDay, departureCity, destinationCity);
        Flight inbound = findFlight(returnDay, destinationCity, departureCity);
        if (outbound == null || inbound == null) {
            System.out.println("No flight between those cities");
            return;
        }

        boolean outboundBusiness = requestedBusiness;
        boolean inboundBusiness = requestedBusiness;
        boolean outboundSwitched = false;
        boolean inboundSwitched = false;

        // Check outbound capacity; fall back to the other cabin only if enough seats exist.
        if (outbound.getAvailableSeats(requestedBusiness) < count) {
            if (outbound.getAvailableSeats(!requestedBusiness) < count) {
                System.out.println("At least one of the flights being requested doesn't have the number of seats the passenger requires.");
                return;
            }
            outboundBusiness = !requestedBusiness;
            outboundSwitched = true;
        }

        // Check return capacity; fall back to the other cabin only if enough seats exist.
        if (inbound.getAvailableSeats(requestedBusiness) < count) {
            if (inbound.getAvailableSeats(!requestedBusiness) < count) {
                System.out.println("At least one of the flights being requested doesn't have the number of seats the passenger requires.");
                return;
            }
            inboundBusiness = !requestedBusiness;
            inboundSwitched = true;
        }

        // Ask for passenger consent whenever either leg requires the alternate cabin.
        if (outboundSwitched || inboundSwitched) {
            String alternateName = requestedBusiness ? "Economy" : "Business";
            String requestedName = requestedBusiness ? "Business" : "Economy";
            if (outboundSwitched && inboundSwitched) {
                System.out.println("Both flights (" + outbound.getFlightNumber() + " and "
                        + inbound.getFlightNumber() + ") do not have enough seats in " + requestedName + ".");
            } else if (outboundSwitched) {
                System.out.println("Outbound flight " + outbound.getFlightNumber()
                        + " does not have enough seats in " + requestedName + ".");
            } else {
                System.out.println("Return flight " + inbound.getFlightNumber()
                        + " does not have enough seats in " + requestedName + ".");
            }
            if (!askYesNo("Is it acceptable to place the affected leg(s) in " + alternateName + "? (y/n): ")) {
                System.out.println("Reservation not created.");
                return;
            }
        }

        int[] outboundSeats = outbound.reserveSeats(outboundBusiness, count);
        int[] inboundSeats = inbound.reserveSeats(inboundBusiness, count);
        if (outboundSeats == null || inboundSeats == null) {
            if (outboundSeats != null) {
                outbound.releaseSeats(outboundBusiness, outboundSeats);
            }
            if (inboundSeats != null) {
                inbound.releaseSeats(inboundBusiness, inboundSeats);
            }
            System.out.println("At least one of the flights being requested doesn't have the number of seats the passenger requires.");
            return;
        }

        Booking booking = new Booking(nextBookingNumber++, firstName, lastName,
                outbound, outboundBusiness, outboundSeats,
                inbound, inboundBusiness, inboundSeats);
        bookings.add(booking);
        System.out.println("Reservation successful!");
        booking.displayBooking();
    }

    /** Option 2: Lists active reservations and cancels the chosen booking number. */
    private void cancelReservation() {
        boolean hasActive = false;
        for (Booking booking : bookings) {
            if (booking.isActive()) {
                if (!hasActive) {
                    System.out.println("\n--- Active Reservations ---");
                    hasActive = true;
                }
                System.out.println(booking.briefSummary());
            }
        }
        if (!hasActive) {
            System.out.println("No active reservations to cancel.");
            return;
        }

        Integer number = readNumber("Enter booking number to cancel (0 to return): ", 0, Integer.MAX_VALUE);
        if (number == null || number == 0) {
            return;
        }
        for (Booking booking : bookings) {
            if (booking.getBookingNumber() == number) {
                System.out.println(booking.cancel()
                        ? "Reservation cancelled. Seats released on both outbound and return flights."
                        : "Reservation was already cancelled.");
                return;
            }
        }
        System.out.println("Booking not found.");
    }

    /** Option 3: Displays all scheduled flights for a requested day (1-10). */
    private void displayFlightsForDay() {
        Integer day = readInt("Enter day of the month (1-10): ");
        if (day == null) {
            return;
        }
        if (day < 1 || day > 10) {
            System.out.println("One of the days is invalid");
            return;
        }
        boolean found = false;
        System.out.println("\n--- Flights on Day " + day + " ---");
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

    /** Option 4: Displays the seat availability report across all loaded flights and days. */
    private void displayAvailabilityReport() {
        System.out.println("\n--- Seat Availability Report ---");
        for (Flight flight : flights) {
            System.out.println(flight.summary());
        }
    }

    /** Option 5 helper: Writes flight details and remaining seats for each day to output/availability.txt. */
    private void exportReport() {
        Path output = Paths.get("output", "availability.txt");
        List<String> lines = new ArrayList<String>();
        lines.add("Seat availability report (remaining seats)");
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
     * Loads and validates all day blocks from the input file before returning the Flight array.
     * Format: header line "day flightCount", followed by flightCount lines of
     * "flightNumber departureCity destinationCity businessSeats economySeats".
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
                String flightNo = fields[0];
                String departure = fields[1].replace('_', ' ');
                String destination = fields[2].replace('_', ' ');
                for (Flight existing : loaded) {
                    if (existing.getDay() == day && existing.getFlightNumber().equalsIgnoreCase(flightNo)) {
                        throw new IllegalArgumentException("Duplicate flight on day " + day + ": " + flightNo);
                    }
                    if (existing.getDay() == day
                            && existing.getDeparture().equalsIgnoreCase(departure)
                            && existing.getDestination().equalsIgnoreCase(destination)) {
                        throw new IllegalArgumentException("Duplicate route on day " + day + ": "
                                + departure + " -> " + destination);
                    }
                }
                int business = parseRange(fields[3], 0, 16, "business capacity");
                int economy = parseRange(fields[4], 0, 16, "economy capacity");
                loaded.add(new Flight(day, flightNo, departure, destination, business, economy));
            }
        }
        if (loaded.isEmpty()) {
            throw new IllegalArgumentException("Flight file is empty.");
        }
        return loaded.toArray(new Flight[loaded.size()]);
    }

    /** Parses an integer within [min, max] from a flight data file token. */
    private static int parseRange(String value, int min, int max, String label) {
        try {
            int number = Integer.parseInt(value);
            if (number >= min && number <= max) {
                return number;
            }
        } catch (NumberFormatException ex) {
            // Converted into IllegalArgumentException below for unified file error reporting.
        }
        throw new IllegalArgumentException("Invalid " + label + ": " + value + " (" + min + "-" + max + ")");
    }
}
