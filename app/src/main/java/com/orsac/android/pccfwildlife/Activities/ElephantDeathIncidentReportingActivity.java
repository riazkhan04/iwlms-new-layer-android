package com.orsac.android.pccfwildlife.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.orsac.android.pccfwildlife.Model.IncidentReportingData;
import com.orsac.android.pccfwildlife.Model.IncidentTypeModel;
import com.orsac.android.pccfwildlife.Adapters.IncidentRecyclerAdapter;
import com.orsac.android.pccfwildlife.Model.IncidentCheckboxModel.CheckBoxItemModel;
import com.orsac.android.pccfwildlife.MyUtils.CommonMethods;
import com.orsac.android.pccfwildlife.MyUtils.GPSTracker;
import com.orsac.android.pccfwildlife.MyUtils.GoogleApiClientHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SQLiteDB.DBhelper;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class ElephantDeathIncidentReportingActivity extends AppCompatActivity implements LocationListener, com.google.android.gms.location.LocationListener,
        PermissionUtils.PermissionResultCallback, IncidentRecyclerAdapter.OnCheckbox_Selected_listener,
        SensorEventListener {

    Toolbar toolbar;
    ImageView back;
    TextView toolbar_title;
    SearchableSpinner division_spinner,range_spinner,section_spinner,beat_spinner;
    AppCompatSpinner  incidentType_spinner, incidentsubType_spinner,death_reason_spinner;
    ScrollView main_ll;
    MaterialTextView dateofmov;

    TextInputEditText latdeg, latmin, latsec, londeg, lonmin, lonsec, locationname,
            noofelephants, herd, tusker, male, female, calf,
            death_txt,altitude_txt,accuracy_txt,remarks,death_reason_txt;
    TextInputLayout death_til;
    Button save_btn;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String str = sdf.format(new Date());
    Calendar mcalender = Calendar.getInstance();
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;//3 sec
    Location location;
    double latitude;
    double longitude;
    ArrayList<String> permissions = new ArrayList<>();
    PermissionUtils permissionUtils;
    boolean isPermissionGranted;

    public HashMap<String, String> divKey;
    public HashMap<String, String> rangeKey;
    public HashMap<String, String> secKey;
    public HashMap<String, String> beatkey;
    String divisionValue = "", rangeValue = "", secValue = "", beatValue = "", incident_typeValue = "", incident_subtypeValue = "", death_str = "",
            incidentRemark="",death_reason_str="";
    public String divCode = "", rangeCode = "", secCode = "", beatCode = "", dateformatForAPi = "",currentDate="";
    public String division_nm = "", range_nm = "", section_nm = "", beat_nm = "", elephantNo = "", herdNo = "", calfNo = "", tuskerNo = "", maleNo = "", femaleNo = "",
            location_nm = "", lat_deg = "", lat_min = "", lat_sec = "", lng_degree = "", lng_min = "", lng_sec = "", fromTime = "", toTime = "", rmrks = "",
            reportingType_nm = "",altitude="",accuracy="";

    private DBhelper DbHelper;

    SQLiteDatabase db;

    public String token = "", checkInternet_status = "", divisionNmFrom_pref = "", userId = "",
            incident_type_intent_str="",circle_Nm="";
    SessionManager session;
    List<String> incident_type_list = null;
    ArrayList<CheckBoxItemModel> incidentOf_arr, incident_animal_arr, incidnt_elephant_arr,fire_arr;
    //    ArrayList<String> incidentOf_arr,incident_animal_arr,incidnt_elephant_arr;
    LinearLayout checkbox_ll, extra_animal_death_ll;
    RecyclerView incident_recyclerView;
    IncidentRecyclerAdapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    ArrayList<CheckBoxItemModel> chekboxNm_arr, checkboxValue_arr;
    GPSTracker gps;
    public LocationRequest locationRequest;
    final static int REQUEST_LOCATION = 199,Camera_REQUEST_CODE=104,IMEI_REQUEST_CODE=111;
    GoogleApiClientHelperClass googleApiClientHelperClass;
    PermissionHelperClass permissionHelperClass;
    ArrayAdapter<CharSequence> spinner_Adapter=null;
    ImageView camera_img;
    Uri image_uri=null;
    String img_url_str="",compass_direction="";
    private String pictureImagePath = "",imeiNo="";
    Bitmap myBitmap,viewBitmap;
    ArrayAdapter<String> spinnerAdapter = null;
    ArrayList<IncidentTypeModel> incidentTypeModels;
    JSONArray incidentArr=new JSONArray();
    SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;
    int bearingValue=0;
    Handler bearingHandler;
    CommonMethods commonMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.left_to_right_anim,
                R.anim.left_to_right_anim);
        setContentView(R.layout.activity_incident_reporting);

        initData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

//        dateofmov.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dateofmov.setError(null);
////                final Calendar calender = Calendar.getInstance();
//                mYear = mcalender.get(Calendar.YEAR);
//                mMonth = mcalender.get(Calendar.MONTH);
//                mDay = mcalender.get(Calendar.DAY_OF_MONTH);
//                DatePickerDialog dpd = new DatePickerDialog(IncidentReportingActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//                                int mon = monthOfYear + 1;
//                                if (mon < 10) {
//                                    m = "0" + String.valueOf(mon);
//                                } else {
//                                    m = String.valueOf(mon);
//                                }
//                                if (dayOfMonth < 10) {
//
//                                    d = "0" + String.valueOf(dayOfMonth);
//                                } else {
//                                    d = String.valueOf(dayOfMonth);
//                                }
////                                dateofmov.setText(m + "-" + d + "-" + year);
//                                dateofmov.setText(d + "-" + m + "-" + year);
//                                dateformatForAPi = m + "-" + d + "-" + year + " " + str;
////                                Toast.makeText(ElephantReport.this, dateformatForAPi, Toast.LENGTH_SHORT).show();
//
//                            }
//                        }, mYear, mMonth, mDay);
//
//                dpd.getDatePicker().setMaxDate(mcalender.getTimeInMillis());
//                dpd.show();
//            }
//        });

        division_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                divisionValue = (String) adapterView.getItemAtPosition(i);
                divCode = divKey.get(divisionValue);
                bindRange(divCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        range_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rangeValue = (String) adapterView.getItemAtPosition(i);
                rangeCode = rangeKey.get(rangeValue);
                bindSection(rangeCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        section_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                secValue = (String) adapterView.getItemAtPosition(i);
                secCode = secKey.get(secValue);
                bindBeat(secCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        beat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                beatValue = (String) adapterView.getItemAtPosition(i);
                beatCode = beatkey.get(beatValue);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        incidentType_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                incident_typeValue = (String) adapterView.getItemAtPosition(i);

                callForCheckBoxAdapter();//Call for checkbox Adapter

                if (incident_typeValue.equalsIgnoreCase("Animal Death")) {

                    incident_type_list = Arrays.asList(getResources().getStringArray(R.array.animal_death_arr));

                    spinnerAdapter = new ArrayAdapter<String>(ElephantDeathIncidentReportingActivity.this, android.R.layout.simple_spinner_item, incident_type_list);

                } else if (incident_typeValue.equalsIgnoreCase("Elephant Death")) {

                    incident_type_list = Arrays.asList(getResources().getStringArray(R.array.elephant_death_arr));

                    spinnerAdapter = new ArrayAdapter<String>(ElephantDeathIncidentReportingActivity.this, android.R.layout.simple_spinner_item, incident_type_list);

                } else {
                    incident_type_list = Arrays.asList(getResources().getStringArray(R.array.damage_through_elephant_arr));

                    spinnerAdapter = new ArrayAdapter<String>(ElephantDeathIncidentReportingActivity.this, android.R.layout.simple_spinner_item, incident_type_list);

                }
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                incidentsubType_spinner.setAdapter(spinnerAdapter);
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        incidentsubType_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                incident_subtypeValue = (String) adapterView.getItemAtPosition(i);

                if (incident_subtypeValue.equalsIgnoreCase("Crop Damage (acres)")) {

                    death_til.setHint("In Acres");
                    death_txt.setText("");
                } else {
                    death_til.setHint("Death");
                    death_txt.setText("");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        death_reason_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                death_reason_str = (String) adapterView.getItemAtPosition(i);

//                if (incident_subtypeValue.equalsIgnoreCase("Crop Damage (acres)")) {
//
//                    death_til.setHint("In Acres");
//                    death_txt.setText("");
//                } else {
//                    death_til.setHint("Death");
//                    death_txt.setText("");
//                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        camera_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cInt,Camera_REQUEST_CODE);
                if (!pictureImagePath.equalsIgnoreCase("")){
                    callImageViewDialog(ElephantDeathIncidentReportingActivity.this,
                            viewBitmap);
                }else {

                    try {
                        openBackCamera();
                        viewBitmap=null;

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
//                openBackCamera();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

//                 death_reason_str=death_reason_txt.getText().toString().trim();
                    death_str = death_reason_spinner.getSelectedItem().toString();

                if (dateofmov.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Select date of movement ", Snackbar.LENGTH_SHORT).show();
                } else if (locationname.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter location", Snackbar.LENGTH_SHORT).show();
                } else if (divisionValue.equalsIgnoreCase("Select Division")) {
                    Snackbar.make(main_ll, "Select Division", Snackbar.LENGTH_SHORT).show();
                } else if (rangeValue.equalsIgnoreCase("Select Range")) {
                    Snackbar.make(main_ll, "Select Range", Snackbar.LENGTH_SHORT).show();
                } else if (secValue.equalsIgnoreCase("Select Section")) {
                    Snackbar.make(main_ll, "Select Section", Snackbar.LENGTH_SHORT).show();
                } else if (beatValue.equalsIgnoreCase("Select Beat")) {
                    Snackbar.make(main_ll, "Select Beat", Snackbar.LENGTH_SHORT).show();
                } else if (incidentType_spinner.getSelectedItem().toString().equalsIgnoreCase("Select Incident Type")) {
                    Snackbar.make(main_ll, "Select Incident Type", Snackbar.LENGTH_SHORT).show();
                }else if (death_str.equalsIgnoreCase("Select death reason")) {
                    Snackbar.make(main_ll, "Select death reason", Snackbar.LENGTH_SHORT).show();
                }
                else if (image_uri==null){
                    Snackbar.make(main_ll, "Please provide an image ", Snackbar.LENGTH_SHORT).show();
                }
                else if (chekboxNm_arr.isEmpty()){
                    Snackbar.make(main_ll, "Please select elephant type", Snackbar.LENGTH_SHORT).show();
                }
                else {

                    for(int i=0;i<chekboxNm_arr.size();i++) {
                        incidentTypeModels.clear();
                        if (chekboxNm_arr.get(i).isChecked()){
                            for (int j=0;j<chekboxNm_arr.size();j++){
                                incidentTypeModels.add(new IncidentTypeModel(chekboxNm_arr.get(j).getCheck_box_item_nm(),
                                        chekboxNm_arr.get(j).getCheckbox_item_value()));
                            }
                        }
                        Log.i("incident_json",incidentTypeModels.toString());
                    }

                    PermissionUtils.hideKeyboard(ElephantDeathIncidentReportingActivity.this);//Hide Keyboard

                    incident_typeValue = incidentType_spinner.getSelectedItem().toString();
                    incident_subtypeValue = incidentsubType_spinner.getSelectedItem().toString();
                    division_nm = division_spinner.getSelectedItem().toString();
                    range_nm = range_spinner.getSelectedItem().toString();
                    section_nm = section_spinner.getSelectedItem().toString();
                    beat_nm = beat_spinner.getSelectedItem().toString();
                    location_nm = locationname.getText().toString();
                    lat_deg = ""+latitude;//For latitude
                    lat_min = latmin.getText().toString();
                    lat_sec = latsec.getText().toString();
                    lng_degree = ""+longitude; //For longitude
                    lng_min = lonmin.getText().toString();
                    lng_sec = lonsec.getText().toString();
//                    death_str = death_txt.getText().toString().trim();
                    death_str = death_reason_spinner.getSelectedItem().toString();
                    incidentRemark = remarks.getText().toString().trim();


                    for(int i=0;i<chekboxNm_arr.size();i++) {
                        if (chekboxNm_arr.get(i).getCheckbox_item_value().equalsIgnoreCase("0")) {
                            Snackbar.make(main_ll, "Please add incident type data ", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Log.i("incident_value", chekboxNm_arr.get(i).getCheck_box_item_nm() + "-" + chekboxNm_arr.get(i).getCheckbox_item_value());
                            Log.i("death_reason",death_reason_str);

                            JSONObject jsonObject=new JSONObject();
                            jsonObject.put("name",chekboxNm_arr.get(i).getCheck_box_item_nm());
                            jsonObject.put("value",chekboxNm_arr.get(i).getCheckbox_item_value());

                            incidentArr.put(jsonObject);

                            dynamicAlertDialog(ElephantDeathIncidentReportingActivity.this, "Please check your data before save !", "Warning", "Yes");

                        }
                    }


                }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }


    public void initData() {

        try {

            toolbar = findViewById(R.id.toolbar_id);

            back = toolbar.findViewById(R.id.back);
            toolbar_title = toolbar.findViewById(R.id.toolbar_title);

            main_ll = findViewById(R.id.main_ll);
            dateofmov = findViewById(R.id.dateof);
            latdeg = findViewById(R.id.latdeg);
            latmin = findViewById(R.id.latmin);
            latsec = findViewById(R.id.latsec);
            londeg = findViewById(R.id.londeg);
            lonmin = findViewById(R.id.lonmin);
            lonsec = findViewById(R.id.lonsec);
            locationname = findViewById(R.id.locationname);
            death_txt = findViewById(R.id.death_txt);
            death_til = findViewById(R.id.death_til);

            division_spinner = findViewById(R.id.division);
            range_spinner = findViewById(R.id.range);
            section_spinner = findViewById(R.id.section);
            beat_spinner = findViewById(R.id.beat);
            incidentType_spinner = findViewById(R.id.incidentType_spinner);
            incidentsubType_spinner = findViewById(R.id.incidentsubType_spinner);
            death_reason_spinner = findViewById(R.id.death_reason_spinner);
            altitude_txt=findViewById(R.id.altitude_txt);
            accuracy_txt=findViewById(R.id.accuracy_txt);
            remarks=findViewById(R.id.remarks);
            death_reason_txt=findViewById(R.id.death_reason_txt);
            save_btn = findViewById(R.id.save);
            camera_img = findViewById(R.id.camera_img);
            incidentTypeModels=new ArrayList<>();
            chekboxNm_arr = new ArrayList<>();

//        checkbox=findViewById(R.id.checkbox);
            checkbox_ll = findViewById(R.id.checkbox_ll);
            extra_animal_death_ll = findViewById(R.id.extra_animal_death_ll);
            incident_recyclerView = findViewById(R.id.incident_recyclerView);
            commonMethods=new CommonMethods(ElephantDeathIncidentReportingActivity.this);
            toolbar_title.setText(R.string.incident_report);
            DbHelper = new DBhelper(this);
            session = new SessionManager(ElephantDeathIncidentReportingActivity.this);


            token = session.getToken();
            divisionNmFrom_pref = session.getDivision();
            userId = session.getUserID();
            circle_Nm = session.getJuridiction();

            incidentOf_arr = new ArrayList<>();
            incident_animal_arr = new ArrayList<>();
            incidnt_elephant_arr = new ArrayList<>();
            fire_arr = new ArrayList<>();
//            readPhoneStatePermission();//Read PhoneStatePermission

            getImeiNo();//Getting Imei No(from READ PHONE STATE)

            division_spinner.setTitle("Select Division");
            division_spinner.setPositiveButton("OK");

            range_spinner.setTitle("Select Range");
            range_spinner.setPositiveButton("OK");

            section_spinner.setTitle("Select Section");
            section_spinner.setPositiveButton("OK");

            beat_spinner.setTitle("Select Beat");
            beat_spinner.setPositiveButton("OK");

            dateformatForAPi= PermissionUtils.getCurrentDate("dd-MM-yyyy");
            currentDate= PermissionUtils.getCurrentDate("dd-MM-yyyy HH:mm:ss");
            dateofmov.setText(dateformatForAPi);

            permissionUtils = new PermissionUtils(ElephantDeathIncidentReportingActivity.this);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

            incident_type_intent_str=getIntent().getStringExtra("incident_type");//know the incident type for spinner

            getLocation();
            permissionHelperClass = new PermissionHelperClass(ElephantDeathIncidentReportingActivity.this);
            if (permissionHelperClass.hasLocationPermission()) {

                callGoogleApiClient();
            } else {
                permissionHelperClass.requestLocationPermission();//request location permissions
            }

            if (permissionHelperClass.hasCameraNStoragePermission()){

            }else {
                permissionHelperClass.requestCameraNStoragePermission();//request for camera permission
            }

            bindDivision();
            bindRange("");
            bindSection("");
            bindBeat("");

            toolbar_title.setText("Elephant Death Incident Reporting");

            spinner_Adapter= ArrayAdapter.createFromResource(this,
                    R.array.incident_type_arr, android.R.layout.simple_spinner_item);
            spinner_Adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

//            if (incident_type_intent_str.equalsIgnoreCase("elephantDeath")){
//                spinner_Adapter= ArrayAdapter.createFromResource(this,
//                        R.array.incident_type_arr, android.R.layout.simple_spinner_item);
//                spinner_Adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
//
//                toolbar_title.setText("Elephant Death Incident Reporting");
//
//            }else {
//                spinner_Adapter = ArrayAdapter.createFromResource(this,
//                        R.array.incident_other_type_arr, android.R.layout.simple_spinner_item);
//                spinner_Adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
//
//                toolbar_title.setText("Other Incident Reporting");
//            }
            incidentType_spinner.setAdapter(spinner_Adapter);
            incidentType_spinner.setSelection(1);


            incident_recyclerView.setHasFixedSize(true);
//        incident_recyclerView.setNestedScrollingEnabled(false);//It stop lagging in recyclerview
            layoutManager = new LinearLayoutManager(ElephantDeathIncidentReportingActivity.this);
            incident_recyclerView.setLayoutManager(layoutManager);

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

            gps=new GPSTracker(ElephantDeathIncidentReportingActivity.this);

            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(this).equalsIgnoreCase("false")){
//                        googleApiClientHelperClass.buildGoogleApiClient();
                        gps.showSettingsAlert(ElephantDeathIncidentReportingActivity.this);
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    //-------------

    private void bindDivision() {
        List<String> divName = new ArrayList<String>();
        divName.add("Select Division");
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
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, divName);
        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
        division_spinner.setAdapter(dataAdapter);
    }
    private void bindRange(String divCode) {
        List<String> rangeName = new ArrayList<String>();
        rangeName.add("Select Range");
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
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, rangeName);
        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
        range_spinner.setAdapter(dataAdapter);
    }
    public void bindSection(String rangeCode) {
        List<String> secName = new ArrayList<String>();
        secName.add("Select Section");
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
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, secName);
        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
        section_spinner.setAdapter(dataAdapter);
    }

    public void bindBeat(String secCode) {
        List<String> beatName = new ArrayList<String>();
        beatName.add("Select Beat");
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
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, beatName);
        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
        beat_spinner.setAdapter(dataAdapter);
    }

    public void callGoogleApiClient(){
        try {

            googleApiClientHelperClass = new GoogleApiClientHelperClass(ElephantDeathIncidentReportingActivity.this);
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
                    if (ActivityCompat.checkSelfPermission(ElephantDeathIncidentReportingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ElephantDeathIncidentReportingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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

                        altitude_txt.setText(altitude_);
                        accuracy_txt.setText(accuracy);

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
    public void PermissionGranted(int request_code) {
        isPermissionGranted = true;
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

    public void saveIncidentReportData_inLocal(IncidentReportingData incidentReportingData, String message) {

        DbHelper.open();
        DbHelper.insertIncidentReport(incidentReportingData);
        DbHelper.close();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ElephantDeathIncidentReportingActivity.this, R.style.AlertDialogCustom));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ElephantDeathIncidentReportingActivity.this.startActivity(new Intent(ElephantDeathIncidentReportingActivity.this, ElephantDeathIncidentReportingActivity.class));
                ElephantDeathIncidentReportingActivity.this.finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingLeftDialogAnimation;
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        try {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        ElephantDeathIncidentReportingActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

        if (buttonNeed.equalsIgnoreCase("yes")){
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                                //Save in SQLITE
                            IncidentReportingData incidentReportingData= new IncidentReportingData(division_nm,range_nm,section_nm,beat_nm,location_nm,
                                    currentDate, lat_deg, lat_min, lat_sec, lng_degree, lng_min, lng_sec,img_url_str,"NA",
                                    incident_typeValue.toLowerCase(),incidentArr.toString(),circle_Nm,userId,imeiNo,altitude,accuracy,
                                    incidentRemark,death_reason_str);

                            saveIncidentReportData_inLocal(incidentReportingData,"Data saved successfully ");
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });
        }
        else {
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

    public void oncheckBox_Click(AppCompatCheckBox checkBox){

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){
                    Toast.makeText(ElephantDeathIncidentReportingActivity.this, ""+compoundButton.getText(), Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(ElephantDeathIncidentReportingActivity.this, ""+compoundButton.getText()+" Not checked !", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void callForCheckBoxAdapter() {
     try {

        incidentOf_arr.clear();
        incident_animal_arr.clear();
        incidnt_elephant_arr.clear();
        fire_arr.clear();

        checkbox_ll.setVisibility(View.VISIBLE);

        if (incident_typeValue.equalsIgnoreCase("Damage through Elephant")) {

            incidentOf_arr.add(new CheckBoxItemModel(false,"Human Kill","",0));
            incidentOf_arr.add(new CheckBoxItemModel(false,"Human Injury","",0));
            incidentOf_arr.add(new CheckBoxItemModel(false,"Crop Damage (acres)","",0));
            incidentOf_arr.add(new CheckBoxItemModel(false,"House Damage","",0));
            incidentOf_arr.add(new CheckBoxItemModel(false,"Cattle Kill","",0));

            incident_recyclerView.setMinimumHeight(450);
            adapter=new IncidentRecyclerAdapter(ElephantDeathIncidentReportingActivity.this,incidentOf_arr,extra_animal_death_ll,this);
            incident_recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
        else if (incident_typeValue.equalsIgnoreCase("Animal Death")) {


            incident_animal_arr.add(new CheckBoxItemModel(false,"Cow","",0));
            incident_animal_arr.add(new CheckBoxItemModel(false,"Tiger","",0));
            incident_animal_arr.add(new CheckBoxItemModel(false,"Lion","",0));
            incident_animal_arr.add(new CheckBoxItemModel(false,"Goat","",0));
            incident_animal_arr.add(new CheckBoxItemModel(false,"Dog","",0));
            incident_animal_arr.add(new CheckBoxItemModel(false,"Other","",0));

            incident_recyclerView.setMinimumHeight(550);
            adapter=new IncidentRecyclerAdapter(ElephantDeathIncidentReportingActivity.this,incident_animal_arr,extra_animal_death_ll,this);
            incident_recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else if (incident_typeValue.equalsIgnoreCase("Elephant Death")) {

            incidnt_elephant_arr.add(new CheckBoxItemModel(false,"Makhna","",0));
            incidnt_elephant_arr.add(new CheckBoxItemModel(false,"Tusker","",0));
            incidnt_elephant_arr.add(new CheckBoxItemModel(false,"Female","",0));
            incidnt_elephant_arr.add(new CheckBoxItemModel(false,"Calf","",0));

            incident_recyclerView.setMinimumHeight(150);
            adapter=new IncidentRecyclerAdapter(ElephantDeathIncidentReportingActivity.this,incidnt_elephant_arr,extra_animal_death_ll,this);
            incident_recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else if (incident_typeValue.equalsIgnoreCase("Fire")){

            fire_arr.add(new CheckBoxItemModel(false,"Farm Land","",0));
            fire_arr.add(new CheckBoxItemModel(false,"Personal Land","",0));
            fire_arr.add(new CheckBoxItemModel(false,"Other Land","",0));

            incident_recyclerView.setMinimumHeight(120);
            adapter=new IncidentRecyclerAdapter(ElephantDeathIncidentReportingActivity.this,fire_arr,extra_animal_death_ll,this);
            incident_recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else {
            checkbox_ll.setVisibility(View.GONE);
        }

         }catch (Exception e){
        e.printStackTrace();
      }

    }

    @Override
    public void oncheckBoxSelected(ArrayList<CheckBoxItemModel> checkbox_nmArr) {

//        ArrayList<CheckBoxItemModel> chekboxNm_arr = new ArrayList<>();
        chekboxNm_arr.clear();

            for(int i = 0;i<checkbox_nmArr.size();i++){
                if(checkbox_nmArr.get(i).isChecked()){
                    chekboxNm_arr.add(checkbox_nmArr.get(i));
                }

            }
            Log.d("checkBoxNameArr", chekboxNm_arr.toString());
//            Toast.makeText(this, chekboxNm_arr.toString(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void createCheckBoxSelected(ArrayList<CheckBoxItemModel> checkBoxItemModels, RecyclerView recyclerView, int noOfCasuality) {
        try {


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
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
//                                camera_img.setImageBitmap(myBitmap);
                                image_uri=getImageUri(ElephantDeathIncidentReportingActivity.this,myBitmap);
                                img_url_str=getRealPathFromURI(image_uri);

                                filenm=pictureImagePath.replace("/storage/emulated/0/WildLifeAppImages/","");

//                                replaceFile(filenm);

                            }
//                            callCompassDirection();
                            //Call camera geotag dialog
                            callCameraGeotagDialog(ElephantDeathIncidentReportingActivity.this,myBitmap,latitude,longitude,
                                    altitude,accuracy,pictureImagePath,filenm);


                        } else if (resultCode == RESULT_CANCELED) {
                            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                            pictureImagePath="";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

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

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        Uri uri=null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            String path = MediaStore.Images.Media.insertImage(ElephantDeathIncidentReportingActivity.this.getContentResolver(), photo, "incident_report"+ Calendar.getInstance().getTime(), null);
            uri= Uri.parse(path);

        }catch (Exception e){
            e.printStackTrace();
        }
        return uri;

    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
//            googleApiClientHelperClass=new GoogleApiClientHelperClass(IncidentReportingActivity.this);
//            googleApiClientHelperClass.connect();
            callGoogleApiClient();


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



    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();

        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void callCameraGeotagDialog(Context context,Bitmap bitmap,
                                        Double latitude,Double longitude,
                                        String altitude,String accuracy,
                                        String pictureimagePath,String filenm){
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
            TextView textDirection=dialog.findViewById(R.id.textDirection);
            TextView bearing_txt=dialog.findViewById(R.id.bearing_txt);
            CardView ok_cv=dialog.findViewById(R.id.ok_cv);
            RelativeLayout geo_RL=dialog.findViewById(R.id.geo_RL);
            LinearLayout progress_ll=dialog.findViewById(R.id.progress_ll);

            ok_cv.setVisibility(View.GONE);
            progress_ll.setVisibility(View.VISIBLE);
//            textDirection.setVisibility(View.VISIBLE);
//            bearing_txt.setVisibility(View.VISIBLE);

            Date date = new Date();
            String stringDate = DateFormat.getDateTimeInstance().format(date);

            //wait for 3 sec for getting value
            bearingHandler=new Handler();
            bearingHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    time_stamp.setText(stringDate);
                    altitude_txt.setText("Alt - "+altitude);
                    accuracy_txt.setText("Accu - "+accuracy);
                    lat_lng_txt.setText("Latitude-"+latitude+",\nLongitude-"+longitude);
//                    textDirection.setText(compass_direction);
//                    bearing_txt.setText("Bearing - "+bearingValue+"\u00B0  "+compass_direction);
                    progress_ll.setVisibility(View.GONE);
                    ok_cv.setVisibility(View.VISIBLE);
                }
            },3000);
            geo_img.setImageBitmap(bitmap);

            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    pictureImagePath="";
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
                    MediaStore.Images.Media.insertImage(getContentResolver(), bm, "incident_report"+ Calendar.getInstance().getTime(), null);

                    image_uri=getImageUri(ElephantDeathIncidentReportingActivity.this,bm);
//                   String img_url_str=getEncoded64ImageStringFromBitmap(bp);
                    img_url_str=getRealPathFromURI(image_uri);

                    camera_img.setImageBitmap(bm);

                        if (viewBitmap==null) {
                            viewBitmap = bm;
                        }

                    replaceFile(pictureimagePath,filenm,bm);

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

                Uri outputFileUri = FileProvider.getUriForFile(ElephantDeathIncidentReportingActivity.this,
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
                Uri outputFileUri = FileProvider.getUriForFile(ElephantDeathIncidentReportingActivity.this,
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
    private void getImeiNo(){
        String imei="";
        try {
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(ElephantDeathIncidentReportingActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(ElephantDeathIncidentReportingActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, IMEI_REQUEST_CODE);
//                return;
//            }
            Random r = new Random();
            int randomNumber = r.nextInt(1000);
            imeiNo = ""+randomNumber;
//            imeiNo = telephonyManager.getDeviceId();
//            Toast.makeText(this, ""+imeiNo, Toast.LENGTH_SHORT).show();
            Log.i("IMEI_NO-",""+imeiNo);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void readPhoneStatePermission(){
        if (ActivityCompat.checkSelfPermission(ElephantDeathIncidentReportingActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ElephantDeathIncidentReportingActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, IMEI_REQUEST_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IMEI_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                    if (permissionHelperClass.hasCameraNStoragePermission()) {

                    } else {
                        permissionHelperClass.requestCameraNStoragePermission();//request for camera permission
                    }
                    getImeiNo();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void callImageViewDialog(Context context,Bitmap imgpath){
        try {

            Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.image_view_dialog);

            ImageView report_image,close_img;
            TextView cancel_image;
            ZoomableImageView report_imgg;
//            report_image=dialog.findViewById(R.id.report_image);
            report_imgg=dialog.findViewById(R.id.report_image);
            close_img=dialog.findViewById(R.id.close_img);
            cancel_image=dialog.findViewById(R.id.cancel_image);

            cancel_image.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(imgpath)
                    .error(R.drawable.no_image_found)
                    .into(report_imgg);

            cancel_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                        pictureImagePath="";
                        camera_img.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                }
            });

            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callCompassDirection(){
        try {
            valuesAccelerometer = new float[3];
            valuesMagneticField = new float[3];

            matrixR = new float[9];
            matrixI = new float[9];
            matrixValues = new float[3];

            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            sensorManager.registerListener(this,
                    sensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this,
                    sensorMagneticField,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            switch(event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    for(int i =0; i < 3; i++){
                        valuesAccelerometer[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    for(int i =0; i < 3; i++){
                        valuesMagneticField[i] = event.values[i];
                    }
                    break;
            }

            boolean success = SensorManager.getRotationMatrix(
                    matrixR,
                    matrixI,
                    valuesAccelerometer,
                    valuesMagneticField);

            if(success){
                SensorManager.getOrientation(matrixR, matrixValues);

                double degree = Math.toDegrees(matrixValues[0]);//degree
                double vertical = Math.toDegrees(matrixValues[1]);//vertical
                double horizontal = Math.toDegrees(matrixValues[2]);//horizontal

                compass_direction=commonMethods.getDirectionName((int)degree,
                        sensorManager,sensorAccelerometer,sensorMagneticField,this);
                bearingValue=commonMethods.getBearingDegree((int) degree);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
