package com.tyrel.wgu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


public class BroadcastReceiverClass extends BroadcastReceiver {
    private String CHANNEL_ID = "notification_channel";
    private String CHANNEL_DESC = "WGU Notification Channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);

        mBuilder.setSmallIcon(R.drawable.ic_document);
        mBuilder.setContentTitle("WGU");
        mBuilder.setContentText("Course or Assessment Notification");
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_DESC, NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(channel);

        mNotificationManager.notify(0, mBuilder.build());
    }
}
