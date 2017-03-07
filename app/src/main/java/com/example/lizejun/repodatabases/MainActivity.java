package com.example.lizejun.repodatabases;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lizejun.repodatabases.db.BaseDataLoader;
import com.example.lizejun.repodatabases.db.ConcreteDBHelperFactory;
import com.example.lizejun.repodatabases.db.DBHelperManager;
import com.example.lizejun.repodatabases.db.MultiThreadDBContract;
import com.example.lizejun.repodatabases.db.MultiThreadDBHelper;

public class MainActivity extends Activity {

    private MyLoaderCallback mMyLoaderCallback;

    private TextView mResultView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        register();
    }

    private void init() {
        mEditText = (EditText) findViewById(R.id.loader_input);
        mResultView = (TextView) findViewById(R.id.loader_result);
        mEditText.addTextChangedListener(new MyEditTextWatcher());
    }

    private void register() {
        mMyLoaderCallback = new MyLoaderCallback();
        Log.d("TestLoader", "register");
        getLoaderManager().initLoader(0, null, mMyLoaderCallback);
    }

    private void startQuery(String query) {
        if (query != null) {
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            getLoaderManager().restartLoader(0, bundle, mMyLoaderCallback);
        }
    }

    private void showResult(String result) {
        if (mResultView != null) {
            mResultView.setText(result);
        }
    }

    private static class MyLoader extends BaseDataLoader<String> {

        public MyLoader(Context context) {
            super(context);
        }

        @Override
        protected String loadData(Bundle bundle) {
            Log.d("TestLoader", "loadData");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("loadData, e=" + e);
            }
            return bundle != null ? bundle.getString("query") : "empty";
        }
    }

    private class MyLoaderCallback implements LoaderManager.LoaderCallbacks {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            Log.d("TestLoader", "onCreateLoader");
            return new MyLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            Log.d("TestLoader", "onLoadFinished");
            showResult((String) data);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            Log.d("TestLoader", "onLoaderReset");
            showResult("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //getLoaderManager().destroyLoader(0);
    }

    private class MyEditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("TestLoader", "onTextChanged");
            startQuery(s != null ? s.toString() : "");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


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
