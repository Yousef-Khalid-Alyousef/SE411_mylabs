# Lab 05 - Exception Handling

Independent Maven project. Requires JDK 17 or newer and Maven.

```bash
cd lab05
mvn clean package exec:java
```

Open this folder's `pom.xml` as a Maven project in IntelliJ, or run the command from the repository root with `mvn -f lab05/pom.xml clean package exec:java`.

- `App.validateAge(int)` throws the checked `InvalidAgeException` below 18 and prints `Age valid message.` otherwise.
- Custom exceptions are in the dedicated `exceptions` package.
- `Wallet.withdraw(amount, bankAccount)` transfers money into a simulated `BankAccount`. An excessive withdrawal throws the checked `InsufficientFundsException` before either balance changes.
- `BigDecimal` preserves money values exactly. Amounts must be positive, have at most two decimal places, and specify a bank account; the initial wallet balance may be zero.
- The main program demonstrates valid and invalid ages, a successful transfer, insufficient funds, and withdrawal of the remaining balance.

Run the automated boundary and balance-preservation checks with `mvn test`.

The lab sheet also requests a GitHub Copilot exception-handling review. That external review has not been run; perform it with Copilot in your IDE. Automated tests cover the implemented exception paths.
