package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
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
import sandjentrance.com.sj.ui.extras.AddFileInterface;
import sandjentrance.com.sj.ui.extras.DelayedTextWatcher;
import sandjentrance.com.sj.ui.extras.ProjClickInterface;
import sandjentrance.com.sj.ui.extras.ProjListAdapter;

@EventListener(producers = {
        FindFolderChildrenAction.class,
        DbFindClaimedProjListAction.class,
        FindClaimedProjAction.class,
        DbAddNewFileAction.class
})
public class ProjListActivity extends BaseActivity implements ProjClickInterface, AddFileInterface {

    //region Fields----------------------
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
            LocalFileObj localFileObj = new LocalFileObj(newFileObj.title, newFileObj.mime, newFileObj.localFilePath);
            openLocalFile(localFileObj, null);
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
        setContentView(R.layout.search_proj_activity);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        PennStation.unRegisterListener(eventListener);
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

        searchView.setVisibility(View.VISIBLE);
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
        // FIXME: 4/18/16   put all this into the pennstation task!!!   also run task in project detail
        // FIXME: 4/18/16 also call the uploadNewFileAction more often..... maybe in onResume;

        PennStation.requestAction(PsDbAddNewFileAction.helper(newFileObj));
    }
    //endregion

}