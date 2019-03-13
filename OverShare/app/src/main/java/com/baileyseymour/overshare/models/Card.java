// Bailey Seymour
// DVP6 - 1903
// Card.java

package com.baileyseymour.overshare.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static com.baileyseymour.overshare.interfaces.Constants.KEY_CREATED_BY_UID;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_CREATED_TIMESTAMP;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_FIELDS;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_HEX_ID;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_TITLE;

// Some properties are only used by internal fire base methods
// and the java compiler does not recognize this
@SuppressWarnings("unused")
@IgnoreExtraProperties
public class Card implements Serializable {

    // Database model properties
    // Important: Each custom class must have a public constructor that takes no arguments.
    // In addition, the class must include a public getter for each property.

    @PropertyName(KEY_TITLE)
    private String title;

    @PropertyName(KEY_HEX_ID)
    private String hexId;

    @PropertyName(KEY_FIELDS)
    private ArrayList<Map<String, String>> fields;

    @PropertyName(KEY_CREATED_TIMESTAMP)
    private @ServerTimestamp
    Date createdTimestamp;

    @PropertyName(KEY_CREATED_BY_UID)
    private String createdByUID;

    // Used only by internal fire base methods
    public Card() {
    }

    // Standard constructor
    public Card(String title, String hexId, ArrayList<Map<String, String>> fields, String createdByUID) {
        this.title = title;
        this.hexId = hexId;
        this.fields = fields;
        this.createdByUID = createdByUID;
    }

    // Getters

    @PropertyName(KEY_TITLE)
    public String getTitle() {
        return title;
    }

    @PropertyName(KEY_FIELDS)
    public ArrayList<Map<String, String>> getFields() {
        return fields;
    }

    @PropertyName(KEY_CREATED_TIMESTAMP)
    public @ServerTimestamp
    Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    @PropertyName(KEY_CREATED_BY_UID)
    public String getCreatedByUID() {
        return createdByUID;
    }

    @PropertyName(KEY_HEX_ID)
    public String getHexId() {
        return hexId;
    }

}
