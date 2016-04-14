package sandjentrance.com.sj.utils;
// import Apache HTTP Client v 4.3

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

// import JSON

public class ZamzarUtil {
    public final static String apiKey = "2f17f1de747f028a421e54251d9429815ebd5495";
    public final static String endpoint = "https://sandbox.zamzar.com/v1/formats/dwg";

    public static void doo() throws Exception {
        // Create HTTP client and request object
        CloseableHttpClient httpClient = getHttpClient(apiKey);
        HttpGet request = new HttpGet(endpoint);

        // Make request
        CloseableHttpResponse response = httpClient.execute(request);

        // Extract body from response
        HttpEntity responseContent = response.getEntity();
        String result = EntityUtils.toString(responseContent, "UTF-8");

        // Parse result as JSON
        JSONObject json = new JSONObject(result);

        // Print result
        System.out.println(json);

        // Finalise response and client
        response.close();
        httpClient.close();
    }

//    public static void tell() throws Exception {
//        String apiKey = "2f17f1de747f028a421e54251d9429815ebd5495";
//        String endpoint = "https://sandbox.zamzar.com/v1/jobs";
//        String sourceFile = "/tmp/portrait.gif";
//        String targetFormat = "png";
//
//        // Create HTTP client and request object
//        CloseableHttpClient httpClient = getHttpClient(apiKey);
//
//        HttpEntity requestContent = MultipartEntityBuilder.create()
//                .addPart("source_file", new FileBody(new File(sourceFile)))
//                .addPart("target_format", new StringBody(targetFormat, ContentType.TEXT_PLAIN))
//                .build();
//        HttpPost request = new HttpPost(endpoint);
//        request.setEntity(requestContent);
//
//        // Make request
//        CloseableHttpResponse response = httpClient.execute(request);
//
//        // Extract body from response
//        HttpEntity responseContent = response.getEntity();
//        String result = EntityUtils.toString(responseContent, "UTF-8");
//
//        // Parse result as JSON
//        JSONObject json = new JSONObject(result);
//
//        // Print result
//        System.out.println(json);
//
//        // Finalise response and client
//        response.close();
//        httpClient.close();
//    }


    public static void ask() throws Exception {
        int jobId = 15;
        String endpoint = "https://sandbox.zamzar.com/v1/jobs/" + jobId;

        // Create HTTP client and request object
        CloseableHttpClient httpClient = getHttpClient(apiKey);
        HttpGet request = new HttpGet(endpoint);

        // Make request
        CloseableHttpResponse response = httpClient.execute(request);

        // Extract body from response
        HttpEntity responseContent = response.getEntity();
        String result = EntityUtils.toString(responseContent, "UTF-8");

        // Parse result as JSON
        JSONObject json = new JSONObject(result);

        // Print result
        System.out.println(json);

        // Finalise response and client
        response.close();
        httpClient.close();
    }


    public static void dl() throws Exception {
        int fileId = 3;
        String endpoint = "https://sandbox.zamzar.com/v1/files/" + fileId + "/content";
        String localFilename = "/tmp/portrait.png";

        // Create HTTP client and request object
        CloseableHttpClient httpClient = getHttpClient(apiKey);
        HttpGet request = new HttpGet(endpoint);

        // Make request
        CloseableHttpResponse response = httpClient.execute(request);

        // Extract body from response
        HttpEntity responseContent = response.getEntity();

        // Save response content to file on local disk
        BufferedInputStream bis = new BufferedInputStream(responseContent.getContent());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localFilename));
        int inByte;
        while ((inByte = bis.read()) != -1) {
            bos.write(inByte);
        }

        // Print success message
        System.out.println("File downloaded");

        // Finalise response, client and streams
        response.close();
        httpClient.close();
        bos.close();
        bis.close();
    }


    // Creates a HTTP client object that always makes requests
    // that are signed with the specified API key via Basic Auth
    public static CloseableHttpClient getHttpClient(String apiKey) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(apiKey, ""));

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        return httpClient;
    }
}
