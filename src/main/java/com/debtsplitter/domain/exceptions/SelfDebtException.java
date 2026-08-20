package com.debtsplitter.domain.exceptions;

public class SelfDebtException extends DomainException {
    public SelfDebtException(String message) {
        super(message);
    }
}
