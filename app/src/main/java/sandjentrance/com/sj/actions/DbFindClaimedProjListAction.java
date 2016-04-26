package sandjentrance.com.sj.actions;

import android.content.Context;

import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.annotations.Event;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableField;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.RequestEnv;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import sandjentrance.com.sj.actions.events.DbFindClaimedProjListActionFailure;
import sandjentrance.com.sj.actions.events.DbFindClaimedProjListActionSuccess;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper()
@EventProducer(generated = {
        @Event(postFix = "Success", fields = {
                @ParcelableField(name = "results", kind = @Kind(clazz = FileObj[].class))
        }),
        @Event(postFix = "Failure")
})
public class DbFindClaimedProjListAction extends BaseAction {

    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        try {
            List<FileObj> fileObjList = databaseHelper.getClaimProjDao().queryForAll();
            FileObj[] array = fileObjList.toArray(new FileObj[fileObjList.size()]);
            Arrays.sort(array, FileObj.getComparator());
            return new DbFindClaimedProjListActionSuccess(array);
        } catch (SQLException e) {
            e.printStackTrace();
            return new DbFindClaimedProjListActionFailure();
        }
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        return null;
    }
}
