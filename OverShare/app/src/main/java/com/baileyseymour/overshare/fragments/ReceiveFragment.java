// Bailey Seymour
// DVP6 - 1903
// ReceiveFragment.java

package com.baileyseymour.overshare.fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.utils.CardUtils;
import com.baileyseymour.overshare.utils.ChirpManager;
import com.baileyseymour.overshare.views.CustomVisualizer;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.chirp.connect.models.ChirpError;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;


public class ReceiveFragment extends Fragment implements ChirpManager.Receiver {

    // States
    private static final int STATE_RECEIVED = 781;
    private static final int STATE_RECEIVING = 214;
    private static final int STATE_INIT = 808;
    private static final int STATE_MIC_ERROR = 161;
    private static final int STATE_READY = 971;
    private static final String TAG = "ReceiveFragment";

    // Instance vars
    private long mChirpStartMillis = 0;
    private ArrayList<String> mReceivedIds;
    private FirebaseFirestore mDB;
    private State mState;

    // For visualizer view
    private Timer mTimer = new Timer();
    private byte[] mInputBytes;

    class State {
        // The description to be displayed on the label
        @StringRes
        final
        int description;

        // Primary icon
        @DrawableRes
        final
        int icon;

        // Show action button
        final boolean showButton;

        // The id
        final int id;

        State(@StringRes int description, @DrawableRes int icon, boolean showButton, int id) {
            this.description = description;
            this.icon = icon;
            this.showButton = showButton;
            this.id = id;
            Log.d(TAG, "State: showBtn: " + this.showButton);
        }
    }

    // Views

    @BindView(R.id.primaryIconImageView)
    ImageView primaryIconImageView;

    @BindView(R.id.textViewStateDescription)
    TextView stateDescription;

    @BindView(R.id.buttonPrimaryAction)
    Button buttonPrimaryAction;

    @BindView(R.id.visualizer)
    CustomVisualizer visualizerView;

    public ReceiveFragment() {
        // Default constructor
    }

    // Factory method
    public static ReceiveFragment newInstance() {

        Bundle args = new Bundle();

        ReceiveFragment fragment = new ReceiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_receive, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void addReceivedId(String id) {

        // Check that we have valid data
        if (mReceivedIds != null && id != null && !id.trim().isEmpty()) {

            // Get the count of item ids we have
            int sizeBefore = mReceivedIds.size();

            // Add the received card id
            mReceivedIds.add(id);

            // Remove duplicates
            LinkedHashSet<String> set = new LinkedHashSet<>(mReceivedIds);
            mReceivedIds.clear();
            mReceivedIds.addAll(set);

            // Get the count of item ids after removing duplicates
            int sizeAfter = mReceivedIds.size();

            // Check if there were any modifications
            if (sizeBefore == sizeAfter) {
                Toast.makeText(getContext(), R.string.in_received, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.added_received, Toast.LENGTH_SHORT).show();
            }
        }

        refreshButton();
    }

    // Do any card ids exist in received queue?
    private boolean shouldShowSave() {
        return (mReceivedIds != null && mReceivedIds.size() > 0);
    }

    private void refreshButton() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (shouldShowSave()) {
                    buttonPrimaryAction.setVisibility(View.VISIBLE);
                } else {
                    buttonPrimaryAction.setVisibility(View.GONE);
                }
                if (mReceivedIds != null) {
                    int size = mReceivedIds.size();

                    // Set the button title using plurals
                    String saveTitle = getResources()
                            .getQuantityString(R.plurals.save_btn_fmt, size, size);
                    buttonPrimaryAction.setText(saveTitle);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Get an instance of the database
        mDB = FirebaseFirestore.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mReceivedIds = new ArrayList<>();
        if (getContext() != null) {
            Nammu.init(getContext().getApplicationContext());
            ChirpManager.getInstance(getContext()).setReceiver(this);

            // Initial State

            State initialState = new State(R.string.setting_up,
                    R.drawable.ic_mic_outline_black_24dp, false, STATE_INIT);
            setState(initialState);

            // Schedule a timer to update the visualizer view
            // Only display visualizer view when receiving
            if (mTimer != null)
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Refresh the visualizer view
                        if (mInputBytes != null && visualizerView != null && mState != null)
                            visualizerView.triggerReceive(
                                    (mState.id != STATE_RECEIVING) ? 0 : CustomVisualizer.calcVolume(mInputBytes));
                    }
                }, 0, 100);
        }

    }

    private void setState(final State state) {
        if (mState != null && state.id == mState.id) return;

        mState = state;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // Update the UI on the main thread
                stateDescription.setText(state.description);
                primaryIconImageView.setImageResource(state.icon);

//                if (state.id == STATE_RECEIVING) {
//                    buttonPrimaryAction.setEnabled(false);
//                } else {
//                    buttonPrimaryAction.setEnabled(true);
//                }

                refreshButton();
                // buttonPrimaryAction.setVisibility(state.showButton ? View.VISIBLE : View.GONE);

            }
        });
    }

    @OnClick(R.id.buttonPrimaryAction)
    public void onPrimaryAction() {
        if (getActivity() == null) return;

        // If we have something saved save it to the db
        if (mReceivedIds != null) {

            // Save all items
            for (String id : mReceivedIds) {
                onReceivedChirpHexId(id);
            }

        }

        getActivity().finish();
    }

    // This method reverse looks up a card by its chirp id
    // Then creates a new document in saved_cards
    // with the added key of "savedByUID" to associate it with the current user's account under
    // received cards

    private void onReceivedChirpHexId(String chirpHexTestId) {

        if (mDB != null)
            CardUtils.addCardToSavedCollection(chirpHexTestId, mDB);
    }

    @Override
    public void onReceiving(int channel) {
        // When we start receiving display it
        Log.d(TAG, "onReceiving: channel: " + channel);

        State state = new State(R.string.receiving, R.drawable.avd_anim,
                false, STATE_RECEIVING);
        setState(state);
    }

    @Override
    public void onReceived(@org.jetbrains.annotations.Nullable byte[] bytes, int channel) {
        Log.v(TAG, "onReceived: " + Arrays.toString(bytes) + " channel: " + channel);

        if (bytes != null) {
            // Encode the bytes received
            String identifier = new String(Hex.encodeHex(bytes));

            Log.d("ChirpSDK: ", "Received " + identifier);


            // If we have a valid identifier change state
            if (!identifier.trim().isEmpty()) {

                addReceivedId(identifier);

                State state = new State(R.string.received, R.drawable.ic_icons8_id_card,
                        true, STATE_RECEIVED);
                setState(state);
            }

        } else {
            Log.e("ChirpError: ", "Decode failed");

            // Revert back to ready if we failed

            State readyState = new State(R.string.ready_to_receive,
                    R.drawable.ic_mic_on_black_24dp, false, STATE_READY);
            setState(readyState);

            // Show a toast indicating failure
            Toast.makeText(getContext(), R.string.failed_to_receive, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAudioInputIsZeroed(boolean isZeroed, final byte[] bytes) {
        if (mState.id == STATE_RECEIVED || mState.id == STATE_RECEIVING) return;

        // Store current bytes used in calc for visualizer view
        if (visualizerView != null) {
            mInputBytes = bytes;
        }

        if (isZeroed) {
            // Prevent error-ing within the first second
            long timeSinceStart = (System.currentTimeMillis() - mChirpStartMillis);
            if (mChirpStartMillis > 0 && timeSinceStart < 1000) {
                return;
            }

            // Notify of error

            State errorState = new State(R.string.error_conn_mic,
                    R.drawable.ic_mic_off_black_24dp, false, STATE_MIC_ERROR);
            setState(errorState);
        } else {
            // We are ready
            State readyState = new State(R.string.ready_to_receive,
                    R.drawable.ic_mic_on_black_24dp, false, STATE_READY);
            setState(readyState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop receiving on pause
        ChirpManager manager = ChirpManager.getInstance(getContext());
        manager.stop();
//        ChirpConnectState state = manager.getChirpConnect().getState();
//        if (state != ChirpConnectState.CHIRP_CONNECT_STATE_NOT_CREATED && state != ChirpConnectState.CHIRP_CONNECT_STATE_STOPPED) {
//            try {
//                ChirpError error = manager.getChirpConnect().stop();
//                // Note: it's ok if an error occurs here as it is common that
//                // Chirp to tries to stop itself when running
//                if (error.getCode() > 0) {
//                    Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
//                }
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//        }


    }


    @Override
    public void onResume() {
        super.onResume();

        Nammu.askForPermission(this, Manifest.permission.RECORD_AUDIO, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                startChirp();
            }

            @Override
            public void permissionRefused() {
                // If we do not have permissions close the activity
                if (getActivity() != null)
                    getActivity().finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startChirp() {
        // Start listening via Chirp
        ChirpManager manager = ChirpManager.getInstance(getContext());
        mChirpStartMillis = System.currentTimeMillis();
        ChirpError error = manager.startReceiver();
        if (error.getCode() > 0) {
            Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clear the visualizer timer
        mTimer.cancel();
        mTimer = null;
    }
}
