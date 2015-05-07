package teamawesome.alertme.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AlertMeMetadataSingleton {

    private static AlertMeMetadataSingleton instance;
    private static ArrayList<AlertMeAlarm> alarms;

    public static void initInstance() {
        if (instance == null) {
            instance = new AlertMeMetadataSingleton();
        }
    }

    public static AlertMeMetadataSingleton getInstance() {
        return instance;
    }

    private AlertMeMetadataSingleton() {
        alarms = new ArrayList<>();
        addAlarm("Demo Fun");
        addAlarm("Preset Shenanigans");
    }

    public void addAlarm(String name) {
        AlertMeAlarm alarm = new AlertMeAlarm();
        if (name == null || name.trim().isEmpty()) {
            alarm.setName("Default");
        } else {
            alarm.setName(name);
        }
        alarms.add(alarm);
    }
    
    public void deleteAlarms(Set<Integer> indexes) {
        Iterator<AlertMeAlarm> iter = alarms.iterator();
        int i = 0;
        while (iter.hasNext()) {
            iter.next();
            if (indexes.contains(i)) {
                iter.remove();
            }
            i++;
        }
    }

    public AlertMeAlarm getAlarm(int index) {
        return alarms.get(index);
    }

    public int size() {
        return alarms.size();
    }

    public void saveWeather(WeatherForecastData weatherForecast, Context context) {
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

    public void saveLocation(Location location, Context context) {
        SharedPreferences weatherData = context.getSharedPreferences("weather_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = weatherData.edit();

        editor.putFloat("locationLatitude", (float) location.getLatitude());
        editor.putFloat("locationLongitude", (float) location.getLongitude());

        editor.apply();
    }

}