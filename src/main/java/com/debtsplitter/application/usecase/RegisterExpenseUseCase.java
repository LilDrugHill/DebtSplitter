package com.debtsplitter.application.usecase;

import com.debtsplitter.domain.model.entities.Expense;

public interface RegisterExpenseUseCase {
    Expense register(RegisterExpenseCommand command);
}
