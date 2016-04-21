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
import com.edisonwang.ps.lib.PennStation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import sandjentrance.com.sj.actions.DbAddUploadFileAction_.PsDbAddUploadFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction_.PsDownloadFileAction;
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.utils.UtilFile;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "fileDl", kind = @Kind(clazz = FileDownloadObj.class), required = true),
        @ClassField(name = "ActionEnum", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "localFileObj", kind = @Kind(clazz = LocalFileObj.class)),
                @ParcelableClassField(name = "ActionEnum", kind = @Kind(clazz = String.class)),
        }),
        @EventClass(classPostFix = "Failure")
})

public class DownloadFileAction extends BaseAction {

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        DownloadFileActionHelper helper = PsDownloadFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
        FileDownloadObj fileDlObj = helper.fileDl();
        String actionEnum = helper.ActionEnum();

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        File localFile = null;
        if (actionEnum.equals(ActionEnum.EDIT.name())) {
            File fileLocation = UtilFile.getLocalFile(fileDlObj.fileId, fileDlObj.mime);
            localFile = downloadFile(fileDlObj, fileLocation);
        } else {
            //see if local file exists and copy it over to cache
            File tempFile = UtilFile.getLocalFile(fileDlObj.fileId, fileDlObj.mime);
            File fileLocation = UtilFile.getCachedFile(fileDlObj.fileName, fileDlObj.mime);

            if (tempFile != null || tempFile.exists()) {
                try {
                    FileUtils.copyFile(tempFile, fileLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            localFile = downloadFile(fileDlObj, fileLocation);
        }

        if (localFile != null && localFile.exists()) {
            LocalFileObj localFileObj = new LocalFileObj(localFile.getName(), fileDlObj.mime, localFile.getAbsolutePath());

            if (actionEnum.equals(ActionEnum.EDIT.name())) {
                PennStation.requestAction(PsDbAddUploadFileAction.helper(new FileUploadObj(fileDlObj.parentId, fileDlObj.fileId, fileDlObj.fileName, localFile.getAbsolutePath(), fileDlObj.mime)));
            }
            return new DownloadFileActionEventSuccess(localFileObj, actionEnum);
        } else {
            return new DownloadFileActionEventFailure();
        }
    }

    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    public enum ActionEnum {
        SHARE,
        PRINT,
        EDIT
    }


}
