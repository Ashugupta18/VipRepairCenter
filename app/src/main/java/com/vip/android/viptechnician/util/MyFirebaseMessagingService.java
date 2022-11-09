package com.vip.android.viptechnician.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vip.android.viptechnician.MainActivity;
import com.vip.android.viptechnician.NotificationUtils;
import com.vip.android.viptechnician.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    static String TAG = "FCM";
    NotificationManager mNotifyManager;
    Intent notificationIntent;
    @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().toString());

        String message = remoteMessage.getData().toString();
        String replace = message.replace("{", "").replace("}", "").replace("message=", "");

        Log.e(TAG, "onMessageReceivedgh: " + message);
        // Log.e(TAG, "onMessageReceivedga: "+remoteMessage.getNotification().getBody() );


        try {


            if (!remoteMessage.getData().toString().equals("{message=UT}")) {

                handleNotification(replace);

                try {
//                Intent intent = new Intent(this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//                String channelId = "Default";
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("test")
//                        .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
//                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
//                    manager.createNotificationChannel(channel);
//
//                }
//                manager.notify(0, builder.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }





        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleNotification(String message) {

        //Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
        //pushNotification.putExtra("message", message);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();

        //NotificationUtils notificationUtils1 = new NotificationUtils(getApplicationContext());
        //notificationUtils1.showNotificationMessage(message);
        //Log.e(TAG, "handleNotificationdd: " + message);


        //New notification  manager to support android 8 and above
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = (int) System.currentTimeMillis();
        String channelId = "vtech_channel";
        String channelName = "VIPTECH";
        int importance;

        //Greater than or equal to Android Oreo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            notificationManager.createNotificationChannel(mChannel);
        }


        if (!message.equals("")) {
            notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.rounded_calendar_item)
                .setContentTitle("VIPREPAIRCENTRE")
                .setContentText(message)
                //.setSound(sound)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, mBuilder.build());


    }
}
