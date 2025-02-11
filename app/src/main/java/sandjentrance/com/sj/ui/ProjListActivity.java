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

import com.crashlytics.android.Crashlytics;
import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import org.apache.commons.io.FilenameUtils;

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
import sandjentrance.com.sj.actions.CheckUploadStatusAction;
import sandjentrance.com.sj.actions.CheckUploadStatusAction_.PsCheckUploadStatusAction;
import sandjentrance.com.sj.actions.DbAddNewFileAction;
import sandjentrance.com.sj.actions.DbAddNewFileAction_.PsDbAddNewFileAction;
import sandjentrance.com.sj.actions.DbFindClaimedProjListAction;
import sandjentrance.com.sj.actions.DbFindClaimedProjListAction_.PsDbFindClaimedProjListAction;
import sandjentrance.com.sj.actions.DownloadFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction_.PsDownloadFileAction;
import sandjentrance.com.sj.actions.FindClaimedProjAction;
import sandjentrance.com.sj.actions.FindClaimedProjAction_.PsFindClaimedProjAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.UploadFileAction;
import sandjentrance.com.sj.actions.UploadFileAction_.PsUploadFileAction;
import sandjentrance.com.sj.actions.UploadNewFileAction;
import sandjentrance.com.sj.actions.UploadNewFileAction_.PsUploadNewFileAction;
import sandjentrance.com.sj.actions.events.CheckUploadStatusActionFailure;
import sandjentrance.com.sj.actions.events.CheckUploadStatusActionSuccess;
import sandjentrance.com.sj.actions.events.DbAddNewFileActionFailure;
import sandjentrance.com.sj.actions.events.DbAddNewFileActionSuccess;
import sandjentrance.com.sj.actions.events.DbFindClaimedProjListActionFailure;
import sandjentrance.com.sj.actions.events.DbFindClaimedProjListActionSuccess;
import sandjentrance.com.sj.actions.events.DownloadFileActionDwgConversion;
import sandjentrance.com.sj.actions.events.DownloadFileActionFailure;
import sandjentrance.com.sj.actions.events.DownloadFileActionSuccess;
import sandjentrance.com.sj.actions.events.FindClaimedProjActionFailure;
import sandjentrance.com.sj.actions.events.FindClaimedProjActionSuccess;
import sandjentrance.com.sj.actions.events.FindFolderChildrenActionFailure;
import sandjentrance.com.sj.actions.events.FindFolderChildrenActionSuccess;
import sandjentrance.com.sj.actions.events.UploadFileActionFailure;
import sandjentrance.com.sj.actions.events.UploadFileActionSuccess;
import sandjentrance.com.sj.actions.events.UploadNewFileActionFailure;
import sandjentrance.com.sj.actions.events.UploadNewFileActionSuccess;
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.ui.extras.DelayedTextWatcher;
import sandjentrance.com.sj.ui.extras.FabAddFileInterface;
import sandjentrance.com.sj.ui.extras.ProjClickInterface;
import sandjentrance.com.sj.ui.extras.ProjListAdapter;
import sandjentrance.com.sj.ui.viewstate.ProjListVS;
import sandjentrance.com.sj.utils.UtilFile;
import sandjentrance.com.sj.utils.UtilImage;
import sandjentrance.com.sj.utils.UtilKeyboard;
import sandjentrance.com.sj.utils.UtilsView;


@EventListener(producers = {
        FindFolderChildrenAction.class,
        DbFindClaimedProjListAction.class,
        FindClaimedProjAction.class,
        DbAddNewFileAction.class,
        CheckUploadStatusAction.class,
        UploadNewFileAction.class,
        DownloadFileAction.class,
        UploadFileAction.class
})
public class ProjListActivity extends BaseActivity implements ProjClickInterface, FabAddFileInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final int REQ_CODE_NEW_IMG = 22935;
    public static final String saveStateImageUri = "saveStateImageUri";
    public static final String saveStateNewFileObj = "saveStateNewFileObj";
    public static final String saveStateviewState = "saveStateviewState";
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.search)
    EditText searchView;
    @Bind(R.id.sync_bg)
    View syncBg;
    @Bind(R.id.sync_fab)
    View syncFab;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private String actionIdDownload;
    private ProjListAdapter adapter;
    private Snackbar snackbar;
    private NewFileObj newFileObj;
    private Uri imagePickerUri;
    private String actionIdFileList;
    private String actionIdClaimedList;
    //~=~=~=~=~=~=~=~=~=~=~=~=View State
    private ProjListVS viewState = new ProjListVS();

    //endregion

    //region PennStation----------------------
    ProjListActivityEventListener eventListener = new ProjListActivityEventListener() {

        @Override
        public void onEventMainThread(DownloadFileActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
            Snackbar.make(progress, R.string.error_network, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(DownloadFileActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            if (event.getResponseInfo().mRequestId.equals(actionIdDownload)) {
                LocalFileObj localFileObj = event.localFileObj;
                if (event.ActionEnum.equals(DownloadFileAction.ActionEnum.EDIT.name())) {
                    openLocalFile(localFileObj, progress);
                } else if (event.ActionEnum.equals(DownloadFileAction.ActionEnum.SHARE.name())) {
                    shareIntentFile(localFileObj);
                } else if (event.ActionEnum.equals(DownloadFileAction.ActionEnum.PRINT.name())) {
                    printIntentFile(localFileObj);
                }
            }
        }

        @Override
        public void onEventMainThread(DownloadFileActionDwgConversion event) {
            viewState.isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(FindClaimedProjActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(FindClaimedProjActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();

            viewState.stateListData = new ArrayList<>(Arrays.asList(event.results));
            adapter.refreshView(viewState.stateListData);
        }

        @Override
        public void onEventMainThread(UploadNewFileActionFailure event) {
            Snackbar.make(progress, event.errorMsg, Snackbar.LENGTH_SHORT).show();
            checkSyncStatus();
        }

        @Override
        public void onEventMainThread(UploadNewFileActionSuccess event) {
            checkSyncStatus();
        }

        @Override
        public void onEventMainThread(UploadFileActionSuccess event) {
            checkSyncStatus();
        }

        @Override
        public void onEventMainThread(UploadFileActionFailure event) {
            Snackbar.make(progress, event.errorMsg, Snackbar.LENGTH_SHORT).show();
            checkSyncStatus();
        }

        @Override
        public void onEventMainThread(DbAddNewFileActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            NewFileObj newFileObj = event.newFileObj;
            if (!newFileObj.parentName.equals(BaseAction.PHOTOS_FOLDER_NAME)) {
                LocalFileObj localFileObj = new LocalFileObj(newFileObj.title, newFileObj.mime, newFileObj.localFilePath);
                openLocalFile(localFileObj, null);
            }
        }

        @Override
        public void onEventMainThread(DbAddNewFileActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(CheckUploadStatusActionSuccess event) {
            viewState.isProgressVisible = false;

            if (event.isSynced) {
                int px = UtilsView.dpToPx(getResources(), 16);
                UtilsView.setMargins(recyclerView, 0, 0, 0, px);
                viewState.isSyncFabVisible = false;
                viewState.isSyncBgVisible = false;
            } else {
                UtilsView.setMargins(recyclerView, 0, 0, 0, (int) getResources().getDimension(R.dimen.sync_bg_plus10));
                viewState.isSyncFabVisible = true;
                viewState.isSyncBgVisible = true;
            }
            updateView();
        }

        @Override
        public void onEventMainThread(CheckUploadStatusActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(DbFindClaimedProjListActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(DbFindClaimedProjListActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            if (event.getResponseInfo().mRequestId.equals(actionIdClaimedList)) {
                viewState.stateListData = new ArrayList<>(Arrays.asList(event.results));
                adapter.refreshView(viewState.stateListData);

            }
        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            archiveFileHelper.wasArhived = false;
            claimChangedFileHelper.wasClaimedChanged = false;

            if (event.getResponseInfo().mRequestId.equals(actionIdFileList)) {
                viewState.stateListData = new ArrayList<>(Arrays.asList(event.results));
                adapter.refreshView(viewState.stateListData);
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

        if (savedInstanceState == null) {
            initData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initClickListeners();

        PennStation.registerListener(eventListener);

        if (archiveFileHelper.wasArhived || claimChangedFileHelper.wasClaimedChanged) {
            refreshFileListFromText();
        }

        checkSyncStatus();
        if (mergePfdHelper.isMerging) {
            startActivity(ProjDetailActivity.getInstance(this, mergePfdHelper.projFolder));
        }
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
                String uriString = null;

                File file = new File(imagePickerUri.getPath());
                if (data == null && !file.exists()) {
                    Snackbar.make(progress, "Sorry, there was an error while retrieving the image.", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (data != null) {
                    uriString = UtilImage.getImageUriFromIntent(data, imagePickerUri);
                }

                if (uriString != null && uriString.startsWith("content")) {
                    //save content media to external storage
                    try {
                        InputStream source = getContentResolver().openInputStream(Uri.parse(uriString));
                        org.apache.commons.io.FileUtils.copyInputStreamToFile(source, new File(imagePickerUri.getPath()));
                    } catch (IOException e) {
                        Crashlytics.getInstance().core.logException(e);
                    }
                }

                newFileObj.localFilePath = imagePickerUri.getPath();
                viewState.isProgressVisible = true;
                updateView();
                PennStation.requestAction(PsDbAddNewFileAction.helper(newFileObj));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imagePickerUri != null) {
            outState.putString(saveStateImageUri, imagePickerUri.toString());
        }
        if (newFileObj != null) {
            outState.putParcelable(saveStateNewFileObj, newFileObj);
        }

        outState.putParcelable(saveStateviewState, viewState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(saveStateImageUri)) {
            imagePickerUri = Uri.parse(savedInstanceState.getString(saveStateImageUri));
        }
        if (savedInstanceState.containsKey(saveStateNewFileObj)) {
            newFileObj = savedInstanceState.getParcelable(saveStateNewFileObj);
        }
        viewState = savedInstanceState.getParcelable(saveStateviewState);
    }

    //endregion

    //region Init----------------------
    private void initData() {
        viewState.isProgressVisible = true;
        updateView();
        actionIdClaimedList = PennStation.requestAction(PsDbFindClaimedProjListAction.helper());
        PennStation.requestAction(PsFindClaimedProjAction.helper());

        checkSyncStatus();
    }

    private void initView() {
        initBg();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProjListAdapter(this);
        recyclerView.setAdapter(adapter);

        if (viewState.stateListData != null && !viewState.stateListData.isEmpty()) {
            adapter.refreshView(viewState.stateListData);
        }

        DelayedTextWatcher.OnTextChanged projSearchTextChanged = new DelayedTextWatcher.OnTextChanged() {
            @Override
            public void onTextChanged(String text) {
                if (text.isEmpty())
                    actionIdClaimedList = PennStation.requestAction(PsDbFindClaimedProjListAction.helper());
                else
                    refreshFileListFromText();

            }
        };
        DelayedTextWatcher.addTo(searchView, projSearchTextChanged, 500);
        updateView();
        updateSnackbar();
    }

    private void initClickListeners() {
        syncFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSync();
            }
        });
    }
    //endregion

    //region View----------------------
    private void refreshFileListFromText() {
        String searchName = searchView.getText().toString();
        if (searchName.isEmpty()) {
            actionIdClaimedList = PennStation.requestAction(PsDbFindClaimedProjListAction.helper());
        } else if (searchName.length() < 3) {
            viewState.snackbarMsg = getResources().getString(R.string.search_proj_hint);
            updateSnackbar();
        } else {
            if (snackbar != null) {
                viewState.snackbarMsg = "";
                updateSnackbar();
            }

            refreshFileList(searchName);
        }
    }

    private void refreshFileList(String search) {
        actionIdFileList = PennStation.requestAction(PsFindFolderChildrenAction.helper(search, prefs.getBaseFolderId(), true));
        viewState.isProgressVisible = true;
        updateView();
    }
    //endregion

    //region Helper----------------------
    private void attemptSync() {
        viewState.isProgressVisible = true;
        updateView();
        PennStation.requestAction(PsUploadNewFileAction.helper(), longTaskQueue);
        PennStation.requestAction(PsUploadFileAction.helper(), longTaskQueue);
    }

    private void checkSyncStatus() {
        PennStation.requestAction(PsCheckUploadStatusAction.helper(), longTaskQueue);
    }

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

    private void updateView() {
        if (viewState.isSyncFabVisible) {
            syncFab.setVisibility(View.VISIBLE);
        } else {
            syncFab.setVisibility(View.GONE);
        }
        if (viewState.isSyncBgVisible) {
            syncBg.setVisibility(View.VISIBLE);
        } else {
            syncBg.setVisibility(View.GONE);
        }
        if (viewState.isProgressVisible) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    private void updateSnackbar() {
        if (viewState.snackbarMsg != null) {
            if (!viewState.snackbarMsg.isEmpty()) {
                snackbar = Snackbar.make(recyclerView, viewState.snackbarMsg, Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            } else {
                if(snackbar != null) {
                    snackbar.dismiss();
                }
            }
        }
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
            File localFile = UtilFile.getLocalFileWithExtension(FilenameUtils.removeExtension(newFileObj.title), BaseAction.MIME_JPEG);
            choosePicture(REQ_CODE_NEW_IMG, localFile);
        } else {
            viewState.isProgressVisible = true;
            updateView();
            PennStation.requestAction(PsDbAddNewFileAction.helper(newFileObj));
        }
    }

    @Override
    public void mergePdfClicked() {
        UtilKeyboard.hideKeyboard(this, searchView, searchView);
        startActivity(ProjDetailActivity.getInstance(this, mergePfdHelper.projFolder));
    }

    @Override
    public void openPoPdfClicked(FileObj fileObj) {
        viewState.isProgressVisible = true;
        updateView();
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, DownloadFileAction.ActionEnum.EDIT.name()));
    }
    //endregion

}