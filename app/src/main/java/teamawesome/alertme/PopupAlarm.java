package teamawesome.alertme;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;


public class PopupAlarm extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_alarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        TextView dataTemp = (TextView) findViewById(R.id.dataTemp);
        TextView dataRain = (TextView) findViewById(R.id.dataRain);
        TextView dataWindSpeed = (TextView) findViewById(R.id.dataWindSpeed);

        SharedPreferences currentWeatherData = getSharedPreferences("weather_data", MODE_PRIVATE);
        dataTemp.setText("Temperature: " + currentWeatherData.getInt("tomorrowMinTemperatureF", -1) + "\u00b0F");
        dataRain.setText("Precipitation: " + currentWeatherData.getInt("tomorrowPrecipitationChance", -1) + "%");
        dataWindSpeed.setText("Wind Speed: " + currentWeatherData.getInt("tomorrowWindSpeedMph", -1) + "mph");
    }

}
