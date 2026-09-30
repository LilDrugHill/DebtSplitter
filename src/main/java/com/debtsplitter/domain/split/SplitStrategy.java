package com.debtsplitter.domain.split;

import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.Share;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.List;

public interface SplitStrategy {
    SplitType type();
    List<Share> split(Money total, List<UserId> members);
}
