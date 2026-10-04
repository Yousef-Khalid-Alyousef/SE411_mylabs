package edu.psu.se411.lab05;

import edu.psu.se411.lab05.exceptions.InsufficientFundsException;
import java.math.BigDecimal;
import java.util.Objects;

public class Wallet {
    private BigDecimal balance;

    public Wallet(BigDecimal initialBalance) {
        balance = Money.validate(initialBalance, true);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void withdraw(BigDecimal amount, BankAccount bankAccount)
            throws InsufficientFundsException {
        Objects.requireNonNull(bankAccount, "Bank account is required.");
        amount = Money.validate(amount, false);
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException(
                    "Cannot withdraw " + amount + "; wallet balance is " + balance + ".");
        }
        bankAccount.deposit(amount);
        balance = balance.subtract(amount);
    }
}
