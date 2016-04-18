package sandjentrance.com.sj.actions;

import android.os.Bundle;
import android.util.Log;

import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.google.api.services.drive.model.File;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.utils.FileUtils;


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

        List<FileUploadObj> fileUploadObjs = new ArrayList<>();
        Dao<FileUploadObj, Integer> fileUploadDao = null;
        try {
            fileUploadDao = databaseHelper.getFileUploadDao();
            fileUploadObjs = fileUploadDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (FileUploadObj obj : fileUploadObjs) {
            uploadFile(fileUploadDao, obj);
        }


        return null;
    }

    private void uploadFile(Dao<FileUploadObj, Integer> dao, FileUploadObj fileUploadObj) {
        //id fileId null
        File createdFile = null;
        if (fileUploadObj.fileId == null) {
            //yes
            Log.d("d----------", "uploadFile: 3");
            createdFile = createFile(fileUploadObj);
        } else {
            //no
            //check if file exists on drive
            FileObj fileById = getFileById(fileUploadObj.fileId);
            if (fileById != null) {
                //yes
                Log.d("d----------", "uploadFile: 2");
                createdFile = replaceFile(fileUploadObj);
//                createdFile = createFile(fileUploadObj);
//                deleteSatisfied = deleteFile(fileById.id);
            } else {
                //no
                createdFile = createFile(fileUploadObj);
                Log.d("d----------", "uploadFile: 1");
            }
        }

        if (createdFile != null) {
            FileUtils.deleteLocalFile(new java.io.File(fileUploadObj.localFilePath));
            try {
                dao.deleteById(fileUploadObj.dbId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
//        if (createdFile != null && deleteSatisfied) {
//            //fixme delete from DB and also the local file
//            try {
//                FileUtils.deleteLocalFile(new java.io.File(fileUploadObj.localFilePath));
//                dao.deleteById(fileUploadObj.dbId);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }

}
