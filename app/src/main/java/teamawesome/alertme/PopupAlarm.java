package teamawesome.alertme;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.HashMap;

import teamawesome.alertme.Utility.AlertMeAlarm;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;


public class PopupAlarm extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_alarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        TextView alarmName = (TextView) findViewById(R.id.alarmName);
        TextView dataTemp = (TextView) findViewById(R.id.dataTemp);
        TextView dataRain = (TextView) findViewById(R.id.dataRain);
        TextView dataWindSpeed = (TextView) findViewById(R.id.dataWindSpeed);

        boolean exceedsTemperatureRange = getIntent().getBooleanExtra("exceedsTemperatureRange", true);
        boolean exceedsPrecipitationCondition = getIntent().getBooleanExtra("exceedsPrecipitationCondition", true);
        boolean exceedsWindSpeedCondition = getIntent().getBooleanExtra("exceedsWindSpeedCondition", true);

        int alarmIndex = getIntent().getIntExtra("alarmIndex", -1);
        AlertMeAlarm alarm = AlertMeMetadataSingleton.getInstance().getAlarm(alarmIndex);

        int a = getIntent().getIntExtra("a", -1);
        int b = getIntent().getIntExtra("b", -1);
        int c = getIntent().getIntExtra("c", -1);
        int d = getIntent().getIntExtra("d", -1);

        SharedPreferences currentWeatherData = getSharedPreferences("weather_data", MODE_PRIVATE);
        alarmName.setText(alarm.getName());
        if (exceedsTemperatureRange) {
            dataTemp.setText("Temperature: " +
                    currentWeatherData.getInt("tomorrowMinTemperatureF", -1) + "\u00b0F" + ", " +
                    currentWeatherData.getInt("tomorrowMaxTemperatureF", -1) + "\u00b0F");
        }
        if (exceedsPrecipitationCondition) {
            dataRain.setText("Precipitation: " + currentWeatherData.getInt("tomorrowPrecipitationChance", -1) + "%");
        }
        if (exceedsWindSpeedCondition) {
            dataWindSpeed.setText("Wind Speed: " + currentWeatherData.getInt("tomorrowWindSpeedMph", -1) + "mph");
        }
    }

}
