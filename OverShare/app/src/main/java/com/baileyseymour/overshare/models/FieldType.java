// Bailey Seymour
// DVP6 - 1903
// FieldType.java

package com.baileyseymour.overshare.models;

public class FieldType {
    private String mDisplayName;
    private boolean mExpectsURL;
    private String mValueFormat;

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
