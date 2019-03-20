// Bailey Seymour
// DVP6 - 1903
// FieldViewHolder.java

package com.baileyseymour.overshare.adapters.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baileyseymour.overshare.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FieldViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    // Use to handle clicked field events
    public interface ClickListener {
        void onItemClick(int position, View v);
        void onLongClick(int position, View v);
    }

    // Views

    @BindView(R.id.fieldTitleTextView)
    public TextView titleTextView;

    @BindView(R.id.fieldValueTextView)
    public TextView valueTextView;

    // Listener

    private final ClickListener mClickListener;

    public FieldViewHolder(@NonNull View itemView, ClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mClickListener = clickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    // When a field is clicked notify our listener / adapter
    @Override
    public void onClick(View v) {
        mClickListener.onItemClick(getAdapterPosition(), v);
    }

    @Override
    public boolean onLongClick(View v) {
        mClickListener.onLongClick(getAdapterPosition(), v);
        return true;
    }
}
