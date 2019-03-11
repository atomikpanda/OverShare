// Bailey Seymour
// DVP6 - 1903
// SmartField.java

package com.baileyseymour.overshare.models;

import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.URLUtil;

import java.util.Locale;

// Represents / wraps a field object to provide useful functionality
// aka. converts raw field data like title, value, and type info
// into things like URLs
public class SmartField {
    private Field mField;

    public SmartField(Field field) {
        mField = field;
    }

    public Field getField() {
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
