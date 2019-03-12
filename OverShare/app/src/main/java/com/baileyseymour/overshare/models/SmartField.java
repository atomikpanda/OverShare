// Bailey Seymour
// DVP6 - 1903
// SmartField.java

package com.baileyseymour.overshare.models;

import android.util.Patterns;

import java.util.Locale;

// Represents / wraps a field object to provide useful functionality
// aka. converts raw field data like title, value, and type info
// into things like URLs
class SmartField {
    private final Field mField;

    public SmartField(Field field) {
        mField = field;
    }

    private Field getField() {
        return mField;
    }

    public String generateURL(FieldType type) {
        return String.format(Locale.US, type.getValueFormat(), getField().getValue());
    }

    public boolean isValueURL() {
        String value = mField.getValue();
        return Patterns.WEB_URL.matcher(value).matches();
    }
}
