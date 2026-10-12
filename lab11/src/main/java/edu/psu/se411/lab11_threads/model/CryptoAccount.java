package edu.psu.se411.lab11_threads.model;

import edu.psu.se411.lab11_threads.exceptions.InsufficientBalanceException;

public class CryptoAccount {
    private final String id;
    private Double balance;

    public CryptoAccount(String id, Double balance) {
        if (id == null) {
            throw new IllegalArgumentException("Account id is required");
        }
        validateAmount(balance);
        this.id = id;
        this.balance = balance;
    }

    public void addToBalance(Double amount) {
        validateAmount(amount);
        double newBalance = balance + amount;
        // Let other threads run between reading and writing to show the race.
        Thread.yield();
        balance = newBalance;
    }

    public void retrieveFromBalance(Double amount) throws InsufficientBalanceException {
        validateAmount(amount);
        double currentBalance = balance;
        if (amount > currentBalance) {
            throw new InsufficientBalanceException("Insufficient balance to withdraw from account " + id);
        }
        Thread.yield();
        balance = currentBalance - amount;
    }

    private static void validateAmount(Double amount) {
        if (amount == null || !Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Amount must be finite and non-negative");
        }
    }

    public String getId() {
        return id;
    }

    public Double getBalance() {
        return balance;
    }
}
