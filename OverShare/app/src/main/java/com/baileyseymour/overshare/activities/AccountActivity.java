// Bailey Seymour
// DVP6 - 1903
// AccountActivity.java

package com.baileyseymour.overshare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.fragments.AccountFragment;

import butterknife.ButterKnife;


public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer,
                            AccountFragment.newInstance())
                    .commit();
        }
    }
}
