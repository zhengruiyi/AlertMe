package teamawesome.alertme.Background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class AlarmWeatherCheckService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
