package com.debtsplitter.domain.split;

import com.debtsplitter.domain.model.exceptions.SelfDebtException;
import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.Share;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class EqualSplit implements SplitStrategy{

    @Override
    public SplitType type() {
        return SplitType.EQUAL;
    }

    @Override
    public List<Share> split(Money total, List<UserId> members) {

        var mCount = members.size();

        if (mCount < 2) { throw new SelfDebtException("Not enough members.");}

        var store = new ArrayList<Share>();
        var part = total.getAmount().divide(BigDecimal.valueOf(mCount), 2, RoundingMode.DOWN);
        var remainder = total.getAmount().subtract(part.multiply(BigDecimal.valueOf(mCount)));
        for (var i = 0; i < mCount; i++) {
            if (i == 0) {
                store.add(new Share(members.get(i), Money.of(part.add(remainder).toString())));
            } else {
                store.add(new Share(members.get(i), Money.of(part.toString())));
            }
        }

        return List.copyOf(store);
    }
}
