package com.laundry.qrtest;

import android.app.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.laundry.qrtest.Petugas.HomePetugas;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New Token: " + token);
        // Kirim token ke server jika diperlukan
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Log data yang diterima
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Tampilkan notifikasi
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "default_channel";

        Uri customSoundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/raw/call");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
        PendingIntent pendingIntent;
        if (sharedPrefManager.getStatus().equals("user")){
            Intent intent = new Intent(this, ListChat.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            Intent intent = new Intent(this, HomePetugas.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.nurse2)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(customSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(0, builder.build());
    }
}

