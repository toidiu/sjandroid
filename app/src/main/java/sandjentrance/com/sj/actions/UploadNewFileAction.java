package sandjentrance.com.sj.actions;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.annotations.Event;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.RequestEnv;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.utils.UtilNetwork;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper()
@EventProducer(generated = {
        @Event(postFix = "Success"),
        @Event(postFix = "Failure")
})

public class UploadNewFileAction extends BaseAction {


    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        Dao<NewFileObj, Integer> newFileObjDao = null;
        List<NewFileObj> newFileObjs = new ArrayList<>();
        try {
            newFileObjDao = databaseHelper.getNewFileObjDao();
            newFileObjs = newFileObjDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!UtilNetwork.isDeviceOnline(context)) {
            return null;
        }
        for (NewFileObj obj : newFileObjs) {
            //locate proper folder else put in base of project folder
            String parentId;
            List<FileObj> foldersByNameFuzzy = getFoldersByNameFuzzy(obj.parentName, obj.projId);
            if (foldersByNameFuzzy != null && foldersByNameFuzzy.size() > 0) {
                parentId = foldersByNameFuzzy.get(0).id;
            } else {
                parentId = obj.projId;
            }

            //upload new file and then delete from db
            FileUploadObj fileUploadObj = new FileUploadObj(parentId, null, obj.title, obj.localFilePath, obj.mime);
            if (!new File(fileUploadObj.localFilePath).exists()) {
                //Markme the file no longer exists so delete it
                //// FIXME: 4/27/16 log crash
                Crashlytics.getInstance().core.logException(new Exception("A file to be uploaded was deleted."));
                newFileObjDao.deleteById(obj.dbId);
            } else {
                boolean uploaded = uploadFile(null, fileUploadObj);
                if (uploaded) {
                    try {
                        newFileObjDao.deleteById(obj.dbId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        return null;
    }
}
