// Bailey Seymour
// DVP6 - 1903
// SmartField.java

package com.baileyseymour.overshare.models;

import android.telephony.PhoneNumberUtils;
import android.util.Patterns;

import com.baileyseymour.overshare.enums.InputType;
import com.baileyseymour.overshare.utils.FieldUtils;

import java.util.Locale;

// Represents / wraps a field object to provide useful functionality
// aka. converts raw field data like title, value, and type info
// into things like URLs
public class SmartField {
    private final Field mField;
    private final FieldType mFieldType;

    public SmartField(Field field) {
        mField = field;
        mFieldType = FieldUtils.fieldTypeFromId(mField.getType());
    }

    private Field getField() {
        return mField;
    }

    public FieldType getFieldType() {
        return mFieldType;
    }

    private String getValue() {
        String value = getField().getValue();

        // Handle formatting phone
        if (mFieldType.getInputType() == InputType.PHONE) {
            String normalized = PhoneNumberUtils.normalizeNumber(value);
            String formatted = PhoneNumberUtils.formatNumberToE164(normalized, Locale.getDefault().getCountry());

            if (formatted == null) return value;

            normalized = PhoneNumberUtils.normalizeNumber(formatted);

            if (normalized != null) {
                normalized = normalized.replaceAll("\\+", "");
                value = normalized;
            }
        }

        return value;
    }

    private String prepareValueForURL(String value) {
        // WhatsApp requires a stripped phone number for urls
        if (mField.getType().equals("whatsapp")) {
            return digitsOnly(value);
        }

        return value;
    }

    private String digitsOnly(String value) {
        StringBuilder stripped = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c))
                stripped.append(c);
        }
        return stripped.toString();
    }

    public String generateURL() {
        String value = prepareValueForURL(getValue());
        return String.format(Locale.US, mFieldType.getValueFormat(), value);
    }

    public String clipboardValue() {
        String textToCopy = getValue();

        if (isValueURL()) {
            textToCopy = generateURL();
        }

        // Don't use mailto: on copy
        if (textToCopy.startsWith("mailto:")) {
            textToCopy = getValue();
        }

        // Make the phone number look pretty
        if (textToCopy.startsWith("tel:") || textToCopy.startsWith("sms:")) {
            return getValue();
        }

        return textToCopy;
    }

    public boolean isValueURL() {
        String value = getValue();

        if (mFieldType != null) {
            value = generateURL();
        }

        return Patterns.WEB_URL.matcher(value).matches()
                || value.startsWith("tel:")
                || value.startsWith("sms:")
                || value.startsWith("mailto:");
    }
}
