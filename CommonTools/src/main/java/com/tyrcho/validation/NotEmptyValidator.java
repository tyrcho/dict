package com.tyrcho.validation;

/**
 * Validates that an object is not null and its string representation is not empty.
 * 
 * @author MDA
 * @version NP
 */
public class NotEmptyValidator<T> extends PatternValidator<T> {
    /**
     * Constructs the NotEmptyValidator with a constant reason used to describe the possible ValidationFailedExceptions.
     * 
     * @param reason
     *            the reason used to construct the ValidationFailedException thrown during validation
     */
    public NotEmptyValidator(String reason) {
        super(".+", reason, false);
    }

    public NotEmptyValidator() {
        this(null);
    }

}
