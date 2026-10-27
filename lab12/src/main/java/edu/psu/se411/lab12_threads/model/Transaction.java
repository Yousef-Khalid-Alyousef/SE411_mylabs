package edu.psu.se411.lab12_threads.model;

import edu.psu.se411.lab12_threads.exceptions.InsufficientBalanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transaction implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);
    private final String id;
    private final CryptoAccount sender;
    private final CryptoAccount receiver;
    private final Double amount;

    public Transaction(String id, CryptoAccount sender, CryptoAccount receiver, Double amount) {
        if (id == null || sender == null || receiver == null) {
            throw new IllegalArgumentException("Transaction id, sender and receiver are required");
        }
        if (sender == receiver) {
            throw new IllegalArgumentException("Sender and receiver must be different accounts");
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
        CryptoAccount first = sender.compareLockOrder(receiver) < 0 ? sender : receiver;
        CryptoAccount second = first == sender ? receiver : sender;
        first.getLock().lock();
        try {
            second.getLock().lock();
            try {
                sender.retrieveFromBalance(amount);
                receiver.addToBalance(amount);
            } finally {
                second.getLock().unlock();
            }
        } finally {
            first.getLock().unlock();
        }
    }

    public String getId() { return id; }
    public CryptoAccount getSender() { return sender; }
    public CryptoAccount getReceiver() { return receiver; }
    public Double getAmount() { return amount; }
}
