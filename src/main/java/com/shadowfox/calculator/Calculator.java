package com.shadowfox.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Scanner;

/**
 * Task 1: Enhanced Console-Based Calculator
 *
 * Supports:
 *  - Basic arithmetic (+, -, *, /) using BigDecimal for precision-safe math
 *  - Scientific operations (square root, exponentiation)
 *  - Unit conversions (temperature, currency)
 *
 * Engineering notes:
 *  - BigDecimal is used instead of double/float for all arithmetic so that
 *    classic floating point errors (0.1 + 0.2 != 0.3) do not occur.
 *  - All user input is validated; the app never crashes on bad input,
 *    it reports the problem and lets the user try again.
 */
public class Calculator {

    private static final MathContext PRECISION = new MathContext(12, RoundingMode.HALF_UP);
    // Fixed reference rate for demo purposes (1 USD = 83.00 INR). In a real app
    // this would come from a live exchange-rate API.
    private static final BigDecimal USD_TO_INR = new BigDecimal("83.00");

    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Calculator().run();
    }

    public void run() {
        System.out.println("=== ShadowFox Enhanced Calculator ===");
        boolean keepGoing = true;

        while (keepGoing) {
            printMenu();
            int choice = readMenuChoice();

            try {
                switch (choice) {
                    case 1 -> doArithmetic();
                    case 2 -> doSquareRoot();
                    case 3 -> doExponentiation();
                    case 4 -> doTemperatureConversion();
                    case 5 -> doCurrencyConversion();
                    case 0 -> keepGoing = false;
                    default -> System.out.println("Please choose a number from the menu.");
                }
            } catch (ArithmeticException e) {
                // Covers division by zero and other invalid math operations
                System.out.println("Math error: " + e.getMessage());
            } catch (Exception e) {
                // Safety net so an unexpected error never crashes the app
                System.out.println("Something went wrong: " + e.getMessage());
            }

            if (keepGoing) {
                System.out.println();
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private void printMenu() {
        System.out.println("""
                Choose an option:
                  1) Basic arithmetic (+, -, *, /)
                  2) Square root
                  3) Exponentiation (x^y)
                  4) Temperature conversion (C <-> F <-> K)
                  5) Currency conversion (USD <-> INR)
                  0) Exit
                """);
        System.out.print("Your choice: ");
    }

    private int readMenuChoice() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("That's not a valid number, try again: ");
            }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return new BigDecimal(line);
            } catch (NumberFormatException e) {
                // Handles the "user types abc instead of a number" crash case
                System.out.println("Please enter a valid number (e.g. 12.5).");
            }
        }
    }

    private void doArithmetic() {
        BigDecimal a = readBigDecimal("Enter first number: ");
        System.out.print("Enter operator (+, -, *, /): ");
        String op = scanner.nextLine().trim();
        BigDecimal b = readBigDecimal("Enter second number: ");

        BigDecimal result = switch (op) {
            case "+" -> a.add(b);
            case "-" -> a.subtract(b);
            case "*" -> a.multiply(b);
            case "/" -> {
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Cannot divide by zero.");
                }
                yield a.divide(b, PRECISION);
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };

        System.out.println("Result: " + result);

        // Demonstrates the fix for the classic floating point rounding bug
        if (a.compareTo(new BigDecimal("0.1")) == 0 && b.compareTo(new BigDecimal("0.2")) == 0
                && op.equals("+")) {
            System.out.println("(Using BigDecimal, 0.1 + 0.2 correctly equals 0.3, not 0.30000000000000004)");
        }
    }

    private void doSquareRoot() {
        BigDecimal a = readBigDecimal("Enter a number: ");
        if (a.compareTo(BigDecimal.ZERO) < 0) {
            throw new ArithmeticException("Cannot take the square root of a negative number.");
        }
        System.out.println("Square root: " + a.sqrt(PRECISION));
    }

    private void doExponentiation() {
        BigDecimal base = readBigDecimal("Enter base: ");
        System.out.print("Enter exponent (whole number): ");
        String line = scanner.nextLine().trim();
        int exponent;
        try {
            exponent = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Exponent must be a whole number.");
        }
        System.out.println("Result: " + base.pow(Math.abs(exponent), PRECISION));
    }

    private void doTemperatureConversion() {
        System.out.println("Convert from: 1) Celsius  2) Fahrenheit  3) Kelvin");
        int from = readMenuChoice();
        BigDecimal value = readBigDecimal("Enter temperature value: ");

        BigDecimal celsius = switch (from) {
            case 1 -> value;
            case 2 -> value.subtract(new BigDecimal("32")).multiply(new BigDecimal("5"))
                    .divide(new BigDecimal("9"), PRECISION);
            case 3 -> value.subtract(new BigDecimal("273.15"));
            default -> throw new IllegalArgumentException("Invalid choice.");
        };

        BigDecimal fahrenheit = celsius.multiply(new BigDecimal("9"))
                .divide(new BigDecimal("5"), PRECISION).add(new BigDecimal("32"));
        BigDecimal kelvin = celsius.add(new BigDecimal("273.15"));

        System.out.println("Celsius:    " + celsius.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Fahrenheit: " + fahrenheit.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Kelvin:     " + kelvin.setScale(2, RoundingMode.HALF_UP));
    }

    private void doCurrencyConversion() {
        System.out.println("Convert: 1) USD -> INR   2) INR -> USD");
        int direction = readMenuChoice();
        BigDecimal amount = readBigDecimal("Enter amount: ");

        BigDecimal result = switch (direction) {
            case 1 -> amount.multiply(USD_TO_INR);
            case 2 -> amount.divide(USD_TO_INR, PRECISION);
            default -> throw new IllegalArgumentException("Invalid choice.");
        };

        System.out.println("Converted amount: " + result.setScale(2, RoundingMode.HALF_UP)
                + "  (rate used: 1 USD = " + USD_TO_INR + " INR, for demo purposes)");
    }
}
