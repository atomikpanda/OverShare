package com.baileyseymour.overshare.interfaces;

import com.baileyseymour.overshare.models.Card;

public interface CardActionListener {
    String ACTION_SHARE_CARD = "ACTION_SHARE_CARD";
    String ACTION_DELETE_CARD = "ACTION_DELETE_CARD";
    String ACTION_EDIT_CARD = "ACTION_EDIT_CARD";

    void onCardAction(String action, Card card, int position);
}
