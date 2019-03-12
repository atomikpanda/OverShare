// Bailey Seymour
// DVP6 - 1903
// CardAdapter.java

package com.baileyseymour.overshare.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.interfaces.CardActionListener;
import com.baileyseymour.overshare.interfaces.FieldClickListener;
import com.baileyseymour.overshare.interfaces.RecyclerEmptyStateListener;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.adapters.viewholders.CardViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CardAdapter extends FirestoreRecyclerAdapter<Card, CardViewHolder> implements CardActionListener {

    // Instance vars
    private final Context mContext;
    private final FieldClickListener mClickListener;
    private final CardActionListener mActionListener;
    private final RecyclerEmptyStateListener mEmptyStateListener;
    private final boolean mIsReceivedCards;

    // Standard constructor
    public CardAdapter(Context context, @NonNull FirestoreRecyclerOptions<Card> options,
                       FieldClickListener fieldClickListener, CardActionListener actionListener,
                       RecyclerEmptyStateListener emptyStateListener, boolean isReceivedCards) {
        super(options);
        mContext = context;
        mClickListener = fieldClickListener;
        mActionListener = actionListener;
        mEmptyStateListener = emptyStateListener;
        mIsReceivedCards = isReceivedCards;
    }

    @Override
    protected void onBindViewHolder(@NonNull CardViewHolder holder, int position, @NonNull Card model) {
        // Set the card's title
        holder.titleTextView.setText(model.getTitle());

        // Create the nested adapter for fields
        FieldAdapter adapter = new FieldAdapter(model, mClickListener);
        holder.fieldsRecyclerView.setAdapter(adapter);
        holder.fieldsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        holder.fieldsRecyclerView.setLayoutManager(layoutManager);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Inflate the card layout
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_layout, viewGroup, false);
        return new CardViewHolder(view, this, mIsReceivedCards);
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        // When data changes notify the empty state listener
        if (mEmptyStateListener != null) {
            mEmptyStateListener.onUpdateEmptyState(getItemCount() > 0);
        }
    }

    @Override
    public void onCardAction(String action, Card card, int position) {

        // Pass the card action up to the actual CardActionListener
        if (mActionListener != null) {
            mActionListener.onCardAction(action, getItem(position), position);
        }
    }
}
