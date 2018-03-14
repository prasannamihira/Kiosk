package com.dulangaperera.kiosk.util;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Mihira on 10/31/2017.
 */

public class Preference {

    /**
     * Save preference
     *
     * @param key
     * @param value
     * @param activity
     */
    public static void savePreference(String key, String value, Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(key, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Get preference by key
     *
     * @param key
     * @param activity
     * @return
     */
    public static String showPreference(String key, Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(key, Activity.MODE_PRIVATE);
        String savedPref = sharedPreferences.getString(key, "");
        return savedPref;
    }
}
