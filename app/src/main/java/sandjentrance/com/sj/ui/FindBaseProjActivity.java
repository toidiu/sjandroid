package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.FindBaseFolderAction;
import sandjentrance.com.sj.actions.FindBaseFolderAction_.PsFindBaseFolderAction;
import sandjentrance.com.sj.actions.SetupDriveAction;
import sandjentrance.com.sj.actions.SetupDriveAction_.PsSetupDriveAction;
import sandjentrance.com.sj.actions.events.FindBaseFolderActionFailure;
import sandjentrance.com.sj.actions.events.FindBaseFolderActionSuccess;
import sandjentrance.com.sj.actions.events.SetupDriveActionFailure;
import sandjentrance.com.sj.actions.events.SetupDriveActionSuccess;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.BaseProjClickInterface;
import sandjentrance.com.sj.ui.extras.BaseProjListAdapter;

@EventListener(producers = {
        FindBaseFolderAction.class,
        SetupDriveAction.class
})
public class FindBaseProjActivity extends BaseActivity implements BaseProjClickInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private BaseProjListAdapter adapter;

    //region PennStation----------------------
    FindBaseProjActivityEventListener eventListener = new FindBaseProjActivityEventListener() {
        @Override
        public void onEventMainThread(FindBaseFolderActionFailure event) {
            progress.setVisibility(View.GONE);
            Snackbar.make(progress, R.string.error_network, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(FindBaseFolderActionSuccess event) {
            progress.setVisibility(View.GONE);
            adapter.refreshView(Arrays.asList(event.results));
        }

        @Override
        public void onEventMainThread(SetupDriveActionSuccess event) {
            progress.setVisibility(View.GONE);
            startProjListActivity();
        }

        @Override
        public void onEventMainThread(SetupDriveActionFailure event) {
            progress.setVisibility(View.GONE);
            Snackbar.make(progress, R.string.error_network, Snackbar.LENGTH_SHORT).show();
        }
    };
    //endregion
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context) {
        return new Intent(context, FindBaseProjActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_activity);

//        setContentView(R.layout.proj_list_activity);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    //region Init----------------------
    private void initData() {
        if (prefs.getBaseFolderId() != null) {
            startProjListActivity();
        } else {
            PennStation.requestAction(PsFindBaseFolderAction.helper("Jobs"));
            progress.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        initBg();
        toolbar.setTitle("Select Base Folder");
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BaseProjListAdapter(this);
        recyclerView.setAdapter(adapter);
    }
    //endregion

    @Override
    protected void onResume() {
        super.onResume();
        PennStation.registerListener(eventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PennStation.unRegisterListener(eventListener);
    }
    //endregion

    //region Helper----------------------
    private void startProjListActivity() {
        startActivity(ProjListActivity.getInstance(this));
        finish();
    }
    //endregion

    //region Interface----------------------
    @Override
    public void folderClicked(FileObj fileObj) {
        PennStation.requestAction(PsSetupDriveAction.helper(fileObj.id));
        progress.setVisibility(View.VISIBLE);
    }
    //endregion

}