// Bailey Seymour
// DVP6 - 1903
// CardFormFragment.java

package com.baileyseymour.overshare.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.activities.FieldFormActivity;
import com.baileyseymour.overshare.adapters.FieldListAdapter;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.utils.EditTextUtils;
import com.baileyseymour.overshare.utils.IdGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.codec.binary.Hex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.baileyseymour.overshare.fragments.FieldFormFragment.RESULT_DELETE_FIELD;
import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_CARDS;
import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_FIELD;
import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_INDEX;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_FIELDS;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_TITLE;
import static com.baileyseymour.overshare.interfaces.Constants.PAYLOAD_SIZE;


public class CardFormFragment extends Fragment implements AdapterView.OnItemClickListener {

    // Constants
    private static final String ARG_CARD = "ARG_CARD";
    private static final String ARG_DOC_ID = "ARG_DOC_ID";
    private static final String TAG = "CardFormFragment";
    private static final int RC_ADD_FIELD = 300;
    private static final int RC_EDIT_FIELD = 548;

    private final ArrayList<Map<String, String>> mTempEditFields = new ArrayList<>();

    // Database
    private FirebaseFirestore mDB;

    // Views

    @BindView(R.id.fieldsListView)
    ListView mFieldsListView;

    @BindView(R.id.editTextCardTitle)
    TextInputEditText mEditTextCardTitle;

    public CardFormFragment() {
        // Default constructor
    }

    // Factory method
    public static CardFormFragment newInstance(Card card, String docId) {

        Bundle args = new Bundle();

        // Store in bundle
        if (card != null)
            args.putSerializable(ARG_CARD, card);

        if (docId != null)
            args.putString(ARG_DOC_ID, docId);

        CardFormFragment fragment = new CardFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Get an instance of the database
        mDB = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_form_card, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // Get the card if any
    private Card getCard() {
        if (getArguments() == null) return null;
        return ((Card) getArguments().getSerializable(ARG_CARD));
    }

    // Card document id
    private String getDocumentId() {
        if (getArguments() == null) return null;

        if (getArguments().containsKey(ARG_DOC_ID))
            return getArguments().getString(ARG_DOC_ID);
        else
            return null;
    }

    private ArrayList<Map<String, String>> getEditedFields() {
        if (getCard() != null)
            return getCard().getFields();

        return mTempEditFields;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu actions
        if (item.getItemId() == R.id.menu_action_save) {
            onSaveTapped();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Make sure layout view has been loaded
        if (getView() == null) return;

        setHasOptionsMenu(true);

        mFieldsListView.setEmptyView(getView().findViewById(android.R.id.empty));
        mFieldsListView.setOnItemClickListener(this);
        refreshFields();

        if (getCard() != null) {
            // Editing a card
            mEditTextCardTitle.setText(getCard().getTitle());
            mFieldsListView.requestFocus();
        }
    }

    @OnClick(R.id.buttonAddField)
    public void onAddField() {
        if (getContext() == null) return;

        // Handle add field
        Intent addFieldIntent = new Intent(getContext(), FieldFormActivity.class);
        addFieldIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(addFieldIntent, RC_ADD_FIELD);
    }

    private void refreshFields() {
        if (getContext() == null || getEditedFields() == null) return;
        mFieldsListView.setAdapter(
                new FieldListAdapter(getContext(), Field.fromListOfMaps(getEditedFields())));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (getContext() == null) return;

        if ((requestCode == RC_ADD_FIELD || requestCode == RC_EDIT_FIELD) && resultCode == RESULT_OK) {

            // Get field extra
            Serializable potentialField = data.getSerializableExtra(EXTRA_FIELD);

            if (potentialField instanceof Field) {

                // Got the field
                Field field = ((Field) potentialField);

                if (getArguments() != null && getEditedFields() != null) {

                    if (requestCode == RC_ADD_FIELD) {
                        // Add the new field
                        getEditedFields().add(field.toMap());
                    } else {
                        // Edit field save by overriding by index
                        int pos = data.getIntExtra(EXTRA_INDEX, -1);
                        if (pos > -1 && pos < getEditedFields().size()) {
                            getEditedFields().set(pos, field.toMap());
                        }
                    }

                    // Refresh current fields list view
                    refreshFields();
                }
            }
        } else if (requestCode == RC_EDIT_FIELD && resultCode == RESULT_DELETE_FIELD) {
            // Handle deleting a field

            int pos = data.getIntExtra(EXTRA_INDEX, -1);

            if (pos > -1 && pos < getEditedFields().size()) {
                // Delete the field at the specified index
                getEditedFields().remove(pos);

                // Refresh current fields list view
                refreshFields();
            }
        }
    }

    private void onSaveTapped() {

        mEditTextCardTitle.setError(null);

        String cardTitle = EditTextUtils.getString(mEditTextCardTitle).trim();

        // Validate fields
        if (cardTitle.trim().isEmpty()) {
            mEditTextCardTitle.setError(getString(R.string.do_not_blank));
            return;
        }

        // Proceed with saving

        if (getCard() != null) {
            handleSaveEdit();
            return;
        }

        // Get the current user id
        String createdByUID = FirebaseAuth.getInstance().getUid();

        // Generate a random byte array to be saved for use with Chirp
        byte[] payload = IdGenerator.randomBytes(PAYLOAD_SIZE);

        // Create a card
        final Card card = new Card(cardTitle, new String(Hex.encodeHex(payload)), getEditedFields(), createdByUID);

        // Create a new document for the created card
        mDB.collection(COLLECTION_CARDS).document()
                .set(card)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        if (getActivity() != null)
            getActivity().finish();
    }

    private void handleSaveEdit() {
        if (getDocumentId() == null) return;

        // NOTE: DO NOT set the Card only update the specific properties
        String cardTitle = EditTextUtils.getString(mEditTextCardTitle);
        Map<String, Object> updates = new HashMap<>();

        // Update the card's title
        updates.put(KEY_TITLE, cardTitle.trim());

        // and the fields property using a map and firebase
        if (getEditedFields() != null)
            updates.put(KEY_FIELDS, getEditedFields());

        mDB.collection(COLLECTION_CARDS).document(getDocumentId()).update(updates);

        if (getActivity() != null)
            getActivity().finish();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // When a field is tapped to be edited
        Intent editFieldIntent = new Intent(getContext(), FieldFormActivity.class);
        Field fieldSelected = ((Field) parent.getAdapter().getItem(position));

        if (fieldSelected == null) return;

        editFieldIntent.putExtra(EXTRA_FIELD, fieldSelected);
        editFieldIntent.putExtra(EXTRA_INDEX, position);
        startActivityForResult(editFieldIntent, RC_EDIT_FIELD);
    }
}
