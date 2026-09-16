import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalletAccount {
    private static final Logger logger = LoggerFactory.getLogger(WalletAccount.class);
    private double balance;

    public WalletAccount(double initialBalance) {
        this.balance = initialBalance;
        logger.debug("Wallet account created with initial balance: {}", initialBalance);
    }

    public void deposit(double amount) {
        this.balance += amount;
        logger.debug("Deposit: added {}, new balance: {}", amount, this.balance);
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds. Your balance is " + balance);
        }
        this.balance -= amount;
        logger.debug("Withdraw: removed {}, new balance: {}", amount, this.balance);
    }
}
