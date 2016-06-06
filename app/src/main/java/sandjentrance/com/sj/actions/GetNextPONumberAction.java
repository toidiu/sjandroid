package sandjentrance.com.sj.actions;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.annotations.Event;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Field;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableField;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.RequestEnv;

import java.io.File;
import java.util.List;

import sandjentrance.com.sj.actions.GetNextPONumberAction_.PsGetNextPONumberAction;
import sandjentrance.com.sj.actions.events.GetNextPONumberActionFailure;
import sandjentrance.com.sj.actions.events.GetNextPONumberActionSuccess;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.utils.UtilFile;
import sandjentrance.com.sj.utils.UtilNetwork;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper(args = {
        @Field(name = "newFileObj", kind = @Kind(clazz = NewFileObj.class), required = true),
})
@EventProducer(generated = {
        @Event(postFix = "Success", fields = {
                @ParcelableField(name = "fileObj", kind = @Kind(clazz = FileObj.class)),
                @ParcelableField(name = "nextNumber", kind = @Kind(clazz = Integer.class))
        }),
        @Event(postFix = "Failure")
})

public class GetNextPONumberAction extends BaseAction {


    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        if (!UtilNetwork.isDeviceOnline(context)) {
            return new GetNextPONumberActionFailure();
        }

        GetNextPONumberActionHelper helper = PsGetNextPONumberAction.helper(request.getArguments(getClass().getClassLoader()));
        NewFileObj newFileObj = helper.newFileObj();


        //find proper folder else put in base of project folder
        String parentId = null;
        List<FileObj> foldersByNameFuzzy = getFoldersByNameFuzzy(newFileObj.parentName, newFileObj.projId);
        if (foldersByNameFuzzy != null && foldersByNameFuzzy.size() > 0) {
            parentId = foldersByNameFuzzy.get(0).id;
        }

        if (parentId == null) return new GetNextPONumberActionFailure();

        //get name of latest purchase order pdf
        FileObj projFolder = getFileById(newFileObj.projId);
        newFileObj.projTitle = projFolder.title;

        int nextNumber = getNextPurchaseOrderNumber(parentId);
        newFileObj.title = getNextPurchaseOrderName(newFileObj, nextNumber);


        //create new local file
        String localFileName = newFileObj.parentName + System.currentTimeMillis();
        File localFile = UtilFile.getLocalFileWithExtension(localFileName, newFileObj.mime);

        localFile = UtilFile.copyAssetsFile(context.getAssets(), newFileObj.assetFileName, localFile);
        newFileObj.localFilePath = localFile.getAbsolutePath();

        //fixme add NEW file to db to be synced later
//        databaseHelper.getNewFileObjDao().create(newFileObj);

        //upload new file
        FileUploadObj fileUploadObj = new FileUploadObj(parentId, null, newFileObj.title, newFileObj.localFilePath, newFileObj.mime);
        com.google.api.services.drive.model.File driveFile = uploadAndReturnFile(null, fileUploadObj);


        if (driveFile != null) {
        FileObj fileObj = new FileObj(driveFile);
//            LocalFileObj localFileObj = new LocalFileObj(newFileObj.title, newFileObj.mime, newFileObj.localFilePath);
            return new GetNextPONumberActionSuccess(fileObj, nextNumber);
        } else {
            return new GetNextPONumberActionFailure();
        }
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        Crashlytics.getInstance().core.logException(e);
        return new GetNextPONumberActionFailure();
    }
}
