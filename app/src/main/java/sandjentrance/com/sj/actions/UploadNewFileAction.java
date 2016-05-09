package sandjentrance.com.sj.actions;

import android.content.Context;
import android.util.Log;

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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sandjentrance.com.sj.actions.events.UploadNewFileActionFailure;
import sandjentrance.com.sj.actions.events.UploadNewFileActionSuccess;
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
        if (!UtilNetwork.isDeviceOnline(context)) {
            return null;
        }


        //get items from DB
        Dao<NewFileObj, Integer> newFileObjDao = null;
        List<NewFileObj> newFileObjs = new ArrayList<>();
        try {
            newFileObjDao = databaseHelper.getNewFileObjDao();
            newFileObjs = newFileObjDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //find parent folder, upload file, delete from db
        for (NewFileObj obj : newFileObjs) {
            //find proper folder else put in base of project folder
            boolean foundBaseFolder = false;
            String parentId;
            List<FileObj> foldersByNameFuzzy = getFoldersByNameFuzzy(obj.parentName, obj.projId);
            if (foldersByNameFuzzy != null && foldersByNameFuzzy.size() > 0) {
                foundBaseFolder = true;
                parentId = foldersByNameFuzzy.get(0).id;
            } else {
                parentId = obj.projId;
            }

            if (obj.parentName.equals(BaseAction.PURCHASE_FOLDER_NAME)) {
                //get name of obj.projId folder to prepend to new file

                //get name of latest purchase order pdf
                if (foundBaseFolder) {
                    obj.title = getNextPurchaseOrderName(obj, parentId);
                }
            }


            //upload new file and then delete from db
            FileUploadObj fileUploadObj = new FileUploadObj(parentId, null, obj.title, obj.localFilePath, obj.mime);
            if (!new File(fileUploadObj.localFilePath).exists()) {
                //Markme the file no longer exists so delete it
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

        return new UploadNewFileActionSuccess();
    }

    private String getNextPurchaseOrderName(NewFileObj newFileObj, String parentId) throws IOException {
        String search =
                "'" + parentId + "'" + " in parents"
                        + " and " + "title != '.DS_Store'"
                        + " and " + "mimeType != '" + FOLDER_MIME + "'";

        List<FileObj> dataFromApi = toFileObjs(executeQueryList(search));
        int biggest = 0;
        for (FileObj f : dataFromApi) {
            if (f.title.indexOf("-") < 0) continue;

            String substring = f.title.substring(f.title.indexOf("-") + 1);

            if (substring.indexOf("-") < 0) continue;
            String substring1 = substring.substring(0, substring.indexOf("-"));
            try {
                Integer integer = Integer.valueOf(substring1);
                if (biggest < integer) {
                    biggest = integer;
                }
            } catch (RuntimeException e) {
                Log.d("---exp", e.toString());
            }
        }
        biggest++;

        assert newFileObj.projTitle != null;
        String projNum = newFileObj.projTitle.substring(1, newFileObj.projTitle.indexOf(" -"));

        return projNum + "-" + biggest + "-" + newFileObj.title;
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        return new UploadNewFileActionFailure();
    }
}
