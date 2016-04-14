package sandjentrance.com.sj.actions;

import android.os.Bundle;

import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;

import sandjentrance.com.sj.utils.NetworkUtils;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper()
@EventProducer()

public class TestAction extends BaseAction {


    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
//        super.processRequest(service, actionRequest, bundle);
//        ClaimProjActionHelper helper = PsClaimProjAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        try {
//            ZamzarUtil.doo();
//            ZamzarUtil.prep();
//            ZamzarUtil.dl();

//            NetworkUtils.prep();
            NetworkUtils.doit();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
