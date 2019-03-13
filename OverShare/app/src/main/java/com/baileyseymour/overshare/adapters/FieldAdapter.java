// Bailey Seymour
// DVP6 - 1903
// FieldAdapter.java

package com.baileyseymour.overshare.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.interfaces.FieldClickListener;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.adapters.viewholders.FieldViewHolder;

import java.util.ArrayList;


public class FieldAdapter extends RecyclerView.Adapter<FieldViewHolder> implements FieldViewHolder.ClickListener {

    // Instance vars
    private final Card mCard;
    private final ArrayList<Field> mFields;
    private final FieldClickListener mFieldClickListener;

    // Standard constructors
    FieldAdapter(Card card, FieldClickListener fieldClickListener) {
        mCard = card;

        // Convert the card's list of maps to actual field objects
        mFields = Field.fromListOfMaps(card.getFields());
        mFieldClickListener = fieldClickListener;
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Inflate view
        View view = LayoutInflater.from(viewGroup.getContext()).inflate
                (R.layout.field_view_layout, viewGroup, false);
        return new FieldViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder viewHolder, int i) {
        // Load the field's properties into the UI
        Field field = mFields.get(i);
        viewHolder.titleTextView.setText(field.getTitle());
        viewHolder.valueTextView.setText(field.getValue());
    }

    @Override
    public int getItemCount() {
        if (mFields != null)
            return mFields.size();
        else
            return 0;
    }

    @Override
    public void onItemClick(int position, View v) {
        // Pass up the field click listener
        if (mFieldClickListener != null) {
            mFieldClickListener.onFieldClicked(mCard, mFields.get(position), position);
        }
    }
}
