package teamawesome.alertme;

import android.util.Log;

import java.util.HashMap;

public class WeatherAlarm {

    private HashMap<String, Integer> weatherConditions;

    private boolean[] daysSelected;
    private int timeFrame;
    private int alertTime;

    private boolean soundToggle;
    private boolean vibrateToggle;


    public WeatherAlarm() {
        // Defaults for the WeatherAlarm are set in the constructor
        initializeDefaultWeatherConditions();

        // Mon, Tue, Wed, Thu, Fri, Sat, Sun
        daysSelected = new boolean[] {true, true, true, true, true, false, false};
        timeFrame = 12;     // either 12 or 24
        alertTime = 6;      // set with 24-hour

        soundToggle = false;
        vibrateToggle = false;
    }

    private void initializeDefaultWeatherConditions() {
        weatherConditions = new HashMap<String, Integer>();
        setTemperatureCondition(true, 50);
        setPrecipitationCondition(50);
        setWindSpeedCondition(true, 35);
    }



    public void setTemperatureCondition(boolean tempUnit, int temperature) {
        if (tempUnit) {
            // Set with Fahrenheit
            weatherConditions.put("F", temperature);
            weatherConditions.put("C", convertFToC(temperature));
        } else {
            // Set with Celsius
            weatherConditions.put("C", temperature);
            weatherConditions.put("F", convertCToF(temperature));
        }
    }

    public void setPrecipitationCondition(int probability) {
        weatherConditions.put("Precipitation", 50);
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

    public void setTimeFrame(int frame) {
        if (frame == 12 || frame == 24) {
            timeFrame = frame;
        } else {
            throw new AssertionError("Tried to set Time Frame to neither 12 nor 24");
        }
    }

    public void setAlertTime(int time) {
        if (time >= 0 && time <= 23) {
            alertTime = time;
        } else {
            throw new AssertionError("Tried to set Alert Time outside of 0 to 24");
        }
    }


    public void toggleSound() {
        soundToggle = !soundToggle;
    }

    public void setSoundToggle(boolean sound) {
        soundToggle = sound;
    }

    public void toggleVibrate() {
        vibrateToggle = !vibrateToggle;
    }

    public void setVibrateToggle(boolean vibrate) {
        vibrateToggle = vibrate;
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
