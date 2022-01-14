package com.arge.correosm.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.arge.correosm.R;

public class NotficationHealper extends ContextWrapper {

    private static final String CHANNEL_id = "com.arge.correosm";
    private static final String CHANNEL_name = "CorreoSM";

    private NotificationManager manager;

    public NotficationHealper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void  createChannels(){
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_id, CHANNEL_name, NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.DKGRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if(manager ==  null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotificaion(String title, String body, PendingIntent intent, Uri soundUri){
        return new Notification.Builder(getApplicationContext(),CHANNEL_id)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_run);
    }

    public NotificationCompat.Builder getBotificaionOldAPI(String title, String body, PendingIntent intent, Uri soundUri){
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_id)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_run);
    }
}














