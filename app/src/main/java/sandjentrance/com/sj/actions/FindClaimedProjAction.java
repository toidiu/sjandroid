package sandjentrance.com.sj.actions;

import android.os.Bundle;

import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableClassField;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper()
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "results", kind = @Kind(clazz = FileObj[].class))
        }),
        @EventClass(classPostFix = "Failure")
})
public class FindClaimedProjAction extends BaseAction {


    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);

        if (credential.getSelectedAccountName() == null) {
            return new FindFolderChildrenActionEventFailure();
        }

        String search = "properties has { key='" + CLAIM_PROPERTY + "' and value='" + credential.getSelectedAccountName() + "' }"
                + " and " + "name != '.DS_Store'"
                + " and " + "'" + prefs.getBaseFolderId() + "'" + " in parents"
                + " and " + "mimeType = '" + FileObj.FOLDER_MIME + "'";

        try {
            List<FileObj> dataFromApi = toFileObjs(executeQueryList(search));
            final FileObj[] array = dataFromApi.toArray(new FileObj[dataFromApi.size()]);
            Arrays.sort(array, FileObj.FileObjComparator);

            databaseHelper.runInTransaction(new Runnable() {
                @Override
                public void run() {
                    try {
                        Dao<FileObj, String> fileObjDao = databaseHelper.getClaimProjDao();
                        DeleteBuilder<FileObj, String> builder = fileObjDao.deleteBuilder();
                        builder.where().isNotNull("dbId");
                        builder.delete();

                        for (FileObj file : array) {
                            fileObjDao.createOrUpdate(file);
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });


            return new DbFindClaimedProjListActionEventSuccess(array);
        } catch (Exception e) {
            e.printStackTrace();
            return new DbFindClaimedProjListActionEventFailure();
        }
    }

}
