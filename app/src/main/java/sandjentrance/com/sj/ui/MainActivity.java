package sandjentrance.com.sj.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import sandjentrance.com.sj.BuildConfig;
import sandjentrance.com.sj.R;
import sandjentrance.com.sj.SJApplication;
import sandjentrance.com.sj.models.FileObj;
import sandjentrance.com.sj.utils.BgImageLoader;
import sandjentrance.com.sj.utils.MoveFolderHelper;
import sandjentrance.com.sj.utils.Prefs;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    //region Fields----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_ACCOUNT_STORAGE = 1003;
    String[] PERM_REQUEST = {GET_ACCOUNTS, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    //~=~=~=~=~=~=~=~=~=~=~=~=View
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    @Inject
    Prefs prefs;
    @Inject
    GoogleAccountCredential credential;
    @Inject
    MoveFolderHelper moveFolderHelper;
    //endregion

    //region LifeCycle----------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        ((SJApplication) getApplication()).getAppComponent().inject(this);

        Log.d("log", "-----------1");
        initBg();
        getStarted();
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Snackbar.make(progress, R.string.error_play_services, Snackbar.LENGTH_INDEFINITE).show();
                } else {
                    getStarted();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        prefs.setUser(accountName);
                        credential.setSelectedAccountName(accountName);
                        getStarted();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getStarted();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        final Snackbar snackbar = Snackbar.make(progress, "Please grant permission to continue.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Grant Permission", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStarted();
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.snackbar_text));
        snackbar.show();
        // Do nothing.
    }

    //endregion

    //region Google Helper----------------------

    private void getStarted() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (credential.getSelectedAccountName() == null) {
            Log.d("log", "-----------2");
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Log.d("log", "-----------3");
            Snackbar.make(progress, R.string.no_network, Snackbar.LENGTH_SHORT).show();
            if (prefs.getBaseFolderId() != null) {
                startBaseProjActivity();
            }
        } else {
            Log.d("log", "-----------4");
            new MakeRequestTask(credential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_ACCOUNT_STORAGE)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, PERM_REQUEST)) {
            String accountName = prefs.getUser();
            if (accountName != null) {
                Log.d("log1", "-----------1");
                credential.setSelectedAccountName(accountName);
                Log.d("log1", String.valueOf(credential == null));

                if (credential != null) {
                    getStarted();
                }
            } else {
                Log.d("log1", "-----------2");
                // Start a dialog from which the user can choose an account
                startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            Log.d("log1", "-----------3");
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(this, getString(R.string.permission_msg),
                    REQUEST_PERMISSION_ACCOUNT_STORAGE, PERM_REQUEST);
        }
    }


    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(MainActivity.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
    //endregion

    //region Helper----------------------

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    void startBaseProjActivity() {
        startActivity(FindBaseProjActivity.getInstance(this));
        finish();
    }
    //endregion

    //region Init----------------------
    protected void initBg() {
        final View layout = findViewById(R.id.layout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Picasso.with(MainActivity.this).load(R.drawable.app_bg).into(new BgImageLoader(getResources(), layout));
            }
        });
    }
    //endregion


    //region MakeCall----------------------
    private class MakeRequestTask extends AsyncTask<Void, Void, List<FileObj>> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential).setApplicationName("SJ").build();
        }

        @Override
        protected List<FileObj> doInBackground(Void... params) {
            try {
                Log.d("log", "-----------5");
                return getDataFromApi();
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<FileObj> getDataFromApi() throws IOException {
            String search = "title contains '" + "Jobs" + "'"
                    + " and " + "title != '.DS_Store'"
                    + " and " + " sharedWithMe=true "
                    + " and " + "mimeType = '" + FileObj.FOLDER_MIME + "'";

            List<FileObj> retFile = new ArrayList<>();

            Log.d("log", "-----------6");
            FileList result = mService.files().list()
                    .setQ(search)
                    .setSpaces("drive")
//                    .setFields("nextPageToken, files(id, name, modifiedTime, owners, mimeType, parents, properties)")
                    .execute();


            List<File> files = result.getItems();
            if (files != null) {
                for (File f : files) {
                    FileObj object = new FileObj(f);
                    retFile.add(object);
                }
            }
            return retFile;
        }

        @Override
        protected void onPostExecute(List<FileObj> output) {
            Log.d("log", "-----------7");
            startBaseProjActivity();
        }

        @Override
        protected void onCancelled() {
            progress.setVisibility(View.GONE);
            Log.d("log", "-----------8");

            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    Toast.makeText(MainActivity.this, "test only: play services error", Toast.LENGTH_SHORT).show();
                    showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    Toast.makeText(MainActivity.this, "test only: recoverable error", Toast.LENGTH_SHORT).show();
                    startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(), MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(MainActivity.this, "test only: login error", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MainActivity.this, "test only: cancelled error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //endregion
}

//
//import android.Manifest;
//import android.accounts.AccountManager;
//import android.app.Activity;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//import android.text.method.ScrollingMovementMethod;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
//import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
//import com.google.api.client.util.ExponentialBackOff;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.DriveScopes;
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.inject.Inject;
//
//import pub.devrel.easypermissions.AfterPermissionGranted;
//import pub.devrel.easypermissions.EasyPermissions;
//import sandjentrance.com.sj.SJApplication;
//import sandjentrance.com.sj.utils.Prefs;
//
//public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {
//    static final int REQUEST_ACCOUNT_PICKER = 1000;
//    static final int REQUEST_AUTHORIZATION = 1001;
//    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
//    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
//    private static final String BUTTON_TEXT = "Call Drive API";
//    private static final String PREF_ACCOUNT_NAME = "accountName";
//    private static final String[] SCOPES = {DriveScopes.DRIVE_METADATA_READONLY};
//    GoogleAccountCredential mCredential;
//    ProgressDialog mProgress;
//    @Inject
//    Prefs prefs;
//    @Inject
//    Drive driveService;
//    private TextView mOutputText;
//    private Button mCallApiButton;
//
//    /**
//     * Create the main activity.
//     *
//     * @param savedInstanceState previously saved instance data.
//     */
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        ((SJApplication) SJApplication.appContext).getAppComponent().inject(this);
//
//        setupView();
//
//        // Initialize credentials and service object.
//        mCredential = GoogleAccountCredential.usingOAuth2(
//                getApplicationContext(), Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff());
//
//    }
//
//    private void setupView() {
//        LinearLayout activityLayout = new LinearLayout(this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        activityLayout.setLayoutParams(lp);
//        activityLayout.setOrientation(LinearLayout.VERTICAL);
//        activityLayout.setPadding(16, 16, 16, 16);
//
//        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        mCallApiButton = new Button(this);
//        mCallApiButton.setText(BUTTON_TEXT);
//        mCallApiButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallApiButton.setEnabled(false);
//                mOutputText.setText("");
//                getResultsFromApi();
//                mCallApiButton.setEnabled(true);
//            }
//        });
//        activityLayout.addView(mCallApiButton);
//
//        mOutputText = new TextView(this);
//        mOutputText.setLayoutParams(tlp);
//        mOutputText.setPadding(16, 16, 16, 16);
//        mOutputText.setVerticalScrollBarEnabled(true);
//        mOutputText.setMovementMethod(new ScrollingMovementMethod());
//        mOutputText.setText(
//                "Click the \'" + BUTTON_TEXT + "\' button to test the API.");
//        activityLayout.addView(mOutputText);
//
//        mProgress = new ProgressDialog(this);
//        mProgress.setMessage("Calling Drive API ...");
//
//        setContentView(activityLayout);
//    }
//
//
//    /**
//     * Attempt to call the API, after verifying that all the preconditions are
//     * satisfied. The preconditions are: Google Play Services installed, an
//     * account was selected and the device currently has online access. If any
//     * of the preconditions are not satisfied, the app will prompt the user as
//     * appropriate.
//     */
//    private void getResultsFromApi() {
//        if (!isGooglePlayServicesAvailable()) {
//            acquireGooglePlayServices();
//        } else if (mCredential.getSelectedAccountName() == null) {
//            chooseAccount();
//        } else if (!isDeviceOnline()) {
//            mOutputText.setText("No network connection available.");
//        } else {
//            new MakeRequestTask(mCredential).execute();
//        }
//    }
//
//    /**
//     * Attempts to set the account used with the API credentials. If an account
//     * name was previously saved it will use that one; otherwise an account
//     * picker dialog will be shown to the user. Note that the setting the
//     * account to use with the credentials object requires the app to have the
//     * GET_ACCOUNTS permission, which is requested here if it is not already
//     * present. The AfterPermissionGranted annotation indicates that this
//     * function will be rerun automatically whenever the GET_ACCOUNTS permission
//     * is granted.
//     */
//    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
//    private void chooseAccount() {
//        if (EasyPermissions.hasPermissions(
//                this, Manifest.permission.GET_ACCOUNTS)) {
////            String accountName = getPreferences(Context.MODE_PRIVATE)
////                    .getString(PREF_ACCOUNT_NAME, null);
//            String accountName = prefs.getUser();
//            if (accountName != null) {
//                mCredential.setSelectedAccountName(accountName);
//                getResultsFromApi();
//            } else {
//                // Start a dialog from which the user can choose an account
//                startActivityForResult(
//                        mCredential.newChooseAccountIntent(),
//                        REQUEST_ACCOUNT_PICKER);
//            }
//        } else {
//            // Request the GET_ACCOUNTS permission via a user dialog
//            EasyPermissions.requestPermissions(
//                    this,
//                    "This app needs to access your Google account (via Contacts).",
//                    REQUEST_PERMISSION_GET_ACCOUNTS,
//                    Manifest.permission.GET_ACCOUNTS);
//        }
//    }
//
//    /**
//     * Called when an activity launched here (specifically, AccountPicker
//     * and authorization) exits, giving you the requestCode you started it with,
//     * the resultCode it returned, and any additional data from it.
//     *
//     * @param requestCode code indicating which activity result is incoming.
//     * @param resultCode  code indicating the result of the incoming
//     *                    activity result.
//     * @param data        Intent (containing result data) returned by incoming
//     *                    activity result.
//     */
//    @Override
//    protected void onActivityResult(
//            int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_GOOGLE_PLAY_SERVICES:
//                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
//                } else {
//                    getResultsFromApi();
//                }
//                break;
//            case REQUEST_ACCOUNT_PICKER:
//                if (resultCode == RESULT_OK && data != null &&
//                        data.getExtras() != null) {
//                    String accountName =
//                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//                    if (accountName != null) {
//                        prefs.setUser(accountName);
////                        SharedPreferences settings =
////                                getPreferences(Context.MODE_PRIVATE);
////                        SharedPreferences.Editor editor = settings.edit();
////                        editor.putString(PREF_ACCOUNT_NAME, accountName);
////                        editor.apply();
//
//                        mCredential.setSelectedAccountName(accountName);
//                        getResultsFromApi();
//                    }
//                }
//                break;
//            case REQUEST_AUTHORIZATION:
//                if (resultCode == RESULT_OK) {
//                    getResultsFromApi();
//                }
//                break;
//        }
//    }
//
//    /**
//     * Respond to requests for permissions at runtime for API 23 and above.
//     *
//     * @param requestCode  The request code passed in
//     *                     requestPermissions(android.app.Activity, String, int, String[])
//     * @param permissions  The requested permissions. Never null.
//     * @param grantResults The grant results for the corresponding permissions
//     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(
//                requestCode, permissions, grantResults, this);
//    }
//
//    /**
//     * Callback for when a permission is granted using the EasyPermissions
//     * library.
//     *
//     * @param requestCode The request code associated with the requested
//     *                    permission
//     * @param list        The requested permission list. Never null.
//     */
//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> list) {
//        // Do nothing.
//    }
//
//    /**
//     * Callback for when a permission is denied using the EasyPermissions
//     * library.
//     *
//     * @param requestCode The request code associated with the requested
//     *                    permission
//     * @param list        The requested permission list. Never null.
//     */
//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> list) {
//        // Do nothing.
//    }
//
//    /**
//     * Checks whether the device currently has a network connection.
//     *
//     * @return true if the device has a network connection, false otherwise.
//     */
//    private boolean isDeviceOnline() {
//        ConnectivityManager connMgr =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        return (networkInfo != null && networkInfo.isConnected());
//    }
//
//    /**
//     * Check that Google Play services APK is installed and up to date.
//     *
//     * @return true if Google Play Services is available and up to
//     * date on this device; false otherwise.
//     */
//    private boolean isGooglePlayServicesAvailable() {
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(this);
//        return connectionStatusCode == ConnectionResult.SUCCESS;
//    }
//
//    /**
//     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
//     * Play Services installation via a user dialog, if possible.
//     */
//    private void acquireGooglePlayServices() {
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(this);
//        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
//            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
//        }
//    }
//
//
//    /**
//     * Display an error dialog showing that Google Play Services is missing
//     * or out of date.
//     *
//     * @param connectionStatusCode code describing the presence (or lack of)
//     *                             Google Play Services on this device.
//     */
//    void showGooglePlayServicesAvailabilityErrorDialog(
//            final int connectionStatusCode) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        Dialog dialog = apiAvailability.getErrorDialog(
//                MainActivity.this,
//                connectionStatusCode,
//                REQUEST_GOOGLE_PLAY_SERVICES);
//        dialog.show();
//    }
//
//    /**
//     * An asynchronous task that handles the Drive API call.
//     * Placing the API calls in their own task ensures the UI stays responsive.
//     */
//    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
//        //        private com.google.api.services.drive.Drive mService = null;
//        private Exception mLastError = null;
//
//        public MakeRequestTask(GoogleAccountCredential credential) {
//
////            HttpTransport transport = AndroidHttp.newCompatibleTransport();
////            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
////            mService = new com.google.api.services.drive.Drive.Builder(
////                    transport, jsonFactory, credential)
////                    .setApplicationName("Drive API Android Quickstart")
////                    .build();
//        }
//
//        /**
//         * Background task to call Drive API.
//         *
//         * @param params no parameters needed for this task.
//         */
//        @Override
//        protected List<String> doInBackground(Void... params) {
//            try {
//                return getDataFromApi();
//            } catch (Exception e) {
//                mLastError = e;
//                cancel(true);
//                return null;
//            }
//        }
//
//        /**
//         * Fetch a list of up to 10 file names and IDs.
//         *
//         * @return List of Strings describing files, or an empty list if no files
//         * found.
//         * @throws IOException
//         */
//        private List<String> getDataFromApi() throws IOException {
//            // Get a list of up to 10 files.
//            List<String> fileInfo = new ArrayList<String>();
//            FileList result = driveService.files().list()
//                    .setMaxResults(10)
//                    .execute();
//            List<File> files = result.getItems();
//            if (files != null) {
//                for (File file : files) {
//                    fileInfo.add(String.format("%s (%s)\n",
//                            file.getTitle(), file.getId()));
//                }
//            }
//            return fileInfo;
//        }
//
//
//        @Override
//        protected void onPreExecute() {
//            mOutputText.setText("");
//            mProgress.show();
//        }
//
//        @Override
//        protected void onPostExecute(List<String> output) {
//            mProgress.hide();
//            if (output == null || output.size() == 0) {
//                mOutputText.setText("No results returned.");
//            } else {
//                output.add(0, "Data retrieved using the Drive API:");
//                mOutputText.setText(TextUtils.join("\n", output));
//                startActivity(FindBaseProjActivity.getInstance(MainActivity.this));
//                finish();
//
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mProgress.hide();
//            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
//                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                            MainActivity.REQUEST_AUTHORIZATION);
//                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
//                }
//            } else {
//                mOutputText.setText("Request cancelled.");
//            }
//        }
//    }
//}