package sandjentrance.com.sj.actions;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.edisonwang.ps.annotations.ClassField;
import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sandjentrance.com.sj.actions.MoveFileAction_.PsMoveFileAction;
import sandjentrance.com.sj.actions.UploadFileAction_.PsUploadFileAction;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "filePath", kind = @Kind(clazz = String.class), required = true),
        @ClassField(name = "fileMime", kind = @Kind(clazz = String.class), required = true),
        @ClassField(name = "parentId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success"),
        @EventClass(classPostFix = "Failure")
})

public class UploadFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private Drive driveService;

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        UploadFileActionHelper helper = PsUploadFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        driveService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("SJ")
                .build();


        //Local file
        FileContent mediaContent = new FileContent(helper.fileMime(), new java.io.File(helper.filePath()));

        //Drive file
        List<String> parents = new ArrayList<>();
        parents.add(helper.parentId());
        File fileMetadata = new File();
        fileMetadata.setName("My Report");
        fileMetadata.setMimeType(helper.fileMime());
        fileMetadata.setParents(parents);


        try {
            File file = driveService.files().create(fileMetadata, mediaContent)
                    .setFields(QUERY_FIELDS)
                    .execute();
            return new UploadFileActionEventSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            return new UploadFileActionEventFailure();
        }
    }

    @NonNull
    public static java.io.File getFile(Context context) {
        java.io.File filesDir = context.getFilesDir();
        java.io.File externalStorageDirectory = Environment.getExternalStorageDirectory();
        Log.d("-----", filesDir.getAbsolutePath());
        Log.d("-----", externalStorageDirectory.getAbsolutePath());

        java.io.File download = new java.io.File(externalStorageDirectory, "Download");
        Log.d("-----", String.valueOf(download.exists()));

        //markme check name
        java.io.File file = new java.io.File(download, "test.JPG");
        Log.d("-----", String.valueOf(file.exists()));
        return file;
    }
}
