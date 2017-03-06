package com.example.lizejun.repodatabases.db;


import android.database.sqlite.SQLiteOpenHelper;

public abstract class DBHelperFactory {

    public abstract SQLiteOpenHelper createDBHelper(String dbName);
}
