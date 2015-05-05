package teamawesome.alertme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import teamawesome.alertme.Utility.AlertMeAlarm;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;

public class AlarmEdit extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        AlarmListAdapter alarmListAdapter = new AlarmListAdapter();
        ListView alarmList = (ListView) findViewById(R.id.alarmListView2);
        alarmList.setAdapter(alarmListAdapter);
    }


    public void toAlarmList(View view){

        Intent intent = new Intent(this, AlarmList.class);
        startActivity(intent);
    }

    public void deleteAlarms(View view){
        String message = "You currently have no alarms selected.";
        Toast.makeText(AlarmEdit.this, message, Toast.LENGTH_LONG).show();
    }


    public class AlarmListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return AlertMeMetadataSingleton.getInstance().size();
        }

        @Override
        public Object getItem(int position) {
            return AlertMeMetadataSingleton.getInstance().getAlarm(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) AlarmEdit.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.alarm_edit_item, parent, false);
            }

            AlertMeAlarm currentAlarm = AlertMeMetadataSingleton.getInstance().getAlarm(position);

            Button alarmNameButton = (Button) convertView.findViewById(R.id.alarmName);
            alarmNameButton.setTag(position);
            alarmNameButton.setText(currentAlarm.getName());
            alarmNameButton.setTextSize(20);

            CheckBox alarmSelect = (CheckBox) convertView.findViewById(R.id.alarmEdit);
            alarmSelect.setOnCheckedChangeListener(alarmSelectListener);

            Button alarmToTimeFrame = (Button) convertView.findViewById(R.id.alarmToTimeFrame);
            alarmToTimeFrame.setTag(position);
            alarmToTimeFrame.setText("EDIT TIME FRAME     >");
            alarmToTimeFrame.setTextSize(12);

            return convertView;
        }

        private CheckBox.OnCheckedChangeListener alarmSelectListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //delete alarm in list
            }
        };
    }
}