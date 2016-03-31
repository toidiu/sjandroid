//package sandjentrance.com.sj;
//
//import android.content.Intent;
//import android.content.IntentSender;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.Toast;
//
//import com.edisonwang.ps.annotations.EventListener;
//import com.edisonwang.ps.lib.PennStation;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//
//import sandjentrance.com.sj.TestAction_.PsTestAction;
//
//@EventListener(producers = {
//        TestAction.class
//})
//public class OtherMainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//
//
//    public static final int RESOLVE_CONNECTION_REQUEST_CODE = 34252;
//    private final OtherMainActivityEventListener mListener = new OtherMainActivityEventListener() {
//
//        @Override
//        public void onEventMainThread(TestActionEventSuccess event) {
////            Toast.makeText(MainActivity.this, "there", Toast.LENGTH_SHORT).show();
//        }
//
//    };
//    private GoogleApiClient gClient;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                callFabClick();
//            }
//        });
//
//        PennStation.requestAction(PsTestAction.helper());
//
//
//        gClient = new GoogleApiClient.Builder(this)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .addScope(Drive.SCOPE_APPFOLDER)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//    }
//
//    private void callFabClick() {
//        ResultCallback<DriveApi.MetadataBufferResult> childrenRetrievedCallback = new
//                ResultCallback<DriveApi.MetadataBufferResult>() {
//                    @Override
//                    public void onResult(DriveApi.MetadataBufferResult result) {
//                        if (!result.getStatus().isSuccess()) {
//                            return;
//                        }
//                        MetadataBuffer metadataBuffer = result.getMetadataBuffer();
//                        int count = metadataBuffer.getCount();
//                        Toast.makeText(MainActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
//                    }
//                };
//
//        ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
//                ResultCallback<DriveFolder.DriveFolderResult>() {
//                    @Override
//                    public void onResult(DriveFolder.DriveFolderResult result) {
//                        if (!result.getStatus().isSuccess()) {
//                            return;
//                        }
//                    }
//                };
//
//        //CREATE
////        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
////                .setTitle("New folder").build();
////        Drive.DriveApi.getRootFolder(gClient).createFolder(gClient, changeSet).setResultCallback(folderCreatedCallback);
////        Drive.DriveApi.getRootFolder(gClient).
//
//
//        //LIST
////        Drive.DriveApi.getRootFolder(gClient).listChildren(gClient).setResultCallback(childrenRetrievedCallback);
//
//        //QUERY
//        Query query = new Query.Builder()
//                .addFilter(Filters.eq(SearchableField.TITLE, "Ethershaft"))
//                .build();
//        Drive.DriveApi.query(gClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
//            @Override
//            public void onResult(DriveApi.MetadataBufferResult metadat    jhfdsa`1aBufferResult) {
//                int count = metadataBufferResult.getMetadataBuffer().getCount();
//                Toast.makeText(MainActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
//
////                DriveId driveId = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
////                driveId.asDriveFolder().delete(gClient);
//            }
//        });
//
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        gClient.connect();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        PennStation.registerListener(mListener);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        PennStation.unRegisterListener(mListener);
//    }
//
//    @Override
//    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        switch (requestCode) {
//            case RESOLVE_CONNECTION_REQUEST_CODE:
//                if (resultCode == RESULT_OK) {
//                    gClient.connect();
//                } else {
//                    // User denied access, show him the account chooser again
//                    Toast.makeText(OtherMainActivity.this, "Permission denied.", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
//
//
//    @Override
//    public void onConnected(Bundle bundle) {
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        if (connectionResult.hasResolution()) {
//            try {
//                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
//            } catch (IntentSender.SendIntentException e) {
//                Toast.makeText(OtherMainActivity.this, "Error while connecting.", Toast.LENGTH_SHORT).show();
//                // Unable to resolve, message user appropriately
//            }
//        } else {
//            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
//        }
//    }
//
//
//}
