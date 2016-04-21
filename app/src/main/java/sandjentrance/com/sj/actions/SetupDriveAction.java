package sandjentrance.com.sj.actions;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.edisonwang.ps.annotations.ClassField;
import com.edisonwang.ps.annotations.EventClass;
import com.edisonwang.ps.annotations.EventProducer;
import com.edisonwang.ps.annotations.Kind;
import com.edisonwang.ps.annotations.RequestAction;
import com.edisonwang.ps.annotations.RequestActionHelper;
import com.edisonwang.ps.lib.ActionRequest;
import com.edisonwang.ps.lib.ActionResult;
import com.edisonwang.ps.lib.EventServiceImpl;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "parentId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success"),
        @EventClass(classPostFix = "Failure")
})

public class SetupDriveAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private String parentId;


    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        FindFolderChildrenActionHelper helper = PsFindFolderChildrenAction.helper(actionRequest.getArguments(getClass().getClassLoader()));
        parentId = helper.parentId();

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        List<ParentReference> parents = new ArrayList<>();
        parents.add(new ParentReference().setId(parentId));

        if (checkAndCreateArchive(parents, parentId) && checkAndCreatePhotos(parents, parentId)) {
            prefs.setBaseFolderId(parentId);
            return new SetupDriveActionEventSuccess();
        } else {
            return new SetupDriveActionEventFailure();
        }

    }

}
