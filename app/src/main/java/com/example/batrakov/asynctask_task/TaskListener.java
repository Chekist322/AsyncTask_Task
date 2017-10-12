package com.example.batrakov.asynctask_task;

/**
 * Allow to communicate with AsyncTask that execute long operation.
 * Created by batrakov on 12.10.17.
 */

interface TaskListener {

    /**
     * Means that task was started.
     */
    void onTaskStarted();

    /**
     * Notify that task was finished.
     */
    void onTaskFinished();
}
