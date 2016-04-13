package sandjentrance.com.sj.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Created by toidiu on 4/12/16.
 */
@ParcelablePlease
public class FileDownloadObj implements Parcelable {

    public int dbId;
    public String fileId;
    public String fileName;
    public String mime;


    public FileDownloadObj(String fileName, String mime) {
        this.fileName = fileName;
        this.mime = mime;
    }

    public FileDownloadObj() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        FileDownloadObjParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<FileDownloadObj> CREATOR = new Creator<FileDownloadObj>() {
        public FileDownloadObj createFromParcel(Parcel source) {
            FileDownloadObj target = new FileDownloadObj();
            FileDownloadObjParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public FileDownloadObj[] newArray(int size) {
            return new FileDownloadObj[size];
        }
    };
}
