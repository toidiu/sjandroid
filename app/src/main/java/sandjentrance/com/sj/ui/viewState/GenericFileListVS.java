package sandjentrance.com.sj.ui.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import java.util.ArrayList;

import sandjentrance.com.sj.models.FileObj;


@ParcelablePlease
public class GenericFileListVS implements Parcelable {
    public enum SnackbarState {
        None,
        PDF_MERGE,
    }


    public static final Creator<GenericFileListVS> CREATOR = new Creator<GenericFileListVS>() {
        public GenericFileListVS createFromParcel(Parcel source) {
            GenericFileListVS target = new GenericFileListVS();
            GenericFileListVSParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public GenericFileListVS[] newArray(int size) {
            return new GenericFileListVS[size];
        }
    };
    public boolean isProgressVisible;
    public SnackbarState snackbarState;
    public ArrayList<FileObj> stateListData;
//    public boolean isSyncFabVisible;
//    public boolean isSyncBgVisible;

    public GenericFileListVS() {
        this.isProgressVisible = false;
        this.snackbarState = SnackbarState.None;
        this.stateListData = new ArrayList<>();
//        this.isSyncFabVisible = false;
//        this.isSyncBgVisible = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
