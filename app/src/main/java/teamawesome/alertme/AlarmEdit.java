package teamawesome.alertme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

    public void editNewAlarmDialog(int index) {
        AlertDialog.Builder locationDialogBuilder = new AlertDialog.Builder(AlarmEdit.this);

        locationDialogBuilder.setTitle("Edit the Alarm Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        final int indexWHY = index;

        locationDialogBuilder
                .setMessage("")
                .setCancelable(false)
                .setView(input)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AlertMeAlarm alarmClicked = AlertMeMetadataSingleton.getInstance().getAlarm(indexWHY);
                        alarmClicked.setName(input.getText().toString());
                        Button alarmNameButton = (Button) findViewById(R.id.alarmName);
                        alarmNameButton.setText("   " + input.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Create location dialog
        AlertDialog alertDialog = locationDialogBuilder.create();
        alertDialog.show();
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
            alarmNameButton.setText("   " + currentAlarm.getName());
            alarmNameButton.setTextSize(20);
            alarmNameButton.setOnClickListener(alarmNameButtonListener);

            CheckBox alarmSelect = (CheckBox) convertView.findViewById(R.id.alarmEdit);
            alarmSelect.setOnCheckedChangeListener(alarmSelectListener);


            return convertView;
        }

        private Button.OnClickListener alarmNameButtonListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNewAlarmDialog((Integer) view.getTag());
            }
        };

        private CheckBox.OnCheckedChangeListener alarmSelectListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //delete alarm in list
            }
        };
    }
}
