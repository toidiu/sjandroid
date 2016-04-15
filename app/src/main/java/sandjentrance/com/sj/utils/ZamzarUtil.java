package sandjentrance.com.sj.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import sandjentrance.com.sj.SecretUtils;

public class ZamzarUtil {

    //region Helper----------------------
    private static OkHttpClient okHttpClient;

    private static OkHttpClient getClientInstance() {
        //------------http://stackoverflow.com/a/34819354/2369122
        if (okHttpClient != null) {
            return okHttpClient;
        }

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(SecretUtils.apiKey, "");
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        });
        client.connectTimeout(10, TimeUnit.SECONDS);
        client.writeTimeout(10, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        okHttpClient = client.build();
        return okHttpClient;
    }
    //endregion

    //region API CALLS----------------------
    public static void info() throws IOException {
        String endpoint = "https://sandbox.zamzar.com/v1/formats/dwg";
        Request request = new Request.Builder()
                .url(endpoint).get().build();

        Response execute = getClientInstance().newCall(request).execute();
        Log.d("----------", execute.body().string());
    }

    public static void convert(File sourceFile) throws Exception {
        String endpoint = "https://sandbox.zamzar.com/v1/jobs";

        String targetFormat = "pdf";

        MediaType MEDIA_TYPE_DWG = MediaType.parse("application/acad");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("source_file", sourceFile.getName(), RequestBody.create(MEDIA_TYPE_DWG, sourceFile))
                .addFormDataPart("target_format", targetFormat)
                .build();

        Request request = new Request.Builder()
                .url(endpoint).post(requestBody).build();

        Response execute = getClientInstance().newCall(request).execute();
        Log.d("----------", execute.body().string());
    }

    public static void askStatus(int jobId) throws Exception {
        String endpoint = "https://sandbox.zamzar.com/v1/jobs/" + jobId;
        Request request = new Request.Builder()
                .url(endpoint).get().build();
        Response execute = getClientInstance().newCall(request).execute();
        Log.d("----------", execute.body().string());
    }

    public static void download(int fileId, File localFile) throws IOException {
        String endpoint = "https://sandbox.zamzar.com/v1/files/" + fileId + "/content";

        Request request = new Request.Builder().url(endpoint).get().build();
        Response execute = getClientInstance().newCall(request).execute();


        BufferedInputStream bis = new BufferedInputStream(execute.body().byteStream());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localFile));
        int inByte;
        while ((inByte = bis.read()) != -1) {
            bos.write(inByte);
        }

        Log.d("----------", "File downloaded");

        execute.body().close();
        bos.close();
        bis.close();
    }
    //endregion

}
