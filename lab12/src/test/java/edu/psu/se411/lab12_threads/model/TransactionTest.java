package edu.psu.se411.lab12_threads.model;

import edu.psu.se411.lab12_threads.Simulation;
import edu.psu.se411.lab12_threads.exceptions.InsufficientBalanceException;
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
    void transfersBetweenAccounts() throws Exception {
        CryptoAccount sender = new CryptoAccount("a", 100.0);
        CryptoAccount receiver = new CryptoAccount("b", 20.0);
        new Transaction("tx", sender, receiver, 35.0).execute();
        assertEquals(65.0, sender.getBalance());
        assertEquals(55.0, receiver.getBalance());
    }

    @Test
    void insufficientBalanceDoesNotChangeEitherAccountOrKeepLocks() {
        CryptoAccount sender = new CryptoAccount("a", 10.0);
        CryptoAccount receiver = new CryptoAccount("b", 20.0);
        Transaction transaction = new Transaction("tx", sender, receiver, 11.0);
        assertThrows(InsufficientBalanceException.class, transaction::execute);
        assertEquals(10.0, sender.getBalance());
        assertEquals(20.0, receiver.getBalance());
        assertFalse(sender.getLock().isLocked());
        assertFalse(receiver.getLock().isLocked());
    }

    @Test
    void rejectsInvalidInputs() {
        CryptoAccount a = new CryptoAccount("a", 100.0);
        CryptoAccount b = new CryptoAccount("b", 100.0);
        for (Double value : new Double[] {null, -1.0, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new CryptoAccount("invalid", value));
            assertThrows(IllegalArgumentException.class, () -> new Transaction("tx", a, b, value));
            assertThrows(IllegalArgumentException.class, () -> a.addToBalance(value));
            assertThrows(IllegalArgumentException.class, () -> a.retrieveFromBalance(value));
        }
        assertThrows(IllegalArgumentException.class, () -> new Transaction("tx", a, a, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Transaction("tx", null, b, 1.0));
    }

    @Test
    void oppositeDirectionTransfersFinishWithoutDeadlock() throws Exception {
        checkOppositeTransfers("a", "b");
    }

    @Test
    void duplicateAccountIdsStillHaveAnOrder() throws Exception {
        checkOppositeTransfers("same", "same");
    }

    private void checkOppositeTransfers(String firstId, String secondId) throws Exception {
        CryptoAccount first = new CryptoAccount(firstId, 10000.0);
        CryptoAccount second = new CryptoAccount(secondId, 10000.0);
        assertEquals(-Integer.signum(first.compareLockOrder(second)),
                Integer.signum(second.compareLockOrder(first)));
        runConcurrently(8, worker -> {
            CryptoAccount from = worker % 2 == 0 ? first : second;
            CryptoAccount to = worker % 2 == 0 ? second : first;
            for (int i = 0; i < 2000; i++) {
                new Transaction("tx", from, to, 1.0).execute();
            }
        });
        assertEquals(10000.0, first.getBalance());
        assertEquals(10000.0, second.getBalance());
    }

    @Test
    void threeAccountCycleCompletes() throws Exception {
        CryptoAccount[] accounts = {
            new CryptoAccount("c", 10000.0),
            new CryptoAccount("a", 10000.0),
            new CryptoAccount("b", 10000.0)
        };
        runConcurrently(3, worker -> {
            for (int i = 0; i < 2000; i++) {
                new Transaction("cycle", accounts[worker], accounts[(worker + 1) % 3], 1.0).execute();
            }
        });
        for (CryptoAccount account : accounts) {
            assertEquals(10000.0, account.getBalance());
        }
    }

    @Test
    void concurrentWithdrawalsCannotDoubleSpend() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 1000.0);
        CryptoAccount[] receivers = new CryptoAccount[8];
        for (int i = 0; i < receivers.length; i++) {
            receivers[i] = new CryptoAccount("receiver_" + i, 0.0);
        }
        AtomicInteger successes = new AtomicInteger();
        runConcurrently(8, worker -> {
            for (int i = 0; i < 200; i++) {
                try {
                    new Transaction("tx", sender, receivers[worker], 1.0).execute();
                    successes.incrementAndGet();
                } catch (InsufficientBalanceException expected) {
                    // Transfers must stop when the sender is empty.
                }
            }
        });
        assertEquals(1000, successes.get());
        assertEquals(0.0, sender.getBalance());
        assertEquals(1000.0, Simulation.sumTotalMoneyInAccounts(receivers));
    }

    @Test
    void fixedAndCachedPoolsBothPreserveTotalMoney() throws Exception {
        assertEquals(100000.0, Simulation.run(false, 10, 1000), 0.00001);
        assertEquals(100000.0, Simulation.run(true, 10, 1000), 0.00001);
    }

    private static void runConcurrently(int workers, ConcurrentTask task) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(workers, runnable -> {
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
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    task.run(worker);
                    return null;
                }));
            }
            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            for (Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    @FunctionalInterface
    private interface ConcurrentTask {
        void run(int worker) throws Exception;
    }
}
