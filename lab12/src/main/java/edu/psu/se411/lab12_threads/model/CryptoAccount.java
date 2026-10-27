package edu.psu.se411.lab12_threads.model;

import edu.psu.se411.lab12_threads.exceptions.InsufficientBalanceException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class CryptoAccount {
    private static final AtomicLong nextOrder = new AtomicLong();
    private final long order = nextOrder.getAndIncrement();
    private final String id;
    private final ReentrantLock lock = new ReentrantLock();
    private double balance;

    public CryptoAccount(String id, Double balance) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Account id is required");
        }
        validateAmount(balance);
        this.id = id;
        this.balance = balance;
    }

    public void addToBalance(Double amount) {
        validateAmount(amount);
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public void retrieveFromBalance(Double amount) throws InsufficientBalanceException {
        validateAmount(amount);
        lock.lock();
        try {
            if (amount > balance) {
                throw new InsufficientBalanceException(id);
            }
            balance -= amount;
        } finally {
            lock.unlock();
        }
    }

    private static void validateAmount(Double amount) {
        if (amount == null || !Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Amount must be finite and non-negative");
        }
    }

    public int compareLockOrder(CryptoAccount other) {
        int byId = id.compareTo(other.id);
        // Different accounts with the same id still need a consistent order.
        return byId != 0 ? byId : Long.compare(order, other.order);
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public String getId() {
        return id;
    }

    public Double getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
