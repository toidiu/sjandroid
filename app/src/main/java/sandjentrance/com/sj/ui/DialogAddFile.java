package sandjentrance.com.sj.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.actions.DbAddNewFileAction;
import sandjentrance.com.sj.actions.DbAddNewFileActionEventFailure;
import sandjentrance.com.sj.actions.DbAddNewFileActionEventSuccess;
import sandjentrance.com.sj.actions.DbAddNewFileAction_.PsDbAddNewFileAction;
import sandjentrance.com.sj.actions.UploadNewFileAction_.PsUploadNewFileAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.ui.extras.AddFileInterface;
import sandjentrance.com.sj.utils.FileUtils;

/**
 * Created by toidiu on 4/4/16.
 */
public class DialogAddFile extends BaseFullScreenDialogFrag {

    //region Fields----------------------
    public static final String FILE_OBJ_EXTRA = "FILE_OBJ_EXTRA";
    //~=~=~=~=~=~=~=~=~=~=~=~=Views
    @Bind(R.id.overlay)
    View overlay;
    @Bind(R.id.container)
    View container;
    @Bind(R.id.purchase_order)
    View purchaseOrder;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private FileObj projFolder;
    private AddFileInterface addFileInterface;
    //endregion

    //region Lifecycle----------------------
    public static DialogAddFile getInstance(FileObj projFolder) {
        DialogAddFile dialog = new DialogAddFile();
        Bundle args = new Bundle();
        args.putParcelable(FILE_OBJ_EXTRA, projFolder);
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_file, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        addFileInterface = (AddFileInterface) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //endregion

    //region Init----------------------
    private void initView() {
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //placeholder so click event doesn't propagate
            }
        });

        purchaseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFileInterface.purchaseOrderClicked(projFolder.id);
                dismiss();
            }
        });
    }


    private void initData() {
        projFolder = getArguments().getParcelable(FILE_OBJ_EXTRA);
    }
    //endregion

}
