package edu.psu.se411.lab11_threads.model;

import edu.psu.se411.lab11_threads.exceptions.InsufficientBalanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    private final String id;
    private final CryptoAccount sender;
    private final CryptoAccount receiver;
    private final Double amount;

    public Transaction(CryptoAccount sender, CryptoAccount receiver, Double amount) {
        this("transaction", sender, receiver, amount);
    }

    public Transaction(String id, CryptoAccount sender, CryptoAccount receiver, Double amount) {
        if (id == null || sender == null || receiver == null) {
            throw new IllegalArgumentException("Transaction id, sender and receiver are required");
        }
        if (amount == null || !Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Amount must be finite and non-negative");
        }
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (InsufficientBalanceException e) {
            logger.warn("Transaction {} failed: {}", id, e.getMessage());
        }
    }

    public void execute() throws InsufficientBalanceException {
        transferSynchronized(sender, receiver, amount);
    }

    public void executeWithoutSynchronization() throws InsufficientBalanceException {
        transfer(sender, receiver, amount);
    }

    // Static gives all Transaction objects the same lock.
    private static synchronized void transferSynchronized(CryptoAccount sender,
            CryptoAccount receiver, Double amount) throws InsufficientBalanceException {
        transfer(sender, receiver, amount);
    }

    private static void transfer(CryptoAccount sender, CryptoAccount receiver, Double amount)
            throws InsufficientBalanceException {
        sender.retrieveFromBalance(amount);
        receiver.addToBalance(amount);
    }

    public CryptoAccount getSender() {
        return sender;
    }

    public CryptoAccount getReceiver() {
        return receiver;
    }

    public Double getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }
}
