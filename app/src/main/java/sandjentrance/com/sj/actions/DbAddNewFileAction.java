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

import java.io.File;
import java.sql.SQLException;

import sandjentrance.com.sj.actions.DbAddNewFileAction_.PsDbAddNewFileAction;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.utils.UtilFile;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "newFileObj", kind = @Kind(clazz = NewFileObj.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "newFileObj", kind = @Kind(clazz = NewFileObj.class))
        }),
        @EventClass(classPostFix = "Failure")
})

public class DbAddNewFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        DbAddNewFileActionHelper helper = PsDbAddNewFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
        NewFileObj newFileObj = helper.newFileObj();

        if (newFileObj.localFilePath == null) {
            String localFileName = newFileObj.parentName + System.currentTimeMillis();

            if (newFileObj.parentName.equals(BaseAction.PHOTOS_FOLDER_NAME)) {
                //photo file should already have path
            } else {
                File localFile = UtilFile.copyAssetsFile(context.getAssets(), newFileObj.assetFileName, localFileName, newFileObj.mime);
                newFileObj.localFilePath = localFile.getAbsolutePath();
            }
        }


        try {
            databaseHelper.getNewFileObjDao().create(newFileObj);
            return new DbAddNewFileActionEventSuccess(newFileObj);
        } catch (SQLException e) {
            e.printStackTrace();
            return new DbAddNewFileActionEventFailure();
        }
    }

}
