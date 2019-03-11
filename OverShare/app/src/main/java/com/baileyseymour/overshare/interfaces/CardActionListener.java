// Bailey Seymour
// DVP6 - 1903
// CardActionListener.java

package com.baileyseymour.overshare.interfaces;

import com.baileyseymour.overshare.models.Card;

public interface CardActionListener {
    // Different actions that can occur on a card
    String ACTION_SHARE_CARD = "ACTION_SHARE_CARD";
    String ACTION_DELETE_CARD = "ACTION_DELETE_CARD";
    String ACTION_EDIT_CARD = "ACTION_EDIT_CARD";

    void onCardAction(String action, Card card, int position);
}
