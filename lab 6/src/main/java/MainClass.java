import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClass	{

	static Logger logger = LoggerFactory.getLogger(MainClass.class);
	
	public static void main (String[] args) {
		logger.info("Application is starting...");
		
		try {
            WalletAccount account = new WalletAccount(500.0);
            account.deposit(200.0);
            account.withdraw(1000.0);
        } catch (InsufficientFundsException e) {
            logger.error("thrown exception: {}", e.getMessage(), e);
        }

        logger.info("Application ends");
		
	}
}
