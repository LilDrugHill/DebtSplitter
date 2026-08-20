package com.debtsplitter.domain.valueObjects;

import com.debtsplitter.domain.exceptions.CurrencyMismatchException;
import com.debtsplitter.domain.exceptions.DomainException;
import com.debtsplitter.domain.exceptions.InvalidAmoundFormatException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class Money implements Comparable<Money> {
    private final BigDecimal amount;
    private final Currency currency;

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    private Money(BigDecimal amount, Currency currency) {
            if (amount == null) { throw new NullPointerException("Amount cannot be null."); }
            if (currency == null) { throw new NullPointerException("Currency cannot be null."); }
            if (amount.scale() != 2) { throw new IllegalArgumentException("BYN must be in 0.01 format.");}
            this.amount = amount;
            this.currency = currency;
    }
    // TODO: Replace hardcode in .env
    public static Money of(String amount) {
         try {
             return new Money(
                     new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN),
                     Currency.getInstance("BYN"));
         }  catch (NumberFormatException e) {
             throw new InvalidAmoundFormatException(amount);
         }

    }

    public Money add(Money other) {
        currencyMismatchThrower(this.currency, other.currency);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        currencyMismatchThrower(this.currency, other.currency);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(Integer multiplier) {
        return new Money(amount.multiply(new BigDecimal(multiplier)), currency);
    }

    public Money negate() {
        return new Money(amount.negate(), currency);
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private static void currencyMismatchThrower(Currency first, Currency second) {
        if (!(first.equals(second))) {
            throw new CurrencyMismatchException(first, second);
        }
    }

    public List<Money> divideOnParts(int N) {
        if (N <= 0) { throw new IllegalArgumentException("N must be positive."); }
        var store = new ArrayList<Money>();
        var part = this.amount.divide(BigDecimal.valueOf(N), 2, RoundingMode.DOWN);
        var remainder = this.amount.subtract(part.multiply(BigDecimal.valueOf(N)));
        for (var i = 0; i < N; i++) {
            if (i == 0) store.add(Money.of(part.add(remainder).toString()));
            store.add(Money.of(part.toString()));
        }
        return store;
    }

    @Override
    public int compareTo(Money o) {
        currencyMismatchThrower(this.currency, o.currency);
        return Integer.compare(this.amount.compareTo(o.amount), 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // link check
        if (o == null || getClass() != o.getClass()) return false; // null/class check
        var money = (Money) o; // upscale to this class
        if (currency != money.currency) return false; // currency check
        return this.amount.compareTo(money.amount) == 0; // amound check
    }

    @Override
    public int hashCode() {
        return amount.hashCode() - currency.hashCode();
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }


}

