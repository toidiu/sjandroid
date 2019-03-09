package sandjentrance.com.sj.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by toidiu on 4/2/16.
 */
@ParcelablePlease
public class LocalFileObj implements Parcelable {
    public static final Creator<LocalFileObj> CREATOR = new Creator<LocalFileObj>() {
        public LocalFileObj createFromParcel(Parcel source) {
            LocalFileObj target = new LocalFileObj();
            LocalFileObjParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public LocalFileObj[] newArray(int size) {
            return new LocalFileObj[size];
        }
    };
    //region Fields----------------------
    @DatabaseField(generatedId = true)
    public int dbId;
    @DatabaseField
    public String title;
    @DatabaseField
    public String mime;
    //endregion
    @DatabaseField
    public String localPath;

    //region Constructor----------------------
    @SuppressWarnings("unused")
    public LocalFileObj() {
    }
    //endregion

    //region Helper----------------------
    //endregion


    public LocalFileObj(String title, String mime, String localPath) {
        this.title = title;
        this.mime = mime;
        this.localPath = localPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        LocalFileObjParcelablePlease.writeToParcel(this, dest, flags);
    }
}
