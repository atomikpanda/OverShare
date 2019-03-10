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
import com.baileyseymour.overshare.models.CardViewHolder;
import com.baileyseymour.overshare.models.Field;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CardAdapter extends FirestoreRecyclerAdapter<Card, CardViewHolder> implements CardActionListener {

    private final Context mContext;
    private FieldClickListener mClickListener;
    private CardActionListener mActionListener;
    private RecyclerEmptyStateListener mEmptyStateListener;
    private boolean mIsReceivedCards;

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
        holder.titleTextView.setText(model.getTitle());

        FieldAdapter adapter = new FieldAdapter(model, mClickListener);
        holder.fieldsRecyclerView.setAdapter(adapter);
        holder.fieldsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        holder.fieldsRecyclerView.setLayoutManager(layoutManager);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_layout, viewGroup, false);
        //view.setOnClickListener(mOnClickListener);
        return new CardViewHolder(view, this, mIsReceivedCards);
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (mEmptyStateListener != null) {
            mEmptyStateListener.onUpdateEmptyState(getItemCount() > 0);
        }
    }

    @Override
    public void onCardAction(String action, Card card, int position) {
        if (mActionListener != null) {
            mActionListener.onCardAction(action, getItem(position), position);
        }
    }
}
