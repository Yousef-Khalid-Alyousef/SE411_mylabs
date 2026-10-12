package edu.psu.se411.lab11_threads;

import edu.psu.se411.lab11_threads.exceptions.InsufficientBalanceException;
import edu.psu.se411.lab11_threads.model.CryptoAccount;
import edu.psu.se411.lab11_threads.model.Transaction;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static final int nbAccounts = 10;
    public static final double initialBalance = 1000.0;
    public static final int nbTransactions = 10000;

    public static void main(String[] args) {
        logger.info("Application is starting...");
        try {
            runTransactions(false);
            runTransactions(true);
        } catch (RuntimeException e) {
            logger.error("Application failed", e);
            throw e;
        } finally {
            logger.info("Application is closing...");
        }
    }

    private static void runTransactions(boolean synchronizedTransfers) {
        CryptoAccount[] accounts = new CryptoAccount[nbAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new CryptoAccount("acc_" + i, initialBalance);
        }

        String mode = synchronizedTransfers ? "With synchronization" : "Without synchronization";
        double initialTotal = computeTotalMoneyInSystem(accounts);
        logger.info("{}: initial total = {}", mode, initialTotal);

        Thread[] threads = new Thread[nbTransactions];
        boolean interrupted = false;
        // Run 10,000 threads in groups of 100 to limit active native threads.
        for (int first = 0; first < threads.length; first += 100) {
            int end = Math.min(first + 100, threads.length);
            CountDownLatch start = new CountDownLatch(1);
            for (int i = first; i < end; i++) {
                int transactionNumber = i + 1;
                threads[i] = new Thread(() -> {
                    try {
                        start.await();
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        CryptoAccount sender = accounts[random.nextInt(accounts.length)];
                        CryptoAccount receiver = accounts[random.nextInt(accounts.length)];
                        double amount = random.nextDouble(1, 11);
                        Transaction transaction = new Transaction("tx_" + transactionNumber,
                                sender, receiver, amount);
                        if (synchronizedTransfers) {
                            transaction.execute();
                        } else {
                            transaction.executeWithoutSynchronization();
                        }
                    } catch (InsufficientBalanceException e) {
                        logger.warn("Transaction {} failed: {}", transactionNumber, e.getMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.warn("Transaction {} interrupted", transactionNumber, e);
                    }
                }, "tx_" + transactionNumber);
                threads[i].start();
            }
            start.countDown();
            for (int i = first; i < end; i++) {
                boolean joined = false;
                while (!joined) {
                    try {
                        threads[i].join();
                        joined = true;
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
            logger.warn("Main thread interrupted while waiting for transactions");
        }

        double finalTotal = computeTotalMoneyInSystem(accounts);
        System.out.printf("%s: initial = %.2f, final = %.2f riyals%n",
                mode, initialTotal, finalTotal);
        logger.info("{}: final total after {} threads = {}", mode, nbTransactions, finalTotal);
        if (synchronizedTransfers && Math.abs(finalTotal - initialTotal) > 0.000001) {
            throw new IllegalStateException("The synchronized total changed");
        }
    }

    public static double computeTotalMoneyInSystem(CryptoAccount[] accounts) {
        double total = 0;
        for (CryptoAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
}
