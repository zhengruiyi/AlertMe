package teamawesome.alertme;

import java.util.ArrayList;

public class AlarmDataSingleton {

    private static AlarmDataSingleton instance;
    private static ArrayList<WeatherAlarm> alarms;
    private static Weather weather;

    public static void initInstance() {
        if (instance == null) {
            instance = new AlarmDataSingleton();
        }
    }

    public static AlarmDataSingleton getInstance() {
        return instance;
    }

    private AlarmDataSingleton() {
        weather = new Weather();

        alarms = new ArrayList<WeatherAlarm>();
        alarms.add(new WeatherAlarm());
    }


    public void addAlarm() {
        alarms.add(new WeatherAlarm());
    }

    public void deleteAlarm(int index) {
        alarms.remove(index);
    }

    public WeatherAlarm getAlarm(int index) {
        return alarms.get(index);
    }

    public int size() {
        return alarms.size();
    }

    public void setWeather(Weather newWeather) {
        weather = newWeather;
    }

    public Weather getWeather() {
        return weather;
    }
}
