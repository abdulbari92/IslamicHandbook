package faizan.com.islamichandbook.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import faizan.com.islamichandbook.R;

public class AlarmPlayingService extends Service {

    MediaPlayer alarmPlayer;

    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";

    //Broadcast receiver for notification cancelled
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            alarmPlayer.stop();
            unregisterReceiver(this);
        }
    };

    public void showNotification(Context ctx, String text) {
        Intent intent = new Intent(NOTIFICATION_DELETED_ACTION);
        PendingIntent pendintIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));

        Notification n = new Notification.Builder(this).
                setContentText("It is Time for " + text)
                .setSmallIcon(R.mipmap.icon)
                .setDeleteIntent(pendintIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
    }
    public AlarmPlayingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String prayer = intent.getExtras().getString("prayer");
        alarmPlayer = MediaPlayer.create(this, R.raw.azaan);
        showNotification(this, intent.getExtras().getString("prayer"));
        alarmPlayer.start();
        return START_NOT_STICKY;
    }
}
