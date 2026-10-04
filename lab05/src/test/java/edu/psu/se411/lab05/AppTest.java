package edu.psu.se411.lab05;

import edu.psu.se411.lab05.exceptions.InvalidAgeException;
import edu.psu.se411.lab05.exceptions.InsufficientFundsException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 17})
    void rejectsUnderage(int age) {
        assertThrows(InvalidAgeException.class, () -> App.validateAge(age));
    }

    @ParameterizedTest
    @ValueSource(ints = {18, 22})
    void acceptsAdult(int age) {
        assertDoesNotThrow(() -> App.validateAge(age));
    }

    @Test
    void transfersMoneyWithoutLosingCents() throws Exception {
        Wallet wallet = new Wallet(new BigDecimal("100.10"));
        BankAccount bank = new BankAccount();
        wallet.withdraw(new BigDecimal("0.10"), bank);
        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
        assertEquals(new BigDecimal("0.10"), bank.getBalance());
        wallet.withdraw(new BigDecimal("100.00"), bank);
        assertEquals(new BigDecimal("0.00"), wallet.getBalance());
        assertEquals(new BigDecimal("100.10"), bank.getBalance());
    }

    @Test
    void insufficientFundsDoesNotChangeEitherBalance() {
        Wallet wallet = new Wallet(new BigDecimal("50.00"));
        BankAccount bank = new BankAccount();
        assertThrows(InsufficientFundsException.class,
                () -> wallet.withdraw(new BigDecimal("50.01"), bank));
        assertEquals(new BigDecimal("50.00"), wallet.getBalance());
        assertEquals(new BigDecimal("0.00"), bank.getBalance());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "0.001"})
    void rejectsInvalidWithdrawalWithoutChangingBalances(String amount) {
        Wallet wallet = new Wallet(new BigDecimal("50.00"));
        BankAccount bank = new BankAccount();
        assertThrows(IllegalArgumentException.class,
                () -> wallet.withdraw(new BigDecimal(amount), bank));
        assertEquals(new BigDecimal("50.00"), wallet.getBalance());
        assertEquals(new BigDecimal("0.00"), bank.getBalance());
    }

    @Test
    void rejectsMissingAccountAndInvalidInitialBalance() {
        Wallet wallet = new Wallet(new BigDecimal("50"));
        assertThrows(NullPointerException.class,
                () -> wallet.withdraw(new BigDecimal("10"), null));
        assertEquals(new BigDecimal("50.00"), wallet.getBalance());
        assertThrows(IllegalArgumentException.class, () -> new Wallet(null));
        assertThrows(IllegalArgumentException.class, () -> new Wallet(new BigDecimal("-1")));
        assertDoesNotThrow(() -> new Wallet(BigDecimal.ZERO));
    }
}
