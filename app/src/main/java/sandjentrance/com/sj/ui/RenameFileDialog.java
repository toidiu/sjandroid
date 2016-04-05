package sandjentrance.com.sj.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.RenameFileAction;
import sandjentrance.com.sj.actions.RenameFileActionEventFailure;
import sandjentrance.com.sj.actions.RenameFileActionEventSuccess;
import sandjentrance.com.sj.actions.RenameFileAction_.PsRenameFileAction;
import sandjentrance.com.sj.models.FileObj;

/**
 * Created by toidiu on 4/4/16.
 */
@EventListener(producers = {
        RenameFileAction.class
})
public class RenameFileDialog extends BaseDialogFrag {

    //region Fields----------------------
    public static final String FILE_OBJ_EXTRA = "FILE_OBJ_EXTRA";
    @Bind(R.id.submit)
    Button submitBtn;
    @Bind(R.id.orig_name)
    TextView origNameView;
    @Bind(R.id.rename)
    EditText renameEdit;
    //endregion

    //region PennStation----------------------
    private RenameFileDialogEventListener eventListener = new RenameFileDialogEventListener() {
        @Override
        public void onEventMainThread(RenameFileActionEventSuccess event) {
            dismiss();
        }

        @Override
        public void onEventMainThread(RenameFileActionEventFailure event) {

        }
    };
    private FileObj fileObj;
    //endregion

    //region Lifecycle----------------------
    public static RenameFileDialog getInstance(FileObj fileObj) {

        RenameFileDialog dialog = new RenameFileDialog();
        Bundle args = new Bundle();
        args.putParcelable(FILE_OBJ_EXTRA, Parcels.wrap(fileObj));
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
        PennStation.unRegisterListener(this);
        super.onDestroy();
    }
    //endregion

    //region Init----------------------
    private void initView() {
        origNameView.setText(fileObj.title);
        renameEdit.setText(fileObj.title);
        renameEdit.setSelection(renameEdit.getText().length());

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = renameEdit.getText().toString();
                if (newName.isEmpty()) {
                    Toast.makeText(getContext(), "New name can't be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    PennStation.requestAction(PsRenameFileAction.helper(fileObj.id, newName));
                }
            }
        });
    }


    private void initData() {
        fileObj = Parcels.unwrap(getArguments().getParcelable(FILE_OBJ_EXTRA));
    }
    //endregion

}
