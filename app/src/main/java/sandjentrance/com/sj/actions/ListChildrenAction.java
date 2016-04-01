package sandjentrance.com.sj.actions;

import android.os.Bundle;

import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableClassField;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "results", kind = @Kind(clazz = String[].class))
        }),
        @EventClass(classPostFix = "Failure")
})
@RequestActionHelper()
//variables = {
//        @ClassField(name = "cred", kind = @Kind(clazz = GoogleAccountCredential.class), required = true)
//}

//@EventProducer(generated = {
//        @EventClass(classPostFix = "Success", fields = {
//                @ParcelableClassField(name = "results", kind = @Kind(clazz = Tumblr.Post[].class))
//        }),
//        @EventClass(classPostFix = "Failure", fields = {
//                @ParcelableClassField(name = "message", kind = @Kind(clazz = String.class))
//        })
//})
public class ListChildrenAction extends BaseAction {

    private Drive driveService;

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);

        if (credential.getSelectedAccountName() == null) {
            return new ListChildrenActionEventFailure();
        }

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        driveService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Drive API Android Quickstart")
                .build();

        try {
            List<String> dataFromApi = getDataFromApi();
            String[] array = dataFromApi.toArray(new String[dataFromApi.size()]);
            return new ListChildrenActionEventSuccess(array);
        } catch (IOException e) {
            e.printStackTrace();
            return new ListChildrenActionEventFailure();
        }

    }

    private List<String> getDataFromApi() throws IOException {
        String projId = "'0Bx-nVlmnGRT3b3hfMGhPLWVKYkE'";

        // Get a list of up to 10 files.
        List<String> fileInfo = new ArrayList<String>();

        FileList result = driveService.files().list()
//                .setQ("name contains 'SJ Jobs'")
                .setQ(projId + " in parents")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name, modifiedTime, owners)")
                .setPageSize(10)
//                .setMaxResults(10)
                .execute();

//        ChildList root = driveService.children()
//                .list("root")
//                .setFields("nextPageToken, files(id, name, modifiedTime, owners)")
//                .execute();
//        List<ChildReference> items = root.getItems();

        List<File> files = result.getFiles();
        if (files != null) {
            for (File file : files) {
                fileInfo.add(String.format("%s (%s)\n",
                        file.getName(), file.getId()));
            }
        }
        return fileInfo;
    }


}
