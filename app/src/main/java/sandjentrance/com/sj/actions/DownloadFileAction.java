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

import java.io.File;

import sandjentrance.com.sj.actions.AddUploadFileAction_.PsAddUploadFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction_.PsDownloadFileAction;
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.models.LocalFileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "fileDl", kind = @Kind(clazz = FileDownloadObj.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "localFileObj", kind = @Kind(clazz = LocalFileObj.class)),
        }),
        @EventClass(classPostFix = "Failure")
})

public class DownloadFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        DownloadFileActionHelper helper = PsDownloadFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
        FileDownloadObj fileDlObj = helper.fileDl();

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        File localFile = downloadFile(fileDlObj);

        if (localFile != null) {
            LocalFileObj localFileObj = new LocalFileObj(localFile.getName(), fileDlObj.mime, localFile.getAbsolutePath());

            PennStation.requestAction(PsAddUploadFileAction.helper(new FileUploadObj(fileDlObj.parentId, fileDlObj.fileId, fileDlObj.fileName, localFile.getAbsolutePath(), fileDlObj.mime)));
            return new DownloadFileActionEventSuccess(localFileObj);
        } else {
            return new DownloadFileActionEventFailure();
        }
    }

}
