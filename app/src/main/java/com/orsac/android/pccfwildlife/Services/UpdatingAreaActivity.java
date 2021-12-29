package com.orsac.android.pccfwildlife.Services;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SQLiteDB.DBhelper;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllNewBeatModels;

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
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class UpdatingAreaActivity {

    SQLiteDatabase mDb;
    String value="";
    Activity activity;

    public UpdatingAreaActivity(Activity activity) {
        this.activity = activity;
        new CallAllUpdatingArea().execute();//comment for now 1st June 2021
//        callUpdateAllArea();//directly retrofit call
    }


    public class CallAllUpdatingArea extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {

//                new CircleDataSyncAsync().execute();
//                new DivisionSyncAsync().execute();
//                new RangeDataSyncAsync().execute();
//                new SectionDataSyncAsync().execute();

                new BeatDataSyncAsync().execute();//comment for now 1st June 2021

//                callUpdateAllArea();

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class CircleDataSyncAsync extends AsyncTask<String, String, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(UpdatingAreaActivity.this, "Updating...", Toast.LENGTH_SHORT).show();
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
                mDb=activity.openOrCreateDatabase(DBhelper.DATABASE_NAME,MODE_PRIVATE,null);

                String circle_id="",circle_name="",div_id="",division_name="",isActive="",isActive_division="", range_id="",range_name="",secId="", secName="", beatId="", beatName="";

                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject division_obj = jsonArray.getJSONObject(i);
                    circle_id = division_obj.optString("circleId");
                    circle_name = division_obj.optString("circleName");

                    mDb.execSQL("INSERT INTO  Circle_Other (Circle_Id,Circle_Name)VALUES('" + circle_id + "','" + circle_name + "');");
                    Log.d("Circle:", "Circle data inserted");
                }


                try {
//                    Toast.makeText(UpdatingAreaActivity.this, "Updating completed !", Toast.LENGTH_SHORT).show();

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

    public class DivisionSyncAsync extends AsyncTask<String, String, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(activity, "Updating...", Toast.LENGTH_SHORT).show();
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
                mDb=activity.openOrCreateDatabase(DBhelper.DATABASE_NAME,MODE_PRIVATE,null);

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
                    Toast.makeText(activity, "Updating completed !", Toast.LENGTH_SHORT).show();

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
//            Toast.makeText(activity, "Updating...", Toast.LENGTH_SHORT).show();
            Log.d("Updating", "updating data...");


        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            HttpResponse response;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(RetrofitClient.BASE_URL1 + "getAllRange");
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
                mDb=activity.openOrCreateDatabase(DBhelper.DATABASE_NAME,MODE_PRIVATE,null);

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
//                    Toast.makeText(activity, "Updating completed !", Toast.LENGTH_SHORT).show();

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
//            Toast.makeText(activity, "Updating...", Toast.LENGTH_SHORT).show();
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
                mDb=activity.openOrCreateDatabase(DBhelper.DATABASE_NAME,MODE_PRIVATE,null);

                String div_id="",division_name="",isActive="", range_id="",range_name="",secId="", secName="", beatId="", beatName="",isActive_division="",isActive_range="";

                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject division_obj = jsonArray.getJSONObject(i);
                    secId = division_obj.optString("sectionId");
                    secName = division_obj.optString("sectionName");

//                    JSONObject range_json=division_obj.getJSONObject("range");
                    range_id = division_obj.optString("rangeId");
                    range_name = division_obj.optString("rangeName");

                    div_id = division_obj.optString("divisionId");
                    division_name = division_obj.optString("divisionName");



                    mDb.execSQL("INSERT INTO  WlSection (Section_ID,Section_name,Range_ID,Range_Name)VALUES('" + secId + "','" + secName + "','" + range_id + "','" + range_name + "');");
                    Log.d("Section:", "Section data inserted");

                }


                try {
//                    Toast.makeText(activity, "Updating completed !", Toast.LENGTH_SHORT).show();

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
//            Toast.makeText(activity, "Updating...", Toast.LENGTH_SHORT).show();
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
                mDb=activity.openOrCreateDatabase(DBhelper.DATABASE_NAME,MODE_PRIVATE,null);

                String div_id="",division_name="",isActive="", range_id="",range_name="",secId="", secName="", beatId="", beatName="",
                        isActive_division="",isActive_range="",isActive_section="",circle_id="",circle_name="";

                try {

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

                    circle_id = beat_obj.optString("circleId");
                    circle_name = beat_obj.optString("circleName");

                    //for circle
                    mDb.execSQL("INSERT INTO  Circle_Other (Circle_Id,Circle_Name)VALUES('" + circle_id + "','" + circle_name + "');");
                    //for division
                    mDb.execSQL("INSERT INTO  Division_Other (Division_Id,Division_Name)VALUES('" + div_id + "','" + division_name + "');");
                    //for range
                    mDb.execSQL("INSERT INTO  WlRange_Other (Range_ID,Range_Name,Division_ID,Division_Name)VALUES('" + range_id + "','" + range_name + "','" + div_id + "','" + division_name + "');");
                    //for section
                    mDb.execSQL("INSERT INTO  WlSection (Section_ID,Section_name,Range_ID,Range_Name)VALUES('" + secId + "','" + secName + "','" + range_id + "','" + range_name + "');");
                    //for beat
                    mDb.execSQL("INSERT INTO  WlBeat (WlBeat_ID,WlBeat_Name,Section_ID,Section_Name)VALUES('" + beatId + "','" + beatName + "','" + secId + "','" + secName + "');");
                    Log.d("Beat:", "Beat data inserted");

                }

                }catch (Exception e){
                    e.printStackTrace();
                }


                try {
//                    Toast.makeText(activity, "Updating completed !", Toast.LENGTH_SHORT).show();

                    Log.d("Work:","Completed");
                    mDb.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("Work:",e.getMessage());
                }

            }catch (Exception e){
                Log.d("allDivision_exception",e.getMessage());
            }

        }
    }


    public void callUpdateAllArea(){
        try {
            //Call retrofit Area
            RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);

            retrofitInterface.getAllBeats().enqueue(new Callback<ArrayList<AllNewBeatModels>>() {
                @Override
                public void onResponse(Call<ArrayList<AllNewBeatModels>> call, Response<ArrayList<AllNewBeatModels>> response) {
                    if (response.isSuccessful()){


                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {

                                String circle_id="",circle_name="",div_id="",division_name="",
                                        range_id="",range_name="",secId="",secName="",beatId="",beatName="";

                                for (int i=0;i<response.body().size();i++){

                                    circle_id=response.body().get(i).getCircleId();
                                    circle_name=response.body().get(i).getCircleName();

                                    div_id=response.body().get(i).getDivisionId();
                                    division_name=response.body().get(i).getDivisionName();

                                    range_id=response.body().get(i).getRangeId();
                                    range_name=response.body().get(i).getRangeName();

                                    secId=response.body().get(i).getSectionId();
                                    secName=response.body().get(i).getSectionName();

                                    beatId=response.body().get(i).getBeatId();
                                    beatName=response.body().get(i).getBeatName();

                                    mDb.execSQL("INSERT INTO  Circle_Other (Circle_Id,Circle_Name)VALUES('" + circle_id + "','" + circle_name + "');");
                                    //for division
                                    mDb.execSQL("INSERT INTO  Division_Other (Division_Id,Division_Name)VALUES('" + div_id + "','" + division_name + "');");
                                    //for range
                                    mDb.execSQL("INSERT INTO  WlRange_Other (Range_ID,Range_Name,Division_ID,Division_Name)VALUES('" + range_id + "','" + range_name + "','" + div_id + "','" + division_name + "');");
                                    //for section
                                    mDb.execSQL("INSERT INTO  WlSection (Section_ID,Section_name,Range_ID,Range_Name)VALUES('" + secId + "','" + secName + "','" + range_id + "','" + range_name + "');");
                                    //for beat
                                    mDb.execSQL("INSERT INTO  WlBeat (WlBeat_ID,WlBeat_Name,Section_ID,Section_Name)VALUES('" + beatId + "','" + beatName + "','" + secId + "','" + secName + "');");

                                }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });

                        Toast.makeText(activity, "Updating Completed !", Toast.LENGTH_SHORT).show();


                    }else {
                        Toast.makeText(activity, "Please try again !!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<AllNewBeatModels>> call, Throwable t) {
                    Toast.makeText(activity, "Failed please try again !!", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
