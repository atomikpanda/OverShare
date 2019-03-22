// Bailey Seymour
// DVP6 - 1903
// AccountFragment.java

package com.baileyseymour.overshare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.activities.SplashActivity;
import com.baileyseymour.overshare.utils.EditTextUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AccountFragment extends Fragment {

    public static final int RESULT_SIGN_OUT = 529;

    @BindView(R.id.editTextName)
    TextInputEditText editTextName;

    @BindView(R.id.textViewEmail)
    TextView emailTextView;

    public AccountFragment() {
        // Default constructor
    }

    // Factory method
    public static AccountFragment newInstance() {

        Bundle args = new Bundle();

        AccountFragment fragment = new AccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        // Make sure layout view has been loaded
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String name = user.getDisplayName();
            if (email != null)
                emailTextView.setText(email);

            if (name != null)
                editTextName.setText(name);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_action_save) {
            onSaveAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSaveAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        editTextName.setError(null);

        if (EditTextUtils.getString(editTextName).trim().isEmpty()) {
            editTextName.setError(getString(R.string.do_not_blank));
            return;
        }

        if (user != null) {
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(EditTextUtils.getString(editTextName)).build());
        }
        if (getActivity() != null)
            getActivity().finish();
    }

    @OnClick(R.id.buttonSignOut)
    public void onSignOut() {
        FirebaseAuth.getInstance().signOut();

        if (getActivity() != null) {
            getActivity().setResult(RESULT_SIGN_OUT);
            getActivity().finish();
        }

        Intent intent = new Intent(getContext(), SplashActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.buttonResetPassword)
    public void onResetPassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Reset password email sent!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to send reset email.", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
