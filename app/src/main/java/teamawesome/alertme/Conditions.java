package teamawesome.alertme;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Conditions extends ActionBarActivity {

    private WeatherAlarm currentAlarm;
    private int currentAlarmIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        int alarmIndex = getIntent().getIntExtra("alarmIndex", -1);
        if (alarmIndex >= 0 && alarmIndex < AlarmDataSingleton.getInstance().size()) {
            currentAlarm = AlarmDataSingleton.getInstance().getAlarm(alarmIndex);
            currentAlarmIndex = alarmIndex;
        } else {
            currentAlarm = AlarmDataSingleton.getInstance().getAlarm(0);
            currentAlarmIndex = 0;
            //throw new AssertionError("Conditions: Failed to access AlarmDataSingleton list at " + alarmIndex);
        }
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

    public void toTimeFrame(View view){
        Intent intent = new Intent(this, TimeFrame.class);
        intent.putExtra("alarmIndex", currentAlarmIndex);
        startActivity(intent);
    }
}
