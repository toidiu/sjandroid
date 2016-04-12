package sandjentrance.com.sj.actions;

import android.os.Bundle;

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

import sandjentrance.com.sj.actions.MoveFileAction_.PsMoveFileAction;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "newParentId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Prime"),
        @EventClass(classPostFix = "Success"),
        @EventClass(classPostFix = "Failure")
})

public class MoveFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private Drive driveService;

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        MoveFileActionHelper helper = PsMoveFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        driveService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("SJ")
                .build();

        if (fileMoved(driveService, moveFolderHelper.fileId, helper.newParentId())) {
            moveFolderHelper.moveDone();
            return new MoveFileActionEventSuccess();
        } else {
            return new MoveFileActionEventFailure();
        }
    }


}
