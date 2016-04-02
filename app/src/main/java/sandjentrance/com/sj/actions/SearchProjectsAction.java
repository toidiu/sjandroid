package sandjentrance.com.sj.actions;

import android.os.Bundle;

import com.edisonwang.ps.annotations.ClassField;
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

import sandjentrance.com.sj.actions.SearchProjectsAction_.PsSearchProjectsAction;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "results", kind = @Kind(clazz = FileObj[].class))
        }),
        @EventClass(classPostFix = "Failure")
})
@RequestActionHelper(variables = {
        @ClassField(name = "searchName", kind = @Kind(clazz = String.class), required = true)
})
public class SearchProjectsAction extends BaseAction {

    private Drive driveService;


    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        SearchProjectsActionHelper helper = PsSearchProjectsAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new SearchProjectsActionEventFailure();
        }

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        driveService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("SJ")
                .build();

        try {
            List<FileObj> dataFromApi = getDataFromApi(helper.searchName());
            FileObj[] array = dataFromApi.toArray(new FileObj[dataFromApi.size()]);
            return new SearchProjectsActionEventSuccess(array);
        } catch (IOException e) {
            e.printStackTrace();
            return new SearchProjectsActionEventFailure();
        }

    }

    private List<FileObj> getDataFromApi(String searchName) throws IOException {

        List<FileObj> retFile = new ArrayList<>();

        FileList result = driveService.files().list()
                .setQ("name contains '" + searchName + "'"
                                //// FIXME: 4/2/16 abstract this to search any folder not just the PROJS
                                + " and " + FileObj.PROJ_ID + " in parents"
                                + " and " + "mimeType = '" + FileObj.FOLDER_MIME + "'"
                )
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name, modifiedTime, owners, mimeType, parents)")
                .setPageSize(10)
                .execute();


        List<File> files = result.getFiles();
        if (files != null) {
            for (File f : files) {
                FileObj object = new FileObj(f);
                retFile.add(object);
            }
        }
        return retFile;
    }


}
