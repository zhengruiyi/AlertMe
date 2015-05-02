package teamawesome.alertme;

import android.app.Application;
import android.content.Context;

import teamawesome.alertme.Background.WeatherCheckerBroadcastReceiver;
import teamawesome.alertme.Utility.AlertMeMetadataSingleton;
import teamawesome.alertme.Utility.CalendarWrapper;


public class AlertMeApplication extends Application {
    // Refer to http://www.devahead.com/blog/2011/06/extending-the-android-application-class-and-dealing-with-singleton/
    // for why we need to use the Application class for singletons

    private static AlertMeApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize the singleton so the instance is bound to the application process
        AlertMeMetadataSingleton.initInstance();

        // Start WeatherChecker
        long sixHoursInMillis = 6 * 60 * 60 * 1000;
        long sixHoursFromNow = CalendarWrapper.getInstance().getTimeInMillis() + sixHoursInMillis;
        WeatherCheckerBroadcastReceiver.setWeatherChecker(sixHoursFromNow, sixHoursInMillis);
    }

    public static Context getContext() {
        return instance;
    }

}
