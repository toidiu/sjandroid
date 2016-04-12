package sandjentrance.com.sj.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.edisonwang.ps.lib.PennStation;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.MoveFileActionEventPrime;
import sandjentrance.com.sj.actions.MoveFileActionEventSuccess;
import sandjentrance.com.sj.models.FileObj;

/**
 * Created by toidiu on 4/4/16.
 */
public class DialogChooseFileAction extends BaseDialogFrag {

    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    public static final String FILE_OBJ_EXTRA = "FILE_OBJ_EXTRA";
    //~=~=~=~=~=~=~=~=~=~=~=~=Views
    @Bind(R.id.move_file)
    Button moveBtn;
    @Bind(R.id.rename_file)
    Button renameBtn;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private FileObj fileObj;
    //endregion

    //region Lifecycle----------------------
    public static DialogChooseFileAction getInstance(FileObj fileObj) {

        DialogChooseFileAction dialog = new DialogChooseFileAction();
        Bundle args = new Bundle();
        args.putParcelable(FILE_OBJ_EXTRA, Parcels.wrap(fileObj));
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_file_action, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }
    //endregion

    //region Init----------------------
    private void initView() {
        moveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveFolderHelper.startMove(fileObj.id, fileObj.parent);
                PennStation.postLocalEvent(new MoveFileActionEventPrime());
                dismiss();
            }
        });
        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogRenameFile.getInstance(fileObj).show(getFragmentManager(), null);
                dismiss();
            }
        });
    }


    private void initData() {
        fileObj = Parcels.unwrap(getArguments().getParcelable(FILE_OBJ_EXTRA));
    }
    //endregion

}
