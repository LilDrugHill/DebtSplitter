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
    final private SplitType splitType;
    final private List<Share> shares;

    private String description;

    private final Instant createdAt;
    private final Instant spendAt;


    public Expense(UserId paidBy, Money amount, GroupId groupId,
                   SplitType splitType, List<Share> shares, String description, Instant createdAt, Instant spendAt) {

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

        if (splitType == null) {
            throw  new IllegalArgumentException("splitType is null");
        }
        this.splitType = splitType;


        var tempShares = List.copyOf(shares);
        if (tempShares.size() < 2) {
            throw new IllegalArgumentException("Shares must have at least 2 Shares");
        }
        this.shares = tempShares;

        this.description = (description == null) ? "" : description;


        Objects.requireNonNull(createdAt, "createdAt is null");
        this.createdAt = createdAt;

        this.spendAt = Objects.requireNonNullElse(spendAt, createdAt);
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

    public GroupId getGroupId() {
        return groupId;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public Instant getCreatedAt() { return Clock.fixed(createdAt, ZoneId.systemDefault()).instant(); }
    public Instant getSpendAt() { return Clock.fixed(spendAt, ZoneId.systemDefault()).instant(); }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description;
    }
    // immutable set in contractor
    public List<Share> getShares() { return shares; }

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
