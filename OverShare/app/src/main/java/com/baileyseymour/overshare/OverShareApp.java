// Bailey Seymour
// DVP6 - 1903
// OverShareApp.java

package com.baileyseymour.overshare;

import android.app.Application;

import com.baileyseymour.overshare.utils.FieldUtils;

import java.io.IOException;
import java.io.InputStream;


// The lint-er does not understand that this class must be public
@SuppressWarnings("WeakerAccess")
public class OverShareApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Init field utils
        try (InputStream is = getResources().openRawResource(R.raw.field_types)) {
            FieldUtils.init(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
