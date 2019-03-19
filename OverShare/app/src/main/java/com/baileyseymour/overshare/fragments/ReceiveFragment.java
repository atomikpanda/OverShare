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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.utils.ChirpManager;
import com.baileyseymour.overshare.views.CustomVisualizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_CARDS;
import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_SAVED;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_CREATED_TIMESTAMP;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_HEX_ID;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_SAVED_BY_UID;


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
    private String mReceivedId;
    private FirebaseFirestore mDB;
    private State mState;

    // For visualizer view
    private Timer mTimer = new Timer();
    private byte[] mInputBytes;

    class State {
        // The description to be displayed on the label
        String description;

        // Primary icon
        @DrawableRes
        int icon;

        // Show action button
        boolean showButton;

        // The id
        int id;

        State(String description, int icon, boolean showButton, int id) {
            this.description = description;
            this.icon = icon;
            this.showButton = showButton;
            this.id = id;
        }
    }

    // Views

    @BindView(R.id.primaryIconImageView)
    ImageView primaryIconImageView;

    @BindView(R.id.textViewStateDescription)
    TextView stateDescription;

    @BindView(R.id.buttonPrimaryAction)
    TextView buttonPrimaryAction;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Get an instance of the database
        mDB = FirebaseFirestore.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getContext() != null) {
            Nammu.init(getContext().getApplicationContext());
            ChirpManager.getInstance(getContext()).setReceiver(this);

            // Initial State
            State initialState = new State("Setting up...",
                    R.drawable.ic_mic_outline_black_24dp, false, STATE_INIT);
            setState(initialState);

            // Schedule a timer to update the visualizer view
            if (mTimer != null)
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Refresh the visualizer view
                        if (mInputBytes != null && visualizerView != null && mState != null)
                            visualizerView.triggerReceive(
                                    mState.id == STATE_RECEIVED ? 0 : CustomVisualizer.calcVolume(mInputBytes));
                    }
                }, 0, 100);
        }
    }

    public void setState(final State state) {
        if (mState != null && state.id == mState.id) return;

        mState = state;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // Update the UI on the main thread
                stateDescription.setText(state.description);
                primaryIconImageView.setImageResource(state.icon);
                buttonPrimaryAction.setVisibility(state.showButton ? View.VISIBLE : View.GONE);

            }
        });
    }

    @OnClick(R.id.buttonPrimaryAction)
    public void onPrimaryAction() {
        if (getActivity() == null) return;

        // If we have something saved save it to the db
        if (mReceivedId != null) {
            onReceivedChirpHexId(mReceivedId);
        }

        getActivity().finish();
    }

    // This method reverse looks up a card by its chirp id
    // Then creates a new document in saved_cards
    // with the added key of "savedByUID" to associate it with the current user's account under
    // received cards

    private void onReceivedChirpHexId(String chirpHexTestId) {

        final String savedByUID = FirebaseAuth.getInstance().getUid();
        if (savedByUID == null) return;

        // Query for a card matching the hex id given
        Query query = mDB.collection(COLLECTION_CARDS)
                .whereEqualTo(KEY_HEX_ID, chirpHexTestId)
                .orderBy(KEY_CREATED_TIMESTAMP, Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Get documents
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();

                            if (documents.size() > 0) {
                                // Get the first matching card
                                DocumentSnapshot snapshot = documents.get(0);
                                Map<String, Object> map = snapshot.getData();
                                if (map != null) {
                                    // Add the current UID as the saving account
                                    map.put(KEY_SAVED_BY_UID, savedByUID);

                                    // Make the createdTimestamp reflect the new saved date
                                    map.put(KEY_CREATED_TIMESTAMP, FieldValue.serverTimestamp());
                                    mDB.collection(COLLECTION_SAVED)
                                            .document()
                                            .set(map);
                                }

                            }
                        }
                    }
                });
    }

    @Override
    public void onReceiving(int channel) {
        // When we start receiving display it
        State state = new State("Receiving", R.drawable.avd_anim,
                false, STATE_RECEIVING);
        setState(state);
    }

    @Override
    public void onReceived(@org.jetbrains.annotations.Nullable byte[] bytes, int i) {
        Log.v(TAG, "onReceived: " + Arrays.toString(bytes));
        if (bytes != null) {
            // Encode the bytes received
            String identifier = new String(Hex.encodeHex(bytes));

            Log.v("ChirpSDK: ", "Received " + identifier);

            mReceivedId = identifier;

            // If we have a valid identifier change state
            if (!identifier.trim().isEmpty()) {
                State state = new State("Received", R.drawable.ic_icons8_id_card,
                        true, STATE_RECEIVED);
                setState(state);
            }

        } else {
            Log.e("ChirpError: ", "Decode failed");

            // Revert back to ready if we failed
            State readyState = new State("Ready to Receive Cards",
                    R.drawable.ic_mic_on_black_24dp, false, STATE_READY);
            setState(readyState);

            // Show a toast indicating failure
            Toast.makeText(getContext(), "Failed to receive card.", Toast.LENGTH_SHORT).show();
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
            State errorState = new State("Error Connecting to Microphone",
                    R.drawable.ic_mic_off_black_24dp, false, STATE_MIC_ERROR);
            setState(errorState);
        } else {
            // We are ready
            State readyState = new State("Ready to Receive Cards",
                    R.drawable.ic_mic_on_black_24dp, false, STATE_READY);
            setState(readyState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop receiving on pause
        ChirpManager manager = ChirpManager.getInstance(getContext());
        manager.getChirpConnect().stop();

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
        manager.getChirpConnect().startReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Clear the visualizer timer
        mTimer.cancel();
        mTimer = null;
    }
}
