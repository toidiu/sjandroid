package sandjentrance.com.sj.utils;

/**
 * Created by toidiu on 4/6/16.
 */
public class MoveFolderHelper {

    public String fileId;
    public String initialParentId;

    public void startMove(String fieldId, String initialParentId) {
        this.fileId = fieldId;
        this.initialParentId = initialParentId;
    }

    public boolean moveReady() {
        return fileId != null && initialParentId != null;
    }

    public void moveDone() {
        fileId = null;
        initialParentId = null;
    }

}
