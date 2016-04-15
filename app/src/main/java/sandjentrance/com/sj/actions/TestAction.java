package sandjentrance.com.sj.actions;

import android.os.Bundle;
import android.os.Environment;

import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;

import java.io.File;

import sandjentrance.com.sj.utils.ZamzarUtil;


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
//            ZamzarUtil.info();

            File download = new File(Environment.getExternalStorageDirectory(), "Download");
            File sourceFile = new File(download, "arch2.dwg");
            ZamzarUtil.convert(sourceFile);

//            ZamzarUtil.askStatus(287328);

            File sj = new File(Environment.getExternalStorageDirectory(), "SJ");
            File localFile = new File(sj, "test.pdf");
            ZamzarUtil.download(13344184, localFile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
