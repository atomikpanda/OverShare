package com.baileyseymour.overshare.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class Card implements Serializable {
    @PropertyName("title")
    private String title;

    @PropertyName("hexId")
    private String hexId;

    @PropertyName("fields")
    private ArrayList<Map<String, String>> fields;

    @PropertyName("createdTimestamp")
    private @ServerTimestamp Date createdTimestamp;

    @PropertyName("createdByUID")
    private String createdByUID;

    public Card() {
    }

    public Card(String title, String hexId, ArrayList<Map<String, String>> fields, String createdByUID) {
        this.title = title;
        this.hexId = hexId;
        this.fields = fields;
        this.createdByUID = createdByUID;
    }

    @PropertyName("title")
    public String getTitle() {
        return title;
    }

    @PropertyName("fields")
    public ArrayList<Map<String, String>> getFields() {
        return fields;
    }

    @PropertyName("createdTimestamp")
    public @ServerTimestamp Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    @PropertyName("createdByUID")
    public String getCreatedByUID() {
        return createdByUID;
    }

    @PropertyName("hexId")
    public String getHexId() {
        return hexId;
    }

}
