import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsufficientFundsException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(InsufficientFundsException.class);

    public InsufficientFundsException(String message) {
        super(message);
        logger.warn("InsufficientFundsException object created: {}", message);
    }
}
