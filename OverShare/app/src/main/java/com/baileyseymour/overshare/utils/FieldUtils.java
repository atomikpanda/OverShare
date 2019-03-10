// Bailey Seymour
// DVP6 - 1903
// FieldUtils.java

package com.baileyseymour.overshare.utils;

import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.models.FieldType;

import java.util.HashMap;
import java.util.Map;

public class FieldUtils {

    private static Map<String, FieldType> AVAILABLE_FIELDS = new HashMap<>();
    static {
        AVAILABLE_FIELDS.put("url", new FieldType("Website (URL)", true));
        AVAILABLE_FIELDS.put("twitter",
                new FieldType("Twitter", false, "https://twitter.com/%s"));
    }

    public static FieldType fieldTypeFromId(String fieldId) {
        return AVAILABLE_FIELDS.get(fieldId);
    }
}
