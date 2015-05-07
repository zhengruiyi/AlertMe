package teamawesome.alertme.Utility;

import java.util.HashMap;

public class AlertMeAlarm {

    private String name;

    private HashMap<String, Integer> weatherConditions;

    private boolean[] daysSelected;
    private long alertTimeInMillis;


    public AlertMeAlarm() {
        name = "Alarm";

        // Defaults for the AlertMeAlarm are set in the constructor
        initializeDefaultWeatherConditions();

        // Mon, Tue, Wed, Thu, Fri, Sat, Sun
        daysSelected = new boolean[] {true, true, true, true, true, false, false};
        alertTimeInMillis = 6 * 60 * 60 * 1000;      // set with 24-hour
    }

    private void initializeDefaultWeatherConditions() {
        weatherConditions = new HashMap<String, Integer>();
        setTemperatureCondition(true, 60, 80);
        setPrecipitationCondition(40);
        setWindSpeedCondition(true, 35);
    }



    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Integer> getWeatherConditions() {
        return weatherConditions;
    }

    public void setTemperatureCondition(boolean tempUnit, int temperatureMin, int temperatureMax) {
        if (tempUnit) {
            // Set with Fahrenheit
            weatherConditions.put("Fmin", temperatureMin);
            weatherConditions.put("Fmax", temperatureMax);
            weatherConditions.put("Cmin", convertFToC(temperatureMin));
            weatherConditions.put("Cmax", convertFToC(temperatureMax));
        } else {
            // Set with Celsius
            weatherConditions.put("Cmin", temperatureMin);
            weatherConditions.put("Cmax", temperatureMax);
            weatherConditions.put("Fmin", convertCToF(temperatureMin));
            weatherConditions.put("Fmax", convertCToF(temperatureMax));
        }
    }

    public void setPrecipitationCondition(int probability) {
        weatherConditions.put("Precipitation", probability);
    }

    public void setWindSpeedCondition(boolean speedUnit, int speed) {
        if (speedUnit) {
            // Set with mph
            weatherConditions.put("MilesPerHour", speed);
            weatherConditions.put("KilometersPerHour", convertMphToKph(speed));
        } else {
            // Set with kph
            weatherConditions.put("KilometersPerHour", speed);
            weatherConditions.put("MilesPerHour", convertKphToMph(speed));
        }
    }


    public void toggleDay(int day) {
        daysSelected[day] = !daysSelected[day];
    }

    public void setDaysSelected(boolean[] days) {
        if (days.length == 7) {
            System.arraycopy(days, 0, daysSelected, 0, 7);
        } else {
            throw new AssertionError("Tried to set Days Selected to boolean list not of length 7");
        }
    }

    public void setAlertTimeWithMinutes(long timeInMinutes) {
        if (timeInMinutes >= 0 && timeInMinutes < 24 * 60) {
            alertTimeInMillis = timeInMinutes * 60 * 1000;
        } else {
            throw new AssertionError("Tried to set Alert Time outside of 0 to 24");
        }
    }

    public long getAlertTimeInMillis() {
        return alertTimeInMillis;
    }


    private int convertFToC(int fTemp) {
        double cTemp = ((double)fTemp - 32.0) * (5.0 / 9.0);
        return (int)cTemp;
    }

    private int convertCToF(int cTemp) {
        double fTemp = ((double)cTemp * (9.0 / 5.0)) + 32;
        return (int)fTemp;
    }

    private int convertMphToKph(int mph) {
        double kph = (double)mph * 1.60934;
        return (int)kph;
    }

    private int convertKphToMph(int kph) {
        double mph = (double)kph * 0.621371;
        return (int)mph;
    }

}
