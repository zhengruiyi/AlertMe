package teamawesome.alertme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;


public class Conditions extends ActionBarActivity {

    private WeatherAlarm currentAlarm;
    private int currentAlarmIndex;

    private CheckBox degreesF, degreesC;
    private SeekBar temperature;
    private int changedProgressTemp;
    private boolean isInUnitsFahrenheit;

    private SeekBar precipitation;
    private int changedProgressPrecip;

    private CheckBox mph, kph;
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
        addListenerdegreesC();

        //Precipitation
        precipitation = (SeekBar) findViewById(R.id.seekBar2);
        precipitation.setOnSeekBarChangeListener(precipitationListener);

        //Wind Speed
        windSpeed = (SeekBar) findViewById(R.id.seekBar3);
        windSpeed.setOnSeekBarChangeListener(windSpeedListener);
        addListenerMPH();
        addListenerKPH();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conditions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putInt("temperature", changedProgressTemp);
        outState.putBoolean("degreesF", degreesF.isChecked());
        outState.putBoolean("degreesC", degreesC.isChecked());

        outState.putInt("precipitation", changedProgressPrecip);

        outState.putBoolean("mph", mph.isChecked());
        outState.putBoolean("kph", kph.isChecked());
        outState.putInt("windSpeed", changedProgressWind);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        //Temperature
        degreesF.setChecked(savedInstanceState.getBoolean("degreesF"));
        degreesC.setChecked(savedInstanceState.getBoolean("degreesC"));
        isInUnitsFahrenheit = degreesF.isChecked();

        changedProgressTemp = mPrefs.getInt("temperature", 0);
        TextView displayValue = (TextView) findViewById(R.id.tempValue);
        //displayValue.setText("" + changedProgressTemp);
        if (isInUnitsFahrenheit){
            displayValue.setText("" + (changedProgressTemp + 32));
            temperature.setProgress(changedProgressTemp + 32);
        }
        else {
            displayValue.setText("" + (changedProgressTemp + 50));
            temperature.setProgress(changedProgressTemp + 50);
        }

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

        mph.setChecked(savedInstanceState.getBoolean("mph"));
        kph.setChecked(savedInstanceState.getBoolean("kph"));
        isInUnitsMPH = mph.isChecked();



    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = mPrefs.edit();
        //Temperature
        save(degreesF.isChecked(), "degreesF");
        save(degreesC.isChecked(), "degreesC");
        editor.putInt("temperature", changedProgressTemp);

        //Precipitation
        editor.putInt("precipitation", changedProgressPrecip);

        //Wind Speed
        editor.putInt("windSpeed", changedProgressWind);
        save(mph.isChecked(), "mph");
        save(kph.isChecked(), "kph");

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        mPrefs = getPreferences(Context.MODE_PRIVATE);

        //Temperature
        degreesF.setChecked(load("degreesF"));
        degreesC.setChecked(load("degreesC"));
        isInUnitsFahrenheit = degreesF.isChecked();

        changedProgressTemp = mPrefs.getInt("temperature", 0);
        TextView displayValue = (TextView) findViewById(R.id.tempValue);
        //displayValue.setText("" + changedProgressTemp);
        if (isInUnitsFahrenheit){
            displayValue.setText("" + (changedProgressTemp + 32));
            temperature.setProgress(changedProgressTemp + 32);
        }
        else {
            displayValue.setText("" + (changedProgressTemp + 50));
            temperature.setProgress(changedProgressTemp + 50);
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

        mph.setChecked(load("mph"));
        kph.setChecked(load("kph"));
        isInUnitsMPH = mph.isChecked();

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
            if (degreesF.isChecked()){
                temperature.setMax(142);
                changedProgressTemp = progress - 32;
            }
            else{
                temperature.setMax(100);
                changedProgressTemp = progress - 50;
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

    //degreesF Checkbox
    public void addListenerdegreesF() {

        degreesF = (CheckBox) findViewById(R.id.checkBox);

        degreesF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && degreesC.isChecked()) {
                    degreesF.setChecked(false);
                }
                else {
                    isInUnitsFahrenheit = true;
                }
            }
        });
    }//end degreesF checkbox

    //degreesC Checkbox
    public void addListenerdegreesC() {

        degreesC = (CheckBox) findViewById(R.id.checkBox2);

        degreesC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && degreesF.isChecked()) {
                    degreesC.setChecked(false);
                }
                else {
                    isInUnitsFahrenheit = false;
                }
            }
        });
    }//end degreesC checkbox

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

    //mph Checkbox
    public void addListenerMPH() {

        mph = (CheckBox) findViewById(R.id.checkBox3);

        mph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && kph.isChecked()) {
                    mph.setChecked(false);
                }
                else {
                    isInUnitsMPH = true;
                    windSpeed.setMax(50);
                }
            }
        });
    }//end mph checkbox

    //kph Checkbox
    public void addListenerKPH() {

        kph = (CheckBox) findViewById(R.id.checkBox4);

        kph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && mph.isChecked()) {
                    kph.setChecked(false);
                }
                else {
                    isInUnitsMPH = false;
                    windSpeed.setMax(100);
                }
            }
        });
    }//end mph checkbox

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
