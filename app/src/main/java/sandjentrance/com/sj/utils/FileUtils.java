package sandjentrance.com.sj.utils;

import android.content.Context;

import java.io.File;

import sandjentrance.com.sj.actions.BaseAction;

/**
 * Created by toidiu on 4/13/16.
 */
public class FileUtils {

    public static File getLocalFile(Context context, String id, String mime) {
        String name = null;
        switch (mime) {
            case BaseAction.IMAGE_MIME:
                name = id + ".jpg";
                break;
            case BaseAction.PDF_MIME:
                name = id + ".pdf";
                break;
        }

        assert name != null;
        return new File(context.getFilesDir(), name);
    }


}
