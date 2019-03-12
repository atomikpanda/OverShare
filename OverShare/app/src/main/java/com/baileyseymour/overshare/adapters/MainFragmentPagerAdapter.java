// Bailey Seymour
// DVP6 - 1903
// MainFragmentPagerAdapter.java

package com.baileyseymour.overshare.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.fragments.CardsListFragment;


public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    // We have two tabs
    private static final int COUNT = 2;
    private final String[] tabTitles;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        // Get the tab titles from the strings
        tabTitles = context.getResources().getStringArray(R.array.tab_titles);
    }

    @Override
    public Fragment getItem(int i) {
        // Load the fragment based on the tab
        if (i == 0) {
            return CardsListFragment.newInstance(false);
        } else if (i == 1) {
            return CardsListFragment.newInstance(true);
        }
        return null;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        // Validate
        if (position < tabTitles.length && position != -1)
            return tabTitles[position];

        return super.getPageTitle(position);
    }
}
