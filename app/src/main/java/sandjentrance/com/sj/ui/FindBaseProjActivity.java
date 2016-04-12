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
import sandjentrance.com.sj.actions.FindBaseFolderActionEventFailure;
import sandjentrance.com.sj.actions.FindBaseFolderActionEventSuccess;
import sandjentrance.com.sj.actions.FindBaseFolderAction_.PsFindBaseFolderAction;
import sandjentrance.com.sj.actions.SetupDriveAction;
import sandjentrance.com.sj.actions.SetupDriveActionEventFailure;
import sandjentrance.com.sj.actions.SetupDriveActionEventSuccess;
import sandjentrance.com.sj.actions.SetupDriveAction_.PsSetupDriveAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.BaseProjListAdapter;
import sandjentrance.com.sj.ui.extras.FileListInterface;

@EventListener(producers = {
        FindBaseFolderAction.class,
        SetupDriveAction.class
})
public class FindBaseProjActivity extends BaseActivity implements FileListInterface {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field
    private BaseProjListAdapter adapter;

    //region PennStation----------------------
    FindBaseProjActivityEventListener eventListener = new FindBaseProjActivityEventListener() {
        @Override
        public void onEventMainThread(FindBaseFolderActionEventFailure event) {
            progress.setVisibility(View.GONE);
            Snackbar.make(progress, R.string.error_network, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(FindBaseFolderActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            adapter.refreshView(Arrays.asList(event.results));
        }

        @Override
        public void onEventMainThread(SetupDriveActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            startProjListActivity();
        }

        @Override
        public void onEventMainThread(SetupDriveActionEventFailure event) {
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
        setContentView(R.layout.search_proj_activity);
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

    //region Interface----------------------
    @Override
    public void fileClicked(FileObj fileObj) {
        PennStation.requestAction(PsSetupDriveAction.helper(fileObj.id));
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void fileLongClicked(FileObj fileObj) {
        //should remain empty!!
    }

    private void startProjListActivity() {
        startActivity(ProjListActivity.getInstance(this));
        finish();
    }
    //endregion

}