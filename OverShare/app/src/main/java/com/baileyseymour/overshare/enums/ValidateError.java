// Bailey Seymour
// DVP6 - 1903
// ValidateError.java

package com.baileyseymour.overshare.enums;

import android.content.res.Resources;

import com.baileyseymour.overshare.R;

public enum ValidateError {
    BLANK,
    INVALID_URL,
    NOT_USERNAME,
    NOT_PHONE,
    NOT_EMAIL,
    VALID;

    public String getDescription(Resources res) {

        switch (this) {

            case BLANK:
                return res.getString(R.string.you_cant_leave_this_blank);
            case INVALID_URL:
                return res.getString(R.string.this_isnt_valid_url);
            case NOT_USERNAME:
                return res.getString(R.string.not_username);
            case NOT_PHONE:
                return res.getString(R.string.not_phone);
            case NOT_EMAIL:
                return res.getString(R.string.not_email);
        }

        return "";
    }
}
