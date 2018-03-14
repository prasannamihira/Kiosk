package com.dulangaperera.kiosk.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mihira on 10/31/2017.
 */

public class DateUtil {

    /**
     * Get current date
     * @return date in format
     */
    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
}
