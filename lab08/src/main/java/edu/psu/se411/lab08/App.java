package edu.psu.se411.lab08;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Application is starting...");
        try {
            long delay = args.length == 0 ? 1000 : Long.parseLong(args[0]);
            if (delay < 0) {
                throw new IllegalArgumentException("Delay must be non-negative.");
            }
            TemperatureSensor temp = new TemperatureSensor("Temperature");
            HumiditySensor humidity = new HumiditySensor("Humidity");
            Observer dashboard = new DashboardObserver("Dashboard");
            Observer readingLogger = new LoggerObserver();
            for (Sensor sensor : new Sensor[] {temp, humidity}) {
                sensor.register(dashboard);
                sensor.register(readingLogger);
            }

            Random random = new Random();
            int iteration = 0;
            while (iteration < 10) {
                temp.setReading(20 + random.nextDouble() * 15);
                humidity.setReading(40 + random.nextDouble() * 20);
                iteration++;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Sensor simulation interrupted.", e);
                    return;
                }
            }

            System.out.println("\nUnregister the dashboard from humidity:");
            humidity.unregister(dashboard);
            humidity.setReading(65); // Only the logger receives this change.

            System.out.println("\nClone temperature; initially it has no observers:");
            TemperatureSensor clone = temp.clone();
            clone.setReading(10); // No output: original observers were not copied.
            clone.register(new DashboardObserver("Clone dashboard"));
            clone.setReading(11);
            System.out.println("Original temperature still has its own observers:");
            temp.setReading(12);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid simulation argument: " + e.getMessage());
            logger.error("Invalid simulation argument", e);
        } finally {
            logger.info("Application is stopping...");
        }
    }
}
