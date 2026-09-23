package com.debtsplitter.domain.model.entities;

import com.debtsplitter.domain.model.exceptions.NonPositiveAmountException;
import com.debtsplitter.domain.model.exceptions.SelfDebtException;
import com.debtsplitter.domain.model.valueObjects.ExpenseId;
import com.debtsplitter.domain.model.valueObjects.GroupId;
import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.*;

public class Expense {
    final private ExpenseId id;
    final private UserId paidBy;
    final private Money amount;
    final private GroupId group;
    final private Date createdAt = new Date();
    final private Date spendAt;


    public Expense(ExpenseId id, UserId paidBy, Money amount, GroupId group) {}

    public Expense(UserId paidBy, Money amount, GroupId groupId) {

        this.id = new ExpenseId(UUID.randomUUID().toString());

        if (paidBy != null) {
            this.paidBy = paidBy;
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

        if (groupId == null) {
            throw  new IllegalArgumentException("group is null");
        }
        if (group.getParticipants().contains(payer)) {
            throw new SelfDebtException("%s@%s%d cant be owe to self".formatted(payer.getName(),
                                                                                payer.getClass().getName(),
                                                                                group.getId()));
        }
        this.group = group;

        if (spendAt == null) {
            ;
        }

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

    public ExpenseId getId() {
        return id;
    }

    public UserId getPaidBy() {
        return paidBy;
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
        if (!(obj instanceof ExpenseId)) return false;
        return  Integer.parseInt(id.id()) == Integer.parseInt(((ExpenseId) obj).id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
