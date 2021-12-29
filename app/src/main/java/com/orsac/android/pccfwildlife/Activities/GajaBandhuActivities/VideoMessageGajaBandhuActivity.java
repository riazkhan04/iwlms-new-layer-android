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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orsac.android.pccfwildlife.CameraClass.RunTimePermission;
import com.orsac.android.pccfwildlife.CameraClass.WhatsappCameraActivity;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SQLiteDB.GajaBandhuDbHelper;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuReportingObj;
import com.orsac.android.pccfwildlife.MyUtils.GPSTracker;
import com.orsac.android.pccfwildlife.MyUtils.GoogleApiClientHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

public class VideoMessageGajaBandhuActivity extends AppCompatActivity implements LocationListener, com.google.android.gms.location.LocationListener,
        PermissionUtils.PermissionResultCallback{

    CardView recordCV;
    EditText extra_txt_edit;
    TextView submit_txt,toolbar_txt;
    ImageView close_img,close_video,play_img,pause_img;
    VideoView videoView;
    private RunTimePermission runTimePermission;
    public static String videoPath="";
    Toolbar toolbar;
    ImageView profileImg,toolbar_profile_img,toolbar_back_img;
    LinearLayout video_imageLL;
    ScrollView main_ll;
    SessionManager session;
    String msg_on_edit="",userID="",reportType="video",fileName="",folderType="gajabandhu",currentDate="",
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
    final static int REQUEST_LOCATION = 199;
    GoogleApiClientHelperClass googleApiClientHelperClass;
    TextInputEditText latdeg,latmin,latsec,londeg,lonmin,lonsec;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_msg_gaja_bandhu_layout);

        try {

            initData();

            videoPath="";

            recordCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    runTimePermission = new RunTimePermission(VideoMessageGajaBandhuActivity.this);
                    runTimePermission.requestPermission(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, new RunTimePermission.RunTimePermissionListener() {

                        @Override
                        public void permissionGranted() {
                            // First we need to check availability of play services
                            startActivity(new Intent(VideoMessageGajaBandhuActivity.this, WhatsappCameraActivity.class));
//                            finish();

                        }

                        @Override
                        public void permissionDenied() {

                            finish();
                        }
                    });


                }
            });

            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()){
                        videoView.pause();
                        pause_img.setVisibility(View.VISIBLE);
                        play_img.setVisibility(View.GONE);
                    }else {
                        videoView.start();
                        play_img.setVisibility(View.GONE);
                        pause_img.setVisibility(View.GONE);
                    }



                }
            });

            if (!videoPath.equalsIgnoreCase("")) {
                videoView.setVisibility(View.VISIBLE);
                video_imageLL.setVisibility(View.VISIBLE);
                try {
                    videoView.setMediaController(null);
                    videoView.setVideoURI(Uri.parse(videoPath));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                videoView.requestFocus();
                //videoView.setZOrderOnTop(true);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        play_img.setVisibility(View.GONE);
                        videoView.start();
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        play_img.setVisibility(View.VISIBLE);
                        videoView.start();
                    }
                });

            }

            submit_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                    msg_on_edit=extra_txt_edit.getText().toString().trim();

                    if (videoPath.equalsIgnoreCase("")){
                        callSnackBarOnTop(getResources().getString(R.string.video_msg_toast));
//                        Toast.makeText(VideoMessageBanaSathiFragment.this, "Lat-"+latitude, Toast.LENGTH_SHORT).show();
                    }else {
//                        Toast.makeText(VideoMessageBanaSathiFragment.this, ""+videoPath, Toast.LENGTH_SHORT).show();

                        PermissionUtils.hideKeyboard(VideoMessageGajaBandhuActivity.this);//Hide Keyboard

                        dynamicAlertDialog(VideoMessageGajaBandhuActivity.this,getResources().getString(R.string.save_before_send),
                                getResources().getString(R.string.warning),"Yes");
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

            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    video_imageLL.setVisibility(View.GONE);
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initData() {

        try {

            recordCV=findViewById(R.id.recordCV);
            extra_txt_edit=findViewById(R.id.extra_txt_edit);
            submit_txt=findViewById(R.id.submit_txt);
            close_img=findViewById(R.id.close_img);
            close_video=findViewById(R.id.close_img);
            play_img=findViewById(R.id.play_img);
            pause_img=findViewById(R.id.pause_img);
            videoView=findViewById(R.id.vidShow);
            video_imageLL=findViewById(R.id.video_imageLL);
            main_ll=findViewById(R.id.main_ll);
            latdeg=findViewById(R.id.latdeg);
            latmin=findViewById(R.id.latmin);
            latsec=findViewById(R.id.latsec);
            londeg=findViewById(R.id.londeg);
            lonmin=findViewById(R.id.lonmin);
            lonsec=findViewById(R.id.lonsec);

            toolbar = findViewById(R.id.toolbar_new_id);
            toolbar_profile_img = toolbar.findViewById(R.id.profile_img);
            toolbar_back_img = toolbar.findViewById(R.id.back_img);
            toolbar_txt = toolbar.findViewById(R.id.toolbar_txt);

            session=new SessionManager(VideoMessageGajaBandhuActivity.this);
            dbHelper=new GajaBandhuDbHelper(VideoMessageGajaBandhuActivity.this);

            toolbar_profile_img.setVisibility(View.GONE);
            toolbar_back_img.setVisibility(View.VISIBLE);
            toolbar_txt.setText("Video Message");
            permissionUtils = new PermissionUtils(VideoMessageGajaBandhuActivity.this);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

            extra_txt_edit.clearFocus();
            PermissionUtils.hideKeyboard(VideoMessageGajaBandhuActivity.this);

            getLocation();
            permissionHelperClass = new PermissionHelperClass(VideoMessageGajaBandhuActivity.this);
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


    @Override
    protected void onResume() {
        super.onResume();
        try {

        if (!videoPath.equalsIgnoreCase("")){
            videoView.setVisibility(View.VISIBLE);
            video_imageLL.setVisibility(View.VISIBLE);
            try {
                videoView.setMediaController(null);
                videoView.setVideoURI(Uri.parse(videoPath));

            } catch (Exception e) {
                e.printStackTrace();
            }
            videoView.requestFocus();
//            play_img.setVisibility(View.VISIBLE);
            //videoView.setZOrderOnTop(true);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    play_img.setVisibility(View.GONE);
                    pause_img.setVisibility(View.GONE);
                    videoView.start();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.pause();
                    pause_img.setVisibility(View.GONE);
                    play_img.setVisibility(View.VISIBLE);
                }
            });
        }

        }catch (Exception e){
            e.printStackTrace();
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
                        VideoMessageGajaBandhuActivity.this.finish();
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

                                fileName=videoPath;
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

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(VideoMessageGajaBandhuActivity.this, R.style.AlertDialogCustom));
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    VideoMessageGajaBandhuActivity.this.startActivity(new Intent(VideoMessageGajaBandhuActivity.this, VideoMessageGajaBandhuActivity.class));
                    VideoMessageGajaBandhuActivity.this.finish();
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

            gps=new GPSTracker(VideoMessageGajaBandhuActivity.this);

            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(this).equalsIgnoreCase("false")){
//                        googleApiClientHelperClass.buildGoogleApiClient();
                        gps.showSettingsAlert(VideoMessageGajaBandhuActivity.this);
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

            googleApiClientHelperClass = new GoogleApiClientHelperClass(VideoMessageGajaBandhuActivity.this);
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
                    if (ActivityCompat.checkSelfPermission(VideoMessageGajaBandhuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(VideoMessageGajaBandhuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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
    public void onStop() {
        super.onStop();
        try {
            if (videoView.isPlaying()) {
                videoView.pause();
            }

            if (googleApiClientHelperClass.isConnected()){
                googleApiClientHelperClass.disconnect();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
