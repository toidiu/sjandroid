package sandjentrance.com.sj.actions;

import android.content.Context;
import android.os.Bundle;

import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.lib.Action;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.utils.Prefs;


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
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    @Inject
    Context context;
    @Inject
    Prefs prefs;
    @Inject
    GoogleAccountCredential credential;


    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        ((SJApplication) SJApplication.appContext).getAppComponent().inject(this);

        return new BaseActionEvent();
    }

    //region Helper----------------------
    protected List<FileObj> queryFileList(Drive driveService, String search) throws IOException {

        List<FileObj> retFile = new ArrayList<>();

        FileList result = driveService.files().list()
                .setQ(search)
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name, modifiedTime, owners, mimeType, parents, properties)")
                .execute();


        List<File> files = result.getFiles();
        if (files != null) {
            for (File f : files) {
                FileObj object = new FileObj(f);
                retFile.add(object);
            }
        }
        return retFile;
    }

    protected List<FileObj> getFolderById(Drive driveService, String fileId, String baseFolderId) {
        String search = "id = '" + fileId + "'"
                + " and " + "'" + baseFolderId + "'" + " in parents"
                + " and " + "mimeType = '" + FileObj.FOLDER_MIME + "'";

        List<FileObj> dataFromApi = new ArrayList<>();
        try {
            dataFromApi = queryFileList(driveService, search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFromApi;
    }

    protected List<FileObj> getFoldersByName(Drive driveService, String folderName, String baseFolderId) {
        String search = "name = '" + folderName + "'"
                + " and " + "'" + baseFolderId + "'" + " in parents"
                + " and " + "mimeType = '" + FileObj.FOLDER_MIME + "'";

        List<FileObj> dataFromApi = new ArrayList<>();
        try {
            dataFromApi = queryFileList(driveService, search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataFromApi;
    }
    //endregion


}
