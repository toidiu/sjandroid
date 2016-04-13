package sandjentrance.com.sj.actions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.lib.Action;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.database.DatabaseHelper;
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.utils.FileUtils;
import sandjentrance.com.sj.utils.MoveFolderHelper;
import sandjentrance.com.sj.utils.Prefs;
import sandjentrance.com.sj.utils.RenameFileHelper;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@EventProducer(generated = {
        @EventClass
})
public class BaseAction implements Action {

    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String CLAIM_PROPERTY = "CLAIM_PROPERTY";
    public static final String ARCHIVE_FOLDER = "Archive";
    public static final String PHOTOS_FOLDER = "Photos";
    public static final String QUERY_FIELDS = "id, name, modifiedTime, owners, mimeType, parents, properties";
    public static final String IMAGE_MIME = "image/jpeg";
    public static final String PDF_MIME = "application/pdf";
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    @Inject
    Context context;
    @Inject
    Prefs prefs;
    @Inject
    GoogleAccountCredential credential;
    @Inject
    Drive driveService;
    @Inject
    MoveFolderHelper moveFolderHelper;
    @Inject
    RenameFileHelper renameFileHelper;
    @Inject
    DatabaseHelper databaseHelper;

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        ((SJApplication) SJApplication.appContext).getAppComponent().inject(this);

        return new BaseActionEvent();
    }

    //region Folder Helper----------------------
    protected List<File> executeQueryList(String search) throws IOException {


        FileList result = driveService.files().list()
                .setQ(search)
                .setSpaces("drive")
                .setFields("nextPageToken, files(" + QUERY_FIELDS + ")")
                .execute();
        List<File> files = result.getFiles();


        return files;
    }

    public List<FileObj> toFileObjs(List<File> files) {
        List<FileObj> retFile = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                FileObj object = new FileObj(f);
                retFile.add(object);
            }
        }
        return retFile;
    }

    protected List<FileObj> getFoldersByName(String folderName, String baseFolderId) {
        String search = "name = '" + folderName + "'"
                + " and " + "'" + baseFolderId + "'" + " in parents"
                + " and " + "mimeType = '" + FileObj.FOLDER_MIME + "'";

        List<FileObj> dataFromApi = new ArrayList<>();
        try {
            dataFromApi = toFileObjs(executeQueryList(search));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFromApi;
    }


    //endregion
    //region File Helper----------------------
    protected FileObj getFileById(String fileId) {
        try {
            File file = driveService.files().get(fileId)
                    .setFields(QUERY_FIELDS)
                    .execute();

            return new FileObj(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    protected List<File> getFileByName(String fileName, String baseFolderId) {
        String search = "name = '" + fileName + "'"
                + " and " + "'" + baseFolderId + "'" + " in parents";

        List<File> dataFromApi = new ArrayList<>();
        try {
            dataFromApi = executeQueryList(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFromApi;
    }

    protected boolean fileMoved(String fileId, String newParentId) {
        try {
            // Retrieve the existing parents to remove
            File file = driveService.files().get(fileId)
                    .setFields("parents")
                    .execute();
            StringBuilder previousParents = new StringBuilder();
            for (String parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }
            // Move the file to the new folder
            driveService.files().update(fileId, null)
                    .setAddParents(newParentId)
                    .setRemoveParents(previousParents.toString())
                    .setFields("id, parents")
                    .execute();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    protected java.io.File downloadFile(FileDownloadObj fileDownloadObj) {
        java.io.File localFile = FileUtils.getLocalFile(context, fileDownloadObj.fileId, fileDownloadObj.mime);
        if (localFile.exists()) {
            return localFile;
        } else {
            //download it
            FileOutputStream fileOutputStream = null;
            try {
                //fixme this should change depending on PDF and image
                fileOutputStream = new FileOutputStream(localFile);
                driveService.files().export(fileDownloadObj.fileId, PDF_MIME)
                        .executeMediaAndDownloadTo(fileOutputStream);
                return localFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Nullable
    protected java.io.File downloadUserImg(java.io.File avatarFile, String fileId) {
        //download it
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(avatarFile);
            driveService.files().get(fileId).executeMediaAndDownloadTo(fileOutputStream);
            return avatarFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    protected File createFile(FileUploadObj fileUploadObj) {
        //Local file
        FileContent mediaContent = new FileContent(fileUploadObj.mime, new java.io.File(fileUploadObj.localFilePath));

        //Drive file
        List<String> parents = new ArrayList<>();
        parents.add(fileUploadObj.parentId);
        File fileMetadata = new File();
        fileMetadata.setName(fileUploadObj.fileName);
        fileMetadata.setMimeType(fileUploadObj.mime);
        fileMetadata.setParents(parents);


        try {
            return driveService.files().create(fileMetadata, mediaContent)
                    .setFields(QUERY_FIELDS)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected boolean deleteFile(String fileId) {
        try {
            driveService.files().delete(fileId).execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


//endregion


}
