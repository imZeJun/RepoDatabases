package com.example.lizejun.repodatabases.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.util.Log;

public abstract class BaseDataLoader<Result> extends AsyncTaskLoader<Result> {

    private static final String TAG = "BaseDataLoader";

    Result mResult;
    Bundle mBundles;
    CancellationSignal mCancellationSignal;

    public BaseDataLoader(Context context, Bundle bundle) {
        super(context);
        mBundles = bundle;
    }

    @Override
    public Result loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        try {
            return loadData(mBundles);
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();
        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    @Override
    public void deliverResult(Result result) {
        if (isReset()) {
            if (result != null && !isResultReleased(result)) {
                releaseResult(result);
            }
            return;
        }
        Result oldResult = mResult;
        mResult = result;
        if (isStarted()) {
            super.deliverResult(result);
        }
        if (oldResult != null && oldResult != result && !isResultReleased(oldResult)) {
            releaseResult(oldResult);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        }
        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Result result) {
        Log.d(TAG, "onCanceled");
        if (result != null && !isResultReleased(result)) {
            releaseResult(result);
        }
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "onReset");
        super.onReset();
        onStopLoading();
        if (mResult != null && !isResultReleased(mResult)) {
            releaseResult(mResult);
        }
        mResult = null;
    }

    protected abstract Result loadData(Bundle bundle);

    protected void releaseResult(Result result) {}

    protected boolean isResultReleased(Result result) { return true; }


}
