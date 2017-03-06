package com.example.lizejun.repodatabases;

import android.app.Application;
import com.example.lizejun.repodatabases.db.ContextUtils;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.setAppContext(this);
    }
}
