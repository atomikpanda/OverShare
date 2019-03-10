// Bailey Seymour
// DVP6 - 1903
// FieldViewHolder.java

package com.baileyseymour.overshare.models;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.interfaces.FieldHolderClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FieldViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.fieldTitleTextView)
    public TextView titleTextView;

    @BindView(R.id.fieldValueTextView)
    public TextView valueTextView;

    private FieldHolderClickListener mClickListener;

    public FieldViewHolder(@NonNull View itemView, FieldHolderClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mClickListener = clickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mClickListener.onItemClick(getAdapterPosition(), v);
    }
}
