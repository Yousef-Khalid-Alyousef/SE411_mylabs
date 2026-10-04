package edu.psu.se411.lab08;

public class TemperatureSensor extends Sensor {
    public TemperatureSensor(String name) {
        super(name, "C");
    }

    @Override
    public TemperatureSensor clone() {
        return (TemperatureSensor) super.clone();
    }
}
