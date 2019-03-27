// Bailey Seymour
// DVP6 - 1903
// ChirpManager.java

package com.baileyseymour.overshare.utils;

import android.content.Context;
import android.util.Log;

import com.baileyseymour.overshare.interfaces.Constants;
import com.google.firebase.FirebaseApp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.interfaces.ConnectEventListener;
import io.chirp.connect.models.ChirpConnectState;
import io.chirp.connect.models.ChirpError;
import io.chirp.connect.models.ChirpErrorCode;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.baileyseymour.overshare.interfaces.Constants.CHIRP_APP_KEY;

public class ChirpManager implements ConnectEventListener {

    private ChirpConnect mChirpConnect;

    public static final String TAG = "ChirpManager";



    // The object that is responsible for handling receiving operations
    public interface Receiver {
        void onReceiving(int channel);

        void onReceived(@Nullable byte[] bytes, int channel);

        void onAudioInputIsZeroed(boolean isZeroed, byte[] bytes);
    }

    // The object that listens for sending callbacks
    public interface Sender {
        void onSending(@NotNull byte[] bytes, int channel);

        void onSent(@NotNull byte[] bytes, int channel);

        void onSystemVolumeChanged(int old, int current);
    }

    private Receiver mReceiver;
    private Sender mSender;
    private boolean mSoundIsPlaying;
    private boolean mStopping;

    private ChirpManager(Context context) {
        initConnect(context);
    }

    private void initConnect(Context context) {
        mChirpConnect = new ChirpConnect(context, CHIRP_APP_KEY, Constants.CHIRP_APP_SECRET);

        ChirpError error = mChirpConnect.setConfig(Constants.CHIRP_APP_CONFIG);

        if (error.getCode() > 0) {
            Log.e(TAG, "ChirpError: " + error.getMessage());
        }

        mChirpConnect.setListener(this);
        mChirpConnect.setInputAudioCallback(new Function1<byte[], Unit>() {
            @Override
            public Unit invoke(byte[] bytes) {

                boolean allZeros = true;
                for (byte aByte : bytes) {
                    if (aByte != 0) {
                        allZeros = false;
                        break;
                    }
                }
                if (mReceiver != null) {
                    mReceiver.onAudioInputIsZeroed(allZeros, bytes);
                }

                return null;
            }
        });
    }

    public ChirpError startSender() {
        Log.d(TAG, "startSender: ");
        if (getChirpConnect() != null) {

            try {
                ChirpError error = getChirpConnect().startSender();
                // Note: it's ok if an error occurs here as it is common that
                // Chirp to tries to stop itself when running
                if (error.getCode() > 0) {
                    Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
                }

                Log.d(TAG, "we started: ");

            } catch (Exception e) {
                e.printStackTrace();
//                mChirpConnect = null;
//                initConnect(FirebaseApp.getInstance().getApplicationContext());
            } finally {
            }

        }

        return new ChirpError(ChirpErrorCode.CHIRP_CONNECT_UNKNOWN_ERROR);
    }

    public ChirpError startReceiver() {
        Log.d(TAG, "startReceiver: ");
        if (getChirpConnect() != null) {
            return getChirpConnect().startReceiver();
        }

        return new ChirpError(ChirpErrorCode.CHIRP_CONNECT_UNKNOWN_ERROR);
    }

    public boolean isSoundPlaying() {
        return mSoundIsPlaying;
    }

    public void setSoundIsPlaying(boolean soundIsPlaying) {
        mSoundIsPlaying = soundIsPlaying;
    }

    private static ChirpManager INSTANCE;

    // Singleton
    public static ChirpManager getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = new ChirpManager(context);
        }

        return INSTANCE;
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public void setSender(Sender sender) {
        mSender = sender;
    }

    private ChirpConnect getChirpConnect() {
        return mChirpConnect;
    }

    public void sendBytes(byte[] bytesToSend) {
        ChirpError error = mChirpConnect.send(bytesToSend);
        if (error.getCode() > 0) {
            Log.e(TAG, "ChirpError: " + error.getMessage());
        }

    }


    // ConnectEventListener

    @Override
    public void onSending(@NotNull byte[] bytes, int i) {
        Log.d(TAG, "This is called when a payload is being sent " + Arrays.toString(bytes) + " on channel: " + i);
        if (mSender != null) {
            mSender.onSending(bytes, i);
        }
    }

    @Override
    public void onSent(@NotNull byte[] bytes, int i) {
        if (mSender != null) {
            mSender.onSent(bytes, i);
        }
        Log.d(TAG, "This is called when a payload has been sent " + Arrays.toString(bytes) + " on channel: " + i);
    }

    @Override
    public void onReceiving(int i) {
        Log.d(TAG, "This is called when the SDK is expecting a payload to be received on channel: " + i);
        if (mReceiver != null) {
            mReceiver.onReceiving(i);
        }
    }

    @Override
    public void onReceived(@Nullable byte[] bytes, int i) {
        Log.d(TAG, "onReceived: " + Arrays.toString(bytes));
        if (mReceiver != null) {
            mReceiver.onReceived(bytes, i);
        }
    }

    @Override
    public void onStateChanged(int i, int i1) {
        Log.d(TAG, "This is called when the SDK state has changed " + i + " -> " + i1);
    }

    @Override
    public void onSystemVolumeChanged(int old, int current) {
        Log.d(TAG, "This is called when the Android system volume has changed " + old + " -> " + current);
        if (mSender != null) {
            mSender.onSystemVolumeChanged(old, current);
        }
    }

    public void stop() {
        if (mStopping) {
            Log.d(TAG, "prevent stop: ");
            return;
        }
        mStopping = true;

        ChirpConnectState state = getChirpConnect().getState();
        if (state != ChirpConnectState.CHIRP_CONNECT_STATE_NOT_CREATED && state != ChirpConnectState.CHIRP_CONNECT_STATE_STOPPED) {
            Log.d(TAG, "attempting stop: ");
            try {
                ChirpError error = getChirpConnect().stop();
                // Note: it's ok if an error occurs here as it is common that
                // Chirp to tries to stop itself when running
                if (error.getCode() > 0) {
                    Log.e(ChirpManager.TAG, "ChirpError: " + error.getMessage());
                }

                Log.d(TAG, "we stopped: ");

                Log.d(TAG, "closing");
                getChirpConnect().close();
                initConnect(FirebaseApp.getInstance().getApplicationContext());
                Log.d(TAG, "we closed");
            } catch (Exception e) {
                e.printStackTrace();
                mChirpConnect = null;
                initConnect(FirebaseApp.getInstance().getApplicationContext());
            } finally {
                mStopping = false;
            }
        }
        mStopping = false;

    }

}
