package com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orsac.android.pccfwildlife.Contract.DashboardContract;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuCreateUserModel;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GenerateOtpResponseModel;
import com.orsac.android.pccfwildlife.MyUtils.GPSTracker;
import com.orsac.android.pccfwildlife.MyUtils.GoogleApiClientHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.Presenter.DashboardPresenter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.orsac.android.pccfwildlife.Activities.SplashScreen.CREATE_BEAT_TABLE;
import static com.orsac.android.pccfwildlife.Activities.SplashScreen.CREATE_DIV_TABLE;
import static com.orsac.android.pccfwildlife.Activities.SplashScreen.CREATE_RANGE_TABLE;
import static com.orsac.android.pccfwildlife.Activities.SplashScreen.CREATE_SECTION_TABLE;
import static com.orsac.android.pccfwildlife.Activities.SplashScreen.DIV_TABLE_NAME;
import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class RegisterGajaBandhuActivity extends AppCompatActivity implements DashboardContract.view, LocationListener,
                                                                    com.google.android.gms.location.LocationListener,
                                                                    PermissionUtils.PermissionResultCallback{

    Toolbar toolbar;
    ImageView back;
    TextView toolbar_title;
    SessionManager session;
    TextInputEditText name,phone_no;
    EditText age_edit;
    CheckBox male_checkbox,female_checkbox,other_checkbox;
    AppCompatButton submit_btn;
    RadioGroup rg;
    SearchableSpinner division_spinner,range_spinner,section_spinner,beat_spinner;
    ArrayAdapter<String> dataAdapter, rangedataAdapter,secdataAdapter,beatdataAdapter;
    List<String> divName,rangeName,secName,beatName;
    public String divCode="", rangeCode="", secCode="", beatCode="";
    String divisionValue="", rangeValue="", secValue="", beatValue="",languageSelect="",phoneNo="";
    SQLiteDatabase db;
    Cursor c=null;
    public HashMap<String, String> divKey;
    public HashMap<String, String> rangeKey;
    public HashMap<String, String> secKey;
    public HashMap<String, String> beatkey;
    DashboardContract.presenter dashboard_presenter;
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
    String userNm="",ageTxt="",genderSelect="",selectAuotmatic_Manual="";
    LinearLayout progress_ll;
    ConstraintLayout main_ll;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_gajabandhu_activity);

        try {

            initData();

            clickFunction();

            bindDivision();
            bindRange("");
            bindSection("");
            bindBeat("");


            languageSelect=session.getLanguageSelect();

            setLocale(languageSelect);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            toolbar=findViewById(R.id.toolbar_id);
            back=toolbar.findViewById(R.id.back);
            toolbar_title=toolbar.findViewById(R.id.toolbar_title);
            submit_btn=findViewById(R.id.submit);
            name=findViewById(R.id.name);
            phone_no=findViewById(R.id.phone_no);

            division_spinner=findViewById(R.id.division);
            range_spinner=findViewById(R.id.range);
            section_spinner=findViewById(R.id.section);
            beat_spinner=findViewById(R.id.beat);
            progress_ll=findViewById(R.id.progress_ll);
            main_ll=findViewById(R.id.main_ll);

            age_edit=findViewById(R.id.age_edit);

//            male_checkbox=findViewById(R.id.male_checkbox);
//            female_checkbox=findViewById(R.id.female_checkbox);
//            other_checkbox=findViewById(R.id.other_checkbox);
            rg=findViewById(R.id.rg);

            permissionUtils = new PermissionUtils(RegisterGajaBandhuActivity.this);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

            getLocation();
            permissionHelperClass = new PermissionHelperClass(RegisterGajaBandhuActivity.this);
            if (permissionHelperClass.hasLocationPermission()) {

                callGoogleApiClient();
            } else {
                permissionHelperClass.requestLocationPermission();//request location permissions
            }

            session=new SessionManager(RegisterGajaBandhuActivity.this);
            dashboard_presenter=new DashboardPresenter(RegisterGajaBandhuActivity.this,this,RegisterGajaBandhuActivity.this);
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

            divName=new ArrayList<String>();
            rangeName=new ArrayList<String>();
            secName=new ArrayList<String>();
            beatName=new ArrayList<String>();

            division_spinner.setTitle(getResources().getString(R.string.division_select));
            division_spinner.setPositiveButton(getResources().getString(R.string.ok));

            range_spinner.setTitle(getResources().getString(R.string.range_select));
            range_spinner.setPositiveButton(getResources().getString(R.string.ok));

            section_spinner.setTitle(getResources().getString(R.string.section_select));
            section_spinner.setPositiveButton(getResources().getString(R.string.ok));

            beat_spinner.setTitle(getResources().getString(R.string.beat_select));
            beat_spinner.setPositiveButton(getResources().getString(R.string.ok));

            //get phone no and set to register page
            phoneNo=getIntent().getStringExtra("phoneNo");
            phone_no.setText(phoneNo);
            phone_no.setEnabled(false);

//            callAlertDialog(RegisterGajaBandhuActivity.this);//for alert dialog of automatic and manual

            division_spinner.setVisibility(View.GONE);
            range_spinner.setVisibility(View.GONE);
            section_spinner.setVisibility(View.GONE);
            beat_spinner.setVisibility(View.GONE);

            //create div_master
            db.execSQL(CREATE_DIV_TABLE);
            db.execSQL(CREATE_RANGE_TABLE);
            db.execSQL(CREATE_SECTION_TABLE);
            db.execSQL(CREATE_BEAT_TABLE);

            PermissionUtils.hideKeyboard(RegisterGajaBandhuActivity.this);//hide keyboard

            try {
                c = db.rawQuery("SELECT * from " + DIV_TABLE_NAME + "", null);
                int count = c.getCount();
                if (count < 1) {

                    //Call service to sync data
//            callUpdate(checkInternet_status,Dashboard_nw.this); //Run for first time only
                    dashboard_presenter.load_Update("",RegisterGajaBandhuActivity.this);//Run for first time only

                }
            }catch (Exception e){
                c.close();
                db.close();
            }

            toolbar_title.setText("Register Gaja Bandhu");


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clickFunction() {
        try {

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId==R.id.male_rb){
                        genderSelect="Male";
//                        Toast.makeText(RegisterGajaBandhuActivity.this, "Male", Toast.LENGTH_SHORT).show();
                    }
                    else if (checkedId==R.id.female_rb){
                        genderSelect="Female";
//                        Toast.makeText(RegisterGajaBandhuActivity.this, "Female", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        genderSelect="Other";
//                        Toast.makeText(RegisterGajaBandhuActivity.this, "Other", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            division_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        divisionValue = (String) adapterView.getItemAtPosition(i);
                        divCode = divKey.get(divisionValue);
                        bindRange(divCode);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            range_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        rangeValue = (String) adapterView.getItemAtPosition(i);
                        rangeCode = rangeKey.get(rangeValue);
                        bindSection(rangeCode);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            section_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    try {
                        secValue = (String) adapterView.getItemAtPosition(i);
                        secCode = secKey.get(secValue);
                        bindBeat(secCode);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            beat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        beatValue = (String) adapterView.getItemAtPosition(i);
                        beatCode = beatkey.get(beatValue);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    userNm=name.getText().toString().trim();
                    ageTxt=age_edit.getText().toString().trim();

                    if (userNm.equalsIgnoreCase("")){
                        callSnackBarOnTop("Please enter your name !");
                    }else if (ageTxt.equalsIgnoreCase("") || Integer.parseInt(ageTxt)<=12){
                        callSnackBarOnTop("Please enter valid age !");
                    }else {

                        if (divCode==null){
                            divCode="";
                        }
                        if (rangeCode==null){
                            rangeCode="";
                        }
                        if (secCode==null){
                            secCode="";
                        }
                        if (beatCode==null){
                            beatCode="";
                        }
                        callGajaBandhuRegisterApi(userNm,ageTxt,phoneNo,divCode,rangeCode,secCode,beatCode,""+longitude,""+latitude);


//                        if (selectAuotmatic_Manual.equalsIgnoreCase("manual")){
//                            if (divCode.equalsIgnoreCase("")){
//                                callSnackBarOnTop("Please select division !");
//                            }else if (rangeCode.equalsIgnoreCase("")){
//                                callSnackBarOnTop("Please select range !");
//                            }else if (secCode.equalsIgnoreCase("")){
//                                callSnackBarOnTop("Please select section !");
//                            }else if (beatCode.equalsIgnoreCase("")){
//                                callSnackBarOnTop("Please select beat !");
//                            }else {
//
//                                callGajaBandhuRegisterApi(userNm,ageTxt,phoneNo,divCode,rangeCode,secCode,beatCode,"","");
//                            }
//
//                        }else {
//                            //For automatic
//                            callGajaBandhuRegisterApi(userNm,ageTxt,phoneNo,divCode,rangeCode,secCode,beatCode,""+longitude,""+latitude);
//
//                        }


                    }

                }
            });




        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void bindDivision() {
        try {
            divName=new ArrayList<String>();
            divName.add(getResources().getString(R.string.division_select));
            divKey = new HashMap<>();
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            Cursor cursor = null;
            cursor = db.rawQuery("SELECT  DISTINCT Division_Id,Division_Name FROM Division_Other order by Division_Name", null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    divName.add(cursor.getString(cursor.getColumnIndex("Division_Name")));
                    divKey.put(cursor.getString(cursor.getColumnIndex("Division_Name")), cursor.getString(cursor.getColumnIndex("Division_Id")));
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            db.close();
            dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, divName);
            dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
            division_spinner.setAdapter(dataAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void bindRange(String divCode) {
        try {
            rangeName = new ArrayList<String>();
            rangeName.add(getResources().getString(R.string.range_select));
            rangeKey = new HashMap<>();
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT  DISTINCT Range_ID,Range_Name FROM WlRange_Other where Division_ID='" + divCode + "' order by Range_Name", null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    rangeName.add(cursor.getString(cursor.getColumnIndex("Range_Name")));
                    rangeKey.put(cursor.getString(cursor.getColumnIndex("Range_Name")), cursor.getString(cursor.getColumnIndex("Range_ID")));
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            db.close();
//        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinneritem, rangeName);
            rangedataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, rangeName);
            rangedataAdapter.setDropDownViewResource(R.layout.spinnerfront);
            range_spinner.setAdapter(rangedataAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void bindSection(String rangeCode) {
        try {
            secName = new ArrayList<String>();
            secName.add(getResources().getString(R.string.section_select));
            secKey = new HashMap<>();
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT  DISTINCT Section_ID,Section_Name FROM WlSection where Range_ID='" + rangeCode + "' order by Section_Name", null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    secName.add(cursor.getString(cursor.getColumnIndex("Section_Name")));
                    secKey.put(cursor.getString(cursor.getColumnIndex("Section_Name")), cursor.getString(cursor.getColumnIndex("Section_ID")));
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            db.close();
            secdataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, secName);
            secdataAdapter.setDropDownViewResource(R.layout.spinnerfront);
            section_spinner.setAdapter(secdataAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void bindBeat(String secCode) {
        try {
            beatName = new ArrayList<String>();
            beatName.add(getResources().getString(R.string.beat_select));
            beatkey = new HashMap<>();
            db = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("SELECT  DISTINCT WlBeat_ID,WlBeat_Name FROM WlBeat where Section_ID='" + secCode + "' order by WlBeat_Name", null);
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                do {
                    beatName.add(cursor.getString(cursor.getColumnIndex("WlBeat_Name")));
                    beatkey.put(cursor.getString(cursor.getColumnIndex("WlBeat_Name")), cursor.getString(cursor.getColumnIndex("WlBeat_ID")));
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            db.close();
            beatdataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, beatName);
            beatdataAdapter.setDropDownViewResource(R.layout.spinnerfront);
            beat_spinner.setAdapter(beatdataAdapter);

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

    public  void setLocale(String lang) {
        try {
            Locale locale = new Locale(lang);
        //  Locale locale = new Locale(lang, "IN");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void replaceFragment(int itemId, Fragment fragmentt, String tag) {
        try {
            //initializing the fragment object which is selected
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.middle_ll, fragmentt, tag);
            if (itemId == 1) {
                ft.addToBackStack("" + itemId);
            }
            ft.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //location code start
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            gps=new GPSTracker(RegisterGajaBandhuActivity.this);

            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(this).equalsIgnoreCase("false")){
//                        googleApiClientHelperClass.buildGoogleApiClient();
                        gps.showSettingsAlert(RegisterGajaBandhuActivity.this);
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

            googleApiClientHelperClass = new GoogleApiClientHelperClass(RegisterGajaBandhuActivity.this);
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
                    if (ActivityCompat.checkSelfPermission(RegisterGajaBandhuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(RegisterGajaBandhuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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
//            latdeg.setText(String.valueOf(d)+ " \u00B0");
//            latmin.setText(String.valueOf(m) + " \u0027 ");
            DecimalFormat df = new DecimalFormat("#.###");
//            latsec.setText(df.format(end)+ " \" ");

            d1 = (int) longitude;
            double mm1 = Double.valueOf(d1);
            double base1 = (longitude - mm1) * st;
            m1 = (int) base1;
            double nn1 = Double.valueOf(m1);
            double end1 = (longitude - mm1 - (nn1 / st)) * tt;
//            londeg.setText(String.valueOf(d1)+ " \u00B0");
//            lonmin.setText(String.valueOf(m1)+ " \u0027 ");
            DecimalFormat df1 = new DecimalFormat("#.###");
//            lonsec.setText(df1.format(end1)+ " \" ");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callAlertDialog(Context context){
        try {
            Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_alert_dialog);

            Button automatic_btn,manual_btn;
            ImageView close_img;

            automatic_btn=dialog.findViewById(R.id.automatic_btn);
            manual_btn=dialog.findViewById(R.id.manual_btn);
            close_img=dialog.findViewById(R.id.close_img);

            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    onBackPressed();
                    selectAuotmatic_Manual="";
                }
            });

            automatic_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    division_spinner.setVisibility(View.GONE);
                    range_spinner.setVisibility(View.GONE);
                    section_spinner.setVisibility(View.GONE);
                    beat_spinner.setVisibility(View.GONE);
                    selectAuotmatic_Manual="automatic";
                }
            });
            manual_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                    division_spinner.setVisibility(View.VISIBLE);
                    range_spinner.setVisibility(View.VISIBLE);
                    section_spinner.setVisibility(View.VISIBLE);
                    beat_spinner.setVisibility(View.VISIBLE);
                    selectAuotmatic_Manual="manual";

                }
            });

            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            dialog.show();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (toolbar_title!=null){
            toolbar_title.setText("Register Gaja Bandhu");
        }
    }

    public void callGajaBandhuRegisterApi(String username,String age,String mobile,String division,String range,
                                          String section,String beat,String longitude,String latitude){
        try {
            GajaBandhuCreateUserModel createUserModel=new GajaBandhuCreateUserModel();
            createUserModel.setUsername(username);
            createUserModel.setAge(age);
            createUserModel.setMobile(mobile);
            createUserModel.setDivision(division);
            createUserModel.setRange(range);
            createUserModel.setSection(section);
            createUserModel.setBeat(beat);
            createUserModel.setLongitude(longitude);
            createUserModel.setLatitude(latitude);

            progress_ll.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface= RetrofitClient.getGajaBandhuRequestClient().create(RetrofitInterface.class);

            retrofitInterface.add_GajaBandhuUser(createUserModel).enqueue(new Callback<GenerateOtpResponseModel>() {
                @Override
                public void onResponse(Call<GenerateOtpResponseModel> call, Response<GenerateOtpResponseModel> response) {

                    if (response.isSuccessful()){
                        progress_ll.setVisibility(View.GONE);

                        session.setGajaBandhu_UserId(mobile);
                        Toast.makeText(RegisterGajaBandhuActivity.this, "Registration Success !", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(RegisterGajaBandhuActivity.this, GajaBandhuItemActivity.class); // Dashboard
                        startActivity(intent);
                        finish();

                    }else {
                        progress_ll.setVisibility(View.GONE);
                        Toast.makeText(RegisterGajaBandhuActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GenerateOtpResponseModel> call, Throwable t) {
                    progress_ll.setVisibility(View.GONE);
                    Toast.makeText(RegisterGajaBandhuActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception e){
            progress_ll.setVisibility(View.GONE);
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
}
