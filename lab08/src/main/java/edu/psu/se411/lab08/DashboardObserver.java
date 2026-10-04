package edu.psu.se411.lab08;

public class DashboardObserver implements Observer {
    private final String name;

    public DashboardObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(Subject subject) {
        if (subject instanceof Sensor sensor) {
            System.out.printf("[%s] %s: %.2f %s%n", name, sensor.getName(),
                    sensor.getReading(), sensor.getUnit());
        }
    }
}
