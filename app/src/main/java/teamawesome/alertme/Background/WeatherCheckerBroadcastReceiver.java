package teamawesome.alertme.Background;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import org.json.JSONException;

import teamawesome.alertme.Network.JSONWeatherParser;
import teamawesome.alertme.Network.WeatherHttpClient;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;
import teamawesome.alertme.Utility.WeatherForecastData;

public class WeatherCheckerBroadcastReceiver extends BroadcastReceiver {

    Context receivedContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        receivedContext = context;
        PowerManager powerManager = (PowerManager) receivedContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "AlarmMe weather checker");

        wakeLock.acquire();

        String city = "Austin,TX";
        if (isOnline(receivedContext)) {
            JSONWeatherTask task = new JSONWeatherTask();
            task.execute(city);
        }

        wakeLock.release();
    }

    public static void setWeatherChecker(Context context, long firstTime, long interval) {
        AlarmManager alarmManager =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent intentBroadcast = PendingIntent.getBroadcast(context, 1337, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstTime, interval, intentBroadcast);
    }

    public static void cancelWeatherChecker(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent intentBroadcast = PendingIntent.getBroadcast(context, 1337, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(intentBroadcast);
    }

    private boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        Log.d("AlarmList Wifi Check", "Wifi connected: " + isWifiConn);
        Log.d("AlarmList Mobile Check", "Mobile connected: " + isMobileConn);

        return isWifiConn || isMobileConn;
    }


    private class JSONWeatherTask extends AsyncTask<String, Void, WeatherForecastData> {

        @Override
        protected WeatherForecastData doInBackground(String... params) {
            WeatherForecastData weather = new WeatherForecastData();
            WeatherHttpClient weatherHttp = new WeatherHttpClient();
            String data = weatherHttp.getWeatherData(params[0]);

            try {
                weather = JSONWeatherParser.getWeather(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;
        }

        @Override
        protected void onPostExecute(WeatherForecastData weather) {
            super.onPostExecute(weather);
            AlertMeMetadataSingleton.getInstance().saveWeather(weather, receivedContext);
        }
    }

}
