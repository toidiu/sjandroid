package sandjentrance.com.sj.actions;

import android.graphics.Bitmap;
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
import com.google.api.services.drive.Drive;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.GetUserImgAction_.PsGetUserImgAction;
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
                @ParcelableClassField(name = "userFilePath", kind = @Kind(clazz = String.class))
        }),
        @EventClass(classPostFix = "Failure")
})

public class GetUserImgAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        GetUserImgActionHelper helper = PsGetUserImgAction.helper(actionRequest.getArguments(getClass().getClassLoader()));


        java.io.File avatarFile = ImageUtil.getAvatarFile(context, helper.userName());

        if (avatarFile.exists()) {
            return new GetUserImgActionEventSuccess(avatarFile.getAbsolutePath());
        } else {
            Bitmap bitmapFromDrawable = ImageUtil.getBitmapFromDrawable(context, R.drawable.profile_image);
            Bitmap resizedBitmap = ImageUtil.getResizedBitmap(bitmapFromDrawable, ImageUtil.IMAGE_RESOLUTION, ImageUtil.IMAGE_RESOLUTION);
        }

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        //// FIXME: 4/13/16 get working
//        File fileMetadata = new File();
//        Map<String, String> prop = new HashMap<>();
//        prop.put(CLAIM_PROPERTY, credential.getSelectedAccountName());
//        fileMetadata.setProperties(prop);
//
//        List<FileObj> photoFile = getFoldersByName(driveService, helper.userName(), prefs.getPhotosFolderId());
//        if (photoFile != null && photoFile.size() > 0) {
//
//        } else {
//        }
//
//        try {
//            FileObj fileObj = new FileObj(file);
//            databaseHelper.getFileObjDao().createOrUpdate(fileObj);
//
//            return new ClaimProjActionEventSuccess(credential.getSelectedAccountName());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ClaimProjActionEventFailure();
//        }

        return null;
    }

}
