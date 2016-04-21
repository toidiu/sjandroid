package sandjentrance.com.sj.utils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.actions.BaseAction;

/**
 * Created by toidiu on 4/13/16.
 */
public class UtilFile {

    @Nullable
    public static File getLocalFile(String idOrName, String mime) {
        String name = null;
        if (mime.equals(BaseAction.MIME_PDF)) {
            name = idOrName + ".pdf";
        } else if (mime.equals(BaseAction.MIME_PNG) || mime.equals(BaseAction.MIME_JPEG)) {
            name = idOrName + ".jpg";
        }

        File sj = getLocalAppFolder();
        if (name == null) {
            return null;
        } else {
            return new File(sj, name);
        }
    }

    public static File getCachedFile(String idOrName, String mime) {
        String name = null;
        if (mime.equals(BaseAction.MIME_PDF) && !idOrName.endsWith(".pdf")) {
            name = idOrName + ".pdf";
        } else if (mime.equals(BaseAction.MIME_PNG) && !idOrName.endsWith(".png") || (!idOrName.endsWith(".jpg") && mime.equals(BaseAction.MIME_JPEG))) {
            name = idOrName + ".jpg";
        } else {
            name = idOrName;
        }

        File cacheAppFolder = getCustomCacheAppFolder();
        File file = new File(cacheAppFolder, name);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    @NonNull
    private static File getCustomCacheAppFolder() {
        File cache = SJApplication.appContext.getExternalCacheDir();
        cache.mkdirs();
        return cache;
    }

    @NonNull
    public static File getLocalAppFolder() {
        File sj = new File(Environment.getExternalStorageDirectory(), "SJ");
        sj.mkdirs();
        return sj;
    }

    public static boolean deleteLocalFile(File file) {
        if (!file.exists() || file.delete()) {
            return true;
        } else {
            return false;
        }
    }


    public static File copyAssetsFile(AssetManager assetManager, String assetFileName, String newName, String mime) {
        File localFile = getLocalFile(newName, mime);

        try {
            InputStream in = assetManager.open(assetFileName);
            OutputStream out = new FileOutputStream(localFile);
//                    openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return localFile;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tag", e.getMessage());
            return null;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
