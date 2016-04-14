package sandjentrance.com.sj.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by toidiu on 4/1/16.
 */
public class NetworkUtils {
    public final static String apiKey = "2f17f1de747f028a421e54251d9429815ebd5495";

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    public static void prep() throws IOException {
        String endpoint = "https://sandbox.zamzar.com/v1/formats/dwg";
        Request request = new Request.Builder()
                .url(endpoint)
                .get()
                .build();


        Response execute = getClient().newCall(request).execute();
        Log.d("----------", execute.body().string());
    }


    public static void doit() throws Exception {
        String endpoint = "https://sandbox.zamzar.com/v1/jobs";
        File download = new File(Environment.getExternalStorageDirectory(), "Download");
        File sourceFile = new File(download, "test.dwg");
        String targetFormat = "pdf";

//        HttpEntity requestContent = MultipartEntityBuilder.create()
//                .addPart("source_file", new FileBody(new File(sourceFile)))
//                .addPart("target_format", new StringBody(targetFormat, ContentType.TEXT_PLAIN))
//                .build();
//        HttpPost request = new HttpPost(endpoint);
//        request.setEntity(requestContent);
//
        MediaType MEDIA_TYPE_DWG = MediaType.parse("application/acad");
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("source_file", sourceFile.getName(), RequestBody.create(MEDIA_TYPE_DWG, sourceFile))
                .addFormDataPart("target_format", targetFormat)
//                .addPart("target_format", RequestBody.create(MEDIA_TYPE_TEXT, targetFormat))
//                .addFormDataPart("title", "Square Logo")
//                .addFormDataPart("image", "logo-square.png", RequestBody.create(MEDIA_TYPE_PNG, new File("website/static/logo-square.png")))
                .build();

        Request request = new Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build();

        Response execute = getClient().newCall(request).execute();
        Log.d("----------", execute.body().string());


//        // Finalise response and client
//        response.close();
//        httpClient.close();
    }


    private static OkHttpClient getClient() {
        //------------http://stackoverflow.com/a/34819354/2369122
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
//                if (responseCount(response) >= 3) {
//                    return null; // If we've failed 3 times, give up. - in real life, never give up!!
//                }
                String credential = Credentials.basic(apiKey, "");
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        });
        client.connectTimeout(10, TimeUnit.SECONDS);
        client.writeTimeout(10, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
        return client.build();
    }
}
