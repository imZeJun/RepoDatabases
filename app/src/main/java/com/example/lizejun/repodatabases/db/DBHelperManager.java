package com.example.lizejun.repodatabases.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.HashMap;

public class DBHelperManager {

    private HashMap<String, SQLiteOpenHelperWrapper> mDBHelperWrappers;
    private DBHelperFactory mDBHelperFactory;

    static class Nested {
        public static DBHelperManager sInstance = new DBHelperManager();
    }

    public static DBHelperManager getInstance() {
        return Nested.sInstance;
    }

    private DBHelperManager() {
        mDBHelperWrappers = new HashMap<>();
    }

    public void setDBHelperFactory(DBHelperFactory dbHelperFactory) {
        mDBHelperFactory = dbHelperFactory;
    }

    private synchronized SQLiteOpenHelperWrapper getSQLiteDBHelperWrapper(String dbName) {
        SQLiteOpenHelperWrapper wrapper = mDBHelperWrappers.get(dbName);
        if (wrapper == null) {
            if (mDBHelperFactory != null) {
                SQLiteOpenHelper dbHelper = mDBHelperFactory.createDBHelper(dbName);
                if (dbHelper != null) {
                    SQLiteOpenHelperWrapper newWrapper = new SQLiteOpenHelperWrapper();
                    newWrapper.mSQLiteOpenHelper = dbHelper;
                    newWrapper.mSQLiteOpenHelper.setWriteAheadLoggingEnabled(true);
                    mDBHelperWrappers.put(dbName, newWrapper);
                    wrapper = newWrapper;
                }
            }
        }
        return wrapper;
    }

    private synchronized SQLiteDatabase getReadableDatabase(String dbName) {
        SQLiteOpenHelperWrapper wrapper = getSQLiteDBHelperWrapper(dbName);
        if (wrapper != null && wrapper.mSQLiteOpenHelper != null) {
            return wrapper.mSQLiteOpenHelper.getReadableDatabase();
        } else {
            return null;
        }
    }

    private synchronized SQLiteDatabase getWritableDatabase(String dbName) {
        SQLiteOpenHelperWrapper wrapper = getSQLiteDBHelperWrapper(dbName);
        if (wrapper != null && wrapper.mSQLiteOpenHelper != null) {
            return wrapper.mSQLiteOpenHelper.getWritableDatabase();
        } else {
            return null;
        }
    }

    private class SQLiteOpenHelperWrapper {
        public SQLiteOpenHelper mSQLiteOpenHelper;
    }

    public long insert(String dbName, String tableName, String nullColumn, ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase(dbName);
        if (db != null) {
            return db.insert(tableName, nullColumn, contentValues);
        }
        return -1;
    }

    public Cursor query(String dbName, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase db = getReadableDatabase(dbName);
        if (db != null) {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
        return null;
    }

    public int update(String dbName, String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase(dbName);
        if (db != null) {
            return db.update(table, values, whereClause, whereArgs);
        }
        return 0;
    }

    public int delete(String dbName, String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase(dbName);
        if (db != null) {
            return db.delete(table, whereClause, whereArgs);
        }
        return 0;
    }

}

