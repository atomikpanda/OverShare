// Bailey Seymour
// DVP6 - 1903
// FieldUtils.java

package com.baileyseymour.overshare.utils;

import android.content.res.Resources;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.models.FieldType;

import org.apache.commons.io.IOUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.baileyseymour.overshare.models.FieldType.*;

// Util class that is responsible for managing the list of available fields to choose from
public class FieldUtils {

    private static final Map<String, FieldType> AVAILABLE_FIELDS = new LinkedHashMap<>();


    // Provides a standard way to access a field by its written type as in db
    public static FieldType fieldTypeFromId(String fieldId) {
        return AVAILABLE_FIELDS.get(fieldId);
    }

    public static Map<String, FieldType> getAvailableFields(Resources res) {
        if (AVAILABLE_FIELDS.size() < 1) {
            readFromJSON(res);
        }
        return AVAILABLE_FIELDS;
    }

    private static void readFromJSON(Resources res) {
        try (InputStream is = res.openRawResource(R.raw.field_types)) {
            String jsonString = IOUtil.toString(is, "UTF-8");
            if (jsonString != null) {
                JSONArray array = new JSONArray(jsonString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject innerObj = array.getJSONObject(i);

                    if (innerObj != null) {
                        FieldType type = new FieldType(innerObj);
                        AVAILABLE_FIELDS.put(innerObj.getString(KEY_ID), type);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int indexOfFieldType(Resources res, String identifier) {
        return new ArrayList<>(getAvailableFields(res).keySet()).indexOf(identifier);
    }

    public static String helperText(FieldType fieldType, Resources res) {
        try (InputStream is = res.openRawResource(R.raw.input_types)) {
            String jsonString = IOUtil.toString(is, "UTF-8");
            if (jsonString != null) {
                JSONObject root = new JSONObject(jsonString);

                return root.getString(FieldType.stringFromInputType(fieldType.getInputType()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}
