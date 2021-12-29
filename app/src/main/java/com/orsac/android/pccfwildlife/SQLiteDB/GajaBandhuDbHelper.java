package com.orsac.android.pccfwildlife.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuReportingObj;

public class GajaBandhuDbHelper {

    public static final String GAJABANDHU_DATABASE_NAME = "GajaBandhu.db";
    private static final int GAJABANDHU_DATABASE_VERSION = 4;

    //Gaja Bandhu report creation
    public static String GAJA_BANDHU_TABLE_NAME = "gajabandhuReport";
    public static String ReportingDate = "date";
    public static String UserID = "userID";
    public static String LOC_LAT = "latitude";
    public static String LOC_LONGI = "longitude";
    public static String TEXT_MSG = "textMsg";
    public static String AUDIO_VIDEO_IMAGE_PATH = "audio_video_img_path";
    public static String REPORT_TYPE = "reportType";
    public static String SYNC_STATUS = "sync_status";
    public static String FOLDER_TYPE = "folder_type";

    public static final String CREATE_GAJABANDHU_TABLE = "CREATE TABLE IF NOT EXISTS " + GAJA_BANDHU_TABLE_NAME +
            "( slno INTEGER PRIMARY KEY, " +
            ReportingDate + " TEXT, " +
            UserID + " TEXT, " +
            LOC_LAT + " TEXT, " +
            LOC_LONGI + " TEXT, " +
            TEXT_MSG + " TEXT, " +
            SYNC_STATUS + " TEXT, " +
            AUDIO_VIDEO_IMAGE_PATH + " TEXT, " +
            REPORT_TYPE + " TEXT, " +
            FOLDER_TYPE + " TEXT)";

    private final DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;


    public static class DatabaseHelper extends SQLiteOpenHelper {

       public DatabaseHelper(Context context) {
            super(context, GAJABANDHU_DATABASE_NAME, null, GAJABANDHU_DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_GAJABANDHU_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + GAJA_BANDHU_TABLE_NAME);
            onCreate(db);
        }
    }
        public void Reset() {
            mDbHelper.onUpgrade(this.mDb, 1, 2);
        }

        public GajaBandhuDbHelper(Context ctx) {
            mCtx = ctx;
            mDbHelper = new DatabaseHelper(mCtx);
        }

        public GajaBandhuDbHelper open() throws SQLException {
            mDb = mDbHelper.getWritableDatabase();
            return this;
        }

        public void close() {
            mDbHelper.close();
        }


    public long insertGajaBandhuReport(GajaBandhuReportingObj gajaBandhuReportingObj) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AUDIO_VIDEO_IMAGE_PATH, gajaBandhuReportingObj.getFileName());
        contentValues.put(REPORT_TYPE, gajaBandhuReportingObj.getReportType());
        contentValues.put(LOC_LAT, gajaBandhuReportingObj.getLatitude());
        contentValues.put(TEXT_MSG, gajaBandhuReportingObj.getMessage());
        contentValues.put(SYNC_STATUS, "0");
        contentValues.put(UserID, gajaBandhuReportingObj.getUserId());
        contentValues.put(ReportingDate, gajaBandhuReportingObj.getDate());
        contentValues.put(LOC_LONGI, gajaBandhuReportingObj.getLongitude());
        contentValues.put(FOLDER_TYPE, gajaBandhuReportingObj.getFolderType());

        long id = mDb.insert(GAJA_BANDHU_TABLE_NAME, null, contentValues);
        return id;
    }

}
