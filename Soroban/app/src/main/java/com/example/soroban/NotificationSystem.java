package com.example.soroban;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

/**
 * Handles the scheduling and receiving of notifications.
 * @Author: Matthieu Larochelle
 * @Version: 1.0
 */
public class NotificationSystem extends BroadcastReceiver {
    private Context context;
    private final String channelID = "Soroban";
    private final String channelName = "Soroban";
    private final int notificationIcon = R.drawable.ic_notifications;
    private NotificationChannel channel;
    private NotificationManager manager;
    private AlarmManager alarmManager;

    /**
     * newInstance method to allow the passing of parameters, since NotificationSystem's constructor must remain parameterless.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     */
    public static NotificationSystem newInstance(AlarmManager alarmManager, Context context){
        NotificationSystem notificationSystem = new NotificationSystem();
        notificationSystem.setAlarmManager(alarmManager);
        notificationSystem.setContext(context);
        return notificationSystem;
    }

    /**
     * Setter method for the system's alarm manager.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param alarmManager Alarm manager.
     */
    public void setAlarmManager(AlarmManager alarmManager){
        this.alarmManager = alarmManager;
    }

    /**
     * Setter method for the system's context.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param context Context.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Overwritten method from BroadCast receiver.
     * Handles behaviour for when the notification is to be dispatched.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        this.manager = context.getSystemService(NotificationManager.class);
        manager.notify(intent.getIntExtra("id", 0),createAndroidNotification(context, intent.getStringExtra("title"),  intent.getStringExtra("text")));
    }

    /**
     * Handles creation of an Android notification.
     * @Author: Matthieu Larochelle
     * @Version: 1.0
     * @param context Context
     */
    private android.app.Notification createAndroidNotification(Context context, String title, String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelName);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(notificationIcon);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        // Creation of notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelID);
        }

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
     * @param time Time at which the notification will take place
     */
    public void setNotification(int notificationID, String title, String text, Calendar time){
        Intent intent = new Intent(this.context, NotificationSystem.class);
        Bundle args = new Bundle();
        args.putInt("id", notificationID);
        args.putString("title", title);
        args.putString("text", text);
        intent.putExtras(args);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),pendingIntent);
    }


}
