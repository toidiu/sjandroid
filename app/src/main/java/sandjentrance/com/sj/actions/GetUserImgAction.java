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
import com.google.api.services.drive.model.File;

import org.joda.time.DateTime;

import java.util.List;

import sandjentrance.com.sj.actions.GetUserImgAction_.PsGetUserImgAction;
import sandjentrance.com.sj.utils.DateUtil;
import sandjentrance.com.sj.utils.ImageUtil;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "userName", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "userName", kind = @Kind(clazz = String.class))
        }),
        @EventClass(classPostFix = "Failure"),
        @EventClass(classPostFix = "NoFile")
})

public class GetUserImgAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        GetUserImgActionHelper helper = PsGetUserImgAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
        java.io.File avatarFile = ImageUtil.getAvatarFile(context, helper.userName());
        String fileName = avatarFile.getName();


        //see if file exists on server
        List<File> fileByName = getFileByName(fileName, prefs.getPhotosFolderId());
        // yes
        if (fileByName != null && fileByName.size() > 0) {
            //see how old the local avatar file is
            DateTime dateTime = new DateTime(avatarFile.lastModified());
            if (DateUtil.isDayOld(dateTime)) {
                //download it
                java.io.File file = downloadUserImg(avatarFile, fileByName.get(0).getId());
                if (file == null || !file.exists()) {
                    return new GetUserImgActionEventFailure();
                }
                return new GetUserImgActionEventSuccess(helper.userName());
            } else {
                //no
                return new GetUserImgActionEventFailure();
            }
        } else {
            return new GetUserImgActionEventNoFile();
        }

    }
}
