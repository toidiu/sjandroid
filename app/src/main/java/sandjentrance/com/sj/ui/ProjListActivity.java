package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.BuildConfig;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.actions.DbAddNewFileAction;
import sandjentrance.com.sj.actions.DbAddNewFileActionEventFailure;
import sandjentrance.com.sj.actions.DbAddNewFileActionEventSuccess;
import sandjentrance.com.sj.actions.DbAddNewFileAction_.PsDbAddNewFileAction;
import sandjentrance.com.sj.actions.DbFindClaimedProjListAction;
import sandjentrance.com.sj.actions.DbFindClaimedProjListActionEventFailure;
import sandjentrance.com.sj.actions.DbFindClaimedProjListActionEventSuccess;
import sandjentrance.com.sj.actions.DbFindClaimedProjListAction_.PsDbFindClaimedProjListAction;
import sandjentrance.com.sj.actions.FindClaimedProjAction;
import sandjentrance.com.sj.actions.FindClaimedProjActionEventFailure;
import sandjentrance.com.sj.actions.FindClaimedProjActionEventSuccess;
import sandjentrance.com.sj.actions.FindClaimedProjAction_.PsFindClaimedProjAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventFailure;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventSuccess;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.UploadFileAction_.PsUploadFileAction;
import sandjentrance.com.sj.actions.UploadNewFileAction_.PsUploadNewFileAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.ui.extras.FabAddFileInterface;
import sandjentrance.com.sj.ui.extras.DelayedTextWatcher;
import sandjentrance.com.sj.ui.extras.ProjClickInterface;
import sandjentrance.com.sj.ui.extras.ProjListAdapter;
import sandjentrance.com.sj.utils.UtilFile;
import sandjentrance.com.sj.utils.UtilImage;

@EventListener(producers = {
        FindFolderChildrenAction.class,
        DbFindClaimedProjListAction.class,
        FindClaimedProjAction.class,
        DbAddNewFileAction.class
})
public class ProjListActivity extends BaseActivity implements ProjClickInterface, FabAddFileInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final int REQ_CODE_NEW_IMG = 22935;
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.search)
    EditText searchView;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private ProjListAdapter adapter;
    //endregion
    private Snackbar snackbar;
    private NewFileObj newFileObj;
    private Uri imagePickerUri;
    private String actionIdFileList;
    private String actionIdClaimedList;
    //region PennStation----------------------
    ProjListActivityEventListener eventListener = new ProjListActivityEventListener() {
        @Override
        public void onEventMainThread(FindClaimedProjActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(FindClaimedProjActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            adapter.refreshView(Arrays.asList(event.results));
//            actionIdClaimedList = PennStation.requestAction(PsDbFindClaimedProjListAction.helper());
        }

        @Override
        public void onEventMainThread(DbAddNewFileActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            NewFileObj newFileObj = event.newFileObj;
            if (!newFileObj.parentName.equals(BaseAction.PHOTOS_FOLDER_NAME)) {
                LocalFileObj localFileObj = new LocalFileObj(newFileObj.title, newFileObj.mime, newFileObj.localFilePath);
                openLocalFile(localFileObj, null);
            }
        }

        @Override
        public void onEventMainThread(DbAddNewFileActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(DbFindClaimedProjListActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(DbFindClaimedProjListActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            if (event.getResponseInfo().mRequestId.equals(actionIdClaimedList)) {
                adapter.refreshView(Arrays.asList(event.results));
            }
        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionEventFailure event) {
            progress.setVisibility(View.GONE);
//            actionIdClaimedList = PennStation.requestAction(PsDbFindClaimedProjListAction.helper());
        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            archiveFileHelper.wasArhived = false;

            if (event.getResponseInfo().mRequestId.equals(actionIdFileList)) {
                adapter.refreshView(Arrays.asList(event.results));
            }
        }
    };
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context) {
        return new Intent(context, ProjListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proj_list_activity);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PennStation.registerListener(eventListener);

        if (archiveFileHelper.wasArhived) {
            refreshFileListFromText();
        }

        PennStation.requestAction(PsUploadNewFileAction.helper());
        PennStation.requestAction(PsUploadFileAction.helper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        PennStation.unRegisterListener(eventListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_NEW_IMG) {
                String uriString = UtilImage.getImageUriFromIntent(data, imagePickerUri);

                if (uriString.startsWith("content")) {
                    //save content media to external storage
                    try {
                        InputStream source = getContentResolver().openInputStream(Uri.parse(uriString));
                        org.apache.commons.io.FileUtils.copyInputStreamToFile(source, new File(imagePickerUri.getPath()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                newFileObj.localFilePath = imagePickerUri.getPath();
                progress.setVisibility(View.VISIBLE);
                PennStation.requestAction(PsDbAddNewFileAction.helper(newFileObj));
            }
        }
    }
    //endregion

    //region Init----------------------
    private void initData() {
        //fixme make separate queues
        progress.setVisibility(View.VISIBLE);
        actionIdClaimedList = PennStation.requestAction(PsDbFindClaimedProjListAction.helper());
        PennStation.requestAction(PsFindClaimedProjAction.helper());

        PennStation.requestAction(PsUploadNewFileAction.helper());
        PennStation.requestAction(PsUploadFileAction.helper());
    }

    private void initView() {
        initBg();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProjListAdapter(this);
        recyclerView.setAdapter(adapter);

        DelayedTextWatcher.OnTextChanged projSearchTextChanged = new DelayedTextWatcher.OnTextChanged() {
            @Override
            public void onTextChanged(String text) {
                refreshFileListFromText();
            }
        };
        DelayedTextWatcher.addTo(searchView, projSearchTextChanged, 500);

    }
    //endregion

    //region View----------------------
    private void refreshFileListFromText() {
        String searchName = searchView.getText().toString();
        if (searchName.isEmpty()) {
            adapter.refreshView(new ArrayList<FileObj>());
        } else if (searchName.length() < 3) {
            snackbar = Snackbar.make(recyclerView, R.string.search_proj_hint, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
            }

            refreshFileList(searchName);
        }
    }

    private void refreshFileList(String search) {
        actionIdFileList = PennStation.requestAction(PsFindFolderChildrenAction.helper(search, prefs.getBaseFolderId(), true));
        progress.setVisibility(View.VISIBLE);
    }
    //endregion

    //region Helper----------------------
    public void choosePicture(int requestCode, File externalFile) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(externalFile));
        imagePickerUri = Uri.parse(externalFile.getAbsolutePath());
        if (BuildConfig.DEBUG) {
            Log.d("photo location", imagePickerUri.toString());
        }

        String pickTitle = getString(R.string.select_picture);
        Intent chooserIntent = Intent.createChooser(takePhotoIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, requestCode);
    }
    //endregion

    //region Interface----------------------
    @Override
    public void folderClicked(FileObj fileObj) {
        if (fileObj.title.equals(BaseAction.ARCHIVE_FOLDER_SETUP)) {
            startActivity(ArchiveFileListActivity.getInstance(this, fileObj));
        } else if (fileObj.title.equals(BaseAction.PHOTOS_FOLDER_SETUP)) {
            startActivity(GenericFileListActivity.getInstance(this, fileObj));
        } else {
            startActivity(ProjDetailActivity.getInstance(this, fileObj));
        }
    }

    @Override
    public void addClicked(FileObj fileObj) {
        DialogAddFile.getInstance(fileObj).show(getSupportFragmentManager(), null);
    }

    @Override
    public void fileClicked(FileObj fileObj) {
        //do nothing
    }

    @Override
    public void addItemClicked(NewFileObj newFileObj) {
        this.newFileObj = newFileObj;
        if (newFileObj.parentName.equals(BaseAction.PHOTOS_FOLDER_NAME)) {
            //get photo
            String fileNAme = newFileObj.title + System.currentTimeMillis();
            File localFile = UtilFile.getLocalFile(fileNAme, BaseAction.MIME_JPEG);
            choosePicture(REQ_CODE_NEW_IMG, localFile);
        } else {
            progress.setVisibility(View.VISIBLE);
            PennStation.requestAction(PsDbAddNewFileAction.helper(newFileObj));
        }
    }
    //endregion

}