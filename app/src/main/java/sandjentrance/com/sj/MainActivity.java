package sandjentrance.com.sj;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.EventService;
import com.edisonwang.ps.lib.PennStation;

import sandjentrance.com.sj.TestAction_.PsTestAction;


@EventListener(producers = {
        TestAction.class
})
public class MainActivity extends AppCompatActivity {


    private final MainActivityEventListener mListener = new MainActivityEventListener() {

        @Override
        public void onEventMainThread(TestActionEventSuccess event) {
            Toast.makeText(MainActivity.this, "there", Toast.LENGTH_SHORT).show();
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        testStruff();
        PennStation.requestAction(PsTestAction.helper());

    }

    @Override
    protected void onResume() {
        super.onResume();
        PennStation.registerListener(mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PennStation.unRegisterListener(mListener);
    }




//    private void testStruff() {
//        // create a new renderer
//        File d = getFilesDir();
//
//        Toast.makeText(MainActivity.this, d.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//
//        ParcelFileDescriptor ret = null;
//        try {
//            ret = ParcelFileDescriptor.open(d,
//                    ParcelFileDescriptor.MODE_READ_ONLY);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        PdfRenderer renderer = null;
//        try {
//            renderer = new PdfRenderer(ret);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // let us just render all pages
//        final int pageCount = renderer.getPageCount();
//        for (int i = 0; i < pageCount; i++) {
//            PdfRenderer.Page page = renderer.openPage(i);
//
//            // say we render for showing on the screen
//            page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//
//            // do stuff with the bitmap
//
//            // close the page
//            page.close();
//        }
//
//        // close the renderer
//        renderer.close();
//    }


}
