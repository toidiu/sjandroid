package sandjentrance.com.sj.actions;

import android.os.Bundle;

import com.edisonwang.ps.annotations.ClassField;
import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.ParcelableClassField;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;

import java.io.IOException;
import java.util.List;

import sandjentrance.com.sj.actions.FindBaseFolderAction_.PsFindBaseFolderAction;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "searchName", kind = @Kind(clazz = String.class), required = true)
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "results", kind = @Kind(clazz = FileObj[].class))
        }),
        @EventClass(classPostFix = "Failure")
})

public class FindBaseFolderAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field


    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        FindBaseFolderActionHelper helper = PsFindBaseFolderAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new FindBaseFolderActionEventFailure();
        }

        String search = "name contains '" + helper.searchName() + "'"
                + " and " + "name != '.DS_Store'"
//                + " and " + " sharedWithMe=true "
                + " and " + "mimeType = '" + FileObj.FOLDER_MIME + "'";

        try {
            List<FileObj> dataFromApi = toFileObjs(executeQueryList(search));
            FileObj[] array = dataFromApi.toArray(new FileObj[dataFromApi.size()]);
            return new FindBaseFolderActionEventSuccess(array);
        } catch (IOException e) {
            e.printStackTrace();
            return new FindBaseFolderActionEventFailure();
        }

    }


}
