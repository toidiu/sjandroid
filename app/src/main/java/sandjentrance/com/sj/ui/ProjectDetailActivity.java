package sandjentrance.com.sj.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.FindFolderChildrenAction;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventFailure;
import sandjentrance.com.sj.actions.FindFolderChildrenActionEventSuccess;
import sandjentrance.com.sj.actions.FindFolderChildrenAction_.PsFindFolderChildrenAction;
import sandjentrance.com.sj.actions.TestActionEventSuccess;
import sandjentrance.com.sj.models.FileObj;

@EventListener(producers = {
        FindFolderChildrenAction.class
})
public class ProjectDetailActivity extends BaseActivity {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String FILE_OBJ = "FILE_OBJ";
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.pm_name)
    TextView pmNameView;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //~=~=~=~=~=~=~=~=~=~=~=~=Field

    //region PennStation----------------------
    ProjectDetailActivityEventListener eventListener = new ProjectDetailActivityEventListener() {
        @Override
        public void onEventMainThread(FindFolderChildrenActionEventFailure event) {

        }

        @Override
        public void onEventMainThread(FindFolderChildrenActionEventSuccess event) {

        }
    };
    private FileObj fileObj;
    //endregion
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context, FileObj fileObj) {
        Intent intent = new Intent(context, ProjectDetailActivity.class);
        intent.putExtra(FILE_OBJ, Parcels.wrap(fileObj));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proj_activity);
        ButterKnife.bind(this);

        fileObj = Parcels.unwrap(getIntent().getParcelableExtra(FILE_OBJ));

        initData();
        initView();
    }

    //region Init----------------------
    private void initData() {
        PennStation.requestAction(PsFindFolderChildrenAction.helper("", fileObj.id, false));
    }

    private void initView() {
        toolbar.setTitle(fileObj.title);
        pmNameView.setText(fileObj.owner);
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