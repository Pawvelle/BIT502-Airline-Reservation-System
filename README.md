# BIT502 Airline Reservation System

A Java console application for the BIT502 Programming Fundamentals assignment. This version implements the structure, menu, flight display, and basic reservations for **Checkpoint 1**. The complete assignment features are still in progress.

## Checkpoint 1 status

The code covers the functional scope listed for Checkpoint 1. Command-line compilation and eight functional checks passed on 24 October 2026.

| Requirement | Current evidence |
| --- | --- |
| Clear class structure | Three classes with separate responsibilities |
| Working console menu | Repeating menu with four options and normal exit |
| Initial flight viewing logic | Displays three sample flights and remaining seats |
| Basic reservations | Books one seat, creates a booking, and reduces availability |
| Methods to organise code | Separate methods for the menu, flight display, booking, and booking display |

The entry class is `Reservation` in `Reservation.java`, matching the naming requirement in the assessment brief.

## Project structure and design

```text
src/
  Reservation.java               Entry point, menu, and application logic
  Flight.java                    Flight details and available-seat count
  Booking.java                   Booking number, passenger, and flight
bin/                             Compiled output used by VS Code
lib/                             No external libraries currently required
.vscode/settings.json            VS Code Java settings
README.md                        Project documentation
```

`Flight[]` stores the sample flights. An `ArrayList<Booking>` stores bookings for the current session. Each booking refers to a `Flight` object. Only `Reservation` contains a `main` method.

## Current features

| Menu option | Behaviour |
| --- | --- |
| `1. View flights` | Shows flight number, destination, and available seats |
| `2. Book a seat` | Accepts a flight number and a non-empty passenger name, then reserves one seat |
| `3. View bookings` | Shows bookings created during the current run |
| `4. Exit` | Ends the program |

Flight numbers are case-insensitive, and surrounding input spaces are trimmed. Entering `0` at the flight-number prompt returns to the menu. The program rejects invalid menu options, unknown flights, blank passenger names, and bookings on a full flight.

| Sample flight | Destination | Initial available seats |
| --- | --- | ---: |
| NZ101 | Auckland | 3 |
| NZ202 | Wellington | 2 |
| NZ303 | Christchurch | 1 |

Flights are hard-coded. Bookings and seat changes are held in memory and reset when the program restarts. Each booking reserves one seat; dates, departure cities, seating classes, and seat numbers are not implemented yet.

## Compile and run

Install a Java Development Kit (JDK). No external libraries are needed. From the project root:

```sh
mkdir -p bin
javac -d bin src/Reservation.java src/Flight.java src/Booking.java
java -cp bin Reservation
```

In VS Code, open the project folder, open `src/Reservation.java`, and select **Run** above `main`. Enter responses in the terminal.

## Functional checks

All three source files compiled together. The following eight scenarios passed during command-line testing on 24 October 2026, with compiled test output stored in a temporary directory.

Start a fresh run for each row. In the input sequences, `→` means press Enter before the next value.

| Scenario | Input sequence | Expected and observed result |
| --- | --- | --- |
| View flights and exit | `1 → 4` | Shows all three flights and exits normally |
| Valid booking | `2 → nz101 → Alex Smith → 3 → 1 → 4` | Creates booking 1, lists the passenger, and shows NZ101 with 2 seats remaining |
| Invalid menu input | `abc → 9 → 4` | Rejects both choices and keeps the menu available |
| Unknown flight | `2 → BAD → 4` | Reports that the flight was not found |
| Blank passenger name | `2 → NZ101 → [spaces] → 1 → 3 → 4` | Rejects the name, keeps 3 seats on NZ101, and creates no booking |
| Full flight | `2 → NZ303 → Alex Smith → 2 → NZ303 → 1 → 4` | Books the only seat, rejects another booking, and shows 0 seats remaining |
| Return without booking | `2 → 0 → 3 → 4` | Returns to the menu and reports no bookings |
| Multiple bookings | `2 → NZ101 → Alex → 2 → NZ202 → Sam → 3 → 4` | Creates booking numbers 1 and 2 and lists both passengers |

Known robustness limitation: if the input stream ends unexpectedly, `Scanner.nextLine()` throws `NoSuchElementException`. Normal exit through option `4` works. End-of-input handling remains a later improvement.

## Data attachment

The supplied `flightinfo.txt.txt` attachment is not currently included in this project or read by the program. It contains eight flight rows with flight number, departure city, destination city, business-seat count, and economy-seat count. It has no day header.

The brief specifies a header with a day and flight count before each day's rows, for example `1 8`. Prepare data following that format before implementing file input. The current hard-coded sample flights are independent of this attachment.

## Later development

The following features remain for development toward Checkpoint 2 and the final system:

- Read flights from a user-specified file and manage flights for days 1–10.
- Store departure cities, business/economy capacity, and numbered seat assignments.
- Make return reservations using passenger details, route, dates, seating class, and seat count.
- Offer the other seating class when the requested class is full, with passenger agreement.
- Cancel reservations and release seats.
- Display flights for a selected day and a seat availability report for each flight on each day.
- Write flight details and remaining seat counts to an output file on exit.
- Improve validation, exception handling, code comments, and document refinements made after feedback.

