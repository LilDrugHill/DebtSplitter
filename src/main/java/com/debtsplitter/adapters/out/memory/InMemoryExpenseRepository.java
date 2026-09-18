package com.debtsplitter.adapters.out.memory;

import com.debtsplitter.domain.model.entities.Expense;
import com.debtsplitter.domain.model.valueObjects.ExpenseId;
import com.debtsplitter.domain.model.valueObjects.GroupId;
import com.debtsplitter.domain.port.ExpenseRepository;

import java.util.*;

public class InMemoryExpenseRepository implements ExpenseRepository {

    private final Map<ExpenseId, Expense> storage = new HashMap<>();

    @Override
    public void save(Expense expense) {

    }

    @Override
    public List<Expense> findByGroup(GroupId groupId) {

        ArrayList<Expense> tempList = new ArrayList<>();

        for(Expense entry : storage.values()) {
            if (entry.getGroupId() == groupId.id()) tempList.add(entry);
        }
        return List.copyOf(tempList);
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
