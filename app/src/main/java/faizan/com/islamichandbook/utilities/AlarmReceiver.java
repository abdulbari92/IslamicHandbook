package faizan.com.islamichandbook.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by buste on 4/19/2017.
 */

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent alarmServiceIntent = new Intent(context,AlarmSettingService.class);

        context.startService(alarmServiceIntent);
    }
}
