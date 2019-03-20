// Bailey Seymour
// DVP6 - 1903
// EditTextUtils.java

package com.baileyseymour.overshare.utils;

import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;

import com.baileyseymour.overshare.models.FieldType;

public class EditTextUtils {

    // Gets the string of an edit text easily
    public static String getString(TextInputEditText editText) {
        if (editText == null) return "";

        Editable text = editText.getText();

        if (text == null) return "";

        return text.toString();
    }

    public static void setEditInputType(EditText editText, com.baileyseymour.overshare.enums.InputType inputType) {
        switch (inputType) {

            case TEXT:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case URL:
            case URL_USER:
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                break;
            case USERNAME:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case PHONE:
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case EMAIL:
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
        }
    }
}
