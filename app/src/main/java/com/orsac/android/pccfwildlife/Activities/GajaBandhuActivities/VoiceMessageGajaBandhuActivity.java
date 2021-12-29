package com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SQLiteDB.GajaBandhuDbHelper;
import com.orsac.android.pccfwildlife.CameraClass.RunTimePermission;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuReportingObj;
import com.orsac.android.pccfwildlife.MyUtils.GPSTracker;
import com.orsac.android.pccfwildlife.MyUtils.GoogleApiClientHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.SQLiteDB.RecordingDbHelper;
import com.orsac.android.pccfwildlife.Services.RecordingService;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import static android.media.AudioAttributes.ALLOW_CAPTURE_BY_SYSTEM;

public class VoiceMessageGajaBandhuActivity extends AppCompatActivity implements LocationListener, com.google.android.gms.location.LocationListener,
        PermissionUtils.PermissionResultCallback{

    CardView recordCV,stopCV,playCV;
    TextView submit_txt,toolbar_txt,added_txt;
    EditText extra_txt_edit;
    private RunTimePermission runTimePermission;
    Toolbar toolbar;
    ImageView profileImg,toolbar_profile_img,toolbar_back_img;
    String mFilePath="";
    ScrollView main_ll;
    SessionManager session;
    String msg_on_edit="",userID="",reportType="voice",fileName="",folderType="gajabandhu",currentDate="",
            lat_deg="",lat_min="",lat_sec="",lng_degree="",lng_min="",lng_sec="";
    GajaBandhuDbHelper dbHelper;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;//3 sec
    Location location;
    double latitude;
    double longitude;
    GPSTracker gps;
    ArrayList<String> permissions = new ArrayList<>();
    PermissionUtils permissionUtils;
    PermissionHelperClass permissionHelperClass;
    boolean isPermissionGranted;
    public LocationRequest locationRequest;
    final static int REQUEST_LOCATION = 199,REQUEST_RECORD_AUDIO_PERMISSION=200;
    GoogleApiClientHelperClass googleApiClientHelperClass;
    TextInputEditText latdeg,latmin,latsec,londeg,lonmin,lonsec;
    Runnable runnable;
    int seconds;

    MediaPlayer mp = new MediaPlayer();
    private MediaRecorder mRecorder = null;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private final int mElapsedSeconds = 0;
    private final String mFileName = null;
    private RecordingDbHelper mDatabase;
    private static final String LOG_TAG = "RecordingService";
    private boolean permissionToRecordAccepted = false;
    private final String [] audio_permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_messge_banasathi_fragment_layout);

        initData();
        
        clickFunction();
        
    }

    private void initData() {
        try {
            recordCV=findViewById(R.id.recordCV);
            stopCV=findViewById(R.id.stopCV);
            playCV=findViewById(R.id.playCV);
            submit_txt=findViewById(R.id.submit_txt);
            extra_txt_edit=findViewById(R.id.extra_txt_edit);
            main_ll=findViewById(R.id.main_ll);
            latdeg=findViewById(R.id.latdeg);
            latmin=findViewById(R.id.latmin);
            latsec=findViewById(R.id.latsec);
            londeg=findViewById(R.id.londeg);
            lonmin=findViewById(R.id.lonmin);
            lonsec=findViewById(R.id.lonsec);
            added_txt=findViewById(R.id.added_txt);

            toolbar = findViewById(R.id.toolbar_new_id);
            toolbar_profile_img = toolbar.findViewById(R.id.profile_img);
            toolbar_back_img = toolbar.findViewById(R.id.back_img);
            toolbar_txt = toolbar.findViewById(R.id.toolbar_txt);

            toolbar_profile_img.setVisibility(View.GONE);
            toolbar_back_img.setVisibility(View.VISIBLE);
            toolbar_txt.setText("Voice Message");
            session=new SessionManager(VoiceMessageGajaBandhuActivity.this);
            dbHelper=new GajaBandhuDbHelper(VoiceMessageGajaBandhuActivity.this);

            recordCV.setVisibility(View.VISIBLE);
            stopCV.setVisibility(View.GONE);
            playCV.setVisibility(View.GONE);

            added_txt.setVisibility(View.GONE);//Remove the added text

            extra_txt_edit.clearFocus();
            PermissionUtils.hideKeyboard(VoiceMessageGajaBandhuActivity.this);

            runTimePermission = new RunTimePermission(VoiceMessageGajaBandhuActivity.this);

            mDatabase = new RecordingDbHelper(getApplicationContext());

            runTimePermission.requestPermission(new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, new RunTimePermission.RunTimePermissionListener() {
                        @Override
                        public void permissionGranted() {

                        }

                        @Override
                        public void permissionDenied() {

                        }
                    }
            );

            permissionUtils = new PermissionUtils(VoiceMessageGajaBandhuActivity.this);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

            getLocation();
            permissionHelperClass = new PermissionHelperClass(VoiceMessageGajaBandhuActivity.this);
            if (permissionHelperClass.hasLocationPermission()) {

                callGoogleApiClient();
            } else {
                permissionHelperClass.requestLocationPermission();//request location permissions
            }
            currentDate=PermissionUtils.getCurrentDate("dd-MM-yyyy HH:mm:ss");
//            userID="7978328829";
            userID=session.getGajaBandhu_UserId();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void clickFunction() {
        try {

            recordCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        recordCV.setVisibility(View.GONE);
                        stopCV.setVisibility(View.VISIBLE);
                        playCV.setVisibility(View.GONE);

//                        Toast.makeText(VoiceMessageGajaBandhuActivity.this,"Recording...",Toast.LENGTH_SHORT).show();
                        File folder = new File(Environment.getExternalStorageDirectory() + "/WildLife_SoundRecorder");
                        if (!folder.exists()) {
                            //folder /SoundRecorder doesn't exist, create the folder
                            folder.mkdir();
                        }

//                        recordVoice();
                        recordVoice(folder);//added new


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            stopCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        recordCV.setVisibility(View.VISIBLE);
                        stopCV.setVisibility(View.GONE);
                        playCV.setVisibility(View.VISIBLE);
                        stopRecordVoice(added_txt);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            playCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                         mp = new MediaPlayer();

                        recordCV.setVisibility(View.VISIBLE);
                        stopCV.setVisibility(View.GONE);
                        playCV.setVisibility(View.GONE);

                        seconds=3;
                        Handler handler=new Handler();
                        handler.postDelayed(runnable=new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.postDelayed(runnable,1000);
                                        int hours = seconds / 3600;
                                        int minutes = (seconds % 3600) / 60;
                                        int secs = seconds % 60;

                                        if (seconds==0){
                                            added_txt.setVisibility(View.GONE);
                                            handler.removeCallbacks(runnable);
                                        }
                                        else {
                                            added_txt.setVisibility(View.VISIBLE);
                                            added_txt.setText("Playing...");
                                            added_txt.setTextColor(getResources().getColor(R.color.app_logo_green));
                                        }
                                        seconds--;

                                    }
                                },100);

                        try {
                            mp.setDataSource(mFilePath);
                            mp.prepare();
                            mp.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            toolbar_back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });

            submit_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                    msg_on_edit=extra_txt_edit.getText().toString().trim();

                    if (mFilePath.equalsIgnoreCase("")){
                        callSnackBarOnTop(getResources().getString(R.string.voice_msg_toast));
                    }
                    else {
//                        Toast.makeText(VoiceMessageBanaSathiFragment.this, ""+mFilePath, Toast.LENGTH_SHORT).show();

                        PermissionUtils.hideKeyboard(VoiceMessageGajaBandhuActivity.this);//Hide Keyboard

                        dynamicAlertDialog(VoiceMessageGajaBandhuActivity.this,getResources().getString(R.string.save_before_send),
                                getResources().getString(R.string.warning),"Yes");
                         }

                     }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recordVoice(File folder){
        try {
            if(Build.VERSION.SDK_INT >= 29) {

                //only api 29 above
                // Create an image file name

//                String mFileName = "gb_voice"
//                        + "_" + (int) System.currentTimeMillis() + ".mp3";
//                mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                mFilePath += "/WildLife_SoundRecorder/" + mFileName;

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "gb_voice" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File file = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".mp3",         /* suffix */
                        storageDir      /* directory */
                );

                mFilePath = file.getAbsolutePath();


                recordAudio(mFilePath);//Added new 19th july 21

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                added_txt.setVisibility(View.VISIBLE);
                added_txt.setText("Recording...");
                added_txt.setTextColor(getResources().getColor(R.color.chart_orange));

            }else{
                //only api 29 down
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AudioAttributes.Builder builder=new AudioAttributes.Builder();
                    builder.setAllowedCapturePolicy(ALLOW_CAPTURE_BY_SYSTEM);
                }

                String mFileName = "gb_voice"
                        + "_" + (int) System.currentTimeMillis() + ".mp3";
                mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                mFilePath += "/WildLife_SoundRecorder/" + mFileName;

                //start RecordingService
                Intent intent=new Intent(VoiceMessageGajaBandhuActivity.this, RecordingService.class);
                intent.putExtra("filePath",mFilePath);
                startService(intent);
                //keep screen on while recording
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                added_txt.setVisibility(View.VISIBLE);
                added_txt.setText("Recording...");
                added_txt.setTextColor(getResources().getColor(R.color.chart_orange));

            }

//            String mFileName = "gb_voice"
//                    + "_" + (int) System.currentTimeMillis() + ".mp3";
//            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            mFilePath += "/WildLife_SoundRecorder/" + mFileName;
//
//            //start RecordingService
//            Intent intent=new Intent(VoiceMessageGajaBandhuActivity.this, RecordingService.class);
//            intent.putExtra("filePath",mFilePath);
//            startService(intent);
//            //keep screen on while recording
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            added_txt.setVisibility(View.VISIBLE);
//            added_txt.setText("Recording...");
//            added_txt.setTextColor(getResources().getColor(R.color.chart_orange));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopRecordVoice(TextView added_txt){
        try {
            //start RecordingService
            if(Build.VERSION.SDK_INT >= 29) {
                stopRecording();//Added new 19th july 21
            }else {
                //for below 29
                Intent intent=new Intent(VoiceMessageGajaBandhuActivity.this, RecordingService.class);
                stopService(intent);
            }

//            Intent intent=new Intent(VoiceMessageGajaBandhuActivity.this, RecordingService.class);
//            stopService(intent);
            //allow the screen to turn off again once recording is finished
           getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

           added_txt.setVisibility(View.VISIBLE);
           added_txt.setText("Audio Added !");
           added_txt.setTextColor(getResources().getColor(R.color.app_logo_green));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) {
//            finish();
        }

    }


    @Override
    public void onBackPressed() {
        try {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getResources().getString(R.string.exit_msg))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        VoiceMessageGajaBandhuActivity.this.finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingLeftDialogAnimation;
        alertDialog.show();
           }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dynamicAlertDialog(Context context, String message, String title, String buttonNeed){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));

            if (buttonNeed.equalsIgnoreCase("yes")) {
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                fileName=mFilePath;
                                GajaBandhuReportingObj gajaBandhuReportingObj = new GajaBandhuReportingObj(currentDate,userID,msg_on_edit,
                                        ""+latitude,""+longitude,reportType,fileName,folderType);

//                                Toast.makeText(context, "Latitude - "+latitude+", Longitude - "+longitude, Toast.LENGTH_SHORT).show();

                                saveData_inLocal(gajaBandhuReportingObj, getResources().getString(R.string.saved_successfully));
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();


                            }
                        });
            } else {
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
//
            }

            AlertDialog alertDialog = builder.create();
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingLeftDialogAnimation;
            alertDialog.setTitle(title);
            alertDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callSnackBarOnTop(String message){
        try {
            Snackbar snackbar=Snackbar.make(main_ll, message, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
//            params.gravity = Gravity.TOP;
            snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
            view.setLayoutParams(params);
            snackbar.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveData_inLocal(GajaBandhuReportingObj gajaBandhuReportingObj, String message) {
        try {
            dbHelper.open();
            dbHelper.insertGajaBandhuReport(gajaBandhuReportingObj);
            dbHelper.close();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(VoiceMessageGajaBandhuActivity.this, R.style.AlertDialogCustom));
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    VoiceMessageGajaBandhuActivity.this.startActivity(new Intent(VoiceMessageGajaBandhuActivity.this, VoiceMessageGajaBandhuActivity.class));
                    VoiceMessageGajaBandhuActivity.this.finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingLeftDialogAnimation;

            alertDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
     //location code start
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            gps=new GPSTracker(VoiceMessageGajaBandhuActivity.this);

            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(this).equalsIgnoreCase("false")){
//                        googleApiClientHelperClass.buildGoogleApiClient();
                        gps.showSettingsAlert(VoiceMessageGajaBandhuActivity.this);
                        callGoogleApiClient();
                    }else {

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            } else {
                this.canGetLocation = true;

                callGoogleApiClient();


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return location;
                }

                //For request location update from newtwork in onLocation changed
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                //For request location update from googleApiclient in onLocation changed
//                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClientHelperClass.getGoogleApiClient(),
//                        locationRequest, this::onLocationChanged);


            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latdeg(latitude, longitude);
//            Toast.makeText(this, "Lat - "+latitude, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();

                        break;
                    case Activity.RESULT_CANCELED:
//                        settingsrequest();//keep asking if imp or do whatever
//                        Toast.makeText(this, "Location required !", Toast.LENGTH_SHORT).show();
                        break;
                }

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void PermissionGranted(int request_code) {

    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {

    }

    @Override
    public void PermissionDenied(int request_code) {

    }

    @Override
    public void NeverAskAgain(int request_code) {

    }

    public void callGoogleApiClient(){
        try {

            googleApiClientHelperClass = new GoogleApiClientHelperClass(VoiceMessageGajaBandhuActivity.this);
            googleApiClientHelperClass.setConnectionListener(new GoogleApiClientHelperClass.ConnectionListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


                }

                @Override
                public void onConnectionSuspended(int i) {
                    googleApiClientHelperClass.connect();
                }

                @Override
                public void onConnected(Bundle bundle) {
                    if (ActivityCompat.checkSelfPermission(VoiceMessageGajaBandhuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(VoiceMessageGajaBandhuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                    PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions

                        return;

                    }
                    startLocationUpdates();
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClientHelperClass.getGoogleApiClient());

                    if (location == null) {
                        startLocationUpdates();

                    }

                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        DecimalFormat df = new DecimalFormat("#.##");
                        String altitude_=df.format(location.getAltitude());


                        latdeg(latitude, longitude);

//                        Toast.makeText(ElephantReport.this, "Getting location...", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startLocationUpdates() {
        try {

            // Create the location request
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(50000)
                    .setFastestInterval(5000);
            // Request location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClientHelperClass.getGoogleApiClient(),
                    locationRequest, this);
            Log.d("reque", "--->>>>");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void latdeg(double latitude, double longitude) {

        try {

            int d, m, d1, m1;
            double st = 60.0;
            double tt = 3600.0;
            d = (int) latitude;
            double mm = Double.valueOf(d);
            double base = (latitude - mm) * st;
            m = (int) base;
            double nn = Double.valueOf(m);
            double end = (latitude - mm - (nn / st)) * tt;
            latdeg.setText(d + " \u00B0");
            latmin.setText(m + " \u0027 ");
            DecimalFormat df = new DecimalFormat("#.###");
            latsec.setText(df.format(end)+ " \" ");

            d1 = (int) longitude;
            double mm1 = Double.valueOf(d1);
            double base1 = (longitude - mm1) * st;
            m1 = (int) base1;
            double nn1 = Double.valueOf(m1);
            double end1 = (longitude - mm1 - (nn1 / st)) * tt;
            londeg.setText(d1 + " \u00B0");
            lonmin.setText(m1 + " \u0027 ");
            DecimalFormat df1 = new DecimalFormat("#.###");
            lonsec.setText(df1.format(end1)+ " \" ");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (googleApiClientHelperClass.isConnected()){
                googleApiClientHelperClass.disconnect();
            }

            if (mRecorder != null) {
                mRecorder.stop();
                mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
                mRecorder.release();
                mRecorder=null;
            }
            if (mp != null) {
                mp.release();
                mp = null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void recordAudio(String path){
        try {
            mRecorder = new MediaRecorder();

//            mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
            mRecorder.setOutputFile(path);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioChannels(1);

            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    private void recordForAudioAndroid10(){
//        try {
//            MediaMuxer muxer = null;
//            try {
//                muxer = new MediaMuxer("temp.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//            // SetUp Video/Audio Tracks.
//            MediaFormat audioFormat = new MediaFormat();
//            int audioTrackIndex = muxer.addTrack(audioFormat);
//
//            // Setup Metadata Track
//            MediaFormat metadataFormat = new MediaFormat();
//            metadataFormat.setString(KEY_MIME, "application/gyro");
//            int metadataTrackIndex = muxer.addTrack(metadataFormat);
//
//            muxer.start();
//            while() {
//                // Allocate bytebuffer and write gyro data(x,y,z) into it.
//                int bufferSize=0;
//                ByteBuffer metaData = ByteBuffer.allocate(bufferSize);
//                float x=0f,y=0f,z=0f;
//                metaData.putFloat(x);
//                metaData.putFloat(y);
//                metaData.putFloat(z);
//                BufferInfo metaInfo = new BufferInfo() {
//                    @Override
//                    public int length() {
//                        return 0;
//                    }
//
//                    @Override
//                    public int capacity() {
//                        return 0;
//                    }
//
//                    @Override
//                    public int available() {
//                        return 0;
//                    }
//                };
//                // Associate this metadata with the video frame by setting
//                // the same timestamp as the video frame.
//                metaInfo.presentationTimeUs = currentVideoTrackTimeUs;
//                metaInfo.offset = 0;
//                metaInfo.flags = 0;
//                metaInfo.size = bufferSize;
//                muxer.writeSampleData(metadataTrackIndex, metaData, metaInfo);
//            };
//            muxer.stop();
//            muxer.release();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.reset();//added new 20th july
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            mRecorder.release();
            Toast.makeText(VoiceMessageGajaBandhuActivity.this, "Recording Finish" + " " + mFilePath, Toast.LENGTH_LONG).show();

            //remove notification
//            if (mIncrementTimerTask != null) {
//                mIncrementTimerTask.cancel();
//                mIncrementTimerTask = null;
//            }

            mRecorder = null;
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e){
            Log.e(LOG_TAG, "exception", e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mRecorder != null) {
                stopRecording();
            }
            if (mp != null) {
                mp.release();
                mp = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
