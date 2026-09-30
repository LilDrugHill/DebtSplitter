package com.debtsplitter.domain.service;

import com.debtsplitter.domain.model.entities.Expense;
import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.Share;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceCalculator {
    public static Map<UserId, Money> calculate(Collection<Expense> expensesInp) {

        var expenses = List.copyOf(expensesInp);
        Map<UserId, Money> balances = new HashMap<>();

        for(Expense expense : expenses) {
            balances.compute(expense.getPaidBy(),
                    (k, v) -> v == null ? expense.getAmount() : v.add(expense.getAmount()));

            for(Share share : expense.getShares()) {
                balances.compute(share.userId(),
                        (k, v) -> v == null ? share.amount().multiply(-1) : v.subtract(share.amount()));
            }
        }

        return balances;
    }
}
