package sandjentrance.com.sj.actions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.lib.Action;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Property;
import com.j256.ormlite.dao.Dao;

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
import sandjentrance.com.sj.utils.MoveFolderHelper;
import sandjentrance.com.sj.utils.Prefs;
import sandjentrance.com.sj.utils.RenameFileHelper;
import sandjentrance.com.sj.utils.UtilFile;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@EventProducer(generated = {
        @EventClass
})
public class BaseAction implements Action {

    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String CLAIM_PROPERTY = "claim";

    public static final String ARCHIVE_FOLDER_SETUP = "Archive";
    public static final String PHOTOS_FOLDER_SETUP = "Photos";

    public static final String PURCHASE_FOLDER_NAME = "Purchase Orders";
    //// FIXME: 4/19/16 get actual folder name
    public static final String LABOUR_FOLDER_NAME = "Proposals";
    public static final String FAB_FOLDER_NAME = "Fab Sheets";
    public static final String NOTES_FOLDER_NAME = "Notes";
    public static final String PHOTOS_FOLDER_NAME = "Photos";

    public static final String PURCHASE_ORDER_PDF = "PurchaseOrder.pdf";
    public static final String FAB_SHEET_PDF = "FabSheet.pdf";
    public static final String PROJECT_LABOR_PDF = "ProjectLaborRequest.pdf";
    public static final String NOTES_PDF = "Notes.pdf";

    public static final String FOLDER_MIME = "application/vnd.google-apps.folder";

    //    public static final String QUERY_FIELDS = "title";
    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_PNG = "image/png";
    public static final String MIME_PDF = "application/pdf";
    public static final String MIME_DWG1 = "application/acad";
    public static final String MIME_DWG2 = "image/vnd.dwg";

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

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        driveService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("SJ")
                .build();

        return new BaseActionEvent();
    }

    //region Folder Helper----------------------
    protected List<File> executeQueryList(String search) throws IOException {


        FileList result = driveService.files().list()
                .setQ(search)
                .execute();
        List<File> files = result.getItems();


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
        String search = "title = '" + folderName + "'"
                + " and " + "'" + baseFolderId + "'" + " in parents"
                + " and " + "mimeType = '" + FOLDER_MIME + "'";

        List<FileObj> dataFromApi = new ArrayList<>();
        try {
            dataFromApi = toFileObjs(executeQueryList(search));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFromApi;
    }

    protected List<FileObj> getFoldersByNameFuzzy(String folderName, String baseFolderId) {
        String search = "title contains '" + folderName + "'"
                + " and " + "'" + baseFolderId + "'" + " in parents"
                + " and " + "mimeType = '" + FOLDER_MIME + "'";

        List<FileObj> dataFromApi = new ArrayList<>();
        try {
            dataFromApi = toFileObjs(executeQueryList(search));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFromApi;
    }

    protected boolean claimProj(String fileId) {
        File fileMetadata = new File();
        List<Property> pp = new ArrayList<>();
        Property property = new Property();
        property.setKey(CLAIM_PROPERTY).setValue(credential.getSelectedAccountName());
        pp.add(property);
        fileMetadata.setProperties(pp);

        try {
            File file = driveService.files().update(fileId, fileMetadata)
                    .execute();
            FileObj fileObj = new FileObj(file);

            Dao<FileObj, Integer> dao = databaseHelper.getClaimProjDao();
            List<FileObj> fileObjList = dao.queryForEq("id", fileObj.id);
            if (fileObjList.isEmpty()) {
                dao.create(fileObj);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @NonNull
    protected boolean checkAndCreateArchive(List<ParentReference> parents, String parentId) {
        List<FileObj> dataFromApi = getFoldersByName(ARCHIVE_FOLDER_SETUP, parentId);

        if (dataFromApi.size() == 0) {
            File archive = new File();
            archive.setTitle(ARCHIVE_FOLDER_SETUP);
            archive.setMimeType(FOLDER_MIME);
            archive.setParents(parents);

            try {
                File file = driveService.files().insert(archive)
                        .execute();
                prefs.setArchiveFolderId(file.getId());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            prefs.setArchiveFolderId(dataFromApi.get(0).id);
            return true;
        }
    }

    @NonNull
    protected boolean checkAndCreatePhotos(List<ParentReference> parents, String parentId) {
        List<FileObj> dataFromApi = getFoldersByName(PHOTOS_FOLDER_SETUP, parentId);

        if (dataFromApi.size() == 0) {
            File photos = new File();
            photos.setTitle(PHOTOS_FOLDER_SETUP);
            photos.setMimeType(FOLDER_MIME);
            photos.setParents(parents);

            try {
                File file = driveService.files()
                        .insert(photos)
                        .execute();
                prefs.setPhotosFolderId(file.getId());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            prefs.setPhotosFolderId(dataFromApi.get(0).id);
            return true;
        }
    }
    //endregion

    //region File Helper----------------------
    protected FileObj getFileById(String fileId) {
        try {
            File file = driveService.files().get(fileId)
//                    .setFields(QUERY_FIELDS)
                    .execute();

            return new FileObj(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    protected List<File> getFileByName(String fileName, String baseFolderId) {
        String search = "title = '" + fileName + "'"
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
//                    .setFields("parents")
                    .execute();

            StringBuilder previousParents = new StringBuilder();
            for (ParentReference parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }

            File file1 = new File();
            List<ParentReference> hh = new ArrayList<>();
            hh.add(new ParentReference().setId(newParentId));
            file1.setParents(hh);

            // Move the file to the new folder
            File execute = driveService.files().update(fileId, file1)
//                    .setAddParents(newParentId)
//                    .setRemoveParents(previousParents.toString())
//                    .setFields(QUERY_FIELDS)
                    .execute();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    protected java.io.File downloadFile(FileDownloadObj fileDownloadObj, java.io.File localFile) {
        if (localFile == null) {
            return null;
        } else if (localFile.exists()) {
            return localFile;
        } else {
            //download it
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(localFile);
                driveService.files().get(fileDownloadObj.fileId).executeMediaAndDownloadTo(fileOutputStream);

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
        java.io.File file = new java.io.File(fileUploadObj.localFilePath);
        if (!file.exists()) {
            //fixme well this is not recoverable...
            return null;
        }
        FileContent mediaContent = new FileContent(fileUploadObj.mime, file);

        //Drive file
        List<String> parents = new ArrayList<>();
        parents.add(fileUploadObj.parentId);
        File fileMetadata = new File();
        fileMetadata.setTitle(fileUploadObj.fileName);
        fileMetadata.setMimeType(fileUploadObj.mime);

//        fileMetadata.setParents(parents);
        List<ParentReference> df = new ArrayList<>();
        df.add(new ParentReference().setId(fileUploadObj.parentId));
        fileMetadata.setParents(df);


        try {
            return driveService.files().insert(fileMetadata, mediaContent)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    protected File replaceFile(FileUploadObj fileUploadObj) {
        //Local file
        java.io.File file = new java.io.File(fileUploadObj.localFilePath);
        if (!file.exists()) {
            return null;
        }
        FileContent mediaContent = new FileContent(fileUploadObj.mime, file);
//        FileInputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        assert inputStream != null;
//        InputStreamContent in = new InputStreamContent(fileUploadObj.mime, inputStream);

        //Drive file
//        List<String> parents = new ArrayList<>();
//        parents.add(fileUploadObj.parentId);
//        File fileMetadata = new File();
//        fileMetadata.setName(fileUploadObj.fileName);
//        fileMetadata.setMimeType(fileUploadObj.mime);
//        fileMetadata.setParents(parents);


        try {
//            File driveFile = driveService.files()
//                    .get(fileUploadObj.fileId)
////                    .setFields(QUERY_FIELDS)
//                    .execute();

            File execute = driveService.files()
                    .update(fileUploadObj.fileId, null, mediaContent)
                    .execute();


            return execute;
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

    protected File renameFile(String id, String newName, String parent) {
        File fileMetadata = new File();
        fileMetadata.setTitle(newName);

        try {
            File execute = driveService.files().update(id, fileMetadata).execute();
            renameFileHelper.parentId = parent;
            return execute;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected boolean uploadFile(@Nullable Dao<FileUploadObj, Integer> dao, FileUploadObj fileUploadObj) {
        //id fileId null
        File createdFile = null;
        if (fileUploadObj.fileId == null) {
            //yes
            Log.d("d----------", "uploadFile: 3");
            createdFile = createFile(fileUploadObj);
        } else {
            //no
            //check if file exists on drive
            FileObj fileById = getFileById(fileUploadObj.fileId);
            if (fileById != null) {
                //yes
                Log.d("d----------", "uploadFile: 2");
                createdFile = replaceFile(fileUploadObj);
            } else {
                //no
                createdFile = createFile(fileUploadObj);
                //fixme this might also return a not null value if it fails
                Log.d("d----------", "uploadFile: 1");
            }
        }

        if (createdFile != null) {
            return UtilFile.deleteLocalFile(new java.io.File(fileUploadObj.localFilePath));
        }

        return false;
    }

//endregion


}
