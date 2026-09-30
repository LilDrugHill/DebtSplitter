package com.debtsplitter.domain.split;

import com.debtsplitter.domain.model.exceptions.NonEqualMemberCountException;
import com.debtsplitter.domain.model.exceptions.ParticipantsMismatchException;
import com.debtsplitter.domain.model.valueObjects.Money;
import com.debtsplitter.domain.model.valueObjects.Share;
import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.*;

public final class SharesSplit implements SplitStrategy{

    final private Map<UserId, Integer> wights;

    public Map<UserId, Integer> getWights() {
        return wights;
    }

    public SharesSplit(Map<UserId, Integer> wights){

        var cWights = Map.copyOf(wights);

        if (cWights.size() < 2) {
            throw new IllegalArgumentException("wights cannot be less than 2");
        }

        this.wights = cWights;
    }

    @Override
    public SplitType type() {
        return SplitType.SHARES;
    }

    @Override
    public List<Share> split(Money total, List<UserId> members) {

        var cMembers = Set.copyOf(members);
        var mCount = cMembers.size();
        var rList = new ArrayList<Share>();

        if(mCount != wights.size()) {
            throw new NonEqualMemberCountException(wights.size(), mCount);
        }

        var wightsKeys = wights.keySet();
        if (!wightsKeys.containsAll(cMembers)) {
            throw new ParticipantsMismatchException(cMembers, wightsKeys);
        };

        int parts = wights.values().stream().mapToInt(Integer::intValue).sum();
        var moneyInParts = total.divideOnParts(parts);

        long streamElCounter = 0;
        for(Map.Entry<UserId, Integer> entry : wights.entrySet()) {
            var mValue = entry.getValue();
            rList.add(new Share(
                    entry.getKey(),
                    moneyInParts.stream()
                            .skip(streamElCounter)
                            .limit(mValue)
                            .reduce(Money.zero(), Money::add))
            );
            streamElCounter += mValue;
        }

        return rList;
    }
}
