package com.example.batrakov.asynctask_task;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Contains target AsyncTasks.
 * Created by batrakov on 12.10.17.
 */
public class SecondScreenActivity extends AppCompatActivity implements TaskListener {

    private static final String TAG = "SecondScreenActivity";
    private TextView mCounterView;
    private TextView mLongWorkView;
    private Button mRestartButton;
    private CounterAsyncTask mCounterAsyncTask;
    private static WorkerAsyncTask sWorkerAsyncTask;
    private static final int THREAD_SLEEP_DELAY = 1000;

    /**
     * Simple AsyncTask, increment integer value and display it in UI thread.
     */
    private class CounterAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onProgressUpdate(Integer... aValues) {
            super.onProgressUpdate(aValues);
            mCounterView.setText(String.valueOf(aValues[0]));
        }

        @Override
        protected Void doInBackground(Void... aParams) {
            int counter = 0;
            while (counter < Integer.MAX_VALUE) {
                if (isCancelled()) {
                    break;
                }
                counter++;
                publishProgress(counter);
                try {
                    Thread.sleep(THREAD_SLEEP_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * Simulate long operation job and send information about it status in UI after job is done.
     */
    private final class WorkerAsyncTask extends AsyncTask<Void, Integer, Void> {

        private boolean mIsWorking = false;
        private WeakReference<TaskListener> mListener;
        private static final int LONG_TASK_WORK_DURATION = 20;

        /**
         * Constructor.
         *
         * @param aListener link to communicate with UI.
         */
        private WorkerAsyncTask(TaskListener aListener) {
            mListener = new WeakReference<>(aListener);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsWorking = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsWorking = false;
            Log.i(TAG, "onPostExecute: " + String.valueOf(mListener.get()));
            Log.i(TAG, "onPostExecute: " + String.valueOf(mListener));
            if (mListener != null) {
                mListener.get().onTaskFinished();
            }
            mListener = null;
        }

        @Override
        protected Void doInBackground(Void... aParams) {
            int counter = 0;
            while (counter < LONG_TASK_WORK_DURATION) {
                if (isCancelled()) {
                    mIsWorking = false;
                    mListener = null;
                    break;
                }
                counter++;
                publishProgress(counter);
                try {
                    Thread.sleep(THREAD_SLEEP_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "doInBackground: " + String.valueOf(counter));
            }
            return null;
        }

        /**
         * Set a new listener if Activity was recreated.
         *
         * @param aListener target new listener.
         */
        void setListener(TaskListener aListener) {
            mListener = new WeakReference<>(aListener);
        }

        /**
         * @return working status.
         */
        boolean isWorking() {
            return mIsWorking;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.second_screen);

        mCounterView = (TextView) findViewById(R.id.counter);
        mLongWorkView = (TextView) findViewById(R.id.job_is_done);
        mRestartButton = (Button) findViewById(R.id.restart);

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                if (sWorkerAsyncTask != null) {
                    if (sWorkerAsyncTask.isWorking()) {
                        sWorkerAsyncTask.cancel(false);
                        onTaskDenied();
                    } else {
                        startWorkingTask();
                        onTaskStarted();
                    }
                } else {
                    startWorkingTask();
                    onTaskStarted();
                }

            }
        });

        mCounterAsyncTask = new CounterAsyncTask();
        mCounterAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        if (sWorkerAsyncTask != null) {
            sWorkerAsyncTask.setListener(this);
            if (sWorkerAsyncTask.isWorking()) {
                onTaskStarted();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.view_for_snackbar),
                        R.string.task_in_progress, Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                view.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                snackbar.show();
            }
        }
    }

    /**
     * Execute new long task.
     */
    private void startWorkingTask() {
        sWorkerAsyncTask = new WorkerAsyncTask(this);
        sWorkerAsyncTask.execute();
        Snackbar snackbar = Snackbar.make(findViewById(R.id.view_for_snackbar),
                R.string.task_restarted, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCounterAsyncTask.cancel(false);
    }

    @Override
    public void onTaskStarted() {
        mRestartButton.setText(getResources().getText(R.string.cancel));
        mLongWorkView.setText(getResources().getText(R.string.job_in_progress));
    }

    @Override
    public void onTaskFinished() {
        mRestartButton.setText(getResources().getText(R.string.restart));
        mLongWorkView.setText(getResources().getText(R.string.job_is_done));
        Snackbar snackbar = Snackbar.make(findViewById(R.id.view_for_snackbar),
                R.string.task_finished, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(getColor(R.color.colorPrimary));
        snackbar.show();
    }

    /**
     * Notify that task was denied.
     */
    private void onTaskDenied() {
        mRestartButton.setText(getResources().getText(R.string.restart));
        mLongWorkView.setText(getResources().getText(R.string.task_denied));
    }
}
