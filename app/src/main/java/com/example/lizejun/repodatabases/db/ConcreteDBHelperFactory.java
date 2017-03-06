package com.example.lizejun.repodatabases.db;

import android.database.sqlite.SQLiteOpenHelper;

public class ConcreteDBHelperFactory extends DBHelperFactory {

    @Override
    public SQLiteOpenHelper createDBHelper(String dbName) {
        return new MultiThreadDBHelper(ContextUtils.getAppContext());
    }
}
