package edu.psu.se411.lab05;

import edu.psu.se411.lab05.exceptions.InvalidAgeException;
import edu.psu.se411.lab05.exceptions.InsufficientFundsException;
import java.math.BigDecimal;

public class App {
    public static void validateAge(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("Age must be at least 18. Provided age: " + age);
        }
        System.out.println("Age valid message.");
    }

    public static void main(String[] args) {
        System.out.println("Exercise 1: custom age exception");
        for (int age : new int[] {16, 18, 22}) {
            try {
                validateAge(age);
            } catch (InvalidAgeException e) {
                System.out.println("Invalid age: " + e.getMessage());
            }
        }

        System.out.println("\nExercise 2: online wallet");
        Wallet wallet = new Wallet(new BigDecimal("500.00"));
        BankAccount bank = new BankAccount();
        for (String amount : new String[] {"150.00", "400.00", "350.00"}) {
            try {
                wallet.withdraw(new BigDecimal(amount), bank);
                System.out.println("Transferred " + amount + " to the bank account.");
            } catch (InsufficientFundsException e) {
                System.out.println("Withdrawal failed: " + e.getMessage());
            }
            System.out.printf("Wallet: %s | Bank: %s%n", wallet.getBalance(), bank.getBalance());
        }
    }
}
