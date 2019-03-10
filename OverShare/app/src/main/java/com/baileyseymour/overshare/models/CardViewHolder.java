package com.baileyseymour.overshare.models;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baileyseymour.overshare.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cardTitleTextView)
    public TextView titleTextView;

    @BindView(R.id.fieldsRecyclerView)
    public RecyclerView fieldsRecyclerView;

    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
