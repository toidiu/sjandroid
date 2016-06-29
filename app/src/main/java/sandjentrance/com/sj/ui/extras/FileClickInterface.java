package sandjentrance.com.sj.ui.extras;

import sandjentrance.com.sj.models.FileObj;

/**
 * Created by toidiu on 4/6/16.
 */
public interface FileClickInterface {
    void folderClicked(FileObj fileObj);

    void renameLongClicked(FileObj fileObj);

    void moveLongClicked(FileObj fileObj);

    void deleteLongClicked(FileObj fileObj);

    void shareClicked(FileObj fileObj);

    void printClicked(FileObj fileObj);

    void duplicateClicked(FileObj fileObj);

    void editClicked(FileObj fileObj);

    void doMerge();
}
