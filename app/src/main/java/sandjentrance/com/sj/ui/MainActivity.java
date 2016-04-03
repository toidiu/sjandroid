package sandjentrance.com.sj.ui;


import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.utils.NetworkUtils;

public class MainActivity extends BaseActivity {
    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.text)
    TextView textView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    //endregion

    //region Lifecycle----------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        // FIXME: 4/2/16 init view
        toolbar.setTitle("Login");

        //fixme as for contact permission on M
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            textView.setText("Google Play Services required: after installing, close and relaunch this app.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progress.setVisibility(View.VISIBLE);

        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        prefs.setUser(accountName);

                        startBaseProjActivity();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    textView.setText("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    //endregion

    //region GoogleHelper----------------------
    private void refreshResults() {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (NetworkUtils.isDeviceOnline(this)) {
                startBaseProjActivity();
            } else {
                textView.setText("No network connection available.");
            }
        }
    }

    private void chooseAccount() {
        progress.setVisibility(View.VISIBLE);
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }


    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    void showGooglePlayErrorDialog(final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, MainActivity.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
    //endregion

    //region Helper----------------------
    void startBaseProjActivity() {
        if (prefs.getBaseFolderId()==null)
        {
            startActivity(FindBaseProjActivity.getInstance(this));
        }else {
            startActivity(ProjListActivity.getInstance(this));
        }
        finish();
    }
    //endregion

}