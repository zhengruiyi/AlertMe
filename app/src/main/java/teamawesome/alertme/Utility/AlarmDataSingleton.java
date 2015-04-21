package teamawesome.alertme.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class AlarmDataSingleton {

    private static AlarmDataSingleton instance;
    private static ArrayList<AlertMeAlarm> alarms;
    private static WeatherForecastData currentWeather;

    public static void initInstance() {
        if (instance == null) {
            instance = new AlarmDataSingleton();
        }
    }

    public static AlarmDataSingleton getInstance() {
        return instance;
    }

    private AlarmDataSingleton() {
        currentWeather = new WeatherForecastData();

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
        currentWeather = newWeather;
        saveWeather(context);
    }

    private void saveWeather(Context context) {
        SharedPreferences weatherData = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = weatherData.edit();

//        editor.putFloat("currentWeatherTemperature", currentWeather.temperature.getMinTemp());
//        editor.putFloat("currentWeatherPrecipitation", currentWeather.rain.getAmmount());
//        editor.putFloat("currentWeatherWindSpeed", currentWeather.wind.getMaxSpeed());

        editor.apply();
    }

    public WeatherForecastData getWeather() {
        return currentWeather;
    }
}
