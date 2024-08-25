package com.devatrii.statussaver.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.devatrii.statussaver.R;
import com.devatrii.statussaver.utils.SharedPrefKeys;

public class FileObserverService extends Service {
    private static final String CHANNEL_ID = "FileObserverServiceChannel";
    private FolderFileObserver fileObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1,createNotification());

        Log.d("FileObserverService", "Service started");
        fileObserver = new FolderFileObserver(this,SharedPrefKeys.getWhatsappDirectoryAdress10p().getAbsolutePath());
        fileObserver.startWatching();
    }


    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("File Observer Service")
                .setContentText("Monitoring file changes")
                .setSmallIcon(R.drawable.ic_menu_wp_downloads)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "File Observer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fileObserver.stopWatching();
    }

}