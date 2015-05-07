package teamawesome.alertme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import teamawesome.alertme.Utility.AlertMeMetadataSingleton;
import teamawesome.alertme.Utility.AlertMeAlarm;


public class Conditions extends ActionBarActivity {

    private AlertMeAlarm currentAlarm;
    private int currentAlarmIndex;

    //temperature
    private final int MAX_DEG_F = 95;
    private final int MIN_DEG_F = 0;
    private final int MAX_DEG_C = 35;
    private final int MIN_DEG_C = -18;
    private Switch temp_F_C;
    RangeSeekBar<Integer> temp_double;
    private int changedProgressTempMin;
    private int changedProgressTempMax;
    private boolean isInUnitsFahrenheit;

    //precipiation
    private final int DEFAULT_PRECIP = 50;
    private SeekBar precipitation;
    private int changedProgressPrecip;

    //wind speed
    private final int DEFAULT_WIND = 15;
    private final int MAX_WIND = 50;
    private Switch wind_mph_kph;
    private SeekBar windSpeed;
    private int changedProgressWind;
    private boolean isInUnitsMPH;

    //to restore settings
    private SharedPreferences mPrefs;
    private String sharedPrefFile;

    private static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        int alarmIndex = getIntent().getIntExtra("alarmIndex", -1);
        if (alarmIndex >= 0 && alarmIndex < AlertMeMetadataSingleton.getInstance().size()) {
            currentAlarm = AlertMeMetadataSingleton.getInstance().getAlarm(alarmIndex);
            currentAlarmIndex = alarmIndex;
        } else {
            // Not what default behavior should be
            // Consider bringing user back to AlarmList with error Toast
            currentAlarm = AlertMeMetadataSingleton.getInstance().getAlarm(0);
            currentAlarmIndex = 0;
        }

        sharedPrefFile = "alert_me_alarm_" + currentAlarmIndex;
        mPrefs = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        //Temperature
        // create RangeSeekBar as Integer range between 0 and 100
        temp_double = new RangeSeekBar<Integer>(MIN_DEG_F, MAX_DEG_F, this);
        temp_double.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                // handle changed range values
                Log.i(TAG, "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
                TextView displayValue = (TextView)findViewById(R.id.tempMax);
                TextView displayValue4 = (TextView)findViewById(R.id.tempMin);
                if (isInUnitsFahrenheit){
                    changedProgressTempMax = maxValue;
                    changedProgressTempMin = minValue;
                }
                else{
                    changedProgressTempMax = degreesFToC(maxValue);
                    changedProgressTempMin = degreesFToC(minValue);
                }
                displayValue.setText("" + changedProgressTempMax);
                displayValue4.setText("" + changedProgressTempMin);

            }
        });
        // add RangeSeekBar to pre-defined layout
        ViewGroup layout = (ViewGroup) findViewById(R.id.temp_layout);
        layout.addView(temp_double);

        addListenerdegreesF();

        //Precipitation
        precipitation = (SeekBar) findViewById(R.id.seekBar2);
        precipitation.setOnSeekBarChangeListener(precipitationListener);

        //Wind Speed
        windSpeed = (SeekBar) findViewById(R.id.seekBar3);
        windSpeed.setOnSeekBarChangeListener(windSpeedListener);
        addListenerMPH();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putInt("temperature_max", changedProgressTempMax);
        outState.putInt("temperature_min", changedProgressTempMin);
        outState.putBoolean("degreesF", temp_F_C.isChecked());

        outState.putInt("precipitation", changedProgressPrecip);

        outState.putBoolean("mph", wind_mph_kph.isChecked());
        outState.putInt("windSpeed", changedProgressWind);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        //Temperature
        temp_F_C.setChecked(savedInstanceState.getBoolean("degreesF"));
        isInUnitsFahrenheit = temp_F_C.isChecked();

        changedProgressTempMax = mPrefs.getInt("temperature_max", 0);
        changedProgressTempMin = mPrefs.getInt("temperature_min", 0);
        TextView displayValue = (TextView) findViewById(R.id.tempMax);
        TextView displayValue4 = (TextView) findViewById(R.id.tempMin);

        displayValue.setText("" + changedProgressTempMax);
        temp_double.setSelectedMaxValue(changedProgressTempMax);
        displayValue4.setText("" + changedProgressTempMin);
        temp_double.setSelectedMinValue(changedProgressTempMin);

        //Precipitation
        changedProgressPrecip = savedInstanceState.getInt("precipitation");
        TextView displayValue2 = (TextView) findViewById(R.id.precipValue);
        displayValue2.setText("" + changedProgressPrecip);
        precipitation.setProgress(changedProgressPrecip);


        //Wind Speed
        changedProgressWind = savedInstanceState.getInt("windSpeed");
        TextView displayValue3 = (TextView) findViewById(R.id.windValue);
        displayValue3.setText("" + changedProgressWind);
        windSpeed.setProgress(changedProgressWind);

        wind_mph_kph.setChecked(savedInstanceState.getBoolean("mph"));
        isInUnitsMPH = wind_mph_kph.isChecked();

    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = mPrefs.edit();
        //Temperature
        save(temp_F_C.isChecked(), "degreesF");
        editor.putInt("temperature_max", changedProgressTempMax);
        editor.putInt("temperature_min", changedProgressTempMin);

        //Precipitation
        editor.putInt("precipitation", changedProgressPrecip);

        //Wind Speed
        editor.putInt("windSpeed", changedProgressWind);
        save(wind_mph_kph.isChecked(), "mph");

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        mPrefs = getPreferences(Context.MODE_PRIVATE);

        //Temperature
        temp_F_C.setChecked(load("degreesF"));
        isInUnitsFahrenheit = temp_F_C.isChecked();

        changedProgressTempMax = mPrefs.getInt("temperature_max", 0);
        changedProgressTempMin = mPrefs.getInt("temperature_min", 0);
        TextView displayValue = (TextView) findViewById(R.id.tempMax);
        TextView displayValue4 = (TextView) findViewById(R.id.tempMin);

        displayValue.setText("" + changedProgressTempMax);
        temp_double.setSelectedMaxValue(changedProgressTempMax);
        displayValue4.setText("" + changedProgressTempMin);
        temp_double.setSelectedMinValue(changedProgressTempMin);

        //Precipitation
        changedProgressPrecip = mPrefs.getInt("precipitation", 0);
        TextView displayValue2 = (TextView) findViewById(R.id.precipValue);
        displayValue2.setText("" + changedProgressPrecip);
        precipitation.setProgress(changedProgressPrecip);

        //Wind Speed
        changedProgressWind = mPrefs.getInt("windSpeed", 0);
        TextView displayValue3 = (TextView) findViewById(R.id.windValue);
        displayValue3.setText("" + changedProgressWind);
        windSpeed.setProgress(changedProgressWind);

        wind_mph_kph.setChecked(load("mph"));
        isInUnitsMPH = wind_mph_kph.isChecked();

    }

    private void save(final boolean isChecked, String saveName) {
        mPrefs = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(saveName, isChecked);
        editor.apply();
    }

    private boolean load(String saveName) {
        mPrefs = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        return mPrefs.getBoolean(saveName, true);
    }

    //temp_F_C switch
    public void addListenerdegreesF() {

        temp_F_C = (Switch) findViewById(R.id.temp_switch);

        temp_F_C.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isInUnitsFahrenheit = !isInUnitsFahrenheit;
            }
        });
    }//end temp_F_C switch

    //precipitation seekbar
    private SeekBar.OnSeekBarChangeListener precipitationListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            changedProgressPrecip = progress;
            TextView displayValue = (TextView) findViewById(R.id.precipValue);
            displayValue.setText("" + changedProgressPrecip);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            //TODO
        }
    };//end precipitation seekbar


    //wind speed seekbar
    private SeekBar.OnSeekBarChangeListener windSpeedListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            changedProgressWind = progress;
            TextView displayValue = (TextView)findViewById(R.id.windValue);
            displayValue.setText("" + changedProgressWind);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            //TODO
        }
    };//end wind speed seekbar

    //wind_mph_kph
    public void addListenerMPH() {

        wind_mph_kph = (Switch) findViewById(R.id.wind_switch);

        wind_mph_kph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    isInUnitsMPH = true;
                    windSpeed.setMax(MAX_WIND);
                }
        });
    }//end wind_mph_kph

    private int degreesFToC(int F){
        return (int)((F-32)*.5556);
    }

    public void showInfo (View v){
        String message = "\"Comfort Temperature\" represents the range of temperatures that you find comfortable." +
                "You will be alerted when temperatures fall below or rise above this range.";
        Toast.makeText(Conditions.this, message, Toast.LENGTH_LONG).show();
    }

    private void saveInfo(){
        currentAlarm.setTemperatureCondition(isInUnitsFahrenheit, changedProgressTempMax, changedProgressTempMin);
        currentAlarm.setPrecipitationCondition(changedProgressPrecip);
        currentAlarm.setWindSpeedCondition(isInUnitsMPH, changedProgressWind);

        SharedPreferences.Editor editor = mPrefs.edit();
        //Temperature
        save(temp_F_C.isChecked(), "degreesF");
        editor.putInt("temperature_max", changedProgressTempMax);
        editor.putInt("temperature_min", changedProgressTempMin);

        //Precipitation
        editor.putInt("precipitation", changedProgressPrecip);

        //Wind Speed
        editor.putInt("windSpeed", changedProgressWind);
        save(wind_mph_kph.isChecked(), "mph");

        editor.apply();
    }

    public void toTimeFrame(View view){
        saveInfo();

        Intent intent = new Intent(this, TimeFrame.class);
        intent.putExtra("alarmIndex", currentAlarmIndex);
        startActivity(intent);
    }

}
