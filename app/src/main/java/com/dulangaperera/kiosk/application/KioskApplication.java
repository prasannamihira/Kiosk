package com.dulangaperera.kiosk.application;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.support.compat.BuildConfig;

/**
 * Created by Mihira on 10/31/2017.
 */

public class KioskApplication extends Application {

    @Override
    public void onCreate() {

        if(BuildConfig.DEBUG) {
            /**
             * Doesn't enable anything on the main thread that related
             * to resource access.
             */
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .penaltyDeath()
                    .build());

            /**
             * Doesn't enable any leakage of the application's components.
             */
            final StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder.detectLeakedRegistrationObjects();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                builder.detectFileUriExposure();
            }

            builder.detectLeakedClosableObjects()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDeath();
            StrictMode.setVmPolicy(builder.build());
        }
        super.onCreate();
    }
}
