// Bailey Seymour
// DVP6 - 1903
// CardFormActivity.java

package com.baileyseymour.overshare.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.fragments.CardFormFragment;
import com.baileyseymour.overshare.models.Card;

import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_CARD;

public class CardFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        Card card = null;
        Intent intent = getIntent();
        int titleId = R.string.create_card;

        // Get the card extra
        if (intent != null) {
            if (intent.hasExtra(EXTRA_CARD)) {
                titleId = R.string.edit_card;
                card = (Card) intent.getSerializableExtra(EXTRA_CARD);
            }
        }

        setTitle(titleId);

        // Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,
                        CardFormFragment.newInstance(card))
                .commit();
    }
}
