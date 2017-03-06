package com.example.lizejun.repodatabases;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.lizejun.repodatabases.db.ConcreteDBHelperFactory;
import com.example.lizejun.repodatabases.db.DBHelperManager;
import com.example.lizejun.repodatabases.db.MultiThreadDBContract;
import com.example.lizejun.repodatabases.db.MultiThreadDBHelper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelperManager.getInstance().setDBHelperFactory(new ConcreteDBHelperFactory());
    }

    /**
     * 多线程同时创建,每个线程持有一个SQLiteOpenHelper
     * @param view
     */
    public void multiOnCreate(View view) {
        int threadCount = 50;
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    MultiThreadDBHelper dbHelper = new MultiThreadDBHelper(MainActivity.this);
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

    /**
     * 多个线程同时写入,每个线程持有一个SQLiteOpenHelper
     * @param view
     */
    public void multiWriteUseMultiDBHelper(View view) {
        MultiThreadDBHelper init = new MultiThreadDBHelper(MainActivity.this);
        SQLiteDatabase database = init.getWritableDatabase();
        database.close();
        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    MultiThreadDBHelper dbHelper = new MultiThreadDBHelper(MainActivity.this);
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

    /**
     * 多个线程同时写入,公用一个SQLiteOpenHelper
     * @param view
     */
    public void multiWriteUseOneDBHelper(View view) {
        MultiThreadDBHelper init = new MultiThreadDBHelper(MainActivity.this);
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

    /**
     * 多线程同时读取,每个线程持有一个SQLiterDBHelper
     * @param view
     */
    public void multiReadUseMultiDBHelper(View view) {
        MultiThreadDBHelper init = new MultiThreadDBHelper(MainActivity.this);
        SQLiteDatabase database = init.getWritableDatabase();
        database.close();
        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    MultiThreadDBHelper dbHelper = new MultiThreadDBHelper(MainActivity.this);
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

    /**
     * 多线程下共用一个SQLiteDBHelper
     * @param view
     */
    public void multiCloseUseOneDBHelper(View view) {
        final MultiThreadDBHelper init = new MultiThreadDBHelper(MainActivity.this);
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

    public void multiWriteUseManager(View view) {
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
