package com.debtsplitter.domain.model.exceptions;

import com.debtsplitter.domain.model.valueObjects.UserId;

import java.util.Set;
import java.util.stream.Collectors;

public class ParticipantsMismatchException extends DomainException {
    final private Set<UserId> actual;
    final private Set<UserId> expected;
    private Set<UserId> missing;
    public Set<UserId> getActual() { return actual; }
    public Set<UserId> getExpected() { return expected; }

    public Set<UserId> getMissing() {
        if (missing == null) {
            missing = getDifference(actual, expected);
        }
        return missing;
    }

    public ParticipantsMismatchException(Set<UserId> actual, Set<UserId> expected)
    {
        super(formatMessage(Set.copyOf(actual).toString(), Set.copyOf(expected).toString()));
        this.actual = Set.copyOf(actual);
        this.expected = Set.copyOf(expected);
    }

    private static String formatMessage(String actualToString, String expectedToString) {
        return ("Actual and expected user set is not equal:" + System.lineSeparator()
                + "expected=" + expectedToString + System.lineSeparator()
                + "actual=" + actualToString);
    }
    private Set<UserId> getDifference(Set<UserId> actual, Set<UserId> expected) {
        return expected.stream()
                .filter(userId -> !actual.contains(userId))
                .collect(Collectors.toUnmodifiableSet());
    }

}
