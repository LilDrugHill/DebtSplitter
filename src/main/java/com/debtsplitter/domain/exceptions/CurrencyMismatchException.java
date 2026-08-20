package com.debtsplitter.domain.exceptions;

import java.util.Currency;

public class CurrencyMismatchException extends DomainException {
    private Currency expected;
    private Currency actual;
    public CurrencyMismatchException(Currency expected, Currency actual)
    {
        super(formatMessage(expected,actual));
    }
    private static String formatMessage(Currency expected, Currency actual) {
        if (actual == null) throw new NullPointerException("actual is null");
        if (expected == null) throw new NullPointerException("expected is null");
        return "Currency mismatch: " + " Expected: " + expected + " Actual: " + actual;
    }
}
