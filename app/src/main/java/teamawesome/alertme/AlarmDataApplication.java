package teamawesome.alertme;

import android.app.Application;

import teamawesome.alertme.Utility.AlertMeMetadataSingleton;


public class AlarmDataApplication extends Application {
    // Refer to http://www.devahead.com/blog/2011/06/extending-the-android-application-class-and-dealing-with-singleton/
    // for why we need to use the Application class for singletons

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the singleton so the instance is bound to the application process
        AlertMeMetadataSingleton.initInstance();
    }

}
