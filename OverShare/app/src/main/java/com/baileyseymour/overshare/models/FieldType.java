// Bailey Seymour
// DVP6 - 1903
// FieldType.java

package com.baileyseymour.overshare.models;

public class FieldType {
    // The field type's user display name eg. "Twitter" or "Website"
    private final String mDisplayName;

    // Is the type's value supposed to ultimately be a URL or text
    private final boolean mExpectsURL;
    private final String mValueFormat;

    public FieldType(String displayName, boolean expectsURL) {
        this(displayName, expectsURL, "%s");
    }

    public FieldType(String displayName, boolean expectsURL, String valueFormat) {
        mDisplayName = displayName;
        mExpectsURL = expectsURL;
        mValueFormat = valueFormat;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public boolean isExpectsURL() {
        return mExpectsURL;
    }

    public String getValueFormat() {
        return mValueFormat;
    }

    @Override
    public String toString() {
        return mDisplayName;
    }
}
