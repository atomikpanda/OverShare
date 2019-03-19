// Bailey Seymour
// DVP6 - 1903
// FieldFormFragment.java

package com.baileyseymour.overshare.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
import com.baileyseymour.overshare.models.SmartField;
import com.baileyseymour.overshare.models.ValidateError;
import com.baileyseymour.overshare.utils.EditTextUtils;
import com.baileyseymour.overshare.utils.FieldUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_FIELD;
import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_INDEX;


public class FieldFormFragment extends Fragment {

    // Constants
    private static final String ARG_FIELD = "ARG_FIELD";
    public static final int RESULT_DELETE_FIELD = 494;

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
        loadFieldTitleValue();
        setupSpinner();
        loadFieldSpinner();


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
        if (getContext() == null) return;

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
        Object fieldTypes[] = FieldUtils.getAvailableFields().values().toArray();
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, fieldTypes);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(mOnSpinnerSelected);
    }

    private PhoneNumberFormattingTextWatcher mWatcher = new PhoneNumberFormattingTextWatcher();

    private AdapterView.OnItemSelectedListener mOnSpinnerSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            FieldType typeSelected = (FieldType) FieldUtils.getAvailableFields().values().toArray()[position];
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
                case EMAIL:
                    mValueEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    break;
            }

            if (inputType == FieldType.InputType.PHONE) {
                mValueEditText.addTextChangedListener(mWatcher);
            } else {
                mValueEditText.removeTextChangedListener(mWatcher);
            }

            mValueEditText.setError(null);
            mValueInputLayout.setHelperTextEnabled(true);
            mValueInputLayout.setHelperText(FieldUtils.helperText(typeSelected, getResources()));
            String suggestion = typeSelected.getSuggestion();

            // Either we are in add field mode or we are in edit mode with a differing type

            if (getField() == null || (getField() != null && !typeSelected.equals(new SmartField(getField()).getFieldType()))) {
                mTitleEditText.setText(suggestion);
            }

            if (!suggestion.isEmpty()) {
                mValueEditText.requestFocus();
            } else {
                mTitleEditText.requestFocus();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void loadFieldTitleValue() {
        if (getField() == null) return;

        // Load in the values from the Field object
        // into the UI
        mTitleEditText.setText(getField().getTitle());
        mValueEditText.setText(getField().getValue());
    }

    private void loadFieldSpinner() {
        if (getField() == null || mSpinner == null) return;

        FieldType fieldType = FieldUtils.fieldTypeFromId(getField().getType());

        if (fieldType != null) {
            int position = FieldUtils.indexOfFieldType(getField().getType());
            mSpinner.setSelection(position);
        }
    }

    private void onDeleteTapped() {
        if (getActivity() == null) return;
        // handle field deletion
        // Finish with a result code that signifies a delete
        Intent data = new Intent();

        Intent startIntent = getActivity().getIntent();
        if (startIntent.hasExtra(EXTRA_INDEX)) {
            data.putExtra(EXTRA_INDEX, startIntent.getIntExtra(EXTRA_INDEX, -1));
        }

        getActivity().setResult(RESULT_DELETE_FIELD, data);
        getActivity().finish();
    }

    private void onSaveTapped() {
        if (getActivity() == null) return;
        // handle field add/edit saving
        FieldType typeSelected = (FieldType) mSpinner.getSelectedItem();
        LinkedHashMap<String, FieldType> availableFields = FieldUtils.getAvailableFields();

        // Lookup the
        String keyIdSelected = null;
        for (Map.Entry<String, FieldType> entry : availableFields.entrySet()) {
            if (entry.getValue().equals(typeSelected)) {
                keyIdSelected = entry.getKey();
                break;
            }
        }

        if (keyIdSelected == null) return;


        String type = keyIdSelected;
        String title = EditTextUtils.getString(mTitleEditText);
        String value = EditTextUtils.getString(mValueEditText);

        if (type.trim().isEmpty() || title.trim().isEmpty() || value.trim().isEmpty()) {
            return;
        }

        // Finish with RESULT_OK and an extra of the Field obj
        Field fieldObj = new Field(title, value, type);
        Intent data = new Intent();

        Intent startIntent = getActivity().getIntent();
        if (startIntent.hasExtra(EXTRA_INDEX)) {
            data.putExtra(EXTRA_INDEX, startIntent.getIntExtra(EXTRA_INDEX, -1));
        }

        data.putExtra(EXTRA_FIELD, fieldObj);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    @OnClick(R.id.buttonPaste)
    public void onPasteTapped() {

        String clipData = getClipboardData();

        if (clipData != null)
            mValueEditText.setText(clipData);
    }

    private String getClipboardData() {
        if (getContext() == null) return null;

        // Retrieve the clipboard manager from the context
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager != null) {

            // Check if the clipboard even has a primary clip
            if (clipboardManager.hasPrimaryClip()) {
                ClipData clip = clipboardManager.getPrimaryClip();

                // Make sure clip isn't null
                if (clip == null) return null;

                // Get a description
                ClipDescription clipDescription = clip.getDescription();

                if (clipDescription == null) return null;

                // Make sure we have text data
                if (!clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) return null;

                // Ensure we have more than one item
                if (clip.getItemCount() <= 0) return null;

                // Get the first item
                ClipData.Item item = clip.getItemAt(0);
                if (item == null) return null;

                CharSequence charSequence = item.getText();

                // Convert to string
                if (charSequence == null || mValueEditText == null) return null;
                return charSequence.toString();
            }
        }

        return null;
    }
}
