package sandjentrance.com.sj;

import android.app.Application;
import android.content.Context;

import com.edisonwang.ps.lib.EventService;
import com.edisonwang.ps.lib.PennStation;

import sandjentrance.com.sj.actions.AddUploadFileAction_.PsAddUploadFileAction;
import sandjentrance.com.sj.actions.UploadFileAction_.PsUploadFileAction;
import sandjentrance.com.sj.dagger.ApiModules;
import sandjentrance.com.sj.dagger.AppModule;
import sandjentrance.com.sj.dagger.ApplicationComponent;
import sandjentrance.com.sj.dagger.ContextModule;
import sandjentrance.com.sj.dagger.DaggerApplicationComponent;
import sandjentrance.com.sj.models.FileUploadObj;

/**
 * Created by toidiu on 3/28/16.
 */
public class SJApplication extends Application {

    public static Context appContext;
    ApplicationComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
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
}
