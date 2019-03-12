// Bailey Seymour
// DVP6 - 1903
// ReceiveActivity.java

package com.baileyseymour.overshare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.fragments.ReceiveFragment;

import butterknife.ButterKnife;

public class ReceiveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);
        ButterKnife.bind(this);

        // Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer,
                        ReceiveFragment.newInstance())
                .commit();
    }
}
