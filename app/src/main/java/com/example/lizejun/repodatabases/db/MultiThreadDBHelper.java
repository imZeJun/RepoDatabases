package com.example.lizejun.repodatabases.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MultiThreadDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "MultiThreadDBHelper";


    public MultiThreadDBHelper(Context context) {
        super(context, MultiThreadDBContract.DATABASE_NAME, null, MultiThreadDBContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate, threadId=" + Thread.currentThread().getId());
        db.execSQL(MultiThreadDBContract.TABLE_KEY_VALUE.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade, threadId=" + Thread.currentThread().getId() + ",oldVersion=" + oldVersion + ",newVersion=" + newVersion);
    }


}
