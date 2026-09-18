package com.debtsplitter.domain.port;

import com.debtsplitter.domain.model.entities.Expense;
import com.debtsplitter.domain.model.valueObjects.ExpenseId;
import com.debtsplitter.domain.model.valueObjects.GroupId;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
    void save(Expense expense);
    List<Expense> findByGroup(GroupId groupId);
    Optional<Expense> findById(ExpenseId expenseId);
}
