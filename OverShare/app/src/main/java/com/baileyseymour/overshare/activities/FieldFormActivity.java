// Bailey Seymour
// DVP6 - 1903
// FieldFormActivity.java

package com.baileyseymour.overshare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.fragments.FieldFormFragment;
import com.baileyseymour.overshare.models.Field;

import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_FIELD;


public class FieldFormActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);

        Field field = null;
        Intent intent = getIntent();
        int titleId = R.string.add_field;

        // Get the field extra
        if (intent != null) {
            if (intent.hasExtra(EXTRA_FIELD)) {
                titleId = R.string.edit_field;
                field = (Field) intent.getSerializableExtra(EXTRA_FIELD);
            }
        }

        setTitle(titleId);

        // Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,
                        FieldFormFragment.newInstance(field))
                .commit();
    }
}
