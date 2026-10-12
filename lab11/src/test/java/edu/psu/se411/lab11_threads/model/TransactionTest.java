package edu.psu.se411.lab11_threads.model;

import edu.psu.se411.lab11_threads.App;
import edu.psu.se411.lab11_threads.exceptions.InsufficientBalanceException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    @Test
    void transfersMoneyBetweenAccounts() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 100.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 20.0);
        new Transaction(sender, receiver, 35.0).execute();
        assertEquals(65.0, sender.getBalance().doubleValue());
        assertEquals(55.0, receiver.getBalance().doubleValue());
    }

    @Test
    void failedTransferLeavesBothBalancesUnchanged() {
        CryptoAccount sender = new CryptoAccount("sender", 10.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 20.0);
        Transaction transaction = new Transaction("overdraft", sender, receiver, 11.0);
        assertThrows(InsufficientBalanceException.class, transaction::execute);
        assertDoesNotThrow(transaction::run);
        assertEquals(10.0, sender.getBalance().doubleValue());
        assertEquals(20.0, receiver.getBalance().doubleValue());
    }

    @Test
    void acceptsZeroAndTransfersToTheSameAccount() throws Exception {
        CryptoAccount account = new CryptoAccount("account", 10.0);
        new Transaction("self", account, account, 10.0).execute();
        new Transaction("zero", account, account, 0.0).execute();
        assertEquals(10.0, account.getBalance().doubleValue());
    }

    @Test
    void rejectsInvalidAmountsAndInitialBalances() {
        CryptoAccount sender = new CryptoAccount("sender", 10.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 20.0);
        for (Double amount : new Double[] {null, -1.0, Double.NaN,
                Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new CryptoAccount("invalid", amount));
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction("invalid", sender, receiver, amount));
            assertThrows(IllegalArgumentException.class, () -> sender.addToBalance(amount));
            assertThrows(IllegalArgumentException.class, () -> sender.retrieveFromBalance(amount));
        }
        assertThrows(IllegalArgumentException.class, () -> new CryptoAccount(null, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(null, sender, receiver, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("invalid", null, receiver, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction("invalid", sender, null, 1.0));
        assertEquals(10.0, sender.getBalance().doubleValue());
    }

    @Test
    void concurrentTransfersCannotOverdrawASharedSender() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 100.0);
        CryptoAccount[] receivers = new CryptoAccount[8];
        for (int i = 0; i < receivers.length; i++) {
            receivers[i] = new CryptoAccount("receiver_" + i, 0.0);
        }
        AtomicInteger successfulTransfers = new AtomicInteger();
        runConcurrently(8, worker -> {
            for (int i = 0; i < 100; i++) {
                try {
                    new Transaction("transfer", sender, receivers[worker], 1.0).execute();
                    successfulTransfers.incrementAndGet();
                } catch (InsufficientBalanceException expected) {
                    // Once the shared sender is empty, later transfers must fail.
                }
            }
        });
        assertEquals(100, successfulTransfers.get());
        assertEquals(0.0, sender.getBalance().doubleValue());
        assertEquals(100.0, App.computeTotalMoneyInSystem(receivers));
    }

    @Test
    void oppositeDirectionTransfersFinishAndPreserveBalances() throws Exception {
        // Accounts with the same id still share the transaction lock.
        CryptoAccount first = new CryptoAccount("same_id", 10000.0);
        CryptoAccount second = new CryptoAccount("same_id", 10000.0);
        runConcurrently(8, worker -> {
            CryptoAccount sender = worker % 2 == 0 ? first : second;
            CryptoAccount receiver = worker % 2 == 0 ? second : first;
            for (int i = 0; i < 2000; i++) {
                new Transaction("transfer", sender, receiver, 1.0).execute();
            }
        });
        assertEquals(10000.0, first.getBalance().doubleValue());
        assertEquals(10000.0, second.getBalance().doubleValue());
        assertEquals(20000.0, App.computeTotalMoneyInSystem(new CryptoAccount[] {first, second}));
    }

    @Test
    void unsynchronizedTransferWorksWithOneThread() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 100.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 20.0);
        new Transaction(sender, receiver, 35.0).executeWithoutSynchronization();
        assertEquals(65.0, sender.getBalance().doubleValue());
        assertEquals(55.0, receiver.getBalance().doubleValue());
    }

    @Test
    void concurrentFractionalTransfersPreserveTheTotal() throws Exception {
        CryptoAccount[] accounts = new CryptoAccount[10];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new CryptoAccount("acc_" + i, 1000.0);
        }
        runConcurrently(8, worker -> {
            for (int i = 0; i < 1250; i++) {
                new Transaction(accounts[worker], accounts[(worker + 1) % accounts.length],
                        0.1 + worker * 0.001).execute();
            }
        });
        assertEquals(10000.0, App.computeTotalMoneyInSystem(accounts), 0.000001);
        for (CryptoAccount account : accounts) {
            assertTrue(account.getBalance() >= 0);
        }
    }

    private static void runConcurrently(int workers, ConcurrentTask task) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(workers, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        CountDownLatch ready = new CountDownLatch(workers);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < workers; i++) {
                int worker = i;
                futures.add(pool.submit(() -> {
                    ready.countDown();
                    start.await();
                    task.run(worker);
                    return null;
                }));
            }
            assertTrue(ready.await(5, TimeUnit.SECONDS), "Workers did not start");
            start.countDown();
            for (Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            start.countDown();
            pool.shutdownNow();
        }
    }

    @FunctionalInterface
    private interface ConcurrentTask {
        void run(int worker) throws Exception;
    }
}
