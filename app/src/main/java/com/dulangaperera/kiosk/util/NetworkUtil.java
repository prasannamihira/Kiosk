package com.dulangaperera.kiosk.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;

/**
 * Created by Mihira on 10/31/2017.
 */

public class NetworkUtil {

    final private static String TAG = "error";

    /**
     * Check the network is connected
     * @param ctx
     * @return
     */
    public static boolean isInternetConnected(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Popup wifi enable message
     * @param contex
     */
    public static void popupDataWifiEnableMessage(final Context contex) {

        AlertDialog alertDialog = new AlertDialog.Builder(contex).create();
        alertDialog.setTitle("Enable Data | WiFi");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            enableWifi(contex);
                            setMobileDataEnabled(contex);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "EXIT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * Enable mobile data connection
     * @param context
     */
    public static void setMobileDataEnabled(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * Enable wifi connection
     * @param context
     */
    public static void enableWifi(Context context) {
        try {
            WifiManager wifi = (WifiManager)
                    context.getSystemService(Context.WIFI_SERVICE);

            WifiConfiguration wc = new WifiConfiguration();
            wc.SSID = "\"SSIDName\"";
            wc.preSharedKey = "\"password\"";
            wc.hiddenSSID = true;
            wc.status = WifiConfiguration.Status.ENABLED;

            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

            boolean b = wifi.isWifiEnabled();
            if (b) {
                wifi.setWifiEnabled(false);
            } else {
                wifi.setWifiEnabled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
