package teamawesome.alertme;

import android.app.Application;

/*
    Refer to http://www.devahead.com/blog/2011/06/extending-the-android-application-class-and-dealing-with-singleton/
    for why we need to use the Application class for singletons
 */
public class AlarmDataApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    protected void initSingletons() {
        // Initialize the instance of AlarmDataSingleton
        AlarmDataSingleton.initInstance();
    }

    public void customAppMethod() {
        // Custom application method
    }
}
