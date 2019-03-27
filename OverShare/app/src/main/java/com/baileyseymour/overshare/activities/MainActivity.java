// Bailey Seymour
// DVP6 - 1903
// MainActivity.java

package com.baileyseymour.overshare.activities;

import android.Manifest;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.adapters.MainFragmentPagerAdapter;
import com.baileyseymour.overshare.fragments.CardsListFragment;
import com.baileyseymour.overshare.interfaces.ChirpContainer;
import com.baileyseymour.overshare.interfaces.Constants;
import com.baileyseymour.overshare.utils.CardUtils;
import com.baileyseymour.overshare.utils.ChirpManager;
import com.baileyseymour.overshare.utils.ThemeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.chirp.connect.models.ChirpConnectState;
import io.chirp.connect.models.ChirpError;
import pl.tajchert.nammu.Nammu;

import static com.baileyseymour.overshare.fragments.AccountFragment.RESULT_SIGN_OUT;
import static com.baileyseymour.overshare.interfaces.Constants.ACTION_SHORTCUT_RECEIVE;
import static com.baileyseymour.overshare.interfaces.Constants.URI_CARD_PATH;
import static com.baileyseymour.overshare.interfaces.Constants.URI_HOST;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, CardsListFragment.FabContainer,
        ChirpContainer {

    // Views

    @BindView(R.id.mainViewPager)
    ViewPager mViewPager;

    @BindView(R.id.mainTabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ChirpManager mChirpManager;

    private static final int RC_ACCOUNT = 420;
    private static final int MATCH_CARD_URI = 73;
    private static final String TAG = "MainActivity";
    private static final String EXTRA_THEME_CHANGED = "EXTRA_THEME_CHANGED";

    private static WeakReference<MainActivity> INSTANCE;

    public static MainActivity getInstance() {
        return INSTANCE.get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        INSTANCE = new WeakReference<>(this);
        getManager();
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //FieldUtils.init(getResources());


        setSupportActionBar(mToolbar);

        initializeTabs();

        // Show a snack bar on theme changed
        if (getIntent() != null && getIntent().getBooleanExtra(EXTRA_THEME_CHANGED, false)) {
            Snackbar.make(findViewById(android.R.id.content), R.string.theme_applied, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleNightMode();
                        }
                    })
                    .show();
        }

        handleIntent();

    }

    private void handleIntent() {
        Intent intent = getIntent();

        if (intent == null) return;

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Signed out
            Intent splashIntent = new Intent(this, SplashActivity.class);
            startActivity(splashIntent);
            finish();

            Toast.makeText(this, R.string.must_sign_in, Toast.LENGTH_SHORT).show();
            return;
        }

        if (intent.getAction() != null &&
                intent.getAction().equals(ACTION_SHORTCUT_RECEIVE)) {

            // Select the received cards tab
            TabLayout.Tab tab = mTabLayout.getTabAt(1);
            if (tab != null)
                tab.select();

            // If we have permission open receive
            if (Nammu.hasPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
                Intent receiveIntent = new Intent(this, ReceiveActivity.class);
                startActivity(receiveIntent);
            } else {
                Toast.makeText(this, R.string.micro_req_shortcut, Toast.LENGTH_SHORT).show();
            }

            return;
        }

        Uri data = intent.getData();
        if (data != null) {

            UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

            // Match only our URI which is host/overshare/card/<card_id_here>
            matcher.addURI(URI_HOST, URI_CARD_PATH + "/*", MATCH_CARD_URI);

            int match = matcher.match(data);
            if (match == MATCH_CARD_URI) {

                // Extract the identifier
                String identifier = data.getLastPathSegment();
                if (identifier == null) identifier = "";

                Log.d(TAG, "handleIntent: " + identifier);

                // Validate id
                if (!identifier.trim().isEmpty()) {

                    // Save to Firebase DB
                    CardUtils.addCardToSavedCollection(identifier,
                            FirebaseFirestore.getInstance());

                    // Select the received cards tab
                    TabLayout.Tab tab = mTabLayout.getTabAt(1);
                    if (tab != null)
                        tab.select();
                }
            }
        }
    }

    @Override
    public FloatingActionButton onProvideFab() {
        return fab;
    }

    @Override
    public int getPagePosition() {
        if (mViewPager == null) return 0;
        return mViewPager.getCurrentItem();
    }

    private void initializeTabs() {
        // Setup action bar
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setElevation(0);
        }

        // Load the view pager into the tab layout
        if (mViewPager.getAdapter() == null)
            mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), this));

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.menu_action_theme).setTitle(
                getString(ThemeUtils.isNightModeEnabled(this)
                        ? R.string.disable_night_mode : R.string.enable_night_mode));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_action_account) {
            // Launch AccountActivity
            Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
            startActivityForResult(accountIntent, RC_ACCOUNT);

            return true;
        } else if (id == R.id.menu_action_about) {
            // Create the libs builder activity
            boolean nightModeEnabled = ThemeUtils.isNightModeEnabled(this);
            new LibsBuilder()
                    .withActivityStyle(nightModeEnabled
                            ? Libs.ActivityStyle.DARK : Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                    .start(MainActivity.this);
            return true;
        } else if (id == R.id.menu_action_theme) {
            // Set the night mode state and recreate the activity
            toggleNightMode();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleNightMode() {
        ThemeUtils.setNightModeEnabled(!ThemeUtils.isNightModeEnabled(this), this);
        this.setIntent(getIntent().setAction(""));
        this.getIntent().putExtra(EXTRA_THEME_CHANGED, true);
        this.recreate();
    }

    // View Pager

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        // Switch the fab icon based on the page that is selected
        if (i == 0) {
            fab.setImageResource(R.drawable.ic_add_black_24dp);
        } else if (i == 1) {
            fab.setImageResource(R.drawable.ic_receive);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Sign out to splash screen if needed
        if (resultCode == RESULT_SIGN_OUT && requestCode == RC_ACCOUNT) {
            finish();
        }
    }

    @Override
    public ChirpManager getManager() {
        if (mChirpManager == null) {
            mChirpManager = new ChirpManager(this);
        }
        return mChirpManager;
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
    protected void onPause() {
        super.onPause();

        try {
            ChirpError error = getManager().getChirpConnect().stop();

            // Note: it's ok if an error occurs here as it is common that
            // Chirp to tries to stop itself when running
            if (error.getCode() > 0) {
                Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        INSTANCE = null;

        Log.i("CDBUG", "onDestroy: MainActivity");
        if (mChirpManager != null) {
            try {
                mChirpManager.getChirpConnect().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
