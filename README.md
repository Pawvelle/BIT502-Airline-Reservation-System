# BIT502 Airline Reservation System (Final Submission)

A Java console application for managing flights, return-trip seat bookings, cancellations, and seat availability reports for Days 1-10 of a month.

## Files

- [src/Reservation.java](src/Reservation.java): Main class with the console menu, file loading/saving, and input validation.
- [src/Flight.java](src/Flight.java): Flight class using `boolean[] businessSeats` and `boolean[] economySeats` to track seat numbers (`1..b` for Business, `b+1..b+e` for Economy).
- [src/Booking.java](src/Booking.java): Booking class storing passenger name, leaving and returning flights, assigned seat arrays, and `cancel()`.
- [data/flights.txt](data/flights.txt): Input flight schedule (50 flights across Days 1-10).
- [output/availability.txt](output/availability.txt): Output seat availability report written on exit.
- [TestPlan.txt](TestPlan.txt): Test cases and results.
- [Reflection.txt](Reflection.txt): Short written reflection (~200 words).

## Changes Across Checkpoints

1. **Checkpoint 1**: Three classes (`Reservation`, `Flight`, `Booking`), basic menu, and hard-coded single-seat flights.
2. **Checkpoint 2**: File input (`loadFlights`), seat arrays (`boolean[]` and `int[]`), alternate cabin prompt (`y/n`), booking cancellation, and file export.
3. **Final Submission**:
   - Updated Option 1 to book return trips by departure city, destination city, leaving day, and returning day.
   - Matched the 5 menu options from the assignment brief (`1. Reserve seats for passengers`, `2. Cancel a reservation`, `3. Display the flights available`, `4. Display the seat availability report`, `5. Exit the program`).
   - Added checks and messages for invalid days, missing routes, and not enough seats across both flights.

## Compile and Run (VS Code / Terminal)

```sh
mkdir -p bin
javac -d bin src/Reservation.java src/Flight.java src/Booking.java
java -cp bin Reservation
```

Press **Enter** at the file prompt to load `data/flights.txt`, or type another file path (`0` to exit).
In VS Code, open `src/Reservation.java` and click **Run** with the working directory set to the project root.
