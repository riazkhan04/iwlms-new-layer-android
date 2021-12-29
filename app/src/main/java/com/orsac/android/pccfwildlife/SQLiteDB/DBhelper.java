package com.orsac.android.pccfwildlife.SQLiteDB;

/**
 * Created by sparcnewmis on 09-08-2020.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.orsac.android.pccfwildlife.Model.ElephantCorridorData;
import com.orsac.android.pccfwildlife.Model.IncidentReportingData;
import com.orsac.android.pccfwildlife.Model.IndirectReportingData;
import com.orsac.android.pccfwildlife.Model.NilReportingData;
import com.orsac.android.pccfwildlife.Model.AllLatLngData;

import java.util.ArrayList;

public class DBhelper {
//    public static final String DATABASE_NAME = "pccfwl.db";
    public static final String DATABASE_NAME = "pccfwl_data_V1.db";
    private static final int DATABASE_VERSION = 7;

    //elephant report creation
    public static String ELEPHANT_CORRIDOR_TABLE_NAME = "elephantReport";
    public static String INDIRECT_REPORT_TABLE_NAME = "indirectReport";
    public static String INCIDENT_REPORT_TABLE_NAME = "incidentReport";
    public static String NIL_REPORT_TABLE_NAME = "nilReport";
    public static String ALL_LAT_LNG_TABLE_NAME = "AllLatLong";
    public static String ELEPHANT_CORRIDOR_SL_NO = "slno";
    public static String ELEPHANT_CIRCLE_ID = "Circle_Id";
    public static String ELEPHANT_CIRCLE_NAME = "Circle_Name";
    public static String ELEPHANT_CORRIDOR_DIVISION_ID = "Division_Id";
    public static String ELEPHANT_CORRIDOR_DIVISION_NAME = "Division_Name";
    public static String ELEPHANT_CORRIDOR_RANGE_ID = "Range_ID";
    public static String ELEPHANT_CORRIDOR_RANGE_NAME = "Range_Name";
    public static String ELEPHANT_CORRIDOR_SECTION_ID = "Section_ID";
    public static String ELEPHANT_CORRIDOR_SECTION_NAME = "Section_Name";
    public static String ELEPHANT_CORRIDOR_BEAT_ID = "WlBeat_ID";
    public static String ELEPHANT_CORRIDOR_BEAT_NAME = "WlBeat_Name";
    public static String ELEPHANT_CORRIDOR_LOCATION_NAME = "loc";
    public static String ELEPHANT_CORRIDOR_DATE = "date";
    public static String ELEPHANT_CORRIDOR_NUMBER_ELEPHANT = "noeleph";
    public static String ELEPHANT_CORRIDOR_DEG = "deg";
    public static String ELEPHANT_CORRIDOR_MIN = "min";
    public static String ELEPHANT_CORRIDOR_SEC = "sec";
    public static String ELEPHANT_CORRIDOR_DEG1 = "deg1";
    public static String ELEPHANT_CORRIDOR_MIN1 = "min1";
    public static String ELEPHANT_CORRIDOR_SEC1 = "sec1";
//    public static String ELEPHANT_ISACTIVE = "Elephant_isActive";

    //create elephant corridor
    private static final String CREATE_ELEPHANT_CORRIDOR_TABLE = "CREATE TABLE IF NOT EXISTS " + ELEPHANT_CORRIDOR_TABLE_NAME +
            "( slno INTEGER PRIMARY KEY, " +
            ELEPHANT_CORRIDOR_DIVISION_ID + "TEXT, " +
            "Division_Name TEXT, " +
            ELEPHANT_CORRIDOR_RANGE_ID + "TEXT, " +
            "Range_Name TEXT, " +
            ELEPHANT_CORRIDOR_SECTION_ID + "TEXT, " +
            "Section_Name TEXT, " +
            ELEPHANT_CORRIDOR_BEAT_ID + "TEXT, " +
            "WlBeat_Name TEXT, " +
            "loc TEXT, " +
            "date TEXT, " +
            "noeleph TEXT, " +
            "deg TEXT, " +
            "min TEXT, " +
            "sec TEXT, " +
            "deg1 TEXT, " +
            "min1 TEXT, " +
            "sec1 TEXT, " +
            "herd TEXT, " +
            "male TEXT, " +
            "female TEXT, " +
            "calf TEXT, " +
            "sync_status TEXT, " +
            "image_uri TEXT, " +
            "img_path TEXT, " +
            "fromtime TEXT, " +
            "totime TEXT, " +
            "tusker TEXT, " +
            "report_id TEXT, " +
            "altitude TEXT, " +
            "accuracy TEXT, " +
            "userId TEXT, " +
            "filename TEXT, " +
            "imei TEXT)";

    private static final String CREATE_INDIRECT_REPORT_TABLE = "CREATE TABLE IF NOT EXISTS " + INDIRECT_REPORT_TABLE_NAME +
            "( slno INTEGER PRIMARY KEY, " +
            ELEPHANT_CORRIDOR_DIVISION_ID + "TEXT, " +
            "Division_Name TEXT, " +
            ELEPHANT_CORRIDOR_RANGE_ID + "TEXT, " +
            "Range_Name TEXT, " +
            ELEPHANT_CORRIDOR_SECTION_ID + "TEXT, " +
            "Section_Name TEXT, " +
            ELEPHANT_CORRIDOR_BEAT_ID + "TEXT, " +
            "WlBeat_Name TEXT, " +
            "loc TEXT, " +
            "date TEXT, " +
            "noeleph TEXT, " +
            "deg TEXT, " +
            "min TEXT, " +
            "sec TEXT, " +
            "deg1 TEXT, " +
            "min1 TEXT, " +
            "sec1 TEXT, " +
            "herd TEXT, " +
            "male TEXT, " +
            "female TEXT, " +
            "calf TEXT, " +
            "sync_status TEXT, " +
            "image_uri TEXT, " +
            "img_path TEXT, " +
            "fromtime TEXT, " +
            "totime TEXT, " +
            "tusker TEXT, " +
            "remark TEXT, " +
            "report_id TEXT, " +
            "reportType TEXT, " +
            "altitude TEXT, " +
            "accuracy TEXT, " +
            "userId TEXT, " +
            "filename TEXT, " +
            "imei TEXT)";
    //create nil corridor
    private static final String CREATE_NIL_REPORT_TABLE = "CREATE TABLE IF NOT EXISTS " + NIL_REPORT_TABLE_NAME +
            "( slno INTEGER PRIMARY KEY, " +
            ELEPHANT_CORRIDOR_DIVISION_ID + "TEXT, " +
            "Division_Name TEXT, " +
            ELEPHANT_CORRIDOR_RANGE_ID + "TEXT, " +
            "Range_Name TEXT, " +
            ELEPHANT_CORRIDOR_SECTION_ID + "TEXT, " +
            "Section_Name TEXT, " +
            ELEPHANT_CORRIDOR_BEAT_ID + "TEXT, " +
            "WlBeat_Name TEXT, " +
            "loc TEXT, " +
            "fromtime TEXT, " +
            "totime TEXT, " +
            "date TEXT, " +
            "remark TEXT, " +
            "imei TEXT, " +
            "deg TEXT, " +
            "min TEXT, " +
            "sec TEXT, " +
            "deg1 TEXT, " +
            "min1 TEXT, " +
            "sec1 TEXT, " +
            "altitude TEXT, " +
            "accuracy TEXT, " +
            "sync_status TEXT)";
    //Create incident report
    private static final String CREATE_INCIDENT_REPORT_TABLE = "CREATE TABLE IF NOT EXISTS " + INCIDENT_REPORT_TABLE_NAME +
            "( slno INTEGER PRIMARY KEY, " +
            ELEPHANT_CORRIDOR_DIVISION_ID + "TEXT, " +
            "Division_Name TEXT, " +
            ELEPHANT_CORRIDOR_RANGE_ID + "TEXT, " +
            "Range_Name TEXT, " +
            ELEPHANT_CORRIDOR_SECTION_ID + "TEXT, " +
            "Section_Name TEXT, " +
            ELEPHANT_CORRIDOR_BEAT_ID + "TEXT, " +
            "WlBeat_Name TEXT, " +
            "loc TEXT, " + "" +
            "date TEXT, " +
            "deg TEXT, " +
            "min TEXT, " +
            "sec TEXT, " +
            "deg1 TEXT, " +
            "min1 TEXT, " +
            "sec1 TEXT, " +
            "sync_status TEXT, " +
            "image_uri TEXT, " +
            "img_path TEXT, " +
            "incident_type TEXT, " +
            "incident TEXT, " +
            "incident_remark TEXT, " +
            "circleId TEXT, " +
            "altitude TEXT, " +
            "accuracy TEXT, " +
            "death_reason TEXT, " +
            "imei TEXT)";

    //create all lat lng
    private static final String CREATE_ALL_LAT_LNG = "CREATE TABLE IF NOT EXISTS " + ALL_LAT_LNG_TABLE_NAME +
            "( slno INTEGER PRIMARY KEY, " +
            "lat_lng BLOB, " +
            "date TEXT)";



    private final DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;


    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ELEPHANT_CORRIDOR_TABLE);
            db.execSQL(CREATE_INDIRECT_REPORT_TABLE);
            db.execSQL(CREATE_NIL_REPORT_TABLE);
            db.execSQL(CREATE_INCIDENT_REPORT_TABLE);
            db.execSQL(CREATE_ALL_LAT_LNG);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + ELEPHANT_CORRIDOR_TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + INDIRECT_REPORT_TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + NIL_REPORT_TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + INCIDENT_REPORT_TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + ALL_LAT_LNG_TABLE_NAME);
                onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 2);
    }

    public DBhelper(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public DBhelper open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    //Storing data in survey table data collection module


    public long insertElephantReport(ElephantCorridorData elephantCorridorData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ELEPHANT_CORRIDOR_DIVISION_NAME, elephantCorridorData.getTxtDivision());
        contentValues.put(ELEPHANT_CORRIDOR_RANGE_NAME, elephantCorridorData.getTxtRange());
        contentValues.put(ELEPHANT_CORRIDOR_SECTION_NAME, elephantCorridorData.getTxtSection());
        contentValues.put(ELEPHANT_CORRIDOR_BEAT_NAME, elephantCorridorData.getTxtBeat());
        contentValues.put(ELEPHANT_CORRIDOR_LOCATION_NAME, elephantCorridorData.getTxtLocation());
        contentValues.put(ELEPHANT_CORRIDOR_DATE, elephantCorridorData.getTxtDate());
        contentValues.put(ELEPHANT_CORRIDOR_NUMBER_ELEPHANT, elephantCorridorData.getTxtNumEleph());
        contentValues.put(ELEPHANT_CORRIDOR_DEG, elephantCorridorData.getTxtDeg());
        contentValues.put(ELEPHANT_CORRIDOR_MIN, elephantCorridorData.getTxtMin());
        contentValues.put(ELEPHANT_CORRIDOR_SEC, elephantCorridorData.getTxtSec());
        contentValues.put(ELEPHANT_CORRIDOR_DEG1, elephantCorridorData.getTxtDeg1());
        contentValues.put(ELEPHANT_CORRIDOR_MIN1, elephantCorridorData.getTxtMin1());
        contentValues.put(ELEPHANT_CORRIDOR_SEC1, elephantCorridorData.getTxtSec1());
        contentValues.put("herd", elephantCorridorData.getTxtHerd());
        contentValues.put("male", elephantCorridorData.getTxtMale());
        contentValues.put("female", elephantCorridorData.getTxtFemale());
        contentValues.put("calf", elephantCorridorData.getTxtCalf());
        contentValues.put("sync_status", "0");
        contentValues.put("image_uri", elephantCorridorData.getImageUri());
        contentValues.put("img_path", elephantCorridorData.getImagePath());
        contentValues.put("fromtime", elephantCorridorData.getFromtime());
        contentValues.put("totime", elephantCorridorData.getToTime());
        contentValues.put("tusker", elephantCorridorData.getTusker());
        contentValues.put("report_id", elephantCorridorData.getReport_id());
        contentValues.put("altitude", elephantCorridorData.getAltitude());
        contentValues.put("accuracy", elephantCorridorData.getAccuracy());
        contentValues.put("imei", elephantCorridorData.getImeiNo());
        contentValues.put("filename", elephantCorridorData.getFilename());
        long id = mDb.insert(ELEPHANT_CORRIDOR_TABLE_NAME, null, contentValues);
        return id;
    }

    public long insertIndirectReport(IndirectReportingData indirectReportingData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ELEPHANT_CORRIDOR_DIVISION_NAME, indirectReportingData.getTxtDivision());
        contentValues.put(ELEPHANT_CORRIDOR_RANGE_NAME, indirectReportingData.getTxtRange());
        contentValues.put(ELEPHANT_CORRIDOR_SECTION_NAME, indirectReportingData.getTxtSection());
        contentValues.put(ELEPHANT_CORRIDOR_BEAT_NAME, indirectReportingData.getTxtBeat());
        contentValues.put(ELEPHANT_CORRIDOR_LOCATION_NAME, indirectReportingData.getTxtLocation());
        contentValues.put(ELEPHANT_CORRIDOR_DATE, indirectReportingData.getTxtDate());
        contentValues.put(ELEPHANT_CORRIDOR_NUMBER_ELEPHANT, indirectReportingData.getTxtNumEleph());
        contentValues.put(ELEPHANT_CORRIDOR_DEG, indirectReportingData.getTxtDeg());
        contentValues.put(ELEPHANT_CORRIDOR_MIN, indirectReportingData.getTxtMin());
        contentValues.put(ELEPHANT_CORRIDOR_SEC, indirectReportingData.getTxtSec());
        contentValues.put(ELEPHANT_CORRIDOR_DEG1, indirectReportingData.getTxtDeg1());
        contentValues.put(ELEPHANT_CORRIDOR_MIN1, indirectReportingData.getTxtMin1());
        contentValues.put(ELEPHANT_CORRIDOR_SEC1, indirectReportingData.getTxtSec1());
        contentValues.put("herd", indirectReportingData.getTxtHerd());
        contentValues.put("male", indirectReportingData.getTxtMale());
        contentValues.put("female", indirectReportingData.getTxtFemale());
        contentValues.put("calf", indirectReportingData.getTxtCalf());
        contentValues.put("sync_status", "0");
        contentValues.put("image_uri", indirectReportingData.getImageUri());
        contentValues.put("img_path", indirectReportingData.getImagePath());
        contentValues.put("fromtime", indirectReportingData.getFromtime());
        contentValues.put("totime", indirectReportingData.getToTime());
        contentValues.put("tusker", indirectReportingData.getTusker());
        contentValues.put("remark", indirectReportingData.getRemarks());
        contentValues.put("reportType", indirectReportingData.getReportType());
        contentValues.put("imei", indirectReportingData.getImei());
        contentValues.put("altitude", indirectReportingData.getAltitude());
        contentValues.put("accuracy", indirectReportingData.getAccuracy());
        contentValues.put("filename", indirectReportingData.getFilename());
        long id = mDb.insert(INDIRECT_REPORT_TABLE_NAME, null, contentValues);
        return id;
    }
    public long insertNilReport(NilReportingData nilReportingData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ELEPHANT_CORRIDOR_DATE, nilReportingData.getTxtDate());
        contentValues.put(ELEPHANT_CORRIDOR_LOCATION_NAME, nilReportingData.getTxtLocation());
        contentValues.put("fromtime", nilReportingData.getFromtime());
        contentValues.put("totime", nilReportingData.getToTime());
        contentValues.put(ELEPHANT_CORRIDOR_DIVISION_NAME, nilReportingData.getTxtDivision());
        contentValues.put(ELEPHANT_CORRIDOR_RANGE_NAME, nilReportingData.getTxtRange());
        contentValues.put(ELEPHANT_CORRIDOR_SECTION_NAME, nilReportingData.getTxtSection());
        contentValues.put(ELEPHANT_CORRIDOR_BEAT_NAME, nilReportingData.getTxtBeat());
        contentValues.put("remark", nilReportingData.getRemarks());
        contentValues.put("imei", nilReportingData.getImei());
        contentValues.put(ELEPHANT_CORRIDOR_DEG, nilReportingData.getTxtDeg());//latitude
        contentValues.put(ELEPHANT_CORRIDOR_MIN, nilReportingData.getTxtMin());
        contentValues.put(ELEPHANT_CORRIDOR_SEC, nilReportingData.getTxtSec());
        contentValues.put(ELEPHANT_CORRIDOR_DEG1, nilReportingData.getTxtDeg1());//longitude
        contentValues.put(ELEPHANT_CORRIDOR_MIN1, nilReportingData.getTxtMin1());
        contentValues.put(ELEPHANT_CORRIDOR_SEC1, nilReportingData.getTxtSec1());
        contentValues.put("altitude", nilReportingData.getAltitude());
        contentValues.put("accuracy", nilReportingData.getAccuracy());
        contentValues.put("sync_status", "0");
        long id = mDb.insert(NIL_REPORT_TABLE_NAME, null, contentValues);
        return id;
    }

    public long insertIncidentReport(IncidentReportingData incidentData) {
        long id=0;
        try {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ELEPHANT_CORRIDOR_DIVISION_NAME, incidentData.getTxtDivision());
        contentValues.put(ELEPHANT_CORRIDOR_RANGE_NAME, incidentData.getTxtRange());
        contentValues.put(ELEPHANT_CORRIDOR_SECTION_NAME, incidentData.getTxtSection());
        contentValues.put(ELEPHANT_CORRIDOR_BEAT_NAME, incidentData.getTxtBeat());
        contentValues.put(ELEPHANT_CORRIDOR_LOCATION_NAME, incidentData.getTxtLocation());
        contentValues.put(ELEPHANT_CORRIDOR_DATE, incidentData.getTxtDate());
        contentValues.put(ELEPHANT_CORRIDOR_DEG, incidentData.getTxtDeg());
        contentValues.put(ELEPHANT_CORRIDOR_MIN, incidentData.getTxtMin());
        contentValues.put(ELEPHANT_CORRIDOR_SEC, incidentData.getTxtSec());
        contentValues.put(ELEPHANT_CORRIDOR_DEG1, incidentData.getTxtDeg1());
        contentValues.put(ELEPHANT_CORRIDOR_MIN1, incidentData.getTxtMin1());
        contentValues.put(ELEPHANT_CORRIDOR_SEC1, incidentData.getTxtSec1());
        contentValues.put("sync_status", "0");
        contentValues.put("image_uri", incidentData.getImageUri());
        contentValues.put("img_path", incidentData.getImagePath());
        contentValues.put("incident_type", incidentData.getIncidentType());
        contentValues.put("incident", incidentData.getIncidentjson());
        contentValues.put("incident_remark", incidentData.getIncidentRemark());
        contentValues.put("circleId", incidentData.getCircleId());
        contentValues.put("imei", incidentData.getImei());
        contentValues.put("altitude", incidentData.getAltitude());
        contentValues.put("accuracy", incidentData.getAccuracy());
        contentValues.put("death_reason", incidentData.getDeathReason());
         id = mDb.insert(INCIDENT_REPORT_TABLE_NAME, null, contentValues);
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public long insertLatlng(ArrayList<AllLatLngData> latLngData,String current_date) {
        long id=0;
        try {
//            ArrayList<AllLatLngData> all_latlng  = new ArrayList<>();
            Gson gson = new Gson();
            ContentValues contentValues = new ContentValues();
    //      contentValues.put("lat_lng", latLngData.toString());
            contentValues.put("lat_lng", gson.toJson(latLngData).getBytes());
            contentValues.put("date", current_date);
            id = mDb.insert(ALL_LAT_LNG_TABLE_NAME,null, contentValues);
    }catch (Exception e){
        e.printStackTrace();
    }
        return id;
    }


}

