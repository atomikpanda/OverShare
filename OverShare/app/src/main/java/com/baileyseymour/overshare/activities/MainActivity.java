// Bailey Seymour
// DVP6 - 1903
// MainActivity.java

package com.baileyseymour.overshare.activities;

import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.adapters.MainFragmentPagerAdapter;
import com.baileyseymour.overshare.fragments.CardsListFragment;
import com.baileyseymour.overshare.fragments.ReceiveFragment;
import com.baileyseymour.overshare.utils.CardUtils;
import com.baileyseymour.overshare.utils.FieldUtils;
import com.baileyseymour.overshare.utils.ThemeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.baileyseymour.overshare.fragments.AccountFragment.RESULT_SIGN_OUT;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, CardsListFragment.FabContainer {

    // Views

    @BindView(R.id.mainViewPager)
    ViewPager mViewPager;

    @BindView(R.id.mainTabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private static final int RC_ACCOUNT = 420;
    private static final int MATCH_CARD_URI = 73;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FieldUtils.init(getResources());

        setSupportActionBar(mToolbar);

        initializeTabs();

        handleIntent();


    }

    private void handleIntent() {
        Intent intent = getIntent();

        Uri data = intent.getData();
        if (data != null) {
            UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            matcher.addURI("baileyseymour.com", "overshare/card/*", MATCH_CARD_URI);
            int match = matcher.match(data);
            if (match == MATCH_CARD_URI) {
                String identifier = data.getLastPathSegment();
                if (identifier == null) identifier = "";

                Log.i(TAG, "handleIntent: " + identifier);

                if (!identifier.trim().isEmpty()) {

                    CardUtils.onReceivedChirpHexId(identifier,
                            FirebaseFirestore.getInstance());
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
            new LibsBuilder()
                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                    .start(MainActivity.this);
            return true;
        } else if (id == R.id.menu_action_theme) {
            ThemeUtils.setNightModeEnabled(!ThemeUtils.isNightModeEnabled(this), this);
            this.recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        if (resultCode == RESULT_SIGN_OUT && requestCode == RC_ACCOUNT) {
            finish();
        }
    }
}
