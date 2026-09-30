package com.debtsplitter.domain.model.exceptions;

import com.debtsplitter.domain.split.SplitType;

import java.util.List;

public class UnknownSplitTypeException extends DomainException {
    final static List<SplitType> expectedSplitTypeList = List.of(SplitType.values());
    final SplitType actualSplitType;

    public UnknownSplitTypeException(SplitType splitType) {
        super(formatMassage(splitType));
        this.actualSplitType = splitType;
    }

    private static String formatMassage(SplitType splitType) {
        return "Actual SplitType: " + splitType.name() + System.lineSeparator() +
                "Expected SplitType: " + expectedSplitTypeList.toString();
    }
}
