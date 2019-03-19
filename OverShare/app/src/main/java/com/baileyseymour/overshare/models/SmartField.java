// Bailey Seymour
// DVP6 - 1903
// SmartField.java

package com.baileyseymour.overshare.models;

import android.telephony.PhoneNumberUtils;
import android.util.Patterns;

import com.baileyseymour.overshare.utils.FieldUtils;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.util.Locale;

// Represents / wraps a field object to provide useful functionality
// aka. converts raw field data like title, value, and type info
// into things like URLs
public class SmartField {
    private final Field mField;
    private final FieldType mFieldType;

    public SmartField(Field field, FieldType fieldType) {
        mField = field;
        mFieldType = fieldType;
    }

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
        if (mFieldType.getInputType() == FieldType.InputType.PHONE) {
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

    public String generateURL() {
        String value = getValue();
        return String.format(Locale.US, mFieldType.getValueFormat(), value);
    }

    public boolean isValueURL() {
        String value = getValue();
        if (mFieldType != null) {
            value = generateURL();
        }
        return Patterns.WEB_URL.matcher(value).matches() || value.startsWith("tel:")
        || value.startsWith("mailto:");
    }
}
