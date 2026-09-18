package com.debtsplitter.domain.split;

import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.Share;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.List;

public final class SharesSplit implements SplitStrategy{
    @Override
    public SplitType type() {
        return SplitType.SHARES;
    }

    @Override
    public List<Share> split(Money total, List<UserId> members) {

        var mCount = members.size();

        if

        return List.of();
    }
}
