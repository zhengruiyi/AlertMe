package teamawesome.alertme;

import java.util.ArrayList;

public class AlarmDataSingleton {

    private static AlarmDataSingleton instance;
    private static ArrayList<WeatherAlarm> alarms;

    public static void initInstance() {
        if (instance == null) {
            instance = new AlarmDataSingleton();
        }
    }

    public static AlarmDataSingleton getInstance() {
        return instance;
    }

    private AlarmDataSingleton() {
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
}
