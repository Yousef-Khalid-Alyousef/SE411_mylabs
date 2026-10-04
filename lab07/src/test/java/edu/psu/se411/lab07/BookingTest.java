package edu.psu.se411.lab07;

import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    private final LocalDate date = LocalDate.of(2026, 12, 13);

    private FlightBooking flight() throws InvalidArgumentException {
        return new FlightBooking("F1", "Yousef Alyousef", date, "Jeddah", 500);
    }

    private TrainBooking train(SeatClass seat) throws InvalidArgumentException {
        return new TrainBooking("T1", "Yousef Alyousef", date, "Dammam", seat);
    }

    private CarRentalBooking car() throws InvalidArgumentException {
        return new CarRentalBooking("C1", "Yousef Alyousef", date, "Riyadh", 150);
    }

    @Test
    void missingInformationIsDifferentFromZero() throws Exception {
        FlightBooking flight = flight();
        assertThrows(MissingInformationException.class, flight::computeTotalPrice);
        assertThrows(MissingInformationException.class, train(SeatClass.STANDARD)::computeTotalPrice);
        assertThrows(MissingInformationException.class, car()::computeTotalPrice);
        flight.setLuggageWeight(0);
        assertEquals(500, flight.computeTotalPrice(), 0.000001);
    }

    @Test
    void computesPricesPolymorphicallyForEveryBookingType() throws Exception {
        FlightBooking flight = flight();
        TrainBooking standard = train(SeatClass.STANDARD);
        TrainBooking first = train(SeatClass.FIRST_CLASS);
        CarRentalBooking car = car();
        flight.setLuggageWeight(20);
        standard.setDistance(400);
        first.setDistance(400);
        car.setRentalDays(3);
        Booking[] bookings = {flight, standard, first, car};
        double[] expected = {700, 200, 400, 450};
        for (int i = 0; i < bookings.length; i++) {
            assertEquals(expected[i], App.computeTotalPrice(bookings[i]), 0.000001);
        }
    }

    @Test
    void acceptsInclusiveBoundaries() throws Exception {
        FlightBooking flight = flight();
        flight.setLuggageWeight(40);
        assertEquals(900, flight.computeTotalPrice(), 0.000001);
        TrainBooking train = train(SeatClass.STANDARD);
        train.setDistance(1);
        assertEquals(0.5, train.computeTotalPrice(), 0.000001);
        train.setDistance(2000);
        assertEquals(1000, train.computeTotalPrice(), 0.000001);
        CarRentalBooking car = car();
        car.setRentalDays(1);
        assertEquals(150, car.computeTotalPrice(), 0.000001);
        car.setRentalDays(30);
        assertEquals(4500, car.computeTotalPrice(), 0.000001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 40.1, Double.NaN, Double.POSITIVE_INFINITY})
    void rejectsInvalidLuggageAndKeepsPreviousValue(double value) throws Exception {
        FlightBooking flight = flight();
        flight.setLuggageWeight(20);
        assertThrows(InvalidArgumentException.class, () -> flight.setLuggageWeight(value));
        assertEquals(700, flight.computeTotalPrice(), 0.000001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 2000.1, Double.NaN, Double.NEGATIVE_INFINITY})
    void rejectsInvalidDistance(double value) throws Exception {
        TrainBooking train = train(SeatClass.STANDARD);
        assertThrows(InvalidArgumentException.class, () -> train.setDistance(value));
        assertThrows(MissingInformationException.class, train::computeTotalPrice);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 31})
    void rejectsInvalidRentalDays(int value) throws Exception {
        CarRentalBooking car = car();
        assertThrows(InvalidArgumentException.class, () -> car.setRentalDays(value));
        assertThrows(MissingInformationException.class, car::computeTotalPrice);
    }

    @Test
    void rejectsInvalidConstructionData() {
        assertThrows(InvalidArgumentException.class,
                () -> new FlightBooking(" ", "Yousef", date, "Jeddah", 500));
        assertThrows(InvalidArgumentException.class,
                () -> new FlightBooking("F1", null, date, "Jeddah", 500));
        assertThrows(InvalidArgumentException.class,
                () -> new FlightBooking("F1", "Yousef", null, "Jeddah", 500));
        assertThrows(InvalidArgumentException.class,
                () -> new FlightBooking("F1", "Yousef", date, "", 500));
        assertThrows(InvalidArgumentException.class,
                () -> new FlightBooking("F1", "Yousef", date, "Jeddah", -1));
        assertThrows(InvalidArgumentException.class, () -> train(null));
        assertThrows(InvalidArgumentException.class,
                () -> new CarRentalBooking("C1", "Yousef", date, "Riyadh", Double.NaN));
    }
}
