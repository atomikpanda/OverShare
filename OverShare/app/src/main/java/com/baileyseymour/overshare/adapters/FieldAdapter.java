package com.baileyseymour.overshare.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.interfaces.FieldClickListener;
import com.baileyseymour.overshare.interfaces.FieldHolderClickListener;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.models.FieldViewHolder;

import java.util.ArrayList;


public class FieldAdapter extends RecyclerView.Adapter<FieldViewHolder> implements FieldHolderClickListener {
    private Card mCard;
    private ArrayList<Field> mFields;
    private FieldClickListener mFieldClickListener;

    public FieldAdapter(Card card, FieldClickListener fieldClickListener) {
        mCard = card;
        mFields = Field.fromListOfMaps(card.getFields());
        mFieldClickListener = fieldClickListener;
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.field_view_layout, viewGroup, false);
        return new FieldViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder viewHolder, int i) {
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
        if (mFieldClickListener != null) {
            mFieldClickListener.onFieldClicked(mCard, mFields.get(position), position);
        }
    }
}
