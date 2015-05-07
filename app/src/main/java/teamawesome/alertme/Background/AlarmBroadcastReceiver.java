package teamawesome.alertme.Background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;

import teamawesome.alertme.AlertMeApplication;
import teamawesome.alertme.PopupAlarm;


public class AlarmBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                "AlarmMe weather alarm");


        int alarmIndex = intent.getIntExtra("alarmIndex", -1);
        SharedPreferences alarmInfo = context.getSharedPreferences("alert_me_alarm_" + alarmIndex, Context.MODE_PRIVATE);
        SharedPreferences currentWeatherData = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE);

        boolean exceedsTemperatureRange;
        if (alarmInfo.getBoolean("degreesF", true)) {
            exceedsTemperatureRange =
                    (currentWeatherData.getInt("tomorrowMinTemperatureF", -500) <= alarmInfo.getInt("temperature_min", 0)) ||
                            (currentWeatherData.getInt("tomorrowMaxTemperatureF", 500) >= alarmInfo.getInt("temperature_max", 0));
        } else {
            exceedsTemperatureRange =
                    (currentWeatherData.getInt("tomorrowMinTemperatureC", -500) <= alarmInfo.getInt("temperature_min", 0)) ||
                            (currentWeatherData.getInt("tomorrowMaxTemperatureC", 500) >= alarmInfo.getInt("temperature_max", 0));
        }

        boolean exceedsPrecipitationCondition = currentWeatherData.getInt("tomorrowPrecipitationChance", 100) >= alarmInfo.getInt("precipitation", 0);

        boolean exceedsWindSpeedCondition;
        if (alarmInfo.getBoolean("mph", true)) {
            exceedsWindSpeedCondition = currentWeatherData.getInt("tomorrowWindSpeedMph", 100) >= alarmInfo.getInt("windSpeed", 0);
        } else {
            exceedsWindSpeedCondition = currentWeatherData.getInt("tomorrowWindSpeedKph", 100) >= alarmInfo.getInt("windSpeed", 0);
        }

        if (exceedsTemperatureRange || exceedsPrecipitationCondition || exceedsWindSpeedCondition) {
            wakeLock.acquire();

            // TODO: all debugging stuff
            Intent popupAlarm = new Intent(context, PopupAlarm.class);
            popupAlarm.putExtra("a", alarmInfo.getInt("temperature_max", 0));
            popupAlarm.putExtra("b", currentWeatherData.getInt("tomorrowMaxTemperatureF", 500));
            popupAlarm.putExtra("c", alarmInfo.getInt("precipitation", -1));
            popupAlarm.putExtra("d", currentWeatherData.getInt("tomorrowPrecipitationChance", 100));

            popupAlarm.putExtra("alarmIndex", alarmIndex);
            popupAlarm.putExtra("exceedsTemperatureRange", exceedsTemperatureRange);
            popupAlarm.putExtra("exceedsPrecipitationCondition", exceedsPrecipitationCondition);
            popupAlarm.putExtra("exceedsWindSpeedCondition", exceedsWindSpeedCondition);
            popupAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(popupAlarm);

            wakeLock.release();
        }
    }

    public static void setAlarm(int id, long time, int alarmIndex) {
        Context appContext = AlertMeApplication.getContext();
        AlarmManager alarmManager = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(appContext, AlarmBroadcastReceiver.class);
        alarmIntent.putExtra("alarmIndex", alarmIndex);
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
