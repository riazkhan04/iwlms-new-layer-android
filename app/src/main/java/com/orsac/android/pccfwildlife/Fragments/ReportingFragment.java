package com.orsac.android.pccfwildlife.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Animatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orsac.android.pccfwildlife.Activities.DashboardMonitoringActivity;
import com.orsac.android.pccfwildlife.Activities.ElephantDeathIncidentReportingActivity;
import com.orsac.android.pccfwildlife.Activities.IndirectReporting;
import com.orsac.android.pccfwildlife.Activities.OtherIncidentReporting;
import com.orsac.android.pccfwildlife.Contract.DashboardContract;
import com.orsac.android.pccfwildlife.Model.DashboardItemModel;
import com.orsac.android.pccfwildlife.Activities.NilReportActivity;
import com.orsac.android.pccfwildlife.Activities.ElephantReport;
import com.orsac.android.pccfwildlife.Activities.ViewOwnReportActivity;
import com.orsac.android.pccfwildlife.Adapters.DashboardItemsRecycleAdapter;
import com.orsac.android.pccfwildlife.Model.EditReportResponseData;
import com.orsac.android.pccfwildlife.Model.FileUploadResponse;
import com.orsac.android.pccfwildlife.Model.LoginModel.LoginData;
import com.orsac.android.pccfwildlife.Model.ReportResponse.ReportAddResponse;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.Presenter.DashboardPresenter;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cdflynn.android.library.checkview.CheckView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.orsac.android.pccfwildlife.Activities.Dashboard_nw.profile_img;
import static com.orsac.android.pccfwildlife.Activities.LoginActivity.LOGIN_SHARED;
import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;


public class ReportingFragment extends Fragment implements DashboardItemsRecycleAdapter.DashItemClickListener, DashboardContract.view {

    CardView elephant_report, nil_report, indirect_report, sync;
    RecyclerView dashboard_recyclerV;
    public RecyclerView.LayoutManager layoutManager;
    private ArrayList<DashboardItemModel> dashboardItemModels_arr;
    DashboardItemsRecycleAdapter dashboard_adapter;
    public String elephantReport = "Direct Reporting", nilReport = "Nil Reporting",
            indirectReport = "Indirect Reporting", sync_str = "Report Sync", incident_report = "Incident Reporting",view_report="View Report",
            elephantSighting="Elephant Sighting";
    Context context;
    SQLiteDatabase db;
    String check_internet = "", fileName="";
    LinearLayout main_ll;
    public String token = "", checkInternet_status = "", divisionNmFrom_pref = "", userId = "",guestFlag="",username="",passwd="";
    SessionManager session;
    ProgressDialog progressDialog;
    int elephant_report_size = 0, indirect_report_size = 0, nil_report_size = 0, incident_report_size = 0,
        update_report_size = 0, total_data_count_max = 0;
    public int progressStatus = 0;
    CheckView checkV;
    ImageView crossView,cancel_dialog;
    TextView message,fileNo,total_no_file,versionNo;
    Button cancel_btn,guest_login;
    ProgressBar progressBar;
    int val=0,difference=0,sync_value=0;
    String synced_Str="To be synced - ",imeiNo="",updated_msg="",syncing_status="",roleId="";
    Handler handler=new Handler();
    PopupMenu popup;
    public final int IMEI_REQUEST_CODE=111;
    DashboardContract.presenter dashboard_presenter;
    LinearLayout progress_ll;
    TextView directreportNo,indirectreportNo,nillreportNo,incidentreportNo;
    Handler updateHandler;
    Runnable runnable;
    int seconds,delayTime=400;
    TextInputEditText user_name,password;
    Dialog dialog=null;
    ProgressBar login_progressbar;
    RetrofitInterface apiInterface_signin;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public ReportingFragment(String guestStatus) {
        try {
            guestFlag=guestStatus;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        try {

            elephant_report = view.findViewById(R.id.elephreport);
            nil_report = view.findViewById(R.id.nilreport);
            indirect_report = view.findViewById(R.id.indirectReport);
            sync = view.findViewById(R.id.sync);
            main_ll = view.findViewById(R.id.main_ll);
            dashboardItemModels_arr = new ArrayList<>();
            dashboard_recyclerV = view.findViewById(R.id.dashboard_recyclerV);
            progress_ll=view.findViewById(R.id.progress_ll);

            directreportNo=view.findViewById(R.id.directreportNo);
            indirectreportNo=view.findViewById(R.id.indirectreportNo);
            nillreportNo=view.findViewById(R.id.nillreportNo);
            incidentreportNo=view.findViewById(R.id.incidentreportNo);
            versionNo=view.findViewById(R.id.versionNo);

            progressDialog = new ProgressDialog(context);

            session = new SessionManager(context);
            token = session.getToken();
            divisionNmFrom_pref = session.getDivision();
            userId = session.getUserID();
            dashboard_presenter=new DashboardPresenter(context,this,getActivity());

            versionNo.setText("Version - "+PermissionUtils.getVersion_Code_Name(getActivity()));//for getting version name dynamically

        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void item_clickListener(CardView cardView, int pos, ImageView item_img, ImageView elephant_img,TextView sync_value,
                                   CardView directCV,LinearLayout reporting_ll,CardView indirectCV,CardView nilCV,
                                   CardView elephant_death_cv, CardView other_incident) {
        if (guestFlag.equalsIgnoreCase("guest")){
            //For guest user

            if (pos == 0) {//For Reporting
                if (reporting_ll.getVisibility()==View.VISIBLE){
                    reporting_ll.setVisibility(View.GONE);
                }else {
                    reporting_ll.setVisibility(View.VISIBLE);
                }
                directCV.setVisibility(View.VISIBLE);
                indirectCV.setVisibility(View.VISIBLE);
                nilCV.setVisibility(View.VISIBLE);
                elephant_death_cv.setVisibility(View.GONE);
                other_incident.setVisibility(View.GONE);
            }
            else if (pos == 1) {
//            callMenu(context,elephant_img,R.menu.incident_report_menu,"incident",sync_value);//call Menu for incident

                if (reporting_ll.getVisibility()==View.VISIBLE){
                    reporting_ll.setVisibility(View.GONE);
                }else {
                    reporting_ll.setVisibility(View.VISIBLE);
                }
                directCV.setVisibility(View.GONE);
                indirectCV.setVisibility(View.GONE);
                nilCV.setVisibility(View.GONE);
                elephant_death_cv.setVisibility(View.VISIBLE);
                other_incident.setVisibility(View.VISIBLE);

            }
            else if (pos == 2) {
                if (PermissionUtils.check_InternetConnection(context) == "true") {

                    call_intializeTotaltable();
                    int total_data = getdata_countForSync();
                    sync_value.setText(synced_Str+total_data);//Sync value

                    if (total_data == 0) {
                        // --- No data to sync
                        sync_value.setText("No data to sync");

                        sync_value.setTextColor(getResources().getColor(R.color.textDark));

                        Animation animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                        sync_value.startAnimation(animation);

                    }
                    else {

                        dynamicAlertDialog(context,"Do you really want to sync ? ","Syncing ","yes",sync_value);

                    }

//                callMenu(context,elephant_img,R.menu.sync_menu,"sync",sync_value);//call Menu for sync

                } else {
                    PermissionUtils.no_internet_Dialog(context, "No Internet Connection !", new ReportDataSync());
                }

            }

        }else {
            //for Login user
            if (pos == 0) {//For Reporting
                if (reporting_ll.getVisibility()==View.VISIBLE){
                    reporting_ll.setVisibility(View.GONE);
                }else {
                    reporting_ll.setVisibility(View.VISIBLE);
                }
                directCV.setVisibility(View.VISIBLE);
                indirectCV.setVisibility(View.VISIBLE);
                nilCV.setVisibility(View.VISIBLE);
                elephant_death_cv.setVisibility(View.GONE);
                other_incident.setVisibility(View.GONE);
            }
            else if (pos == 1) {
//            callMenu(context,elephant_img,R.menu.incident_report_menu,"incident",sync_value);//call Menu for incident

                if (reporting_ll.getVisibility()==View.VISIBLE){
                    reporting_ll.setVisibility(View.GONE);
                }else {
                    reporting_ll.setVisibility(View.VISIBLE);
                }
                directCV.setVisibility(View.GONE);
                indirectCV.setVisibility(View.GONE);
                nilCV.setVisibility(View.GONE);
                elephant_death_cv.setVisibility(View.VISIBLE);
                other_incident.setVisibility(View.VISIBLE);

            } else if (pos == 2) {

                Intent i = new Intent(getActivity(), ViewOwnReportActivity.class);
                startActivity(i);

            }
            else if (pos == 3) {
                if (PermissionUtils.check_InternetConnection(context) == "true") {

                    call_intializeTotaltable();
                    int total_data = getdata_countForSync();
                    sync_value.setText(synced_Str+total_data);//Sync value

                    if (total_data == 0) {
                        // --- No data to sync
                        sync_value.setText("No data to sync");

                        sync_value.setTextColor(getResources().getColor(R.color.textDark));

                        Animation animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                        sync_value.startAnimation(animation);

                    }
                    else {

                        dynamicAlertDialog(context,"Do you really want to sync ? ","Syncing ","yes",sync_value);

                    }

//                callMenu(context,elephant_img,R.menu.sync_menu,"sync",sync_value);//call Menu for sync

                } else {
                    PermissionUtils.no_internet_Dialog(context, "No Internet Connection !", new ReportDataSync());
                }

            }
        }


//        else if (pos==4){
//
////            Snackbar.make(main_ll, "In Progress...", Snackbar.LENGTH_LONG).show();
////            Intent i = new Intent(getActivity(), ViewOwnReportActivity.class);
////            startActivity(i);
//        }
//        else if (pos == 5) { //for Sync option
////            if (PermissionUtils.check_InternetConnection(context) == "true") {
////
//////                call_intializeTotaltable();
//////                int total_data = getdata_countForSync();
//////                sync_value.setText(synced_Str+total_data);//Sync value
//////
//////                if (total_data == 0) {
//////                    // --- No data to sync
//////                    sync_value.setText("No data to sync");
//////
//////                    sync_value.setTextColor(getResources().getColor(R.color.textDark));
//////
//////                    Animation animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
//////                    sync_value.startAnimation(animation);
//////
//////                }
//////                else {
////
////                    callMenu(context,elephant_img,R.menu.sync_menu,"sync",sync_value);//call Menu for sync
////
//////                    dynamicAlertDialog(context,"Do you really want to sync ? ","Syncing ","yes",sync_value);
//////                }
////
////
////            } else {
////                PermissionUtils.no_internet_Dialog(context, "No Internet Connection !", new ReportDataSync());
////            }
//
//        }

        directCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ElephantReport.class);
                startActivity(i);
            }
        });
        indirectCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), IndirectReporting.class);
                startActivity(i);
            }
        });
        nilCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NilReportActivity.class);
                startActivity(i);

            }
        });
        elephant_death_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent incident_intent=new Intent(context, ElephantDeathIncidentReportingActivity.class);
                incident_intent.putExtra("incident_type","elephantDeath");
                startActivity(incident_intent);
            }
        });
        other_incident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent other_incident_intent=new Intent(context, OtherIncidentReporting.class);
                other_incident_intent.putExtra("incident_type","otherIncident");
                startActivity(other_incident_intent);
            }
        });

    }

    public void callDashboard_Adapter(String division_name, int dashboard_item_arr_size,int sync_value) {
        try {
            dashboard_recyclerV.setHasFixedSize(true);
            dashboard_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview

//        layoutManager = new GridLayoutManager(getActivity(),2);
            layoutManager = new LinearLayoutManager(getActivity());
            dashboard_recyclerV.setLayoutManager(layoutManager);

            dashboardItemModels_arr.clear();

            if (guestFlag.equalsIgnoreCase("guest")){

                dashboardItemModels_arr.add(new DashboardItemModel(elephantSighting, "0", R.drawable.elephant_sighting, R.drawable.ic_elephant));
                dashboardItemModels_arr.add(new DashboardItemModel(incident_report, "1", R.drawable.reporting_incident, R.drawable.ic_elephant));
//                dashboardItemModels_arr.add(new DashboardItemModel(view_report, "4", R.drawable.gajabandhureport_view, R.drawable.ic_elephant));
                dashboardItemModels_arr.add(new DashboardItemModel(sync_str, "2", R.drawable.reporting_sync, R.drawable.ic_elephant));


            }else {

                dashboardItemModels_arr.add(new DashboardItemModel(elephantSighting, "0", R.drawable.elephant_sighting, R.drawable.ic_elephant));
                dashboardItemModels_arr.add(new DashboardItemModel(incident_report, "1", R.drawable.reporting_incident, R.drawable.ic_elephant));
                dashboardItemModels_arr.add(new DashboardItemModel(view_report, "2", R.drawable.gajabandhureport_view, R.drawable.ic_elephant));
                dashboardItemModels_arr.add(new DashboardItemModel(sync_str, "3", R.drawable.reporting_sync, R.drawable.ic_elephant));

            }

            dashboard_adapter = new DashboardItemsRecycleAdapter(getActivity(), dashboardItemModels_arr, this,
                    dashboard_item_arr_size,sync_value,guestFlag);
            dashboard_recyclerV.setAdapter(dashboard_adapter);
            dashboard_adapter.notifyDataSetChanged();

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

    public class ReportDataSync extends AsyncTask<Integer, Integer, List<Integer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (elephant_report_size == 0 && indirect_report_size == 0 && nil_report_size == 0 && incident_report_size == 0) {
                dismissProgressDialog(context);
            } else {
//                showProgress_dialog(context, getdata_countForSync());

            }

        }

        @Override
        protected List<Integer> doInBackground(Integer... strings) {
            List<Integer> taskList = null;
            try {
                int count = strings.length;
                // Initialize a new list
                taskList = new ArrayList<>(count);
                // Loop through the task
                for (int i = 0; i < count; i++) {
                    // Do the current task here
                    int currentTask = strings[i];
                    taskList.add(currentTask);

                    // Sleep the thread for 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isCancelled()) {
                        break;
                    }
                    if (i > taskList.size()) {
                        dismissProgressDialog(context);
                    }
//                    if (taskList.size()>0){
//                        callDialog_forProgress(context,getdata_countForSync(),strings);
//                    }

                    if (count != 0) {
                        if (strings[i] == count) {
//                            && strings[1]==0  && strings[2]==0  && strings[3]==0  && strings[4]==0
                            dismissProgressDialog(context);
                        } else {
//                            progressDialog.setProgress((int) (((i + 1) / (float) count) * getdata_countForSync()));
                            publishProgress((int) (((i + 1) / (float) count) * getdata_countForSync()));

                    }

                    }

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if (elephant_report_size == 0 && indirect_report_size == 0 && nil_report_size == 0 && incident_report_size == 0) {

                                Snackbar.make(main_ll, "You don't have any report for Synchronization", Snackbar.LENGTH_LONG).show();

                            } else {

                                call_elephantReportApi();
                                call_indirectReportApi();
                                call_nilReportApi();
                                call_incidentReportApi();
//                                call_update_elephantReportApi();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {

            }

            return taskList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            total_no_file.setText(""+getdata_countForSync());


        }

        @Override
        protected void onPostExecute(List<Integer> s) {
            super.onPostExecute(s);

            val=100/getdata_countForSync();
            difference=100/getdata_countForSync();
            progressBar.setProgress(val);

                for (int i=0;i<s.size();i++){
                    fileNo.setText(""+(s.get(i)+1));
                    progressBar.setProgress((s.get(i)+1)*(val+1));

                    if (s.get(i)>=getdata_countForSync()-1){
                        message.setText("Updating please wait... !");

                        //hold for 4 sec then update
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (updated_msg.equalsIgnoreCase("error")){
                                    message.setText("Internal Server Error !");
                                    checkV.setVisibility(View.GONE);
                                    checkV.uncheck();
                                    crossView.setVisibility(View.VISIBLE);
                                    ((Animatable) crossView.getDrawable()).start();

                                }else if (updated_msg.equalsIgnoreCase("failed")){
                                    message.setText("Failed..Please try again !");
                                    checkV.setVisibility(View.GONE);
                                    checkV.uncheck();
                                    crossView.setVisibility(View.VISIBLE);
                                    ((Animatable) crossView.getDrawable()).start();

                                }
                                else if (updated_msg.equalsIgnoreCase("position_invalid")){
                                    message.setText("Report given is not in a valid position !");
                                    checkV.setVisibility(View.GONE);
                                    checkV.uncheck();
                                    crossView.setVisibility(View.VISIBLE);
                                    ((Animatable) crossView.getDrawable()).start();
                                }
                                else if (updated_msg.equalsIgnoreCase("success")){

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            message.setText("Updated Successfully");
                                            checkV.setVisibility(View.VISIBLE);
                                            checkV.check();
                                            crossView.setVisibility(View.GONE);
                                        }
                                    },9000);

                                }
                                else {
                                    message.setText("Please wait...or try again !");
                                    checkV.setVisibility(View.INVISIBLE);
                                    checkV.uncheck();
                                    crossView.setVisibility(View.GONE);
                                }


                            }
                        },4500);

                    }
                }


        }


    }


    public void call_elephantReportApi() {

        try {

            if (elephant_report_size != 0) {

                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * from elephantReport where sync_status='0'", null);
                int count = c.getCount();
                if (count >= 1) {
                    c.moveToFirst();
                    if (c.moveToFirst()) {
                        do {

//                    try {
                            JSONObject report_json_obj = new JSONObject();

                            report_json_obj = request_body_elephantReport(c);

                            final String surveyKey = c.getString(c.getColumnIndex("slno"));
                            String reportId = c.getString(c.getColumnIndex("report_id"));
                            String imei = c.getString(c.getColumnIndex("imei"));
                            String image = c.getString(c.getColumnIndex("image_uri"));

                            //Call File Upload Api
                            callFileUploadApi(imei,userId,image,reportId,report_json_obj.toString(),surveyKey,"direct");


                        } while (c.moveToNext());

                    }
                } else {
//                    Snackbar.make(main_ll, "You don't have any elephant report for Synchronization", Snackbar.LENGTH_LONG).show();
                }
                c.close();
                db.close();
            } else {

            }
        } catch (Exception e) {
            Log.d("elephant_exception", e.getMessage());
        }

    }

    public void call_indirectReportApi() {

        try {

            if (indirect_report_size != 0) {

                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * from indirectReport where sync_status='0'", null);
                int count = c.getCount();
                if (count >= 1) {
                    c.moveToFirst();
                    if (c.moveToFirst()) {
                        do {

//                    try {
                            JSONObject report_json_obj = new JSONObject();

                            report_json_obj = request_body_indirectReport(c);

                            final String surveyKey = c.getString(c.getColumnIndex("slno"));
                            String imei = c.getString(c.getColumnIndex("imei"));
//                            String reportId = c.getString(c.getColumnIndex("report_id"));
                            String reportId = "";
                            String image = c.getString(c.getColumnIndex("image_uri"));
                            fileName="";//for giving blank file name
                            //Call File Upload Api
                            if (image.equalsIgnoreCase("")){
                                callReportApi(reportId,imei,report_json_obj.toString(),surveyKey,fileName,"indirect");
                            }else {
                                callFileUploadApi(imei,userId,image,reportId,report_json_obj.toString(),surveyKey,"indirect");

                            }
//

                        } while (c.moveToNext());

                    }
                } else {
//                    Snackbar.make(main_ll, "You don't have any indirect report for Synchronization", Snackbar.LENGTH_LONG).show();
                }
                c.close();
                db.close();
            } else {

            }
        } catch (Exception e) {
            Log.d("indirect_exception", e.getMessage());
        }

    }

    public void call_nilReportApi() {

        try {
            if (nil_report_size != 0) {

                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * from nilReport where sync_status='0'", null);
                int count = c.getCount();
                if (count >= 1) {
                    c.moveToFirst();
                    if (c.moveToFirst()) {
                        do {

//                    try {
                            JSONObject report_json_obj = new JSONObject();

                            report_json_obj = request_body_nilReport(c);

                            final String surveyKey = c.getString(c.getColumnIndex("slno"));
                            String imei = c.getString(c.getColumnIndex("imei"));

                            fileName="";
                            callReportApi("",imei,report_json_obj.toString(),surveyKey,fileName,"nill");


                        } while (c.moveToNext());

                    }
                } else {
//                    Snackbar.make(main_ll, "You don't have any nil report for Synchronization", Snackbar.LENGTH_LONG).show();
                }
                c.close();
                db.close();
            } else {

            }
        } catch (Exception e) {
            Log.d("nil_exception", e.getMessage());
        }

    }

    //-------------Incident Reporting---------------

    public void call_incidentReportApi() {

        try {
            if (incident_report_size != 0) {

                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * from incidentReport where sync_status='0'", null);
                int count = c.getCount();
                if (count >= 1) {
                    c.moveToFirst();
                    if (c.moveToFirst()) {
                        do {

//                    try {
                            JSONObject report_json_obj = new JSONObject();

                            report_json_obj = request_body_incidentReport(c);

                            final String surveyKey = c.getString(c.getColumnIndex("slno"));
                            String imei = c.getString(c.getColumnIndex("imei"));
                            String image = c.getString(c.getColumnIndex("image_uri"));
                            String incidentType = c.getString(c.getColumnIndex("incident_type"));

                            if (incidentType.equalsIgnoreCase("elephant death")){
                                callFileUploadApi(imei,userId,image,"",report_json_obj.toString(),surveyKey,"elephant_death_incident");
                            }else {
                                callFileUploadApi(imei,userId,image,"",report_json_obj.toString(),surveyKey,"incident");
                            }


                        } while (c.moveToNext());

                    }
                } else {
//                    Snackbar.make(main_ll, "You don't have any incident report for Synchronization", Snackbar.LENGTH_LONG).show();
                }
                c.close();
                db.close();
            } else {

            }
        } catch (Exception e) {
            Log.d("incident_exception", e.getMessage());
        }

    }


    //--------------For update of Elephant Report -----

    public void call_update_elephantReportApi() {

        try {

            if (update_report_size != 0) {


                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * from elephantReport where sync_status='0'", null);
                int count = c.getCount();
                if (count >= 1) {
                    c.moveToFirst();
                    if (c.moveToFirst()) {
                        do {

//                    try {
                            JSONObject report_json_obj = new JSONObject();

                            report_json_obj = request_body_elephantReport(c);

                            final String surveyKey = c.getString(c.getColumnIndex("slno"));
                            String reportId = c.getString(c.getColumnIndex("report_id"));

                            check_internet = PermissionUtils.check_InternetConnection(getActivity());

                            if (check_internet.equalsIgnoreCase("true")) {  //Internet connected

                                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), report_json_obj.toString());

                                RetrofitInterface retrofitInterface = RetrofitClient.getClient("").create(RetrofitInterface.class);
                                retrofitInterface.editReport(reportId, body).enqueue(new Callback<EditReportResponseData>() {
                                    @Override
                                    public void onResponse(Call<EditReportResponseData> call, Response<EditReportResponseData> response) {

                                        if (response.isSuccessful()) {

                                            db = ReportingFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                                            db.execSQL("update elephantReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");
//                                    db.close();
//                                            checkV.check();
//                                            message.setText("Updated Successfully");
                                            Snackbar.make(main_ll, "Successfully data updated in Server", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            if (response.code() == 500) {
                                                Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();

                                            } else {
                                                Snackbar.make(main_ll, "Please try again...!", Snackbar.LENGTH_SHORT).show();

                                            }

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<EditReportResponseData> call, Throwable t) {

                                        Snackbar.make(main_ll, "Failed...please try again !", Snackbar.LENGTH_SHORT).show();

                                    }
                                });

                            } else {
                                Snackbar.make(main_ll, "No internet connection !", Snackbar.LENGTH_SHORT).show();
                            }


//                    } catch (JSONException je) {
//
//                    }
                        } while (c.moveToNext());

                    }
                } else {
                    Snackbar.make(main_ll, "You don't have any updated data for Synchronization", Snackbar.LENGTH_LONG).show();
                }
                c.close();
                db.close();
            } else {

            }
        } catch (Exception e) {

            Log.d("update_ele_exception", e.getMessage());
        }

    }
    //--------------------------------

        /* Api calling key for reporting -----
           ==================================*/
    public JSONObject request_body_elephantReport(Cursor c) {

        JSONObject report_json_obj = new JSONObject();
        try {

//          report_json_obj.put("ElephDate", c.getString(c.getColumnIndex("date"))); It take system generated date from server
            report_json_obj.put("sightingDate", c.getString(c.getColumnIndex("date")));// date from mobile
//            report_json_obj.put("syncDate", "");// It take system generated date from server
            report_json_obj.put("location", c.getString(c.getColumnIndex("loc")));

//            report_json_obj.put("division", c.getString(c.getColumnIndex("Division_Name")));
//            report_json_obj.put("range", c.getString(c.getColumnIndex("Range_Name")));
//            report_json_obj.put("section", c.getString(c.getColumnIndex("Section_Name")));
//            report_json_obj.put("beat", c.getString(c.getColumnIndex("WlBeat_Name")));

            report_json_obj.put("division", "");//for now
            report_json_obj.put("range", "");
            report_json_obj.put("section", "");
            report_json_obj.put("beat", "");

            report_json_obj.put("sightingTimefrom", c.getString(c.getColumnIndex("fromtime")));
            report_json_obj.put("sightingTimeTo", c.getString(c.getColumnIndex("totime")));
            report_json_obj.put("heard", c.getString(c.getColumnIndex("herd")));
            report_json_obj.put("total", c.getString(c.getColumnIndex("noeleph")));
            report_json_obj.put("tusker", c.getString(c.getColumnIndex("tusker")));
            report_json_obj.put("mukhna", c.getString(c.getColumnIndex("male")));
            report_json_obj.put("female", c.getString(c.getColumnIndex("female")));
            report_json_obj.put("calf", c.getString(c.getColumnIndex("calf")));

            report_json_obj.put("latitude", c.getString(c.getColumnIndex("deg")));
            report_json_obj.put("longitude", c.getString(c.getColumnIndex("deg1")));

//            report_json_obj.put("latitude", "21.62494");//deogarh
//            report_json_obj.put("longitude","84.828964");
            //Previously sent for lat and lng
//            report_json_obj.put("lat_degree", c.getString(c.getColumnIndex("deg")));
//            report_json_obj.put("long_degree", c.getString(c.getColumnIndex("deg1")));
//            report_json_obj.put("lat_minute", c.getString(c.getColumnIndex("min")));
//            report_json_obj.put("long_minute", c.getString(c.getColumnIndex("min1")));
//            report_json_obj.put("lat_seconds", c.getString(c.getColumnIndex("sec")));
//            report_json_obj.put("long_seconds", c.getString(c.getColumnIndex("sec1")));

            report_json_obj.put("remarks", "");
//            report_json_obj.put("report", "");
            report_json_obj.put("indirectreportType", "");
            report_json_obj.put("report_type", "direct");//for now
//            report_json_obj.put("report_type", "");
            report_json_obj.put("report_through", "mobile");
            report_json_obj.put("user", userId);
            report_json_obj.put("altitude", c.getString(c.getColumnIndex("altitude")));
            report_json_obj.put("accuracy", c.getString(c.getColumnIndex("accuracy")));

        } catch (JSONException je) {

        }
        return report_json_obj;
    }

    public JSONObject request_body_indirectReport(Cursor c) {

        JSONObject report_json_obj = new JSONObject();
        try {

//          report_json_obj.put("ElephDate", c.getString(c.getColumnIndex("date"))); It take system generated date from server
            report_json_obj.put("sightingDate", c.getString(c.getColumnIndex("date")));// date from mobile
//            report_json_obj.put("syncDate", "");// It take system generated date from server
            report_json_obj.put("location", c.getString(c.getColumnIndex("loc")));
            report_json_obj.put("division", c.getString(c.getColumnIndex("Division_Name")));
            report_json_obj.put("range", c.getString(c.getColumnIndex("Range_Name")));
            report_json_obj.put("section", c.getString(c.getColumnIndex("Section_Name")));
            report_json_obj.put("beat", c.getString(c.getColumnIndex("WlBeat_Name")));
            report_json_obj.put("sightingTimefrom", c.getString(c.getColumnIndex("fromtime")));
            report_json_obj.put("sightingTimeTo", c.getString(c.getColumnIndex("totime")));
            report_json_obj.put("heard", c.getString(c.getColumnIndex("herd")));
            report_json_obj.put("total", c.getString(c.getColumnIndex("noeleph")));
            report_json_obj.put("tusker", c.getString(c.getColumnIndex("tusker")));
            report_json_obj.put("mukhna", c.getString(c.getColumnIndex("male")));
            report_json_obj.put("female", c.getString(c.getColumnIndex("female")));
            report_json_obj.put("calf", c.getString(c.getColumnIndex("calf")));

            report_json_obj.put("latitude", c.getString(c.getColumnIndex("deg")));
            report_json_obj.put("longitude", c.getString(c.getColumnIndex("deg1")));

            report_json_obj.put("remarks", c.getString(c.getColumnIndex("remark")));
//            report_json_obj.put("report", "");//indirect
            report_json_obj.put("report_type", "indirect");
            report_json_obj.put("indirectreportType", c.getString(c.getColumnIndex("reportType")));
            report_json_obj.put("report_through", "mobile");
            report_json_obj.put("user", userId);
            report_json_obj.put("altitude", c.getString(c.getColumnIndex("altitude")));
            report_json_obj.put("accuracy", c.getString(c.getColumnIndex("accuracy")));

        } catch (JSONException je) {

        }
        return report_json_obj;
    }
//For Nil Report request
    public JSONObject request_body_nilReport(Cursor c) {

        JSONObject report_json_obj = new JSONObject();
        try {
            report_json_obj.put("sightingDate", c.getString(c.getColumnIndex("date")));// date from mobile
//            report_json_obj.put("syncDate", "");// It take system generated date from server
            report_json_obj.put("location", c.getString(c.getColumnIndex("loc")));// It take system generated date from server
            report_json_obj.put("division", c.getString(c.getColumnIndex("Division_Name")));
            report_json_obj.put("range", c.getString(c.getColumnIndex("Range_Name")));
            report_json_obj.put("section", c.getString(c.getColumnIndex("Section_Name")));
            report_json_obj.put("beat", c.getString(c.getColumnIndex("WlBeat_Name")));
            report_json_obj.put("sightingTimefrom", c.getString(c.getColumnIndex("fromtime")));
            report_json_obj.put("sightingTimeTo", c.getString(c.getColumnIndex("totime")));

            report_json_obj.put("heard", "0");
            report_json_obj.put("total", "0");
            report_json_obj.put("tusker", "0");
            report_json_obj.put("mukhna", "0");
            report_json_obj.put("female", "0");
            report_json_obj.put("calf", "0");
            report_json_obj.put("latitude", c.getString(c.getColumnIndex("deg")));
            report_json_obj.put("longitude", c.getString(c.getColumnIndex("deg1")));
            report_json_obj.put("remarks", c.getString(c.getColumnIndex("remark")));
            report_json_obj.put("indirectreportType", "");
            report_json_obj.put("report_type", "nill");
            report_json_obj.put("report_through", "mobile");
            report_json_obj.put("user", userId);
            report_json_obj.put("altitude", c.getString(c.getColumnIndex("altitude")));
            report_json_obj.put("accuracy", c.getString(c.getColumnIndex("accuracy")));

        } catch (JSONException je) {

        }
        return report_json_obj;
    }

    public JSONObject request_body_incidentReport(Cursor c) {

        JSONObject report_json_obj = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray(c.getString(c.getColumnIndex("incident")));

//          report_json_obj.put("ElephDate", c.getString(c.getColumnIndex("date"))); It take system generated date from server
            report_json_obj.put("incidentDate", c.getString(c.getColumnIndex("date")));// date from mobile
//            report_json_obj.put("syncDate", "");// It take system generated date from server
            report_json_obj.put("location", c.getString(c.getColumnIndex("loc")));
            report_json_obj.put("reportType", c.getString(c.getColumnIndex("incident_type")));
            report_json_obj.put("incidentOf", "");
//            report_json_obj.put("latitude", "20.96191");
            report_json_obj.put("latitude", c.getString(c.getColumnIndex("deg")).replace("\u00B0",""));
            report_json_obj.put("longitude", c.getString(c.getColumnIndex("deg1")).replace("\u00B0",""));
//            report_json_obj.put("longitude", "86.64230");
//            report_json_obj.put("latitude", "20.7788246");

            report_json_obj.put("damage",jsonArray);
//            report_json_obj.put("damage", c.getString(c.getColumnIndex("incident")).replace("\"" ,""));
            report_json_obj.put("accuracy", c.getString(c.getColumnIndex("accuracy")));
            report_json_obj.put("altitude", c.getString(c.getColumnIndex("altitude")));
            report_json_obj.put("user", userId);
            report_json_obj.put("incidentRemark", c.getString(c.getColumnIndex("incident_remark")));//remark for selection of type i.e.Other
            report_json_obj.put("deathReason", c.getString(c.getColumnIndex("death_reason")));//death_reason elephant death
//            report_json_obj.put("imgPath",  c.getString(c.getColumnIndex("image_uri")));
//            report_json_obj.put("report_through", "mobile");
            report_json_obj.put("circleId", c.getString(c.getColumnIndex("circleId")));

        } catch (JSONException je) {
            je.printStackTrace();
        }
        return report_json_obj;
    }

    public void showProgress_dialog(Context context, int data_count_max) {

        // Initialize the progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(data_count_max);
        progressDialog.incrementProgressBy(1);
        progressDialog.setTitle("Syncing Data to Server");
        progressDialog.setMessage("Please wait...we are synchronizing your data !");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (progressDialog.getProgress() <= progressDialog
                            .getMax()) {
                        Thread.sleep(300);
                        handle.sendMessage(handle.obtainMessage());
                        if (progressDialog.getProgress() == progressDialog
                                .getMax()) {
                            progressDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        progressDialog.show();
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.incrementProgressBy(1);
        }
    };

    public void dismissProgressDialog(Context context){
        progressDialog.dismiss();
    }

    public int getReport_dataSize(String reportType) {
        int count=0;
        Cursor c=null;
        try {

            if (reportType.equalsIgnoreCase("elephant_report")) {
                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                c = db.rawQuery("SELECT * from elephantReport where sync_status='0'", null);
                count = c.getCount();
            }
            else if (reportType.equalsIgnoreCase("indirect_report")) {
                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                c = db.rawQuery("SELECT * from indirectReport where sync_status='0'", null);
                count = c.getCount();
            }
            else if (reportType.equalsIgnoreCase("nil_report")) {
                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                c = db.rawQuery("SELECT * from nilReport where sync_status='0'", null);
                count = c.getCount();
            }
            else if (reportType.equalsIgnoreCase("incident_report")) {
                db = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                c = db.rawQuery("SELECT * from incidentReport where sync_status='0'", null);
                count = c.getCount();
            }

            db.close();
            c.close();

        }catch (Exception e){
            Log.d("exception_onSize",e.getMessage());
        }
        return count;
    }

    public int getdata_countForSync(){
        total_data_count_max=elephant_report_size+
                             indirect_report_size+
                             nil_report_size+
                             incident_report_size;
        return total_data_count_max;
    }



    //Dialog for Progress ----
    public void callProgressDialog(){
        Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_progess_dialog);
        dialog.setCancelable(false);

        checkV=dialog.findViewById(R.id.check);

        cancel_btn=dialog.findViewById(R.id.cancel_btn);
        crossView = dialog.findViewById(R.id.cross);

        message=dialog.findViewById(R.id.message);
        fileNo=dialog.findViewById(R.id.fileNo);
        total_no_file=dialog.findViewById(R.id.total_no_file);
        progressBar=dialog.findViewById(R.id.progress_bar_dialog);

//        if (updated_msg.equalsIgnoreCase("success")){
//            cancel_btn.setText("Wait...");
//        }

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onResume();
//                if (cancel_btn.getText().toString().equalsIgnoreCase("Done")) {
//
//                }
//                else {
//                    Toast.makeText(context, "Please wait while uploading", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        dialog.show();
    }

    public void call_intializeTotaltable(){
        elephant_report_size = getReport_dataSize("elephant_report");
        indirect_report_size = getReport_dataSize("indirect_report");
        nil_report_size = getReport_dataSize("nil_report");
        incident_report_size = getReport_dataSize("incident_report");
    }

    @Override
    public void onResume() {
        super.onResume();

        call_intializeTotaltable();
        int total_data = getdata_countForSync();
        sync_value=getdata_countForSync();
        callDashboard_Adapter("", 4,sync_value);

        //for auto sync ------
        if (total_data == 0) {
            //from onResume() for demo testing
//            profile_img.setVisibility(View.VISIBLE);
//            guestLogin(context);

        } else {
            if (syncing_status.equalsIgnoreCase("processing")){
                Toast.makeText(context, context.getResources().getString(R.string.already_syncing_report), Toast.LENGTH_SHORT).show();
            }else {

                if (PermissionUtils.check_InternetConnection(context) == "true") {

                    if (guestFlag.equalsIgnoreCase("guest")){
                        profile_img.setVisibility(View.GONE);//menu img to hide for guest
                        if (dialog!=null) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                                guestLogin(context);

                            } else {
                                guestLogin(context);//from onResume()

                            }
                        }else {
                            guestLogin(context);//from onResume()

                        }

                    }else {
                        profile_img.setVisibility(View.VISIBLE);//menu img to show for guest
                        new AutoSyncDataSync().execute();//call in onResume() when login

                    }


                }else {
                    Toast.makeText(context, "Please connect internet to sync !", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public void dynamicAlertDialog(Context context,String message,String title,String buttonNeed,TextView sync_value){

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));

        if (buttonNeed.equalsIgnoreCase("yes")){
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Sync", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            // TODO: 24-11-2020 Added for sync -----
                            Toast.makeText(getContext(), "Syncing...", Toast.LENGTH_SHORT).show();
                            call_intializeTotaltable();
                            int total_data = getdata_countForSync();
                            sync_value.setText(synced_Str+total_data);//Sync value

                            if (total_data == 0) {
                                Snackbar.make(main_ll, "You don't have any report for Synchronization", Snackbar.LENGTH_LONG).show();

                            } else {
                                if (PermissionUtils.check_InternetConnection(context) == "true") {

                                    if (guestFlag.equalsIgnoreCase("guest")){
                                        Toast.makeText(context, "Login to sync", Toast.LENGTH_SHORT).show();

                                        guestLogin(context);//for guest to login


                                    }else {
                                        new AutoSyncDataSync().execute();//click to sync
                                    }
                                }else {
                                    Toast.makeText(context, "Please connect internet to sync !", Toast.LENGTH_SHORT).show();
                                }
                                //old method to show progress of report
//                                for (int i = 0; i < total_data; i++) {
//                                    new ReportDataSync().execute(i);
//                                }
//                                callProgressDialog();
                            }

                            //------- end for sync -------------

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

        }

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingLeftDialogAnimation;
        alertDialog.setTitle(title);
        alertDialog.show();

    }

    private void callMenu(Context context,View popup_view_direction,int menu_nm,String type,TextView sync_value){
      try {
          popup = new PopupMenu(context, popup_view_direction);//popup view where view present
          popup.setGravity(Gravity.NO_GRAVITY);

          //Inflating the Popup using xml file
          popup.getMenuInflater().inflate(menu_nm, popup.getMenu());
//                setForceShowIcon(popup);
          popup.show();
          if (type.equalsIgnoreCase("sync")){

              popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                  @Override
                  public boolean onMenuItemClick(MenuItem item) {

                      switch (item.getItemId()) {
                          case R.id.sync_master_data:

                              dashboard_presenter.load_Update(checkInternet_status,context);
                              break;
                          case R.id.sync_reports:

                              call_intializeTotaltable();
                              int total_data = getdata_countForSync();
                              sync_value.setText(synced_Str+total_data);//Sync value

                              if (total_data == 0) {
                                  // --- No data to sync
                                  sync_value.setText("No data to sync");

                                  sync_value.setTextColor(getResources().getColor(R.color.textDark));

                                  Animation animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                                  sync_value.startAnimation(animation);

                              }
                              else {

                                  dynamicAlertDialog(context,"Do you really want to sync ? ","Syncing ","yes",sync_value);

                              }
                              break;


                          default:
                              break;
                      }
                      return true;

                  }

              });

          }else {

              popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                  @Override
                  public boolean onMenuItemClick(MenuItem item) {

                      switch (item.getItemId()) {
                          case R.id.nav_elephant_death:

                              Intent incident_intent=new Intent(context, ElephantDeathIncidentReportingActivity.class);
                              incident_intent.putExtra("incident_type","elephantDeath");
                              startActivity(incident_intent);

                              break;
                          case R.id.nav_other_incident:

                              Intent other_incident_intent=new Intent(context, OtherIncidentReporting.class);
                              other_incident_intent.putExtra("incident_type","otherIncident");
                              startActivity(other_incident_intent);
                              break;


                          default:
                              break;
                      }
                      return true;

                  }

              });
          }


      }catch (Exception e){
            e.printStackTrace();
      }

    }

    private void getImeiNo(){
        String imei="";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, IMEI_REQUEST_CODE);
                return;
            }
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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, IMEI_REQUEST_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case IMEI_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission granted.", Toast.LENGTH_SHORT).show();
                    getImeiNo();
                } else {
                    Toast.makeText(context, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void callFileUploadApi(String imeiNo,String userID,String image_nm,String reportId,String json_request,String surveyKey,String reportType){
        try {
            RetrofitInterface retrofitInterface = RetrofitClient.getNewFileUploadClient().create(RetrofitInterface.class);

            File file = new File(image_nm);
            //creating request body for file
            okhttp3.RequestBody requestFile =
                    okhttp3.RequestBody.create(okhttp3.MediaType.parse(context.getContentResolver().toString()),file);

            MultipartBody.Part image_body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//            retrofitInterface.addfileUpload(imeiNo,userID,image_body).enqueue(new Callback<FileUploadResponse>() {
//            retrofitInterface.addfileUpload(imeiNo,userID,"report",image_body).enqueue(new Callback<FileUploadResponse>() {
            retrofitInterface.updateImage(image_body,userID,"report").enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.isSuccessful()){

                        fileName=""+response.body().getFileName();

                        if (reportType.equalsIgnoreCase("elephant_death_incident")){
                            callReportApi(reportId,imeiNo,json_request,surveyKey,fileName,"elephant_death_incident");
                        }else {
                            callReportApi(reportId,imeiNo,json_request,surveyKey,fileName,reportType);
                        }


                    }else {
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    progress_ll.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception e){
            progress_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    private void callReportApi(String reportId,String imei,String json_request,String surveyKey,String fileName,String reportType){
        try {

              check_internet = PermissionUtils.check_InternetConnection(getActivity());

            if (reportId.equalsIgnoreCase("")) {

                if (check_internet.equalsIgnoreCase("true")) {  //Internet connected

                    RequestBody request_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json_request);
                    RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), json_request);
                    syncing_status="processing";
                    if (reportType.equalsIgnoreCase("incident")){
                        //For incident add report

                        try {

                            RetrofitInterface retrofitInterface = RetrofitClient.getIncidentClient().create(RetrofitInterface.class);

                            retrofitInterface.add_IncidentReportApi(fileName,request_body).enqueue(new Callback<ReportAddResponse>() {
                                @Override
                                public void onResponse(Call<ReportAddResponse> call, Response<ReportAddResponse> response) {

                                    if (response.isSuccessful()){
                                        syncing_status="";
                                        db = ReportingFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                                        db.execSQL("update incidentReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");

                                        db.close();
                                        updated_msg="success";
                                        Snackbar.make(main_ll, "Successfully data updated in Server", Snackbar.LENGTH_SHORT).show();


                                    }
                                    else {
                                        progress_ll.setVisibility(View.GONE);
                                        if (response.code() == 500) {
                                            Log.i("response-",response.raw().toString());
                                            Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();
                                            updated_msg="error";
                                            syncing_status="";

                                        }else if (response.code()==404){
                                            Snackbar.make(main_ll, "You are not in a correct position", Snackbar.LENGTH_SHORT).show();
                                            updated_msg="position_invalid";
                                            syncing_status="";
                                        }
                                        else {
                                            Snackbar.make(main_ll, "Please try again...!", Snackbar.LENGTH_SHORT).show();
                                            updated_msg="failed";
                                            syncing_status="";

                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<ReportAddResponse> call, Throwable t) {

                                    Snackbar.make(main_ll, "Failed...please try again !", Snackbar.LENGTH_SHORT).show();
                                    updated_msg="failed";
                                    syncing_status="";
                                    progress_ll.setVisibility(View.GONE);
                                }
                            });


                        }catch (Exception e){
                            progress_ll.setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                    }
                    //For Elephant Death report
                    else if (reportType.equalsIgnoreCase("elephant_death_incident")){

                        try {

                            RetrofitInterface retrofitInterface = RetrofitClient.getIncidentClient().create(RetrofitInterface.class);

                            retrofitInterface.add_ElephantDeathReportApi(fileName,request_body).enqueue(new Callback<ReportAddResponse>() {
                                @Override
                                public void onResponse(Call<ReportAddResponse> call, Response<ReportAddResponse> response) {
                                    if (response.isSuccessful()){
                                        syncing_status="";
                                        db = ReportingFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                                        db.execSQL("update incidentReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");

                                        db.close();
                                        updated_msg="success";
//                                        message.setText("Updated Successfully");
//                                        checkV.setVisibility(View.VISIBLE);
//                                        checkV.check();
                                        Snackbar.make(main_ll, "Successfully data updated in Server", Snackbar.LENGTH_SHORT).show();

                                    }
                                    else {
                                        progress_ll.setVisibility(View.GONE);
                                        if (response.code() == 500) {
                                            Log.i("response-",response.raw().toString());
                                            Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();
                                            updated_msg="error";
                                            syncing_status="";
//                                            message.setText("Internal Server Error !");
//                                            checkV.uncheck();

                                        } else if (response.code()==404){
                                            Snackbar.make(main_ll, "You are not in a correct position", Snackbar.LENGTH_SHORT).show();
                                            updated_msg="position_invalid";
                                            syncing_status="";
//                                            message.setText("You are not in a correct position");
//                                            checkV.uncheck();
                                        }
                                        else {
                                            Snackbar.make(main_ll, "Please try again...!", Snackbar.LENGTH_SHORT).show();
                                            updated_msg="failed";
                                            syncing_status="";
//                                            message.setText("Failed...some data not synced .Please try again after sometime !!");
//                                            checkV.uncheck();

                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<ReportAddResponse> call, Throwable t) {

                                    Snackbar.make(main_ll, "Failed...please try again !", Snackbar.LENGTH_SHORT).show();
                                    updated_msg="failed";
                                    progress_ll.setVisibility(View.GONE);
                                    syncing_status="";
//                                    message.setText("Failed...some data not synced .Please try again after sometime !");
//                                    checkV.uncheck();
                                }
                            });

                        }catch (Exception e){
                            progress_ll.setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                    }
                    else {

                        try {
                            //For direct ,indirect and nill report
                            RetrofitInterface retrofitInterface = RetrofitClient.getReportClient().create(RetrofitInterface.class);

                            retrofitInterface.addreportApi(fileName,request_body)
                                    .enqueue(new Callback<ReportAddResponse>() {
                                        @Override
                                        public void onResponse(Call<ReportAddResponse> call, Response<ReportAddResponse> response) {

                                            if (response.isSuccessful()) {
                                                syncing_status="";
                                                if (reportType.equalsIgnoreCase("direct")){
                                                    db = ReportingFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                                                    db.execSQL("update elephantReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");
                                                }else if (reportType.equalsIgnoreCase("indirect")){
                                                    db = ReportingFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                                                    db.execSQL("update indirectReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");

                                                } else if (reportType.equalsIgnoreCase("nill")|| reportType.equalsIgnoreCase("nil")){
                                                    db = ReportingFragment.this.getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
                                                    db.execSQL("update nilReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");
                                                }

                                                db.close();
                                                updated_msg="success";
                                                Snackbar.make(main_ll, "Successfully data updated in Server", Snackbar.LENGTH_SHORT).show();

                                                //------------------

//                                                message.setText("Updated Successfully");
//                                                checkV.setVisibility(View.VISIBLE);
//                                                checkV.check();
//                                                crossView.setVisibility(View.GONE);

                                            } else {
                                                progress_ll.setVisibility(View.GONE);
                                                if (response.code() == 500) {
                                                    Log.i("response-",response.raw().toString());
                                                    Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();
                                                    updated_msg="error";
                                                    syncing_status="";
//                                                    message.setText("Internal Server Error !");
//                                                    checkV.uncheck();

                                                }
                                                else if (response.code()==404){
                                                    Snackbar.make(main_ll, "You are not in a correct position", Snackbar.LENGTH_SHORT).show();
                                                    updated_msg="position_invalid";
                                                    syncing_status="";
//                                                    message.setText("You are not in a correct position");
//                                                    checkV.uncheck();
                                                }
                                                else {
                                                    Snackbar.make(main_ll, "Please try again...!", Snackbar.LENGTH_SHORT).show();
                                                    updated_msg="failed";
                                                    syncing_status="";
//                                                    message.setText("Failed...some data not synced .Please try again after sometime !!");
//                                                    checkV.uncheck();

                                                }

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ReportAddResponse> call, Throwable t) {

                                            Snackbar.make(main_ll, "Failed...please try again !", Snackbar.LENGTH_SHORT).show();
                                            progress_ll.setVisibility(View.GONE);
                                            syncing_status="";
//                                            message.setText("Failed...some data not synced .Please try again after sometime !");
//                                            checkV.uncheck();

                                        }
                                    });



                        }catch (Exception e){
                            progress_ll.setVisibility(View.GONE);
                            e.printStackTrace();
                        }


                    }


                } else {
                    Snackbar.make(main_ll, "No internet connection !", Snackbar.LENGTH_SHORT).show();
                }

            } else {

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //For AutoSync of Report
    public class AutoSyncDataSync extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_ll.setVisibility(View.VISIBLE);
            callUpdateNoOfCountForProgress();

        }

        @Override
        protected String doInBackground(String... strings) {

            String res="";

            try {
                if (elephant_report_size == 0 && indirect_report_size == 0 && nil_report_size == 0 && incident_report_size == 0) {


                } else {

                    call_elephantReportApi();
                    call_indirectReportApi();
                    call_nilReportApi();
                    call_incidentReportApi();
                }
            } catch (Exception e) {
                e.printStackTrace();
                progress_ll.setVisibility(View.GONE);
            }
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//
//                }
//            });
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress_ll.setVisibility(View.GONE);

                    callUpdateCountForAdapter();//call for update
                }
            },9000);

        }
    }

    public void callUpdateCountForAdapter(){
        try {
            call_intializeTotaltable();
            sync_value=getdata_countForSync();
            callDashboard_Adapter("", 4,sync_value);
            if (updateHandler!=null){
                updateHandler.removeCallbacks(runnable);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callUpdateNoOfCountForProgress(){
        try {

            updateHandler=new Handler();
            updateHandler.postDelayed(runnable=new Runnable() {
                @Override
                public void run() {
                    updateHandler.postDelayed(runnable,delayTime);

                    call_intializeTotaltable();

                    elephant_report_size = getReport_dataSize("elephant_report");
                    indirect_report_size = getReport_dataSize("indirect_report");
                    nil_report_size = getReport_dataSize("nil_report");
                    incident_report_size = getReport_dataSize("incident_report");

                    directreportNo.setText(""+elephant_report_size);
                    indirectreportNo.setText(""+indirect_report_size);
                    nillreportNo.setText(""+nil_report_size);
                    incidentreportNo.setText(""+incident_report_size);

                    Animation direct_animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                    directreportNo.startAnimation(direct_animation);
                    indirectreportNo.startAnimation(direct_animation);
                    nillreportNo.startAnimation(direct_animation);
                    incidentreportNo.startAnimation(direct_animation);

                    int total_data = getdata_countForSync();
                    if (total_data==0){
                        progress_ll.setVisibility(View.GONE);
                        callUpdateCountForAdapter();
                        updateHandler.removeCallbacks(runnable);
                    }


                }
            },0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void guestLogin(Context context){
        try {
            dialog =new Dialog(context);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.guest_login);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            cancel_dialog=dialog.findViewById(R.id.cancel_dialog);
            guest_login=dialog.findViewById(R.id.login);
            user_name=dialog.findViewById(R.id.UserName);
            password=dialog.findViewById(R.id.password);
            login_progressbar=dialog.findViewById(R.id.login_progressbar);

            cancel_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    } else {

                    }
                }
            });

            guest_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        username=user_name.getText().toString().trim();
                        passwd=password.getText().toString().trim();

                        PermissionUtils.hideKeyboard(getActivity());

                        if (PermissionUtils.check_InternetConnection(context)=="true") {

                            if (username.equalsIgnoreCase("") || passwd.equalsIgnoreCase("")) {

                                Toast.makeText(context, "Please enter valid username and password !", Toast.LENGTH_SHORT).show();

                            } else {

//                                loginPresenter.load_login(username, passwd, apiInterface_signin);
                                apiInterface_signin= RetrofitClient.getClient("signin").create(RetrofitInterface.class);

                                call_login(username, passwd, apiInterface_signin,dialog);
                            }

                        }
                        else {
//                    Toast.makeText(context, "Please connect with internet to Login !", Toast.LENGTH_SHORT).show();
                            PermissionUtils.no_internet_Dialog(context, "", null);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });


            dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //for login
    public void call_login(String usernm, String passwrd, RetrofitInterface apiInterface,Dialog dialog){

        JSONObject jsonObject=new JSONObject();
        try{

            jsonObject.put("login_id", usernm);
            jsonObject.put("password", passwrd);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObject.toString());

            apiInterface.getlogin_api(body).enqueue(new Callback<LoginData>() {
                //         apiInterface.getlogin_api(body).enqueue(new Callback<ArrayList<LoginData>>() {
                @Override
                public void onResponse(Call<LoginData> call, Response<LoginData> response) {

                    if (response.isSuccessful()){
                        if (response.body().getUsername().equalsIgnoreCase("")){
                            callToast("Invalid Credentials");
//                            loginview.hide_progress();
                        }
                        else {
                             callToast("Login Successfull");
//                            loginview.hide_progress();

                            dialog.dismiss();

                            SessionManager sessionManager=new SessionManager(getActivity());
                            SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_SHARED, 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.putString("user_name", response.body().getUsername());
                            editor.putString("token", response.body().getAccessToken());
                            for (int i=0;i<response.body().getRoles().size();i++){
                                editor.putString("roles", response.body().getRoles().get(i));
                                sessionManager.setRoles(""+response.body().getRoles().get(i));
                            }

                            editor.apply();

                            sessionManager.setUserName(response.body().getUsername());
                            sessionManager.setToken(response.body().getAccessToken());
                            sessionManager.setUserID(""+response.body().getId());
                            sessionManager.setEmail(""+response.body().getEmail());
                            sessionManager.setJuridiction(""+response.body().getJuridictionName());
                            sessionManager.setJuridictionID(""+response.body().getJuridictionId());
                            sessionManager.setRoleId(""+response.body().getRoleId());
                            sessionManager.setCircleId(""+response.body().getCircleId());
                            sessionManager.setDivisionId(""+response.body().getDivisionId());
                            sessionManager.setRangeId(""+response.body().getRangeId());
                            sessionManager.setSectionId(""+response.body().getSectionId());
                            sessionManager.setBeatId(""+response.body().getBeatId());

                            roleId=""+response.body().getRoleId();

                            if (roleId.equalsIgnoreCase("1")||roleId.equalsIgnoreCase("3")||
                                    roleId.equalsIgnoreCase("6")){ //For PCCF and other higher officer

                                Intent intent = new Intent(getActivity() , DashboardMonitoringActivity.class);
                                startActivity(intent);
                                getActivity().finish();

                            }
                            else if (roleId.equalsIgnoreCase("4") || roleId.equalsIgnoreCase("5")){//For Ranger and below
                                reload();//reload fragment
                            }
                            else if (roleId.equalsIgnoreCase("2")){//for DFO

                                Intent intent = new Intent(getActivity() , DashboardMonitoringActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }


                            Log.i("login_response",response.body().toString());
                        }
                    }
                    else {

                        callToast("Invalid user !");//For 401 status unauthorized when wrong username ,password
//                        loginview.hide_progress();
                    }
                }

                @Override
                public void onFailure(Call<LoginData> call, Throwable t) {

                    callToast("Failed...Please try again !");
//                    loginview.hide_progress();
                }
            });

        }
        catch (Exception e){
//            loginview.hide_progress();
        }
    }
    public void callToast(String msg){
        try {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reload(){
        try {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(ReportingFragment.this.getId(), new ReportingFragment("login"),"reporting_frag")
                    .commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

