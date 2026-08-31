package com.debtsplitter.domain.exceptions;

public class InvalidAmoundFormatException extends DomainException {
    private final static String validFormat = "0.1";
    private final Object inputFormat;

    public <T> InvalidAmoundFormatException(T inputFormat) {
        super(formatMessage(inputFormat));
        this.inputFormat = (Object) inputFormat;
    }

    private static <T> String formatMessage(T inputFormat) {
        return inputFormat.getClass().getSimpleName() + ": " + inputFormat + "is invalid."
                + System.lineSeparator()
                + "Use only" + validFormat.getClass() + "with format like this:" + validFormat;
    }

    public Object getInputFormat() {
        return inputFormat;
    }
}
