package sandjentrance.com.sj.utils;

import java.util.HashSet;
import java.util.Set;

import sandjentrance.com.sj.models.FileObj;

/**
 * Created by toidiu on 4/7/16.
 */
public class MultiShareHelper {

    public Set<FileObj> shareMultiple;

    public MultiShareHelper() {
        this.shareMultiple = new HashSet<>();
    }
}
