package sandjentrance.com.sj.models;

import android.support.annotation.NonNull;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.User;
import com.j256.ormlite.field.DatabaseField;

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
    public static final String FOLDER_MIME = "application/vnd.google-apps.folder";

    //region Fields----------------------
    @DatabaseField(generatedId = true)
    public int dbId;

    @DatabaseField
    public String id;
    @DatabaseField
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
    @DatabaseField
    public String mime;
    @DatabaseField
    public String owner;
    @DatabaseField
    public String lastModified;
    @DatabaseField
    public String parent;
    //endregion
    @Nullable
    @android.support.annotation.Nullable
    @DatabaseField
    public String claimUser;

    //region Constructor----------------------
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
    //endregion

    @SuppressWarnings("unused")
    public FileObj() {
    }

    //region Helper----------------------
    public static boolean isFolder(@NonNull String mime) {
        return mime.equals(FOLDER_MIME);
    }

    public static boolean isBaseFolder(@NonNull String id, @NonNull String projId) {
        return id.equals(projId);
    }
    //endregion
}
