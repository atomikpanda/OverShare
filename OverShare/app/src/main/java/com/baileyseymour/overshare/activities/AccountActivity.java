// Bailey Seymour
// DVP6 - 1903
// AccountActivity.java

package com.baileyseymour.overshare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.fragments.AccountFragment;
import com.baileyseymour.overshare.utils.ThemeUtils;

import butterknife.ButterKnife;


public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);
        ButterKnife.bind(this);

        setTitle(R.string.account);

        // Load fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer,
                            AccountFragment.newInstance())
                    .commit();
        }
    }
}
