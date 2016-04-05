package sandjentrance.com.sj.ui;


import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import static android.Manifest.permission.READ_CONTACTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends BaseActivity {
    public static final int PERM_REQUEST_CONTACT = 1236;
    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    static final int REQUEST_ACCOUNT_PICKER = 9846;
    static final int REQUEST_AUTHORIZATION = 3791;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 4232;
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

        toolbar.setTitle("Login");
        if (!isPermissionsGranted()) {
            requestPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAccountChooser();
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
                    textView.setText("Specify an account to proceed.");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERM_REQUEST_CONTACT:
                if (grantResults[0] == PERMISSION_GRANTED) {
//                    startAccountChooser();
                } else {
                    Snackbar make = Snackbar.make(progress, "App requires permissions to proceed.", Snackbar.LENGTH_INDEFINITE);
                    make.setAction("Grant Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPermission();
                        }
                    });
                    make.setActionTextColor(getResources().getColor(R.color.snackbar_text));
                    make.show();
                }
        }
    }
    //endregion

    //region GoogleHelper----------------------
    private void startAccountChooser() {
        if (isPermissionsGranted()) {
            if (isGooglePlayServicesAvailable()) {
                refreshResults();
            } else {
                textView.setText("Google Play Services required: after installing, close and relaunch this app.");
            }
        }
    }

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
    private boolean isPermissionsGranted() {
        if (ActivityCompat.checkSelfPermission(this, READ_CONTACTS) == PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_CONTACTS}, PERM_REQUEST_CONTACT);
        }
    }

    void startBaseProjActivity() {
        startActivity(FindBaseProjActivity.getInstance(this));
        finish();
    }
    //endregion

}