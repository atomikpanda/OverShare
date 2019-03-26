// Bailey Seymour
// DVP6 - 1903
// ThemeUtils.java

package com.baileyseymour.overshare.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.baileyseymour.overshare.R;
import com.baileyseymour.overshare.activities.MainActivity;

public class ThemeUtils {

    private static final String KEY_NIGHT_MODE_ENABLED = "com.baileyseymour.overshare.NIGHT_MODE_ENABLED";
    private static SharedPreferences preferences;

    // Applies the currently active theme to the app
    public static void applyTheme(Activity activity) {
        loadPrefs(activity);

        boolean enabled = isNightModeEnabled(activity);

        // Switch theme but also make sure the main activity requires NoActionBar
        if (activity instanceof MainActivity) {
            activity.setTheme(enabled ? R.style.AppThemeDark_NoActionBar : R.style.AppTheme_NoActionBar);
        } else {
            activity.setTheme(enabled ? R.style.AppThemeDark : R.style.AppTheme);
        }
    }

    // Gets the value for night mode
    public static boolean isNightModeEnabled(Context context) {
        if (context == null) return false;

        loadPrefs(context);

        if (preferences == null) return false;
        return preferences.getBoolean(KEY_NIGHT_MODE_ENABLED, false);
    }

    // Overrides the value for night mode
    public static void setNightModeEnabled(boolean enabled, Context context) {
        if (context == null) return;
        loadPrefs(context);

        if (preferences == null) return;

        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(KEY_NIGHT_MODE_ENABLED, enabled);
        edit.apply();
    }

    // Load the default prefs from the context
    private static void loadPrefs(Context context) {
        if (preferences == null && context != null)
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
