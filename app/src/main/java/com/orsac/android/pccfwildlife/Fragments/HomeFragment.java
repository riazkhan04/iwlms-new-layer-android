package com.orsac.android.pccfwildlife.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.orsac.android.pccfwildlife.Activities.ElephantReport;
import com.orsac.android.pccfwildlife.Activities.IndirectReporting;
import com.orsac.android.pccfwildlife.Activities.NilReportActivity;
import com.orsac.android.pccfwildlife.Model.DashboardItemModel;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Adapters.DashboardItemsRecycleAdapter;
import com.orsac.android.pccfwildlife.Model.EditReportResponseData;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class HomeFragment extends Fragment implements DashboardItemsRecycleAdapter.DashItemClickListener{

    CardView elephant_report,nil_report,indirect_report,sync;
    RecyclerView dashboard_recyclerV;
    public RecyclerView.LayoutManager layoutManager;
    private ArrayList<DashboardItemModel> dashboardItemModels_arr;
    DashboardItemsRecycleAdapter dashboard_adapter;
    public String elephantReport="Elephant Report",nilReport="Nil Report",
    indirectReport="Indirect Reporting",sync_str="Sync",incident_report="Incident Report";
    Context context;
    SQLiteDatabase db;
    String check_internet="";
    ScrollView main_ll;
    public String token="",checkInternet_status="",divisionNmFrom_pref="",userId="";
    SessionManager session;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);

        elephant_report = view.findViewById(R.id.elephreport);
        nil_report = view.findViewById(R.id.nilreport);
        indirect_report = view.findViewById(R.id.indirectReport);
        sync = view.findViewById(R.id.sync);
        main_ll = view.findViewById(R.id.main_ll);
        dashboardItemModels_arr=new ArrayList<>();
        dashboard_recyclerV = view.findViewById(R.id.dashboard_recyclerV);
        session=new SessionManager(context);

        token=session.getToken();
        divisionNmFrom_pref=session.getDivision();
        userId=session.getUserID();

        callDashboard_Adapter("",4);

        return view;
    }

    @Override
    public void item_clickListener(CardView cardView, int pos, ImageView item_img, ImageView elephant_img, TextView sync_value,
                                   CardView directCV, LinearLayout reporting_ll,CardView indirectCV,CardView nilCV,
                                   CardView elephant_death_cv, CardView other_incident) {

        if (pos==0){
            Intent i = new Intent(getActivity(), ElephantReport.class);
            startActivity(i);
        }
        else if (pos==1){
            Intent i = new Intent(getActivity(), NilReportActivity.class);
            startActivity(i);
        }
        else if (pos==2){

            Intent i = new Intent(getActivity(), IndirectReporting.class);
            startActivity(i);
        }
        else if (pos==3){
//            Toast.makeText(getContext(), "Syncing...", Toast.LENGTH_SHORT).show();
//            new ReportDataSync().execute();
        }
        else if (pos==4){
            Toast.makeText(getContext(), "Syncing...", Toast.LENGTH_SHORT).show();
            new ReportDataSync().execute();
        }

    }

    public void callDashboard_Adapter(String division_name,int dashboard_item_arr_size){

        dashboard_recyclerV.setHasFixedSize(true);
        dashboard_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview

//        layoutManager = new GridLayoutManager(getActivity(),2);
        layoutManager = new LinearLayoutManager(getActivity());
        dashboard_recyclerV.setLayoutManager(layoutManager);

        dashboardItemModels_arr.add(new DashboardItemModel(elephantReport,"0",R.drawable.reporting_direct_img,R.drawable.ic_elephant));
        dashboardItemModels_arr.add(new DashboardItemModel(nilReport,"1",R.drawable.reporting_nil,R.drawable.ic_elephant));
        dashboardItemModels_arr.add(new DashboardItemModel(indirectReport,"2",R.drawable.reporting_indirect,R.drawable.ic_elephant));
        dashboardItemModels_arr.add(new DashboardItemModel(incident_report,"3",R.drawable.reporting_incident,R.drawable.ic_elephant));
        dashboardItemModels_arr.add(new DashboardItemModel(sync_str,"4",R.drawable.reporting_sync,R.drawable.ic_elephant));


        dashboard_adapter=new DashboardItemsRecycleAdapter(getActivity(), dashboardItemModels_arr, this,
                dashboard_item_arr_size);
        dashboard_recyclerV.setAdapter(dashboard_adapter);
        dashboard_adapter.notifyDataSetChanged();
    }

    public class ReportDataSync extends AsyncTask<String ,Void,String>{


        @Override
        protected String doInBackground(String... strings) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    call_elephantReportApi();
                    call_indirectReportApi();
                    call_nilReportApi();
                    call_update_elephantReportApi();
                }
            });
            return "Executed";
        }
    }


    public void call_elephantReportApi(){

        db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * from elephantReport where sync_status='0'", null);
        int count = c.getCount();
        if (count >= 1) {
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {

//                    try {
//                        JSONObject report_json_obj = new JSONObject();
//
//                        report_json_obj=request_body_elephantReport(c);
//
//                        final String surveyKey = c.getString(c.getColumnIndex("slno"));
//                        String reportId=c.getString(c.getColumnIndex("report_id"));
//
//                        check_internet=PermissionUtils.check_InternetConnection(getActivity());
//
//                        if (reportId.equalsIgnoreCase("")){
//
//                            if (check_internet.equalsIgnoreCase("true")){  //Internet connected
//
//                                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), report_json_obj.toString());
//
//                                RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
//                                retrofitInterface.addreportApi(body).enqueue(new Callback<Void>() {
//                                    @Override
//                                    public void onResponse(Call<Void> call, Response<Void> response) {
//
//                                        if (response.isSuccessful()) {
//
//                                            db = HomeFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
//                                            db.execSQL("update elephantReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");
////                                    db.close();
//                                            Snackbar.make(main_ll, "Successfully data updated in Server", Snackbar.LENGTH_SHORT).show();
//                                        }
//                                        else {
//                                            if (response.code()==500){
//                                                Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();
//
//                                            }else {
//                                                Snackbar.make(main_ll, "Please try again...!", Snackbar.LENGTH_SHORT).show();
//
//                                            }
//
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<Void> call, Throwable t) {
//
//                                        Snackbar.make(main_ll, "Failed...please try again !", Snackbar.LENGTH_SHORT).show();
//
//                                    }
//                                });
//
//                            }
//                            else {
//                                Snackbar.make(main_ll, "No internet connection !", Snackbar.LENGTH_SHORT).show();
//                            }
//
//                        }
//                        else {
//                            call_update_elephantReportApi();
//
//                        }






//                    } catch (JSONException je) {
//
//                    }
                } while (c.moveToNext());

            }
        }
        else {
            Snackbar.make(main_ll,"You don't have any data for Synchronization", Snackbar.LENGTH_LONG).show();
        }
        c.close();
        db.close();

    }

    public void call_indirectReportApi(){

        db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * from indirectReport where sync_status='0'", null);
        int count = c.getCount();
        if (count >= 1) {
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {

//                    try {
//                    JSONObject report_json_obj = new JSONObject();
//
//                    report_json_obj=request_body_indirectReport(c);
//
//                    final String surveyKey = c.getString(c.getColumnIndex("slno"));
//
//                    check_internet=PermissionUtils.check_InternetConnection(getActivity());
//
//                    if (check_internet.equalsIgnoreCase("true")){  //Internet connected
//
//                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), report_json_obj.toString());
//
//                        RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
//                        retrofitInterface.addreportApi(body).enqueue(new Callback<Void>() {
//                            @Override
//                            public void onResponse(Call<Void> call, Response<Void> response) {
//
//                                if (response.isSuccessful()) {
//
//                                    db = HomeFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
//                                    db.execSQL("update indirectReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");
////                                    db.close();
//                                    Snackbar.make(main_ll, "Successfully data updated in Server", Snackbar.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    if (response.code()==500){
//                                        Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();
//
//                                    }else {
//                                        Snackbar.make(main_ll, "Please try again...!", Snackbar.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Void> call, Throwable t) {
//
//                                Snackbar.make(main_ll, "Failed...please try again !", Snackbar.LENGTH_SHORT).show();
//
//                            }
//                        });
//
//                    }
//                    else {
//                        Snackbar.make(main_ll, "No internet connection !", Snackbar.LENGTH_SHORT).show();
//                    }


//                    } catch (JSONException je) {
//
//                    }
                } while (c.moveToNext());

            }
        }
        else {
            Snackbar.make(main_ll,"You don't have any data for Synchronization", Snackbar.LENGTH_LONG).show();
        }
        c.close();
        db.close();

    }

    public void call_nilReportApi(){

        db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * from nilReport where sync_status='0'", null);
        int count = c.getCount();
        if (count >= 1) {
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {

//                    try {
                    JSONObject report_json_obj = new JSONObject();

                    report_json_obj=request_body_nilReport(c);

                    final String surveyKey = c.getString(c.getColumnIndex("slno"));

                    check_internet=PermissionUtils.check_InternetConnection(getActivity());

                    if (check_internet.equalsIgnoreCase("true")){  //Internet connected

//                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), report_json_obj.toString());
//
//                        RetrofitInterface retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);
//                        retrofitInterface.add_NilReportApi(body).enqueue(new Callback<Void>() {
//                            @Override
//                            public void onResponse(Call<Void> call, Response<Void> response) {
//
//                                if (response.isSuccessful()) {
//
//                                    db = HomeFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
//                                    db.execSQL("update nilReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");
////                                    db.close();
//                                    Snackbar.make(main_ll, "Successfully data updated in Server", Snackbar.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    if (response.code()==500){
//                                        Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();
//
//                                    }else {
//                                        Snackbar.make(main_ll, "Please try again...!", Snackbar.LENGTH_SHORT).show();
//
//                                    }
//
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Void> call, Throwable t) {
//
//                                Snackbar.make(main_ll, "Failed...please try again !", Snackbar.LENGTH_SHORT).show();
//
//                            }
//                        });

                    }
                    else {
                        Snackbar.make(main_ll, "No internet connection !", Snackbar.LENGTH_SHORT).show();
                    }


//                    } catch (JSONException je) {
//
//                    }
                } while (c.moveToNext());

            }
        }
        else {
            Snackbar.make(main_ll,"You don't have any data for Synchronization", Snackbar.LENGTH_LONG).show();
        }
        c.close();
        db.close();

    }

    //--------------For update of Elephant Report -----

    public void call_update_elephantReportApi(){

        db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * from elephantReport where sync_status='0'", null);
        int count = c.getCount();
        if (count >= 1) {
            c.moveToFirst();
            if (c.moveToFirst()) {
                do {

//                    try {
                    JSONObject report_json_obj = new JSONObject();

                    report_json_obj=request_body_elephantReport(c);

                    final String surveyKey = c.getString(c.getColumnIndex("slno"));
                    String reportId=c.getString(c.getColumnIndex("report_id"));

                    check_internet=PermissionUtils.check_InternetConnection(getActivity());

                    if (check_internet.equalsIgnoreCase("true")){  //Internet connected

                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), report_json_obj.toString());

                        RetrofitInterface retrofitInterface = RetrofitClient.getClient("").create(RetrofitInterface.class);
                        retrofitInterface.editReport(reportId,body).enqueue(new Callback<EditReportResponseData>() {
                            @Override
                            public void onResponse(Call<EditReportResponseData> call, Response<EditReportResponseData> response) {

                                if (response.isSuccessful()) {

                                    db = HomeFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                                    db.execSQL("update elephantReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");
//                                    db.close();
                                    Snackbar.make(main_ll, "Successfully data updated in Server", Snackbar.LENGTH_SHORT).show();
                                }
                                else {
                                    if (response.code()==500){
                                        Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();

                                    }else {
                                        Snackbar.make(main_ll, "Please try again...!", Snackbar.LENGTH_SHORT).show();

                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<EditReportResponseData> call, Throwable t) {

                                Snackbar.make(main_ll, "Failed...please try again !", Snackbar.LENGTH_SHORT).show();

                            }
                        });

                    }
                    else {
                        Snackbar.make(main_ll, "No internet connection !", Snackbar.LENGTH_SHORT).show();
                    }


//                    } catch (JSONException je) {
//
//                    }
                } while (c.moveToNext());

            }
        }
        else {
            Snackbar.make(main_ll,"You don't have any data for Synchronization", Snackbar.LENGTH_LONG).show();
        }
        c.close();
        db.close();

    }
    //--------------------------------



    public JSONObject request_body_elephantReport(Cursor c){

        JSONObject report_json_obj = new JSONObject();
        try {

//          report_json_obj.put("ElephDate", c.getString(c.getColumnIndex("date"))); It take system generated date
            report_json_obj.put("location", c.getString(c.getColumnIndex("loc")));
            report_json_obj.put("division", c.getString(c.getColumnIndex("Division_Name")));
            report_json_obj.put("range", c.getString(c.getColumnIndex("Range_Name")));
            report_json_obj.put("section", c.getString(c.getColumnIndex("Section_Name")));
            report_json_obj.put("beat", c.getString(c.getColumnIndex("WlBeat_Name")));
            report_json_obj.put("sighting_time_from", c.getString(c.getColumnIndex("fromtime")));
            report_json_obj.put("sighting_time_to", c.getString(c.getColumnIndex("totime")));
            report_json_obj.put("heard", c.getString(c.getColumnIndex("herd")));
            report_json_obj.put("total", c.getString(c.getColumnIndex("noeleph")));
            report_json_obj.put("tusker", c.getString(c.getColumnIndex("tusker")));
            report_json_obj.put("mukhna", c.getString(c.getColumnIndex("male")));
            report_json_obj.put("female", c.getString(c.getColumnIndex("female")));
            report_json_obj.put("calf", c.getString(c.getColumnIndex("calf")));
            report_json_obj.put("lat_degree", c.getString(c.getColumnIndex("deg")));
            report_json_obj.put("long_degree", c.getString(c.getColumnIndex("deg1")));
            report_json_obj.put("lat_minute", c.getString(c.getColumnIndex("min")));
            report_json_obj.put("long_minute", c.getString(c.getColumnIndex("min1")));
            report_json_obj.put("lat_seconds", c.getString(c.getColumnIndex("sec")));
            report_json_obj.put("long_seconds", c.getString(c.getColumnIndex("sec1")));
            report_json_obj.put("report", "direct");
            report_json_obj.put("report_type", "");
            report_json_obj.put("report_through", "mobile");
            report_json_obj.put("uid", userId);
            report_json_obj.put("report_id", c.getString(c.getColumnIndex("report_id")));
        }catch (JSONException je){

        }
        return report_json_obj;
    }

    public JSONObject request_body_indirectReport(Cursor c){

        JSONObject report_json_obj = new JSONObject();
        try {

//          report_json_obj.put("ElephDate", c.getString(c.getColumnIndex("date"))); It take system generated date
            report_json_obj.put("location", c.getString(c.getColumnIndex("loc")));
            report_json_obj.put("division", c.getString(c.getColumnIndex("Division_Name")));
            report_json_obj.put("range", c.getString(c.getColumnIndex("Range_Name")));
            report_json_obj.put("section", c.getString(c.getColumnIndex("Section_Name")));
            report_json_obj.put("beat", c.getString(c.getColumnIndex("WlBeat_Name")));
            report_json_obj.put("sighting_time_from", c.getString(c.getColumnIndex("fromtime")));
            report_json_obj.put("sighting_time_to", c.getString(c.getColumnIndex("totime")));
            report_json_obj.put("heard", c.getString(c.getColumnIndex("herd")));
            report_json_obj.put("total", c.getString(c.getColumnIndex("noeleph")));
            report_json_obj.put("tusker", c.getString(c.getColumnIndex("tusker")));
            report_json_obj.put("mukhna", c.getString(c.getColumnIndex("male")));
            report_json_obj.put("female", c.getString(c.getColumnIndex("female")));
            report_json_obj.put("calf", c.getString(c.getColumnIndex("calf")));
            report_json_obj.put("lat_degree", c.getString(c.getColumnIndex("deg")));
            report_json_obj.put("long_degree", c.getString(c.getColumnIndex("deg1")));
            report_json_obj.put("lat_minute", c.getString(c.getColumnIndex("min")));
            report_json_obj.put("long_minute", c.getString(c.getColumnIndex("min1")));
            report_json_obj.put("lat_seconds", c.getString(c.getColumnIndex("sec")));
            report_json_obj.put("long_seconds", c.getString(c.getColumnIndex("sec1")));
//            report_json_obj.put("Remark", c.getString(c.getColumnIndex("remark")));
            report_json_obj.put("report", "indirect");
            report_json_obj.put("report_type", c.getString(c.getColumnIndex("reportType")));
            report_json_obj.put("report_through", "mobile");
            report_json_obj.put("uid", userId);
        }catch (JSONException je){

        }
        return report_json_obj;
    }

    public JSONObject request_body_nilReport(Cursor c){

        JSONObject report_json_obj = new JSONObject();
        try {

          report_json_obj.put("ElephDate", c.getString(c.getColumnIndex("date")));// It take system generated date
            report_json_obj.put("division", c.getString(c.getColumnIndex("Division_Name")));
            report_json_obj.put("range", c.getString(c.getColumnIndex("Range_Name")));
            report_json_obj.put("section", c.getString(c.getColumnIndex("Section_Name")));
            report_json_obj.put("beat", c.getString(c.getColumnIndex("WlBeat_Name")));
            report_json_obj.put("report_through", "mobile");
            report_json_obj.put("uid", userId);
        }catch (JSONException je){

        }
        return report_json_obj;
    }


}
