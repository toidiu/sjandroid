package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sandjentrance.com.sj.BuildConfig;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.ArchiveFileAction;
import sandjentrance.com.sj.actions.ArchiveFileAction_.PsArchiveFileAction;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.actions.ClaimProjAction;
import sandjentrance.com.sj.actions.ClaimProjAction_.PsClaimProjAction;
import sandjentrance.com.sj.actions.DbAddNewFileAction;
import sandjentrance.com.sj.actions.DbAddNewFileAction_.PsDbAddNewFileAction;
import sandjentrance.com.sj.actions.DbFindClaimedProjListAction;
import sandjentrance.com.sj.actions.DbFindClaimedProjListAction_.PsDbFindClaimedProjListAction;
import sandjentrance.com.sj.actions.DownloadFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction_.PsDownloadFileAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.GetUserImgAction;
import sandjentrance.com.sj.actions.GetUserImgAction_.PsGetUserImgAction;
import sandjentrance.com.sj.actions.MergePdfAction;
import sandjentrance.com.sj.actions.MoveFileAction;
import sandjentrance.com.sj.actions.MoveFileAction_.PsMoveFileAction;
import sandjentrance.com.sj.actions.RenameFileAction;
import sandjentrance.com.sj.actions.UnClaimProjAction;
import sandjentrance.com.sj.actions.UnClaimProjAction_.PsUnClaimProjAction;
import sandjentrance.com.sj.actions.events.ArchiveFileActionFailure;
import sandjentrance.com.sj.actions.events.ArchiveFileActionSuccess;
import sandjentrance.com.sj.actions.events.ClaimProjActionFailure;
import sandjentrance.com.sj.actions.events.ClaimProjActionSuccess;
import sandjentrance.com.sj.actions.events.DbAddNewFileActionFailure;
import sandjentrance.com.sj.actions.events.DbAddNewFileActionSuccess;
import sandjentrance.com.sj.actions.events.DbFindClaimedProjListActionFailure;
import sandjentrance.com.sj.actions.events.DbFindClaimedProjListActionSuccess;
import sandjentrance.com.sj.actions.events.DownloadFileActionDwgConversion;
import sandjentrance.com.sj.actions.events.DownloadFileActionFailure;
import sandjentrance.com.sj.actions.events.DownloadFileActionSuccess;
import sandjentrance.com.sj.actions.events.FindFolderChildrenActionFailure;
import sandjentrance.com.sj.actions.events.FindFolderChildrenActionSuccess;
import sandjentrance.com.sj.actions.events.GetUserImgActionFailure;
import sandjentrance.com.sj.actions.events.GetUserImgActionNoFile;
import sandjentrance.com.sj.actions.events.GetUserImgActionSuccess;
import sandjentrance.com.sj.actions.events.MergePdfActionFailure;
import sandjentrance.com.sj.actions.events.MergePdfActionSuccess;
import sandjentrance.com.sj.actions.events.MoveFileActionFailure;
import sandjentrance.com.sj.actions.events.MoveFileActionPrime;
import sandjentrance.com.sj.actions.events.MoveFileActionSuccess;
import sandjentrance.com.sj.actions.events.RenameFileActionFailure;
import sandjentrance.com.sj.actions.events.RenameFileActionSuccess;
import sandjentrance.com.sj.actions.events.UnClaimProjActionFailure;
import sandjentrance.com.sj.actions.events.UnClaimProjActionSuccess;
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.ui.extras.ArchiveInterface;
import sandjentrance.com.sj.ui.extras.FabAddFileInterface;
import sandjentrance.com.sj.ui.extras.FileClickInterface;
import sandjentrance.com.sj.ui.extras.ProjDetailAdapter;
import sandjentrance.com.sj.utils.UtilFile;
import sandjentrance.com.sj.utils.UtilImage;
import sandjentrance.com.sj.utils.UtilKeyboard;
import sandjentrance.com.sj.views.SpaceItemDecoration;

@EventListener(producers = {
        FindFolderChildrenAction.class,
        ClaimProjAction.class,
        UnClaimProjAction.class,
        MoveFileAction.class,
        RenameFileAction.class,
        ArchiveFileAction.class,
        GetUserImgAction.class,
        DbAddNewFileAction.class,
        DownloadFileAction.class,
        MergePdfAction.class,
        DbFindClaimedProjListAction.class
})

public class ProjDetailActivity extends BaseActivity implements FileClickInterface, FabAddFileInterface, ArchiveInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String FILE_OBJ = "FILE_OBJ";
    public static final int REQ_CODE_USER_IMG = 23969;
    public static final int REQ_CODE_NEW_IMG = 23970;
    public static final String saveStateImageUri = "saveStateImageUri";
    public static final String saveStateNewFileObj = "saveStateNewFileObj";
    public static final String saveStateFileObjList = "saveStateFileObjList";
    public static final String saveStateProjOwnedByMe = "saveStateProjOwnedByMe";
    public static final String saveStateActionIdClaimedList = "saveStateActionIdClaimedList";
    public static final String saveStateActionIdDownload = "saveStateActionIdDownload";
    public static final String saveStateActionIdFileList = "saveStateActionIdFileList";
    public static final String saveStateisProgressVisible = "saveStateisProgressVisible";
    public static final String saveStateisClaimBtnVisible = "saveStateisClaimBtnVisible";
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.pm_name)
    TextView pmNameView;
    @Bind(R.id.cliam_btn)
    TextView claimBtn;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.proj_title)
    TextView projTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.profile_img)
    CircleImageView profileImg;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private FileObj fileObj;
    private ProjDetailAdapter adapter;
    private Menu menu;
    //~=~=~=~=~=~=~=~=~=~=~=~=Stateful
    private String actionIdClaimedList;
    private String actionIdDownload;
    private String actionIdFileList;
    private boolean stateProjOwnedByMe = false;
    private Uri stateImagePickerUri;
    private NewFileObj stateNewFileObj;
    private ArrayList<FileObj> stateListData;
    //~=~=~=~=~=~=~=~=~=~=~=~=View State
    private boolean isProgressVisible = false;
    private boolean isClaimBtnVisible = true;
    //endregion

    //region PennStation----------------------
    ProjDetailActivityEventListener eventListener = new ProjDetailActivityEventListener() {
        @Override
        public void onEventMainThread(DbAddNewFileActionSuccess event) {
            isProgressVisible = false;
            updateView();
            NewFileObj newFileObj = event.newFileObj;

            if (!newFileObj.parentName.equals(BaseAction.PHOTOS_FOLDER_NAME)) {
                LocalFileObj localFileObj = new LocalFileObj(newFileObj.title, newFileObj.mime, newFileObj.localFilePath);
                openLocalFile(localFileObj, progress);
            }
        }

        @Override
        public void onEventMainThread(DbAddNewFileActionFailure event) {
            isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(ArchiveFileActionFailure event) {
            isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(ArchiveFileActionSuccess event) {
            isProgressVisible = false;
            updateView();
            archiveFileHelper.wasArhived = true;
            finish();
        }

        @Override
        public void onEventMainThread(MoveFileActionPrime event) {
            refreshMenu();
        }

        @Override
        public void onEventMainThread(MoveFileActionSuccess event) {
            refreshFileList();
            moveFolderHelper.resetState();
            refreshMenu();
        }

        @Override
        public void onEventMainThread(MoveFileActionFailure event) {

        }

        @Override
        public void onEventMainThread(DbFindClaimedProjListActionFailure event) {
        }

        @Override
        public void onEventMainThread(DbFindClaimedProjListActionSuccess event) {
            if (event.getResponseInfo().mRequestId.equals(actionIdClaimedList)) {
                for (FileObj f : event.results) {
                    if (f.id.equals(fileObj.id)) {
                        stateProjOwnedByMe = true;
                        refreshMenu();
                        break;
                    }
                }
            }
        }

        @Override
        public void onEventMainThread(UnClaimProjActionSuccess event) {
            isProgressVisible = false;
            isClaimBtnVisible = true;
            updateView();
            fileObj.claimUser = null;
            pmNameView.setText("");
            stateProjOwnedByMe = false;
            refreshMenu();
            claimChangedFileHelper.wasClaimedChanged = true;
        }

        @Override
        public void onEventMainThread(UnClaimProjActionFailure event) {
            isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(ClaimProjActionSuccess event) {
            isProgressVisible = false;
            isClaimBtnVisible = false;
            updateView();
            fileObj.claimUser = event.claimUser;
            pmNameView.setText(fileObj.claimUser);
            stateProjOwnedByMe = true;
            refreshMenu();
            claimChangedFileHelper.wasClaimedChanged = true;
        }

        @Override
        public void onEventMainThread(ClaimProjActionFailure event) {
            isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(RenameFileActionSuccess event) {
            refreshFileList();
        }

        @Override
        public void onEventMainThread(RenameFileActionFailure event) {

        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionFailure event) {
            isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionSuccess event) {
            isProgressVisible = false;
            updateView();
            if (event.getResponseInfo().mRequestId.equals(actionIdFileList)) {
                stateListData = new ArrayList<>(Arrays.asList(event.results));
                Collections.sort(stateListData, FileObj.getComparator());
                adapter.refreshView(stateListData);
            }
            if (mergePfdHelper.isMerging) {
                navigateToFabFolder();
            }
        }

        @Override
        public void onEventMainThread(GetUserImgActionFailure event) {
        }

        @Override
        public void onEventMainThread(GetUserImgActionNoFile event) {

        }

        @Override
        public void onEventMainThread(GetUserImgActionSuccess event) {
            if (event.userName.equals(fileObj.claimUser)) {
                invalidateAndSetUserImage();
            }
        }

        @Override
        public void onEventMainThread(DownloadFileActionFailure event) {
            isProgressVisible = false;
            updateView();
            Snackbar.make(progress, R.string.error_network, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(DownloadFileActionSuccess event) {
            isProgressVisible = false;
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
            isProgressVisible = false;
            updateView();
            Snackbar.make(progress, R.string.zamzar_started, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(MergePdfActionSuccess event) {
            isProgressVisible = false;
            updateView();
            openLocalFile(event.localFileObj, progress);
        }

        @Override
        public void onEventMainThread(MergePdfActionFailure event) {
            isProgressVisible = false;
            updateView();
        }
    };

    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context, FileObj fileObj) {
        Intent intent = new Intent(context, ProjDetailActivity.class);
        intent.putExtra(FILE_OBJ, fileObj);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proj_detail_activity);
        ButterKnife.bind(this);

        fileObj = getIntent().getParcelableExtra(FILE_OBJ);

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

        if (fileObj.parent.equals(moveFolderHelper.initialParentId) && !moveFolderHelper.moveReady()) {
            refreshFileList();
            moveFolderHelper.resetState();
        }

        refreshMenu();
        if (mergePfdHelper.isMerging) {
            navigateToFabFolder();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PennStation.unRegisterListener(eventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        this.menu = menu;
        refreshMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_paste:
                PennStation.requestAction(PsMoveFileAction.helper(fileObj.id));
                isProgressVisible = true;
                updateView();
                return true;
            case R.id.menu_archive:
                DialogConfirmArchive.getInstance().show(getSupportFragmentManager(), null);
                return true;
            case R.id.menu_claim:
                claimProject();
                return true;
            case R.id.menu_unclaim:
                unclaimProject();
                return true;
            default:
                // If we got here, the user's ActionEnum was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_USER_IMG) {
                //----------------
                String uriString = UtilImage.getImageUriFromIntent(data, stateImagePickerUri);
                startActivityForResult(UserImageCropActivity.getInstance(this, uriString), UserImageCropActivity.RESULT_CODE);

            } else if (requestCode == UserImageCropActivity.RESULT_CODE) {
                //----------------
                invalidateAndSetUserImage();
            } else if (requestCode == REQ_CODE_NEW_IMG) {
                //----------------

                String uriString = null;
                File file = new File(stateImagePickerUri.getPath());
                if (data == null && !file.exists()) {
                    Snackbar.make(progress, "Sorry, there was an error while retrieving the image.", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (data != null) {
                    uriString = UtilImage.getImageUriFromIntent(data, stateImagePickerUri);
                }

                if (uriString != null && uriString.startsWith("content")) {
                    //save content media to external storage
                    try {
                        InputStream source = getContentResolver().openInputStream(Uri.parse(uriString));
                        org.apache.commons.io.FileUtils.copyInputStreamToFile(source, new File(stateImagePickerUri.getPath()));
                    } catch (IOException e) {
                        Crashlytics.getInstance().core.logException(e);
                    }
                }

                stateNewFileObj.localFilePath = stateImagePickerUri.getPath();
                isProgressVisible = true;
                updateView();
                PennStation.requestAction(PsDbAddNewFileAction.helper(stateNewFileObj));

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (stateImagePickerUri != null) {
            outState.putString(saveStateImageUri, stateImagePickerUri.toString());
        }
        if (stateNewFileObj != null) {
            outState.putParcelable(saveStateNewFileObj, stateNewFileObj);
        }
        if (stateListData != null) {
            outState.putParcelableArrayList(saveStateFileObjList, stateListData);
        }
        if (stateListData != null) {
            outState.putBoolean(saveStateProjOwnedByMe, stateProjOwnedByMe);
        }
        if (actionIdClaimedList != null) {
            outState.putString(saveStateActionIdClaimedList, actionIdClaimedList);
        }
        if (actionIdClaimedList != null) {
            outState.putString(saveStateActionIdDownload, actionIdDownload);
        }
        if (actionIdClaimedList != null) {
            outState.putString(saveStateActionIdFileList, actionIdFileList);
        }
        outState.putBoolean(saveStateisProgressVisible, isProgressVisible);
        outState.putBoolean(saveStateisClaimBtnVisible, isClaimBtnVisible);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(saveStateImageUri)) {
            stateImagePickerUri = Uri.parse(savedInstanceState.getString(saveStateImageUri));
        }
        if (savedInstanceState.containsKey(saveStateNewFileObj)) {
            stateNewFileObj = savedInstanceState.getParcelable(saveStateNewFileObj);
        }
        if (savedInstanceState.containsKey(saveStateFileObjList)) {
            stateListData = savedInstanceState.getParcelableArrayList(saveStateFileObjList);
        }
        if (stateListData != null) {
            stateProjOwnedByMe = savedInstanceState.getBoolean(saveStateProjOwnedByMe);
        }
        if (actionIdClaimedList != null) {
            actionIdClaimedList = savedInstanceState.getString(saveStateActionIdClaimedList);
        }
        if (actionIdClaimedList != null) {
            actionIdDownload = savedInstanceState.getString(saveStateActionIdDownload);
        }
        if (actionIdClaimedList != null) {
            actionIdFileList = savedInstanceState.getString(saveStateActionIdFileList);
        }
        isProgressVisible = savedInstanceState.getBoolean(saveStateisProgressVisible);
        isClaimBtnVisible = savedInstanceState.getBoolean(saveStateisClaimBtnVisible);
    }

    //endregion

    //region Init----------------------
    private void initData() {
        actionIdClaimedList = PennStation.requestAction(PsDbFindClaimedProjListAction.helper());
        refreshFileList();
    }

    private void initView() {
        projTitle.setText(fileObj.title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (fileObj.claimUser != null && !fileObj.claimUser.equals("")) {
            isClaimBtnVisible = false;
            updateView();
            pmNameView.setText(fileObj.claimUser);
            refreshMenu();
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);

        // so we dont keep adding space on each call
        if (adapter == null) {
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.proj_detail_spacing);
            recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        }

        adapter = new ProjDetailAdapter(this);
        recyclerView.setAdapter(adapter);
        if (stateListData != null && !stateListData.isEmpty()) {
            Collections.sort(stateListData, FileObj.getComparator());
            adapter.refreshView(stateListData);
        }

        PennStation.requestAction(PsGetUserImgAction.helper(fileObj.claimUser));
        Picasso.with(this).load(UtilImage.getAvatarFile(this, fileObj.claimUser)).placeholder(R.drawable.ic_profile_image).into(profileImg);

    }

    private void initClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddFile.getInstance(fileObj).show(getSupportFragmentManager(), null);
            }
        });
        claimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claimProject();
            }
        });
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileObj.claimUser.equals(prefs.getUser())) {
                    File externalFile = UtilImage.getTempFile(ProjDetailActivity.this);
                    choosePicture(REQ_CODE_USER_IMG, externalFile);
                }
            }
        });
    }
        //endregion

    //region View----------------------
    private void refreshMenu() {
        if (menu != null) {
            if (moveFolderHelper.moveReady()) {
                menu.findItem(R.id.menu_paste).setVisible(true);
            } else {
                menu.findItem(R.id.menu_paste).setVisible(false);
            }
            menu.findItem(R.id.menu_archive).setVisible(true);

            if (fileObj.claimUser != null && !fileObj.claimUser.equals("")) {
                menu.findItem(R.id.menu_claim).setVisible(true);
            }

            if (stateProjOwnedByMe) {
                menu.findItem(R.id.menu_unclaim).setVisible(true);
            } else {
                menu.findItem(R.id.menu_unclaim).setVisible(false);
            }
        }
    }

    private void refreshFileList() {
        isProgressVisible = true;
        updateView();
        actionIdFileList = PennStation.requestAction(PsFindFolderChildrenAction.helper("", fileObj.id, false));
    }

    private void invalidateAndSetUserImage() {
        Picasso.with(this).invalidate(UtilImage.getAvatarFile(this, fileObj.claimUser));
        Picasso.with(this).load(UtilImage.getAvatarFile(this, fileObj.claimUser))
                .placeholder(R.drawable.ic_profile_image)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(profileImg);
    }
    //endregion

    //region Helper----------------------
    private void claimProject() {
        PennStation.requestAction(PsClaimProjAction.helper(fileObj.id));
        isProgressVisible = true;
        updateView();
    }

    private void unclaimProject() {
        PennStation.requestAction(PsUnClaimProjAction.helper(fileObj.id));
        isProgressVisible = true;
        updateView();
    }

    private void navigateToFabFolder() {
        if (adapter != null) {
            List<FileObj> list = adapter.getList();
            for (FileObj file : list) {
                if (file.title.matches("(?i)fab sheet.*")) {
                    startActivity(GenericFileListActivity.getInstance(this, file));
                    break;
                }
                Snackbar.make(progress, "No Fab Sheet folder found. Cancelling merge action.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void choosePicture(int requestCode, File externalFile) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(externalFile));
        stateImagePickerUri = Uri.parse(externalFile.getAbsolutePath());
        if (BuildConfig.DEBUG) {
            Log.d("photo location", stateImagePickerUri.toString());
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
        startActivity(GenericFileListActivity.getInstance(this, fileObj));
    }

    @Override
    public void renameLongClicked(FileObj fileObj) {
        DialogRenameFile.getInstance(fileObj).show(getSupportFragmentManager(), null);
    }

    @Override
    public void moveLongClicked(FileObj fileObj) {
        moveFolderHelper.startMove(fileObj.id, fileObj.parent);
        PennStation.postLocalEvent(new MoveFileActionPrime());
    }

    @Override
    public void deleteLongClicked(FileObj fileObj) {
        //this should only happend in the GenericFileListActivity
    }

    @Override
    public void editClicked(FileObj fileObj) {
        isProgressVisible = true;
        updateView();
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, DownloadFileAction.ActionEnum.EDIT.name()));
    }

    @Override
    public void doMerge() {
        //this should only happend in the GenericFileListActivity
    }

    @Override
    public void shareClicked(FileObj fileObj) {
        isProgressVisible = true;
        updateView();
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, DownloadFileAction.ActionEnum.SHARE.name()));
    }

    @Override
    public void multiShareClicked(FileObj fileObj) {
        //this should only happend in the GenericFileListActivity
    }

    @Override
    public void printClicked(FileObj fileObj) {
        isProgressVisible = true;
        updateView();
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, DownloadFileAction.ActionEnum.PRINT.name()));
    }

    @Override
    public void duplicateClicked(FileObj fileObj) {
        //this should only happend in the GenericFileListActivity
    }

    @Override
    public void addItemClicked(NewFileObj newFileObj) {
        this.stateNewFileObj = newFileObj;
        if (newFileObj.parentName.equals(BaseAction.PHOTOS_FOLDER_NAME)) {
            //get photo
            String fileNAme = newFileObj.title + System.currentTimeMillis();
            File localFile = UtilFile.getLocalFileWithExtension(fileNAme, BaseAction.MIME_JPEG);
            choosePicture(REQ_CODE_NEW_IMG, localFile);
        } else {
            isProgressVisible = true;
            updateView();
            PennStation.requestAction(PsDbAddNewFileAction.helper(newFileObj));
        }
    }

    @Override
    public void mergePdfClicked() {
        UtilKeyboard.hideKeyboard(this, progress, null);
        navigateToFabFolder();
    }

    @Override
    public void openPoPdfClicked(FileObj fileObj) {
        isProgressVisible = true;
        updateView();
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, DownloadFileAction.ActionEnum.EDIT.name()));
    }

    @Override
    public void archiveClicked() {
        PennStation.requestAction(PsArchiveFileAction.helper(fileObj.id));

        isProgressVisible = true;
        updateView();
    }
    //endregion

    //region Helper----------------------
    private void updateView() {
        if (isClaimBtnVisible) {
            claimBtn.setVisibility(View.VISIBLE);
        } else {
            claimBtn.setVisibility(View.GONE);
        }
        if (isProgressVisible) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }
    //endregion

}