// Bailey Seymour
// DVP6 - 1903
// FieldType.java

package com.baileyseymour.overshare.models;

public class FieldType {
    // The field type's user display name eg. "Twitter" or "Website"
    private final String mDisplayName;

    // Is the type's value supposed to ultimately be a URL or text
    private final ContentType mContentType;

    public enum ContentType {
        TEXT,
        URL
    }

    private final String mValueFormat;

    public FieldType(String displayName, ContentType contentType) {
        this(displayName, contentType, "%s");
    }

    public FieldType(String displayName, ContentType contentType, String valueFormat) {
        mDisplayName = displayName;
        mContentType = contentType;
        mValueFormat = valueFormat;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public ContentType getContentType() {
        return mContentType;
    }

    public String getValueFormat() {
        return mValueFormat;
    }

    @Override
    public String toString() {
        return mDisplayName;
    }
}
