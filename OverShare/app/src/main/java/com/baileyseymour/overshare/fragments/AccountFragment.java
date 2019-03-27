// Bailey Seymour
// DVP6 - 1903
// AccountFragment.java

package com.baileyseymour.overshare.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    // Views

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

        // Load user data
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

        // Validate
        if (EditTextUtils.getString(editTextName).trim().isEmpty()) {
            editTextName.setError(getString(R.string.do_not_blank));
            return;
        }

        // Update the user name
        if (user != null) {
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(EditTextUtils.getString(editTextName)).build());
        }

        Toast.makeText(getContext(), R.string.account_info_saved, Toast.LENGTH_SHORT).show();

        // Close
        if (getActivity() != null)
            getActivity().finish();
    }

    @OnClick(R.id.buttonSignOut)
    public void onSignOut() {
        if (getContext() == null) return;

        // Confirm before signing out
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.sign_out_q)
                .setMessage(R.string.sign_out_dialog_msg)
                .setNegativeButton(R.string.fui_cancel, null)
                .setPositiveButton(R.string.sign_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Sign out from firebase
                        FirebaseAuth.getInstance().signOut();

                        if (getActivity() != null) {
                            // Tell MainActivity to finish
                            getActivity().setResult(RESULT_SIGN_OUT);

                            // Tell AccountActivity to finish
                            getActivity().finish();
                        }

                        // Start the splash screen activity
                        Intent intent = new Intent(getContext(), SplashActivity.class);
                        startActivity(intent);
                    }
                })
                .show();

    }

    @OnClick(R.id.buttonResetPassword)
    public void onResetPassword() {

        if (getContext() == null) return;

        // Get the user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.reset_password_q)
                .setMessage(R.string.reset_password_dialog_msg)
                .setNegativeButton(R.string.fui_cancel, null)
                .setPositiveButton(R.string.reset_password, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Try to send the email
                        FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), R.string.reset_pw_sent, Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), R.string.reset_pw_fail, Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                })
                .show();


    }
}
