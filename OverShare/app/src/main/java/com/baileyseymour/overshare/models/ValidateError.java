package com.baileyseymour.overshare.models;

public enum ValidateError {
    BLANK,
    INVALID_URL,
    NOT_USERNAME,
    NOT_PHONE,
    VALID;

    @Override
    public String toString() {
        switch (this) {

            case BLANK:
                return "You can't leave this blank.";
            case INVALID_URL:
                return "This isn't a valid URL";
            case NOT_USERNAME:
                return "This isn't a valid username.";
            case NOT_PHONE:
                return "This isn't a valid phone number.";
        }
        return "";
    }
}
