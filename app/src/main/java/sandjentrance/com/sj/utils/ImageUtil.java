package sandjentrance.com.sj.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by toidiu on 2/26/16.
 */
public class ImageUtil {

    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
//fixme !!! make sure we dont need this
//        http://stackoverflow.com/questions/3879992/get-bitmap-from-an-uri-android
//        bm.recycle();
        return resizedBitmap;
    }

    public static File getAvatarFile(Context context) {
        String USER_IMG_FILE_NAME = "userImg.png";
        return new File(context.getFilesDir(), USER_IMG_FILE_NAME);
    }

    public static File getTempFile(Context context) {
        File images = context.getExternalFilesDir("images");
        images.mkdirs();
        return new File(images, "temp.png");
    }

    //--------------------------
    public static void uploadObject(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());
        out.write("aGkgdGhlcmU=");
        out.close();
        int responseCode = connection.getResponseCode();
        System.out.println("Service returned response code " + responseCode);
        System.out.println("Service returned response code " + connection.getResponseMessage());

    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public static int upload(byte[] fileBytes, URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/octet-stream"); // Very important ! It won't work without adding this!
        OutputStream output = connection.getOutputStream();

        InputStream input = new ByteArrayInputStream(fileBytes);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        output.flush();

        Log.d("-----code", String.valueOf(connection.getResponseCode()));
        return connection.getResponseCode();
    }

}
