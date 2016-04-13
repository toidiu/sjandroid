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
import com.google.api.services.drive.Drive;

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
public class DbFindClaimedProjListAction extends BaseAction {

    private Drive driveService;


    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);

        try {
            List<FileObj> fileObjList = databaseHelper.getClaimProjDao().queryForAll();
            FileObj[] array = fileObjList.toArray(new FileObj[fileObjList.size()]);
            Arrays.sort(array, FileObj.FileObjComparator);
            return new DbFindClaimedProjListActionEventSuccess(array);
        } catch (SQLException e) {
            e.printStackTrace();
            return new DbFindClaimedProjListActionEventFailure();
        }
    }

}
