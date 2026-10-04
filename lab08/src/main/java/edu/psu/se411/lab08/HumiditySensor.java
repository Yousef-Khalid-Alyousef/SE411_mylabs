package edu.psu.se411.lab08;

public class HumiditySensor extends Sensor {
    public HumiditySensor(String name) {
        super(name, "%");
    }

    @Override
    protected void validateReading(double value) {
        super.validateReading(value);
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Humidity must be between 0 and 100 percent.");
        }
    }

    @Override
    public HumiditySensor clone() {
        return (HumiditySensor) super.clone();
    }
}
