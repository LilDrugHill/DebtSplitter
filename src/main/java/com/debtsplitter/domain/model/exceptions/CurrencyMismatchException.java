package com.debtsplitter.domain.model.exceptions;

import java.util.Currency;

public class CurrencyMismatchException extends DomainException {
    private final Currency expected;
    private final Currency actual;
    public CurrencyMismatchException(Currency expected, Currency actual)
    {
        super(formatMessage(expected,actual));
        this.expected = expected;
        this.actual = actual;
    }
    private static String formatMessage(Currency expected, Currency actual) {
        if (actual == null) throw new NullPointerException("actual is null");
        if (expected == null) throw new NullPointerException("expected is null");
        return "Currency mismatch: " + " Expected: " + expected + " Actual: " + actual;
    }


    public Currency getExpected() {
        return expected;
    }

    public Currency getActual() {
        return actual;
    }
}
