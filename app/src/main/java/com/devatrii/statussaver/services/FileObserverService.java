//package com.devatrii.statussaver.services;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Intent;
//import android.os.Build;
//import android.os.FileObserver;
//import android.os.IBinder;
//import android.util.Log;
//
//
//
//import androidx.core.app.NotificationCompat;
//
//import com.devatrii.statussaver.R;
//import com.devatrii.statussaver.utils.SharedPrefKeys;
//import com.devatrii.statussaver.utils.SharedPrefUtils;
//
//
//public class FileObserverService extends Service {
//    private static final String CHANNEL_ID = "FileObserverServiceChannel";
//    private FolderFileObserver fileObserver;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        createNotificationChannel();
//        startForeground(1, createNotification());
//
//        Log.d("FileObserverService", "Service started");
//        String path;
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//            Log.d("FileObserverService", "getWhatsappDirectoryAdress10m path");
//            if(SharedPrefUtils.INSTANCE.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,false)) {
//                Log.d("FileObserverService", "getWhatsappDirectoryAdress10m path true");
//                path = SharedPrefKeys.getWhatsappDirectoryAdress10m().getAbsolutePath();
//                fileObserver = new FolderFileObserver(this, path);
//            }
//            if (SharedPrefUtils.INSTANCE.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,false)) {
//                path = SharedPrefKeys.getWhatsappBusinessDirectoryAdress10m().getAbsolutePath();
//                fileObserver = new FolderFileObserver(this, path);
//            }
//        } else {
//                if (SharedPrefUtils.INSTANCE.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, false)) {
//                    Log.d("FileObserverService", "WP Permission Granted");
//                    String localPath = SharedPrefKeys.getWhatsappDirectoryAdress10p().getAbsolutePath();
//                    Log.d("FileObserverService", "Path: " + localPath);
//                    fileObserver = new FolderFileObserver(FileObserverService.this, localPath);
//                    fileObserver.startWatching();
//                }
//            if (SharedPrefUtils.INSTANCE.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED, false)) {
//                Log.d("FileObserverService", "WP Business Permission Granted");
//                path = SharedPrefKeys.getWhatsappBusinessDirectoryAdress10p().getAbsolutePath();
//                Log.d("FileObserverService", "Path: " + path);
//                fileObserver = new FolderFileObserver(this, path);
//                fileObserver.startWatching();
//            }
//
//        }
//    }
//
//    private Notification createNotification() {
//        return new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("File Observer Service")
//                .setContentText("Monitoring file changes")
//                .setSmallIcon(R.drawable.ic_menu_wp_downloads)
//                .build();
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "File Observer Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        fileObserver.stopWatching();
//    }
//}
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
import com.devatrii.statussaver.utils.SharedPrefUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileObserverService extends Service {
    private static final String CHANNEL_ID = "FileObserverServiceChannel";
    private FolderFileObserver fileObserver;
    private ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FileObserverService", "onCreate");
        createNotificationChannel();
        startForeground(1, createNotification());

        Log.d("FileObserverService", "Service started");

        // Initialize the executorService
        executorService = Executors.newSingleThreadExecutor();

        String path;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            executorService.execute(() -> {
                while (!SharedPrefUtils.INSTANCE.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, false)) {
                    try {
                        Thread.sleep(1000); // Wait for 1 second before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                Log.d("FileObserverService", "WP Permission Granted");

                String localPath = SharedPrefKeys.getWhatsappDirectoryAdress10m().getAbsolutePath();
                Log.d("FileObserverService", "Path: " + localPath);

                // Initialize and start the FileObserver
                fileObserver = new FolderFileObserver(FileObserverService.this, localPath);
            });
            executorService.execute(() -> {
                while (!SharedPrefUtils.INSTANCE.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED, false)) {
                    try {
                        Thread.sleep(1000); // Wait for 1 second before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                Log.d("FileObserverService", "WP Business Permission Granted");

                String localPath = SharedPrefKeys.getWhatsappBusinessDirectoryAdress10m().getAbsolutePath();
                Log.d("FileObserverService", "Path: " + localPath);

                // Initialize and start the FileObserver
                fileObserver = new FolderFileObserver(FileObserverService.this, localPath);
            });
        } else {
            executorService.execute(() -> {
                while (!SharedPrefUtils.INSTANCE.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, false)) {
                    try {
                        Thread.sleep(1000); // Wait for 1 second before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                Log.d("FileObserverService", "WP Permission Granted");

                String localPath = SharedPrefKeys.getWhatsappDirectoryAdress10p().getAbsolutePath();
                Log.d("FileObserverService", "Path: " + localPath);

                // Initialize and start the FileObserver
                fileObserver = new FolderFileObserver(FileObserverService.this, localPath);
                fileObserver.startWatching();
            });

            executorService.execute(() -> {
                while (!SharedPrefUtils.INSTANCE.getPrefBoolean(SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED, false)) {
                    try {
                        Thread.sleep(1000); // Wait for 1 second before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                Log.d("FileObserverService", "WP Business Permission Granted");

                String localPath = SharedPrefKeys.getWhatsappBusinessDirectoryAdress10p().getAbsolutePath();
                Log.d("FileObserverService", "Path: " + localPath);

                // Initialize and start the FileObserver
                fileObserver = new FolderFileObserver(FileObserverService.this, localPath);
                fileObserver.startWatching();
            });
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Status Notification")
                .setContentText("Once you give us folder permission, we will notify you for new statuses.")
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


}