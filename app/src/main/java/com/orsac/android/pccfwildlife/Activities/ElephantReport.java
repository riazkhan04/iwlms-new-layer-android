package com.orsac.android.pccfwildlife.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.orsac.android.pccfwildlife.Model.ElephantCorridorData;
import com.orsac.android.pccfwildlife.Model.FileUploadResponse;
import com.orsac.android.pccfwildlife.MyUtils.CommonMethods;
import com.orsac.android.pccfwildlife.MyUtils.GPSTracker;
import com.orsac.android.pccfwildlife.MyUtils.GoogleApiClientHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SQLiteDB.DBhelper;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class ElephantReport extends AppCompatActivity implements LocationListener,
        com.google.android.gms.location.LocationListener,PermissionUtils.PermissionResultCallback,
        SensorEventListener {

    Toolbar toolbar;
    ImageView back;
    TextView toolbar_title;
    MaterialTextView from_time,to_time,dateofmov;
    TextInputEditText latdeg,latmin,latsec,londeg,lonmin,lonsec,locationname,
            noofelephants,herd,tusker,male,female,calf,altitude_txt,accuracy_txt;
    SearchableSpinner division_spinner,range_spinner,section_spinner,beat_spinner;
//    AppCompatSpinner range_spinner,section_spinner,beat_spinner;
    ScrollView main_ll;
    AppCompatButton save_btn;
    String choosedFromHour="", choosedFromMinute="", choosedToHour="", choosedToMinute = "";
    private int finalfrom, finalto,mYear, mMonth, mDay;
    String m, d;
    Calendar mcalender = Calendar.getInstance();
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;// for 3sec
    Location location;
    double latitude;
    double longitude;
    ArrayList<String> permissions = new ArrayList<>();
    PermissionUtils permissionUtils;
    boolean isPermissionGranted;
    SQLiteDatabase db;

    public HashMap<String, String> divKey;
    public HashMap<String, String> rangeKey;
    public HashMap<String, String> secKey;
    public HashMap<String, String> beatkey;
    String divisionValue="", rangeValue="", secValue="", beatValue="",imeiNo="";
    public String divCode="", rangeCode="", secCode="", beatCode="",dateformatForAPi="",currentDate="";
    public String division_nm="",range_nm="",section_nm="",beat_nm="",elephantNo="",herdNo="",calfNo="",tuskerNo="",maleNo="",femaleNo="",
           location_nm="",lat_deg="",lat_min="",lat_sec="",lng_degree="",lng_min="",lng_sec="",fromTime="",toTime="",altitude="",accuracy="";

    private DBhelper DbHelper;
    public String token="",checkInternet_status="",divisionNmFrom_pref="",userId="";
    SessionManager session;
    GPSTracker gps;
    public LocationRequest locationRequest;
    final static int REQUEST_LOCATION = 199,Camera_REQUEST_CODE=104,IMEI_REQUEST_CODE=111;
    GoogleApiClientHelperClass googleApiClientHelperClass;
    PermissionHelperClass permissionHelperClass;
    ImageView direct_camera_img;
    Uri image_uri=null;
    String img_url_str="";
    private String pictureImagePath = "",selectedFromTime="",compass_direction="",filenm="";
    Bitmap myBitmap,viewBitmap;
    TimePickerDialog fromTimePicker,toTimePicker;
    long fromtime;
    SimpleDateFormat date_sdf = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat from_sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    String currentdateFormat = date_sdf.format(new Date());
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.left_to_right_anim,
                R.anim.left_to_right_anim);
        setContentView(R.layout.activity_elephant_report);

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
//
//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//                String str = sdf.format(new Date());
//                dateofmov.setError(null);
////                final Calendar calender = Calendar.getInstance();
//                mYear = mcalender.get(Calendar.YEAR);
//                mMonth = mcalender.get(Calendar.MONTH);
//                mDay = mcalender.get(Calendar.DAY_OF_MONTH);
//                DatePickerDialog dpd = new DatePickerDialog(ElephantReport.this,
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
//                                dateofmov.setText(d + "-" + m + "-" + year);
////                                dateofmov.setText(m + "-" + d + "-" + year);
//                                dateformatForAPi=m + "-" + d + "-" + year+" "+str;
////                                Toast.makeText(ElephantReport.this, dateformatForAPi, Toast.LENGTH_SHORT).show();
//
//                            }
//                        }, mYear, mMonth, mDay);
//
//                dpd.getDatePicker().setMaxDate(mcalender.getTimeInMillis());
//                dpd.show();
//            }
//        });
//        dateformatForAPi= PermissionUtils.getCurrentDate("dd-MM-yyyy");

        from_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Calendar mcurrentTime = Calendar.getInstance();
                try {

                    Calendar c = Calendar.getInstance();
                    int hour = mcalender.get(Calendar.HOUR_OF_DAY);
                    int minute = mcalender.get(Calendar.MINUTE);
//                TimePickerDialog mTimePicker;
                    TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (selectedHour < 10) {
                                choosedFromHour = "0" + selectedHour;
                            } else {
                                choosedFromHour = "" + selectedHour;
                            }
                            if (selectedMinute < 10) {
                                choosedFromMinute = "0" + selectedMinute;
                            } else {
                                choosedFromMinute = "" + selectedMinute;
                            }

                            selectedFromTime=currentdateFormat+" "+choosedFromHour + ":" + choosedFromMinute;
                            fromtime =   PermissionUtils.convert_date_to_timestamp1("dd-MM-yyyy HH:mm",
                                    selectedFromTime);

                            if(fromtime>System.currentTimeMillis()){
                                callSnackBarOnTop("From time must be less than current time !");
                                from_time.setText("From Time");

                            }else{
                                from_time.setText(choosedFromHour + ":" + choosedFromMinute);
                            }

                            String a = choosedFromHour + choosedFromMinute;
//                            finalfrom = Integer.parseInt(a);
//                            Log.d("finalform", "finalform" + finalfrom);
                        }
                    };

                    c.add(Calendar.YEAR, 5);

                    fromTimePicker = new TimePickerDialog(ElephantReport.this,timeSetListener,
                            c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true);

                    fromTimePicker.show();
                    fromTimePicker.setTitle("Select Time");


                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        to_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                try {
                    // Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcalender.get(Calendar.HOUR_OF_DAY);
                    int minute = mcalender.get(Calendar.MINUTE);

                    toTimePicker = new TimePickerDialog(ElephantReport.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            if (selectedHour < 10) {
                                choosedToHour = "0" + selectedHour;
                            } else {
                                choosedToHour = "" + selectedHour;
                            }
                            if (selectedMinute < 10) {
                                choosedToMinute = "0" + selectedMinute;
                            } else {
                                choosedToMinute = "" + selectedMinute;
                            }

                            selectedFromTime=currentdateFormat+" "+choosedToHour + ":" + choosedToMinute;
                            long toTime =   PermissionUtils.convert_date_to_timestamp1("dd-MM-yyyy HH:mm",
                                    selectedFromTime);

                            if(toTime >= fromtime && toTime<=System.currentTimeMillis()){
                                to_time.setText(choosedToHour + ":" + choosedToMinute);
                            }else{
                                to_time.setText("To Time");
                                callSnackBarOnTop("To time must be in between from time & current time !");
                            }

                            String a = choosedFromHour + choosedFromMinute;

                        }
                    }, hour, minute, true);//Yes 24 hour time
                    toTimePicker.setTitle("Select Time");
                    toTimePicker.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

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

        direct_camera_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cam_intent,Camera_REQUEST_CODE);
                if (!pictureImagePath.equalsIgnoreCase("")){
                    callImageViewDialog(ElephantReport.this,
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

                Log.i("altitude-",altitude+" acc- "+accuracy);

//                Toast.makeText(getApplicationContext(), ""+divCode+"--"+rangeCode+"---"+secCode+"----"+beatCode+"check - "+divisionValue, Toast.LENGTH_SHORT).show();
//
                if (dateofmov.getText().toString().matches("")) {
                    Snackbar.make(main_ll,"Select date of movement ",Snackbar.LENGTH_SHORT).show();
                }else if (locationname.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter location", Snackbar.LENGTH_SHORT).show();
                } else if (from_time.getText().toString().matches("")) {
                }else if (from_time.getText().toString().matches("") || from_time.getText().toString().equalsIgnoreCase("From Time")) {
                    Snackbar.make(main_ll, "Select From time", Snackbar.LENGTH_SHORT).show();
                } else if (to_time.getText().toString().matches("") || to_time.getText().toString().equalsIgnoreCase("To Time")) {
                    Snackbar.make(main_ll, "Select To time", Snackbar.LENGTH_SHORT).show();
                }  else if (noofelephants.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter total no. of elephants", Snackbar.LENGTH_SHORT).show();
                } else if (herd.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter Herd", Snackbar.LENGTH_SHORT).show();
                } else if (male.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter Male", Snackbar.LENGTH_SHORT).show();
                } else if (female.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter Female", Snackbar.LENGTH_SHORT).show();
                } else if (calf.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter calf", Snackbar.LENGTH_SHORT).show();
                } else if (tusker.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter Tusker", Snackbar.LENGTH_SHORT).show();
                } else if (image_uri==null){
                    Snackbar.make(main_ll, "Please provide an image ", Snackbar.LENGTH_SHORT).show();
                }
                else {

                    PermissionUtils.hideKeyboard(ElephantReport.this);//Hide Keyboard

                    division_nm = divCode;
                    range_nm = rangeCode;
                    section_nm = secCode;
                    beat_nm = beatCode;

                    location_nm = locationname.getText().toString();
                    lat_deg = ""+latitude; //For latitude
//                    lat_deg = latdeg.getText().toString();
                    lat_min = latmin.getText().toString();
                    lat_sec = latsec.getText().toString();

                    lng_degree = ""+longitude; //For longitude
//                    lng_degree = londeg.getText().toString();
                    lng_min = lonmin.getText().toString();
                    lng_sec = lonsec.getText().toString();
                    elephantNo = noofelephants.getText().toString();
                    herdNo = herd.getText().toString();
                    maleNo = male.getText().toString();
                    femaleNo = female.getText().toString();
                    calfNo = calf.getText().toString();
                    tuskerNo = tusker.getText().toString();
                    fromTime=PermissionUtils.getCurrentDate("dd-MM-yyyy")+" "+from_time.getText().toString().trim()+":00";
                    toTime=PermissionUtils.getCurrentDate("dd-MM-yyyy")+" "+to_time.getText().toString().trim()+":12";

                    String match_type=PermissionUtils.calculateElephant(Integer.parseInt(elephantNo),
                            Integer.parseInt(herdNo),
                            Integer.parseInt(tuskerNo),
                            Integer.parseInt(maleNo),
                            Integer.parseInt(femaleNo),
                            Integer.parseInt(calfNo),ElephantReport.this);

                    if (match_type.equalsIgnoreCase("match")){

                        dynamicAlertDialog(ElephantReport.this,"Please check your data before save !","Warning","Yes");
                    }
                    else {
                        main_ll.scrollTo(0,main_ll.getBottom());
                        noofelephants.requestFocus();
//                        Toast.makeText(ElephantReport.this, "Data not correctly added !", Toast.LENGTH_SHORT).show();
                        dynamicAlertDialog(ElephantReport.this,"Data not correctly added ! !","Warning","No");

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

        toolbar=findViewById(R.id.toolbar_id);

        back=toolbar.findViewById(R.id.back);
        DbHelper = new DBhelper(this);

        toolbar_title=toolbar.findViewById(R.id.toolbar_title);
        from_time=findViewById(R.id.from_time);
        to_time=findViewById(R.id.to_time);
        dateofmov=findViewById(R.id.dateof);
        latdeg=findViewById(R.id.latdeg);
        latmin=findViewById(R.id.latmin);
        latsec=findViewById(R.id.latsec);
        londeg=findViewById(R.id.londeg);
        lonmin=findViewById(R.id.lonmin);
        lonsec=findViewById(R.id.lonsec);
        locationname=findViewById(R.id.locationname);
        noofelephants=findViewById(R.id.noofelephants);
        herd=findViewById(R.id.herd);
        tusker=findViewById(R.id.tusker);
        male=findViewById(R.id.male);
        female=findViewById(R.id.female);
        calf=findViewById(R.id.calf);
        altitude_txt=findViewById(R.id.altitude_txt);
        accuracy_txt=findViewById(R.id.accuracy_txt);
        main_ll=findViewById(R.id.main_ll);

        division_spinner=findViewById(R.id.division);
        range_spinner=findViewById(R.id.range);
        section_spinner=findViewById(R.id.section);
        beat_spinner=findViewById(R.id.beat);
        direct_camera_img=findViewById(R.id.direct_camera_img);
        save_btn=findViewById(R.id.loc);
        session=new SessionManager(ElephantReport.this);
        permissionHelperClass = new PermissionHelperClass(ElephantReport.this);
        commonMethods=new CommonMethods(ElephantReport.this);

        toolbar_title.setText(R.string.elephant_report);

        token=session.getToken();
        divisionNmFrom_pref=session.getDivision();
        userId=session.getUserID();

        division_spinner.setTitle("Select Division");
        division_spinner.setPositiveButton("OK");

        range_spinner.setTitle("Select Range");
        range_spinner.setPositiveButton("OK");

        section_spinner.setTitle("Select Section");
        section_spinner.setPositiveButton("OK");

        beat_spinner.setTitle("Select Beat");
        beat_spinner.setPositiveButton("OK");

//        readPhoneStatePermission();//Read PhoneStatePermission

        getImeiNo();//Getting Imei No(from READ PHONE STATE)

        dateformatForAPi= PermissionUtils.getCurrentDate("dd-MM-yyyy");
        currentDate= PermissionUtils.getCurrentDate("dd-MM-yyyy HH:mm:ss");
        dateofmov.setText(dateformatForAPi);

        permissionUtils = new PermissionUtils(ElephantReport.this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

        getLocation();

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


          }catch (Exception e){
            e.printStackTrace();
        }

    }

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

    //location code start
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            gps=new GPSTracker(ElephantReport.this);

            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(this).equalsIgnoreCase("false")){
//                        googleApiClientHelperClass.buildGoogleApiClient();
                        gps.showSettingsAlert(ElephantReport.this);
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

    public void callGoogleApiClient(){
        try {

            googleApiClientHelperClass = new GoogleApiClientHelperClass(ElephantReport.this);
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
                    if (ActivityCompat.checkSelfPermission(ElephantReport.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(ElephantReport.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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
//                        long acc=""
                        accuracy_txt.setText(accuracy);

                        latdeg(latitude, longitude);

//                        Toast.makeText(ElephantReport.this, "Getting location..."+location.getBearingAccuracyDegrees(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
            lonsec.setText(""+df1.format(end1)+ " \u005c\u0022");

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

    public void saveData_inLocal(ElephantCorridorData elephantCorridorData, String message) {
        try {
        DbHelper.open();
        DbHelper.insertElephantReport(elephantCorridorData);
        DbHelper.close();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ElephantReport.this, R.style.AlertDialogCustom));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ElephantReport.this.startActivity(new Intent(ElephantReport.this, ElephantReport.class));
                ElephantReport.this.finish();
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

    @Override
    public void onBackPressed() {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            builder.setMessage("Are you sure you want to exit ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            ElephantReport.this.finish();
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

    public void dynamicAlertDialog(Context context,String message,String title,String buttonNeed){

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));

        if (buttonNeed.equalsIgnoreCase("yes")){
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

//                            callFileUploadApi(imeiNo,userId,img_url_str); //File upload Api

                            ElephantCorridorData elephantCorridorData = new ElephantCorridorData
                                    (division_nm, range_nm, section_nm, beat_nm, location_nm, currentDate,
                                            elephantNo, lat_deg, lat_min, lat_sec, lng_degree, lng_min, lng_sec, herdNo,
                                            maleNo, femaleNo, calfNo, img_url_str, "NA", fromTime,
                                            toTime, tuskerNo,userId,"",altitude,accuracy,imeiNo,"");

                            saveData_inLocal(elephantCorridorData,"Data saved successfully");

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
                            File imgFile = new  File(pictureImagePath);
                            if(imgFile.exists()){
                                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                                camera_img.setImageBitmap(myBitmap);
                                image_uri=getImageUri(ElephantReport.this,myBitmap);
                                img_url_str=getRealPathFromURI(image_uri);

                                filenm=pictureImagePath.replace("/storage/emulated/0/WildLifeAppImages/","");

//                                replaceFile(filenm);

                            }
//                            callCompassDirection();

                            //Call camera geotag dialog
                            callCameraGeotagDialog(ElephantReport.this,myBitmap,latitude,longitude,
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

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(ElephantReport.this.getContentResolver(), photo, "elephant_report"+ Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();

        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void getImeiNo(){
        String imei="";
        try {
            Random r = new Random();
            int randomNumber = r.nextInt(1000);
            imeiNo = ""+randomNumber;
//            Toast.makeText(this, ""+imeiNo, Toast.LENGTH_SHORT).show();
            Log.i("IMEI_NO-",""+imeiNo);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void readPhoneStatePermission(){
        if (ActivityCompat.checkSelfPermission(ElephantReport.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ElephantReport.this, new String[]{Manifest.permission.READ_PHONE_STATE}, IMEI_REQUEST_CODE);
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

    public void callFileUploadApi(String imeiNo,String userID,String image_nm){
        try {

            RetrofitInterface retrofitInterface = RetrofitClient.getNewFileUploadClient().create(RetrofitInterface.class);

            File file = new File(image_nm);
            //creating request body for file
            okhttp3.RequestBody requestFile =
                    okhttp3.RequestBody.create(okhttp3.MediaType.parse(getContentResolver().toString()),file);

            MultipartBody.Part image_body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            retrofitInterface.updateImage(image_body,userID,"report").enqueue(new Callback<FileUploadResponse>() {
//            retrofitInterface.addfileUpload(imeiNo,userID,"report",image_body).enqueue(new Callback<FileUploadResponse>() {
//            retrofitInterface.addfileUpload(imeiNo,userID,image_body).enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.isSuccessful()){

                        Toast.makeText(ElephantReport.this, "Upload successfull .File name is - "+response.body().getFileName(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(ElephantReport.this, ""+response.body().getFileName(), Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(ElephantReport.this, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    Toast.makeText(ElephantReport.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });


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
                        MediaStore.Images.Media.insertImage(getContentResolver(), bm, "elephant_report"+ Calendar.getInstance().getTime(), null);

                        image_uri=getImageUri(ElephantReport.this,bm);
//                   String img_url_str=getEncoded64ImageStringFromBitmap(bp);
                        img_url_str=getRealPathFromURI(image_uri);

                        direct_camera_img.setImageBitmap(bm);

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

                Uri outputFileUri = FileProvider.getUriForFile(ElephantReport.this,
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
                Uri outputFileUri = FileProvider.getUriForFile(ElephantReport.this,
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

    public void callSnackBarOnTop(String message){
        try {
            Snackbar snackbar=Snackbar.make(main_ll, message, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbar.setBackgroundTint(getResources().getColor(R.color.tranparent_red_lil));
            view.setLayoutParams(params);
            snackbar.show();

        }catch (Exception e){
            e.printStackTrace();
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
                        direct_camera_img.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));

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
