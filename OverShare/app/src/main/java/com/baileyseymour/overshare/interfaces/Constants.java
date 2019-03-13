// Bailey Seymour
// DVP6 - 1903
// Constants.java

package com.baileyseymour.overshare.interfaces;

// Constants used app-wide
public interface Constants {

    // Extras
    String EXTRA_CARD = "com.baileyseymour.overshare.EXTRA_CARD";
    String EXTRA_CARD_DOC_ID = "com.baileyseymour.overshare.EXTRA_CARD_DOC_ID";
    String EXTRA_FIELD = "com.baileyseymour.overshare.EXTRA_FIELD";

    // multiply this by x2 to find the total hex string length
    int PAYLOAD_SIZE = 6;

    String COLLECTION_SAVED = "saved_cards";
    String COLLECTION_CARDS = "cards";

    // Database keys
    String KEY_TITLE = "title";
    String KEY_VALUE = "value";
    String KEY_TYPE = "type";

    String KEY_HEX_ID = "hexId";
    String KEY_FIELDS = "fields";
    String KEY_CREATED_TIMESTAMP = "createdTimestamp";
    String KEY_CREATED_BY_UID = "createdByUID";
    String KEY_SAVED_BY_UID = "savedByUID";

}
