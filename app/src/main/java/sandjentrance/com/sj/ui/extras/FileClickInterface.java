package sandjentrance.com.sj.ui.extras;

import sandjentrance.com.sj.models.FileObj;

/**
 * Created by toidiu on 4/6/16.
 */
public interface FileClickInterface {
    void folderClicked(FileObj fileObj);

//    void fileLongClicked(FileObj fileObj);
    void renameLongClicked(FileObj fileObj);
    void moveLongClicked(FileObj fileObj);


//        void fileClicked(FileObj fileObj);
    void shareClicked(FileObj fileObj);

    void printClicked(FileObj fileObj);

    void editClicked(FileObj fileObj);
}
