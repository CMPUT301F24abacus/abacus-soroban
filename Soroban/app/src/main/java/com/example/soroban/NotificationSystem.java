package com.example.soroban;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/**
 * Handles set-up of Android notifications.
 * Provides functionality for creating and scheduling notifications.
 * @Author: Matthieu Larochelle
 * @Version: 2.0
 */
public class NotificationSystem{
    private Context context;
    private final String channelID = "Soroban";
    private final String channelName = "Soroban";
    private final int notificationIcon = R.drawable.ic_notifications;
    private NotificationChannel channel;
    private NotificationManager manager;

    /**
     * Constructs a new NotificationSystem with the provided context.
     * Initializes the notification manager and creates a notification channel.
     *
     * @param context the application context for notification management.
     * @see NotificationManager
     * @see NotificationChannel
     */
    public NotificationSystem(Context context) {
        this.context = context;
        manager = context.getSystemService(NotificationManager.class);
        // Creation of notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Handles creation of an Android notification.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param context Context
     * @see Notification
     */
    private android.app.Notification createAndroidNotification(Context context, String title, String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelName);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(notificationIcon);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setChannelId(channelID);


        return builder.build();
    }

    /**
     * Handles scheduling of a notification through the use of the alarm manager.
     * Additional parameters form the content of the notification.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param notificationID Id of the notification that will be dispatched
     * @param title Title of the notification
     * @param text Text of the body of the notification
     */
    public void setNotification(int notificationID, String title, String text){
        Notification newNotification = createAndroidNotification(context, title, text);
        manager.notify(notificationID,newNotification);
    }

}
