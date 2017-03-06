package com.example.lizejun.repodatabases.db;

import android.content.Context;

public class ContextUtils {

    private static Context sAppContext;

    public static void setAppContext(Context context) {
        sAppContext = context;
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
