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

import com.example.lizejun.repodatabases.loader.BaseDataLoader;
import com.example.lizejun.repodatabases.db.DBHelperManager;
import com.example.lizejun.repodatabases.db.MultiThreadDBContract;
import com.example.lizejun.repodatabases.db.MultiThreadDBHelper;

public class MainActivity extends Activity {

    private static final String LOADER_TAG = "loader_tag";
    private static final String QUERY = "query";

    private MyLoaderCallback mMyLoaderCallback;
    private TextView mResultView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        restore(savedInstanceState);
    }

    private void save(Bundle outState) {
        if (mEditText != null) {
            outState.putString(QUERY, mEditText.getText().toString());
        }
    }

    private void restore(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Bundle bundle = new Bundle();
            String query = bundle.getString(QUERY);
            bundle.putString(QUERY, query);
            getLoaderManager().initLoader(0, bundle, mMyLoaderCallback);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        save(outState);
        super.onSaveInstanceState(outState);
    }

    private void init() {
        mEditText = (EditText) findViewById(R.id.loader_input);
        mResultView = (TextView) findViewById(R.id.loader_result);
        mEditText.addTextChangedListener(new MyEditTextWatcher());
        mMyLoaderCallback = new MyLoaderCallback();
    }

    private void startQuery(String query) {
        if (query != null) {
            Bundle bundle = new Bundle();
            bundle.putString(QUERY, query);
            LoaderManager.enableDebugLogging(true);
            getLoaderManager().restartLoader(0, bundle, mMyLoaderCallback);
        }
    }

    private void showResult(String result) {
        if (mResultView != null) {
            mResultView.setText(result);
        }
    }

    private static class MyLoader extends BaseDataLoader<String> {

        public MyLoader(Context context, Bundle bundle) {
            super(context, bundle);
        }

        @Override
        protected String loadData(Bundle bundle) {
            Log.d(LOADER_TAG, "loadData");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bundle != null ? bundle.getString(QUERY) : "empty";
        }
    }

    private class MyLoaderCallback implements LoaderManager.LoaderCallbacks {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            Log.d(LOADER_TAG, "onCreateLoader");
            return new MyLoader(getApplicationContext(), args);
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            Log.d(LOADER_TAG, "onLoadFinished=" + data);
            showResult((String) data);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            Log.d(LOADER_TAG, "onLoaderReset");
            showResult("");
        }
    }

    private class MyEditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(LOADER_TAG, "onTextChanged=" + s);
            startQuery(s != null ? s.toString() : "");
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

}
