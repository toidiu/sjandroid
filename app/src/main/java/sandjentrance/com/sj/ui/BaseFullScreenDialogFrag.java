package sandjentrance.com.sj.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import sandjentrance.com.sj.R;
import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.actions.BaseAction;
import sandjentrance.com.sj.models.LocalFileObj;
import sandjentrance.com.sj.utils.MergePfdHelper;

/**
 * Created by toidiu on 4/4/16.
 */
public class BaseFullScreenDialogFrag extends BaseDialogFrag {

    private static final int OPEN_FILE_REQUEST_CODE = 48262;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    // @Inject
    // protected Prefs prefs;
    // @Inject
    // MoveFolderHelper moveFolderHelper;
    @Inject
    MergePfdHelper mergePfdHelper;
    @Inject
    Context context;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((SJApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // the content
        View root = new View(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    public void openLocalFile(LocalFileObj localFileObj, @Nullable View view) {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType(localFileObj.mime);
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (list.isEmpty()) {
            String msg = localFileObj.mime.equals(BaseAction.MIME_PDF) ? getString(R.string.install_pdf_msg) : getString(R.string.install_img_msg);
            if (view != null) {
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            File file = new File(localFileObj.localPath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), localFileObj.mime);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            getActivity().startActivityForResult(intent, OPEN_FILE_REQUEST_CODE);
        }
    }
}
