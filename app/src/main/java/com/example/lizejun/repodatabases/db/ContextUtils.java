package com.example.lizejun.repodatabases.db;

import android.content.Context;

public class ContextUtils {

    private static Context sContext;

    public static void setAppContext(Context context) {
        sContext = context;
    }

    public static Context getAppContext() {
        return sContext;
    }
}
