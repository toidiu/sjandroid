package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.TestAction;
import sandjentrance.com.sj.actions.TestActionEventSuccess;

@EventListener(producers = {
        TestAction.class
})
public class ProjectActivity extends BaseActivity {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    //region PennStation----------------------
    ProjectActivityEventListener eventListener = new ProjectActivityEventListener() {
        @Override
        public void onEventMainThread(TestActionEventSuccess event) {

        }
    };
    //endregion
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context) {
        return new Intent(context, ProjectActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proj_activity);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    //region Init----------------------
    private void initData() {
    }

    private void initView() {
        toolbar.setTitle("Project");

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
    //endregion

}