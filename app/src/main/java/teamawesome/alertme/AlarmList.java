package teamawesome.alertme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;

import java.util.GregorianCalendar;

import teamawesome.alertme.Background.AlarmBroadcastReceiver;
import teamawesome.alertme.Network.JSONWeatherParser;
import teamawesome.alertme.Network.WeatherHttpClient;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;
import teamawesome.alertme.Utility.WeatherForecastData;
import teamawesome.alertme.Utility.AlertMeAlarm;


public class AlarmList extends ActionBarActivity {

    private TextView dataTemp;
    private TextView dataRain;
    private TextView dataWindSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list_2);

        AlarmListAdapter alarmListAdapter = new AlarmListAdapter();
        ListView alarmList = (ListView) findViewById(R.id.alarmListView);
        alarmList.setAdapter(alarmListAdapter);

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

//        Long alarmTime = new GregorianCalendar().getTimeInMillis() + 10 * 1000;
//        scheduleAlarm(alarmTime);
    }

    public void toConditions(View view) {
        Intent intent = new Intent(this, Conditions.class);
        intent.putExtra("alarmIndex", 0);
        startActivity(intent);
    }

    public void toTimeFrame(View view) {
        Intent intent = new Intent(this, TimeFrame.class);
        intent.putExtra("alarmIndex", 0);
        startActivity(intent);
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

    private void scheduleAlarm(long time) {
        AlarmBroadcastReceiver alarmMaker = new AlarmBroadcastReceiver();
        alarmMaker.setAlarm(this, 0, time);
        Toast.makeText(this, "Alarm Scheduled for 10 seconds", Toast.LENGTH_LONG).show();
    }


    public class AlarmListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return AlertMeMetadataSingleton.getInstance().size();
        }

        @Override
        public Object getItem(int position) {
            return AlertMeMetadataSingleton.getInstance().getAlarm(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) AlarmList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.alarm_list_item, parent, false);
            }

            TextView alarmName = (TextView) convertView.findViewById(R.id.alarmName);
            AlertMeAlarm currentAlarm = AlertMeMetadataSingleton.getInstance().getAlarm(position);
            alarmName.setText(currentAlarm.getName());
            alarmName.setTextSize(20);

            ToggleButton alarmToggle = (ToggleButton) convertView.findViewById(R.id.alarmToggle);
            alarmToggle.setOnClickListener(alarmToggleListener);

            return convertView;
        }

        private ToggleButton.OnClickListener alarmToggleListener = new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ToggleButton) view).toggle();
            }
        };
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
            AlertMeMetadataSingleton.getInstance().setWeather(weather, AlarmList.this);

            SharedPreferences currentWeatherData = getSharedPreferences("weather_data", MODE_PRIVATE);
            dataTemp.setText("Temperature: " + currentWeatherData.getInt("tomorrowMinTemperatureF", -1) + "\u00b0F");
            dataRain.setText("Precipitation: " + currentWeatherData.getInt("tomorrowPrecipitationChance", -1) + "%");
            dataWindSpeed.setText("Wind Speed: " + currentWeatherData.getInt("tomorrowWindSpeedMph", -1) + "mph");
        }
    }
}
