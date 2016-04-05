package sandjentrance.com.sj.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import javax.inject.Inject;

import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.utils.Prefs;

/**
 * Created by toidiu on 4/4/16.
 */
public class BaseDialogFrag extends DialogFragment {

    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    @Inject
    protected Prefs prefs;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((SJApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // the content
//        View root = new View(getActivity());
//        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//
//        // creating the fullscreen dialog
//        Dialog dialog = new Dialog(getActivity());
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(root);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow()
//                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//
//        return dialog;
//    }
//
}
