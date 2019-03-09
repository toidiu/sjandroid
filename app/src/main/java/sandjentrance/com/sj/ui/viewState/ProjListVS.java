package sandjentrance.com.sj.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import java.util.ArrayList;

import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.FileUploadObj;
import sandjentrance.com.sj.models.FileUploadObjParcelablePlease;


@ParcelablePlease
public class ProjListVS implements Parcelable {

    public static final Creator<ProjListVS> CREATOR = new Creator<ProjListVS>() {
        public ProjListVS createFromParcel(Parcel source) {
            ProjListVS target = new ProjListVS();
            ProjListVSParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ProjListVS[] newArray(int size) {
            return new ProjListVS[size];
        }
    };
    public boolean isProgressVisible;
    public boolean isSyncFabVisible;
    public boolean isSyncBgVisible;
    public String snackbarMsg;
    public ArrayList<FileObj> stateListData;

    public ProjListVS() {
        this.isProgressVisible = false;
        this.isSyncFabVisible = false;
        this.isSyncBgVisible = false;
        this.snackbarMsg = "";
        this.stateListData = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
