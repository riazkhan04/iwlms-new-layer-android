package com.orsac.android.pccfwildlife.Services;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class MyService extends IntentService{
    Runnable mRunnable;
    SQLiteDatabase mDb;
//    public static final String DATABASE_NAME = "pccfwl_v1.db";
//    public static final String DATABASE_NAME = "pccfwl_new.db";
//    public static final String DATABASE_NAME = "pccfwl.db";
    String value="";

    public static final String PERMISSION_BIND = "android.permission.BIND_JOB_SERVICE";

    public MyService() {
        super("Data Sync");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        final Handler mHandler = new Handler();
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//
//                Toast.makeText(MyService.this, "Started Updating...", Toast.LENGTH_SHORT).show();
//                new DataSyncAsync().execute();
//
//                mHandler.postDelayed(mRunnable, 10 * 1000);//for run in every 10 sec
//            }
//        };
//        mHandler.postDelayed(mRunnable, 1 * 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    public class CircleDataSyncAsync extends AsyncTask<String, String, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(MyService.this, "Updating...", Toast.LENGTH_SHORT).show();
            Log.d("Updating", "updating data...");


        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            HttpResponse response;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(RetrofitClient.BASE_URL1 + "getAllCircle");
//                 httpGet.setHeader("Content-Type", "application/json");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = httpClient.execute(httpGet);
                String sresponse = response.getEntity().toString();
                value = EntityUtils.toString(response.getEntity());
//                 value = httpClient.execute(httpGet,responseHandler);



            }catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());

            }
            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONArray jsonArray=new JSONArray(s);
                mDb=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);

                String circle_id="",circle_name="",div_id="",division_name="",isActive="",isActive_division="", range_id="",range_name="",secId="", secName="", beatId="", beatName="";

                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject division_obj = jsonArray.getJSONObject(i);
                    circle_id = division_obj.optString("circleId");
                    circle_name = division_obj.optString("circleName");

//                    JSONObject division_json=division_obj.getJSONObject("division");
//                    div_id = division_json.optString("divisionId");
//                    division_name = division_json.optString("divisionName");
//                    isActive_division = division_json.optString("isActive");

                    mDb.execSQL("INSERT INTO  Circle_Other (Circle_Id,Circle_Name)VALUES('" + circle_id + "','" + circle_name + "');");
                    Log.d("Circle:", "Circle data inserted");
                }


                try {
//                    Toast.makeText(MyService.this, "Updating completed !", Toast.LENGTH_SHORT).show();

                    Log.d("Work:","Completed");
                    mDb.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("Work:",e.getMessage());
                }


//                mDb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            }catch (Exception e){
                Log.d("allDivision_exception",e.getMessage());
            }

        }
    }

    public class DataSyncAsync extends AsyncTask<String, String, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(MyService.this, "Updating...", Toast.LENGTH_SHORT).show();
            Log.d("Updating", "updating data...");


        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            HttpResponse response;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(RetrofitClient.BASE_URL1 + "getAllDivision");
//                 httpGet.setHeader("Content-Type", "application/json");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = httpClient.execute(httpGet);
                String sresponse = response.getEntity().toString();
                value = EntityUtils.toString(response.getEntity());
//                 value = httpClient.execute(httpGet,responseHandler);



            }catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());

            }
            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONArray jsonArray=new JSONArray(s);
                mDb=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);

                String div_id="",division_name="",isActive="", range_id="",range_name="",secId="", secName="", beatId="", beatName="";

                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject division_obj = jsonArray.getJSONObject(i);
                    div_id = division_obj.optString("divisionId");
                    division_name = division_obj.optString("divisionName");

                    mDb.execSQL("INSERT INTO  Division_Other (Division_Id,Division_Name)VALUES('" + div_id + "','" + division_name + "');");
//                    mDb.execSQL("INSERT INTO  Division_Other (Division_Id,Division_Name,Elephant_isActive)VALUES('" + div_id + "','" + division_name + "','" + isActive + "');");
                    Log.d("Division:", "Division data inserted");
                }

                try {
                    Toast.makeText(MyService.this, "Updating completed !", Toast.LENGTH_SHORT).show();

                    Log.d("Work:","Completed");
                    mDb.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("Work:",e.getMessage());
                }


//                mDb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            }catch (Exception e){
                Log.d("allDivision_exception",e.getMessage());
            }

        }
    }

    //For getAllRange
    public class RangeDataSyncAsync extends AsyncTask<String, String, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(MyService.this, "Updating...", Toast.LENGTH_SHORT).show();
            Log.d("Updating", "updating data...");


        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            HttpResponse response;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(RetrofitClient.BASE_URL1 + "getAllRange");
//                 httpGet.setHeader("Content-Type", "application/json");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = httpClient.execute(httpGet);
                String sresponse = response.getEntity().toString();
                value = EntityUtils.toString(response.getEntity());
//                 value = httpClient.execute(httpGet,responseHandler);



            }catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());

            }
            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONArray jsonArray=new JSONArray(s);
                mDb=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);

                String div_id="",division_name="",isActive="", range_id="",range_name="",secId="", secName="", beatId="", beatName="",isActive_division="";

                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject division_obj = jsonArray.getJSONObject(i);
                    range_id = division_obj.optString("rangeId");
                    range_name = division_obj.optString("rangeName");

//                    JSONObject division_json=division_obj.getJSONObject("division");
                    div_id = division_obj.optString("divisionId");
                    division_name = division_obj.optString("divisionName");


                    mDb.execSQL("INSERT INTO  WlRange_Other (Range_ID,Range_Name,Division_ID,Division_Name)VALUES('" + range_id + "','" + range_name + "','" + div_id + "','" + division_name + "');");
//                    mDb.execSQL("INSERT INTO  WlRange_Other (Range_ID,Range_Name,Division_ID,Division_Name)VALUES('" + range_id + "','" + range_name + "','" + div_id + "','" + division_name + "');");
                        Log.d("Range:", "Range data inserted");
                }


                try {
//                    Toast.makeText(MyService.this, "Updating completed !", Toast.LENGTH_SHORT).show();

                    Log.d("Work:","Completed");
                    mDb.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("Work:",e.getMessage());
                }


//                mDb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            }catch (Exception e){
                Log.d("allDivision_exception",e.getMessage());
            }

        }
    }

    //For getAllSection
    public class SectionDataSyncAsync extends AsyncTask<String, String, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(MyService.this, "Updating...", Toast.LENGTH_SHORT).show();
            Log.d("Updating", "updating data...");


        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            HttpResponse response;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(RetrofitClient.BASE_URL1 + "getAllSection");
//                 httpGet.setHeader("Content-Type", "application/json");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = httpClient.execute(httpGet);
                String sresponse = response.getEntity().toString();
                value = EntityUtils.toString(response.getEntity());
//                 value = httpClient.execute(httpGet,responseHandler);



            }catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());

            }
            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONArray jsonArray=new JSONArray(s);
                mDb=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);

                String div_id="",division_name="",isActive="", range_id="",range_name="",secId="", secName="", beatId="", beatName="",isActive_division="",isActive_range="";

                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject division_obj = jsonArray.getJSONObject(i);
                    secId = division_obj.optString("sectionId");
                    secName = division_obj.optString("sectionName");

//                    JSONObject range_json=division_obj.getJSONObject("range");
                    range_id = division_obj.optString("rangeId");
                    range_name = division_obj.optString("rangeName");


//                    JSONObject division_json=range_json.getJSONObject("division");
                    div_id = division_obj.optString("divisionId");
                    division_name = division_obj.optString("divisionName");



                    mDb.execSQL("INSERT INTO  WlSection (Section_ID,Section_name,Range_ID,Range_Name)VALUES('" + secId + "','" + secName + "','" + range_id + "','" + range_name + "');");
                    Log.d("Section:", "Section data inserted");

                }


                try {
//                    Toast.makeText(MyService.this, "Updating completed !", Toast.LENGTH_SHORT).show();

                    Log.d("Work:","Completed");
                    mDb.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("Work:",e.getMessage());
                }


//                mDb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            }catch (Exception e){
                Log.d("allDivision_exception",e.getMessage());
            }

        }
    }

    //For getAllBeat
    public class BeatDataSyncAsync extends AsyncTask<String, String, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(MyService.this, "Updating...", Toast.LENGTH_SHORT).show();
            Log.d("Updating", "updating data...");


        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            HttpResponse response;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(RetrofitClient.BASE_URL1 + "getAllBeat");
//                 httpGet.setHeader("Content-Type", "application/json");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = httpClient.execute(httpGet);
                String sresponse = response.getEntity().toString();
                value = EntityUtils.toString(response.getEntity());
//                 value = httpClient.execute(httpGet,responseHandler);



            }catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());

            }
            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONArray jsonArray=new JSONArray(s);
                mDb=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);

                String div_id="",division_name="",isActive="", range_id="",range_name="",secId="", secName="", beatId="", beatName="",
                        isActive_division="",isActive_range="",isActive_section="";

                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject beat_obj = jsonArray.getJSONObject(i);
                    beatId = beat_obj.optString("beatId");
                    beatName = beat_obj.optString("beatName");


//                    JSONObject section_json=beat_obj.getJSONObject("section");
                    secId = beat_obj.optString("sectionId");
                    secName = beat_obj.optString("sectionName");


//                    JSONObject range_json=section_json.getJSONObject("range");
                    range_id = beat_obj.optString("rangeId");
                    range_name = beat_obj.optString("rangeName");

//                    JSONObject division_json=range_json.getJSONObject("division");
                    div_id = beat_obj.optString("divisionId");
                    division_name = beat_obj.optString("divisionName");

                    mDb.execSQL("INSERT INTO  WlBeat (WlBeat_ID,WlBeat_Name,Section_ID,Section_Name)VALUES('" + beatId + "','" + beatName + "','" + secId + "','" + secName + "');");
                    Log.d("Beat:", "Beat data inserted");

                }


                try {
//                    Toast.makeText(MyService.this, "Updating completed !", Toast.LENGTH_SHORT).show();

                    Log.d("Work:","Completed");
                    mDb.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("Work:",e.getMessage());
                }


//                mDb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
            }catch (Exception e){
                Log.d("allDivision_exception",e.getMessage());
            }

        }
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            new CircleDataSyncAsync().execute();
            new DataSyncAsync().execute();
            new RangeDataSyncAsync().execute();
            new SectionDataSyncAsync().execute();
            new BeatDataSyncAsync().execute();

        }catch (Exception e){
            e.printStackTrace();
        }
//        Toast.makeText(MyService.this, "Started Updating...", Toast.LENGTH_SHORT).show();
    }

//    private <T> Iterable<T> iterate(final Iterator<T> i){
//        return new Iterable<T>() {
//            @Override
//            public Iterator<T> iterator() {
//                return i;
//            }
//        };
//    }

    public void getAllDivisionData(String s){

        try {

            JSONObject jsonObject = new JSONObject(s);
            JSONArray division_arr=jsonObject.getJSONArray("division");
            JSONArray ranger_arr=jsonObject.getJSONArray("range");
            JSONArray section_arr=jsonObject.getJSONArray("section");
            JSONArray beat_arr=jsonObject.getJSONArray("beat");
            mDb=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);

//                 //For Division-----
            for(int i=0;i<division_arr.length();i++){
                JSONObject division_obj=division_arr.getJSONObject(i);
                int div_id=division_obj.getInt("div_id");
                String division_name=division_obj.getString("division_name");

//                     Toast.makeText(DashboardActivity.this, ""+division_name, Toast.LENGTH_SHORT).show();
//                     insertDivisionFromApi(div_id,division_name,mDb);

                //------------------
                mDb.execSQL("INSERT INTO  Division_Other (Division_Id,Division_Name)VALUES('"+div_id+"','"+division_name+"');");
                Log.d("Division:","Division data inserted");

            }
//                Toast.makeText(MyService.this, "Division data inserted", Toast.LENGTH_SHORT).show();
            //For Ranger-----
            for(int i=0;i<ranger_arr.length();i++){
                JSONObject ranger_obj=ranger_arr.getJSONObject(i);
                int ranger_id=ranger_obj.getInt("range_id");
                String ranger_name=ranger_obj.getString("range_name");
                String fk_div=ranger_obj.getString("fk_div");
                JSONObject division_master=ranger_obj.getJSONObject("division_master");
                int ranger_division_id=division_master.getInt("div_id");
                String ranger_division_name=division_master.getString("division_name");

//                     insertRangeFromApi(ranger_id,ranger_name,ranger_division_id,ranger_division_name,mDb);

                mDb.execSQL("INSERT INTO  WlRange_Other (Range_ID,Range_Name,Division_ID,Division_Name)VALUES('"+ranger_id+"','"+ranger_name+"','"+ranger_division_id+"','"+ranger_division_name+"');");
                Log.d("Range:","Range data inserted");


//                     Toast.makeText(DashboardActivity.this, ""+ranger_division_id, Toast.LENGTH_SHORT).show();
            }
//                Toast.makeText(MyService.this, "Range data inserted", Toast.LENGTH_SHORT).show();


            //For Section-----
            for(int i=0;i<section_arr.length();i++){
                JSONObject section_obj=section_arr.getJSONObject(i);
                int section_id=section_obj.getInt("section_id");
                String section_name=section_obj.getString("section_name");
                String fk_range=section_obj.getString("fk_range");

                JSONObject range_master_obj=section_obj.getJSONObject("range_master");
                int section_ranger_id=range_master_obj.getInt("range_id");
                String section_ranger_name=range_master_obj.getString("range_name");
                String section_fk_div=range_master_obj.getString("fk_div");

                JSONObject division_master_obj=range_master_obj.getJSONObject("division_master");
                int ranger_division_id=division_master_obj.getInt("div_id");
                String ranger_division_name=division_master_obj.getString("division_name");

//                     insertSectionFromApi(section_id,section_name,section_ranger_id,section_ranger_name,mDb);

                //-------------------------------------
                mDb.execSQL("INSERT INTO  WlSection (Section_ID,Section_name,Range_ID,Range_Name)VALUES('"+section_id+"','"+section_name+"','"+section_ranger_id+"','"+section_ranger_name+"');");
                Log.d("Section:","Section data inserted");


//                     Toast.makeText(DashboardActivity.this, ""+ranger_division_name, Toast.LENGTH_SHORT).show();
            }
//                Toast.makeText(MyService.this, "Section data inserted", Toast.LENGTH_SHORT).show();


            //For Beat-----
            for(int i=0;i<beat_arr.length();i++){
                JSONObject beat_obj=beat_arr.getJSONObject(i);
                int beat_id=beat_obj.getInt("beat_id");
                String beat_name=beat_obj.getString("beat_name");
                String fk_section=beat_obj.getString("fk_section");

                JSONObject section_master_obj=beat_obj.getJSONObject("section_master");
                int beat_section_id=section_master_obj.getInt("section_id");
                String beat_section_name=section_master_obj.getString("section_name");
                String fk_range=section_master_obj.getString("fk_range");


                JSONObject range_master_obj=section_master_obj.getJSONObject("range_master");
                int section_ranger_id=range_master_obj.getInt("range_id");
                String section_ranger_name=range_master_obj.getString("range_name");
                String section_ranger_fk_div=range_master_obj.getString("fk_div");

                JSONObject division_master_obj=range_master_obj.getJSONObject("division_master");
                int ranger_division_id=division_master_obj.getInt("div_id");
                String ranger_division_name=division_master_obj.getString("division_name");

//                     Toast.makeText(DashboardActivity.this, ""+beat_name, Toast.LENGTH_SHORT).show();
//                     Toast.makeText(DashboardActivity.this, ""+ranger_division_name, Toast.LENGTH_SHORT).show();

//                     insertBeatFromApi(beat_id,beat_name,beat_section_id,beat_section_id,mDb);

                //----------------------
                mDb.execSQL("INSERT INTO  WlBeat (WlBeat_ID,WlBeat_Name,Section_ID,Section_Name)VALUES('"+beat_id+"','"+beat_name+"','"+beat_section_id+"','"+beat_section_id+"');");
                Log.d("Beat:","Beat data inserted");

            }
//                Toast.makeText(MyService.this, "Beat data inserted", Toast.LENGTH_SHORT).show();


            try {
                Toast.makeText(MyService.this, "Updating completed !", Toast.LENGTH_SHORT).show();

                Log.d("Work:","Completed");
                mDb.close();
            }catch (Exception e){
                e.printStackTrace();
                Log.d("Work:",e.getMessage());
            }

//                 Toast.makeText(DashboardActivity.this, "Updating completed !", Toast.LENGTH_SHORT).show();



//                if (jsonObject.has("division")){
////
////                     Toast.makeText(DashboardActivity.this, "Working...", Toast.LENGTH_SHORT).show();
//                }
//                else {
////                     Toast.makeText(DashboardActivity.this, "Data already updated !", Toast.LENGTH_SHORT).show();
//
//                }





        }catch (Exception e){
//                Log.d("Workk:",e.getMessage());
            e.printStackTrace();
        }
    }
}
