package com.debtsplitter.domain.model.entities;

import com.debtsplitter.domain.model.exceptions.NonPositiveAmountException;
import com.debtsplitter.domain.model.exceptions.SelfDebtException;
import com.debtsplitter.domain.model.valueObjects.*;
import com.debtsplitter.domain.split.SplitType;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public class Expense {
    final private ExpenseId id;
    final private UserId paidBy;
    final private Money amount;
    final private GroupId groupId;

    private String description;
    private SplitType splitType;
    private List<Share> shares;

    private Instant createdAt;
    private Instant spendAt;


    public Expense(UserId paidBy, Money amount, GroupId group, Instant spendAt) {
        this(paidBy, amount, group);

        if (spendAt != null) {
            this.spendAt = spendAt;
        }
    }

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

        this.groupId = groupId;


        var clock = Clock.systemDefaultZone();
        createdAt = clock.instant();
        spendAt = Clock.fixed(createdAt, clock.getZone()).instant();
    }

//    public HashMap<User, Money> getParticipantsDept() {
//        var guys = this.getGroup().getParticipants();
//        var gCount = guys.size();
//        List<Money> gAmounds = this.getAmount().divideOnParts(gCount);
//
//        var debtsDict = new HashMap<User, Money>();
//
//        for (int i = 0; i < gCount; i++) {
//            debtsDict.put(guys.get(i), gAmounds.get(i));
//        }
//
//        return  debtsDict;
//    }

    public ExpenseId getId() {
        return id;
    }

    public UserId getPaidBy() {
        return paidBy;
    }

    public Money getAmount() {
        return amount;
    }

    public GroupId getGroup() {
        return groupId;
    }

    public Instant getCreatedAt() { return Clock.fixed(createdAt, ZoneId.systemDefault()).instant(); }
    public Instant getSpendAt() { return Clock.fixed(spendAt, ZoneId.systemDefault()).instant(); }

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
