package edu.psu.se411.lab07;

/** Example agency rates: the lab does not specify numerical prices. */
public final class BookingConfig {
    public static final double EXTRA_LUGGAGE_RATE = 10.0;
    public static final double TRAIN_STANDARD_RATE = 0.5;
    public static final double TRAIN_FIRST_CLASS_RATE = 1.0;
    public static final double MIN_LUGGAGE_WEIGHT = 0.0;
    public static final double MAX_LUGGAGE_WEIGHT = 40.0;
    public static final double MIN_TRAIN_DISTANCE = 1.0;
    public static final double MAX_TRAIN_DISTANCE = 2000.0;
    public static final int MIN_RENTAL_DAYS = 1;
    public static final int MAX_RENTAL_DAYS = 30;

    private BookingConfig() { }
}
