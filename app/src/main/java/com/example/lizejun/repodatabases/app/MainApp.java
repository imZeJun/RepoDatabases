package com.example.lizejun.repodatabases.app;

import android.app.Application;
import com.example.lizejun.repodatabases.utils.ContextUtils;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.setAppContext(this);
    }
}
