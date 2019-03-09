package sandjentrance.com.sj.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.RenameFileAction;
import sandjentrance.com.sj.actions.RenameFileAction_.PsRenameFileAction;
import sandjentrance.com.sj.actions.events.RenameFileActionFailure;
import sandjentrance.com.sj.actions.events.RenameFileActionSuccess;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.utils.UtilKeyboard;

/**
 * Created by toidiu on 4/4/16.
 */
@EventListener(producers = {
        RenameFileAction.class
})
public class DialogRenameFile extends BaseDialogFrag {

    //region Fields----------------------
    public static final String FILE_OBJ_EXTRA = "FILE_OBJ_EXTRA";
    @Bind(R.id.submit)
    View submitBtn;
    @Bind(R.id.orig_name)
    TextView origNameView;
    @Bind(R.id.rename)
    EditText renameEdit;
    @Bind(R.id.progress)
    ProgressBar progress;
    //~=~=~=~=~=~=~=~=~=~=~=~=View State
    private boolean isProgressVisible = false;
    //endregion

    //region PennStation----------------------
    private DialogRenameFileEventListener eventListener = new DialogRenameFileEventListener() {
        @Override
        public void onEventMainThread(RenameFileActionSuccess event) {
            dismiss();
            isProgressVisible = false;
            updateView();
        }

        @Override
        public void onEventMainThread(RenameFileActionFailure event) {
            isProgressVisible = false;
            updateView();
        }
    };
    private FileObj fileObj;
    //endregion

    //region Lifecycle----------------------
    public static DialogRenameFile getInstance(FileObj fileObj) {

        DialogRenameFile dialog = new DialogRenameFile();
        Bundle args = new Bundle();
        args.putParcelable(FILE_OBJ_EXTRA, fileObj);
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rename_file, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PennStation.registerListener(eventListener);

        initData();
        initView();
    }

    @Override
    public void onDestroy() {
        PennStation.unRegisterListener(eventListener);
        super.onDestroy();
    }
    //endregion

    //region Init----------------------
    private void initView() {
        origNameView.setText(fileObj.title);
        renameEdit.setText(fileObj.title);
        renameEdit.setSelection(renameEdit.getText().length());

        UtilKeyboard.toggleKeyboard(getContext());

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = renameEdit.getText().toString();
                if (newName.isEmpty()) {
                    Toast.makeText(getContext(), "New name can't be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    PennStation.requestAction(PsRenameFileAction.helper(fileObj, newName));
                    isProgressVisible = true;
                    updateView();
                }
            }
        });

        updateView();
    }


    private void initData() {
        fileObj = getArguments().getParcelable(FILE_OBJ_EXTRA);
    }
    //endregion


    //region Helper----------------------
    private void updateView() {
        if (isProgressVisible) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }
    //endregion

}
