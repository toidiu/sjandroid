package sandjentrance.com.sj.actions;

import android.os.Bundle;
import android.util.Log;

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

import java.io.IOException;
import java.util.List;

import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "searchName", kind = @Kind(clazz = String.class), required = true),
        @ClassField(name = "parentId", kind = @Kind(clazz = String.class), required = true),
        @ClassField(name = "folderOnly", kind = @Kind(clazz = boolean.class), required = true)
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "results", kind = @Kind(clazz = FileObj[].class))
        }),
        @EventClass(classPostFix = "Failure")
})
public class FindFolderChildrenAction extends BaseAction {

    private Drive driveService;


    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        FindFolderChildrenActionHelper helper = PsFindFolderChildrenAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new FindFolderChildrenActionEventFailure();
        }

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        driveService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("SJ")
                .build();

        String search = "name contains '" + helper.searchName() + "'"
                + " and " + "'" + helper.parentId() + "'" + " in parents ";
        if (helper.folderOnly()) {
            search += " and " + "mimeType = '" + FileObj.FOLDER_MIME + "'";
        }

        try {
            List<FileObj> dataFromApi = getDataFromApi(driveService, search);
            FileObj[] array = dataFromApi.toArray(new FileObj[dataFromApi.size()]);
            Log.d("------", String.valueOf(array.length));
            return new FindFolderChildrenActionEventSuccess(array);
        } catch (IOException e) {
            e.printStackTrace();
            return new FindFolderChildrenActionEventFailure();
        }

    }

}
