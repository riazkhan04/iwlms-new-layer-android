package com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

public class ImageMessageGajaBandhuActivity extends AppCompatActivity implements LocationListener, com.google.android.gms.location.LocationListener,
        PermissionUtils.PermissionResultCallback{

    final static int REQUEST_LOCATION = 199,Camera_REQUEST_CODE=104,IMEI_REQUEST_CODE=111;
    private String pictureImagePath = "";
    TextView submit_txt,toolbar_txt;
    Uri image_uri=null;
    String img_url_str="",currentDate="";
    Bitmap myBitmap;
    ImageView selected_img,imgShow,close_img;
    LinearLayout video_imageLL;
    Toolbar toolbar;
    ImageView profileImg,toolbar_profile_img,toolbar_back_img;
    String msg_on_edit="",userID="",reportType="image",fileName="",folderType="gajabandhu",
            lat_deg="",lat_min="",lat_sec="",lng_degree="",lng_min="",lng_sec="",altitude="",accuracy="";
    EditText extra_txt_edit;
    ScrollView main_ll;
    private RunTimePermission runTimePermission;
    SessionManager session;
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
    GoogleApiClientHelperClass googleApiClientHelperClass;
    TextInputEditText latdeg,latmin,latsec,londeg,lonmin,lonsec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_file_layout_banasathi);
        
        initData();

        clickFunction();
        
    }

    private void initData() {
        try {

            submit_txt=findViewById(R.id.submit_txt);
            selected_img=findViewById(R.id.selected_img);
            imgShow=findViewById(R.id.imgShow);
            close_img=findViewById(R.id.close_img);
            video_imageLL=findViewById(R.id.video_imageLL);
            main_ll=findViewById(R.id.main_ll);
            extra_txt_edit=findViewById(R.id.extra_txt_edit);
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
            session=new SessionManager(ImageMessageGajaBandhuActivity.this);
            dbHelper=new GajaBandhuDbHelper(ImageMessageGajaBandhuActivity.this);

            toolbar_profile_img.setVisibility(View.GONE);
            toolbar_back_img.setVisibility(View.VISIBLE);
            toolbar_txt.setText("Image Message");

            permissionUtils = new PermissionUtils(ImageMessageGajaBandhuActivity.this);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

            extra_txt_edit.clearFocus();
            PermissionUtils.hideKeyboard(ImageMessageGajaBandhuActivity.this);

            getLocation();
            permissionHelperClass = new PermissionHelperClass(ImageMessageGajaBandhuActivity.this);
            if (permissionHelperClass.hasLocationPermission()) {

                callGoogleApiClient();
            } else {
                permissionHelperClass.requestLocationPermission();//request location permissions
            }

            currentDate=PermissionUtils.getCurrentDate("dd-MM-yyyy HH:mm:ss");
//            userID="7978328829";
            userID=session.getGajaBandhu_UserId();

            runTimePermission = new RunTimePermission(ImageMessageGajaBandhuActivity.this);

            runTimePermission.requestPermission(new String[]{
                            Manifest.permission.CAMERA,
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void clickFunction() {
        try {

            toolbar_back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });

            selected_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openBackCamera();
                }
            });
            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    img_url_str="";
                    video_imageLL.setVisibility(View.GONE);
                }
            });

            submit_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    msg_on_edit=extra_txt_edit.getText().toString().trim();

                    if (img_url_str.equalsIgnoreCase("")){
                        callSnackBarOnTop(getResources().getString(R.string.capture_image_toast));
//                        Toast.makeText(ChooseFileBanaSathiFragment.this, "Latitude - "+latitude+", Longitude - "+longitude, Toast.LENGTH_SHORT).show();
                    }else {
                        //call method to save data !
//                        callSnackBarOnTop(img_url_str);

                        PermissionUtils.hideKeyboard(ImageMessageGajaBandhuActivity.this);//Hide Keyboard

                        dynamicAlertDialog(ImageMessageGajaBandhuActivity.this,getResources().getString(R.string.save_before_send),
                                getResources().getString(R.string.warning),"Yes");

                    }
                }
            });





        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openBackCamera() {
        try {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            if(Build.VERSION.SDK_INT >= 29) {
                //only api 29 above
                // Create an image file name
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File file = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );

                // Save a file: path for use with ACTION_VIEW intents
                pictureImagePath = file.getAbsolutePath();

                Uri outputFileUri = FileProvider.getUriForFile(ImageMessageGajaBandhuActivity.this,
                        getApplicationContext().getPackageName() + ".provider", file);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                //COMPATIBILITY
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
                    cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                startActivityForResult(cameraIntent, Camera_REQUEST_CODE);


            }else{
                //only api 29 down
                String imageFileName = timeStamp + ".jpg";

                String rootPath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/WildLifeAppImages/";

                File root = new File(rootPath);
                if (!root.exists()) {
                    root.mkdirs();
                }

                pictureImagePath=  rootPath + imageFileName;
                File file = new File(pictureImagePath);
                Uri outputFileUri = FileProvider.getUriForFile(ImageMessageGajaBandhuActivity.this,
                        getApplicationContext().getPackageName() + ".provider", file);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                //COMPATIBILITY
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
                    cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                startActivityForResult(cameraIntent, Camera_REQUEST_CODE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showCameraDialog(Context context){
    try {
        Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
        dialog.setContentView(R.layout.camera_dialog);

        ImageView close_img;
        LinearLayout camera_ll,gallery_ll;
        close_img=dialog.findViewById(R.id.close_img);
        camera_ll=dialog.findViewById(R.id.camera_ll);
        gallery_ll=dialog.findViewById(R.id.gallery_ll);


        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        camera_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                openBackCamera();
            }
        });
        gallery_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                onBackPressed();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }catch (Exception e){
        e.printStackTrace();
    }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case RESULT_OK:
                        startLocationUpdates();

                        break;
                    case RESULT_CANCELED:
//                        settingsrequest();//keep asking if imp or do whatever
//                        Toast.makeText(this, "Location required !", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case Camera_REQUEST_CODE:

                if (requestCode == Camera_REQUEST_CODE) {
                    try {

                        if (resultCode == RESULT_OK) {
//                            Bitmap bp = (Bitmap) data.getExtras().get("data");
////                            camera_img.setImageBitmap(bp);
//                            image_uri=getImageUri(IncidentReportingActivity.this,bp);
//                            img_url_str=getRealPathFromURI(image_uri);
//                            Toast.makeText(this, ""+img_url_str, Toast.LENGTH_SHORT).show();
                            String filenm="";
                            File imgFile = new  File(pictureImagePath);
                            if(imgFile.exists()){
                                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

//                                selected_img.setImageBitmap(myBitmap);
                                video_imageLL.setVisibility(View.VISIBLE);
//                                imgShow.setImageBitmap(myBitmap);

                                image_uri=getImageUri(ImageMessageGajaBandhuActivity.this,myBitmap);
                                img_url_str=getRealPathFromURI(image_uri);

                                filenm=pictureImagePath.replace("/storage/emulated/0/WildLifeAppImages/","");

                                //Call camera geotag dialog
                                callCameraGeotagDialog(ImageMessageGajaBandhuActivity.this,myBitmap,latitude,longitude,
                                        altitude,accuracy,pictureImagePath,filenm);

                            }

                            //Call camera geotag dialog
//                            callCameraGeotagDialog(getActivity(),myBitmap,latitude,longitude,
//                                    altitude,accuracy,pictureImagePath,filenm);


                        } else if (resultCode == RESULT_CANCELED) {
                            Toast.makeText(ImageMessageGajaBandhuActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "animal_info", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getResources().getString(R.string.exit_msg))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ImageMessageGajaBandhuActivity.this.finish();
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

                                fileName=img_url_str;
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

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ImageMessageGajaBandhuActivity.this, R.style.AlertDialogCustom));
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ImageMessageGajaBandhuActivity.this.startActivity(new Intent(ImageMessageGajaBandhuActivity.this, ImageMessageGajaBandhuActivity.class));
                    ImageMessageGajaBandhuActivity.this.finish();
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

            gps=new GPSTracker(ImageMessageGajaBandhuActivity.this);

            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(this).equalsIgnoreCase("false")){
//                        googleApiClientHelperClass.buildGoogleApiClient();
                        gps.showSettingsAlert(ImageMessageGajaBandhuActivity.this);
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

            googleApiClientHelperClass = new GoogleApiClientHelperClass(ImageMessageGajaBandhuActivity.this);
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
                    if (ActivityCompat.checkSelfPermission(ImageMessageGajaBandhuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ImageMessageGajaBandhuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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

                        altitude=""+location.getAltitude();
                        accuracy=""+location.getAccuracy();

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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callCameraGeotagDialog(Context context,Bitmap bitmap,
                                        Double latitude,Double longitude,
                                        String altitude,String accuracy,
                                        String pictureImagePath,String filenm){
        try {
            Dialog dialog=new Dialog(context,R.style.SplashViewTheme);
//            Dialog dialog=new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.camera_geotag_layout);

            ImageView geo_img=dialog.findViewById(R.id.geo_image);
            ImageView close_img=dialog.findViewById(R.id.close_img);
            TextView lat_lng_txt=dialog.findViewById(R.id.lat_lng_txt);
            TextView time_stamp=dialog.findViewById(R.id.timestamp);
            TextView altitude_txt=dialog.findViewById(R.id.altitude_txt);
            TextView accuracy_txt=dialog.findViewById(R.id.accuracy_txt);
            CardView ok_cv=dialog.findViewById(R.id.ok_cv);
            RelativeLayout geo_RL=dialog.findViewById(R.id.geo_RL);

            Date date = new Date();
            String stringDate = DateFormat.getDateTimeInstance().format(date);

            time_stamp.setText(stringDate);
            altitude_txt.setText("Alt - "+altitude);
            accuracy_txt.setText("Accu - "+accuracy);
            lat_lng_txt.setText("Latitude-"+latitude+",\nLongitude-"+longitude);

            geo_img.setImageBitmap(bitmap);

            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            ok_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        dialog.dismiss();
//                    camera_img.setImageBitmap(bitmap);

                        geo_RL.setDrawingCacheEnabled(true);
                        int totalHeight = geo_RL.getHeight();
                        int totalWidth = geo_RL.getWidth();
                        geo_RL.layout(0, 0, totalWidth, totalHeight);
                        geo_RL.buildDrawingCache(true);
                        Bitmap bm = Bitmap.createBitmap(geo_RL.getDrawingCache());
                        geo_RL.setDrawingCacheEnabled(false);
//                    Toast.makeText(IncidentReportingActivity.this, "Taking Screenshot", Toast.LENGTH_SHORT).show();
                        MediaStore.Images.Media.insertImage(getContentResolver(), bm, null, null);

                        image_uri=getImageUri(ImageMessageGajaBandhuActivity.this,bm);
//                   String img_url_str=getEncoded64ImageStringFromBitmap(bp);
                        img_url_str=getRealPathFromURI(image_uri);

                        imgShow.setImageBitmap(bm);//Set image in imageView

                        replaceFile(pictureImagePath,filenm,bm);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void replaceFile(String pictureImagePath,String filenm,Bitmap bm) {
        try {

            String rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/WildLifeAppImages/";
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }

            pictureImagePath=  rootPath + filenm;
//        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
            File file = new File(pictureImagePath);

            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
