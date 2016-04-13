//package sandjentrance.com.sj.actions;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.annotation.NonNull;
//import android.util.Log;
//
//import com.edisonwang.ps.annotations.ClassField;
//import com.edisonwang.ps.annotations.EventClass;
//import com.edisonwang.ps.annotations.EventProducer;
//import com.edisonwang.ps.annotations.Kind;
//import com.edisonwang.ps.annotations.RequestAction;
//import com.edisonwang.ps.annotations.RequestActionHelper;
//import com.edisonwang.ps.lib.ActionRequest;
//import com.edisonwang.ps.lib.ActionResult;
//import com.edisonwang.ps.lib.EventServiceImpl;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.http.FileContent;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import sandjentrance.com.sj.actions.UploadFileAction_.PsUploadFileAction;
//import sandjentrance.com.sj.models.FileUploadObj;
//
//
///**
// * Created by toidiu on 3/28/16.
// */
//@RequestAction
//@RequestActionHelper(variables = {
//        @ClassField(name = "fileUploadObj", kind = @Kind(clazz = FileUploadObj.class), required = true),
//})
//@EventProducer(generated = {
//        @EventClass(classPostFix = "Success"),
//        @EventClass(classPostFix = "Failure")
//})
//
//public class DownloadFileAction extends BaseAction {
//
//    //~=~=~=~=~=~=~=~=~=~=~=~=Field
//
//    @Override
//    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
//        super.processRequest(service, actionRequest, bundle);
////        UploadFileActionHelper helper = PsUploadFileAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
////        FileUploadObj fileUploadObj = helper.fileUploadObj();
//
//        if (credential.getSelectedAccountName() == null) {
//            return new SetupDriveActionEventFailure();
//        }
//
//
////        String fileId = "1ZdR3L3qP4Bkq8noWLJHSr_iBau0DNT4Kli4SxNc2YEo";
////        OutputStream outputStream = new ByteArrayOutputStream();
////        driveService.files().export(fileId, "application/pdf")
////                .executeMediaAndDownloadTo(outputStream);
//
//
//
//
//
//        //Local file
//        FileContent mediaContent = new FileContent(fileUploadObj.mime, new java.io.File(fileUploadObj.filePath));
//
//        //Drive file
//        List<String> parents = new ArrayList<>();
//        parents.add(fileUploadObj.parentId);
//        File fileMetadata = new File();
//        fileMetadata.setName(fileUploadObj.fileName);
//        fileMetadata.setMimeType(fileUploadObj.mime);
//        fileMetadata.setParents(parents);
//
//
//        try {
//            File file = driveService.files().create(fileMetadata, mediaContent)
//                    .setFields(QUERY_FIELDS)
//                    .execute();
//            return new UploadFileActionEventSuccess();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new UploadFileActionEventFailure();
//        }
//    }
//
//    @NonNull
//    public static java.io.File getFile(Context context) {
//        java.io.File filesDir = context.getFilesDir();
//        java.io.File externalStorageDirectory = Environment.getExternalStorageDirectory();
//        Log.d("-----", filesDir.getAbsolutePath());
//        Log.d("-----", externalStorageDirectory.getAbsolutePath());
//
//        java.io.File download = new java.io.File(externalStorageDirectory, "Download");
//        Log.d("-----", String.valueOf(download.exists()));
//
//        //markme check name
////        java.io.File file = new java.io.File(download, "test.JPG");
//        java.io.File file = new java.io.File(download, "annaLIRR.png");
//        Log.d("-----", String.valueOf(file.exists()));
//        return file;
//    }
//}
