package com.debtsplitter.domain.entities;

import com.debtsplitter.domain.exceptions.InvalidAmoundFormatException;
import com.debtsplitter.domain.exceptions.NonPositiveAmountException;
import com.debtsplitter.domain.exceptions.SelfDebtException;
import com.debtsplitter.domain.valueObjects.Money;

import java.util.*;
import java.util.function.Consumer;

public class Expense {
    final private Integer id;
    final private User payer;
    final private Money amount;
    final private Group group;

    public Expense(User payer, Money amount, Group group) {

        this.id = new Random().nextInt();

        if (payer != null) {
            this.payer = payer;
        } else {
            throw  new IllegalArgumentException("payer is null");
        }

        if (amount == null) {
            throw new IllegalArgumentException("amount is null");
        }
        if (amount.isNegative()) {
            throw new NonPositiveAmountException(amount.getAmount().toString());
        }
        if (amount.isZero()) {
            throw new NonPositiveAmountException("amount is zero");
        }
        this.amount = amount;

        if (group == null) {
            throw  new IllegalArgumentException("group is null");
        }
        if (group.getParticipants().contains(payer)) {
            throw new SelfDebtException("%s@%s%d cant be owe to self".formatted(payer.getName(),
                                                                                payer.getClass().getName(),
                                                                                group.getId()));
        }
        this.group = group;
    }

    public HashMap<User, Money> getParticipantsDept() {
        var guys = this.getGroup().getParticipants();
        var gCount = guys.size();
        List<Money> gAmounds = this.getAmount().divideOnParts(gCount);

        var debtsDict = new HashMap<User, Money>();

        for (int i = 0; i < gCount; i++) {
            debtsDict.put(guys.get(i), gAmounds.get(i));
        }

        return  debtsDict;
    }

    public Integer getId() {
        return id;
    }

    public User getPayer() {
        return payer;
    }

    public Money getAmount() {
        return amount;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Expense)) return false;
        return  (this.id != null && Objects.equals(this.id, ((Expense) obj).getId()));
    }

    @Override
    public int hashCode() {
        if (id == null) return 11;
        return Objects.hash(id);
    }

}
