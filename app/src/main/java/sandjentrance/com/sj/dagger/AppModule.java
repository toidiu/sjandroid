package sandjentrance.com.sj.dagger;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import java.util.Arrays;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sandjentrance.com.sj.utils.MoveFolderHelper;
import sandjentrance.com.sj.utils.Prefs;

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
//
//        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
//                context.getApplicationContext(), Arrays.asList(SCOPES));
//                .setBackOff(new ExponentialBackOff());
//
//        if (prefs.getUser() != null) {
//            credential.setSelectedAccountName(prefs.getUser());
//        }

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


        return credential;
//        return null;
    }

    @Provides
    @Singleton
    MoveFolderHelper provideMoveFolderHelper() {
        return new MoveFolderHelper();
    }

}
