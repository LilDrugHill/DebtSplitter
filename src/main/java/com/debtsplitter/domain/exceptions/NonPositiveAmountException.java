package com.debtsplitter.domain.exceptions;

public class NonPositiveAmountException extends DomainException {
    public NonPositiveAmountException(String message) {
        super(message);
    }
}
