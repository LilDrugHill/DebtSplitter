package com.debtsplitter.adapters.out.memory;

import com.debtsplitter.domain.model.entities.Expense;
import com.debtsplitter.domain.model.valueObjects.ExpenseId;
import com.debtsplitter.domain.model.valueObjects.GroupId;
import com.debtsplitter.domain.port.ExpenseRepository;

import java.util.*;

public class InMemoryExpenseRepository implements ExpenseRepository {

    private final Map<ExpenseId, Expense> storage = new HashMap<>();

    public Map<ExpenseId, Expense> getStorage() {
        return Map.copyOf(storage);
    }


    @Override
    public void save(Expense expense) {
        storage.compute(expense.getId(),
                (expenseId, expenseInner) -> {
                    if (expenseInner != null) {
                        System.out.printf("Expense with id: %s already exists, but replaced\n", expense.getId());
                    }
                    return expense;
                });

        System.out.printf("Expense with id: %s has been saved\n", expense.getId());
    }

    public void showAll() {
        storage.forEach((expenseId, expenseInner) -> {
            System.out.printf("Expense with id: %s\n", expenseId);
        });
    }

    @Override
    public List<Expense> findByGroup(GroupId groupId) {

        ArrayList<Expense> expenseList = new ArrayList<>();

        for(Expense entry : storage.values()) {
            if (entry.getGroupId().equals(groupId)) expenseList.add(entry);
        }

        return expenseList;
    }

    @Override
    public Optional<Expense> findById(ExpenseId expenseId) {

        Optional<Expense> expense;

        if (storage.containsKey(expenseId)) {
            expense = Optional.of(storage.get(expenseId));
        } else {
            expense = Optional.empty();
        }

        return expense;
    }
}
