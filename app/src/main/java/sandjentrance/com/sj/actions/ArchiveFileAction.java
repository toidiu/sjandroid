package sandjentrance.com.sj.actions;

import android.os.Bundle;

import com.edisonwang.ps.annotations.ClassField;
import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;

import sandjentrance.com.sj.actions.ArchiveFileAction_.PsArchiveFileAction;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "fileId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success"),
        @EventClass(classPostFix = "Failure")
})

public class ArchiveFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        ArchiveFileActionHelper helper = PsArchiveFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        String fileId = helper.fileId();
        if (fileMoved(fileId, prefs.getArchiveFolderId())) {
            try {
                Dao<FileObj, Integer> dao = databaseHelper.getClaimProjDao();
                DeleteBuilder<FileObj, Integer> builder = dao.deleteBuilder();
                builder.where().eq("id", fileId);
                builder.delete();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ArchiveFileActionEventSuccess();
        } else {
            return new ArchiveFileActionEventFailure();
        }

    }

}
