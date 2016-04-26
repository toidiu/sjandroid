package sandjentrance.com.sj.actions;

import android.content.Context;
import android.os.Bundle;

import com.edisonwang.ps.annotations.Action;
import com.edisonwang.ps.annotations.ActionHelper;

import com.edisonwang.ps.annotations.Event;

import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Field;
import com.edisonwang.ps.annotations.Kind;



import com.edisonwang.ps.annotations.ParcelableField;

import com.edisonwang.ps.annotations.ActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.edisonwang.ps.lib.RequestEnv;
import com.google.api.services.drive.model.File;

import org.joda.time.DateTime;

import java.util.List;

import sandjentrance.com.sj.actions.GetUserImgAction_.PsGetUserImgAction;
import sandjentrance.com.sj.actions.events.GetUserImgActionFailure;
import sandjentrance.com.sj.actions.events.GetUserImgActionNoFile;
import sandjentrance.com.sj.actions.events.GetUserImgActionSuccess;
import sandjentrance.com.sj.utils.UtilsDate;
import sandjentrance.com.sj.utils.UtilImage;


/**
 * Created by toidiu on 3/28/16.
 */
@Action
@ActionHelper(args = {
        @Field(name = "userName", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @Event(postFix = "Success", fields = {
                @ParcelableField(name = "userName", kind = @Kind(clazz = String.class))
        }),
        @Event(postFix = "Failure"),
        @Event(postFix = "NoFile")
})

public class GetUserImgAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    @Override
    protected ActionResult process(Context context, ActionRequest request, RequestEnv env) throws Throwable {
        GetUserImgActionHelper helper = PsGetUserImgAction.helper(request.getArguments(getClass().getClassLoader()));
        java.io.File avatarFile = UtilImage.getAvatarFile(context, helper.userName());
        String fileName = avatarFile.getName();


        //see if file exists on server
        List<File> fileByName = getFileByName(fileName, prefs.getPhotosFolderId());
        // yes
        if (fileByName != null && fileByName.size() > 0) {
            //see how old the local avatar file is
            DateTime dateTime = new DateTime(avatarFile.lastModified());
            if (UtilsDate.isDayOld(dateTime)) {
                //download it
                java.io.File file = downloadUserImg(avatarFile, fileByName.get(0).getId());
                if (file == null || !file.exists()) {
                    return new GetUserImgActionFailure();
                }
                return new GetUserImgActionSuccess(helper.userName());
            } else {
                //no
                return new GetUserImgActionFailure();
            }
        } else {
            return new GetUserImgActionNoFile();
        }
    }

    @Override
    protected ActionResult onError(Context context, ActionRequest request, RequestEnv env, Throwable e) {
        return null;
    }
}
