// Bailey Seymour
// DVP6 - 1903
// CardViewHolder.java

package com.baileyseymour.overshare.adapters.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.interfaces.CardActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CardViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

    // Views
    @BindView(R.id.cardTitleTextView)
    public TextView titleTextView;

    @BindView(R.id.fieldsRecyclerView)
    public RecyclerView fieldsRecyclerView;

    private static final String TAG = "CardViewHolder";

    // Action Listener
    private final CardActionListener mListener;

    public CardViewHolder(@NonNull View itemView, CardActionListener listener, boolean isReceivedCards) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mListener = listener;

        Log.d(TAG, "CardViewHolder: isReceivedCards: " + isReceivedCards);

        // Only show edit button on cards owned by the current user
//        if (isReceivedCards) {
//            itemView.findViewById(R.id.editCardButton).setVisibility(View.GONE);
//        } else {
//            itemView.findViewById(R.id.editCardButton).setVisibility(View.VISIBLE);
//        }

    }

    // Action Button Click listeners

    @OnClick(R.id.shareCardButton)
    public void shareTapped(View button) {

        PopupMenu popup = new PopupMenu(itemView.getContext(), button, Gravity.TOP);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_share);
        popup.show();
    }

    @OnClick(R.id.deleteCardButton)
    public void deleteTapped() {

        if (mListener != null)
            mListener.onCardAction(CardActionListener.ACTION_DELETE_CARD, null, getAdapterPosition(), null);
    }

    @OnClick(R.id.editCardButton)
    public void editTapped() {

        if (mListener != null)
            mListener.onCardAction(CardActionListener.ACTION_EDIT_CARD, null, getAdapterPosition(), null);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (mListener != null) {
            String action = CardActionListener.ACTION_SHARE_CARD_CHIRP;

            // Handle different share actions
            if (item.getItemId() == R.id.menu_action_share_download) {
                action = CardActionListener.ACTION_SHARE_CARD_DOWNLOAD;
            } else if (item.getItemId() == R.id.menu_action_share_copy) {
                action = CardActionListener.ACTION_SHARE_CARD_COPY;
            } else if (item.getItemId() == R.id.menu_action_share_url) {
                action = CardActionListener.ACTION_SHARE_CARD_URL;
            }

            mListener.onCardAction(action, null, getAdapterPosition(), null);
        }

        return false;
    }
}
