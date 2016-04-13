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
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.ArrayList;
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


        //check if the file already exists
        List<File> fildByName = getFildByName(fileUploadObj.fileName, prefs.getPhotosFolderId());
        File imgFile = null;
        if (fildByName != null && fildByName.size() > 0) {
            imgFile = fildByName.get(0);
        }

        //Local file
        FileContent mediaContent = new FileContent(fileUploadObj.mime, new java.io.File(fileUploadObj.filePath));

        //Drive file
        List<String> parents = new ArrayList<>();
        parents.add(fileUploadObj.parentId);
        File fileMetadata = new File();
        fileMetadata.setName(fileUploadObj.fileName);
        fileMetadata.setMimeType(fileUploadObj.mime);
        fileMetadata.setParents(parents);

        try {
            driveService.files().create(fileMetadata, mediaContent)
                    .setFields(QUERY_FIELDS)
                    .execute();

            if (imgFile != null) {
                //// markme: 4/13/16 update is failing.. but that should be the prefered way
//                driveService.files().update(imgFile.getId(), imgFile, mediaContent).execute();
                driveService.files().delete(imgFile.getId()).execute();
            }
            return new UploadFileActionEventSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            return new UploadFileActionEventFailure();
        }
    }

}
