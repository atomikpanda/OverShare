// Bailey Seymour
// DVP6 - 1903
// CardsListFragment.java

package com.baileyseymour.overshare.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.adapters.CardAdapter;
import com.baileyseymour.overshare.interfaces.CardActionListener;
import com.baileyseymour.overshare.interfaces.FieldClickListener;
import com.baileyseymour.overshare.interfaces.RecyclerEmptyStateListener;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.CardViewHolder;
import com.baileyseymour.overshare.models.Field;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_CARDS;
import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_SAVED;


public class CardsListFragment extends Fragment implements FieldClickListener, CardActionListener, RecyclerEmptyStateListener {

    private static final String ARG_IS_RECEIVED = "ARG_IS_RECEIVED";

    private FirebaseFirestore mDB;
    private FirestoreRecyclerAdapter<Card, CardViewHolder> mCardAdapter;

    public CardsListFragment() {
        // Default constructor
    }

    // Factory method
    public static CardsListFragment newInstance(boolean isReceivedCards) {

        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_RECEIVED, isReceivedCards);
        CardsListFragment fragment = new CardsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Load database
        mDB = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        return inflater.inflate(R.layout.fragment_list_cards, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String uid = FirebaseAuth.getInstance().getUid();

        Query query = null;

        if (!getIsReceivedCards() && uid != null) {
            // Query for only cards created by this user
            query = mDB.collection(COLLECTION_CARDS)
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("createdByUID", uid);
        } else if (uid != null) {
            // Query for only cards SAVED by this user
            query = mDB.collection(COLLECTION_SAVED)
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("savedByUID", uid);
        }

        // Create the fire store options builder
        FirestoreRecyclerOptions.Builder<Card> builder = new FirestoreRecyclerOptions.Builder<>();

        // Set the query
        if (query != null)
            builder.setQuery(query, Card.class);

        // Build options
        FirestoreRecyclerOptions<Card> options = builder.build();

        // Initialize the card adapter
        mCardAdapter = new CardAdapter(getContext(), options, this, this, this, getIsReceivedCards());

        // Setup recycler view to use our fire store adapter
        if (getView() != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mCardAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mCardAdapter);
        }


    }

    // Is this fragment supposed to display the cards that were received vs created
    private boolean getIsReceivedCards() {
        if (getArguments() != null) {
            return getArguments().getBoolean(ARG_IS_RECEIVED, false);
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Tell our adapter to listen for db changes
        if (mCardAdapter != null)
            mCardAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop listening
        if (mCardAdapter != null)
            mCardAdapter.stopListening();
    }

    // Field click listener

    @Override
    public void onFieldClicked(Card card, Field field, int fieldPosition) {
        Toast.makeText(getContext(), field.getValue(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateEmptyState(boolean hasData) {
        // Handle showing/hiding the recycler view and empty state based
        // on if any data exists
        if (getView() != null) {
            View emptyView = getView().findViewById(android.R.id.empty);
            View recyclerView = getView().findViewById(R.id.recyclerView);

            // Toggle view visibility
            if (emptyView != null)
                emptyView.setVisibility(hasData ? View.GONE : View.VISIBLE);
            if (recyclerView != null)
                recyclerView.setVisibility(hasData ? View.VISIBLE : View.GONE);
        }
    }

    // CardActionListener

    @Override
    public void onCardAction(String action, Card card, int position) {
        String todo = "TODO: Milestone 2: ";

        // Handle card actions
        switch (action) {
            case ACTION_SHARE_CARD:
                todo += "Share: ";
                break;
            case ACTION_DELETE_CARD:
                todo += "Delete: ";
                break;
            case ACTION_EDIT_CARD:
                todo += "Edit: ";
                break;
        }

        Toast.makeText(getContext(), todo + card.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
