package sandjentrance.com.sj.actions;

import android.os.Bundle;

import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.utils.UtilNetwork;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper()
@EventProducer(generated = {
        @EventClass(classPostFix = "Success"),
        @EventClass(classPostFix = "Failure")
})

public class UploadFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
//        UploadFileActionHelper helper = PsUploadFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
//        FileUploadObj fileUploadObj = helper.fileUploadObj();

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
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


}
