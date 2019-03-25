// Bailey Seymour
// DVP6 - 1903
// FieldAdapter.java

package com.baileyseymour.overshare.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.adapters.viewholders.FieldViewHolder;
import com.baileyseymour.overshare.interfaces.FieldClickListener;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.models.SmartField;
import com.baileyseymour.overshare.utils.ClipboardUtils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.ButterKnife;


public class FieldAdapter extends RecyclerView.Adapter<FieldViewHolder> implements FieldViewHolder.ClickListener {

    // Instance vars
    private final Card mCard;
    private final ArrayList<Field> mFields;
    private final FieldClickListener mFieldClickListener;

    @BindColor(R.color.field_colors)
    ColorStateList fieldColors;

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

//        if (viewGroup.getContext() != null)
//            mContext = viewGroup.getContext();

        View view = LayoutInflater.from(viewGroup.getContext()).inflate
                (R.layout.field_view_layout, viewGroup, false);
        ButterKnife.bind(this, view);
        return new FieldViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder viewHolder, int i) {
        // Load the field's properties into the UI
        Field field = mFields.get(i);

        SmartField smartField = new SmartField(field);

        viewHolder.titleTextView.setText(field.getTitle());
        viewHolder.valueTextView.setTextColor(fieldColors);
        viewHolder.valueTextView.setEnabled(smartField.isValueURL());

        // Simply allow some additional customization for displaying
        String displayString = String.format(Locale.US, smartField.getFieldType().getDisplayFormat(), field.getValue());
        viewHolder.valueTextView.setText(displayString);
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
            Field field = mFields.get(position);

            if (field != null) {
                SmartField smartField = new SmartField(field);

                if (!smartField.isValueURL()) {
                    // Prevent clicking non url fields
                    copyToClipboard(field, v);
                    return;
                }
            }

            mFieldClickListener.onFieldClicked(mCard, field, position);
        }
    }

    @Override
    public void onLongClick(int position, View v) {
        Field field = mFields.get(position);

        if (field != null) {
            copyToClipboard(field, v);
        }
    }

    private void copyToClipboard(Field field, View v) {
        Context context = v.getContext();

        if (context != null) {
            // Copy to clipboard if we have plain text
            SmartField smartField = new SmartField(field);

            ClipboardUtils.getInstance(context).setClipboard(field.getTitle(), smartField.clipboardValue());
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        }
    }
}
