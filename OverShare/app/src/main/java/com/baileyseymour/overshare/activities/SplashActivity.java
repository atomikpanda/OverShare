// Bailey Seymour
// DVP6 - 1903
// SplashActivity.java

package com.baileyseymour.overshare.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.activities.MainActivity;
import com.baileyseymour.overshare.utils.ThemeUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    // Constants
    private static final int RC_SIGN_IN = 466;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            startMainAndFinish();
        }

    }

    // Starts the main activity and closes the splash
    private void startMainAndFinish() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @OnClick(R.id.buttonSignIn)
    public void signIn(View view) {
        // Allow users to sign in via email
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAlwaysShowSignInMethodScreen(false)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.ic_oversharelogo_02)
                        .setTheme(ThemeUtils.isNightModeEnabled(this)
                                ? R.style.AppThemeDark : R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                startMainAndFinish();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response != null && response.getError() != null) {
                    Toast.makeText(this, response.getError().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
