package com.dulangaperera.kiosk.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created by Mihira on 10/31/2017.
 */

public class BasicDeviceAdminReceiver extends DeviceAdminReceiver {

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), BasicDeviceAdminReceiver.class);
    }
}
