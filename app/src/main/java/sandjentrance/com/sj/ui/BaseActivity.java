package sandjentrance.com.sj.ui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import javax.inject.Inject;

import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.utils.Prefs;

public class BaseActivity extends AppCompatActivity {

    @Inject
    Prefs prefs;

    @Inject
    GoogleAccountCredential credential;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SJApplication) getApplication()).getAppComponent().inject(this);
    }


}