package edu.psu.se411.lab07;

import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import java.time.LocalDate;

public class CarRentalBooking extends Booking {
    private final double dailyRentalRate;
    private Integer rentalDays;

    public CarRentalBooking(String id, String customer, LocalDate date, String destination,
                            double dailyRentalRate) throws InvalidArgumentException {
        super(id, customer, date, destination);
        this.dailyRentalRate = requirePrice(dailyRentalRate, "Daily rental rate");
    }

    public void setRentalDays(int days) throws InvalidArgumentException {
        requireRange(days, BookingConfig.MIN_RENTAL_DAYS, BookingConfig.MAX_RENTAL_DAYS,
                "Rental days");
        rentalDays = days;
    }

    @Override
    public double computeTotalPrice() throws MissingInformationException, InvalidArgumentException {
        if (rentalDays == null) {
            throw new MissingInformationException("Rental days have not been provided.");
        }
        return requirePrice(dailyRentalRate * rentalDays, "Total rental price");
    }
}
