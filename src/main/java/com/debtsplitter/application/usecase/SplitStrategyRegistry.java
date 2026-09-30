package com.debtsplitter.application.usecase;

import com.debtsplitter.domain.model.exceptions.UnknownSplitTypeException;
import com.debtsplitter.domain.split.SplitStrategy;
import com.debtsplitter.domain.split.SplitType;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class SplitStrategyRegistry {

    private final Map<SplitType, SplitStrategy> strategies;

    public SplitStrategyRegistry(List<? extends SplitStrategy> all) {
        this.strategies = all.stream()
                .collect(toMap(SplitStrategy::type, identity()));
    }

    public SplitStrategy forType(SplitType type) {
        SplitStrategy strategy = strategies.get(type);
        if (strategy == null) throw new UnknownSplitTypeException(type);
        return strategy;
    }
}