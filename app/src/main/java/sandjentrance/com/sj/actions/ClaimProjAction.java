package sandjentrance.com.sj.actions;

import android.os.Bundle;
import android.util.Log;

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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Property;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sandjentrance.com.sj.actions.ClaimProjAction_.PsClaimProjAction;
import sandjentrance.com.sj.models.FileObj;


/**
 * Created by toidiu on 3/28/16.
 */
@RequestAction
@RequestActionHelper(variables = {
        @ClassField(name = "fileId", kind = @Kind(clazz = String.class), required = true),
})
@EventProducer(generated = {
        @EventClass(classPostFix = "Success", fields = {
                @ParcelableClassField(name = "claimUser", kind = @Kind(clazz = String.class))
        }),
        @EventClass(classPostFix = "Failure")
})

public class ClaimProjAction extends BaseAction {

    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    @Override
    public ActionResult processRequest(EventServiceImpl service, ActionRequest actionRequest, Bundle bundle) {
        super.processRequest(service, actionRequest, bundle);
        ClaimProjActionHelper helper = PsClaimProjAction.helper(actionRequest.getArguments(getClass().getClassLoader()));

        if (credential.getSelectedAccountName() == null) {
            return new SetupDriveActionEventFailure();
        }

        File fileMetadata = new File();
//        Map<String, String> prop = new HashMap<>();
//        prop.put(CLAIM_PROPERTY, "asdf32d");
        List<Property> pp = new ArrayList<>();
        Property property = new Property();
        property.setKey(CLAIM_PROPERTY).setValue(credential.getSelectedAccountName());
        pp.add(property);
        fileMetadata.setProperties(pp);

        Log.d("--------", credential.getSelectedAccountName());
        try {
            File file = driveService.files().update(helper.fileId(), fileMetadata)
//                    .setFields(QUERY_FIELDS)
                    .execute();
            FileObj fileObj = new FileObj(file);

            Dao<FileObj, Integer> dao = databaseHelper.getClaimProjDao();
            List<FileObj> fileObjList = dao.queryForEq("id", fileObj.id);
            if (fileObjList.isEmpty())
            {
                dao.create(fileObj);
            }

            return new ClaimProjActionEventSuccess(credential.getSelectedAccountName());
        } catch (Exception e) {
            e.printStackTrace();
            return new ClaimProjActionEventFailure();
        }

    }

}
