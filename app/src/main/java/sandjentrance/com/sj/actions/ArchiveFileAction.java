package sandjentrance.com.sj.actions;

import android.content.Context;

import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.annotations.Event;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Field;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.RequestEnv;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;

import sandjentrance.com.sj.actions.ArchiveFileAction_.PsArchiveFileAction;
import sandjentrance.com.sj.actions.events.ArchiveFileActionFailure;
import sandjentrance.com.sj.actions.events.ArchiveFileActionSuccess;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@ActionHelper(args = {
        @Field(name = "fileId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @Event(postFix = "Success"),
        @Event(postFix = "Failure")
})
@Action
public class ArchiveFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        ArchiveFileActionHelper helper = PsArchiveFileAction.helper(request.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
//            return new SetupDriveFailure();
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
            return new ArchiveFileActionSuccess();
        } else {
            return new ArchiveFileActionFailure();
        }

    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        return null;
    }
}
