package sandjentrance.com.sj.ui.extras;

import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.NewFileObj;

/**
 * Created by toidiu on 4/18/16.
 */
public interface FabAddFileInterface {
    void addItemClicked(NewFileObj newFileObj);

    void mergePdfClicked();

    void openPoPdfClicked(FileObj fileObj);
}
