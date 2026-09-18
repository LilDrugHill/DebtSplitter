package com.debtsplitter.domain.split;

import com.debtsplitter.domain.model.valueObjects.Money;

public interface SplitStrategy {
    SplitType type();
    List<Share> split(Money total, List<UserId> members);
}
