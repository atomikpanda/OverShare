// Bailey Seymour
// DVP6 - 1903
// EditTextUtils.java

package com.baileyseymour.overshare.utils;

import android.support.design.widget.TextInputEditText;
import android.text.Editable;

public class EditTextUtils {

    // Gets the string of an edit text easily
    public static String getString(TextInputEditText editText) {
        if (editText == null) return "";

        Editable text = editText.getText();

        if (text == null) return "";

        return text.toString();
    }
}
