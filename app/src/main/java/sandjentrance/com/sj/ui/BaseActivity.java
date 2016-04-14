package sandjentrance.com.sj.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.utils.ArchiveFileHelper;
import sandjentrance.com.sj.utils.BgImageLoader;
import sandjentrance.com.sj.utils.MoveFolderHelper;
import sandjentrance.com.sj.utils.Prefs;
import sandjentrance.com.sj.utils.RenameFileHelper;

public class BaseActivity extends AppCompatActivity {

    @Inject
    Prefs prefs;
    @Inject
    GoogleAccountCredential credential;
    @Inject
    MoveFolderHelper moveFolderHelper;
    @Inject
    RenameFileHelper renameFileHelper;
    @Inject
    ArchiveFileHelper archiveFileHelper;
    @Inject
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SJApplication) getApplication()).getAppComponent().inject(this);
    }


    protected void initBg() {
        final View layout = findViewById(R.id.layout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Picasso.with(BaseActivity.this).load(R.drawable.app_bg).into(new BgImageLoader(getResources(), layout));
            }
        });
    }


}