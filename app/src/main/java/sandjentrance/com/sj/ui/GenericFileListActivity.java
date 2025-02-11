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

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.ArchiveFileAction;
import sandjentrance.com.sj.actions.ArchiveFileAction_.PsArchiveFileAction;
import sandjentrance.com.sj.actions.DeleteFileAction;
import sandjentrance.com.sj.actions.DeleteFileAction_.PsDeleteFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction;
import sandjentrance.com.sj.actions.DownloadFileAction.ActionEnum;
import sandjentrance.com.sj.actions.DownloadFileAction_.PsDownloadFileAction;
import sandjentrance.com.sj.actions.DownloadMultiFileAction;
import sandjentrance.com.sj.actions.DownloadMultiFileAction_.PsDownloadMultiFileAction;
import sandjentrance.com.sj.actions.DuplicateFileAction;
import sandjentrance.com.sj.actions.DuplicateFileAction_.PsDuplicateFileAction;
import sandjentrance.com.sj.actions.DwgConversionAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.MergePdfAction;
import sandjentrance.com.sj.actions.MergePdfAction_.PsMergePdfAction;
import sandjentrance.com.sj.actions.MoveFileAction;
import sandjentrance.com.sj.actions.MoveFileAction_.PsMoveFileAction;
import sandjentrance.com.sj.actions.RenameFileAction;
import sandjentrance.com.sj.actions.events.ArchiveFileActionFailure;
import sandjentrance.com.sj.actions.events.ArchiveFileActionSuccess;
import sandjentrance.com.sj.actions.events.DeleteFileActionFailure;
import sandjentrance.com.sj.actions.events.DeleteFileActionSuccess;
import sandjentrance.com.sj.actions.events.DownloadFileActionDwgConversion;
import sandjentrance.com.sj.actions.events.DownloadFileActionFailure;
import sandjentrance.com.sj.actions.events.DownloadFileActionSuccess;
import sandjentrance.com.sj.actions.events.DownloadMultiFileActionFailure;
import sandjentrance.com.sj.actions.events.DownloadMultiFileActionSuccess;
import sandjentrance.com.sj.actions.events.DuplicateFileActionFailure;
import sandjentrance.com.sj.actions.events.DuplicateFileActionSuccess;
import sandjentrance.com.sj.actions.events.DwgConversionActionFailure;
import sandjentrance.com.sj.actions.events.DwgConversionActionSuccess;
import sandjentrance.com.sj.actions.events.FindFolderChildrenActionFailure;
import sandjentrance.com.sj.actions.events.FindFolderChildrenActionSuccess;
import sandjentrance.com.sj.actions.events.MergePdfActionFailure;
import sandjentrance.com.sj.actions.events.MergePdfActionSuccess;
import sandjentrance.com.sj.actions.events.MoveFileActionFailure;
import sandjentrance.com.sj.actions.events.MoveFileActionPrime;
import sandjentrance.com.sj.actions.events.MoveFileActionSuccess;
import sandjentrance.com.sj.actions.events.RenameFileActionFailure;
import sandjentrance.com.sj.actions.events.RenameFileActionSuccess;
import sandjentrance.com.sj.models.FileDownloadObj;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.ui.extras.FileClickInterface;
import sandjentrance.com.sj.ui.extras.GenericListAdapter;
import sandjentrance.com.sj.ui.extras.ShareInterface;
import sandjentrance.com.sj.ui.viewstate.GenericFileListVS;


@EventListener(producers = {
        FindFolderChildrenAction.class,
        MoveFileAction.class,
        RenameFileAction.class,
        ArchiveFileAction.class,
        DownloadFileAction.class,
        DwgConversionAction.class,
        MergePdfAction.class,
        DeleteFileAction.class,
        DuplicateFileAction.class,
        DownloadMultiFileAction.class
})
public class GenericFileListActivity extends BaseActivity implements FileClickInterface, ShareInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String FILE_OBJ = "FILE_OBJ";
    public static final String saveStateviewState = "saveStateviewState";
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
    private String actionIdDelete;
    private String actionIdDuplicate;
    private String actionIdDownload;
    private String actionIdFileList;
    private Snackbar snackbar;
    //~=~=~=~=~=~=~=~=~=~=~=~=View State
    private GenericFileListVS viewState = new GenericFileListVS();

    //region PennStation----------------------
    GenericFileListActivityEventListener eventListener = new GenericFileListActivityEventListener() {
        @Override
        public void onEventMainThread(ArchiveFileActionFailure event) {
            viewState.isProgressVisible = false;
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
        public void onEventMainThread(ArchiveFileActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
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
            if (event.getResponseInfo().mRequestId.equals(actionIdFileList)) {
                viewState.stateListData = new ArrayList<>(Arrays.asList(event.results));
                adapter.refreshView(viewState.stateListData);
            }
        }

        @Override
        public void onEventMainThread(MoveFileActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
        }

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
                if (event.ActionEnum.equals(ActionEnum.EDIT.name())) {
                    openLocalFile(localFileObj, progress);
                } else if (event.ActionEnum.equals(ActionEnum.SHARE.name())) {
                    shareIntentFile(localFileObj);
                } else if (event.ActionEnum.equals(ActionEnum.PRINT.name())) {
                    printIntentFile(localFileObj);
                }
            }
        }

        @Override
        public void onEventMainThread(DownloadFileActionDwgConversion event) {
            viewState.isProgressVisible = false;
            updateView();
            Snackbar.make(progress, R.string.zamzar_started, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(DeleteFileActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            if (event.getResponseInfo().mRequestId.equals(actionIdDelete)) {
                refreshFileList();
            }
        }

        @Override
        public void onEventMainThread(DeleteFileActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
            Snackbar.make(progress, R.string.error_network, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(DwgConversionActionSuccess event) {
            Snackbar.make(progress, R.string.zamzar_success, Snackbar.LENGTH_SHORT).show();
            if (event.parentId.equals(fileObj.id)) {
                refreshFileList();
            }
        }

        @Override
        public void onEventMainThread(DuplicateFileActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            if (event.getResponseInfo().mRequestId.equals(actionIdDuplicate)) {
                refreshFileList();
            }
        }

        @Override
        public void onEventMainThread(DuplicateFileActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(DwgConversionActionFailure event) {
            Snackbar.make(progress, R.string.zamzar_failed, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(MoveFileActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            refreshMenu();
            refreshFileList();
        }

        @Override
        public void onEventMainThread(DownloadMultiFileActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
            Snackbar.make(progress, R.string.error_network, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(DownloadMultiFileActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            multiShareHelper.shareMultiple.clear();
            adapter.notifyDataSetChanged();
            refreshMenu();
            shareIntentMultiFile(event.localFileObj);
        }

        @Override
        public void onEventMainThread(MoveFileActionPrime event) {
            refreshMenu();
        }

        @Override
        public void onEventMainThread(MergePdfActionSuccess event) {
            viewState.isProgressVisible = false;
            updateView();
            openLocalFile(event.localFileObj, progress);
        }

        @Override
        public void onEventMainThread(MergePdfActionFailure event) {
            viewState.isProgressVisible = false;
            updateView();
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

        if (savedInstanceState == null) {
            initData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();

        PennStation.registerListener(eventListener);

        if (renameFileHelper.isValid() && fileObj.id.equals(renameFileHelper.parentId)) {
            renameFileHelper.parentId = null;
            refreshFileList();
        }

        if (fileObj.id.equals(moveFolderHelper.initialParentId) && !moveFolderHelper.moveReady()) {
            refreshFileList();
            moveFolderHelper.resetState();
        }

        if (mergePfdHelper.isMerging) {
            viewState.snackbarState = GenericFileListVS.SnackbarState.PDF_MERGE;
            updateSnackbar();
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
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        this.menu = menu;
        refreshMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_paste:
                PennStation.requestAction(PsMoveFileAction.helper(fileObj.id));
                viewState.isProgressVisible = true;
                updateView();
                return true;
            case R.id.menu_cancel_share:
                multiShareHelper.shareMultiple.clear();
                adapter.notifyDataSetChanged();
                refreshMenu();
                return true;
            case R.id.menu_multi_share:
                FileDownloadObj[] fileDlArr = new FileDownloadObj[multiShareHelper.shareMultiple.size()];
                int i = 0;
                for (FileObj f : multiShareHelper.shareMultiple) {
                    fileDlArr[i] = new FileDownloadObj(f.parent, f.id, f.title, f.mime);
                    i++;
                }

                PennStation.requestAction(PsDownloadMultiFileAction.helper(fileDlArr));
                viewState.isProgressVisible = true;
                updateView();
                return true;
            case R.id.menu_archive:
                PennStation.requestAction(PsArchiveFileAction.helper(fileObj.id));
                viewState.isProgressVisible = true;
                updateView();
                return true;
            default:
                // If we got here, the user's ActionEnum was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(saveStateviewState, viewState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        viewState = savedInstanceState.getParcelable(saveStateviewState);
    }

    //endregion

    //region Init----------------------
    private void initData() {
        refreshFileList();
    }

    private void initView() {
        toolbar.setTitle(fileObj.title);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new GenericListAdapter(this, multiShareHelper.shareMultiple);
        recyclerView.setAdapter(adapter);

        if (viewState.stateListData != null && !viewState.stateListData.isEmpty()) {
            adapter.refreshView(viewState.stateListData);
        }

        updateView();
        updateSnackbar();
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
            if (multiShareHelper.shareMultiple.size() > 0) {
                menu.findItem(R.id.menu_cancel_share).setVisible(true);
                menu.findItem(R.id.menu_multi_share).setVisible(true);
            } else {
                menu.findItem(R.id.menu_cancel_share).setVisible(false);
                menu.findItem(R.id.menu_multi_share).setVisible(false);
            }
        }
    }

    private void refreshFileList() {
        viewState.isProgressVisible = true;
        updateView();
        actionIdFileList = PennStation.requestAction(PsFindFolderChildrenAction.helper("", fileObj.id, false));
    }
    //endregion

    //region Helper----------------------

    private void updateView() {
        if (viewState.isProgressVisible) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    private void updateSnackbar() {
        if (viewState.snackbarState != null) {
                switch (viewState.snackbarState) {
                    case None:
                        dismissSnackbar();
                        break;
                    case PDF_MERGE:
                        snackbar = Snackbar.make(progress, R.string.choose_pdf_to_merge, Snackbar.LENGTH_INDEFINITE).setAction("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mergePfdHelper.isMerging = false;
                                dismissSnackbar();
                            }
                        });
                        snackbar.show();
                        break;
                    default:
                        dismissSnackbar();
                        break;
                }
            }
        }

    private void dismissSnackbar() {
        viewState.snackbarState = GenericFileListVS.SnackbarState.None;
        if(snackbar != null) {
            snackbar.dismiss();
        }
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
        progress.setVisibility(View.VISIBLE);
        actionIdDelete = PennStation.requestAction(PsDeleteFileAction.helper(fileObj.id));
    }

    @Override
    public void shareClicked(FileObj fileObj) {
        dialogShareClicked(fileObj);
    }

    @Override
    public void multiShareClicked(FileObj fileObj) {
        multiShareHelper.shareMultiple.add(fileObj);
        adapter.notifyDataSetChanged();
        refreshMenu();
    }

    @Override
    public void printClicked(FileObj fileObj) {
        progress.setVisibility(View.VISIBLE);
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, ActionEnum.PRINT.name()));
    }

    @Override
    public void duplicateClicked(FileObj fileObj) {
        progress.setVisibility(View.VISIBLE);
        actionIdDuplicate = PennStation.requestAction(PsDuplicateFileAction.helper(fileObj.id, fileObj.title));
    }

    @Override
    public void editClicked(FileObj fileObj) {
        if (multiShareHelper.shareMultiple.size() > 0) {
            if (multiShareHelper.shareMultiple.contains(fileObj)) {
                multiShareHelper.shareMultiple.remove(fileObj);
            } else {
                multiShareHelper.shareMultiple.add(fileObj);
            }
            adapter.notifyDataSetChanged();
            refreshMenu();
            return;
        }
        progress.setVisibility(View.VISIBLE);
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, ActionEnum.EDIT.name()));
    }

    @Override
    public void doMerge() {
        progress.setVisibility(View.VISIBLE);
        PennStation.requestAction(PsMergePdfAction.helper(), longTaskQueue);
        mergePfdHelper.isMerging = false;
        dismissSnackbar();
    }

    @Override
    public void dialogShareClicked(FileObj fileObj) {
        progress.setVisibility(View.VISIBLE);
        FileDownloadObj fileDownloadObj = new FileDownloadObj(fileObj.parent, fileObj.id, fileObj.title, fileObj.mime);
        actionIdDownload = PennStation.requestAction(PsDownloadFileAction.helper(fileDownloadObj, ActionEnum.SHARE.name()));
    }
    //endregion

}