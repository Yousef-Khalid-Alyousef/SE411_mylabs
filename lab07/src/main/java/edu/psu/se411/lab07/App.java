package edu.psu.se411.lab07;

import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static double computeTotalPrice(Booking booking)
            throws MissingInformationException, InvalidArgumentException {
        return booking.computeTotalPrice(); // Dynamic dispatch selects the subclass implementation.
    }

    public static void main(String[] args) {
        logger.info("Application is starting...");
        try {
            LocalDate date = LocalDate.of(2026, 12, 13);
            FlightBooking flight = new FlightBooking("F001", "Yousef Alyousef", date, "Jeddah", 500);
            TrainBooking standard = new TrainBooking("T001", "Sara Ahmed", date, "Dammam", SeatClass.STANDARD);
            TrainBooking firstClass = new TrainBooking("T002", "Ali Khalid", date, "Dammam", SeatClass.FIRST_CLASS);
            CarRentalBooking car = new CarRentalBooking("C001", "Omar Saleh", date, "Riyadh", 150);

            // Missing data is handled by the caller and recorded in the file logger.
            printPrice(flight);
            printPrice(standard);
            printPrice(car);
            flight.setLuggageWeight(20);
            standard.setDistance(400);
            firstClass.setDistance(400);
            car.setRentalDays(3);

            Booking[] bookings = {flight, standard, firstClass, car};
            for (Booking booking : bookings) {
                printPrice(booking);
            }

            try {
                flight.setLuggageWeight(41);
            } catch (InvalidArgumentException e) {
                reportInvalid(e);
            }
            try {
                standard.setDistance(2001);
            } catch (InvalidArgumentException e) {
                reportInvalid(e);
            }
            try {
                car.setRentalDays(31);
            } catch (InvalidArgumentException e) {
                reportInvalid(e);
            }
        } catch (InvalidArgumentException e) {
            reportInvalid(e);
        } finally {
            logger.info("Application is stopping...");
        }
    }

    private static void printPrice(Booking booking) {
        try {
            System.out.printf("%s -> total price: %.2f%n", booking, computeTotalPrice(booking));
        } catch (MissingInformationException | InvalidArgumentException e) {
            System.out.println(booking.getBookingId() + ": " + e.getMessage());
            logger.warn("Could not price booking " + booking.getBookingId(), e);
        }
    }

    private static void reportInvalid(InvalidArgumentException e) {
        System.out.println("Invalid booking information: " + e.getMessage());
        logger.warn("Invalid booking information", e);
    }
}
