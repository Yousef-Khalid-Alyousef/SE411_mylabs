package edu.psu.se411.lab12_threads;

import edu.psu.se411.lab12_threads.exceptions.InsufficientBalanceException;
import edu.psu.se411.lab12_threads.model.CryptoAccount;
import edu.psu.se411.lab12_threads.model.Transaction;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simulation {
    private static final Logger logger = LoggerFactory.getLogger(Simulation.class);
    public static final int FIXED_THREADS = Math.max(2, Math.min(8, Runtime.getRuntime().availableProcessors()));
    public static final int MAX_OUTSTANDING = 128;
    public static final double INITIAL_BALANCE = 10000.0;

    public static void runCases(boolean cached, String[] args) throws InterruptedException {
        logger.info("Application is starting: {} pool", cached ? "cached" : "fixed");
        try {
            System.out.printf("Fixed workers: %d; maximum outstanding tasks: %d%n", FIXED_THREADS, MAX_OUTSTANDING);
            int transactions = args.length == 0 ? 1000000 : Integer.parseInt(args[0]);
            for (int accountCount : new int[] {1000, 10}) {
                run(cached, accountCount, transactions);
            }
        } catch (InterruptedException e) {
            logger.warn("Simulation interrupted", e);
            Thread.currentThread().interrupt();
            throw e;
        } catch (RuntimeException e) {
            logger.error("Simulation failed", e);
            throw e;
        } finally {
            logger.info("Application is closing...");
        }
    }

    public static double run(boolean cached, int accountCount, int transactions)
            throws InterruptedException {
        if (accountCount < 2 || transactions < 0) {
            throw new IllegalArgumentException("At least two accounts and a non-negative transaction count are required");
        }
        CryptoAccount[] accounts = new CryptoAccount[accountCount];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new CryptoAccount("acc_" + i, INITIAL_BALANCE);
        }
        double initialTotal = sumTotalMoneyInAccounts(accounts);
        ExecutorService executor = cached ? Executors.newCachedThreadPool()
                : Executors.newFixedThreadPool(FIXED_THREADS);
        // Apply the same submission limit to both pools; cached pools are otherwise unbounded.
        Semaphore outstanding = new Semaphore(MAX_OUTSTANDING);
        AtomicInteger rejected = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        Random random = new Random(411);
        long start = System.nanoTime();
        try {
            for (int i = 0; i < transactions; i++) {
                int sender = random.nextInt(accountCount);
                int receiver = random.nextInt(accountCount - 1);
                if (receiver >= sender) {
                    receiver++;
                }
                double amount = random.nextInt(10000) / 100.0;
                Transaction transaction = new Transaction("tx_" + i,
                        accounts[sender], accounts[receiver], amount);
                outstanding.acquire();
                try {
                    executor.execute(() -> {
                        try {
                            transaction.execute();
                        } catch (InsufficientBalanceException e) {
                            rejected.incrementAndGet();
                        } catch (RuntimeException e) {
                            errors.incrementAndGet();
                            logger.error("Transaction failed unexpectedly", e);
                        } finally {
                            outstanding.release();
                        }
                    });
                } catch (RuntimeException e) {
                    outstanding.release();
                    throw e;
                }
            }
        } finally {
            executor.shutdown();
            try {
                while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    // Wait for every submitted transaction before checking the total.
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                throw e;
            }
        }
        double milliseconds = (System.nanoTime() - start) / 1000000.0;
        double finalTotal = sumTotalMoneyInAccounts(accounts);
        if (errors.get() != 0 || Math.abs(initialTotal - finalTotal) > 0.00001) {
            throw new IllegalStateException("Transactions failed or the total balance changed");
        }
        if (rejected.get() > 0) {
            logger.warn("{} transactions rejected due to insufficient balance", rejected.get());
        }
        String pool = cached ? "Cached" : "Fixed";
        System.out.printf("%s pool | accounts: %d | transactions: %d | time: %.2f ms | initial: %.2f | final: %.2f | rejected: %d%n",
                pool, accountCount, transactions, milliseconds, initialTotal, finalTotal, rejected.get());
        logger.info("{} pool: {} accounts, {} transactions, {} ms, final total {}",
                pool, accountCount, transactions, milliseconds, finalTotal);
        return finalTotal;
    }

    public static double sumTotalMoneyInAccounts(CryptoAccount[] accounts) {
        double total = 0;
        for (CryptoAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
}
