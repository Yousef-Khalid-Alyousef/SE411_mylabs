# Lab 07 - Polymorphism

Independent Maven project. Requires JDK 17 or newer and Maven.

```bash
cd lab07
mvn clean package exec:java
```

Open this folder's `pom.xml` as a Maven project in IntelliJ. Run `mvn test` for the booking rules and boundary tests.

`Booking` holds the common ID, customer name, travel date, and destination. Its subclasses override `computeTotalPrice()`. `App.computeTotalPrice(Booking)` uses dynamic dispatch without checking the booking type.

| Booking | Price | Valid later-entered data |
| --- | --- | --- |
| Flight | Base ticket price + weight x luggage rate | 0-40 kg, inclusive |
| Standard train | Distance x standard rate | 1-2000 km, inclusive |
| First-class train | Distance x first-class rate | 1-2000 km, inclusive |
| Car rental | Daily rate x rental days | 1-30 days, inclusive |

Later-entered values are initially missing. Price calculation throws `MissingInformationException` until supplied. Invalid setters and invalid construction data throw `InvalidArgumentException`; rejected setters preserve prior valid values. Rates and bounds are centralized in `BookingConfig`. Example rates are 10 per kg, 0.5 per standard-train km, and 1 per first-class-train km because the worksheet does not specify numeric rates.

The main program demonstrates missing data, all booking types, and invalid inputs. Its configured examples produce 700.00, 200.00, 400.00, and 450.00. SLF4J and the worksheet's log4j12 binding record startup, shutdown, and handled exceptions in `lab07.log` in the working directory. Maven resolves the binding's published relocation to reload4j. Generated logs and build output are ignored by Git.
