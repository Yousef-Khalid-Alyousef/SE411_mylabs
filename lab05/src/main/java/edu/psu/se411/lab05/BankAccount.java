package edu.psu.se411.lab05;

import java.math.BigDecimal;

/** A local bank-account simulation; no external bank is contacted. */
public class BankAccount {
    private BigDecimal balance = new BigDecimal("0.00");

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        amount = Money.validate(amount, false);
        balance = balance.add(amount);
    }
}
