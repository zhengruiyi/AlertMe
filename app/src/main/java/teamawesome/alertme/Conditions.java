package teamawesome.alertme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import teamawesome.alertme.Utility.AlarmDataSingleton;
import teamawesome.alertme.Utility.AlertMeAlarm;


public class Conditions extends ActionBarActivity {

    private AlertMeAlarm currentAlarm;
    private int currentAlarmIndex;

    //temperature
    private final int MAX_DEG_F = 132;
    private final int MAX_DEG_C = 100;
    private final int DEG_F_OFFSET = 32;
    private final int DEG_C_OFFSET = 50;
    private Switch temp_F_C;
    private SeekBar temperature;
    private int changedProgressTemp;
    private boolean isInUnitsFahrenheit;

    //precipiation
    private SeekBar precipitation;
    private int changedProgressPrecip;

    private Switch wind_mph_kph;
    private SeekBar windSpeed;
    private int changedProgressWind;
    private boolean isInUnitsMPH;

    //to restore settings
    private SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        int alarmIndex = getIntent().getIntExtra("alarmIndex", -1);
        if (alarmIndex >= 0 && alarmIndex < AlarmDataSingleton.getInstance().size()) {
            currentAlarm = AlarmDataSingleton.getInstance().getAlarm(alarmIndex);
            currentAlarmIndex = alarmIndex;
        } else {
            currentAlarm = AlarmDataSingleton.getInstance().getAlarm(0);
            currentAlarmIndex = 0;
            //throw new AssertionError("Conditions: Failed to access AlarmDataSingleton list at " + alarmIndex);
        }

        //Temperature
        temperature = (SeekBar) findViewById(R.id.seekBar);
        temperature.setOnSeekBarChangeListener(temperatureListener);
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

        outState.putInt("temperature", changedProgressTemp);
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
        if(isInUnitsFahrenheit){temperature.setMax(MAX_DEG_F);}
        else{temperature.setMax(MAX_DEG_C);}

        changedProgressTemp = mPrefs.getInt("temperature", 0);
        TextView displayValue = (TextView) findViewById(R.id.tempValue);
        if (isInUnitsFahrenheit){changedProgressTemp += DEG_F_OFFSET;}
        else {changedProgressTemp += DEG_C_OFFSET;}
        displayValue.setText("" + changedProgressTemp);
        temperature.setProgress(changedProgressTemp);

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
        editor.putInt("temperature", changedProgressTemp);

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
        if(isInUnitsFahrenheit){temperature.setMax(MAX_DEG_F);}
        else{temperature.setMax(MAX_DEG_C);}

        changedProgressTemp = mPrefs.getInt("temperature", 0);
        TextView displayValue = (TextView) findViewById(R.id.tempValue);
        if (isInUnitsFahrenheit){
            displayValue.setText("" + (changedProgressTemp + DEG_F_OFFSET));
            temperature.setProgress(changedProgressTemp + DEG_F_OFFSET);
        }
        else{
            displayValue.setText("" + (changedProgressTemp + DEG_C_OFFSET));
            temperature.setProgress(changedProgressTemp + DEG_C_OFFSET);
        }

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
        mPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(saveName, isChecked);
        editor.apply();
    }

    private boolean load(String saveName) {
        mPrefs = getPreferences(Context.MODE_PRIVATE);
        return mPrefs.getBoolean(saveName, false);
    }

    //temperature seekbar
    private SeekBar.OnSeekBarChangeListener temperatureListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            if (temp_F_C.isChecked()){
                temperature.setMax(MAX_DEG_F);
                changedProgressTemp = progress - DEG_F_OFFSET;
            }
            else{
                temperature.setMax(MAX_DEG_C);
                changedProgressTemp = progress - DEG_C_OFFSET;
            }
            TextView displayValue = (TextView) findViewById(R.id.tempValue);
            displayValue.setText("" + changedProgressTemp);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            //TODO
        }
    };//end temperature seekbar

    //temp_F_C switch
    public void addListenerdegreesF() {

        temp_F_C = (Switch) findViewById(R.id.temp_switch);

        temp_F_C.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    isInUnitsFahrenheit = true;
                    temperature.setMax(MAX_DEG_F);
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
                    windSpeed.setMax(DEG_C_OFFSET);
                }
        });
    }//end wind_mph_kph

    private void saveInfo(){
        currentAlarm.setTemperatureCondition(isInUnitsFahrenheit, changedProgressTemp);
        currentAlarm.setPrecipitationCondition(changedProgressPrecip);
        currentAlarm.setWindSpeedCondition(isInUnitsMPH, changedProgressWind);
    }

    public void toTimeFrame(View view){
        saveInfo();

        Intent intent = new Intent(this, TimeFrame.class);
        intent.putExtra("alarmIndex", currentAlarmIndex);
        startActivity(intent);
    }

}
