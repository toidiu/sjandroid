package sandjentrance.com.sj.models;

import android.support.annotation.NonNull;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.User;

import java.util.List;

/**
 * Created by toidiu on 4/2/16.
 */
public class FileObj {
    // FIXME: 4/2/16 New activity to search for the project folder!
    public static final String PROJ_ID = " '0Bx-nVlmnGRT3b3hfMGhPLWVKYkE' ";
    public static final String FOLDER_MIME = "application/vnd.google-apps.folder";


    public String id;
    public String title;
    public String mime;
    public String owner;
    public String lastModified;
    public String parent;

//    public FileObj(String id, String title, String mime, String owner, String lastModified, String parent) {
//        this.id = id;
//        this.title = title;
//        this.mime = mime;
//        this.owner = owner;
//        this.lastModified = lastModified;
//        this.parent = parent;
//    }

    public FileObj(File f) {
        this.id = f.getId();
        this.title = f.getName();
        this.mime = f.getMimeType();
        this.lastModified = f.getModifiedTime().toString();

        List<User> owners = f.getOwners();
        if (owners.size() > 0) {
            this.owner = owners.get(0).getDisplayName();
        }
        List<String> parents = f.getParents();
        if (parents.size() > 0) {
            this.parent = parents.get(0);
        }

    }

    public static boolean isFolder(@NonNull String mime) {
        return mime.equals(FOLDER_MIME);
    }

    public static boolean isBaseFolder(@NonNull String id) {
        return id.equals(PROJ_ID);
    }
}
