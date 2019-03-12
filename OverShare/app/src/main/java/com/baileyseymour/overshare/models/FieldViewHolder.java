// Bailey Seymour
// DVP6 - 1903
// FieldViewHolder.java

package com.baileyseymour.overshare.models;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baileyseymour.overshare.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FieldViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    // Views

    @BindView(R.id.fieldTitleTextView)
    public TextView titleTextView;

    @BindView(R.id.fieldValueTextView)
    public TextView valueTextView;

    // Listener

    private ClickListener mClickListener;

    public FieldViewHolder(@NonNull View itemView, ClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mClickListener = clickListener;
        itemView.setOnClickListener(this);
    }

    // When a field is clicked notify our listener / adapter
    @Override
    public void onClick(View v) {
        mClickListener.onItemClick(getAdapterPosition(), v);
    }
}
