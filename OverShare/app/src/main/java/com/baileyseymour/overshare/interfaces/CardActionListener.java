// Bailey Seymour
// DVP6 - 1903
// CardActionListener.java

package com.baileyseymour.overshare.interfaces;

import com.baileyseymour.overshare.models.Card;
import com.google.firebase.firestore.DocumentSnapshot;

public interface CardActionListener {
    // Different actions that can occur on a card
    String ACTION_SHARE_CARD_CHIRP = "ACTION_SHARE_CARD_CHIRP";
    String ACTION_SHARE_CARD_DOWNLOAD = "ACTION_SHARE_CARD_DOWNLOAD";
    String ACTION_SHARE_CARD_COPY = "ACTION_SHARE_CARD_COPY";
    String ACTION_SHARE_CARD_URL = "ACTION_SHARE_CARD_URL";
    String ACTION_DELETE_CARD = "ACTION_DELETE_CARD";
    String ACTION_EDIT_CARD = "ACTION_EDIT_CARD";

    void onCardAction(String action, Card card, int position, DocumentSnapshot snapshot);
}
