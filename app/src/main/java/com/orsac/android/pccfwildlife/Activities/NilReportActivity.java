package com.orsac.android.pccfwildlife.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.orsac.android.pccfwildlife.Model.NilReportingData;
import com.orsac.android.pccfwildlife.MyUtils.GPSTracker;
import com.orsac.android.pccfwildlife.MyUtils.GoogleApiClientHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SQLiteDB.DBhelper;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

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
import androidx.core.app.ActivityCompat;
import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class NilReportActivity extends AppCompatActivity implements LocationListener,
        com.google.android.gms.location.LocationListener,PermissionUtils.PermissionResultCallback{

    Toolbar toolbar;
    ImageView back;
    TextView toolbar_title;
    MaterialTextView from_time,to_time,dateofmov;
    SearchableSpinner division_spinner,range_spinner,section_spinner,beat_spinner;
//    AppCompatSpinner division_spinner,range_spinner,section_spinner,beat_spinner;
    AppCompatButton save_btn;
    ScrollView main_ll;
    TextInputEditText locationname,remarks;

    String choosedFromHour="", choosedFromMinute="", choosedToHour="", choosedToMinute = "",dateformatForAPi="",
            imeiNo="",altitude="",accuracy="",currentDate="";
    private int finalfrom, finalto,mYear, mMonth, mDay;
    String m, d;

    SimpleDateFormat date_sdf = new SimpleDateFormat("dd-MM-yyyy");
    String currentdateFormat = date_sdf.format(new Date());
    Calendar mcalender = Calendar.getInstance();
    private DBhelper DbHelper;
    SQLiteDatabase db;

    public HashMap<String, String> divKey;
    public HashMap<String, String> rangeKey;
    public HashMap<String, String> secKey;
    public HashMap<String, String> beatkey;
    String divisionValue="", rangeValue="", secValue="", beatValue="";
    public String divCode="", rangeCode="", secCode="", beatCode="";
    public String division_nm="",range_nm="",section_nm="",beat_nm="",
            location_nm="",lat_deg="0",lat_min="0",lat_sec="0",lng_degree="0",lng_min="0",lng_sec="0",fromTime="00:00",toTime="00:00",
            selectedFromTime="",selectedToTime="";
    public String token="",checkInternet_status="",divisionNmFrom_pref="",userId="",remarks_txt="";
    SessionManager session;
    ArrayAdapter<String> dataAdapter, rangedataAdapter,secdataAdapter,beatdataAdapter;
    List<String> divName,rangeName,secName,beatName;
    TimePickerDialog fromTimePicker,toTimePicker;
    long fromtime;

    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3 * 1;// for 3sec
    Location location;
    double latitude;
    double longitude;
    GPSTracker gps;
    public LocationRequest locationRequest;
    final static int REQUEST_LOCATION = 199,Camera_REQUEST_CODE=104,IMEI_REQUEST_CODE=111;
    GoogleApiClientHelperClass googleApiClientHelperClass;
    PermissionHelperClass permissionHelperClass;
    ArrayList<String> permissions = new ArrayList<>();
    PermissionUtils permissionUtils;
    boolean isPermissionGranted;
    TextInputEditText latdeg,latmin,latsec,londeg,lonmin,lonsec,altitude_txt,accuracy_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.left_to_right_anim,
                R.anim.left_to_right_anim);
        setContentView(R.layout.activity_nil_report);

        initData(); //Initializing data

        token=session.getToken();
        divisionNmFrom_pref=session.getDivision();
        userId=session.getUserID();

        bindDivision();
        bindRange("");
        bindSection("");
        bindBeat("");




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
//                DatePickerDialog dpd = new DatePickerDialog(AttendActivity.this,
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
                    }
                };
//                fromTimePicker = new TimePickerDialog(AttendActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//
//                        if (selectedHour < 10) {
//                            choosedFromHour = "0" + selectedHour;
//                        } else {
//                            choosedFromHour = "" + selectedHour;
//                        }
//                        if (selectedMinute < 10) {
//                            choosedFromMinute = "0" + selectedMinute;
//                        } else {
//                            choosedFromMinute = "" + selectedMinute;
//                        }
//                        from_time.setText(choosedFromHour + ":" + choosedFromMinute);
//                        String a = choosedFromHour + choosedFromMinute;
//                        finalfrom = Integer.parseInt(a);
//                        Log.d("finalform", "finalform" + finalfrom);
//
////                        myTimePickerClass.onTimeChanged(timePicker,Integer.parseInt(choosedToHour),Integer.parseInt(choosedToMinute));
//                    }
//                }, hour, minute, true);//Yes 24 hour time
//                fromTimePicker.setTitle("Select Time");

                c.add(Calendar.YEAR, 5);

                fromTimePicker = new TimePickerDialog(NilReportActivity.this,timeSetListener,
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
                    //                Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcalender.get(Calendar.HOUR_OF_DAY);
                    int minute = mcalender.get(Calendar.MINUTE);

                    toTimePicker = new TimePickerDialog(NilReportActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateofmov.getText().toString().matches("")) {
                    Snackbar.make(main_ll,"Select date of movement ",Snackbar.LENGTH_SHORT).show();
                } else if (locationname.getText().toString().matches("")) {
                    Snackbar.make(main_ll, "Please enter location", Snackbar.LENGTH_SHORT).show();
                }else if (from_time.getText().toString().matches("") || from_time.getText().toString().equalsIgnoreCase("From Time")) {
                    Snackbar.make(main_ll, "Select From time", Snackbar.LENGTH_SHORT).show();
                } else if (to_time.getText().toString().matches("") || to_time.getText().toString().equalsIgnoreCase("To Time")) {
                    Snackbar.make(main_ll, "Select To time", Snackbar.LENGTH_SHORT).show();
                }else if (divisionValue.equalsIgnoreCase("Select Division")) {
                    Snackbar.make(main_ll, "Select Division", Snackbar.LENGTH_SHORT).show();
                } else if (rangeValue.equalsIgnoreCase("Select Range")) {
                    Snackbar.make(main_ll, "Select Range", Snackbar.LENGTH_SHORT).show();
                } else if (secValue.equalsIgnoreCase("Select Section")) {
                    Snackbar.make(main_ll, "Select Section", Snackbar.LENGTH_SHORT).show();
                } else if (beatValue.equalsIgnoreCase("Select Beat")) {
                    Snackbar.make(main_ll, "Select Beat", Snackbar.LENGTH_SHORT).show();
                } else {

                    division_nm = divCode;
                    range_nm = rangeCode;
                    section_nm = secCode;
                    beat_nm = beatCode;
                    remarks_txt=remarks.getText().toString().trim();

                    location_nm = locationname.getText().toString();
                    lat_deg = ""+latitude; //For latitude
//                    lat_deg = latdeg.getText().toString();
                    lat_min = latmin.getText().toString();
                    lat_sec = latsec.getText().toString();

                    lng_degree = ""+longitude; //For longitude
//                    lng_degree = londeg.getText().toString();
                    lng_min = lonmin.getText().toString();
                    lng_sec = lonsec.getText().toString();

                    location_nm = locationname.getText().toString();
                    fromTime= PermissionUtils.getCurrentDate("dd-MM-yyyy")+" "+from_time.getText().toString().trim()+":00";
                    toTime=PermissionUtils.getCurrentDate("dd-MM-yyyy")+" "+to_time.getText().toString().trim()+":05";

//                    NilReportingData nilReportingData = new NilReportingData
//                            (dateformatForAPi,division_nm, range_nm, section_nm, beat_nm,userId);
//
//                    saveData_inLocal(nilReportingData,"Data successfully Stored ");

                    dynamicAlertDialog(NilReportActivity.this,"Please check your data before save !","Warning","Yes");


                }

            }
        });





    }

    public void initData(){
        try {

        toolbar=findViewById(R.id.toolbar_id);

        back=toolbar.findViewById(R.id.back);
        toolbar_title=toolbar.findViewById(R.id.toolbar_title);

        toolbar_title.setText(R.string.nil_report);

        dateofmov=findViewById(R.id.dateof);
        from_time=findViewById(R.id.from_time);
        to_time=findViewById(R.id.to_time);
        locationname=findViewById(R.id.locationname);
        remarks=findViewById(R.id.remarks);
        main_ll=findViewById(R.id.main_ll);

        division_spinner=findViewById(R.id.division);
        range_spinner=findViewById(R.id.range);
        section_spinner=findViewById(R.id.section);
        beat_spinner=findViewById(R.id.beat);
        save_btn=findViewById(R.id.loc);
        latdeg=findViewById(R.id.latdeg);
        latmin=findViewById(R.id.latmin);
        latsec=findViewById(R.id.latsec);
        londeg=findViewById(R.id.londeg);
        lonmin=findViewById(R.id.lonmin);
        lonsec=findViewById(R.id.lonsec);
        altitude_txt=findViewById(R.id.altitude_txt);
        accuracy_txt=findViewById(R.id.accuracy_txt);

        DbHelper = new DBhelper(this);
        session=new SessionManager(NilReportActivity.this);
        permissionHelperClass = new PermissionHelperClass(NilReportActivity.this);

        divName=new ArrayList<String>();
        rangeName=new ArrayList<String>();
        secName=new ArrayList<String>();
        beatName=new ArrayList<String>();
        permissionUtils = new PermissionUtils(NilReportActivity.this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

        if (permissionHelperClass.hasLocationPermission()) {
            callGoogleApiClient();
        } else {
            permissionHelperClass.requestLocationPermission();//request location permissions

        }
//        readPhoneStatePermission();//Read PhoneStatePermission

        getLocation();

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


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void bindDivision() {
        try {
            divName=new ArrayList<String>();
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
            beatdataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, beatName);
            beatdataAdapter.setDropDownViewResource(R.layout.spinnerfront);
            beat_spinner.setAdapter(beatdataAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void saveData_inLocal(NilReportingData nilReportingData, String message) {
        try {
            DbHelper.open();
            DbHelper.insertNilReport(nilReportingData);
            DbHelper.close();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(NilReportActivity.this, R.style.AlertDialogCustom));
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NilReportActivity.this.startActivity(new Intent(NilReportActivity.this, NilReportActivity.class));
                    NilReportActivity.this.finish();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        NilReportActivity.this.finish();
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
    }

    public void dynamicAlertDialog(Context context, String message, String title, String buttonNeed){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));

            if (buttonNeed.equalsIgnoreCase("yes")) {
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                NilReportingData nilReportingData = new NilReportingData
                                        (currentDate, location_nm, fromTime, toTime, division_nm, range_nm, section_nm, beat_nm, userId, remarks.getText().toString().trim()
                                                , imeiNo,lat_deg, lat_min, lat_sec, lng_degree, lng_min, lng_sec,altitude,accuracy);

                                saveData_inLocal(nilReportingData, "Data saved successfully ");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    private void getImeiNo(){
        String imei="";
        try {
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(AttendActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(AttendActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, IMEI_REQUEST_CODE);
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
        if (ActivityCompat.checkSelfPermission(NilReportActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NilReportActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, IMEI_REQUEST_CODE);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        //if()
        super.onPause();

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
    //location code start
    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            gps=new GPSTracker(NilReportActivity.this);

            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(this).equalsIgnoreCase("false")){
//                        googleApiClientHelperClass.buildGoogleApiClient();
                        gps.showSettingsAlert(NilReportActivity.this);
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


    public void callGoogleApiClient(){
        try {

            googleApiClientHelperClass = new GoogleApiClientHelperClass(NilReportActivity.this);
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
                    if (ActivityCompat.checkSelfPermission(NilReportActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(NilReportActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
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

}
