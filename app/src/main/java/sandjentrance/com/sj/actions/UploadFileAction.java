package sandjentrance.com.sj.actions;

import android.content.Context;
import android.os.Bundle;

import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.annotations.Event;

import com.edisonwang.ps.annotations.EventProducer;

import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.edisonwang.ps.lib.RequestEnv;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        //        UploadFileActionHelper helper = PsUploadFileAction.helper(request.getArguments(getClass().getClassLoader()));
//        FileUploadObj fileUploadObj = helper.fileUploadObj();

        if (credential.getSelectedAccountName() == null) {
            //// FIXME: 4/25/16
//            return new SetupDriveFailure();
        }
        if (!UtilNetwork.isDeviceOnline(context)) {
            return null;
        }

        List<FileUploadObj> fileUploadObjs = new ArrayList<>();
        Dao<FileUploadObj, Integer> fileUploadDao = null;
        try {
            fileUploadDao = databaseHelper.getFileUploadDao();
            fileUploadObjs = fileUploadDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (FileUploadObj obj : fileUploadObjs) {
            if (uploadFile(fileUploadDao, obj)){
                try {
                    fileUploadDao.deleteById(obj.dbId);
                } catch (SQLException e) {
                    e.printStackTrace();
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
