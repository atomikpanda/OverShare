// Bailey Seymour
// DVP6 - 1903
// Field.java

package com.baileyseymour.overshare.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field {
    private String title;
    private String value;
    private String type;

    private Field(Map<String, String> map) {
        title = map.get("title");
        value = map.get("value");
        type = map.get("type");
    }

    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("value", value);
        map.put("type", type);

        return map;
    }

    public static ArrayList<Field> fromListOfMaps(List<Map<String, String>> list) {
        ArrayList<Field> fields = new ArrayList<>();
        if (list != null) {
            for (Map<String, String> aMap : list) {
                fields.add(new Field(aMap));
            }
        }
        return fields;
    }

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
