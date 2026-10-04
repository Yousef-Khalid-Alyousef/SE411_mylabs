package edu.psu.se411.lab07;

import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import java.time.LocalDate;

public abstract class Booking {
    private final String bookingId;
    private final String customerFullName;
    private final LocalDate travelDate;
    private final String destinationCity;

    protected Booking(String bookingId, String customerFullName, LocalDate travelDate,
                      String destinationCity) throws InvalidArgumentException {
        this.bookingId = requireText(bookingId, "Booking ID");
        this.customerFullName = requireText(customerFullName, "Customer full name");
        this.destinationCity = requireText(destinationCity, "Destination city");
        if (travelDate == null) {
            throw new InvalidArgumentException("Travel date is required.");
        }
        this.travelDate = travelDate;
    }

    private static String requireText(String value, String name) throws InvalidArgumentException {
        if (value == null || value.isBlank()) {
            throw new InvalidArgumentException(name + " is required.");
        }
        return value.trim();
    }

    protected static double requireRange(double value, double minimum, double maximum,
                                         String name) throws InvalidArgumentException {
        if (!Double.isFinite(value) || value < minimum || value > maximum) {
            throw new InvalidArgumentException(name + " must be between "
                    + minimum + " and " + maximum + ".");
        }
        return value;
    }

    protected static double requirePrice(double value, String name)
            throws InvalidArgumentException {
        return requireRange(value, 0, Double.MAX_VALUE, name);
    }

    public String getBookingId() { return bookingId; }
    public String getCustomerFullName() { return customerFullName; }
    public LocalDate getTravelDate() { return travelDate; }
    public String getDestinationCity() { return destinationCity; }

    public abstract double computeTotalPrice()
            throws MissingInformationException, InvalidArgumentException;

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + bookingId + ", " + customerFullName
                + ", " + travelDate + ", " + destinationCity + "]";
    }
}
