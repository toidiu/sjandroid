package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import org.parceler.Parcels;

import java.io.File;
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
import sandjentrance.com.sj.actions.ClaimProjAction;
import sandjentrance.com.sj.actions.ClaimProjActionEventFailure;
import sandjentrance.com.sj.actions.ClaimProjActionEventSuccess;
import sandjentrance.com.sj.actions.ClaimProjAction_.PsClaimProjAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventFailure;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventSuccess;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.MoveFileAction;
import sandjentrance.com.sj.actions.MoveFileActionEventFailure;
import sandjentrance.com.sj.actions.MoveFileActionEventPrime;
import sandjentrance.com.sj.actions.MoveFileActionEventSuccess;
import sandjentrance.com.sj.actions.MoveFileAction_.PsMoveFileAction;
import sandjentrance.com.sj.actions.RenameFileAction;
import sandjentrance.com.sj.actions.RenameFileActionEventFailure;
import sandjentrance.com.sj.actions.RenameFileActionEventSuccess;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.FileListAdapter;
import sandjentrance.com.sj.ui.extras.FileListInterface;
import sandjentrance.com.sj.utils.ImageUtil;

@EventListener(producers = {
        FindFolderChildrenAction.class,
        ClaimProjAction.class,
        MoveFileAction.class,
        RenameFileAction.class,
        ArchiveFileAction.class
})
public class ProjDetailActivity extends BaseActivity implements FileListInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String FILE_OBJ = "FILE_OBJ";
    public static final int SELECT_PICTURE = 23969;
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.pm_name)
    TextView pmNameView;
    @Bind(R.id.cliam_btn)
    Button claimBtn;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.profile_img)
    CircleImageView profileImg;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private FileObj fileObj;
    private FileListAdapter adapter;
    private Menu menu;
    private String actionIdFileList;
    //region PennStation----------------------
    ProjDetailActivityEventListener eventListener = new ProjDetailActivityEventListener() {
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

    };
    private Uri imagePickerUri;
    //endregion
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context, FileObj fileObj) {
        Intent intent = new Intent(context, ProjDetailActivity.class);
        intent.putExtra(FILE_OBJ, Parcels.wrap(fileObj));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proj_detail_activity);
        ButterKnife.bind(this);

        fileObj = Parcels.unwrap(getIntent().getParcelableExtra(FILE_OBJ));

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
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                final boolean isCamera;
                if (data.getData() == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = imagePickerUri;
                } else {
                    selectedImageUri = data.getData();
                }
                if (BuildConfig.DEBUG) {
                    Log.d("drawer_text-----------", selectedImageUri.toString());
                }

                startActivityForResult(ArtistImageCropActivity
                                .getInstance(this, selectedImageUri.toString()),
                        ArtistImageCropActivity.RESULT_CODE);
            } else if (requestCode == ArtistImageCropActivity.RESULT_CODE) {
                //// FIXME: 4/12/16 set image for user
//                if (imageUrl != null) {
//                    Picasso.with(this).invalidate(Uri.parse(imageUrl));
//                }
//                Picasso.with(this).invalidate(ImageUtil.getAvatarFile(this));
//
//                Picasso.with(this).load(ImageUtil.getAvatarFile(this)).placeholder(R.drawable.ic_profile)
//                        .networkPolicy(NetworkPolicy.NO_CACHE)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(userImg);
//                Picasso.with(this).load(ImageUtil.getAvatarFile(this)).placeholder(R.drawable.ic_profile)
//                        .networkPolicy(NetworkPolicy.NO_CACHE)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(profileBanner);
//
            }
        }
    }
    //endregion

    //region Init----------------------
    private void initData() {
        refreshFileList();
    }

    private void initView() {
        final View layout = findViewById(R.id.layout);
        initBg(layout);

        toolbar.setTitle(fileObj.title);
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FileListAdapter(this);
        recyclerView.setAdapter(adapter);

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
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

    //endregion

    //region Helper----------------------
    private void claimProject() {
        PennStation.requestAction(PsClaimProjAction.helper(fileObj.id));
        progress.setVisibility(View.VISIBLE);
    }

    public void choosePicture() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalFile = ImageUtil.getTempFile(this);

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(externalFile));
        imagePickerUri = Uri.parse(externalFile.getAbsolutePath());
        if (BuildConfig.DEBUG) {
            Log.d("photo location", imagePickerUri.toString());
        }

        String pickTitle = getString(R.string.select_picture);
        Intent chooserIntent = Intent.createChooser(takePhotoIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }
    //endregion


    //region Interface----------------------
    @Override
    public void fileClicked(FileObj fileObj) {
        startActivity(GenericFileListActivity.getInstance(this, fileObj));
    }

    @Override
    public void fileLongClicked(FileObj fileObj) {
        DialogChooseFileAction.getInstance(fileObj).show(getSupportFragmentManager(), null);
    }
    //endregion

}