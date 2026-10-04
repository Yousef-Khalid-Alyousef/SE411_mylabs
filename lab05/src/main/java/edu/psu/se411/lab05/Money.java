package edu.psu.se411.lab05;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class Money {
    private Money() { }

    static BigDecimal validate(BigDecimal amount, boolean allowZero) {
        if (amount == null || amount.signum() < 0
                || (!allowZero && amount.signum() == 0)) {
            throw new IllegalArgumentException("Amount must be "
                    + (allowZero ? "non-negative." : "positive."));
        }
        try {
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Amount must have at most two decimal places.", e);
        }
    }
}
