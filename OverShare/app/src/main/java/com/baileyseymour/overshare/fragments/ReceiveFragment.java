// Bailey Seymour
// DVP6 - 1903
// ReceiveFragment.java

package com.baileyseymour.overshare.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baileyseymour.overshare.R;

import butterknife.ButterKnife;


public class ReceiveFragment extends Fragment {

    /*
    class State {
        // The description to be displayed on the label
        public String description;

        // Primary icon
        @DrawableRes
        int icon;

        // Show action button
        boolean showButton;

        // The id
        int id;
    }
    */

    public ReceiveFragment() {
        // Default constructor
    }

    // Factory method
    public static ReceiveFragment newInstance() {

        Bundle args = new Bundle();

        ReceiveFragment fragment = new ReceiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_receive, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
