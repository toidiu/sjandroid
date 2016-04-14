package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.utils.ArchiveFileHelper;
import sandjentrance.com.sj.utils.BgImageLoader;
import sandjentrance.com.sj.utils.MoveFolderHelper;
import sandjentrance.com.sj.utils.Prefs;
import sandjentrance.com.sj.utils.RenameFileHelper;

public class BaseActivity extends AppCompatActivity {

    public static final int OPEN_FILE_REQUEST_CODE = 23415;

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

    protected void openLocalFile(LocalFileObj localFileObj, View view) {
        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType(localFileObj.mime);
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (list.isEmpty()) {
            String msg = localFileObj.mime.equals(BaseAction.MIME_PDF) ? getString(R.string.install_pdf_msg) : getString(R.string.install_img_msg);
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
        } else {
            File file = new File(localFileObj.localPath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), localFileObj.mime);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);
        }
    }
}