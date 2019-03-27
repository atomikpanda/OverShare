// Bailey Seymour
// DVP6 - 1903
// CardsListFragment.java

package com.baileyseymour.overshare.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.activities.CardFormActivity;
import com.baileyseymour.overshare.activities.ReceiveActivity;
import com.baileyseymour.overshare.adapters.CardAdapter;
import com.baileyseymour.overshare.interfaces.CardActionListener;
import com.baileyseymour.overshare.interfaces.FieldClickListener;
import com.baileyseymour.overshare.interfaces.RecyclerEmptyStateListener;
import com.baileyseymour.overshare.models.Card;
import com.baileyseymour.overshare.adapters.viewholders.CardViewHolder;
import com.baileyseymour.overshare.models.Field;
import com.baileyseymour.overshare.models.SmartField;
import com.baileyseymour.overshare.utils.AudioUtils;
import com.baileyseymour.overshare.utils.CardUtils;
import com.baileyseymour.overshare.utils.ChirpAudioDownloaderUtil;
import com.baileyseymour.overshare.utils.ChirpManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import io.chirp.connect.models.ChirpConnectState;
import io.chirp.connect.models.ChirpError;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.content.Intent.EXTRA_TEXT;
import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_CARDS;
import static com.baileyseymour.overshare.interfaces.Constants.COLLECTION_SAVED;
import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_CARD;
import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_CARD_DOC_ID;
import static com.baileyseymour.overshare.interfaces.Constants.EXTRA_IS_RECEIVED;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_CREATED_BY_UID;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_CREATED_TIMESTAMP;
import static com.baileyseymour.overshare.interfaces.Constants.KEY_SAVED_BY_UID;
import static com.baileyseymour.overshare.interfaces.Constants.URI_CARD_PATH;
import static com.baileyseymour.overshare.interfaces.Constants.URI_HOST;
import static com.baileyseymour.overshare.utils.ChirpAudioDownloaderUtil.EXT_MP3;


public class CardsListFragment extends Fragment implements FieldClickListener, CardActionListener, RecyclerEmptyStateListener, ChirpManager.Sender {

    private static final String ARG_IS_RECEIVED = "ARG_IS_RECEIVED";
    private static final String TAG = "CardsListFrag";
    private static final int RC_RECEIVE = 247;

    private FirebaseFirestore mDB;
    private FirestoreRecyclerAdapter<Card, CardViewHolder> mCardAdapter;
    private FabContainer mFabContainer;
    private boolean mIsPlayingSound;

    // Fab container interface allows us to access the fab remotely
    public interface FabContainer {
        FloatingActionButton onProvideFab();

        int getPagePosition();
    }

    public CardsListFragment() {
        // Default constructor
    }

    // Factory method
    public static CardsListFragment newInstance(boolean isReceivedCards) {

        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_RECEIVED, isReceivedCards);
        CardsListFragment fragment = new CardsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FabContainer) {
            mFabContainer = ((FabContainer) context);
        }

        // Load database
        mDB = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout
        return inflater.inflate(R.layout.fragment_list_cards, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getContext() != null) {

            Nammu.init(getContext().getApplicationContext());

            setupFab();
            ChirpManager.getInstance(getContext());

        }

        setupRecycler();

    }

    private void setupRecycler() {

        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            return;
        }

        Query query;// = null;

        if (!getIsReceivedCards() /*&& uid != null*/) {
            // Query for only cards created by this user
            query = mDB.collection(COLLECTION_CARDS)
                    .orderBy(KEY_CREATED_TIMESTAMP, Query.Direction.DESCENDING)
                    .whereEqualTo(KEY_CREATED_BY_UID, uid);
        } else /*if (uid != null)*/ {
            // Query for only cards SAVED by this user
            query = mDB.collection(COLLECTION_SAVED)
                    .orderBy(KEY_CREATED_TIMESTAMP, Query.Direction.DESCENDING)
                    .whereEqualTo(KEY_SAVED_BY_UID, uid);
        }

        // Create the fire store options builder
        FirestoreRecyclerOptions.Builder<Card> builder = new FirestoreRecyclerOptions.Builder<>();

        // Set the query
        builder.setQuery(query, Card.class);

        // Build options
        FirestoreRecyclerOptions<Card> options = builder.build();

        // Initialize the card adapter
        mCardAdapter = new CardAdapter(getContext(), options, this, this, this, getIsReceivedCards());


        // Setup recycler view to use our fire store adapter
        if (getView() != null) {
            final RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);

            mCardAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    // Scroll to the top when an item is added
                    scrollToTop(recyclerView);
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mCardAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mCardAdapter);
            scrollToTop(recyclerView);
        }
    }

    private void scrollToTop(RecyclerView recyclerView) {
        if (recyclerView == null) return;

        LinearLayoutManager manager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int first = 0;

        if (manager != null) {
            first = manager.findFirstVisibleItemPosition();
        }

        if (first == RecyclerView.NO_POSITION) {
            first = 0;
        }
        recyclerView.smoothScrollToPosition(first);
    }

    private void setupFab() {
        if (mFabContainer != null) {

            // Get the fab button from the fab container
            final FloatingActionButton fab = mFabContainer.onProvideFab();

            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = mFabContainer.getPagePosition();
                        if (position == 0) {
                            fab.setEnabled(false);
                            // We are on the my cards tab, so FAB is add card
                            Intent addCardIntent = new Intent(getContext(), CardFormActivity.class);
                            startActivity(addCardIntent);

                        } else if (position == 1) {
                            // We are on the receive cards tab, so FAB is receive
                            // Start ReceiveActivity via an intent

                            goToReceive(fab);

                        }

                    }
                });
            }
        }
    }

    private void goToReceive(final FloatingActionButton fab) {
        Nammu.askForPermission(CardsListFragment.this, Manifest.permission.RECORD_AUDIO, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                if (fab != null)
                    fab.setEnabled(false);
                // Start the receive activity only after getting permission
                Intent receiveIntent = new Intent(getContext(), ReceiveActivity.class);
                startActivityForResult(receiveIntent, RC_RECEIVE);
            }

            @Override
            public void permissionRefused() {

            }
        });
    }

    // Is this fragment supposed to display the cards that were received vs created
    private boolean getIsReceivedCards() {
        if (getArguments() != null) {
            return getArguments().getBoolean(ARG_IS_RECEIVED, false);
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Tell our adapter to listen for db changes
        if (mCardAdapter != null)
            mCardAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        // Stop listening
        if (mCardAdapter != null)
            mCardAdapter.stopListening();
    }

    // Field click listener

    @Override
    public void onFieldClicked(Card card, Field field, int fieldPosition) {
        Log.d(TAG, "onFieldClicked: card: " + card + ", field: " + field + ", pos: " + fieldPosition);

        SmartField smartField = new SmartField(field);

        if (smartField.getFieldType() != null) {
            String action = Intent.ACTION_VIEW;

            // Handle phone URLs
            if (smartField.generateURL().startsWith("tel:")) {
                action = Intent.ACTION_DIAL;
            }

            // Open the browser or phone app
            Intent intent = new Intent(action, Uri.parse(smartField.generateURL()));
            startActivity(intent);
        }
    }

    @Override
    public void onUpdateEmptyState(boolean hasData) {
        // Handle showing/hiding the recycler view and empty state based
        // on if any data exists
        if (getView() != null) {
            View emptyView = getView().findViewById(android.R.id.empty);
            View recyclerView = getView().findViewById(R.id.recyclerView);

            // Toggle view visibility
            if (emptyView != null)
                emptyView.setVisibility(hasData ? View.GONE : View.VISIBLE);

            // Switch empty state text to clearly tell the user what to do
            TextView textView = getView().findViewById(R.id.textViewEmptyDescription);

            if (emptyView != null && textView != null) {

                @StringRes int emptyDescription = R.string.click_the_button_to_add_a_card;

                if (getIsReceivedCards())
                    emptyDescription = R.string.click_the_receive_button;

                textView.setText(emptyDescription);
            }

            // Toggle recycler view visibility
            if (recyclerView != null)
                recyclerView.setVisibility(hasData ? View.VISIBLE : View.GONE);
        }
    }

    // CardActionListener

    @Override
    public void onCardAction(String action, final Card card, int position, final DocumentSnapshot snapshot) {
        Log.d(TAG, "onCardAction: action: " + action + ", card: " + card + ", pos: " + position +
                ", documentId: " + snapshot.getId());

        // Handle card actions
        switch (action) {
            case ACTION_SHARE_CARD_CHIRP:
                shareCardChirp(card, snapshot);
                break;
            case ACTION_SHARE_CARD_DOWNLOAD:
                Nammu.askForPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , new PermissionCallback() {
                            @Override
                            public void permissionGranted() {
                                ChirpAudioDownloaderUtil.downloadCard(card, EXT_MP3, getContext());
                            }

                            @Override
                            public void permissionRefused() {

                            }
                        });

                break;
            case ACTION_SHARE_CARD_COPY:
                if (getContext() != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(EXTRA_TEXT, CardUtils.sharableString(card));
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Share Card Text"));
                }
                break;
            case ACTION_SHARE_CARD_URL:
                if (getContext() != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    String url = String.format(Locale.US, "https://%s/%s/%s", URI_HOST, URI_CARD_PATH, card.getHexId());
                    intent.putExtra(EXTRA_TEXT, url);
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Share Card URL"));
                }
                break;
            case ACTION_DELETE_CARD:

                if (getContext() == null) return;
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.confirm_delete_title)
                        .setMessage(R.string.cant_undo)
                        .setNegativeButton(R.string.fui_cancel, null)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Delete the document
                                mDB.collection(getIsReceivedCards() ? COLLECTION_SAVED : COLLECTION_CARDS)
                                        .document(snapshot.getId()).delete();
                            }
                        })
                        .show();


                break;
            case ACTION_EDIT_CARD:

                Intent editIntent = new Intent(getContext(), CardFormActivity.class);
                editIntent.putExtra(EXTRA_CARD, card);
                editIntent.putExtra(EXTRA_CARD_DOC_ID, snapshot.getId());
                editIntent.putExtra(EXTRA_IS_RECEIVED, getIsReceivedCards());
                editIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(editIntent);

                break;
        }


    }

    private void shareCardChirp(final Card card, final DocumentSnapshot snapshot) {

        if (getContext() == null) return;

        Log.d(TAG, "shareCardChirp: card: " + card.getHexId() + "snapshot: " + snapshot);

        Nammu.askForPermission(this, Manifest.permission.RECORD_AUDIO, new PermissionCallback() {
            @Override
            public void permissionGranted() {

                // Prevent playing multiple times at once
                if (mIsPlayingSound) return;

                // Set max volume
                AudioUtils.getInstance(getContext()).setMaxVolume(getContext());

                // Start up Chirp SDK
                ChirpManager manager = ChirpManager.getInstance(getContext());
                ChirpError error = manager.getChirpConnect().startSender();
                if (error.getCode() > 0) {
                    Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
                }
                manager.setSender(CardsListFragment.this);

                String hexId = card.getHexId();
                if (hexId != null && !hexId.trim().isEmpty()) {

                    try {
                        // Play sound
                        byte[] hexBytes = Hex.decodeHex(hexId.toCharArray());

                        manager.sendBytes(hexBytes);
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void permissionRefused() {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        // Re-enable fab
        if (mFabContainer != null) {
            FloatingActionButton fab = mFabContainer.onProvideFab();

            if (fab != null)
                fab.setEnabled(true);
        }

        ChirpManager manager = ChirpManager.getInstance(getContext());
        manager.setSender(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        ChirpManager manager = ChirpManager.getInstance(getContext());
        if (!manager.getChirpConnect().getState().equals(ChirpConnectState.CHIRP_CONNECT_STATE_NOT_CREATED)) {
            try {
                ChirpError error = manager.getChirpConnect().stop();

                // Note: it's ok if an error occurs here as it is common that
                // Chirp to tries to stop itself when running
                if (error.getCode() > 0) {
                    Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        manager.setSender(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChirpManager manager = ChirpManager.getInstance(getContext());
        try {
            manager.getChirpConnect().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSending(@NotNull byte[] bytes, int channel) {
        Log.d(TAG, "onSending: bytes: " + Arrays.toString(bytes) + "channel: " + channel);
        mIsPlayingSound = true;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (getView() == null) return;

                // Show a snack bar when the data is sending
                Snackbar snackbar = Snackbar.make(getView(), R.string.sharing_transmitting, Snackbar.LENGTH_LONG);
                snackbar.setDuration(4200);
                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        AudioUtils.getInstance(getContext()).revertVolume();
                        mIsPlayingSound = false;
                    }
                });
                snackbar.show();
            }
        });

    }

    @Override
    public void onSent(@NotNull byte[] bytes, int channel) {
        Log.d(TAG, "onSent: bytes: " + Arrays.toString(bytes) + " channel: " + channel);
        mIsPlayingSound = false;
    }

    @Override
    public void onSystemVolumeChanged(int old, int current) {
        Log.d(TAG, "onSystemVolumeChanged: old: " + old + " current: " + current);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
