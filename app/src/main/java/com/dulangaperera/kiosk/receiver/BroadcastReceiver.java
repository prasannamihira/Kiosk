package com.dulangaperera.kiosk.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dulangaperera.kiosk.activity.MainActivity;

/**
 * Created by Mihira on 10/31/2017.
 */

public class BroadcastReceiver extends android.content.BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("STARTUP", "BOOTING!");

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

        //Intent startServiceIntent = new Intent(context, MyService.class);
        //context.startService(startServiceIntent);
    }
}
