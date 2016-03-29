package sandjentrance.com.sj;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.edisonwang.ps.annotations.ClassField;
import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableClassField;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.Action;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;

/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@EventProducer(generated = {
        @EventClass(classPostFix = "Success")
})
@RequestActionHelper()
//variables = {
//        @ClassField(name = "tag", kind = @Kind(clazz = String.class), required = true)
//})
//@EventProducer(generated = {
//        @EventClass(classPostFix = "Success", fields = {
//                @ParcelableClassField(name = "results", kind = @Kind(clazz = Tumblr.Post[].class))
//        }),
//        @EventClass(classPostFix = "Failure", fields = {
//                @ParcelableClassField(name = "message", kind = @Kind(clazz = String.class))
//        })
//})
public class TestAction implements Action {
    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        Log.d("adf", "asdf");
        return new TestActionEventSuccess();
    }

}
