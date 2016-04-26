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

import sandjentrance.com.sj.actions.MoveFileAction_.PsMoveFileAction;
import sandjentrance.com.sj.actions.events.MoveFileActionFailure;
import sandjentrance.com.sj.actions.events.MoveFileActionSuccess;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper(args = {
        @Field(name = "newParentId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @Event(postFix = "Prime"),
        @Event(postFix = "Success"),
        @Event(postFix = "Failure")
})

public class MoveFileAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        MoveFileActionHelper helper = PsMoveFileAction.helper(request.getArguments(getClass().getClassLoader()));

        if (fileMoved(moveFolderHelper.fileId, helper.newParentId())) {
            moveFolderHelper.moveDone();
            return new MoveFileActionSuccess();
        } else {
            return new MoveFileActionFailure();
        }
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        return new MoveFileActionFailure();
    }
}
