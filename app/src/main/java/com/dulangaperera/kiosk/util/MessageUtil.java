package com.dulangaperera.kiosk.util;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Mihira on 10/31/2017.
 */

public class MessageUtil {

    /**
     * Show snack-bar message
     *
     * @param message
     * @param coordinatorLayout
     */
    public static void showSnackBarMessage(String message, CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
