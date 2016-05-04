package sandjentrance.com.sj.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.ui.extras.AddFileAdapter;
import sandjentrance.com.sj.ui.extras.FabAddFileInterface;
import sandjentrance.com.sj.ui.extras.FileAddInterface;
import sandjentrance.com.sj.utils.UtilKeyboard;

/**
 * Created by toidiu on 4/4/16.
 */
public class DialogAddFile extends BaseFullScreenDialogFrag implements FileAddInterface {

    //region Fields----------------------
    public static final String FILE_OBJ_EXTRA = "FILE_OBJ_EXTRA";
    //~=~=~=~=~=~=~=~=~=~=~=~=Views
    @Bind(R.id.overlay)
    View overlay;
    @Bind(R.id.container)
    View container;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.file_name)
    EditText fileNameEdit;
    @Bind(R.id.file_name_container)
    View fileNameContainer;
    @Bind(R.id.create)
    View createBtn;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private FileObj projFolder;
    private FabAddFileInterface addFileInterface;
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
        addFileInterface = (FabAddFileInterface) activity;
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

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        AddFileAdapter adapter = new AddFileAdapter(this);
        List<String> addList = new ArrayList<>();
        addList.add(BaseAction.PURCHASE_FOLDER_NAME);
        addList.add(BaseAction.FAB_FOLDER_NAME);
        addList.add(BaseAction.PROJ_REQUEST_NAME);
        addList.add(BaseAction.PHOTOS_FOLDER_NAME);
        addList.add(BaseAction.NOTES_FOLDER_NAME);
        adapter.refreshView(addList);
        recyclerView.setAdapter(adapter);

        fileNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileNameContainer.setVisibility(View.GONE);
                UtilKeyboard.hideKeyboard(getActivity(), fileNameEdit, fileNameEdit);
            }
        });
    }

    private void initData() {
        projFolder = getArguments().getParcelable(FILE_OBJ_EXTRA);
    }
    //endregion

    //region Interface----------------------
    @Override
    public void itemClicked(final String type) {
        if (type.equals(BaseAction.PHOTOS_FOLDER_NAME)) {
            String name = "photo" + System.currentTimeMillis() + ".jpg";
            createNewFile(type, name);
            return;
        }
//        else if (type.equals(BaseAction.PURCHASE_FOLDER_NAME)) {
//            createNewFile(type, "Purchase Order" + System.currentTimeMillis() + ".pdf");
//        }

        fileNameContainer.setVisibility(View.VISIBLE);
        UtilKeyboard.toggleKeyboard(getActivity());
        fileNameEdit.requestFocus();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = fileNameEdit.getText().toString().trim();
                if (!fileName.isEmpty()) {
                    if (!fileName.endsWith(".pdf")) {
                        fileName = fileName + ".pdf";
                    }
                    createNewFile(type, fileName);
                } else {
                    Snackbar.make(fileNameEdit, "File name can't be empty.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createNewFile(String type, String fileName) {
        NewFileObj newFileObj = null;
        switch (type) {
            case BaseAction.PURCHASE_FOLDER_NAME:
                newFileObj = new NewFileObj(BaseAction.PURCHASE_FOLDER_NAME, BaseAction.PURCHASE_ORDER_PDF, BaseAction.MIME_PDF, fileName, projFolder.id, null);
                break;
            case BaseAction.FAB_FOLDER_NAME:
                newFileObj = new NewFileObj(BaseAction.FAB_FOLDER_NAME, BaseAction.FAB_SHEET_PDF, BaseAction.MIME_PDF, fileName, projFolder.id, null);
                break;
            case BaseAction.PROJ_REQUEST_NAME:
                newFileObj = new NewFileObj(BaseAction.PROJ_REQUEST_NAME, BaseAction.PROJECT_LABOR_PDF, BaseAction.MIME_PDF, fileName, projFolder.id, null);
                break;
            case BaseAction.PHOTOS_FOLDER_NAME:
                newFileObj = new NewFileObj(BaseAction.PHOTOS_FOLDER_NAME, fileName, BaseAction.MIME_JPEG, fileName, projFolder.id, null);
                break;
            case BaseAction.NOTES_FOLDER_NAME:
                newFileObj = new NewFileObj(BaseAction.NOTES_FOLDER_NAME, BaseAction.NOTES_PDF, BaseAction.MIME_PDF, fileName, projFolder.id, null);
                break;
            default:
                break;
        }

        if (newFileObj != null) {
            newFileObj.projTitle = projFolder.title;
            addFileInterface.addItemClicked(newFileObj);
        }

        UtilKeyboard.hideKeyboard(getActivity(), fileNameEdit, fileNameEdit);
        dismiss();
    }
    //endregion

}
