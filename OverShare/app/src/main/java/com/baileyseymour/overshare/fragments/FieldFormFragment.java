// Bailey Seymour
// DVP6 - 1903
// FieldFormFragment.java

package com.baileyseymour.overshare.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.models.FieldType;
import com.baileyseymour.overshare.models.ValidateError;
import com.baileyseymour.overshare.utils.EditTextUtils;
import com.baileyseymour.overshare.utils.FieldUtils;

import java.util.Locale;

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

    @BindView(R.id.inputLayoutValue)
    TextInputLayout mValueInputLayout;

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

        // Setup UI
        setupSpinner();
        loadField();

        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable[] r = new Runnable[1];


        if (mValueInputLayout.getEditText() != null) {
            mValueInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(final Editable s) {
                    handler.removeCallbacks(r[0]);
                    r[0] = new Runnable() {
                        @Override
                        public void run() {
                            validateValue(s.toString());
                        }
                    };
                    handler.postDelayed(r[0], 500);
                }
            });
        }
    }

    private void validateValue(String s) {
        FieldType typeSelected = (FieldType) mSpinner.getSelectedItem();
        ValidateError valid = typeSelected.getInputType().validate(s);
        if (valid != ValidateError.VALID) {
            mValueEditText.setError(valid.toString());
        } else {
            mValueEditText.setError(null);
            mValueInputLayout.setHelperTextEnabled(true);
            mValueInputLayout.setHelperText(FieldUtils.helperText(typeSelected, getResources()));
        }
    }

    private void setupSpinner() {
        if (getContext() == null) return;

        // Load FieldTypes into spinner
        Object fieldTypes[] = FieldUtils.getAvailableFields(getResources()).values().toArray();
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, fieldTypes);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(mOnSpinnerSelected);
    }

    private AdapterView.OnItemSelectedListener mOnSpinnerSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            FieldType typeSelected = (FieldType) FieldUtils.getAvailableFields(getResources()).values().toArray()[position];
            FieldType.InputType inputType = typeSelected.getInputType();

            switch (inputType) {

                case TEXT:
                    mValueEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case URL:
                case URL_USER:
                    mValueEditText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                    break;
                case USERNAME:
                    mValueEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case PHONE:
                    mValueEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
            }

            mValueEditText.setError(null);
            mValueInputLayout.setHelperTextEnabled(true);
            mValueInputLayout.setHelperText(FieldUtils.helperText(typeSelected, getResources()));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void loadField() {
        if (getField() == null) return;

        // Load in the values from the Field object
        // into the UI
        mTitleEditText.setText(getField().getTitle());
        mValueEditText.setText(getField().getValue());
        FieldType fieldType = FieldUtils.fieldTypeFromId(getField().getType());

        if (fieldType != null) {
            int position = FieldUtils.indexOfFieldType(getResources(), getField().getType());
            mSpinner.setSelection(position);
        }
    }

    private void onDeleteTapped() {
        // TODO: handle field deletion
        // Finish with a result code that signifies a delete
    }

    private void onSaveTapped() {
        // TODO: handle field add/edit saving
        // Finish with RESULT_OK and an extra of the Field obj
    }
}
