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

import java.util.List;

import sandjentrance.com.sj.actions.UploadUserImageAction_.PsUploadUserImageAction;
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

public class UploadUserImageAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        UploadUserImageActionHelper helper = PsUploadUserImageAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
        FileUploadObj fileUploadObj = helper.fileUploadObj();

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }


        File createdFile;
        //check if the file already exists
        List<File> fileByName = getFileByName(fileUploadObj.fileName, prefs.getPhotosFolderId());
        if (fileByName != null && fileByName.size() > 0) {
            //yes
            createdFile = replaceFile(fileUploadObj);
        } else {
            //no
            createdFile = createFile(helper.fileUploadObj());
        }


        if (createdFile != null) {
            return new UploadFileActionEventSuccess();
        } else {
            return new UploadFileActionEventFailure();
        }
    }

}
