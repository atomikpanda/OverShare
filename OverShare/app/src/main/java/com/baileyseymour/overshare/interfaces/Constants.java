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
    String EXTRA_INDEX = "com.baileyseymour.overshare.EXTRA_INDEX";

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

    // Chirp

    String CHIRP_APP_KEY = "D18A1E9Dbe052D29b2D3A4cDe";
    String CHIRP_APP_SECRET = "Ede5ea6C69611b156EB1AD8Eac2162e01179C06DE7B1463cD5";
    @SuppressWarnings("SpellCheckingInspection")
    String CHIRP_APP_CONFIG = "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";

}
