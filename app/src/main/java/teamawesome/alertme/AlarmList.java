package teamawesome.alertme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;

import java.text.DecimalFormat;

import teamawesome.alertme.Network.JSONWeatherParser;
import teamawesome.alertme.Network.WeatherHttpClient;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;
import teamawesome.alertme.Utility.WeatherForecastData;
import teamawesome.alertme.Utility.AlertMeAlarm;


public class AlarmList extends ActionBarActivity implements LocationListener {

    private Location userLocation;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        AlarmListAdapter alarmListAdapter = new AlarmListAdapter();
        ListView alarmList = (ListView) findViewById(R.id.alarmListView);
        alarmList.setAdapter(alarmListAdapter);

        userLocation = getCurrentLocation();
        if (userLocation == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, this);
        } else {
            onLocationChanged(userLocation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.weatherForecast) {
            createWeatherForecastDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createWeatherForecastDialog() {
        AlertDialog.Builder weatherDataDialog = new AlertDialog.Builder(AlarmList.this);

        weatherDataDialog.setTitle("Weather Forecast for Tomorrow");

        SharedPreferences currentWeatherData = getSharedPreferences("weather_data", MODE_PRIVATE);
        String weatherData;
        weatherData = "Temperature:\t" +
                currentWeatherData.getInt("tomorrowMinTemperatureF", -1) + "\u00b0F" + ", " +
                currentWeatherData.getInt("tomorrowMaxTemperatureF", -1) + "\u00b0F";
        weatherData = weatherData + "\n" + "Precipitation:\t" +
                currentWeatherData.getInt("tomorrowPrecipitationChance", -1) + "%";
        weatherData = weatherData + "\n" + "Wind Speed:\t\t" +
                currentWeatherData.getInt("tomorrowWindSpeedMph", -1) + "mph";
        weatherData = weatherData + "\n\n" + "Latitude:\t\t\t\t\t\t" +
                currentWeatherData.getFloat("locationLatitude", -1);
        weatherData = weatherData + "\n" + "Longitude:\t\t\t\t" +
                currentWeatherData.getFloat("locationLongitude", -1);

        // Set dialog message
        weatherDataDialog
                .setMessage(weatherData)
                .setCancelable(true);

        // Create weather dialog
        AlertDialog alertDialog = weatherDataDialog.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        Log.d("AlarmList Wifi Check", "Wifi connected: " + isWifiConn);
        Log.d("AlarmList Mobile Check", "Mobile connected: " + isMobileConn);

        return isWifiConn || isMobileConn;
    }

    private Location getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location networkLocation = null;
        Location gpsLocation = null;
        Location finalLocation = null;

        if (gpsEnabled) {
            gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (networkEnabled) {
            networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (!gpsEnabled && !networkEnabled) {
            createLocationDialog();
        }

        if (gpsLocation != null && networkLocation != null) {
            if (hasGreaterOrEqualAccuracyThan(gpsLocation, networkLocation)) {
                finalLocation = gpsLocation;
            } else {
                finalLocation = networkLocation;
            }
        } else {
            if (gpsLocation != null) {
                finalLocation = gpsLocation;
            } else if (networkLocation != null) {
                finalLocation = networkLocation;
            }
        }

        if (finalLocation != null ) {
            long thirtyMinutes = 30 * 60 * 1000;
            if (System.currentTimeMillis() - finalLocation.getTime() > thirtyMinutes) {
                return null;
            } else {
                return finalLocation;
            }
        } else {
            return null;
        }
    }

    private boolean hasGreaterOrEqualAccuracyThan(Location a, Location b) {
        return a.getAccuracy() >= b.getAccuracy();
    }

    private void createLocationDialog() {
        AlertDialog.Builder locationDialogBuilder = new AlertDialog.Builder(AlarmList.this);

        locationDialogBuilder.setTitle("Location Services Disabled");

        // Set dialog message
        locationDialogBuilder
                .setMessage("Please enable location services.")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // Start activity to location setting
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // Close dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // Create location dialog
        AlertDialog alertDialog = locationDialogBuilder.create();
        alertDialog.show();
    }

    private String formatForApi(double latitude, double longitude) {
        return new DecimalFormat("###.#").format(latitude) + "," + new DecimalFormat("###.#").format(longitude);
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        double latitude = userLocation.getLatitude();
        double longitude = userLocation.getLongitude();
        String queryLatLon = formatForApi(latitude, longitude);

        AlertMeMetadataSingleton.getInstance().saveLocation(location, this);

        if (isOnline()) {
            JSONWeatherTask task = new JSONWeatherTask();
            task.execute(queryLatLon);
        } else {
            String toastText = "Unable to connect to the network\r\nUsing cached weather data";
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }

        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
                LayoutInflater inflater = (LayoutInflater) AlarmList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.alarm_list_item, parent, false);
            }

            AlertMeAlarm currentAlarm = AlertMeMetadataSingleton.getInstance().getAlarm(position);

            Button alarmNameButton = (Button) convertView.findViewById(R.id.alarmName);
            alarmNameButton.setTag(position);
            alarmNameButton.setText(currentAlarm.getName());
            alarmNameButton.setTextSize(20);
            alarmNameButton.setOnClickListener(alarmNameButtonListener);

            ToggleButton alarmToggle = (ToggleButton) convertView.findViewById(R.id.alarmToggle);
            alarmToggle.setOnClickListener(alarmToggleListener);

            Button alarmToTimeFrame = (Button) convertView.findViewById(R.id.alarmToTimeFrame);
            alarmToTimeFrame.setTag(position);
            alarmToTimeFrame.setText("EDIT TIME FRAME     >");
            alarmToTimeFrame.setTextSize(12);
            alarmToTimeFrame.setOnClickListener(alarmToTimeFrameButtonListener);

            return convertView;
        }

        private Button.OnClickListener alarmNameButtonListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmList.this, Conditions.class);
                int position = (Integer) view.getTag();
                intent.putExtra("alarmIndex", position);
                startActivity(intent);
            }
        };

        private ToggleButton.OnClickListener alarmToggleListener = new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ToggleButton) view).toggle();
            }
        };

        private Button.OnClickListener alarmToTimeFrameButtonListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmList.this, TimeFrame.class);
                int position = (Integer) view.getTag();
                intent.putExtra("alarmIndex", position);
                startActivity(intent);
            }
        };
    }


    private class JSONWeatherTask extends AsyncTask<String, Void, WeatherForecastData> {

        @Override
        protected WeatherForecastData doInBackground(String... params) {
            WeatherForecastData weather = new WeatherForecastData();
            WeatherHttpClient weatherHttp = new WeatherHttpClient();
            String data = weatherHttp.getWeatherData(params[0]);

            try {
                weather = JSONWeatherParser.getWeather(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;
        }

        @Override
        protected void onPostExecute(WeatherForecastData weather) {
            super.onPostExecute(weather);
            AlertMeMetadataSingleton.getInstance().saveWeather(weather, AlarmList.this);
        }
    }

    public void toEdit(View view){
        Intent intent = new Intent(this, AlarmEdit.class);
        startActivity(intent);
    }

    public void addNewAlarm(View view){
        AlertMeMetadataSingleton alarmInstance = AlertMeMetadataSingleton.getInstance();
        alarmInstance.addAlarm();
        Intent intent = new Intent(AlarmList.this, Conditions.class);
        int position = alarmInstance.size() - 1;
        intent.putExtra("alarmIndex", position);
        startActivity(intent);
    }
}
