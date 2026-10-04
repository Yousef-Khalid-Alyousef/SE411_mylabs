package edu.psu.se411.lab07;

import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import java.time.LocalDate;

public class TrainBooking extends Booking {
    private final SeatClass seatClass;
    private Double distance;

    public TrainBooking(String id, String customer, LocalDate date, String destination,
                        SeatClass seatClass) throws InvalidArgumentException {
        super(id, customer, date, destination);
        if (seatClass == null) {
            throw new InvalidArgumentException("Seat class is required.");
        }
        this.seatClass = seatClass;
    }

    public void setDistance(double kilometers) throws InvalidArgumentException {
        distance = requireRange(kilometers, BookingConfig.MIN_TRAIN_DISTANCE,
                BookingConfig.MAX_TRAIN_DISTANCE, "Train distance");
    }

    @Override
    public double computeTotalPrice() throws MissingInformationException {
        if (distance == null) {
            throw new MissingInformationException("Train distance has not been provided.");
        }
        double rate = seatClass == SeatClass.STANDARD
                ? BookingConfig.TRAIN_STANDARD_RATE : BookingConfig.TRAIN_FIRST_CLASS_RATE;
        return distance * rate;
    }
}
