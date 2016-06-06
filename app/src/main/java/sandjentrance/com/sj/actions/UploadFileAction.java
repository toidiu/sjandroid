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
import java.util.List;

import sandjentrance.com.sj.actions.events.UploadFileActionFailure;
import sandjentrance.com.sj.actions.events.UploadFileActionSuccess;
import sandjentrance.com.sj.database.TransactionRunnable;
import sandjentrance.com.sj.models.FileUploadObj;
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

public class UploadFileAction extends BaseAction {

    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {

        if (!UtilNetwork.isDeviceOnline(context)) {
            return null;
        }

        databaseHelper.runInTransaction(new TransactionRunnable() {
            @Override
            public void run() throws Exception {


                Dao<FileUploadObj, Integer> fileUploadDao = null;
                fileUploadDao = databaseHelper.getFileUploadDao();
                List<FileUploadObj> fileUploadObjs = fileUploadDao.queryForAll();

                for (FileUploadObj obj : fileUploadObjs) {
                    if (!new File(obj.localFilePath).exists()) {
                        //Markme the file no longer exists so delete it
                        //// FIXME: 4/27/16 log crash
                        Crashlytics.getInstance().core.logException(new Exception("A file to be uploaded was deleted."));
                        fileUploadDao.deleteById(obj.dbId);
                    } else {
                        boolean uploaded = uploadFile(fileUploadDao, obj);
                        if (uploaded) {
                            try {
                                fileUploadDao.deleteById(obj.dbId);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


            }
        });

        return new UploadFileActionSuccess();
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        Crashlytics.getInstance().core.logException(e);
        return new UploadFileActionFailure();
    }
}
