package teamawesome.alertme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;


public class AlarmList extends ActionBarActivity {

    private TextView dataTemp;
    private TextView dataRain;
    private TextView dataWindSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        dataTemp = (TextView) findViewById(R.id.dataTemp);
        dataRain = (TextView) findViewById(R.id.dataRain);
        dataWindSpeed = (TextView) findViewById(R.id.dataWindSpeed);

        String city = "Austin,TX";
        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{city});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toConditions(View view){
        Intent intent = new Intent(this, Conditions.class);
        intent.putExtra("alarmIndex", 0);
        startActivity(intent);
    }

    public void toTimeFrame(View view){
        Intent intent = new Intent(this, TimeFrame.class);
        intent.putExtra("alarmIndex", 0);
        startActivity(intent);
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
            dataTemp.setText("" + currentWeatherData.getFloat("currentWeatherTemperature", 0.0f));
            dataRain.setText("" + currentWeatherData.getFloat("currentWeatherPrecipitation", 0.0f));
            dataWindSpeed.setText("" + currentWeatherData.getFloat("currentWeatherWindSpeed", 0.0f));
        }
    }
}
