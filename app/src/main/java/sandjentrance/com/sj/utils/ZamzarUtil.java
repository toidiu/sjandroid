package sandjentrance.com.sj.utils;
// import Apache HTTP Client v 4.3

import android.os.Environment;
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

// import JSON

public class ZamzarUtil {
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
        File sourceFile = new File(download, "arch2.dwg");
        String targetFormat = "pdf";

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
    }

    public static void ask(int jobId) throws Exception {
        String endpoint = "https://sandbox.zamzar.com/v1/jobs/" + jobId;
        Request request = new Request.Builder()
                .url(endpoint)
                .get()
                .build();
        Response execute = getClient().newCall(request).execute();
        Log.d("----------", execute.body().string());
    }

    public static void dl(int fileId) throws IOException {
        String endpoint = "https://sandbox.zamzar.com/v1/files/" + fileId + "/content";
        File sj = new File(Environment.getExternalStorageDirectory(), "SJ");
        File localFilename = new File(sj, "test.pdf");

//        // Create HTTP client and request object
//        CloseableHttpClient httpClient = getHttpClient(NetworkUtils.apiKey);
//        HttpGet request = new HttpGet(endpoint);
//
//        // Make request
//        CloseableHttpResponse response = httpClient.execute(request);
//
//        // Extract body from response
//        HttpEntity responseContent = response.getEntity();
//
//        // Save response content to file on local disk
//        BufferedInputStream bis = new BufferedInputStream(responseContent.getContent());
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localFilename));
//        int inByte;
//        while ((inByte = bis.read()) != -1) {
//            bos.write(inByte);
//        }


        Request request = new Request.Builder().url(endpoint).get()
//                .addHeader("Content-Type", "application/json")
                .build();
        Response execute = getClient().newCall(request).execute();

//        InputStream in = execute.body().byteStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        String result, line = reader.readLine();
//        result = line;
//        while((line = reader.readLine()) != null) {
//            result += line;
//        }


        BufferedInputStream bis = new BufferedInputStream(execute.body().byteStream());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localFilename));
        int inByte;
        while ((inByte = bis.read()) != -1) {
            bos.write(inByte);
        }

        Log.d("----------", "File downloaded");

        execute.body().close();
        bos.close();
        bis.close();
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
                String credential = Credentials.basic(SecretUtils.apiKey, "");
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        });
        client.connectTimeout(10, TimeUnit.SECONDS);
        client.writeTimeout(10, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
        return client.build();
    }
}
