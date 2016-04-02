package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.BuildConfig;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.SearchProjectsAction;
import sandjentrance.com.sj.actions.SearchProjectsActionEventFailure;
import sandjentrance.com.sj.actions.SearchProjectsActionEventSuccess;
import sandjentrance.com.sj.actions.SearchProjectsAction_.PsSearchProjectsAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.ui.extras.DelayedTextWatcher;
import sandjentrance.com.sj.ui.extras.SearchProjAdapter;

@EventListener(producers = {
        SearchProjectsAction.class
})
public class SearchProjectsActivity extends BaseActivity implements SearchProjAdapter.ProjListInterface {

    //region Fields----------------------
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
    private SearchProjAdapter adapter;

    //region PennStation----------------------
    SearchProjectsActivityEventListener eventListener = new SearchProjectsActivityEventListener() {
        @Override
        public void onEventMainThread(SearchProjectsActionEventFailure event) {
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onEventMainThread(SearchProjectsActionEventSuccess event) {
            progress.setVisibility(View.GONE);
            adapter.refreshView(Arrays.asList(event.results));
        }
    };
    //endregion
    private Snackbar snackbar;
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context) {
        return new Intent(context, SearchProjectsActivity.class);
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
        if (BuildConfig.DEBUG) {
            PennStation.requestAction(PsSearchProjectsAction.helper("Ralph"));
            progress.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        toolbar.setTitle("Project List");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SearchProjAdapter(this);
        recyclerView.setAdapter(adapter);

        DelayedTextWatcher.OnTextChanged projSearchTextChanged = new DelayedTextWatcher.OnTextChanged() {
            @Override
            public void onTextChanged(String text) {
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
                    PennStation.requestAction(PsSearchProjectsAction.helper(searchName));
                    progress.setVisibility(View.VISIBLE);
                }
            }
        };
        DelayedTextWatcher.addTo(searchView, projSearchTextChanged, 300);
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
    public void projClicked(FileObj fileObj) {
        Snackbar.make(recyclerView, fileObj.id, Snackbar.LENGTH_SHORT).show();
        startActivity(ProjectActivity.getInstance(this));
    }
    //endregion

}