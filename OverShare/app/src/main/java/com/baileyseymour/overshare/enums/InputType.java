// Bailey Seymour
// DVP6 - 1903
// InputType.java

package com.baileyseymour.overshare.enums;

import android.util.Patterns;
import android.webkit.URLUtil;

import com.baileyseymour.overshare.enums.ValidateError;

public enum InputType {
    TEXT("text"),
    URL("url"),
    URL_USER("url_user"),
    USERNAME("username"),
    PHONE("phone"),
    EMAIL("email");

    final String stringValue;

    InputType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static InputType fromString(String string) {
        for (InputType b : InputType.values()) {
            if (b.stringValue.equalsIgnoreCase(string)) {
                return b;
            }
        }
        return URL;
    }

    public ValidateError validate(String input) {

        // Make sure the input is not null
        if (input == null) return ValidateError.BLANK;

        // Ensure it is not blank
        if (input.trim().isEmpty()) return ValidateError.BLANK;

        // Validates an input based on the expected input type
        switch (this) {

            case TEXT:
                return ValidateError.VALID;
            case URL:
                if (Patterns.WEB_URL.matcher(input).matches())
                    return ValidateError.VALID;
                else
                    return ValidateError.INVALID_URL;
            case URL_USER:
                if (Patterns.WEB_URL.matcher(input).matches())
                    return ValidateError.VALID;
                else
                    return ValidateError.INVALID_URL;
            case USERNAME:
                if (!URLUtil.isValidUrl(input))
                    return ValidateError.VALID;
                else
                    return ValidateError.NOT_USERNAME;
            case PHONE:
                if (Patterns.PHONE.matcher(input).matches())
                    return ValidateError.VALID;
                else
                    return ValidateError.NOT_PHONE;
            case EMAIL:
                if (Patterns.EMAIL_ADDRESS.matcher(input).matches())
                    return ValidateError.VALID;
                else
                    return ValidateError.NOT_EMAIL;
        }

        return ValidateError.BLANK;
    }
}
