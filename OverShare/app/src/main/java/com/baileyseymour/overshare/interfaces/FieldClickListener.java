package com.baileyseymour.overshare.interfaces;

import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.Field;

public interface FieldClickListener {
    void onFieldClicked(Card card, Field field, int fieldPosition);
}
