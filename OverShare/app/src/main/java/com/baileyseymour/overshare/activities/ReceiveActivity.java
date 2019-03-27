// Bailey Seymour
// DVP6 - 1903
// ReceiveActivity.java

package com.baileyseymour.overshare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.fragments.ReceiveFragment;
import com.baileyseymour.overshare.interfaces.ChirpContainer;
import com.baileyseymour.overshare.utils.ChirpManager;
import com.baileyseymour.overshare.utils.ThemeUtils;

import butterknife.ButterKnife;
import io.chirp.connect.models.ChirpConnectState;
import io.chirp.connect.models.ChirpError;

public class ReceiveActivity extends AppCompatActivity implements ChirpContainer {

    private ChirpManager mChirpManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);
        ButterKnife.bind(this);

        mChirpManager = MainActivity.getInstance().getManager();

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

    @Override
    public ChirpManager getManager() {
        return mChirpManager;
    }

    @Override
    protected void onPause() {
        super.onPause();

//        ChirpError error = getManager().getChirpConnect().stop();
//        // Note: it's ok if an error occurs here as it is common that
//        // Chirp to tries to stop itself when running
//        if (error.getCode() > 0) {
//            Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ChirpConnectState state = getManager().getChirpConnect().getState();
        if (state == ChirpConnectState.CHIRP_CONNECT_STATE_STOPPED || state == ChirpConnectState.CHIRP_CONNECT_STATE_NOT_CREATED) {
            ChirpError error = getManager().getChirpConnect().start();
            if (error.getCode() > 0) {
                Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("CDBUG", "onDestroy: ReceiveActivity");
    }
}
