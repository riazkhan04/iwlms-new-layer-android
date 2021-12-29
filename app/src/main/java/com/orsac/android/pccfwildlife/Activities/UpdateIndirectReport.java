package com.orsac.android.pccfwildlife.Activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.SQLiteDB.DBhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class UpdateIndirectReport extends AppCompatActivity {

    AppCompatButton update_button;
    Toolbar toolbar;
    ImageView back;
    TextView toolbar_title;
    MaterialTextView from_time,to_time,dateofmov;
    TextInputEditText latdeg,latmin,latsec,londeg,lonmin,lonsec,locationname,
            noofelephants,herd,tusker,male,female,calf;
    AppCompatSpinner division_spinner,range_spinner,section_spinner,beat_spinner,reportType_spinner;
    ScrollView main_ll;
    String choosedFromHour="", choosedFromMinute="", choosedToHour="", choosedToMinute = "";
    private int finalfrom, finalto,mYear, mMonth, mDay;
    String m, d;
    Calendar mcalender = Calendar.getInstance();
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
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
    String divisionValue="", rangeValue="", secValue="", beatValue="",repTypeValue="";
    public String divCode="", rangeCode="", secCode="", beatCode="",dateformatForAPi="";
    public String division_nm="",range_nm="",section_nm="",beat_nm="",elephantNo="",herdNo="",calfNo="",tuskerNo="",maleNo="",femaleNo="",
            location_nm="",lat_deg="",lat_min="",lat_sec="",lng_degree="",lng_min="",lng_sec="",fromTime="",toTime="";

    private DBhelper DbHelper;
    public String token="",checkInternet_status="",divisionNmFrom_pref="",userId="";
    SessionManager session;
    String[] reptypes = new String[]{
            "Select Reporting",
            "Trumpeting call/Sound",
            "Fresh Dungs",
            "Fresh Footprints",
            "Others"
    };
    List<String> repList;
    LinearLayout remarkslayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indirect_report);

        initData();

        update_button.setText("Update");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        location_nm=getIntent().getStringExtra("location_nm");
        dateformatForAPi=getIntent().getStringExtra("dom");
        fromTime=getIntent().getStringExtra("from_time");
        toTime=getIntent().getStringExtra("to_time");
        division_nm=getIntent().getStringExtra("division");
        range_nm=getIntent().getStringExtra("range");
        section_nm=getIntent().getStringExtra("section");
        beat_nm=getIntent().getStringExtra("beat");
        elephantNo=getIntent().getStringExtra("total_no");
        herdNo=getIntent().getStringExtra("herd");
        tuskerNo=getIntent().getStringExtra("tusker");
        maleNo=getIntent().getStringExtra("male");
        femaleNo=getIntent().getStringExtra("female");
        calfNo=getIntent().getStringExtra("calf");
        lat_deg=getIntent().getStringExtra("lat_deg");
        lat_min=getIntent().getStringExtra("lat_min");
        lat_sec=getIntent().getStringExtra("lat_sec");
        lng_degree=getIntent().getStringExtra("lng_deg");
        lng_min=getIntent().getStringExtra("lng_min");
        lng_sec=getIntent().getStringExtra("lng_sec");
        repTypeValue=getIntent().getStringExtra("report_type");

        repList = new ArrayList<>(Arrays.asList(reptypes));
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, repList);
        batchAdapter.setDropDownViewResource(R.layout.spinnerfront);
        reportType_spinner.setAdapter(batchAdapter);
        if (repTypeValue != null) {
            int spinnerPosition = batchAdapter.getPosition(repTypeValue);
            reportType_spinner.setSelection(spinnerPosition);
        }

        reportType_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repTypeValue = (String) parent.getItemAtPosition(position);
                if (repTypeValue.equals("Others")) {
                    remarkslayout.setVisibility(View.VISIBLE);
                }
                else {
                    remarkslayout.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bindDivision();
        bindRange("");
        bindSection("");
        bindBeat("");

        locationname.setEnabled(false);//disable the text

        if (dateformatForAPi.equalsIgnoreCase("")|| dateformatForAPi==null){
            dateofmov.setText("NA");
        }
        else {
            dateofmov.setText(dateformatForAPi);
        }

        if (location_nm.equalsIgnoreCase("")|| location_nm==null){
            locationname.setText("NA");
        }
        else {
            locationname.setText(location_nm);

        }

        if (fromTime.equalsIgnoreCase("")|| fromTime==null){
            from_time.setText("NA");
        }
        else {
            from_time.setText(fromTime);
        }
        if (toTime.equalsIgnoreCase("")|| toTime==null){
            to_time.setText("NA");
        }
        else {
            to_time.setText(toTime);
        }

        if (elephantNo.equalsIgnoreCase("")|| elephantNo==null){
            noofelephants.setText("0");
        }
        else {
            noofelephants.setText(elephantNo);
        }

        if (herdNo.equalsIgnoreCase("")|| herdNo==null){
            herd.setText("0");
        }
        else {
            herd.setText(herdNo);
        }
        if (tuskerNo.equalsIgnoreCase("")|| tuskerNo==null){
            tusker.setText("0");
        }
        else {
            tusker.setText(tuskerNo);
        }
        if (maleNo.equalsIgnoreCase("")|| maleNo==null){
            male.setText("0");
        }
        else {
            male.setText(maleNo);
        }

        if (femaleNo.equalsIgnoreCase("")|| femaleNo==null){
            female.setText("0");
        }
        else {
            female.setText(femaleNo);
        }
        if (calfNo.equalsIgnoreCase("")|| calfNo==null){
            calf.setText("0");
        }
        else {
            calf.setText(calfNo);
        }
        if (lat_deg.equalsIgnoreCase("")|| lat_deg==null){
            latdeg.setText("0");
        }
        else {
            latdeg.setText(lat_deg);
        }
        if (lat_min.equalsIgnoreCase("")|| lat_min==null){
            latmin.setText("0");
        }
        else {
            latmin.setText(lat_min);
        }
        if (lat_sec.equalsIgnoreCase("")|| lat_sec==null){
            latsec.setText("0");
        }
        else {
            latsec.setText(lat_sec);
        }
//        -------
        if (lng_degree.equalsIgnoreCase("")|| lng_degree==null){
            londeg.setText("0");
        }
        else {
            londeg.setText(lng_degree);
        }
        if (lng_min.equalsIgnoreCase("")|| lng_min==null){
            lonmin.setText("0");
        }
        else {
            lonmin.setText(lng_min);
        }
        if (lng_sec.equalsIgnoreCase("")|| lng_sec==null){
            lonsec.setText("0");
        }
        else {
            lonsec.setText(lng_sec);
        }

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

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repTypeValue.equalsIgnoreCase("Select Reporting")){
                    Toast.makeText(UpdateIndirectReport.this, "Select Reporting", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(UpdateIndirectReport.this, repTypeValue, Toast.LENGTH_SHORT).show();

                }

            }
        });



    }

    public void initData() {

        toolbar=findViewById(R.id.toolbar_id);

        back=toolbar.findViewById(R.id.back);
        DbHelper = new DBhelper(this);

        update_button=findViewById(R.id.loc);
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
        main_ll=findViewById(R.id.main_ll);

        division_spinner=findViewById(R.id.division);
        range_spinner=findViewById(R.id.range);
        section_spinner=findViewById(R.id.section);
        beat_spinner=findViewById(R.id.beat);
        reportType_spinner=findViewById(R.id.repType);
        remarkslayout=findViewById(R.id.remarkslayout);
        session=new SessionManager(UpdateIndirectReport.this);

        toolbar_title.setText("Update Indirect Report");

        token=session.getToken();
        divisionNmFrom_pref=session.getDivision();
        userId=session.getUserID();

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
        division_spinner.setEnabled(false);//disable spinner
//        division_spinner.setClickable(false);
        division_spinner.setAdapter(dataAdapter);
        if (division_nm != null) {
            int spinnerPosition = dataAdapter.getPosition(division_nm);
            division_spinner.setSelection(spinnerPosition);
        }
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
        range_spinner.setEnabled(false);//disable spinner
        range_spinner.setAdapter(dataAdapter);
        if (range_nm != null) {
            int spinnerPosition = dataAdapter.getPosition(range_nm);
            range_spinner.setSelection(spinnerPosition);
        }
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
        section_spinner.setEnabled(false);//disable spinner
        section_spinner.setAdapter(dataAdapter);
        if (section_nm != null) {
            int spinnerPosition = dataAdapter.getPosition(section_nm);
            section_spinner.setSelection(spinnerPosition);
        }

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
        beat_spinner.setEnabled(false);//disable spinner
        beat_spinner.setAdapter(dataAdapter);
        if (beat_nm != null) {
            int spinnerPosition = dataAdapter.getPosition(beat_nm);
            beat_spinner.setSelection(spinnerPosition);
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        UpdateIndirectReport.this.finish();
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

}
