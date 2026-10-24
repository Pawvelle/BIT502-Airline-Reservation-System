# BIT502 Airline Reservation System — Checkpoint 2

A Java console application extending the Checkpoint 1 project with flight file input,
seat arrays, day selection, cabin selection, group bookings, cancellation and report output.
The entry class remains `Reservation`. No external Java libraries are required.

## Checkpoint evidence

| Focus | Implementation |
| --- | --- |
| Arrays | `Flight[] flights`; each flight has `boolean[] businessSeats` and `boolean[] economySeats`; each booking holds `int[] seatNumbers` |
| File input | Loads and validates a user-selected UTF-8 text file before showing the menu |
| File output | Writes remaining seats for every scheduled flight to `output/availability.txt`, on option 6 and on exit |
| Interaction | Repeating menu, day/flight/cabin/party selection, alternate cabin offered with explicit `y` agreement |
| Refinement | Cancellation releases numbered seats; cancelled bookings remain visible; booking numbers are never reused within a run |
| Validation | Rejects malformed files, duplicate day/flight entries, invalid menu/numeric input, blank names and insufficient capacity; handles end-of-input |

Compared with Checkpoint 1, flights are now loaded from a file instead of hard-coded,
availability is tracked separately for each day and cabin, and reservations can include
multiple seats. Missing files and invalid inputs produce messages without a stack trace.

## Compile and run

Use JDK 8 or newer. From the project root:

```sh
mkdir -p bin
javac -d bin src/Reservation.java src/Flight.java src/Booking.java
java -cp bin Reservation
```

Press Enter at the file prompt to load `data/flights.txt`, or enter another file path.
Enter `0` to exit before loading. A command-line path can also be supplied:

```sh
java -cp bin Reservation data/flights.txt
```

In VS Code, open this folder and run `main` in `src/Reservation.java` using **Run Java**.
Keep the working directory set to the project root so the default relative paths work.
For BlueJ, create a project, add all three Java files, and invoke
`Reservation.main` with `{}`. Copy the `data` folder into that project's working
directory, or enter an absolute input file path.

| Menu | Action |
| --- | --- |
| 1 | Book numbered seats for a passenger/contact and party |
| 2 | Cancel by booking number and release seats |
| 3 | Display flights for a selected day (1–10) |
| 4 | Display availability for all scheduled flights |
| 5 | Display current-session bookings, including cancelled records |
| 6 | Export availability without exiting |
| 7 | Export availability and exit |

Business seats are numbered from 1 to the business capacity; economy seat numbers
continue from the next number. Each booking belongs to one day,
one flight and one cabin. A party must fit entirely in one cabin. When it does not
fit in the requested cabin, the program offers the other cabin if enough seats are
available. Enter `y` to accept or `n` to decline (case-insensitive); other responses
are rejected and the confirmation is requested again.
Flight numbers are case-insensitive. Input is trimmed.

## Flight input format

The included file contains **invented demonstration data**, not the original course
attachment. It has six scheduled flights across days 1, 2 and 10, and serves as the
project's own input file. Additional test files can use the format below.

```text
# day flightCount
1 2
NZ101 Auckland Wellington 2 3
NZ202 Wellington Christchurch 1 3
2 1
NZ101 Auckland Wellington 2 3
```

Each header contains a day (1–10) and number of following flights (1–100).
Each flight row has exactly five whitespace-separated fields:

```text
flightNumber departureCity destinationCity businessCapacity economyCapacity
```

Use underscores in multiword city names, e.g. `New_Plymouth`; they display as spaces.
Blank lines and full-line `#` comments are ignored. Cabin capacities must be nonnegative
whole numbers, with 4–16 seats in total per flight. Departure and destination cities
must differ. A day can occur in only one block; flight numbers and directed routes
must be unique within that day. A file must contain at least one flight. Invalid files are
rejected as a whole, and interactive startup lets the user try another path.

The earlier README described an attachment named `flightinfo.txt.txt` with flight
rows but no day header. That attachment is absent from this repository. For a file
containing eight such rows, insert a header such as `1 8` before those rows. Confirm
the actual attachment and assessment specification before adapting other days.

## Current limits and acknowledgements

Bookings and occupied-seat arrays last for the current run only. On restart, the
input capacities are loaded again. The exported report is a human-readable snapshot;
it is not a reloadable booking database, and each export overwrites the previous
report. A failed export is reported to the user. Outbound and return trips can be
booked separately; linked return reservations, fares and named passengers for every
seat are outside this checkpoint implementation.

Implementation uses Java standard-library APIs only. No third-party code or assets
were copied. AI assistance (OpenAI Codex) was used for the Checkpoint 2 implementation,
documentation and test preparation. Review and understand the code, and follow the
course's AI-use policy and required declaration before submitting.
