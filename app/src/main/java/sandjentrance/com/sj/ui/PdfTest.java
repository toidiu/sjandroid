package sandjentrance.com.sj.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.radaee.pdf.Global;
import com.radaee.reader.PDFViewAct;

import java.io.File;

import sandjentrance.com.sj.R;

/**
 * Created by toidiu on 4/7/16.
 */
public class PdfTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        File file = getFile();

        Global.Init(this);
        Intent intent = new Intent();
        intent.setClass(this, PDFViewAct.class);
        intent.putExtra("PDFPath", file.getAbsolutePath());
        startActivity(intent);
    }

    @NonNull
    public File getFile() {
        File filesDir = getFilesDir();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        Log.d("-----", filesDir.getAbsolutePath());
        Log.d("-----", externalStorageDirectory.getAbsolutePath());

        File download = new File(externalStorageDirectory, "Download");
        Log.d("-----", String.valueOf(download.exists()));

        //markme check name
        File file = new File(download, "test.JPG");
        Log.d("-----", String.valueOf(file.exists()));
        return file;
    }
}
