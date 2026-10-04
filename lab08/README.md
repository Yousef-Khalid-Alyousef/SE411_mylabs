# Lab 08 - Observer Pattern

Independent Maven project. Requires JDK 17 or newer and Maven.

```bash
cd lab08
mvn clean package exec:java
```

Open this folder's `pom.xml` as a Maven project in IntelliJ. Run `mvn test` for notification, registration, unregistration, and cloning tests. For a fast demonstration without the default one-second pauses:

```bash
mvn clean package exec:java -Dexec.args="0"
```

- `Subject` and `Observer` use the interfaces required by the lab sheet.
- The abstract `Sensor` shares registration, unregistration, change notification, reading storage, and cloning between `TemperatureSensor` and `HumiditySensor`.
- `DashboardObserver` and `LoggerObserver` print updates using `System.out.printf`. The logger also records readings in `lab08.log` in the working directory using SLF4J and the log4j12 binding (resolved by Maven to reload4j).
- The main method registers both observers with both sensors and runs a ten-iteration `while` loop with the worksheet's simulated reading ranges.
- Reassigning an unchanged reading causes no notification. Registering an observer twice does not duplicate updates. A snapshot allows observers to unregister during notification.
- A cloned sensor copies the reading and sensor metadata but starts with its own empty observer list. Changing a clone does not notify the original's observers. The main program demonstrates this and registers a separate dashboard on the clone.
- Humidity must be within 0-100 percent; all readings must be finite. Interrupted sleep restores the interrupt flag and stops the simulation.

Generated logs and build output are ignored by Git.
