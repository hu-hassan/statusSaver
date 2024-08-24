package com.devatrii.statussaver.workers

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devatrii.statussaver.services.FileObserverService

class RestartServiceWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val serviceIntent = Intent(applicationContext, FileObserverService::class.java)
        applicationContext.startForegroundService(serviceIntent)
        return Result.success()
    }
}