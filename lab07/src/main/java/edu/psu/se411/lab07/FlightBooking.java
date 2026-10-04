package edu.psu.se411.lab07;

import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import java.time.LocalDate;

public class FlightBooking extends Booking {
    private final double baseTicketPrice;
    private Double luggageWeight;

    public FlightBooking(String id, String customer, LocalDate date, String destination,
                         double baseTicketPrice) throws InvalidArgumentException {
        super(id, customer, date, destination);
        this.baseTicketPrice = requirePrice(baseTicketPrice, "Base ticket price");
    }

    public void setLuggageWeight(double weight) throws InvalidArgumentException {
        luggageWeight = requireRange(weight, BookingConfig.MIN_LUGGAGE_WEIGHT,
                BookingConfig.MAX_LUGGAGE_WEIGHT, "Luggage weight");
    }

    @Override
    public double computeTotalPrice() throws MissingInformationException, InvalidArgumentException {
        if (luggageWeight == null) {
            throw new MissingInformationException("Luggage weight has not been provided.");
        }
        return requirePrice(baseTicketPrice + luggageWeight * BookingConfig.EXTRA_LUGGAGE_RATE,
                "Total flight price");
    }
}
