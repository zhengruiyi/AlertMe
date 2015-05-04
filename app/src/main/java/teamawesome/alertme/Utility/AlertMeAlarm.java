package teamawesome.alertme.Utility;

import java.util.HashMap;

public class AlertMeAlarm {

    private String name;

    private HashMap<String, Integer> weatherConditions;

    private boolean[] daysSelected;
    private int timeFrame;
    private long alertTimeInMillis;
    private boolean inMorning;

    private boolean soundToggle;
    private boolean vibrateToggle;


    public AlertMeAlarm() {
        name = "Default";

        // Defaults for the AlertMeAlarm are set in the constructor
        initializeDefaultWeatherConditions();

        // Mon, Tue, Wed, Thu, Fri, Sat, Sun
        daysSelected = new boolean[] {true, true, true, true, true, false, false};
        timeFrame = 24;     // either 12 or 24
        alertTimeInMillis = 6 * 60 * 60 * 1000;      // set with 24-hour

        inMorning = false;

        soundToggle = false;
        vibrateToggle = false;
    }

    private void initializeDefaultWeatherConditions() {
        weatherConditions = new HashMap<String, Integer>();
        setTemperatureCondition(true, 50, 0);
        setPrecipitationCondition(50);
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

    public void setTemperatureCondition(boolean tempUnit, int temperatureMax, int temperatureMin) {
        if (tempUnit) {
            // Set with Fahrenheit
            weatherConditions.put("Fmax", temperatureMax);
            weatherConditions.put("Fmin", temperatureMin);
            weatherConditions.put("Cmax", convertFToC(temperatureMax));
            weatherConditions.put("Cmin", convertFToC(temperatureMin));
        } else {
            // Set with Celsius
            weatherConditions.put("Cmax", temperatureMax);
            weatherConditions.put("Cmin", temperatureMin);
            weatherConditions.put("Fmax", convertCToF(temperatureMax));
            weatherConditions.put("Fmin", convertCToF(temperatureMin));
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

    public void setTimeFrame(int frame) {
        if (frame == 12 || frame == 24) {
            timeFrame = frame;
        } else {
            throw new AssertionError("Tried to set Time Frame to neither 12 nor 24");
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

    public void setAmPm (boolean inMorning){
        this.inMorning = inMorning;
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
