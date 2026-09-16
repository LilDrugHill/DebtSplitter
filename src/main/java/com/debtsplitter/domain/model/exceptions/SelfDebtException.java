package com.debtsplitter.domain.model.exceptions;

public class SelfDebtException extends DomainException {
    public SelfDebtException(String message) {
        super(message);
    }
}
