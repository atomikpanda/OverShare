// Bailey Seymour
// DVP6 - 1903
// FieldClickListener.java

package com.baileyseymour.overshare.interfaces;

import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.Field;

public interface FieldClickListener {
    // Runs when a field was clicked on and should be handled to open a link
    void onFieldClicked(Card card, Field field, int fieldPosition);
}
