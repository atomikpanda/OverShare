// Bailey Seymour
// DVP6 - 1903
// FieldListAdapter.java

package com.baileyseymour.overshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.models.Field;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FieldListAdapter extends BaseAdapter {

    private static final long BASE_ID = 0x04090000;
    private final Context mContext;
    private final ArrayList<Field> mFields;

    public FieldListAdapter(Context context, ArrayList<Field> fields) {
        mContext = context;
        mFields = fields;
    }

    @Override
    public int getCount() {
        if (mFields != null) {
            return mFields.size();
        }

        return 0;
    }

    @Override
    public long getItemId(int position) {
        return BASE_ID + position;
    }

    @Override
    public Field getItem(int position) {
        if (mFields != null && position < mFields.size() && position >= 0) {
            return mFields.get(position);
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_field, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        // Get item
        Field field = getItem(position);
        if (field != null) {
            // Configure the view using the holder
            holder.title.setText(field.getTitle());
            holder.value.setText(field.getValue());
        }

        return convertView;
    }

    static class ViewHolder {

        // Instance var subviews
        @BindView(R.id.textViewFieldTitle)
        TextView title;

        @BindView(R.id.textViewFieldValue)
        TextView value;

        ViewHolder(View layout) {
            // Get the views from the parent layout
            ButterKnife.bind(this, layout);
        }
    }

}
