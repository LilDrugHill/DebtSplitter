package com.debtsplitter.domain.model.exceptions;

import com.debtsplitter.domain.model.valueObjects.Money;

public class SharesDoNotMatchAmountException extends DomainException {
    final private Money expected;
    final private Money actual;
    public Money getExpected() {return this.expected;}
    public Money getActual() {return this.actual;}


    public SharesDoNotMatchAmountException(Money expected, Money actual) {
        super(formatMessage(expected.getAmount().toString(), actual.getAmount().toString()));
        this.expected = expected;
        this.actual = actual;
    }

    private static String formatMessage(String expected, String actual) {
        return ("Expected amount: " + expected +
                "Actual amount: " + actual + System.lineSeparator()
                + "They must be equal");
    }
}
