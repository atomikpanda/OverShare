// Bailey Seymour
// DVP6 - 1903
// FieldType.java

package com.baileyseymour.overshare.models;

import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.webkit.URLUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Objects;


public class FieldType {

    public static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SUGGESTION = "suggestion";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_INPUT = "input";
    private static final String KEY_OUTPUT = "output";

    // Input type string representations
    private static final String INPUT_TEXT = "text";
    private static final String INPUT_URL = "url";
    private static final String INPUT_URL_USER = "url_user";
    private static final String INPUT_PHONE = "phone";
    private static final String INPUT_USERNAME = "username";
    private static final String INPUT_EMAIL = "email";

    // The field type's user display name eg. "Twitter" or "Website"
    private String mDisplayName;

    private String mSuggestion;

    // The raw input kind
    private InputType mInputType;

    // Is the type's value supposed to ultimately be a URL or text
    private OutputType mOutputType;


    public enum InputType {
        TEXT,
        URL,
        URL_USER,
        USERNAME,
        PHONE,
        EMAIL;

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

    public enum OutputType {
        TEXT,
        URL
    }

    private String mValueFormat;

    public FieldType(String displayName, InputType inputType, OutputType outputType) {
        this(displayName, inputType, outputType, "%s");
    }

    public FieldType(String displayName, InputType inputType, OutputType outputType, String valueFormat) {
        mDisplayName = displayName;
        mInputType = inputType;
        mOutputType = outputType;
        mValueFormat = valueFormat;
    }

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
                mInputType = inputTypeFromString(jsonObject.getString(KEY_INPUT));

            if (jsonObject.has(KEY_OUTPUT))
                mOutputType = outputTypeFromString(jsonObject.getString(KEY_OUTPUT));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static InputType inputTypeFromString(String str) {
        switch (str.toLowerCase()) {
            case INPUT_TEXT:
                return InputType.TEXT;
            case INPUT_URL:
                return InputType.URL;
            case INPUT_URL_USER:
                return InputType.URL_USER;
            case INPUT_PHONE:
                return InputType.PHONE;
            case INPUT_USERNAME:
                return InputType.USERNAME;
            case INPUT_EMAIL:
                return InputType.EMAIL;
            default:
                return InputType.URL;
        }
    }

    private static OutputType outputTypeFromString(String str) {
        switch (str.toLowerCase()) {
            case "text":
                return OutputType.TEXT;
            case "url":
                return OutputType.URL;
            default:
                return OutputType.URL;
        }
    }

    public static String stringFromInputType(InputType inputType) {
        switch (inputType) {

            case TEXT:
                return INPUT_TEXT;
            case URL:
                return INPUT_URL;
            case URL_USER:
                return INPUT_URL_USER;
            case USERNAME:
                return INPUT_USERNAME;
            case EMAIL:
                return INPUT_EMAIL;
            case PHONE:
                return INPUT_PHONE;
        }
        return "";
    }

    // Getters

    public String getDisplayName() {
        return mDisplayName;
    }

    public InputType getInputType() {
        return mInputType;
    }

    public OutputType getOutputType() {
        return mOutputType;
    }

    public String getValueFormat() {
        return mValueFormat;
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
