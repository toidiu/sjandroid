package sandjentrance.com.sj.actions;

import android.os.Bundle;
import android.os.Parcelable;

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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import org.parceler.Parcels;

import java.io.IOException;

import sandjentrance.com.sj.actions.RenameFileAction_.PsRenameFileAction;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        //fixme switch to ParcelablePlease!!!
        @ClassField(name = "file", kind = @Kind(clazz = Parcelable.class), required = true),
        @ClassField(name = "newName", kind = @Kind(clazz = String.class), required = true)
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success"),
        @EventClass(classPostFix = "Failure")
})

public class RenameFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private Drive driveService;

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        RenameFileActionHelper helper = PsRenameFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
        FileObj fileObj = Parcels.unwrap(helper.file());

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        driveService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("SJ")
                .build();

        File fileMetadata = new File();
        fileMetadata.setName(helper.newName());

        try {
            driveService.files().update(fileObj.id, fileMetadata).execute();
            renameFileHelper.parentId = fileObj.parent;
            return new RenameFileActionEventSuccess();
        } catch (IOException e) {
            e.printStackTrace();

            return new RenameFileActionEventFailure();
        }

    }

}
