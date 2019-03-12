// Bailey Seymour
// DVP6 - 1903
// FieldFormFragment.java

package com.baileyseymour.overshare.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.models.FieldType;
import com.baileyseymour.overshare.utils.FieldUtils;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FieldFormFragment extends Fragment {

    // Constants
    private static final String ARG_FIELD = "ARG_FIELD";

    // Views

    @BindView(R.id.editTextFieldTitle)
    TextInputEditText mTitleEditText;

    @BindView(R.id.editTextFieldValue)
    TextInputEditText mValueEditText;

    @BindView(R.id.spinnerType)
    Spinner mSpinner;

    public FieldFormFragment() {
        // Default constructor
    }

    // Factory method
    public static FieldFormFragment newInstance(Field field) {

        Bundle args = new Bundle();

        // Store in bundle
        if (field != null)
            args.putSerializable(ARG_FIELD, field);

        FieldFormFragment fragment = new FieldFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_form_field, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // Get the field if any
    private Field getField() {
        if (getArguments() == null) return null;
        return ((Field) getArguments().getSerializable(ARG_FIELD));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu
        int menuId = R.menu.menu_save;

        // If in edit mode show a delete button as well
        if (getField() != null)
            menuId = R.menu.menu_delete_save;

        inflater.inflate(menuId, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu actions
        if (item.getItemId() == R.id.menu_action_save) {
            onSaveTapped();
            return true;
        } else if (item.getItemId() == R.id.menu_action_delete) {
            onDeleteTapped();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        setupSpinner();
        loadField();

    }

    private void setupSpinner() {
        if (getContext() == null) return;
        // Load FieldTypes into spinner
        Object fieldTypes[] = FieldUtils.getAvailableFields().values().toArray();
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, fieldTypes);
        mSpinner.setAdapter(adapter);
    }

    private void loadField() {
        if (getField() == null) return;

        mTitleEditText.setText(getField().getTitle());
        mValueEditText.setText(getField().getValue());
        FieldType fieldType = FieldUtils.fieldTypeFromId(getField().getType());
        if (fieldType != null) {
            int position = FieldUtils.indexOfFieldType(getField().getType());
            mSpinner.setSelection(position);
        }
    }

    private void onDeleteTapped() {
        // TODO: handle field deletion
    }

    private void onSaveTapped() {
        // TODO: handle field add/edit saving
    }
}
