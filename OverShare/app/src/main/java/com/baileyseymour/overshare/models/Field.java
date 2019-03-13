// Bailey Seymour
// DVP6 - 1903
// Field.java

package com.baileyseymour.overshare.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.baileyseymour.overshare.interfaces.Constants.KEY_TITLE;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_TYPE;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_VALUE;

public class Field implements Serializable {
    private final String title;
    private final String value;
    private final String type;

    // Create a field from a db style map
    private Field(Map<String, String> map) {
        title = map.get(KEY_TITLE);
        value = map.get(KEY_VALUE);
        type = map.get(KEY_TYPE);
    }

    // Converts a field to a map
    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_TITLE, title);
        map.put(KEY_VALUE, value);
        map.put(KEY_TYPE, type);

        return map;
    }

    // Used to convert the raw db list of maps to an array of field objects
    public static ArrayList<Field> fromListOfMaps(List<Map<String, String>> list) {
        ArrayList<Field> fields = new ArrayList<>();
        if (list != null) {
            for (Map<String, String> aMap : list) {
                fields.add(new Field(aMap));
            }
        }
        return fields;
    }

    // Getters

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
