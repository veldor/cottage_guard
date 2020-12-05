package net.veldor.cottage_guard.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import net.veldor.cottage_guard.App;
import net.veldor.cottage_guard.MainActivity;
import net.veldor.cottage_guard.R;
import net.veldor.cottage_guard.selections.DefenceAlert;

import java.util.Locale;


public class MyNotify {
    private static final String CHECKER_CHANNEL_ID = "checker";
    private static final String DEFENCE_STATE_CNANGED_CHANNEL_ID = "congrats";
    private static final String ALERT_RECEIVED_CHANNEL_ID = "alert received";
    public static final int CHECKER_NOTIFICATION = 3;
    public final NotificationManager mNotificationManager;
    private final App mContext;
    private int mLastNotificationId = 100;

    public MyNotify() {
        mContext = App.getInstance();
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // создам каналы уведомлений
        createChannels();
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                // создам канал статического уведомления для фонового получения данных
                NotificationChannel nc = new NotificationChannel(CHECKER_CHANNEL_ID, mContext.getString(R.string.checker_channel_description), NotificationManager.IMPORTANCE_DEFAULT);
                nc.setDescription(mContext.getString(R.string.checker_channel_description));
                nc.enableLights(false);
                nc.setSound(null, null);
                nc.enableVibration(false);
                mNotificationManager.createNotificationChannel(nc);

                // создам канал уведомлений о срабатывании защиты
                nc = new NotificationChannel(ALERT_RECEIVED_CHANNEL_ID, mContext.getString(R.string.alert_received_channel_description), NotificationManager.IMPORTANCE_HIGH);
                nc.setDescription(mContext.getString(R.string.alert_received_channel_description));
                nc.enableLights(true);
                nc.setLightColor(Color.RED);
                nc.enableVibration(true);
                mNotificationManager.createNotificationChannel(nc);

            }
        }
    }

    public Notification createCheckingNotification() {
        Intent notificationIntent = new Intent(App.getInstance(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(App.getInstance(),
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder checkerNotification = new NotificationCompat.Builder(mContext, CHECKER_CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mContext.getString(R.string.checker_message)))
                .setContentTitle(null)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_defence)
                .setOngoing(true);
        Notification notification = checkerNotification.build();
        mNotificationManager.notify(CHECKER_NOTIFICATION, notification);
        return notification;
    }

    public void showAlertNotification(DefenceAlert alert) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, ALERT_RECEIVED_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_defence)
                .setContentTitle(String.format(Locale.ENGLISH, mContext.getString(R.string.alert_received_title), alert.cottageNumber))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(String.format(Locale.ENGLISH, "contact state is %s, called in %s", alert.pinStatus, alert.actionTime)))
                .setColor(Color.RED)
                .setAutoCancel(true);
        Notification notification = notificationBuilder.build();
        mNotificationManager.notify(mLastNotificationId, notification);
        mLastNotificationId++;
    }
}
