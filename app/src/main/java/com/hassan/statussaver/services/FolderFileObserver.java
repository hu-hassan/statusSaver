package com.hassan.statussaver.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.documentfile.provider.DocumentFile;

import com.hassan.statussaver.R;
import com.hassan.statussaver.utils.SharedPrefKeys;
import com.hassan.statussaver.utils.SharedPrefUtils;
import com.hassan.statussaver.views.activities.MainActivity;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FolderFileObserver extends FileObserver {
    private Context context;
    private String folderPath;
    private Handler handler;
    private long checkInterval = 1000L; // Check every 5 seconds
    private Map<String, Long> fileMap;
    private boolean isInitialLoad = true;
    ExecutorService executor = Executors.newSingleThreadExecutor();




    public FolderFileObserver(Context context, String path) {
        super(path, FileObserver.ALL_EVENTS);
        this.context = context;
        this.folderPath = path;
        this.handler = new Handler(Looper.getMainLooper());
        this.fileMap = new HashMap<>();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            Log.d("Android 10 checker started", "Checking android 10 statuses" );
            startWatching();
            startPeriodicCheck();
        }
    }

    @Override
    public void onEvent(int event, String path) {
        if (path != null) {
            if (event == FileObserver.CREATE || event == FileObserver.MODIFY) {
                Log.d("FolderFileObserver", "New file created or written: " + path);
                sendNotification(
                        this.context,
                        "New Status Available",
                        "Click here to download new status"
                );
            }
        }
    }

    private void startPeriodicCheck() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.d("Android 10 checker started", "Checking android 10 statuses" );
                checkForModifications();
                handler.postDelayed(this, checkInterval);
            }
        }, checkInterval);
    }

//    private void checkForModifications() {
//        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
//            Uri folderUri = Uri.parse(SharedPrefUtils.INSTANCE.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, ""));
////            Log.d("Uritest", "Checking for modifications in folder: " + folderUri);
//
//                try {
//                    DocumentFile folder = DocumentFile.fromTreeUri(context, folderUri);
//                    if (folder != null && folder.isDirectory()) {
//                        Log.d("Uritest", "Folder exists and is a directory");
//                        for (DocumentFile file : folder.listFiles()) {
//                            long lastModified = file.lastModified();
//                            if (fileMap.containsKey(file.getName())) {
//                                if (!fileMap.get(file.getName()).equals(lastModified)) {
//                                    fileMap.put(file.getName(), lastModified);
//                                    Log.d("New File", "New file created or written: " + file.getName());
//                                    sendNotification(context, "New Status Available", "Click here to download new status");
//                                }
//                            } else {
//                                fileMap.put(file.getName(), lastModified);
//                                if (!isInitialLoad) {
//                                    Log.d("New File", "New file created or written: " + file.getName());
//                                    sendNotification(context, "New Status Available", "Click here to download new status");
//                                }
//                            }
//                        }
//                        isInitialLoad = false;
//                    } else {
//                        Log.e("Folder not exist", "Folder does not exist or is not a directory");
//                    }
//                } catch (Exception e) {
//                    Log.d("Uritest", "Error: " + e.getMessage());
//                }
//        }
//        else {
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//                File folder = new File(folderPath);
//                if (folder.exists() && folder.isDirectory()) {
//                    File[] files = folder.listFiles();
//                    if (files != null) {
//                        for (File file : files) {
//                            long lastModified = file.lastModified();
//                            if (fileMap.containsKey(file.getName())) {
//                                if (!fileMap.get(file.getName()).equals(lastModified)) {
//                                    fileMap.put(file.getName(), lastModified);
//                                    sendNotification(context, "New Status Available", "Click here to download new status");
//                                }
//                            } else {
//                                fileMap.put(file.getName(), lastModified);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void checkForModifications() {
//        executor.execute(() -> {
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//                Uri folderUri = Uri.parse(SharedPrefUtils.INSTANCE.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, ""));
//                try {
//                    DocumentFile folder = DocumentFile.fromTreeUri(context, folderUri);
//                    if (folder != null && folder.isDirectory()) {
//                        Log.d("Uritest", "Folder exists and is a directory");
//                        for (DocumentFile file : folder.listFiles()) {
//                            long lastModified = file.lastModified();
//                            if (fileMap.containsKey(file.getName())) {
//                                if (!fileMap.get(file.getName()).equals(lastModified)) {
//                                    fileMap.put(file.getName(), lastModified);
//                                    Log.d("New File", "New file created or written: " + file.getName());
//                                    sendNotification(context, "New Status Available", "Click here to download new status");
//                                }
//                            } else {
//                                fileMap.put(file.getName(), lastModified);
//                                if (!isInitialLoad) {
//                                    Log.d("New File", "New file created or written: " + file.getName());
//                                    sendNotification(context, "New Status Available", "Click here to download new status");
//                                }
//                            }
//                        }
//                        isInitialLoad = false;
//                    } else {
//                        Log.e("Folder not exist", "Folder does not exist or is not a directory");
//                    }
//                } catch (Exception e) {
//                    Log.d("Uritest", "Error: " + e.getMessage());
//                }
//            }
//            else {
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//                    File folder = new File(folderPath);
//                    if (folder.exists() && folder.isDirectory()) {
//                        File[] files = folder.listFiles();
//                        if (files != null) {
//                            for (File file : files) {
//                                long lastModified = file.lastModified();
//                                if (fileMap.containsKey(file.getName())) {
//                                    if (!fileMap.get(file.getName()).equals(lastModified)) {
//                                        fileMap.put(file.getName(), lastModified);
//                                        sendNotification(context, "New Status Available", "Click here to download new status");
//                                    }
//                                } else {
//                                    fileMap.put(file.getName(), lastModified);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        });
//    }
// Global variables
private static long lastNotificationTime = 0;
    private static final long DEBOUNCE_DURATION = 2000; // 1 minute in milliseconds
//    private boolean isInitialLoad = true; // Ensure initial load check

    // Load fileMap from SharedPreferences
    private Map<String, Long> loadFileMap(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("filePrefs", Context.MODE_PRIVATE);
        Map<String, Long> fileMap = new HashMap<>();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            fileMap.put(entry.getKey(), (Long) entry.getValue());
        }
        return fileMap;
    }

    // Save fileMap to SharedPreferences
    private void saveFileMap(Context context, Map<String, Long> fileMap) {
        SharedPreferences prefs = context.getSharedPreferences("filePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (Map.Entry<String, Long> entry : fileMap.entrySet()) {
            editor.putLong(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    // Main method to check for file modifications
    private void checkForModifications() {
        executor.execute(() -> {
            // Load cached fileMap
            Map<String, Long> fileMap = loadFileMap(context);

            long currentTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                Uri folderUri1= Uri.parse(SharedPrefUtils.INSTANCE.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, ""));
                DocumentFile folder1 = DocumentFile.fromTreeUri(context, folderUri1);
                Uri folderUri = null;
                try {
                    if (folderPath.equals(SharedPrefKeys.getWhatsappDirectoryAdress10m().getAbsolutePath())) {
                        Log.d("FolderPath", "checkForModifications: " + folderPath);
                        folderUri = folder1.findFile("WhatsApp").findFile("Media").findFile(".Statuses").getUri();
                    } else if (folderPath.equals(SharedPrefKeys.getWhatsappDirectoryAdress10m().getAbsolutePath())) {
                        Log.d("FolderPath", "checkForModifications: " + folderPath);
                        folderUri = folder1.findFile("WhatsApp Business").findFile("Media").findFile(".Statuses").getUri();
                    } else if (folderPath.equals(SharedPrefKeys.getWhatsappDirectoryAdress10p().getAbsolutePath())) {
                        Log.d("FolderPath", "checkForModifications: " + folderPath);
                        folderUri = folder1.findFile("com.whatsapp").findFile("WhatsApp").findFile("Media").findFile(".Statuses").getUri();
                    } else {
                        Log.d("FolderPath", "checkForModifications: " + folderPath);
                        folderUri = folder1.findFile("com.whatsapp.w4b").findFile("WhatsApp Business").findFile("Media").findFile(".Statuses").getUri();
                    }
                } catch (NullPointerException e) {
                    try{
                        if (folderPath.equals(SharedPrefKeys.getWhatsappDirectoryAdress10m().getAbsolutePath())) {
                            Log.d("FolderPath", "checkForModifications: " + folderPath);
                            folderUri = folder1.findFile("Android").findFile("media").findFile("com.whatsapp").findFile("WhatsApp").findFile("Media").findFile(".Statuses").getUri();
                        }
                        else{
                            folderUri = folder1.findFile("Android").findFile("media").findFile("com.whatsapp.w4b").findFile("WhatsApp Business").findFile("Media").findFile(".Statuses").getUri();

                        }
                    }catch (NullPointerException e2) {
                        Log.e("FolderPath", "NullPointerException occurred: " + e2.getMessage());
                        folderUri = null;
                    }
                }
                try {
                    DocumentFile folder = DocumentFile.fromTreeUri(context, folderUri);

                    if (folder != null && folder.isDirectory()) {
                        Log.d("Uritest", "Folder exists and is a directory");
                        for (DocumentFile file : folder.listFiles()) {
                            long lastModified = file.lastModified();
                            handleFileModification(context, file.getName(), lastModified, fileMap, currentTime);
                        }
                        isInitialLoad = false; // Mark initial load as complete
                    } else {
                        Log.e("Folder not exist", "Folder does not exist or is not a directory");
                    }
                } catch (Exception e) {
                    Log.d("Uritest", "Error: " + e.getMessage());
                }
            } else {
                File folder = new File(folderPath);
                if (folder.exists() && folder.isDirectory()) {
                    File[] files = folder.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            long lastModified = file.lastModified();
                            handleFileModification(context, file.getName(), lastModified, fileMap, currentTime);
                        }
                    }
                }
            }

            // Cache updated fileMap after processing
            saveFileMap(context, fileMap);
        });
    }

    // Helper method to handle file modification check and notification debouncing
    private void handleFileModification(Context context, String fileName, long lastModified,
                                        Map<String, Long> fileMap, long currentTime) {
        if (fileMap.containsKey(fileName)) {
            if (!fileMap.get(fileName).equals(lastModified)) {
                fileMap.put(fileName, lastModified);
                if (currentTime - lastNotificationTime > DEBOUNCE_DURATION) {
                    sendNotification(context, "New Status Available", "Click here to download new status");
                    lastNotificationTime = currentTime; // Update last notification time
                    Log.d("New File", "Updated file: " + fileName);
                }
            }
        } else {
            fileMap.put(fileName, lastModified);
            if (!isInitialLoad && currentTime - lastNotificationTime > DEBOUNCE_DURATION) {
                sendNotification(context, "New Status Available", "Click here to download new status");
                lastNotificationTime = currentTime; // Update last notification time
                Log.d("New File", "New file created or written: " + fileName);
            }
        }
    }



    public static void sendNotification(Context context, String title, String message) {
        Log.d("TAG", "sendNotification: " + title + " " + message);

        // Create an intent for the notification tap action
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Define the notification channel ID
        String channelId = "default_channel";

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_download)  // Replace with your own icon
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for sound/vibration/alerts
                .setDefaults(NotificationCompat.DEFAULT_ALL); // Enable sound, vibration, and lights

        // Get the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android Oreo and above, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH // High importance for sound/vibration/alerts
            );
            channel.enableVibration(true); // Enable vibration
            channel.setVibrationPattern(new long[]{0, 500, 500, 500}); // Custom vibration pattern
            channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null); // Default sound
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}