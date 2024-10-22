package com.hassan.statussaver.workers

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hassan.statussaver.services.FileObserverService

class RestartServiceWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        Log.d("RestartServiceWorker", "Restarting service");
        val serviceIntent = Intent(applicationContext, FileObserverService::class.java)
        applicationContext.startService(serviceIntent)
        return Result.success()
    }
}