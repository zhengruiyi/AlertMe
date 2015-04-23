package teamawesome.alertme;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import teamawesome.alertme.Network.JSONWeatherParser;
import teamawesome.alertme.Network.WeatherHttpClient;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;
import teamawesome.alertme.Utility.WeatherForecastData;


public class PopupAlarm extends ActionBarActivity {

    private TextView dataTemp;
    private TextView dataRain;
    private TextView dataWindSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_alarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        dataTemp = (TextView) findViewById(R.id.dataTemp);
        dataRain = (TextView) findViewById(R.id.dataRain);
        dataWindSpeed = (TextView) findViewById(R.id.dataWindSpeed);

        String city = "Austin,TX";
        if (isOnline()) {
            JSONWeatherTask task = new JSONWeatherTask();
            task.execute(city);
        } else {
            String toastText = "Unable to connect to the network\r\nUsing cached weather data";
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

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
            AlertMeMetadataSingleton.getInstance().setWeather(weather, PopupAlarm.this);

            SharedPreferences currentWeatherData = getSharedPreferences("weather_data", MODE_PRIVATE);
            dataTemp.setText("Temperature: " + currentWeatherData.getInt("tomorrowMinTemperatureF", -1) + "\u00b0F");
            dataRain.setText("Precipitation: " + currentWeatherData.getInt("tomorrowPrecipitationChance", -1) + "%");
            dataWindSpeed.setText("Wind Speed: " + currentWeatherData.getInt("tomorrowWindSpeedMph", -1) + "mph");
        }
    }
}
