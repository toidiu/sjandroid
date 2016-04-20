package sandjentrance.com.sj.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import javax.inject.Inject;

import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.utils.MoveFolderHelper;
import sandjentrance.com.sj.utils.Prefs;

/**
 * Created by toidiu on 4/4/16.
 */
public class BaseDialogFrag extends DialogFragment {

    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    @Inject
    protected Prefs prefs;
    @Inject
    MoveFolderHelper moveFolderHelper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((SJApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

}
