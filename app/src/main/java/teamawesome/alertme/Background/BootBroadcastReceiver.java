package teamawesome.alertme.Background;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import teamawesome.alertme.Utility.CalendarWrapper;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long sixHoursInMillis = 6 * 60 * 60 * 1000;
        long sixHoursFromNow = CalendarWrapper.getInstance().getTimeInMillis() + sixHoursInMillis;
        WeatherCheckerBroadcastReceiver.setWeatherChecker(context, sixHoursFromNow, sixHoursInMillis);
    }
}
