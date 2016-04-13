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

import java.util.List;

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
                @ParcelableClassField(name = "userName", kind = @Kind(clazz = String.class))
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
        String fileName = avatarFile.getName();


        //// FIXME: 4/13/16 see if file exists on server
        List<File> fileByName = getFileByName(fileName, prefs.getPhotosFolderId());
        // yes
        if (fileByName != null && fileByName.size() > 0) {
            //download it
            downloadUserImg(avatarFile, fileByName.get(0).getId());
        } else {
            //no
            //upload local if exists
            if (avatarFile.exists()){

            }
        }

//        if (!avatarFile.exists()) {
//            Bitmap bitmapFromDrawable = ImageUtil.getBitmapFromDrawable(context, R.drawable.profile_image);
//            Bitmap resizedBitmap = ImageUtil.getResizedBitmap(bitmapFromDrawable, ImageUtil.IMAGE_RESOLUTION, ImageUtil.IMAGE_RESOLUTION);
//            ImageUtil.saveUserImage(context, resizedBitmap, helper.userName());
//        }
//
//        //check if the file already exists
//        List<File> fildByName = getFileByName(fileName, prefs.getPhotosFolderId());
//        File imgFile = null;
//        if (fildByName != null && fildByName.size() > 0) {
//            imgFile = fildByName.get(0);
//        }
//
//        //Local file
//        FileContent mediaContent = new FileContent(IMAGE_MIME, new java.io.File(avatarFile.getAbsolutePath()));
//
//        //Drive file
//        List<String> parents = new ArrayList<>();
//        parents.add(prefs.getPhotosFolderId());
//        File fileMetadata = new File();
//        fileMetadata.setName(fileName);
//        fileMetadata.setMimeType(IMAGE_MIME);
//        fileMetadata.setParents(parents);
//
//        try {
//            driveService.files().create(fileMetadata, mediaContent)
//                    .setFields(QUERY_FIELDS)
//                    .execute();
//
//            if (imgFile != null) {
//                //// markme: 4/13/16 update is failing.. but that should be the prefered way
////                driveService.files().update(imgFile.getId(), imgFile, mediaContent).execute();
//                driveService.files().delete(imgFile.getId()).execute();
//            }
//            return new GetUserImgActionEventSuccess(helper.userName());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new GetUserImgActionEventFailure();
//        }

        return null;
    }

}
