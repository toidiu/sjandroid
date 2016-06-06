package sandjentrance.com.sj;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.edisonwang.ps.lib.EventService;
import com.edisonwang.ps.lib.PennStation;

import io.fabric.sdk.android.Fabric;
import sandjentrance.com.sj.dagger.ApiModules;
import sandjentrance.com.sj.dagger.AppModule;
import sandjentrance.com.sj.dagger.ApplicationComponent;
import sandjentrance.com.sj.dagger.ContextModule;
import sandjentrance.com.sj.dagger.DaggerApplicationComponent;

/**
 * Created by toidiu on 3/28/16.
 */
public class SJApplication extends Application {

    public static Context appContext;
    ApplicationComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        appContext = this.getApplicationContext();

        PennStation.init(this, new PennStation.PennStationOptions(EventService.class)); //or extended class.

        appComponent = DaggerApplicationComponent.builder()
                .apiModules(new ApiModules())
                .contextModule(new ContextModule(this.getApplicationContext()))
                .appModule(new AppModule())
                .build();
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
