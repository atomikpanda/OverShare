// Bailey Seymour
// DVP6 - 1903
// CardViewHolder.java

package com.baileyseymour.overshare.adapters.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.interfaces.CardActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CardViewHolder extends RecyclerView.ViewHolder {

    // Views
    @BindView(R.id.cardTitleTextView)
    public TextView titleTextView;

    @BindView(R.id.fieldsRecyclerView)
    public RecyclerView fieldsRecyclerView;

    // Action Listener
    private final CardActionListener mListener;

    public CardViewHolder(@NonNull View itemView, CardActionListener listener, boolean isReceivedCards) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mListener = listener;

        // Only show edit button on cards owned by the current user
        if (isReceivedCards) {
            itemView.findViewById(R.id.editCardButton).setVisibility(View.GONE);
        } else {
            itemView.findViewById(R.id.editCardButton).setVisibility(View.VISIBLE);
        }
    }

    // Action Button Click listeners

    @OnClick(R.id.shareCardButton)
    public void shareTapped() {

        if (mListener != null)
            mListener.onCardAction(CardActionListener.ACTION_SHARE_CARD, null, getAdapterPosition(), null);
    }

    @OnClick(R.id.deleteCardButton)
    public void deleteTapped() {

        if (mListener != null)
            mListener.onCardAction(CardActionListener.ACTION_DELETE_CARD, null, getAdapterPosition(), null);
    }

    @OnClick(R.id.editCardButton)
    public void editTapped() {

        if (mListener != null)
            mListener.onCardAction(CardActionListener.ACTION_EDIT_CARD,null, getAdapterPosition(), null);
    }
}