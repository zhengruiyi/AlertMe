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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class TimeFrame extends ActionBarActivity {
    //to store alarm info
    private WeatherAlarm currentAlarm;

    //weekday checkboxes
    private CheckBox weekday, weekend;
    private boolean[] weekdays = {false, false, false, false, false, false, false};

    //time frame checkboxes
    private CheckBox twelveHour, twentyFourHour;
    private int timeFrame = 12; //default twelveHour

    //time of day check boxes
    private CheckBox am, pm;
    private boolean inMorning = false;

    //Alert time seekbar
    private int changedProgress = 0;
    private SeekBar alertTime;

    //Sounds
    private CheckBox vibrate;
    private Switch sound;

    //to restore settings
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_frame);

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        int alarmIndex = getIntent().getIntExtra("alarmIndex", -1);
        if (alarmIndex >= 0 && alarmIndex < AlarmDataSingleton.getInstance().size()) {
            currentAlarm = AlarmDataSingleton.getInstance().getAlarm(alarmIndex);
        } else {
            throw new AssertionError("TimeFrame: Failed to access AlarmDataSingleton list at " + alarmIndex);
        }

        //Seekbar
        alertTime = (SeekBar) findViewById(R.id.seekBar4);
        alertTime.setOnSeekBarChangeListener(alertTimeListener);

        addListenerWeekday();
        addListenerWeekend();

        addListenerTimeFrame12();
        addListenerTimeFrame24();

        addListenerAm();
        addListenerPm();

        addListenerSound();
        addListenerVibrate();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_frame, menu);
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

        outState.putInt("seekBar", changedProgress);

        outState.putBooleanArray("weekdays", weekdays);
        outState.putBoolean("weekday", weekday.isChecked());
        outState.putBoolean("weekend", weekend.isChecked());

        outState.putBoolean("twelveHour", twelveHour.isChecked());
        outState.putBoolean("twentyFourHour", twentyFourHour.isChecked());

        outState.putBoolean("am", am.isChecked());
        outState.putBoolean("pm", pm.isChecked());

        outState.putBoolean("sound", sound.isChecked());
        outState.putBoolean("vibrate", vibrate.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        changedProgress = savedInstanceState.getInt("seekBar");
        TextView displayValue = (TextView) findViewById(R.id.seekBarTextView);
        displayValue.setText("" + changedProgress);
        alertTime.setProgress(changedProgress);

        weekdays = savedInstanceState.getBooleanArray("weekdays");

        //weekdays checkboxes
        weekday.setChecked(savedInstanceState.getBoolean("weekday"));
        weekend.setChecked(savedInstanceState.getBoolean("weekend"));

        //timeframe checkboxes
        twelveHour.setChecked(savedInstanceState.getBoolean("twelveHour"));
        twentyFourHour.setChecked(savedInstanceState.getBoolean("twentyFourHour"));
        if(twelveHour.isChecked()){timeFrame = 12;}
        else {timeFrame = 24;}

        //am/pm checkboxes
        am.setChecked(savedInstanceState.getBoolean("am"));
        pm.setChecked(savedInstanceState.getBoolean("pm"));
        inMorning = am.isChecked();

        sound.setChecked(savedInstanceState.getBoolean("sound"));
        vibrate.setChecked(savedInstanceState.getBoolean("vibrate"));


    }

    @Override
    public void onPause() {
        super.onPause();
        //check boxes
        save(weekday.isChecked(), "weekday");
        save(weekend.isChecked(), "weekend");
        save(twelveHour.isChecked(), "twelveHour");
        save(twentyFourHour.isChecked(), "twentyFourHour");
        save(am.isChecked(), "am");
        save(pm.isChecked(), "pm");
        save(vibrate.isChecked(), "vibrate");
        //switch
        save(sound.isChecked(), "sound");

        //seekbar
        mPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("seekBar", changedProgress);

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        //check boxes
        weekday.setChecked(load("weekday"));
        weekend.setChecked(load("weekend"));

        //time frame
        twelveHour.setChecked(load("twelveHour"));
        twentyFourHour.setChecked(load("twentyFourHour"));
        if (twelveHour.isChecked()){timeFrame = 12;}
        else {timeFrame = 24;}

        //am/pm checkboxes
        am.setChecked(load("am"));
        pm.setChecked(load("pm"));
        inMorning = am.isChecked();

        //sounds
        vibrate.setChecked(load("vibrate"));
        sound.setChecked(load("sound"));

        //seekbar
        changedProgress = mPrefs.getInt("seekBar", 0);
        TextView displayValue = (TextView) findViewById(R.id.seekBarTextView);
        displayValue.setText("" + changedProgress);
        alertTime.setProgress(changedProgress);

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

    //Alert Time seekbar
    private SeekBar.OnSeekBarChangeListener alertTimeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            changedProgress = progress;
            TextView displayValue = (TextView) findViewById(R.id.seekBarTextView);
            displayValue.setText("" + changedProgress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            //TODO
        }
    };//end alert time seekbar

    //Weekday Checkbox
    public void addListenerWeekday() {

        weekday = (CheckBox) findViewById(R.id.weekday);

        weekday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    for (int i = 0; i < 5; i ++){
                        weekdays[i] = true;
                    }
                }
            }
        });
    }//end weekday checkbox

    //Weekend check box
    public void addListenerWeekend() {

        weekend = (CheckBox) findViewById(R.id.weekend);

        weekend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    weekdays[5] = weekdays[6] = true;
                }
            }
        });
    }//end weekend checkbox

    public void showInfo (View v){
        String message = "\"Time Frame\" refers to the length of " +
              "time in a day Alert Me! will check the " +
              "forecast for your specified conditions.";
        Toast.makeText(TimeFrame.this, message, Toast.LENGTH_LONG).show();
    }


    //12-hour Checkbox
    public void addListenerTimeFrame12() {

        twelveHour = (CheckBox) findViewById(R.id.checkBox8);

        twelveHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && twentyFourHour.isChecked()) {
                    twelveHour.setChecked(false);
                } else {
                    timeFrame = 12;
                }
            }
        });
    }//end 12-hour checkbox

    //24-hour check box
    public void addListenerTimeFrame24() {

        twentyFourHour = (CheckBox) findViewById(R.id.checkBox7);

        twentyFourHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && twelveHour.isChecked()) {
                    twentyFourHour.setChecked(false);
                } else {
                    timeFrame = 24;
                }
            }
        });
    }//end 24-hour checkbox

    //AM Checkbox
    public void addListenerAm() {

        am = (CheckBox) findViewById(R.id.checkBox9);

        am.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && pm.isChecked()) {
                    am.setChecked(false);
                } else {
                    inMorning = true;
                }
            }
        });
    }//end am checkbox

    //PM check box
    public void addListenerPm() {

        pm = (CheckBox) findViewById(R.id.checkBox10);

        pm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && am.isChecked()) {
                    pm.setChecked(false);
                } else {
                    inMorning = false;
                }
            }
        });
    }//end pm checkbox

    //Sound switch
    public void addListenerSound() {

        sound = (Switch) findViewById(R.id.switch1);

        sound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((Switch) v).isChecked()) {
                   currentAlarm.toggleSound();
                }
            }
        });
    }//end sound switch

    //vibrate Checkbox
    public void addListenerVibrate() {

        vibrate = (CheckBox) findViewById(R.id.checkBox11);

        vibrate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    currentAlarm.toggleVibrate();
                }
            }
        });
    }//end vibrate checkbox

    private void saveInfo (){
        currentAlarm.setAlertTime(changedProgress);
        currentAlarm.setDaysSelected(weekdays);
        currentAlarm.setTimeFrame(timeFrame);
        currentAlarm.setAmPm(inMorning);
    }

    public void toAlarmList(View view){
        saveInfo();

        Intent intent = new Intent(this, AlarmList.class);
        startActivity(intent);
    }

}
