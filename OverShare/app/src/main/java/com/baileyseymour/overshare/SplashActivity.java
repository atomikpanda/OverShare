package com.baileyseymour.overshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 466;

    private FirebaseAuth mAuth;

    @BindView(R.id.buttonSignIn)
    Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.buttonSignIn)
    public void signIn(View view) {
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAlwaysShowSignInMethodScreen(false)
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher_round)
                        .build(),
                RC_SIGN_IN);
    }
}
