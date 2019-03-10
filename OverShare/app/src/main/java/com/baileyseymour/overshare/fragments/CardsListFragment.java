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
import com.baileyseymour.overshare.interfaces.FieldClickListener;
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


public class CardsListFragment extends Fragment implements FieldClickListener {

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
            query = mDB.collection(COLLECTION_CARDS)
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("createdByUID", uid);
        } else if (uid != null) {
            query = mDB.collection(COLLECTION_SAVED)
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    .whereEqualTo("savedByUID", uid);
        }

        FirestoreRecyclerOptions.Builder<Card> builder = new FirestoreRecyclerOptions.Builder<>();

        if (query != null)
            builder.setQuery(query, Card.class);

        FirestoreRecyclerOptions<Card> options = builder.build();

        mCardAdapter = new CardAdapter(getContext(), options, this);

        if (getView() != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mCardAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mCardAdapter);
        }

    }

    private boolean getIsReceivedCards() {
        if (getArguments() != null) {
            return getArguments().getBoolean(ARG_IS_RECEIVED, false);
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCardAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCardAdapter.stopListening();
    }

//    @Override
////    public void onClick(View v) {
////        if (getView() == null) return;
////
////        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
////        if (recyclerView != null) {
////            CardViewHolder viewHolder = (CardViewHolder) recyclerView.getChildViewHolder(v);
////            int position = viewHolder.getAdapterPosition();
////
////            Card selected = mCardAdapter.getItem(position);
////            Toast.makeText(getContext(), selected.getTitle(), Toast.LENGTH_SHORT).show();
////        }
////    }

    @Override
    public void onFieldClicked(Card card, Field field, int fieldPosition) {
        Toast.makeText(getContext(), field.getValue(), Toast.LENGTH_SHORT).show();
    }
}
