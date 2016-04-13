package sandjentrance.com.sj.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Created by toidiu on 4/12/16.
 */
@ParcelablePlease
public class FileUploadObj implements Parcelable {

    public int dbId;
    public String fileId;
    public String parentId;
    public String fileName;
    public String localFilePath;
    public String mime;

    public FileUploadObj(String parentId, String fileName, String localFilePath, String mime) {
        this.parentId = parentId;
        this.fileName = fileName;
        this.localFilePath = localFilePath;
        this.mime = mime;
    }

    public FileUploadObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        FileUploadObjParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<FileUploadObj> CREATOR = new Creator<FileUploadObj>() {
        public FileUploadObj createFromParcel(Parcel source) {
            FileUploadObj target = new FileUploadObj();
            FileUploadObjParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public FileUploadObj[] newArray(int size) {
            return new FileUploadObj[size];
        }
    };
}
