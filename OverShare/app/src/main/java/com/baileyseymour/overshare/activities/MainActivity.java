// Bailey Seymour
// DVP6 - 1903
// MainActivity.java

package com.baileyseymour.overshare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.adapters.MainFragmentPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    // Views

    @BindView(R.id.mainViewPager)
    ViewPager mViewPager;

    @BindView(R.id.mainTabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.fab)
    protected FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeTabs();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mViewPager.getCurrentItem();
                if (position == 0) {
                    // We are on the my cards tab, so FAB is add card
                    Intent addCardIntent = new Intent(MainActivity.this, CardFormActivity.class);
                    startActivity(addCardIntent);

                } else if (position == 1) {
                    // TODO: We are on the receive cards tab, so FAB is receive
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(MainActivity.this, "TODO: Milestone 2: Receive", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initializeTabs() {
        // Setup action bar
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setElevation(0);
        }

        // Load the view pager into the tab layout
        mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), this));
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_action_account) {
            Toast.makeText(MainActivity.this, "TODO: Milestone 3: Account", Toast.LENGTH_SHORT).show();
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
}
