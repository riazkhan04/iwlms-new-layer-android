package com.orsac.android.pccfwildlife.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.SphericalUtil;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.LoginActivityNewGajaBandhu;
import com.orsac.android.pccfwildlife.Contract.DashboardContract;
import com.orsac.android.pccfwildlife.Model.ChangePasswordModel.ChangePasswordRequestModel;
import com.orsac.android.pccfwildlife.Model.ChangePasswordModel.ChangeUserNameModel;
import com.orsac.android.pccfwildlife.Model.SelectionReportModel;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Adapters.SelectionReportAdapter;
import com.orsac.android.pccfwildlife.Model.AllLatLngData;
import com.orsac.android.pccfwildlife.Model.AllLatLngModel;
import com.orsac.android.pccfwildlife.Model.ProfileModel.SuccessResponseData;
import com.orsac.android.pccfwildlife.MyUtils.GPSTracker;
import com.orsac.android.pccfwildlife.MyUtils.GoogleApiClientHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.GpsUtils;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.Presenter.DashboardPresenter;
import com.orsac.android.pccfwildlife.SQLiteDB.DBhelper;
import com.orsac.android.pccfwildlife.Services.NotificationCancelReceiver;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class SelectionPage extends AppCompatActivity
        implements SelectionReportAdapter.OnItemSelection , DashboardContract.view,
        LocationListener, com.google.android.gms.location.LocationListener, PermissionUtils.PermissionResultCallback,onLatitude {

    RecyclerView selection_recyclerV;
    SelectionReportAdapter selectionReportAdapter;
    private ArrayList<SelectionReportModel> selectionReportArr;
    public RecyclerView.LayoutManager layoutManager;
    SessionManager session;
    String division_nm="",userId="",userNm="",roles="",roleID="",alreadyNoitfyCall="",guestFlag="";
    TextView welcome_txt,iwlms_txt;
    ImageView logout_icon;
    DashboardContract.presenter dashboard_presenter;
    PopupMenu popup;
    PermissionUtils permissionUtils;
    ArrayList<String> permissions = new ArrayList<>();
    GoogleApiClientHelperClass googleApiClientHelperClass;
    final static int REQUEST_LOCATION = 199;
    Location location;
    double latitude;
    double longitude;
    PermissionHelperClass permissionHelperClass;
    public LocationRequest locationRequest;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;// for 3sec
    GPSTracker gps;
    DBhelper dBhelper;
    AllLatLngData allLatLngData;
    SQLiteDatabase db;
    long distance=0L;
    RelativeLayout main_RL;
    public static int notificationId = 1;
    NotificationManager notificationManager=null;
    ImageView close_img;
    AppCompatButton submit;
    TextInputEditText old_password,new_password,confirm_password,old_userid,new_userid,confirm_userid;
    String oldPwd="",newPwd="",confirmPwd="",oldUserId="",newUserId="",confirmUserId="";
    LinearLayout loading_ll;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            setContentView(R.layout.selection_page);

            initData();

            click_function();

        }catch (Exception e){
            e.printStackTrace();
        }

//        getAllLatLngFromLocal();
    }


    public void initData(){

        try {

        selectionReportArr=new ArrayList<>();
        selection_recyclerV=findViewById(R.id.selection_recyclerV);
        welcome_txt=findViewById(R.id.welcome_txt);
        iwlms_txt=findViewById(R.id.iwlms_txt);
        logout_icon=findViewById(R.id.logout_icon);
        main_RL=findViewById(R.id.main_RL);
        dBhelper=new DBhelper(SelectionPage.this);
        callSelectionReport_Adapter("");

        session=new SessionManager(SelectionPage.this);
        dashboard_presenter=new DashboardPresenter(SelectionPage.this,this,SelectionPage.this);
        division_nm=session.getDivision();
        userId=session.getUserID();
        userNm=session.getUserName();
        roles=session.getRoles();
        roleID=session.getRoleId();

        guestFlag=getIntent().getStringExtra("guestReporting");

        welcome_txt.setText("Welcome  "+userNm);

        Animation animation= AnimationUtils.loadAnimation(SelectionPage.this,R.anim.push_up_in_anim);
        iwlms_txt.startAnimation(animation);

        permissionHelperClass = new PermissionHelperClass(SelectionPage.this);
        permissionUtils = new PermissionUtils(SelectionPage.this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

            //for not showing the selection page for dfo and higher officer
            if (!roleID.equalsIgnoreCase("4") && !roleID.equalsIgnoreCase("5") &&
                    !roleID.equalsIgnoreCase("7") && !roleID.equalsIgnoreCase("8") &&
                    !roleID.equalsIgnoreCase("9") && !roleID.equalsIgnoreCase("Default")){

                Intent intent = new Intent(SelectionPage.this, DashboardMonitoringActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                //for getting location and update in notification for ranger and below
                if (permissionHelperClass.hasLocationPermission()) {
                    callGoogleApiClient();

                } else {
                    permissionHelperClass.requestLocationPermission();//request location permissions

                }

                getLocation();
                    //comment for now 25th nov 2021
//                if (PermissionUtils.check_InternetConnection(SelectionPage.this) == "true") {
//                    checkForAllLatLng();//call for choose local data or to call api
//
//                }else {
//                    getAllLatLngFromLocal(); //When no internet
//                    Snackbar.make(main_RL, "No Internet Connection !", Snackbar.LENGTH_SHORT).show();
//                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void click_function() {
        try {

            logout_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    popup = new PopupMenu(SelectionPage.this, view);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
//                setForceShowIcon(popup);
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.nav_profile:

                                    Intent profile_intent=new Intent(SelectionPage.this,ProfileActivity.class);
                                    startActivity(profile_intent);

                                    break;
                                case R.id.changePwd:
//                                        Toast.makeText(Dashboard_nw.this, "Change password !", Toast.LENGTH_SHORT).show();
                                    callChangePasswordDialog(SelectionPage.this);
                                    break;

                                case R.id.changeUserId:
//                                        Toast.makeText(Dashboard_nw.this, "Change UserId !", Toast.LENGTH_SHORT).show();
                                    callChangeUserIdDialog(SelectionPage.this);
                                    break;

                                case R.id.nav_logout:
//                                call_logout(Dashboard_nw.this,"Do you want to logout the app ?","Logout");
                                    dashboard_presenter.call_logout(SelectionPage.this,"Do you want to logout the app ?","Logout",session);
                                    break;

                                default:
                                    break;
                            }
                            return true;

                        }

                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callSelectionReport_Adapter(String division_name) {

        try {

            selection_recyclerV.setHasFixedSize(true);
            selection_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview

            layoutManager = new GridLayoutManager(SelectionPage.this,2);
//        layoutManager = new LinearLayoutManager(getActivity());
            selection_recyclerV.setLayoutManager(layoutManager);

            selectionReportArr.add(new SelectionReportModel("Reporting", "0", R.drawable.reporting));
            selectionReportArr.add(new SelectionReportModel("Monitoring", "1", R.drawable.monitoring));


            selectionReportAdapter = new SelectionReportAdapter(SelectionPage.this, selectionReportArr,this::onItem_selected);
            selection_recyclerV.setAdapter(selectionReportAdapter);
            selectionReportAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onItem_selected(String item_name, int position, CardView cview) {
    try {

        if (position==0){//Reporting
            if (roleID.equalsIgnoreCase("4")||roleID.equalsIgnoreCase("5")||
                roleID.equalsIgnoreCase("7")||roleID.equalsIgnoreCase("8")||
                roleID.equalsIgnoreCase("9")){

                Intent intent = new Intent(SelectionPage.this, Dashboard_nw.class);
//                    Intent intent = new Intent(LoginActivity.this, Dashboard_nw.class);
                intent.putExtra("guestReporting","login");
                startActivity(intent);
                finish();
            }
            else {
                Snackbar.make(main_RL, "You are not authorized to access !", Snackbar.LENGTH_SHORT).show();
            }
        }
        else {//Monitoring

            if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("2")
                    || roleID.equalsIgnoreCase("3")||roleID.equalsIgnoreCase("6")){

                Intent intent = new Intent(SelectionPage.this, DashboardMonitoringActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                //For Ranger Monitoring is not available
                Snackbar.make(main_RL, "You are not authorized to access !", Snackbar.LENGTH_SHORT).show();
//            finish();
            }

        }


        }catch (Exception e){
        e.printStackTrace();
     }


    }


    @Override
    public void show_progress() {

    }

    @Override
    public void hide_progress() {

    }

    @Override
    public void success(String message) {

    }

    @Override
    public void failed(String message) {

    }

    //location code start
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            gps=new GPSTracker(SelectionPage.this);

            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(this).equalsIgnoreCase("false")){
//                        googleApiClientHelperClass.buildGoogleApiClient();
                        gps.showSettingsAlert(SelectionPage.this);
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
//            Toast.makeText(this, "Lat - "+latitude, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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

    public void callGoogleApiClient(){
        try {

            googleApiClientHelperClass = new GoogleApiClientHelperClass(SelectionPage.this);
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
                    if (ActivityCompat.checkSelfPermission(SelectionPage.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(SelectionPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        //comment for now 25th nov 2021
//                        if (!alreadyNoitfyCall.equalsIgnoreCase("done")){
//                            //commented below code and add callAllLAtLngIn24Hrs() in oct 20
//                            getlatLong(latitude,longitude);//interface used to get lat long
////                            callAllLAtLngIn24Hrs();//Its working  oct 22 comment for now
//                        }

//                        Toast.makeText(SelectionPage.this, "Getting location..."+latitude, Toast.LENGTH_SHORT).show();
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
//                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
//                        settingsrequest();//keep asking if imp or do whatever
//                        Toast.makeText(this, "Location required !", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
      }

    @Override
    public void getlatLong(double lat, double lng) {
        //It will call when lat lng changes
        try {
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * from "+ DBhelper.ALL_LAT_LNG_TABLE_NAME, null);
//            Cursor c = db.rawQuery("SELECT * from "+dBhelper.ALL_LAT_LNG_TABLE_NAME+ " where sync_status='0'" , null);
            int count = c.getCount();
            if (count >= 1) {
                c.moveToFirst();
                if (c.moveToFirst()) {
                    do {
                        final String surveyKey = c.getString(c.getColumnIndex("slno"));

//                        String lat_lng = c.getString(c.getColumnIndex("lat_lng"));
                        String saved_date = c.getString(c.getColumnIndex("date"));
                        String currentDate = PermissionUtils.getCurrentDate("dd-MM-yyyy");
                        if (!saved_date.equalsIgnoreCase(currentDate)) {
                            //call api
                            db.delete(DBhelper.ALL_LAT_LNG_TABLE_NAME,null, null);
                            callAllLAtLngIn24Hrs();
                        } else {
                            getAllLatLngFromLocal();//call from local
                        }
                    }while (c.moveToNext());
                }
            }
            else {
                callAllLAtLngIn24Hrs();//first time call
            }

        }catch (Exception e){
            e.printStackTrace();
        }

//        getAllLatLngFromLocal();
    }


    public  Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2)/1000;
    }

    public void callAllLAtLngIn24Hrs(){
        try {
            RetrofitInterface retrofitInterface= RetrofitClient.getReportClient().create(RetrofitInterface.class);

            retrofitInterface.get_allLatlngIn24hrs().enqueue(new Callback<ArrayList<AllLatLngModel>>() {
                @Override
                public void onResponse(Call<ArrayList<AllLatLngModel>> call, Response<ArrayList<AllLatLngModel>> response) {
                    if (response.isSuccessful()){
                        if (response.body().isEmpty()){

                        }else {
                            ArrayList<AllLatLngData> allLatlngArr=new ArrayList<>();
                            for (int i=0;i<response.body().size();i++){

                                allLatLngData=new AllLatLngData(response.body().get(i).getLatitude(),
                                                                response.body().get(i).getLongitude());
                                allLatlngArr.add(allLatLngData);
                            }
                            dBhelper=new DBhelper(SelectionPage.this);
                            dBhelper.open();
                            dBhelper.insertLatlng(allLatlngArr,PermissionUtils.getCurrentDate("dd-MM-yyyy"));
                            dBhelper.close();

                            if(latitude == 0.0 ) {
                              callGoogleApiClient();
                            }else{
                                getAllLatLngFromLocal();
                            }

                        }

                    }else {
                        Toast.makeText(SelectionPage.this, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<AllLatLngModel>> call, Throwable throwable) {
                    Toast.makeText(SelectionPage.this, "Failed..Please try again !", Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getAllLatLngFromLocal(){
        try {
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * from "+ DBhelper.ALL_LAT_LNG_TABLE_NAME, null);
//            Cursor c = db.rawQuery("SELECT * from "+dBhelper.ALL_LAT_LNG_TABLE_NAME+ " where sync_status='0'" , null);
            int count = c.getCount();
            if (count >= 1) {
                c.moveToFirst();
                if (c.moveToFirst()) {
                    do {
                        final String surveyKey = c.getString(c.getColumnIndex("slno"));
                        String saved_date = c.getString(c.getColumnIndex("date"));
                        String currentDate = PermissionUtils.getCurrentDate("dd-MM-yyyy");
                        if (!saved_date.equalsIgnoreCase(currentDate)) {
                            db.delete(DBhelper.ALL_LAT_LNG_TABLE_NAME,null, null);
                        }
                        else{
                            //when saved date is same with current date
                            byte[] blob = c.getBlob(c.getColumnIndex("lat_lng"));
                            String json = new String(blob);
                            Gson gson = new Gson();
                            ArrayList<AllLatLngData> allLatlngArr = new ArrayList<>();
                            allLatlngArr.clear();
                            allLatlngArr=gson.fromJson(json, new TypeToken<ArrayList<AllLatLngData>>()
                            {}.getType());

                            for (int i=0;i<allLatlngArr.size();i++){
//                            Toast.makeText(SelectionPage.this, ""+allLatLngDataArr.get(i).toString(), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(SelectionPage.this, "Getting location..."+latitude, Toast.LENGTH_SHORT).show();
                                Log.i("latitude",""+latitude);
                                if (latitude!=0.0){
                                    distance=Math.round(distanceBetween(
                                            new LatLng(latitude,longitude),
                                            new LatLng(Double.parseDouble(allLatlngArr.get(i).getLatitude()),
                                                    Double.parseDouble(allLatlngArr.get(i).getLongitude()))
                                    ));
                                }else {
                                    callGoogleApiClient();
                                }

                                if (distance<=2){
                                    if (distance==0){
                                        showNotification(SelectionPage.this, "Elephant Alert !",
                                                "You are in between 1 km range from elephant sighting.Be aware!", getIntent());
                                        alreadyNoitfyCall="done";
                                        break;
                                    }else if (distance==1){
                                        showNotification(SelectionPage.this, "Elephant Alert !",
                                                "You are near " + distance + " km range of elephant sighting.Be aware!", getIntent());
                                        alreadyNoitfyCall="done";
                                        break;
                                    }else if (distance==2){
                                        showNotification(SelectionPage.this, "Elephant Alert !",
                                                "You are near " + distance + " km range of elephant sighting.Be aware!", getIntent());
                                        alreadyNoitfyCall="done";
                                        break;
                                    }
                                    break;
                                }

                                else {
                                    break;
                                }
//                            break;
                            }
                        }

                    } while (c.moveToNext());

                }
            }else {

            }
            c.close();
            db.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkForAllLatLng(){
        try {
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * from "+ DBhelper.ALL_LAT_LNG_TABLE_NAME, null);
//            Cursor c = db.rawQuery("SELECT * from "+dBhelper.ALL_LAT_LNG_TABLE_NAME+ " where sync_status='0'" , null);
            int count = c.getCount();
            if (count >= 1) {
                c.moveToFirst();
                if (c.moveToFirst()) {
                    do {
                        final String surveyKey = c.getString(c.getColumnIndex("slno"));

//                        String lat_lng = c.getString(c.getColumnIndex("lat_lng"));
                        String saved_date = c.getString(c.getColumnIndex("date"));
                        String currentDate = PermissionUtils.getCurrentDate("dd-MM-yyyy");
                        if (!saved_date.equalsIgnoreCase(currentDate)) {
                            //call api
                            callAllLAtLngIn24Hrs();
                        } else {
                            getAllLatLngFromLocal();//call from local
                        }
                    }while (c.moveToNext());
                }
            }
            else {
                callAllLAtLngIn24Hrs();//first time call
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showNotification(Context context, String title, String body, Intent intent) {
        try {

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            RemoteViews remoteViews;

//            int notificationId = 1;
            String channelId = "Wildlife_notify_channel";
//            String CHANNEL_ID = String.valueOf(System.currentTimeMillis());
            String channelName = title;
            int importance = NotificationManager.IMPORTANCE_HIGH;

            Notification notification= new Notification();

            Uri defaultSoundUri = Uri.parse(String.valueOf(Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.alarm_warning)));
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            long[] pattern = {500,500,500,500,500,500,500,500,500};

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                mChannel.enableVibration(true);
                mChannel.setSound(defaultSoundUri, attributes);
                notificationManager.createNotificationChannel(mChannel);
            }
            Intent closeButton;
            closeButton=create_intent("cancel");
            closeButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            // notification's layout
            remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
            remoteViews.setImageViewResource(R.id.notification_icon, R.drawable.logo);
            remoteViews.setTextViewText(R.id.notification_title, title);
            remoteViews.setTextViewText(R.id.notification_body, body);
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, closeButton, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setOnClickPendingIntent(R.id.cancel_txt, pendingSwitchIntent);



            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true)
                    .setVibrate(pattern)
                    .setSound(defaultSoundUri)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContent(remoteViews);

            notificationManager.notify(notificationId, mBuilder.build());

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private Intent create_intent(String okOrcancel) {

        Intent intent = new Intent(SelectionPage.this, NotificationCancelReceiver.class);
        intent.putExtra("cancel", okOrcancel);
        return intent;

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (googleApiClientHelperClass != null) {
                googleApiClientHelperClass.connect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
//            permissionHelperClass = new PermissionHelperClass(SelectionPage.this);
//            if (permissionHelperClass.hasLocationPermission()) {
//
//                callGoogleApiClient();
//            } else {
//                permissionHelperClass.requestLocationPermission();//request location permissions
//            }
//            getLocation();

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            notificationManager=(NotificationManager)getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            notificationManager=(NotificationManager)getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            googleApiClientHelperClass.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            notificationManager=(NotificationManager)getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            googleApiClientHelperClass.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callChangePasswordDialog(Context context){
        try {
            Dialog changePwdDialog=new Dialog(context);
            changePwdDialog.setCancelable(false);
            changePwdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            changePwdDialog.setContentView(R.layout.change_password_dialog);

            close_img=changePwdDialog.findViewById(R.id.close_img);
            submit=changePwdDialog.findViewById(R.id.submit);
            old_password=changePwdDialog.findViewById(R.id.old_password);
            new_password=changePwdDialog.findViewById(R.id.new_password);
            confirm_password=changePwdDialog.findViewById(R.id.confirm_password);
            loading_ll=changePwdDialog.findViewById(R.id.loading_ll);


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePwdDialog.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oldPwd=old_password.getText().toString().trim();
                    newPwd=new_password.getText().toString().trim();
                    confirmPwd=confirm_password.getText().toString().trim();

                    if (oldPwd.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter old password !", Toast.LENGTH_SHORT).show();
                    }
                    else if (newPwd.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter new password !", Toast.LENGTH_SHORT).show();
                    }
                    else if (confirmPwd.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter confirm password !", Toast.LENGTH_SHORT).show();
                    }
                    else if (newPwd.length()<=7 || confirmPwd.length()<=7){
                        Toast.makeText(context, "Password must be minimum 8 characters !", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if (newPwd.equalsIgnoreCase(confirmPwd)){

                            callChangePasswordApi(userNm,oldPwd,newPwd,SelectionPage.this,
                                    changePwdDialog,loading_ll);
                        }
                        else {
                            Toast.makeText(context, "Please check new and confirm password must be same!", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });

            changePwdDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callChangePasswordApi(String username,String oldPassword,String newPassword,
                                       Context context,Dialog dialog,LinearLayout loading_ll){
        try {

            ChangePasswordRequestModel changePasswordRequestModel=new ChangePasswordRequestModel();
            changePasswordRequestModel.setUsername(username);
            changePasswordRequestModel.setOldPassword(oldPassword);
            changePasswordRequestModel.setNewPassword(newPassword);
            loading_ll.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface= RetrofitClient.getClient("changePassword").create(RetrofitInterface.class);

            retrofitInterface.changePassword(changePasswordRequestModel).enqueue(new Callback<SuccessResponseData>() {
                @Override
                public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {
                    if (response.isSuccessful()){
                        if (response.body().getMessage().equalsIgnoreCase("success")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Successfully Password Changed ! ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            try {
                                session.clear();
                                if (db.isOpen()){
                                    db.close();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Intent i = new Intent(context, LoginActivityNewGajaBandhu.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            finish();
                        }
                        else if (response.body().getMessage().equalsIgnoreCase("passwordmismatch")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Old Password Mismatched ! ", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                        else {  //failed
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "You have some issue with your password..Please check ! ", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                    }
                    else {
                        loading_ll.setVisibility(View.GONE);
                        Toast.makeText(context, "Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                    loading_ll.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            loading_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void callChangeUserIdDialog(Context context){
        try {
            Dialog changeUserIdDialog=new Dialog(context);
            changeUserIdDialog.setCancelable(false);
            changeUserIdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            changeUserIdDialog.setContentView(R.layout.change_userid_layout);

            close_img=changeUserIdDialog.findViewById(R.id.close_img);
            submit=changeUserIdDialog.findViewById(R.id.submit);
            old_userid=changeUserIdDialog.findViewById(R.id.old_userid);
            new_userid=changeUserIdDialog.findViewById(R.id.new_userid);
            confirm_userid=changeUserIdDialog.findViewById(R.id.confirm_userid);
            loading_ll=changeUserIdDialog.findViewById(R.id.loading_ll);


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeUserIdDialog.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oldUserId=old_userid.getText().toString().trim();
                    newUserId=new_userid.getText().toString().trim();
                    confirmUserId=confirm_userid.getText().toString().trim();

                    if (oldUserId.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter old username !", Toast.LENGTH_SHORT).show();
                    }
                    else if (newUserId.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter new username !", Toast.LENGTH_SHORT).show();
                    }
                    else if (confirmUserId.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter confirm username !", Toast.LENGTH_SHORT).show();
                    }
                    else if (newUserId.length()<=4 || confirmUserId.length()<=4){
                        Toast.makeText(context, "UserId must be minimum 4 characters !", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if (newUserId.equalsIgnoreCase(confirmUserId)){

                             callChangeUsernameApi(userNm,oldUserId,newUserId,SelectionPage.this,
                                    changeUserIdDialog,loading_ll);
//                            Toast.makeText(context, "Working on  Api !", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(context, "Please check new and confirm password must be same!", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });

            changeUserIdDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callChangeUsernameApi(String username,String oldUsername,String newUsername,
                                       Context context,Dialog dialog,LinearLayout loading_ll){
        try {

            ChangeUserNameModel changeUserNameModel=new ChangeUserNameModel();
            changeUserNameModel.setUsername(username);
            changeUserNameModel.setOldUsername(oldUsername);
            changeUserNameModel.setNewUsername(newUsername);
            loading_ll.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface= RetrofitClient.getClient("changeUsername").create(RetrofitInterface.class);

            retrofitInterface.changeUsername(changeUserNameModel).enqueue(new Callback<SuccessResponseData>() {
                @Override
                public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {
                    if (response.isSuccessful()){
                        if (response.body().getMessage().equalsIgnoreCase("success")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Successfully Username Changed ! ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            try {
                                session.clear();
                                if (db.isOpen()){
                                    db.close();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Intent i = new Intent(context, LoginActivityNewGajaBandhu.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            finish();
                        }
                        else if (response.body().getMessage().equalsIgnoreCase("notMatched")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Old Username Mismatched ! ", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                        else {
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "You have some issue with your username..Please check ! ", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                    }
                    else {
                        loading_ll.setVisibility(View.GONE);
                        Toast.makeText(context, "Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                    loading_ll.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            loading_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }




}

interface onLatitude{
    void getlatLong(double lat,double lng);
}
