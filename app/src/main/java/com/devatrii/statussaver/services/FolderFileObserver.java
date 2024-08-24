package com.devatrii.statussaver.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.FileObserver;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.devatrii.statussaver.R;
import com.devatrii.statussaver.views.activities.MainActivity;

public class FolderFileObserver extends FileObserver {
    private Context context;

    public FolderFileObserver(Context context, String path) {
        super(path, FileObserver.CREATE);
        this.context = context;
    }

    @Override
    public void onEvent(int event, String path) {
        if (event == FileObserver.CREATE && path != null) {
            Log.d("FolderFileObserver", "New file created: " + path);
            sendNotification(
                    this.context,
                    "New Status Available",
                    "Click here to download new status"
            );
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
            channel.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null); // Default sound
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build());
    }

}
