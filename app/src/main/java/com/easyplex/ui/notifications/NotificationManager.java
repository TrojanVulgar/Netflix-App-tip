package com.easyplex.ui.notifications;


import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.easyplex.R;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.util.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


/**
 * EasyPlex - Android Movie Portal App
 * @package     EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2020 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


public class NotificationManager extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "CHANNEL_ID";


    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Timber.d(remoteMessage.getFrom());


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            String clickAction = remoteMessage.getNotification().getClickAction();
            if (clickAction != null) {
                sendNotification(title, message,clickAction);
            }
        }
    }


    // param Notification Tite,messageBody,Type FCM message body received.
    private void sendNotification (String title, String messageBody,String clickAction) {

        Intent intent = new Intent();

        if ("MOVIE".equals(clickAction)) {
            intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(Constants.MOVIE_ID, clickAction);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if ("SERIE".equals(clickAction)) {

            intent = new Intent(this, SerieDetailsActivity.class);
            intent.putExtra(Constants.MOVIE_ID, clickAction);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if ("EASYPLEX".equals(clickAction)) {
            intent = new Intent(this, BaseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }



        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_smal_size)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "EasyPlex",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
