// Bailey Seymour
// DVP6 - 1903
// ClipboardUtils.java

package com.baileyseymour.overshare.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ClipboardUtils {

    private final ClipboardManager mClipboardManager;

    private static ClipboardUtils INSTANCE;

    private ClipboardUtils(Context context) {
        mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    // Singleton

    public static ClipboardUtils getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ClipboardUtils(context);
        }
        return INSTANCE;
    }

    public void setClipboard(String label, String text) {
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));
    }

    @Nullable
    public String getClipboard() {

        // Retrieve the clipboard manager from the context

        if (mClipboardManager != null) {

            // Check if the clipboard even has a primary clip
            if (mClipboardManager.hasPrimaryClip()) {
                ClipData clip = mClipboardManager.getPrimaryClip();

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
                if (charSequence == null) return null;
                return charSequence.toString();
            }
        }

        return null;
    }
}
