package com.dulangaperera.kiosk.network;

import android.content.Context;

/**
 * Created by lkfaswuser2 on 11/3/2017.
 */

public class ApiConstants {

        private static Context context;

        public ApiConstants(Context context) {
            this.context = context;
        }

        public static final String BASE_URL = "http://kiosk.com/api/";

        public static final int HTTP_CONNECT_TIMEOUT = 6000; // milliseconds
        public static final int HTTP_READ_TIMEOUT = 10000; // milliseconds
        public static String RESPONSE_VALUE_SUCCESS = "success";
}
