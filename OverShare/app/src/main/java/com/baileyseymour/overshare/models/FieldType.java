// Bailey Seymour
// DVP6 - 1903
// FieldType.java

package com.baileyseymour.overshare.models;

import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.webkit.URLUtil;

import com.baileyseymour.overshare.enums.InputType;
import com.baileyseymour.overshare.enums.OutputType;
import com.baileyseymour.overshare.enums.ValidateError;

import org.json.JSONObject;

import java.util.Objects;


public class FieldType {

    // JSON keys
    public static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SUGGESTION = "suggestion";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_INPUT = "input";
    private static final String KEY_OUTPUT = "output";
    private static final String KEY_DISPLAY_FORMAT = "display_format";

    // The field type's user display name eg. "Twitter" or "Website"
    private String mDisplayName;

    private String mSuggestion;

    // The raw input kind
    private InputType mInputType;

    // Is the type's value supposed to ultimately be a URL or text
    private OutputType mOutputType;

    private String mValueFormat;

    // The format use for display ONLY
    private String mDisplayFormat = "%s";

    public FieldType(JSONObject jsonObject) {
        if (jsonObject == null) return;

        // Parse field type JSON
        try {
            if (jsonObject.has(KEY_NAME))
                mDisplayName = jsonObject.getString(KEY_NAME);

            if (jsonObject.has(KEY_SUGGESTION))
                mSuggestion = jsonObject.getString(KEY_SUGGESTION);

            if (jsonObject.has(KEY_FORMAT))
                mValueFormat = jsonObject.getString(KEY_FORMAT);

            if (jsonObject.has(KEY_INPUT))
                mInputType = InputType.fromString(jsonObject.getString(KEY_INPUT));

            if (jsonObject.has(KEY_OUTPUT))
                mOutputType = OutputType.fromString(jsonObject.getString(KEY_OUTPUT));

            if (jsonObject.has(KEY_DISPLAY_FORMAT))
                mDisplayFormat = jsonObject.getString(KEY_DISPLAY_FORMAT);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Getters

    private String getDisplayName() {
        return mDisplayName;
    }

    public InputType getInputType() {
        return mInputType;
    }

    private OutputType getOutputType() {
        return mOutputType;
    }

    String getValueFormat() {
        return mValueFormat;
    }

    public String getDisplayFormat() {
        return mDisplayFormat;
    }

    public String getSuggestion() {
        if (mSuggestion == null) {
            return getDisplayName();
        }
        return mSuggestion;
    }

    @NonNull
    @Override
    public String toString() {
        return mDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldType fieldType = (FieldType) o;
        return Objects.equals(getDisplayName(), fieldType.getDisplayName()) &&
                getInputType() == fieldType.getInputType() &&
                getOutputType() == fieldType.getOutputType() &&
                Objects.equals(getValueFormat(), fieldType.getValueFormat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDisplayName(), getInputType(), getOutputType(), getValueFormat());
    }
}
