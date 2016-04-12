package sandjentrance.com.sj.models;

import android.support.annotation.NonNull;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.User;

import org.parceler.Parcel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import sandjentrance.com.sj.actions.BaseAction;

/**
 * Created by toidiu on 4/2/16.
 */
@Parcel
public class FileObj {
    // FIXME: 4/2/16 New activity to search for the project folder!
//    public static final String PROJ_ID = " '0Bx-nVlmnGRT3b3hfMGhPLWVKYkE' ";
    public static final String FOLDER_MIME = "application/vnd.google-apps.folder";


    public String id;
    public String title;
    public static Comparator<FileObj> FileObjComparator = new Comparator<FileObj>() {

        public int compare(FileObj file1, FileObj file2) {

            String fileName1 = file1.title.toUpperCase();
            String fileName2 = file2.title.toUpperCase();

            //ascending order
            return fileName1.compareTo(fileName2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };
    public String mime;
    public String owner;
    public String lastModified;
    public String parent;
    @Nullable
    @android.support.annotation.Nullable
    public String claimUser;

    public FileObj(File f) {
        this.id = f.getId();
        this.title = f.getName();
        this.mime = f.getMimeType();
        this.lastModified = f.getModifiedTime().toString();

        List<User> owners = f.getOwners();
        if (owners != null && owners.size() > 0) {
            this.owner = owners.get(0).getDisplayName();
        }
        List<String> parents = f.getParents();
        if (parents != null && parents.size() > 0) {
            this.parent = parents.get(0);
        }

        Map<String, String> properties = f.getProperties();
        if (properties != null && properties.containsKey(BaseAction.CLAIM_PROPERTY)) {
            this.claimUser = properties.get(BaseAction.CLAIM_PROPERTY);
        }
    }

    @SuppressWarnings("unused")
    public FileObj() {
    }

    public static boolean isFolder(@NonNull String mime) {
        return mime.equals(FOLDER_MIME);
    }

    public static boolean isBaseFolder(@NonNull String id, @NonNull String projId) {
        return id.equals(projId);
    }
}
