package com.debtsplitter.domain.model.valueObjects;

import com.debtsplitter.domain.model.exceptions.SelfDebtException;

import java.util.Objects;

/**
 * Рекомендация системы: кому и сколько перевести, чтобы балансы обнулились.
 * Не факт платежа — ничего не хранится и не обязано быть исполненным.
 */
public record Transfer(UserId from, UserId to, Money amount) {

    public Transfer {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(amount, "amount");

        if (from.equals(to)) {
            throw new SelfDebtException("Transfer to self: " + from);
        }
        if (amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Transfer amount must be positive: " + amount);
        }
    }

    @Override
    public String toString() {
        return from.id() + " -> " + to.id() + "  " + amount;
    }
}
