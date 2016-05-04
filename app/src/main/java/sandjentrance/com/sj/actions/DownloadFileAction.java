package sandjentrance.com.sj.actions;

import android.content.Context;

import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.annotations.Event;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Field;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableField;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.PennStation;
import com.edisonwang.ps.lib.RequestEnv;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import sandjentrance.com.sj.actions.DbAddUploadFileAction_.PsDbAddUploadFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction_.PsDownloadFileAction;
import sandjentrance.com.sj.actions.DwgConversionAction_.PsDwgConversionAction;
import sandjentrance.com.sj.actions.events.DownloadFileActionDwgConversion;
import sandjentrance.com.sj.actions.events.DownloadFileActionFailure;
import sandjentrance.com.sj.actions.events.DownloadFileActionSuccess;
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.utils.UtilFile;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper(args = {
        @Field(name = "fileDl", kind = @Kind(clazz = FileDownloadObj.class), required = true),
        @Field(name = "ActionEnum", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @Event(postFix = "Success", fields = {
                @ParcelableField(name = "localFileObj", kind = @Kind(clazz = LocalFileObj.class)),
                @ParcelableField(name = "ActionEnum", kind = @Kind(clazz = String.class)),
        }),
        @Event(postFix = "Failure"),
        @Event(postFix = "DwgConversion")
})

public class DownloadFileAction extends BaseAction {

    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        DownloadFileActionHelper helper = PsDownloadFileAction.helper(request.getArguments(getClass().getClassLoader()));
        FileDownloadObj fileDlObj = helper.fileDl();
        String actionEnum = helper.ActionEnum();

        if (credential.getSelectedAccountName() == null) {
            //// FIXME: 4/25/16
//            return new SetupDriveActionFailure();
        }

        File localFile = null;
        if (actionEnum.equals(ActionEnum.EDIT.name())) {
            File fileLocation = UtilFile.getLocalFileWithExtension(fileDlObj.fileId, fileDlObj.mime);
            localFile = downloadFile(fileDlObj, fileLocation);
        } else {
            //if we print or share.. see if local file exists and copy it over to cache
            File tempFile = UtilFile.getLocalFileWithExtension(fileDlObj.fileId, fileDlObj.mime);
            //make a file in the cache folder
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

            if (fileDlObj.mime.equals(BaseAction.MIME_DWG1) && actionEnum.equals(ActionEnum.EDIT.name())) {
                //do the zamzar conversion and dont upload because we dont need to reupload the DWG file
                PennStation.requestAction(PsDwgConversionAction.helper(fileDlObj, localFileObj));
                return new DownloadFileActionDwgConversion();
            } else if (actionEnum.equals(ActionEnum.EDIT.name())) {
                PennStation.requestAction(PsDbAddUploadFileAction.helper(new FileUploadObj(fileDlObj.parentId, fileDlObj.fileId, fileDlObj.fileName, localFile.getAbsolutePath(), fileDlObj.mime)));
            }
            return new DownloadFileActionSuccess(localFileObj, actionEnum);
        } else {
            return new DownloadFileActionFailure();
        }
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        return null;
    }

    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    public enum ActionEnum {
        SHARE,
        PRINT,
        EDIT
    }


}
