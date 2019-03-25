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
import com.baileyseymour.overshare.utils.ThemeUtils;

import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_CARD;
import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_CARD_DOC_ID;

public class CardFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        Card card = null;
        Intent intent = getIntent();
        int titleId = R.string.create_card;
        String docId = null;

        // Get the card extra
        if (intent != null) {
            if (intent.hasExtra(EXTRA_CARD)) {
                titleId = R.string.edit_card;
                card = (Card) intent.getSerializableExtra(EXTRA_CARD);
                docId = intent.getStringExtra(EXTRA_CARD_DOC_ID);
            }
        }

        // Set the action bar title
        setTitle(titleId);

        // Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,
                        CardFormFragment.newInstance(card, docId))
                .commit();
    }
}
