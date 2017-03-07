package com.example.lizejun.repodatabases.db;

import android.database.sqlite.SQLiteOpenHelper;

import com.example.lizejun.repodatabases.utils.ContextUtils;

public class ConcreteDBHelperFactory extends DBHelperFactory {

    @Override
    public SQLiteOpenHelper createDBHelper(String dbName) {
        return new MultiThreadDBHelper(ContextUtils.getAppContext());
    }
}
