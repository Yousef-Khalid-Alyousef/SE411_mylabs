package edu.psu.se411.lab08;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Shared subject implementation for all sensor types. */
public abstract class Sensor implements Subject, Cloneable {
    private final String name;
    private final String unit;
    private double reading;
    private List<Observer> observers = new ArrayList<>();

    protected Sensor(String name, String unit) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Sensor name is required.");
        }
        this.name = name;
        this.unit = unit;
    }

    public String getName() { return name; }
    public String getUnit() { return unit; }
    public double getReading() { return reading; }

    public void setReading(double value) {
        validateReading(value);
        if (Double.compare(reading, value) != 0) {
            reading = value;
            notifyObservers();
        }
    }

    protected void validateReading(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Sensor reading must be finite.");
        }
    }

    @Override
    public void register(Observer observer) {
        Objects.requireNonNull(observer, "Observer is required.");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        // Snapshot permits an observer to unregister itself during notification.
        for (Observer observer : new ArrayList<>(observers)) {
            observer.update(this);
        }
    }

    @Override
    public Sensor clone() {
        try {
            Sensor copy = (Sensor) super.clone();
            copy.observers = new ArrayList<>();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Sensor implements Cloneable.", e);
        }
    }
}
