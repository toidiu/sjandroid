package sandjentrance.com.sj;

import android.app.Application;

import com.edisonwang.ps.lib.EventService;
import com.edisonwang.ps.lib.PennStation;

/**
 * Created by toidiu on 3/28/16.
 */
public class SJApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PennStation.init(this, new PennStation.PennStationOptions(EventService.class)); //or extended class.
    }
}
