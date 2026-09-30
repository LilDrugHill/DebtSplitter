package com.debtsplitter.application.usecase;

import com.debtsplitter.domain.model.entities.Expense;
import com.debtsplitter.domain.model.entities.Group;
import com.debtsplitter.domain.port.ExpenseRepository;
import com.debtsplitter.domain.split.SplitStrategy;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

public class RegisterExpenseService implements RegisterExpenseUseCase {
    public final ExpenseRepository expenseRepository;
    public final SplitStrategyRegistry splitStrategyRegistry;
    public final Clock clock;

    @Override
    public Expense register(RegisterExpenseCommand command) {
        Expense expense;

        var group = new Group(command.groupName(), command.participants());
        var splitStrategy = splitStrategyRegistry.forType(command.splitType());
        var shares = splitStrategy.split(command.amount(), command.participants());
        var createdAt = clock.instant();

        expense = new Expense(command.paidBy(), command.amount(), group.getId(),
                command.splitType(), shares, command.description(), createdAt, command.spentAt());

        expenseRepository.save(expense);
        return expense;
    }

    public RegisterExpenseService(ExpenseRepository repository, SplitStrategyRegistry strategies, Clock clock) {
        this.expenseRepository = Objects.requireNonNull(repository, "expenseRepository");
        this.splitStrategyRegistry = Objects.requireNonNull(strategies, "splitStrategyRegistry");
        this.clock = Objects.requireNonNull(clock, "clock");
    }
}
