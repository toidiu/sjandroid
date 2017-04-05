package sandjentrance.com.sj.actions.fix;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.annotations.Event;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableField;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.RequestEnv;

import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.actions.fix.events.FIXPONumberActionFailure;
import sandjentrance.com.sj.actions.fix.events.FIXPONumberActionSuccess;
import sandjentrance.com.sj.utils.UtilNetwork;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper()
@EventProducer(generated = {
        @Event(postFix = "Success", fields = {
                @ParcelableField(name = "num", kind = @Kind(clazz = String.class))
        }),
        @Event(postFix = "Failure")
})
public class FIXPONumberAction extends BaseAction {


    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        if (!UtilNetwork.isDeviceOnline(context)) {
            return new FIXPONumberActionFailure();
        }


        String n = DEBUGGetPoNumber();
//        String n = DEBUGSetPoNumber(200000);
        assert (n != null);


        return new FIXPONumberActionSuccess(n);
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        Crashlytics.getInstance().core.logException(e);
        return new FIXPONumberActionFailure();
    }
}
