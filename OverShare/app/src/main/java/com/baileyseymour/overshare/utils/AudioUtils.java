// Bailey Seymour
// DVP6 - 1903
// AudioUtils.java

package com.baileyseymour.overshare.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.baileyseymour.overshare.R;

import static android.content.Context.AUDIO_SERVICE;

// Manages device audio output
public class AudioUtils {
    private static final String TAG = "AudioUtils";
    private static final int VOLUME_NULL = -1;
    private static int PREV_SYS_VOLUME = VOLUME_NULL;

    public interface MaxVolDoneListener {
        void onDone(boolean shouldPlaySound);
    }

    private AudioManager mAudioManager;

    private AudioUtils(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    // Singleton
    private static AudioUtils INSTANCE;

    public static AudioUtils getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AudioUtils(context);
        }
        return INSTANCE;
    }

    // Sets the max volume
    public void setMaxVolume(Context context) {
        if (context == null) return;

        if (mAudioManager == null) {
            Toast.makeText(context, R.string.volume_low, Toast.LENGTH_SHORT).show();
            return;
        }

        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (current < max) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);

            int adjusted = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.d(TAG, "setMaxVolume: current: " + current);
            if (adjusted < (int) (max * 0.7f)) {
                Toast.makeText(context, R.string.volume_low, Toast.LENGTH_SHORT).show();
            } else {
                PREV_SYS_VOLUME = current;
            }
        }

    }
//    public void setMaxVolume(final Context context, final MaxVolDoneListener maxVolDoneListener) {
//        boolean isPlayingMusic = false;
//        if (mAudioManager != null) {
//            if (isPlayingMusic && context != null) {
//                new AlertDialog.Builder(context)
//                        .setTitle(R.string.maximize_vol)
//                        .setMessage(R.string.maximize_vol_desc)
//                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (maxVolDoneListener != null) {
//                                    maxVolDoneListener.onDone(false);
//                                }
//                            }
//                        })
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (maxVolDoneListener != null) {
//                                    setMaxVolume(context);
//                                    maxVolDoneListener.onDone(true);
//                                }
//                            }
//                        })
//                        .show();
//            } else if (!isPlayingMusic) {
//                setMaxVolume(context);
//                maxVolDoneListener.onDone(true);
//            }
//        }
//    }

    // Reverts volume level after calling setMaxVolume
    public void revertVolume() {
        if (PREV_SYS_VOLUME != VOLUME_NULL && mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, PREV_SYS_VOLUME, 0);
            PREV_SYS_VOLUME = VOLUME_NULL;
        }
    }
}
