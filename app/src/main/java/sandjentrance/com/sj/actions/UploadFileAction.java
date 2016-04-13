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
import com.google.api.services.drive.model.File;

import sandjentrance.com.sj.actions.UploadFileAction_.PsUploadFileAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.FileUploadObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "fileUploadObj", kind = @Kind(clazz = FileUploadObj.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success"),
        @EventClass(classPostFix = "Failure")
})

public class UploadFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        UploadFileActionHelper helper = PsUploadFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
        FileUploadObj fileUploadObj = helper.fileUploadObj();

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }


        //id fileId null
        File createdFile;
        boolean deleteSatisfied = true;
        if (fileUploadObj.fileId == null) {
            //yes
            createdFile = createFile(fileUploadObj);
        } else {
            //no
            //check if file exists on drive
            FileObj fileById = getFileById(fileUploadObj.fileId);
            if (fileById != null) {
                //yes
                createdFile = createFile(fileUploadObj);
                deleteSatisfied = deleteFile(fileById.id);
            } else {
                //no
                createdFile = createFile(fileUploadObj);
            }
        }

        if (createdFile != null && deleteSatisfied) {
            //fixme delete from DB and also the local file
        }

        return null;
    }

}
