package com.dulangaperera.kiosk.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.dulangaperera.kiosk.receiver.OnScreenOffReceiver;
import com.dulangaperera.kiosk.service.KioskService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Mihira on 10/31/2017.
 */

public class AppContext extends Application {

    private static AppContext instance;
    private PowerManager.WakeLock wakeLock;
    private OnScreenOffReceiver onScreenOffReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerKioskModeScreenOffReceiver();
        startKioskService();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("kiosk.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    public static AppContext getInstance() {
        return instance;
    }

    /**
     * Start kiosk service
     */
    private void startKioskService() {
        startService(new Intent(this, KioskService.class));
    }

    /**
     * Register screen off receiver
     */
    private void registerKioskModeScreenOffReceiver() {
        // register screen off receiver
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        registerReceiver(onScreenOffReceiver, filter);
    }

    /**
     * Get wake lock
     * @return
     */
    public PowerManager.WakeLock getWakeLock() {
        if(wakeLock == null) {
            // lazy loading: first call, create wakeLock via PowerManager.
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
        }
        return wakeLock;
    }
}
