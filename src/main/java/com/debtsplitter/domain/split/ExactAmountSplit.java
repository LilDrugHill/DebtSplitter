package com.debtsplitter.domain.split;

import com.debtsplitter.domain.model.exceptions.SharesDoNotMatchAmountException;
import com.debtsplitter.domain.model.exceptions.NonEqualMemberCountException;
import com.debtsplitter.domain.model.exceptions.ParticipantsMismatchException;
import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.Share;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.*;

public class ExactAmountSplit implements SplitStrategy {

    final private Map<UserId, Money> amounts;
    public  Map<UserId, Money> getAmounts() { return amounts; }

    public ExactAmountSplit(Map<UserId, Money> amounts) {
        var cAmounts = Map.copyOf(amounts);

        if (cAmounts.size() < 2) {
            throw new IllegalArgumentException("amounts cannot be less than 2");
        }

        this.amounts = cAmounts;
    }

    @Override
    public SplitType type() {
        return SplitType.EXACT;
    }

    @Override
    public List<Share> split(Money total, List<UserId> members) {
        var cMembers = Set.copyOf(members);
        var mCount = cMembers.size();
        var rList = new ArrayList<Share>();

        if(mCount != amounts.size()) {
            throw new NonEqualMemberCountException(amounts.size(), mCount);
        }

        var wightsKeys = amounts.keySet();
        if (!wightsKeys.containsAll(cMembers)) {
            throw new ParticipantsMismatchException(cMembers, wightsKeys);
        };

        var amountsSum = this.amounts.values().stream().reduce(Money.zero(), Money::add);

        if (0 != total.compareTo(amountsSum)) {
            throw new SharesDoNotMatchAmountException(amountsSum, total);
        }

        this.amounts.entrySet().stream()
                .map(entry -> new Share(entry.getKey(), entry.getValue()))
                .forEach(rList::add);

        return rList;
    }
}
