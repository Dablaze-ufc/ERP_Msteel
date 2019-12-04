package example.com.erp.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import example.com.erp.R;
import example.com.erp.activity.ActHandlePushService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    PendingIntent resultPendingIntent = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {

            Intent intent = new Intent(getApplicationContext(), ActHandlePushService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("JsonData", new Gson().toJson(remoteMessage.getData()));
            String type = remoteMessage.getData().get("type");
            String image = remoteMessage.getData().get("image");
            String title = remoteMessage.getData().get("title");
            String descriprition = remoteMessage.getData().get("descriprition");
            String message = remoteMessage.getData().get("message");
            intent.putExtra("Intent_received", type);

            //{"type":"3","image":"","title":"test","descriprition":"","message":"message"}

            if (image.equals("") || TextUtils.isEmpty(image)) {//load normal notification...
                showNormalNotifications(title, message, intent);
                //playNotificationSound();
            } else {//load image notification...
                showNotificationWithImage(title, message, image, descriprition, intent);
                //playNotificationSound();
            }
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String title, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, getResources().getString(R.string.default_notification_channel_id));
        mBuilder.setSmallIcon(R.drawable.ic_stat_icon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setBadgeIconType(getBadgeIcon())
                .setChannelId(getResources().getString(R.string.default_notification_channel_id))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setWhen(Calendar.getInstance().getTimeInMillis())
                .setContentText(message);
        return mBuilder;
    }

    void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher_round : R.drawable.ic_stat_icon;
    }

    private int getBadgeIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher_round;
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayNotification(Notification notification) {
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(getResources().getString(R.string.default_notification_channel_id), getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(1985, notification);
        }
    }

    private void showNormalNotifications(String title, String message, @Nullable Intent intent) {
        if (TextUtils.isEmpty(message) && TextUtils.isEmpty(title))
            return;

        if (intent != null) {
            resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, getResources().getString(R.string.default_notification_channel_id));
        mBuilder.setSmallIcon(R.drawable.ic_stat_icon)
                .setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setChannelId(getResources().getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_stat_icon)
                .setContentText(message);

        if (resultPendingIntent != null) {
            mBuilder.setContentIntent(resultPendingIntent);
        }

        displayNotification(mBuilder.build());
    }

    private void showNotificationWithImage(String title, String message, String imageUrl, String imageDescription, @Nullable Intent intent) {
        if (TextUtils.isEmpty(imageUrl))
            return;

        if (intent != null) {
            resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        Bitmap defaultIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = getNotificationBuilder(title, message);
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(getBitmapFromURL(imageUrl) != null ? getBitmapFromURL(imageUrl) : defaultIcon);
        bigPictureStyle.setSummaryText(imageDescription);

        mBuilder
                .setStyle(bigPictureStyle)
                .setLargeIcon(getBitmapFromURL(imageUrl) != null ? getBitmapFromURL(imageUrl) : defaultIcon)
                .setContentText(message);

        if (resultPendingIntent != null) {
            mBuilder.setContentIntent(resultPendingIntent);
        }
        displayNotification(mBuilder.build());
    }

}