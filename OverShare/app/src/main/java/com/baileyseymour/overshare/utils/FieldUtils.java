// Bailey Seymour
// DVP6 - 1903
// FieldUtils.java

package com.baileyseymour.overshare.utils;

import com.baileyseymour.overshare.models.FieldType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

// Util class that is responsible for managing the list of available fields to choose from
public class FieldUtils {

    private static Map<String, FieldType> AVAILABLE_FIELDS = new LinkedHashMap<>();
    static {
        AVAILABLE_FIELDS.put("url", new FieldType("Website (URL)", true));
        AVAILABLE_FIELDS.put("twitter",
                new FieldType("Twitter", false, "https://twitter.com/%s"));
    }

    // Provides a standard way to access a field by its written type as in db
    public static FieldType fieldTypeFromId(String fieldId) {
        return AVAILABLE_FIELDS.get(fieldId);
    }

    public static Map<String, FieldType> getAvailableFields() {
        return AVAILABLE_FIELDS;
    }

    public static int indexOfFieldType(String identifier) {
        return new ArrayList<>(getAvailableFields().keySet()).indexOf(identifier);
    }
}
