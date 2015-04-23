package teamawesome.alertme.Background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

import teamawesome.alertme.PopupAlarm;


public class AlarmBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                "AlarmMe weather alarm");


        // TODO: ADD IF STATEMENT TO CHECK CONDITIONS AGAINST WEATHER
        wakeLock.acquire();

        // Put here YOUR code.
        Intent home = new Intent(context, PopupAlarm.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);
        Toast.makeText(context, "Alarm went off!!!", Toast.LENGTH_LONG).show();

        wakeLock.release();
    }

    public static void setAlarm(Context context, int id, long time) {
        AlarmManager alarmManager =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent intentBroadcast = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, intentBroadcast);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, intentBroadcast);
        }
    }

    public static void cancelAlarm(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent intentBroadcast = PendingIntent.getBroadcast(context, id, alarmIntent, 0);

        alarmManager.cancel(intentBroadcast);
    }

}