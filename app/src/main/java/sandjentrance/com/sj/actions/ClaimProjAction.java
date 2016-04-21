package sandjentrance.com.sj.actions;

import android.os.Bundle;

import com.edisonwang.ps.annotations.ClassField;
import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableClassField;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;

import sandjentrance.com.sj.actions.ClaimProjAction_.PsClaimProjAction;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "fileId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "claimUser", kind = @Kind(clazz = String.class))
        }),
        @EventClass(classPostFix = "Failure")
})

public class ClaimProjAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        ClaimProjActionHelper helper = PsClaimProjAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        if (claimProj(helper.fileId())) {
            return new ClaimProjActionEventSuccess(credential.getSelectedAccountName());
        } else {
            return new ClaimProjActionEventFailure();
        }

    }

}
