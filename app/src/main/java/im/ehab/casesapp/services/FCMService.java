package im.ehab.casesapp.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import im.ehab.casesapp.Contains;
import im.ehab.casesapp.R;
import im.ehab.casesapp.activitys.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(Contains.LOG+"fcm", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(Contains.LOG+"fcm", "Message data payload: " + remoteMessage.getData());

            if (remoteMessage.getData().get("notificationId") != null) {
                Log.d(Contains.LOG+"fcm", "remoteMessage.getData().get(notificationId != null");
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.d(Contains.LOG+"fcm", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT");
                    showNotification(Objects.requireNonNull(remoteMessage.getNotification()).getBody(),
                            Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("notificationId"))));
                }else{
                    Log.d(Contains.LOG+"fcm", "else  Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT");
                    showNotification(remoteMessage.getNotification().getBody(),
                            Integer.parseInt(remoteMessage.getData().get("notificationId")));
                }
            } else {
                Log.d(Contains.LOG+"fcm", "remoteMessage.getData().get(notificationId == null");
                // Handle message within 10 seconds
//                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(Contains.LOG+"fcm", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    private void showNotification(String message, int notifi_id) {
        Log.d(Contains.LOG+"fcm", "showNotification");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
        r.play();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel(manager);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FileDownload")
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#008577"))
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(notifi_id, builder.build());
        startForeground(1, builder.build());

    }

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager) {
        Log.d(Contains.LOG+"fcm", "createChannel");

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        String name = "FileDownload";
        String description = "Notifications for download status";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationChannel mChannel = new NotificationChannel(name, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        mChannel.setLightColor(Color.BLUE);
        mChannel.setSound(alarmSound,attributes);
        mChannel.enableVibration(true);
        notificationManager.createNotificationChannel(mChannel);
    }

    /*@Override
    public void onNewToken(String token) {
        Log.d(Contains.LOG+"refresh_token", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        APIInterface apiInterface = null;
        Call<ErrorBody> call = apiInterface.deviceToken(token);
        call.enqueue(new Callback<ErrorBody>() {
            @Override
            public void onResponse(Call<ErrorBody> call, Response<ErrorBody> response) {

                ErrorBody resource = response.body();

                String status = response.body().getStatus().getMessage();
                String code = response.code()+"";

                Log.d(Contains.LOG+"ErrorBody", "Status = "+status+" | Code = "+code);

                if (status.equals(Contains.message)) {

                    Log.d(Contains.LOG+"device_token", resource.getErrorData().getDeviceToken());

                }


            }

            @Override
            public void onFailure(Call<ErrorBody> call, Throwable t) {
                call.cancel();
            }
        });
    }*/
}
