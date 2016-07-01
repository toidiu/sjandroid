package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.MoveFileAction;
import sandjentrance.com.sj.actions.RenameFileAction;
import sandjentrance.com.sj.actions.UnArchiveFileAction;
import sandjentrance.com.sj.actions.UnArchiveFileAction_.PsUnArchiveFileAction;
import sandjentrance.com.sj.actions.events.FindFolderChildrenActionFailure;
import sandjentrance.com.sj.actions.events.FindFolderChildrenActionSuccess;
import sandjentrance.com.sj.actions.events.MoveFileActionFailure;
import sandjentrance.com.sj.actions.events.MoveFileActionPrime;
import sandjentrance.com.sj.actions.events.MoveFileActionSuccess;
import sandjentrance.com.sj.actions.events.RenameFileActionFailure;
import sandjentrance.com.sj.actions.events.RenameFileActionSuccess;
import sandjentrance.com.sj.actions.events.UnArchiveFileActionFailure;
import sandjentrance.com.sj.actions.events.UnArchiveFileActionSuccess;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.DelayedTextWatcher;
import sandjentrance.com.sj.ui.extras.FileArchiveListAdapter;
import sandjentrance.com.sj.ui.extras.FileArchiveListInterface;

@EventListener(producers = {
        FindFolderChildrenAction.class,
        MoveFileAction.class,
        RenameFileAction.class,
        UnArchiveFileAction.class
})
public class ArchiveFileListActivity extends BaseActivity implements FileArchiveListInterface {

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
    @Bind(R.id.search)
    EditText searchView;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private FileObj fileObj;
    private FileArchiveListAdapter adapter;
    private Snackbar snackbar;
    private Menu menu;
    private String actionIdFileList;
    //region PennStation----------------------
    ArchiveFileListActivityEventListener eventListener = new ArchiveFileListActivityEventListener() {
        @Override
        public void onEventMainThread(UnArchiveFileActionSuccess event) {
            progress.setVisibility(View.GONE);
            refreshFileList();
        }

        @Override
        public void onEventMainThread(UnArchiveFileActionFailure event) {
            progress.setVisibility(View.GONE);
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
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionSuccess event) {
            progress.setVisibility(View.GONE);
            if (event.getResponseInfo().mRequestId.equals(actionIdFileList)) {
                adapter.refreshView(Arrays.asList(event.results));
            }
        }

        @Override
        public void onEventMainThread(MoveFileActionPrime event) {

        }

        @Override
        public void onEventMainThread(MoveFileActionFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(MoveFileActionSuccess event) {
            progress.setVisibility(View.GONE);
//            refreshMenu();
            refreshFileList();
        }
    };
    //endregion
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context, FileObj fileObj) {
        Intent intent = new Intent(context, ArchiveFileListActivity.class);
        intent.putExtra(FILE_OBJ, fileObj);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive_activity);
        ButterKnife.bind(this);

        fileObj = getIntent().getParcelableExtra(FILE_OBJ);

        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PennStation.registerListener(eventListener);
//        refreshMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PennStation.unRegisterListener(eventListener);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_detail, menu);
//        this.menu = menu;
//        refreshMenu();
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_paste:
//                PennStation.requestAction(PsMoveFileAction.helper(fileObj.id));
//                progress.setVisibility(View.VISIBLE);
//                return true;
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//        }
//    }

    //endregion

    //region Init----------------------
    private void initData() {
    }

    private void initView() {
        initBg();
        toolbar.setTitle(fileObj.title);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FileArchiveListAdapter(this);
        recyclerView.setAdapter(adapter);


        DelayedTextWatcher.OnTextChanged projSearchTextChanged = new DelayedTextWatcher.OnTextChanged() {
            @Override
            public void onTextChanged(String text) {
                refreshFileList();
            }
        };
        DelayedTextWatcher.addTo(searchView, projSearchTextChanged, 500);

    }
    //endregion

    //region View----------------------
    private void refreshFileList() {
        String searchName = searchView.getText().toString();
        if (searchName.isEmpty()) {
            adapter.refreshView(new ArrayList<FileObj>());
        } else if (searchName.length() < 3) {
            snackbar = Snackbar.make(recyclerView, R.string.search_archive_hint, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
            }
            progress.setVisibility(View.VISIBLE);
            actionIdFileList = PennStation.requestAction(PsFindFolderChildrenAction.helper(searchName, fileObj.id, false));
        }

    }

    //endregion


    //region Interface----------------------
    @Override
    public void unarchiveFile(FileObj item) {
        PennStation.requestAction(PsUnArchiveFileAction.helper(item.id));
    }
    //endregion

}