package sandjentrance.com.sj.actions;

import android.content.Context;

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

import sandjentrance.com.sj.actions.ClaimProjAction_.PsClaimProjAction;
import sandjentrance.com.sj.actions.DeleteFileAction_.PsDeleteFileAction;
import sandjentrance.com.sj.actions.events.ClaimProjActionFailure;
import sandjentrance.com.sj.actions.events.ClaimProjActionSuccess;
import sandjentrance.com.sj.actions.events.DeleteFileActionFailure;
import sandjentrance.com.sj.actions.events.DeleteFileActionSuccess;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper(args = {
        @Field(name = "fileId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @Event(postFix = "Success"),
        @Event(postFix = "Failure")
})

public class DeleteFileAction extends BaseAction {

    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        DeleteFileActionHelper helper = PsDeleteFileAction.helper(request.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new ClaimProjActionFailure();
        }

        if (deleteFile(helper.fileId())) {
            return new DeleteFileActionSuccess();
        } else {
            return new DeleteFileActionFailure();
        }
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        return new DeleteFileActionFailure();
    }
}
