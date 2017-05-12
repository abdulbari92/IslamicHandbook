package faizan.com.islamichandbook.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PrayerAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent alarmPlayingIntent = new Intent(context,AlarmPlayingService.class);
        Log.i("Abdul",intent.getExtras().getString("prayer"));
        alarmPlayingIntent.putExtra("prayer",intent.getExtras().getString("prayer"));
        context.startService(alarmPlayingIntent);
    }
}
