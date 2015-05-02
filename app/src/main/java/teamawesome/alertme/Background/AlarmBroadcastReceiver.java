package teamawesome.alertme.Background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

import teamawesome.alertme.AlertMeApplication;
import teamawesome.alertme.PopupAlarm;


public class AlarmBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                "AlarmMe weather alarm");


        // TODO: ADD IF STATEMENT TO CHECK CONDITIONS AGAINST WEATHER
        SharedPreferences currentWeatherData = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE);

        wakeLock.acquire();

        Intent home = new Intent(context, PopupAlarm.class);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);
        Toast.makeText(context, "Alarm went off!!!", Toast.LENGTH_LONG).show();

        wakeLock.release();
    }

    public static void setAlarm(int id, long time) {
        Context appContext = AlertMeApplication.getContext();
        AlarmManager alarmManager = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(appContext, AlarmBroadcastReceiver.class);
        PendingIntent intentBroadcast = PendingIntent.getBroadcast(appContext, id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, intentBroadcast);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, intentBroadcast);
        }
    }

    public static void cancelAlarm(int id) {
        Context appContext = AlertMeApplication.getContext();
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(appContext, AlarmBroadcastReceiver.class);
        PendingIntent intentBroadcast = PendingIntent.getBroadcast(appContext, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(intentBroadcast);
    }

}
