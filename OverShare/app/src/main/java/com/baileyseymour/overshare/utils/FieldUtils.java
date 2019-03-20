// Bailey Seymour
// DVP6 - 1903
// FieldUtils.java

package com.baileyseymour.overshare.utils;

import android.content.res.Resources;
import android.util.Log;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.models.FieldType;

import org.apache.commons.io.IOUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.baileyseymour.overshare.models.FieldType.KEY_ID;

// Util class that is responsible for managing the list of available fields to choose from
public class FieldUtils {

    private static final LinkedHashMap<String, FieldType> AVAILABLE_FIELDS = new LinkedHashMap<>();
    private static final String UTF_8_ENCODING = "UTF-8";
    private static final String TAG = "FieldUtils";

    // Initialize at app startup
    public static void init(Resources res) {
        readFromJSON(res);
        Log.d(TAG, "init: Field Utils Initialized");
    }

    // Provides a standard way to access a field by its written type as in db
    public static FieldType fieldTypeFromId(String fieldId) {
        return getAvailableFields().get(fieldId);
    }

    public static LinkedHashMap<String, FieldType> getAvailableFields() {
        return AVAILABLE_FIELDS;
    }

    private static void readFromJSON(Resources res) {
        try (InputStream is = res.openRawResource(R.raw.field_types)) {
            String jsonString = IOUtil.toString(is, UTF_8_ENCODING);
            if (jsonString != null) {

                // Loop through field types array
                JSONArray array = new JSONArray(jsonString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject innerObj = array.getJSONObject(i);

                    // Add field type objects to the map
                    if (innerObj != null) {
                        FieldType type = new FieldType(innerObj);
                        getAvailableFields().put(innerObj.getString(KEY_ID), type);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int indexOfFieldType(String identifier) {
        return new ArrayList<>(getAvailableFields().keySet()).indexOf(identifier);
    }

    // Gets the helper text based on the FieldType's getInputType()
    // (the text displayed when a user is inputting into a field)
    public static String helperText(FieldType fieldType, Resources res) {
        try (InputStream is = res.openRawResource(R.raw.input_types)) {

            String jsonString = IOUtil.toString(is, UTF_8_ENCODING);

            if (jsonString != null) {
                JSONObject root = new JSONObject(jsonString);

                return root.getString(fieldType.getInputType().getStringValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}
