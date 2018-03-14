package com.dulangaperera.kiosk.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import com.dulangaperera.kiosk.application.AppContext;

/**
 * Created by Mihira on 10/31/2017.
 */

public class OnScreenOffReceiver extends BroadcastReceiver{

    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            AppContext ctx = (AppContext) context.getApplicationContext();
            // is Kiosk Mode active?
            if(isKioskModeActive(ctx)) {
                wakeUpDevice(ctx);
            }
        }
    }

    /**
     * Wakeup the device
     * @param context
     */
    private void wakeUpDevice(AppContext context) {
        PowerManager.WakeLock wakeLock = context.getWakeLock(); // get WakeLock reference via AppContext
        if (wakeLock.isHeld()) {
            // release old wake lock
            wakeLock.release();
        }

        // create a new wake lock...
        wakeLock.acquire();

        // ... and release again
        wakeLock.release();
    }

    /**
     * Check kiosk mode is active
     * @param context
     * @return
     */
    private boolean isKioskModeActive(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_KIOSK_MODE, false);
    }
}
