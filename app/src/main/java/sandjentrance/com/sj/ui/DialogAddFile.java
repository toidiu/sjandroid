package sandjentrance.com.sj.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edisonwang.ps.annotations.EventListener;
import com.edisonwang.ps.lib.PennStation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.actions.GetNextPONumberAction;
import sandjentrance.com.sj.actions.GetNextPONumberActionHelper;
import sandjentrance.com.sj.actions.events.GetNextPONumberActionFailure;
import sandjentrance.com.sj.actions.events.GetNextPONumberActionSuccess;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.models.NewFileObj;
import sandjentrance.com.sj.ui.extras.AddFileAdapter;
import sandjentrance.com.sj.ui.extras.FabAddFileInterface;
import sandjentrance.com.sj.ui.extras.FileAddInterface;
import sandjentrance.com.sj.utils.UtilKeyboard;
import sandjentrance.com.sj.utils.UtilNetwork;

/**
 * Created by toidiu on 4/4/16.
 */

@EventListener(producers = {
        GetNextPONumberAction.class
})
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
    @Bind(R.id.po_number)
    TextView poNumberText;
    @Bind(R.id.po_number_container)
    View poNumberContainer;
    @Bind(R.id.file_name_container)
    View fileNameContainer;
    @Bind(R.id.create)
    View createBtn;
    @Bind(R.id.or_text)
    View orText;
    @Bind(R.id.merge)
    View mergeBtn;
    @Bind(R.id.open_file)
    View openFileBtn;
    @Bind(R.id.progress)
    ProgressBar progress;
    //~=~=~=~=~=~=~=~=~=~=~=~=Click
    FileObj clickOpenFileObj = null;
    String clickCreateFileType = null;
    //~=~=~=~=~=~=~=~=~=~=~=~=View State
    private boolean isProgressVisible = false;
    private boolean isfileNameConVisible = false;
    private boolean isfileNameBtnVisible = false;
    private boolean isPoNumConVisible = false;
    private boolean isPoNumBgBlue = false;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private FileObj projFolder;
    private FabAddFileInterface addFileInterface;


    //region PennStation----------------------
    DialogAddFileEventListener eventListener = new DialogAddFileEventListener() {
        @Override
        public void onEventMainThread(GetNextPONumberActionFailure event) {
            isProgressVisible = false;
            updateView();
            Toast.makeText(context, "Unable to add a Purchase Order. Please check your connection.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEventMainThread(final GetNextPONumberActionSuccess event) {
            isProgressVisible = false;
            updateView();
            UtilKeyboard.hideKeyboard(getActivity(), fileNameEdit, fileNameEdit);

            //FIXME this needs to save state!!!!!!!!
            poNumberText.setText(event.nextNumber);

            //show the PO number interface and hide fileName interface
            isfileNameConVisible = false;
            isPoNumConVisible = true;
            updateView();

            //open pdf file
            clickOpenFileObj = event.fileObj;
            updateClickListeners();

            //Copy number
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {

                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("PO number", poNumberText.getText().toString());
                    clipboard.setPrimaryClip(clip);

                    isPoNumBgBlue = true;
                    updateView();

                    Toast.makeText(context, "Order # copied.", Toast.LENGTH_SHORT).show();
                }
            };

            mainHandler.postDelayed(myRunnable, 1000);

        }
    };

    //endregion
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
        PennStation.registerListener(eventListener);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
        initClickListeners();
    }

    @Override
    public void onDetach() {
        PennStation.unRegisterListener(eventListener);
        super.onDetach();
    }
    //endregion

    //region Init----------------------
    private void initView() {

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

        updateView();
    }

    private void initData() {
        projFolder = getArguments().getParcelable(FILE_OBJ_EXTRA);
    }

    private void initClickListeners() {

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

        fileNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isfileNameConVisible = false;
                updateView();
                UtilKeyboard.hideKeyboard(getActivity(), fileNameEdit, fileNameEdit);
            }
        });

        updateClickListeners();
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

        if (type.equals(BaseAction.FAB_FOLDER_NAME)) {
            isfileNameBtnVisible = true;
            updateView();
        } else {
            isfileNameBtnVisible = false;
            updateView();
        }

        isfileNameConVisible = true;
        updateView();
        UtilKeyboard.toggleKeyboard(getActivity());
        fileNameEdit.requestFocus();

        clickCreateFileType = type;
        updateClickListeners();
    }

    private void createNewFile(String type, String fileName) {
        NewFileObj newFileObj = null;

        if (type.equals(BaseAction.PURCHASE_FOLDER_NAME)) {
            newFileObj = new NewFileObj(BaseAction.PURCHASE_FOLDER_NAME, BaseAction.PURCHASE_ORDER_ASSET_PDF, BaseAction.MIME_PDF, fileName, projFolder.id, null);
            isProgressVisible = true;
            updateView();
            PennStation.requestAction(new GetNextPONumberActionHelper(newFileObj));
            return;
        }

        switch (type) {
            case BaseAction.FAB_FOLDER_NAME:
                newFileObj = new NewFileObj(BaseAction.FAB_FOLDER_NAME, BaseAction.FAB_SHEET_ASSET_PDF, BaseAction.MIME_PDF, fileName, projFolder.id, null);
                break;
            case BaseAction.PROJ_REQUEST_NAME:
                newFileObj = new NewFileObj(BaseAction.PROJ_REQUEST_NAME, BaseAction.PROJECT_LABOR_ASSET_PDF, BaseAction.MIME_PDF, fileName, projFolder.id, null);
                break;
            case BaseAction.PHOTOS_FOLDER_NAME:
                newFileObj = new NewFileObj(BaseAction.PHOTOS_FOLDER_NAME, fileName, BaseAction.MIME_JPEG, fileName, projFolder.id, null);
                break;
            case BaseAction.NOTES_FOLDER_NAME:
                newFileObj = new NewFileObj(BaseAction.NOTES_FOLDER_NAME, BaseAction.NOTES_ASSET_PDF, BaseAction.MIME_PDF, fileName, projFolder.id, null);
                break;
            default:
                break;
        }

        dismissDialogAndEdit(newFileObj);
    }


    //endregion

    //region Helper----------------------
    private void dismissDialogAndEdit(NewFileObj newFileObj) {
        if (newFileObj != null) {
            newFileObj.projTitle = projFolder.title;
            addFileInterface.addItemClicked(newFileObj);
        }

        UtilKeyboard.hideKeyboard(getActivity(), fileNameEdit, fileNameEdit);
        dismiss();
    }

    private void updateView() {
        if (isPoNumBgBlue) {
            poNumberText.setBackgroundColor(getResources().getColor(R.color.folder_blue));
        } else {
            poNumberText.setBackgroundColor(getResources().getColor(R.color.white_15));
        }
        if (isfileNameBtnVisible) {
            orText.setVisibility(View.VISIBLE);
            mergeBtn.setVisibility(View.VISIBLE);
        } else {
            orText.setVisibility(View.GONE);
            mergeBtn.setVisibility(View.GONE);
        }

        if (isPoNumConVisible) {
            poNumberContainer.setVisibility(View.VISIBLE);
        } else {
            poNumberContainer.setVisibility(View.GONE);
        }
        if (isfileNameConVisible) {
            fileNameContainer.setVisibility(View.VISIBLE);
        } else {
            fileNameContainer.setVisibility(View.GONE);
        }
        if (isProgressVisible) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    private void updateClickListeners() {
        // open file
        if (clickOpenFileObj != null && openFileBtn.getVisibility() == View.VISIBLE) {
            openFileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFileInterface.openPoPdfClicked(clickOpenFileObj);

                    UtilKeyboard.hideKeyboard(getActivity(), fileNameEdit, fileNameEdit);
                    dismiss();
                }
            });
        }

        // merge pdf
        if (mergeBtn.getVisibility() == View.VISIBLE) {
            mergeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UtilNetwork.isDeviceOnline(getContext())) {
                        mergePfdHelper.isMerging = true;
                        mergePfdHelper.projFolder = projFolder;
                        addFileInterface.mergePdfClicked();
                        UtilKeyboard.hideKeyboard(getActivity(), fileNameEdit, fileNameEdit);
                        dismiss();
                    } else {
                        Snackbar.make(mergeBtn, getString(R.string.no_network_merge), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // create file
        if (clickCreateFileType != null && createBtn.getVisibility() == View.VISIBLE) {
            createBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fileName = fileNameEdit.getText().toString().trim();
                    if (!fileName.isEmpty()) {
                        if (!fileName.endsWith(".pdf")) {
                            fileName = fileName + ".pdf";
                        }
                        createNewFile(clickCreateFileType, fileName);
                    } else {
                        Snackbar.make(fileNameEdit, "File name can't be empty.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    //endregion
}
