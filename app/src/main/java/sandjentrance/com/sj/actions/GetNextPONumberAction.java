package sandjentrance.com.sj.actions;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.annotations.Event;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Field;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableField;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.RequestEnv;

import java.util.List;

import sandjentrance.com.sj.actions.GetNextPONumberAction_.PsGetNextPONumberAction;
import sandjentrance.com.sj.actions.events.GetNextPONumberActionFailure;
import sandjentrance.com.sj.actions.events.GetNextPONumberActionSuccess;
import sandjentrance.com.sj.actions.events.UploadNewFileActionSuccess;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.utils.UtilNetwork;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper(args = {
        @Field(name = "newFileObj", kind = @Kind(clazz = NewFileObj.class), required = true),
})
@EventProducer(generated = {
        @Event(postFix = "Success", fields = {
                @ParcelableField(name = "newFileObj", kind = @Kind(clazz = NewFileObj.class))
        }),
        @Event(postFix = "Failure")
})

public class GetNextPONumberAction extends BaseAction {


    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        if (!UtilNetwork.isDeviceOnline(context)) {
            return new GetNextPONumberActionFailure();
        }

        GetNextPONumberActionHelper helper = PsGetNextPONumberAction.helper(request.getArguments(getClass().getClassLoader()));
        NewFileObj obj = helper.newFileObj();




        //find proper folder else put in base of project folder
        String parentId = null;
        List<FileObj> foldersByNameFuzzy = getFoldersByNameFuzzy(obj.parentName, obj.projId);
        if (foldersByNameFuzzy != null && foldersByNameFuzzy.size() > 0) {
            parentId = foldersByNameFuzzy.get(0).id;
        }

        if (parentId == null) return new GetNextPONumberActionFailure();

        if (obj.parentName.equals(BaseAction.PURCHASE_FOLDER_NAME)) {
            //get name of latest purchase order pdf
            obj.title = getNextPurchaseOrderName(obj, parentId);
        }

        return new GetNextPONumberActionSuccess(obj);
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        Crashlytics.getInstance().core.logException(e);
        return new GetNextPONumberActionFailure();
    }
}
