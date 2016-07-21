package com.rednovo.libs.net.okhttp.utils;

import android.util.Log;

public class L {
    private static boolean debug = false;

    public static void e(String msg) {
        if (debug) {
            Log.e("OkHttp", msg);
        }
    }

}

