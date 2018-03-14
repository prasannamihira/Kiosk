package com.dulangaperera.kiosk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Mihira on 10/31/2017.
 */

public class PhoneUtil {

    private static final String TAG = "PhoneUtil";

    /**
     * Reboot device
     */
    public static void rebootDevice() {
        Process proc = null;
        try {
            //proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot" });
            proc = Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
            proc.waitFor();
        } catch (Exception ex) {
            try {
                Runtime.getRuntime().exec(new String[]{"/system/xbin/su","-c","reboot now"});
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "Could not reboot", e);
            }

        }
    }

    /**
     * Get phone number
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        String number = tm.getLine1Number();


        if (number != null && !number.equals("")) {

            PhoneNumberUtil util = PhoneNumberUtil.createInstance(context);
            try {
                String code = getUserCountry(context).equals("") ? "IT": getUserCountry(context);
                Phonenumber.PhoneNumber phoneNumberInternationl = util.parse(number, code);
                number = util.format(phoneNumberInternationl, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

            } catch (Exception ex) {

            }

            if (number.charAt(0) == '0')
                number = number.substring(1);

            number = number.replace(" ","");
        }
        return number;
    }

    /**
     * get user country
     * @param context
     * @return
     */
    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }

        } catch (Exception e) {
        }
        return null;
    }


}
