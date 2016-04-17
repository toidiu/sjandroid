package sandjentrance.com.sj.dagger;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequestInitializer;
import com.google.api.services.drive.DriveScopes;

import java.util.Arrays;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sandjentrance.com.sj.database.DatabaseHelper;
import sandjentrance.com.sj.utils.ArchiveFileHelper;
import sandjentrance.com.sj.utils.MoveFolderHelper;
import sandjentrance.com.sj.utils.Prefs;
import sandjentrance.com.sj.utils.RenameFileHelper;

/**
 * Created by toidiu on 1/13/16.
 */
@Module()
public class AppModule {
    //~=~=~=~=~=~=~=~=~=~=~=~=CONSTANTS

    @Provides
    @Singleton
    Prefs providesPrefs(Context context) {
        return new Prefs(context);
    }

    @Provides
    @Singleton
    GoogleAccountCredential providesCredentials(Context context, Prefs prefs) {
        final String[] SCOPES = {DriveScopes.DRIVE};

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context.getApplicationContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(prefs.getUser())
                .setBackOff(new ExponentialBackOff());

        return credential;
    }

    @Provides
    Drive provideDriveService(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//        HttpTransport transport = AndroidHttp.newCompatibleTransport();
//        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Drive mService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Drive API Android Quickstart")
                .build();

//        Drive driveService = new Drive.Builder(
//                transport, jsonFactory, credential)
//                .setApplicationName("SJ")
//                .build();

        return mService;
    }

    @Provides
    @Singleton
    MoveFolderHelper provideMoveFolderHelper() {
        return new MoveFolderHelper();
    }

    @Provides
    @Singleton
    ArchiveFileHelper providesArchiveHelper() {
        return new ArchiveFileHelper();
    }

    @Provides
    @Singleton
    RenameFileHelper providesRenameHelper() {
        return new RenameFileHelper();
    }

    @Provides
    @Singleton
    DatabaseHelper providesDatabaseHelper(Context context) {
        return DatabaseHelper.getInstance(context);
    }

}
