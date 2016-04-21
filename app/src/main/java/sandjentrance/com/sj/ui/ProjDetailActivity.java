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
import android.widget.Toast;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sandjentrance.com.sj.BuildConfig;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.ArchiveFileAction;
import sandjentrance.com.sj.actions.ArchiveFileActionEventFailure;
import sandjentrance.com.sj.actions.ArchiveFileActionEventSuccess;
import sandjentrance.com.sj.actions.ArchiveFileAction_.PsArchiveFileAction;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.actions.ClaimProjAction;
import sandjentrance.com.sj.actions.ClaimProjActionEventFailure;
import sandjentrance.com.sj.actions.ClaimProjActionEventSuccess;
import sandjentrance.com.sj.actions.ClaimProjAction_.PsClaimProjAction;
import sandjentrance.com.sj.actions.DbAddNewFileAction;
import sandjentrance.com.sj.actions.DbAddNewFileActionEventFailure;
import sandjentrance.com.sj.actions.DbAddNewFileActionEventSuccess;
import sandjentrance.com.sj.actions.DbAddNewFileAction_.PsDbAddNewFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction;
import sandjentrance.com.sj.actions.DownloadFileActionEventFailure;
import sandjentrance.com.sj.actions.DownloadFileActionEventSuccess;
import sandjentrance.com.sj.actions.DownloadFileAction_.PsDownloadFileAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventFailure;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventSuccess;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.GetUserImgAction;
import sandjentrance.com.sj.actions.GetUserImgActionEventFailure;
import sandjentrance.com.sj.actions.GetUserImgActionEventNoFile;
import sandjentrance.com.sj.actions.GetUserImgActionEventSuccess;
import sandjentrance.com.sj.actions.GetUserImgAction_.PsGetUserImgAction;
import sandjentrance.com.sj.actions.MoveFileAction;
import sandjentrance.com.sj.actions.MoveFileActionEventFailure;
import sandjentrance.com.sj.actions.MoveFileActionEventPrime;
import sandjentrance.com.sj.actions.MoveFileActionEventSuccess;
import sandjentrance.com.sj.actions.MoveFileAction_.PsMoveFileAction;
import sandjentrance.com.sj.actions.RenameFileAction;
import sandjentrance.com.sj.actions.RenameFileActionEventFailure;
import sandjentrance.com.sj.actions.RenameFileActionEventSuccess;
import sandjentrance.com.sj.actions.UploadNewFileAction_.PsUploadNewFileAction;
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.ui.extras.FabAddFileInterface;
import sandjentrance.com.sj.ui.extras.FileClickInterface;
import sandjentrance.com.sj.ui.extras.ProjDetailAdapter;
import sandjentrance.com.sj.utils.UtilFile;
import sandjentrance.com.sj.utils.UtilImage;
import sandjentrance.com.sj.views.SpaceItemDecoration;

@EventListener(producers = {
        FindFolderChildrenAction.class,
        ClaimProjAction.class,
        MoveFileAction.class,
        RenameFileAction.class,
        ArchiveFileAction.class,
        GetUserImgAction.class,
        DbAddNewFileAction.class,
        DownloadFileAction.class
})
public class ProjDetailActivity extends BaseActivity implements FileClickInterface, FabAddFileInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String FILE_OBJ = "FILE_OBJ";
    public static final int REQ_CODE_USER_IMG = 23969;
    public static final int REQ_CODE_NEW_IMG = 23970;
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
    private String actionIdDownload;
    private FileObj fileObj;
    private ProjDetailAdapter adapter;
    private Uri imagePickerUri;
    private NewFileObj newFileObj;
    private Menu menu;
    private String actionIdFileList;
    //region PennStation----------------------
    ProjDetailActivityEventListener eventListener = new ProjDetailActivityEventListener() {
        @Override
        public void onEventMainThread(DbAddNewFileActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            NewFileObj newFileObj = event.newFileObj;

            if (!newFileObj.parentName.equals(BaseAction.PHOTOS_FOLDER_NAME)) {
                LocalFileObj localFileObj = new LocalFileObj(newFileObj.title, newFileObj.mime, newFileObj.localFilePath);
                openLocalFile(localFileObj, progress);
            }
        }

        @Override
        public void onEventMainThread(DbAddNewFileActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(ArchiveFileActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(ArchiveFileActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            archiveFileHelper.wasArhived = true;
            finish();
        }

        @Override
        public void onEventMainThread(MoveFileActionEventPrime event) {
            refreshMenu();
        }

        @Override
        public void onEventMainThread(MoveFileActionEventSuccess event) {
            refreshFileList();
            moveFolderHelper.resetState();
            refreshMenu();
        }

        @Override
        public void onEventMainThread(MoveFileActionEventFailure event) {

        }

        @Override
        public void onEventMainThread(ClaimProjActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            claimBtn.setVisibility(View.GONE);
            fileObj.claimUser = event.claimUser;
            pmNameView.setText(fileObj.claimUser);
        }

        @Override
        public void onEventMainThread(ClaimProjActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(RenameFileActionEventSuccess event) {
            refreshFileList();
        }

        @Override
        public void onEventMainThread(RenameFileActionEventFailure event) {

        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            if (event.getResponseInfo().mRequestId.equals(actionIdFileList)) {
                adapter.refreshView(Arrays.asList(event.results));
            }
        }

        @Override
        public void onEventMainThread(GetUserImgActionEventFailure event) {
        }

        @Override
        public void onEventMainThread(GetUserImgActionEventNoFile event) {

        }

        @Override
        public void onEventMainThread(GetUserImgActionEventSuccess event) {
            if (event.userName.equals(fileObj.claimUser)) {
                invalidateAndSetUserImage();
            }
        }

        @Override
        public void onEventMainThread(DownloadFileActionEventFailure event) {
            progress.setVisibility(View.GONE);
            Snackbar.make(progress, R.string.error_network, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(DownloadFileActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            if (event.getResponseInfo().mRequestId.equals(actionIdDownload)) {
                LocalFileObj localFileObj = event.localFileObj;
                if (event.ActionEnum.equals(DownloadFileAction.ActionEnum.EDIT.name())) {
                    openLocalFile(localFileObj, progress);
                } else if (event.ActionEnum.equals(DownloadFileAction.ActionEnum.SHARE.name())) {
                    shareIntentFile(localFileObj);
                }
            }
        }
    };
    //endregion
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

        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PennStation.registerListener(eventListener);

        if (fileObj.parent.equals(moveFolderHelper.initialParentId) && !moveFolderHelper.moveReady()) {
            refreshFileList();
            moveFolderHelper.resetState();
        }

        PennStation.requestAction(PsUploadNewFileAction.helper());
        refreshMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PennStation.unRegisterListener(eventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        refreshMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_paste:
                PennStation.requestAction(PsMoveFileAction.helper(fileObj.id));
                progress.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_archive:
                PennStation.requestAction(PsArchiveFileAction.helper(fileObj.id));
                progress.setVisibility(View.VISIBLE);
            case R.id.menu_claim:
                claimProject();
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
                String uriString = UtilImage.getImageUriFromIntent(data, imagePickerUri);
                startActivityForResult(UserImageCropActivity.getInstance(this, uriString), UserImageCropActivity.RESULT_CODE);

            } else if (requestCode == UserImageCropActivity.RESULT_CODE) {
                //----------------
                invalidateAndSetUserImage();
            } else if (requestCode == REQ_CODE_NEW_IMG) {
                //----------------
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
        refreshFileList();
    }

    private void initView() {
        projTitle.setText(fileObj.title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (fileObj.claimUser != null) {
            claimBtn.setVisibility(View.GONE);
            pmNameView.setText(fileObj.claimUser);

            refreshMenu();
        }

        claimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claimProject();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.proj_detail_spacing);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        adapter = new ProjDetailAdapter(this);
        recyclerView.setAdapter(adapter);

        PennStation.requestAction(PsGetUserImgAction.helper(fileObj.claimUser));
        Picasso.with(this).load(UtilImage.getAvatarFile(this, fileObj.claimUser)).placeholder(R.drawable.profile_image).into(profileImg);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileObj.claimUser.equals(prefs.getUser())) {
                    File externalFile = UtilImage.getTempFile(ProjDetailActivity.this);
                    choosePicture(REQ_CODE_USER_IMG, externalFile);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddFile.getInstance(fileObj).show(getSupportFragmentManager(), null);
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

            if (fileObj.claimUser != null) {
                menu.findItem(R.id.menu_claim).setVisible(true);
            }
        }
    }

    private void refreshFileList() {
        progress.setVisibility(View.VISIBLE);
        actionIdFileList = PennStation.requestAction(PsFindFolderChildrenAction.helper("", fileObj.id, false));
    }

    private void invalidateAndSetUserImage() {
        Picasso.with(this).invalidate(UtilImage.getAvatarFile(this, fileObj.claimUser));
        Picasso.with(this).load(UtilImage.getAvatarFile(this, fileObj.claimUser))
                .placeholder(R.drawable.profile_image)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(profileImg);
    }
    //endregion

    //region Helper----------------------
    private void claimProject() {
        PennStation.requestAction(PsClaimProjAction.helper(fileObj.id));
        progress.setVisibility(View.VISIBLE);
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
        PennStation.postLocalEvent(new MoveFileActionEventPrime());
    }

    @Override
    public void editClicked(FileObj fileObj) {
        progress.setVisibility(View.VISIBLE);
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, DownloadFileAction.ActionEnum.EDIT.name()));
    }

    @Override
    public void shareClicked(FileObj fileObj) {
        progress.setVisibility(View.VISIBLE);
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, DownloadFileAction.ActionEnum.SHARE.name()));
    }

    @Override
    public void printClicked(FileObj fileObj) {
        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
//        progress.setVisibility(View.VISIBLE);
//        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
//        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, ActionEnum.PRINT.name()));
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