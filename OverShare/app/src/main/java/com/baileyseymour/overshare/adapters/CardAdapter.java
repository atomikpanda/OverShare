package com.baileyseymour.overshare.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.interfaces.FieldClickListener;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.CardViewHolder;
import com.baileyseymour.overshare.models.Field;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CardAdapter extends FirestoreRecyclerAdapter<Card, CardViewHolder> {

    private final Context mContext;
    private FieldClickListener mClickListener;

    public CardAdapter(Context context, @NonNull FirestoreRecyclerOptions<Card> options, FieldClickListener fieldClickListener) {
        super(options);
        mContext = context;
        mClickListener = fieldClickListener;
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
        return new CardViewHolder(view);
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

}
