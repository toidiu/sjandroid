package sandjentrance.com.sj.ui.extras;

import android.view.View;

import sandjentrance.com.sj.models.FileObj;

/**
 * Created by toidiu on 4/6/16.
 */
public interface FileArchiveListInterface {
    void fileClicked(FileObj fileObj);

    void fileLongClicked(FileObj fileObj);

    void unarchiveFile(FileObj item);
}
