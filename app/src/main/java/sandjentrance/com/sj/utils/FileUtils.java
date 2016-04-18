package sandjentrance.com.sj.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;

import sandjentrance.com.sj.actions.BaseAction;

/**
 * Created by toidiu on 4/13/16.
 */
public class FileUtils {

    @Nullable
    public static File getLocalFile(Context context, String id, String mime) {
        String name = null;
        if (mime.equals(BaseAction.MIME_PDF)) {
            name = id + ".pdf";
        } else if (mime.equals(BaseAction.MIME_PNG) || mime.equals(BaseAction.MIME_JPEG)) {
            name = id + ".jpg";
        }

//        assert name != null;
//        return new File(context.getFilesDir(), name);
        File sj = new File(Environment.getExternalStorageDirectory(), "SJ");
        sj.mkdirs();
        if (name == null) {
            return null;
        } else {
            return new File(sj, name);
        }
    }

    public static boolean deleteLocalFile(File file) {
        if (!file.exists() || file.delete()) {
            return true;
        } else {
            return false;
        }
    }

}
