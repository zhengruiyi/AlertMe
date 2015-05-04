package teamawesome.alertme.Utility;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import teamawesome.alertme.AlarmList;
import teamawesome.alertme.R;

public class AlarmEdit extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);
    }


    public void toAlarmList(View view){

        Intent intent = new Intent(this, AlarmList.class);
        startActivity(intent);
    }

    public void deleteAlarms(View view){
        String message = "You currently have no alarms to delete.";
        Toast.makeText(AlarmEdit.this, message, Toast.LENGTH_LONG).show();
    }
}
