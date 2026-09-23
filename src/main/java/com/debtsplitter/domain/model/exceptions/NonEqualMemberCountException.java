package com.debtsplitter.domain.model.exceptions;

public class NonEqualMemberCountException extends DomainException {

    final private int gottenValue;
    final private int waitedValue;



    public NonEqualMemberCountException(Integer gottenValue, Integer waitedValue) {
        super(formatMessage(gottenValue, waitedValue));
        this.gottenValue = gottenValue;
        this.waitedValue = waitedValue;
    }

    private static String formatMessage(int valueFromConstructor, int waitedValue) {
        return ("Values must be equal: "
                + "gottenValue = " + valueFromConstructor
                + ", waitedValue = " + waitedValue);
    }

    public int getValueFromConstructor() { return gottenValue; }
    public int getWaitedValue() { return waitedValue; }
}
