package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;


import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventFailure;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventSuccess;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.MoveFileAction;
import sandjentrance.com.sj.actions.MoveFileActionEventFailure;
import sandjentrance.com.sj.actions.MoveFileActionEventPrime;
import sandjentrance.com.sj.actions.MoveFileActionEventSuccess;
import sandjentrance.com.sj.actions.RenameFileAction;
import sandjentrance.com.sj.actions.RenameFileActionEventFailure;
import sandjentrance.com.sj.actions.RenameFileActionEventSuccess;
import sandjentrance.com.sj.actions.UnArchiveFileAction;
import sandjentrance.com.sj.actions.UnArchiveFileActionEventFailure;
import sandjentrance.com.sj.actions.UnArchiveFileActionEventSuccess;
import sandjentrance.com.sj.actions.UnArchiveFileAction_.PsUnArchiveFileAction;
import sandjentrance.com.sj.models.FileObj;
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
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private FileObj fileObj;
    private FileArchiveListAdapter adapter;

    private Menu menu;
    private String actionIdFileList;
    //region PennStation----------------------
    ArchiveFileListActivityEventListener eventListener = new ArchiveFileListActivityEventListener() {
        @Override
        public void onEventMainThread(UnArchiveFileActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            refreshFileList();
        }

        @Override
        public void onEventMainThread(UnArchiveFileActionEventFailure event) {
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
        public void onEventMainThread(MoveFileActionEventPrime event) {

        }

        @Override
        public void onEventMainThread(MoveFileActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(MoveFileActionEventSuccess event) {
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
//        refreshMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PennStation.unRegisterListener(eventListener);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        refreshFileList();
    }

    private void initView() {
        initBg();

        toolbar.setTitle(fileObj.title);
        setSupportActionBar(toolbar);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FileArchiveListAdapter(this);
        recyclerView.setAdapter(adapter);
    }
    //endregion

    //region View----------------------
//    private void refreshMenu() {
//        if (menu != null) {
//            if (moveFolderHelper.moveReady()) {
//                menu.findItem(R.id.menu_paste).setVisible(true);
//            } else {
//                menu.findItem(R.id.menu_paste).setVisible(false);
//            }
//        }
//    }

    private void refreshFileList() {
        progress.setVisibility(View.VISIBLE);
        actionIdFileList = PennStation.requestAction(PsFindFolderChildrenAction.helper("", fileObj.id, false));
    }

    //endregion


    //region Interface----------------------
    @Override
    public void fileClicked(FileObj fileObj) {
        startActivity(ArchiveFileListActivity.getInstance(this, fileObj));
    }

    @Override
    public void fileLongClicked(FileObj fileObj) {
//        DialogChooseFileAction.getInstance(fileObj).show(getSupportFragmentManager(), null);
    }

    @Override
    public void unarchiveFile(FileObj item) {
        PennStation.requestAction(PsUnArchiveFileAction.helper(item.id));
    }
    //endregion

}