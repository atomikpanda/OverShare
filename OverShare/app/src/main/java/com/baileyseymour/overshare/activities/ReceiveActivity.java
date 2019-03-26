// Bailey Seymour
// DVP6 - 1903
// ReceiveActivity.java

package com.baileyseymour.overshare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.fragments.ReceiveFragment;
import com.baileyseymour.overshare.utils.ThemeUtils;

import butterknife.ButterKnife;

public class ReceiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);
        ButterKnife.bind(this);

        // Keep the screen awake so that we can receive
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setTitle(R.string.receive);

        if (savedInstanceState == null) {
            // Load the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer,
                            ReceiveFragment.newInstance())
                    .commit();
        }
    }
}
