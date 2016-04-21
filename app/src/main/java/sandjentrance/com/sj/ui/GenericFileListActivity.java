package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.ArchiveFileAction;
import sandjentrance.com.sj.actions.ArchiveFileActionEventFailure;
import sandjentrance.com.sj.actions.ArchiveFileActionEventSuccess;
import sandjentrance.com.sj.actions.ArchiveFileAction_.PsArchiveFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction.ActionEnum;
import sandjentrance.com.sj.actions.DownloadFileActionEventFailure;
import sandjentrance.com.sj.actions.DownloadFileActionEventSuccess;
import sandjentrance.com.sj.actions.DownloadFileAction_.PsDownloadFileAction;
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
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.ui.extras.FileClickInterface;
import sandjentrance.com.sj.ui.extras.GenericListAdapter;

@EventListener(producers = {
        FindFolderChildrenAction.class,
        MoveFileAction.class,
        RenameFileAction.class,
        ArchiveFileAction.class,
        DownloadFileAction.class
})
public class GenericFileListActivity extends BaseActivity implements FileClickInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String FILE_OBJ = "FILE_OBJ";
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private FileObj fileObj;
    private GenericListAdapter adapter;
    private Menu menu;
    private String actionIdDownload;
    private String actionIdFileList;
    //region PennStation----------------------
    GenericFileListActivityEventListener eventListener = new GenericFileListActivityEventListener() {
        @Override
        public void onEventMainThread(ArchiveFileActionEventFailure event) {
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
        public void onEventMainThread(ArchiveFileActionEventSuccess event) {
            progress.setVisibility(View.GONE);
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
        public void onEventMainThread(MoveFileActionEventFailure event) {
            progress.setVisibility(View.GONE);
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
                if (event.ActionEnum.equals(ActionEnum.EDIT.name())) {
                    openLocalFile(localFileObj, progress);
                }else if(event.ActionEnum.equals(ActionEnum.SHARE.name())){
                    shareIntentFile(localFileObj);
                }
            }
        }

        @Override
        public void onEventMainThread(MoveFileActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            refreshMenu();
            refreshFileList();
        }

        @Override
        public void onEventMainThread(MoveFileActionEventPrime event) {
            refreshMenu();
        }
    };
    //endregion
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context, FileObj fileObj) {
        Intent intent = new Intent(context, GenericFileListActivity.class);
        intent.putExtra(FILE_OBJ, fileObj);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_activity);
        ButterKnife.bind(this);

        fileObj = getIntent().getParcelableExtra(FILE_OBJ);

        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PennStation.registerListener(eventListener);

        if (renameFileHelper.isValid() && fileObj.id.equals(renameFileHelper.parentId)) {
            renameFileHelper.parentId = null;
            refreshFileList();
        }

        if (fileObj.id.equals(moveFolderHelper.initialParentId) && !moveFolderHelper.moveReady()) {
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
                return true;
            default:
                // If we got here, the user's ActionEnum was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //region Init----------------------
    private void initData() {
        refreshFileList();
    }

    //endregion

    private void initView() {
        toolbar.setTitle(fileObj.title);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new GenericListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    //region View----------------------
    private void refreshMenu() {
        if (menu != null) {
            if (moveFolderHelper.moveReady()) {
                menu.findItem(R.id.menu_paste).setVisible(true);
            } else {
                menu.findItem(R.id.menu_paste).setVisible(false);
            }
        }
    }

    //endregion

    private void refreshFileList() {
        progress.setVisibility(View.VISIBLE);
        actionIdFileList = PennStation.requestAction(PsFindFolderChildrenAction.helper("", fileObj.id, false));
    }

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
    public void shareClicked(FileObj fileObj) {
        progress.setVisibility(View.VISIBLE);
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, ActionEnum.SHARE.name()));
    }

    @Override
    public void printClicked(FileObj fileObj) {
        progress.setVisibility(View.VISIBLE);
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, ActionEnum.PRINT.name()));
    }

    @Override
    public void editClicked(FileObj fileObj) {
        progress.setVisibility(View.VISIBLE);
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, ActionEnum.EDIT.name()));
    }
    //endregion

}