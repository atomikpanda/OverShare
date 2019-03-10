// Bailey Seymour
// DVP6 - 1903
// CardFormFragment.java

package com.baileyseymour.overshare.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.utils.IdGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firestore.v1.DocumentTransform;

import org.apache.commons.codec.binary.Hex;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_CARDS;
import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_SAVED;
import static com.baileyseymour.overshare.interfaces.Constants.PAYLOAD_SIZE;


public class CardFormFragment extends Fragment {

    private static final String ARG_CARD = "ARG_CARD";
    private static final String TAG = "CardFormFragment";

    private FirebaseFirestore mDB;

    @BindView(R.id.fieldsListView)
    ListView mFieldsListView;

    @BindView(R.id.editTextCardTitle)
    EditText mEditTextCardTitle;

    public CardFormFragment() {
        // Default constructor
    }

    // Factory method
    public static CardFormFragment newInstance(Card card) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD, card);
        CardFormFragment fragment = new CardFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    private Card getCard() {
        if (getArguments() == null) return null;
        return ((Card) getArguments().getSerializable(ARG_CARD));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    }

    @OnClick(R.id.buttonAddField)
    public void onAddField() {
        // Handle add field
        Toast.makeText(getContext(), "TODO: Milestone 2", Toast.LENGTH_SHORT).show();
    }

    private void onSaveTapped() {
        mEditTextCardTitle.setError(null);

        String cardTitle = mEditTextCardTitle.getText().toString();

        // Validate fields
        if (cardTitle.trim().isEmpty()) {
            mEditTextCardTitle.setError(getString(R.string.do_not_blank));
            return;
        }

        // Proceed with saving

        String createdByUID = FirebaseAuth.getInstance().getUid();
        byte[] payload = IdGenerator.randomBytes(PAYLOAD_SIZE);

        final Card card = new Card(cardTitle, new String(Hex.encodeHex(payload)), null, createdByUID);

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

    // This method reverse looks up a card by its chirp id
    // Then creates a new document in saved_cards
    // with the added key of "savedByUID" to associate it with the current user's account under
    // received cards
    private void onGotChirpHexId(String chirpHexTestId) {

        final String savedByUID = FirebaseAuth.getInstance().getUid();
        if (savedByUID == null) return;

        // Query for a card matching the hex id given
        Query query = mDB.collection(COLLECTION_CARDS)
                .whereEqualTo("hexId", chirpHexTestId)
                .orderBy("createdTimestamp", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Get documents
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();

                            if (documents.size() > 0) {
                                // Get the first matching card
                                DocumentSnapshot snapshot = documents.get(0);
                                Map<String, Object> map = snapshot.getData();
                                if (map != null) {
                                    // Add the current UID as the saving account
                                    map.put("savedByUID", savedByUID);

                                    // Make the createdTimestamp reflect the new saved date
                                    map.put("createdTimestamp", FieldValue.serverTimestamp());
                                    mDB.collection(COLLECTION_SAVED)
                                            .document()
                                            .set(map);
                                }

                            }
                        }
                    }
                });
    }
}
