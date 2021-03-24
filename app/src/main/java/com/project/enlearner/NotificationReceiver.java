package com.project.enlearner;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver
{
    private final int notificationId = 100;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeatingIntent = new Intent(context, MainActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            String CHANNEL_ID = "channel_01";
            CharSequence name = "channel";
            String Description = "notification channel 01";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.R.drawable.arrow_up_float)
                    .setVibrate(new long[] { 1000, 1000 })
                    .setContentTitle("You've got new word to learn.")
                    .setContentText("Check it out!")
                    .setAutoCancel(true);

            notificationManager.notify(notificationId, builder.build());
        }
        else
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.R.drawable.arrow_up_float)
                    .setContentTitle("You've got new word to learn.")
                    .setContentText("Check this out!")
                    .setAutoCancel(true);

            notificationManager.notify(notificationId, builder.build());
        }
    }
}
