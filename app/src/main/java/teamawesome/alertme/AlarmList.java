package teamawesome.alertme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;

import teamawesome.alertme.Network.JSONWeatherParser;
import teamawesome.alertme.Network.WeatherHttpClient;
import teamawesome.alertme.Utility.AlarmDataSingleton;
import teamawesome.alertme.Utility.Weather;
import teamawesome.alertme.Utility.WeatherAlarm;


public class AlarmList extends ActionBarActivity {

    private TextView dataTemp;
    private TextView dataRain;
    private TextView dataWindSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

//        AlarmListAdapter alarmListAdapter = new AlarmListAdapter();
//        ListView alarmList = (ListView) findViewById(R.id.alarmListView);
//        alarmList.setAdapter(alarmListAdapter);

        dataTemp = (TextView) findViewById(R.id.dataTemp);
        dataRain = (TextView) findViewById(R.id.dataRain);
        dataWindSpeed = (TextView) findViewById(R.id.dataWindSpeed);

        String city = "Austin,TX";
        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{city});
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


    public class AlarmListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return AlarmDataSingleton.getInstance().size();
        }

        @Override
        public Object getItem(int position) {
            return AlarmDataSingleton.getInstance().getAlarm(position);
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
            WeatherAlarm currentAlarm = AlarmDataSingleton.getInstance().getAlarm(position);
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

        public void toConditions(View view) {
            Intent intent = new Intent(AlarmList.this, Conditions.class);
            intent.putExtra("alarmIndex", 0);
            startActivity(intent);
        }

        public void toTimeFrame(View view) {
            Intent intent = new Intent(AlarmList.this, TimeFrame.class);
            intent.putExtra("alarmIndex", 0);
            startActivity(intent);
        }

    }


    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            AlarmDataSingleton.getInstance().setWeather(weather, AlarmList.this);

            SharedPreferences currentWeatherData = getSharedPreferences("weather_data", MODE_PRIVATE);
            dataTemp.setText("Temperature: " + currentWeatherData.getFloat("currentWeatherTemperature", 0.0f) + "F");
            dataRain.setText("Precipitation: " + currentWeatherData.getFloat("currentWeatherPrecipitation", 0.0f) + "%");
            dataWindSpeed.setText("Wind Speed: " + currentWeatherData.getFloat("currentWeatherWindSpeed", 0.0f) + "mph");
        }
    }
}
