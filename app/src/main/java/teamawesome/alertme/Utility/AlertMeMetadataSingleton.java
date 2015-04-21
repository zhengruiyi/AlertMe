package teamawesome.alertme.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class AlertMeMetadataSingleton {

    private static AlertMeMetadataSingleton instance;
    private static ArrayList<AlertMeAlarm> alarms;
    private static WeatherForecastData weatherForecast;

    public static void initInstance() {
        if (instance == null) {
            instance = new AlertMeMetadataSingleton();
        }
    }

    public static AlertMeMetadataSingleton getInstance() {
        return instance;
    }

    private AlertMeMetadataSingleton() {
        weatherForecast = new WeatherForecastData();

        alarms = new ArrayList<AlertMeAlarm>();
        alarms.add(new AlertMeAlarm());
    }


    public void addAlarm() {
        alarms.add(new AlertMeAlarm());
    }

    public void deleteAlarm(int index) {
        alarms.remove(index);
    }

    public AlertMeAlarm getAlarm(int index) {
        return alarms.get(index);
    }

    public int size() {
        return alarms.size();
    }

    public List<AlertMeAlarm> getAlarmsList() {
        return alarms;
    }

    public void setWeather(WeatherForecastData newWeather, Context context) {
        weatherForecast = newWeather;
        saveWeather(context);
    }

    private void saveWeather(Context context) {
        SharedPreferences weatherData = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = weatherData.edit();

        editor.putInt("tomorrowMinTemperatureF", weatherForecast.temperature.getMinTemperatureF());
        editor.putInt("tomorrowMaxTemperatureF", weatherForecast.temperature.getMaxTemperatureF());
        editor.putInt("tomorrowMinTemperatureC", weatherForecast.temperature.getMinTemperatureC());
        editor.putInt("tomorrowMaxTemperatureC", weatherForecast.temperature.getMaxTemperatureC());

        editor.putInt("tomorrowPrecipitationChance", weatherForecast.precipitation.getPercentageChance());
        editor.putFloat("tomorrowRainAmountIn", weatherForecast.precipitation.getRainAmountInches());
        editor.putFloat("tomorrowRainAmountMm", weatherForecast.precipitation.getRainAmountMm());
        editor.putFloat("tomorrowRainAmountIn", weatherForecast.precipitation.getSnowAmountInches());
        editor.putFloat("tomorrowRainAmountCm", weatherForecast.precipitation.getSnowAmountCm());

        editor.putInt("tomorrowWindSpeedMph", weatherForecast.wind.getMaxSpeedMph());
        editor.putInt("tomorrowWindSpeedKph", weatherForecast.wind.getMaxSpeedKph());

        editor.putInt("tomorrowHumidity", weatherForecast.humidity.getHumidity());

        editor.apply();
    }

    public WeatherForecastData getWeather() {
        return weatherForecast;
    }
}