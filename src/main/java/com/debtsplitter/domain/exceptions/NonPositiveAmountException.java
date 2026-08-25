package com.debtsplitter.domain.exceptions;

public class NonPositiveAmountException extends DomainException {
    private final String actual;

    public NonPositiveAmountException(final String actual) {
        super(formatMessage(actual));
        this.actual = actual;
    }
    private static String formatMessage(String actual) {
        return String.format("Non negative amound expected. Gotten: %s", actual);
    }


    public String getActual() {
        return actual;
    }
}
