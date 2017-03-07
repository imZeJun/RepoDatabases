package com.example.lizejun.repodatabases.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBSimulator {

    public static void multiOnCreate(final Context context) {
        int threadCount = 50;
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    MultiThreadDBHelper dbHelper = new MultiThreadDBHelper(context);
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_KEY, "thread_id");
                    contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_VALUE, String.valueOf(Thread.currentThread().getId()));
                    database.insert(MultiThreadDBContract.TABLE_KEY_VALUE.TABLE_NAME, null, contentValues);
                }
            };
            thread.start();
        }
    }


    public void multiWriteUseMultiDBHelper(final Context context) {
        MultiThreadDBHelper init = new MultiThreadDBHelper(context);
        SQLiteDatabase database = init.getWritableDatabase();
        database.close();
        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    MultiThreadDBHelper dbHelper = new MultiThreadDBHelper(context);
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    for (int i = 0; i < 1000; i++) {
                        ContentValues contentValues = new ContentValues(1);
                        contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_KEY, "thread_id");
                        contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_VALUE, String.valueOf(Thread.currentThread().getId()) + "_" + i);
                        database.insert(MultiThreadDBContract.TABLE_KEY_VALUE.TABLE_NAME, null, contentValues);
                    }
                }
            };
            thread.start();
        }
    }


    public void multiWriteUseOneDBHelper(final Context context) {
        MultiThreadDBHelper init = new MultiThreadDBHelper(context);
        final SQLiteDatabase database = init.getWritableDatabase();
        database.close();
        int threadCount = 1000;
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        ContentValues contentValues = new ContentValues(1);
                        contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_KEY, "thread_id");
                        contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_VALUE, String.valueOf(Thread.currentThread().getId()) + "_" + i);
                        database.insert(MultiThreadDBContract.TABLE_KEY_VALUE.TABLE_NAME, null, contentValues);
                    }
                }
            };
            thread.start();
        }
    }


    public void multiReadUseMultiDBHelper(final Context context) {
        MultiThreadDBHelper init = new MultiThreadDBHelper(context);
        SQLiteDatabase database = init.getWritableDatabase();
        database.close();
        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    MultiThreadDBHelper dbHelper = new MultiThreadDBHelper(context);
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    for (int i = 0; i < 1000; i++) {
                        Cursor cursor = database.query(MultiThreadDBContract.TABLE_KEY_VALUE.TABLE_NAME, null, null, null, null, null, null);
                        if (cursor != null) {
                            Log.d("MainActivity", "cursorCount=" + cursor.getCount() + ",threadId=" + Thread.currentThread().getId());
                            cursor.close();
                        }
                    }
                }
            };
            thread.start();
        }
    }


    public void multiCloseUseOneDBHelper(final Context context) {
        final MultiThreadDBHelper init = new MultiThreadDBHelper(context);
        final SQLiteDatabase database = init.getWritableDatabase();
        database.close();
        Thread thread1 = new Thread() {

            @Override
            public void run() {
                SQLiteDatabase database = init.getWritableDatabase();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.e("MainActivity", "e=" + e);
                }
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_KEY, "thread_id");
                contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_VALUE, String.valueOf(Thread.currentThread().getId()));
                database.insert(MultiThreadDBContract.TABLE_KEY_VALUE.TABLE_NAME, null, contentValues);
            }
        };
        thread1.start();
        Thread thread2 = new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.e("MainActivity", "e=" + e);
                }
                init.close();
            }
        };
        thread2.start();
    }

    public void multiWriteUseManager(final Context context) {
        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        ContentValues contentValues = new ContentValues(1);
                        contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_KEY, "thread_id");
                        contentValues.put(MultiThreadDBContract.TABLE_KEY_VALUE.COLUMN_VALUE, String.valueOf(Thread.currentThread().getId()) + "_" + i);
                        DBHelperManager.getInstance().insert(MultiThreadDBContract.DATABASE_NAME, MultiThreadDBContract.TABLE_KEY_VALUE.TABLE_NAME, null, contentValues);
                    }
                }
            };
            thread.start();
        }
    }
}
