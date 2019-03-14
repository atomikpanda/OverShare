// Bailey Seymour
// DVP6 - 1903
// FieldType.java

package com.baileyseymour.overshare.models;

import android.support.annotation.NonNull;
import android.util.Patterns;
import android.webkit.URLUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class FieldType {

    public static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_INPUT = "input";
    private static final String KEY_OUTPUT = "output";

    // The field type's user display name eg. "Twitter" or "Website"
    private String mDisplayName;

    // The raw input kind
    private InputType mInputType;

    // Is the type's value supposed to ultimately be a URL or text
    private OutputType mOutputType;


    public enum InputType {
        TEXT,
        URL,
        URL_USER,
        USERNAME,
        PHONE;

        public ValidateError validate(String input) {
            if (input == null) return ValidateError.BLANK;

            if (input.trim().isEmpty()) return ValidateError.BLANK;

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

        try {
            if (jsonObject.has(KEY_NAME))
                mDisplayName = jsonObject.getString(KEY_NAME);

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
            case "text":
                return InputType.TEXT;
            case "url":
                return InputType.URL;
            case "url_user":
                return InputType.URL_USER;
            case "phone":
                return InputType.PHONE;
            case "username":
                return InputType.USERNAME;
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
                return "text";
            case URL:
                return "url";
            case URL_USER:
                return "url_user";
            case USERNAME:
                return "username";
            case PHONE:
                return "phone";
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

    @NonNull
    @Override
    public String toString() {
        return mDisplayName;
    }
}
