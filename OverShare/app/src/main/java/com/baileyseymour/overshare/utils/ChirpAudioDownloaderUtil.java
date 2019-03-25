// Bailey Seymour
// DVP6 - 1903
// ChirpAudioDownloaderUtil.java

package com.baileyseymour.overshare.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.baileyseymour.overshare.models.Card;

import java.util.Locale;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.baileyseymour.overshare.interfaces.Constants.CHIRP_APP_KEY;
import static com.baileyseymour.overshare.interfaces.Constants.CHIRP_APP_SECRET;

public class ChirpAudioDownloaderUtil {

    // Example: ChirpAudioDownloaderUtil.downloadCard(card, EXT_MP3, getContext());
    // Experimental Feature only

    private static final String TAG = "DownloaderUtil";
    public static final String EXT_MP3 = "mp3";
    //private static final String EXT_WAV = "wav";
    private static final String AUTHORIZATION = "Basic " + Base64.encodeToString((CHIRP_APP_KEY + ":" + CHIRP_APP_SECRET).getBytes(), Base64.NO_WRAP);

    public static void downloadCard(Card card, String extension, Context context) {
        if (context == null) return;

        DownloadManager downloadManager = ((DownloadManager) context.getSystemService(DOWNLOAD_SERVICE));
        if (downloadManager != null) {
            Uri uri = Uri.parse(audioFileURL(card.getHexId(), extension));

            DownloadManager.Request request = new DownloadManager.Request(uri);

            String filename = card.getTitle() + "." + extension;
            request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, filename);
            request.setTitle(filename);
            request.addRequestHeader("Authorization", AUTHORIZATION);
            request.setDescription("Downloading card audio...");
            Log.d(TAG, "download card: " + uri.toString());
            downloadManager.enqueue(request);
        }
    }

    private static String audioFileURL(String hexId, String extension) {
        if (extension == null)
            extension = EXT_MP3;
        return String.format(Locale.US, "https://audio.chirp.io/v3/ultrasonic-long-range/%s.%s",
                hexId, extension);
    }
}
