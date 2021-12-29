package com.orsac.android.pccfwildlife.Presenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.orsac.android.pccfwildlife.Activities.ViewReportMapPointActivity;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.ElephantDeathIncidentDataModel;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.IncidentReportDataModel;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.Adapters.ViewReportRecyclerVNew;
import com.orsac.android.pccfwildlife.Contract.ViewReportContract;
import com.orsac.android.pccfwildlife.Fragments.ViewReportFragment;
import com.orsac.android.pccfwildlife.Model.ReportResponse.ViewReportResponse;
import com.orsac.android.pccfwildlife.MyUtils.CommonMethods;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.orsac.android.pccfwildlife.Fragments.ViewReportFragment.female_calf_LL;
import static com.orsac.android.pccfwildlife.Fragments.ViewReportFragment.tusker_makhna_LL;

public class ViewReportPresenter implements ViewReportContract.presenter,ViewReportRecyclerVNew.Update_clickListener, OnMapReadyCallback {
//public class ViewReportPresenter implements ViewReportContract.presenter,ViewReportRecyclerV_Adapter.Update_clickListener, OnMapReadyCallback {

    Context context;
    ViewReportContract.view viewreport_view;
    Activity activity;
    ViewReportRecyclerVNew view_adapter;
    String date_formatted="";
//    ViewReportRecyclerV_Adapter view_adapter;
    String dom="",location_nm="",from_time="",to_time="",division="",range="",section="",beat="",total_no="",heard="",tusker="",
        male="",female="",calf="",lat_deg="",lat_min="",lat_sec="",lng_deg="",lng_min="",lng_sec="",reportnm="",report_through="",report_type="",
        reportId="";
    GoogleMap map;

    public ViewReportPresenter(Context context, ViewReportContract.view viewreport_view,Activity activity) {
        this.context = context;
        this.viewreport_view = viewreport_view;
        this.activity=activity;
    }

//    @Override
//    public void loadViewAllReport_Data(String fromDate, String toDate, String userId, ArrayList<ViewReportItemData_obj> view_item_arr,
//                                    RecyclerView view_recyclerV, ViewReportRecyclerV_Adapter.Update_clickListener listener, TextView no_data) {
//
//        try {
//
//        viewreport_view.show_progress();
//        RetrofitInterface retrofitInterface= RetrofitClient.getReportClient().create(RetrofitInterface.class);
//        Call<ArrayList<ViewReportItemData_obj>> call=null;
//        if (fromDate.equalsIgnoreCase("") ||toDate.equalsIgnoreCase("")){
//            call=retrofitInterface.get_ViewReport("All","","","",report_type);
////            call=retrofitInterface.get_ViewReport("Balasore (WL) Division",userId,"","");
//        }
//        else {
//            call=retrofitInterface.get_ViewReport("All",fromDate,toDate,"",report_type);
////            call=retrofitInterface.get_ViewReport("Balasore (WL) Division",userId,fromDate,toDate);
//
//        }
//
//        call.enqueue(new Callback<ArrayList<ViewReportItemData_obj>>() {
//            @Override
//            public void onResponse(Call<ArrayList<ViewReportItemData_obj>> call, Response<ArrayList<ViewReportItemData_obj>> response) {
//
//                if (response.isSuccessful()){
//                    viewreport_view.hide_progress();
//                    view_item_arr.clear();
//                    for (int i=0;i<response.body().size();i++){
//
////                        Toast.makeText(context, ""+response.body().get(i), Toast.LENGTH_SHORT).show();
//                        view_item_arr.add(response.body().get(i));
//                    }
//                    if (view_item_arr.size()==0){
////                        Toast.makeText(context, "No data !", Toast.LENGTH_SHORT).show();
//                        no_data.setVisibility(View.VISIBLE);
//                        view_recyclerV.setVisibility(View.INVISIBLE);
//                    }else {
//                        no_data.setVisibility(View.INVISIBLE);
//                        view_recyclerV.setVisibility(View.VISIBLE);
//                        view_adapter=new ViewReportRecyclerV_Adapter(context,view_item_arr,ViewReportPresenter.this);
//                        view_recyclerV.setAdapter(view_adapter);
//                        view_adapter.notifyDataSetChanged();
//                    }
//
//                }
//                else {
//                    viewreport_view.hide_progress();
//                    if (response.code()==500){
//                        Toast.makeText(context, "Internal Server error !", Toast.LENGTH_SHORT).show();
//                    }else {
//                        Toast.makeText(context, "Please try again...!", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<ViewReportItemData_obj>> call, Throwable t) {
//                viewreport_view.hide_progress();
//                Toast.makeText(context, "Failed...please try again !", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

    @Override
    public void loadViewAllReport_FromCount(String fromDate, String toDate, String userId,
                                            ArrayList<ViewReportItemData_obj> view_item_arr, RecyclerView view_recyclerV,
                                            ViewReportRecyclerVNew.Update_clickListener listener) {

        try {

            viewreport_view.show_progress();
            RetrofitInterface retrofitInterface= RetrofitClient.getReportClient().create(RetrofitInterface.class);
            Call<ViewReportResponse> call=null;
            if (fromDate.equalsIgnoreCase("") ||toDate.equalsIgnoreCase("")){
                call=retrofitInterface.get_ViewAllReport("All");
            }
            else {
                call=retrofitInterface.get_ViewAllReport("All");

            }

            call.enqueue(new Callback<ViewReportResponse>() {
                @Override
                public void onResponse(Call<ViewReportResponse> call, Response<ViewReportResponse> response) {

                    if (response.isSuccessful()){
                        viewreport_view.hide_progress();
                        view_item_arr.clear();
                        for (int i=0;i<response.body().getViewReportItemData_arr().size();i++){

                            view_item_arr.add(response.body().getViewReportItemData_arr().get(i));
                        }
                        if (view_item_arr.size()==0){
                        Toast.makeText(context, "No data !", Toast.LENGTH_SHORT).show();
//                            no_data.setVisibility(View.VISIBLE);
                            view_recyclerV.setVisibility(View.INVISIBLE);
                        }else {
//                            no_data.setVisibility(View.INVISIBLE);
                            view_recyclerV.setVisibility(View.VISIBLE);
                            view_adapter=new ViewReportRecyclerVNew(context,view_item_arr,ViewReportPresenter.this);
                            view_recyclerV.setAdapter(view_adapter);
                            view_adapter.notifyDataSetChanged();
                        }

                    }
                    else {
                        viewreport_view.hide_progress();
                        if (response.code()==500){
                            Toast.makeText(context, "Internal Server error !", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Please try again...!", Toast.LENGTH_SHORT).show();
                        }


                    }

                }

                @Override
                public void onFailure(Call<ViewReportResponse> call, Throwable t) {
                    viewreport_view.hide_progress();
                    Toast.makeText(context, "Failed...please try again !", Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void loadViewReport(String fromDate, String toDate, ArrayList<ViewReportItemData_obj> view_item_arr, String circle,
                               String division, String range, String section, String beat,
                               RecyclerView view_recyclerV, ViewReportRecyclerVNew.Update_clickListener listener,
//                               RecyclerView view_recyclerV, ViewReportRecyclerV_Adapter.Update_clickListener listener,
                               TextView no_data, String report_type, ScrollView scrollView, int height, int weight, LinearLayout progressLL,
                               TextView dateTxt,TextView reportCountTxt,TextView updatedBy) {

        try {

            if (PermissionUtils.check_InternetConnection(context) == "true") {

                if (report_type.equalsIgnoreCase("All")||report_type.equalsIgnoreCase("Select Report")){
                    report_type="direct";
                }
                if (report_type.equalsIgnoreCase("Nill")||report_type.equalsIgnoreCase("Nil")){
                    report_type="nill";
                }
                if (ViewReportFragment.circleCode.equalsIgnoreCase("-1")||
                        ViewReportFragment.circleCode.equalsIgnoreCase("")){
                    circle="All";
                }else {
                    circle=ViewReportFragment.circleCode;
                }
                if (ViewReportFragment.divCode.equalsIgnoreCase("-1")||
                        ViewReportFragment.divCode.equalsIgnoreCase("")){
                    division="All";
                }else {
                    division=ViewReportFragment.divCode;
                }
                if (ViewReportFragment.rangeCode.equalsIgnoreCase("-1")||
                        ViewReportFragment.rangeCode.equalsIgnoreCase("")){
                    range="All";
                }else {
                    range=ViewReportFragment.rangeCode;
                }
                if (ViewReportFragment.secCode.equalsIgnoreCase("-1")||
                        ViewReportFragment.secCode.equalsIgnoreCase("")){
                    section="All";
                }else {
                    section=ViewReportFragment.secCode;
                }
                if (ViewReportFragment.beatCode.equalsIgnoreCase("-1") ||
                        ViewReportFragment.beatCode.equalsIgnoreCase("")){
                    beat="All";
                }else {
                    beat=ViewReportFragment.beatCode;
                }

                viewreport_view.show_progress();
                RetrofitInterface retrofitInterface= RetrofitClient.getReportClient().create(RetrofitInterface.class);
                Call<ArrayList<ViewReportItemData_obj>> call=null;
                if (fromDate.equalsIgnoreCase("") ||toDate.equalsIgnoreCase("")){
                    call=retrofitInterface.get_ViewReport(division,circle,range,section,beat,"","",report_type);
                }
                else {
                    call=retrofitInterface.get_ViewReport(division,circle,range,section,beat,fromDate,toDate,report_type);
                }

                call.enqueue(new Callback<ArrayList<ViewReportItemData_obj>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ViewReportItemData_obj>> call, Response<ArrayList<ViewReportItemData_obj>> response) {

                        if (response.isSuccessful()){
                            viewreport_view.hide_progress();
                            view_item_arr.clear();
                            for (int i=0;i<response.body().size();i++){

                                view_item_arr.add(response.body().get(i));//success response of view report
                            }
                            reportCountTxt.setText("Count : "+view_item_arr.size());
                            dateTxt.setText("Date : "+
                                    PermissionUtils.convertDate(fromDate,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy")+" to "+
                                    PermissionUtils.convertDate(toDate,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy"));

                            if (view_item_arr.size()==0){
//                        Toast.makeText(context, "No data !", Toast.LENGTH_SHORT).show();
                                reportCountTxt.setVisibility(View.GONE);
                                dateTxt.setVisibility(View.GONE);
                                no_data.setVisibility(View.VISIBLE);
                                view_recyclerV.setVisibility(View.GONE);
                            }else {
                                reportCountTxt.setVisibility(View.VISIBLE);
                                dateTxt.setVisibility(View.VISIBLE);
                                no_data.setVisibility(View.INVISIBLE);
                                view_recyclerV.setVisibility(View.VISIBLE);
//                                view_adapter=new ViewReportRecyclerV_Adapter(context,view_item_arr,ViewReportPresenter.this);
                                view_adapter=new ViewReportRecyclerVNew(context,view_item_arr,ViewReportPresenter.this);
                                view_recyclerV.setAdapter(view_adapter);
                                view_adapter.notifyDataSetChanged();

                            }

                        }
                        else {
                            viewreport_view.hide_progress();
                            if (response.code()==500){
                                no_data.setVisibility(View.VISIBLE);
                                view_recyclerV.setVisibility(View.INVISIBLE);
                                Toast.makeText(context, "Internal Server error !", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "Please try again...!", Toast.LENGTH_SHORT).show();
                            }


                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<ViewReportItemData_obj>> call, Throwable t) {
                        viewreport_view.hide_progress();
                        Log.i("url",call.request().toString());
                        Toast.makeText(context, "Failed...please try again !", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else {
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void loadBottomLayoutData(TextView date, TextView fromTime, TextView toTime, TextView location,
                                     TextView divisionTxt, TextView rangeTxt, TextView sectionTxt, TextView beatTxt,
                                     TextView reportType, TextView report, TextView totalElephant, TextView herdTxt,
                                     TextView makhnaTxt, TextView tuskerTxt, TextView femaleTxt, TextView calfTxt,
                                     TextView remark, ScrollView bottomlayout_CV) {
        try {

//            Toast.makeText(context, "Working in Presenter", Toast.LENGTH_SHORT).show();
            bottomlayout_CV.setVisibility(View.VISIBLE);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void on_CardViewClick(int position,String from_time,String to_time,Bitmap bitmap) {
        try {

           String pdf_file= PermissionUtils.createPdfFromImage(activity,from_time, to_time,bitmap);


            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    PermissionUtils.sharePdfInSocialMedia(context,pdf_file);
//                    sharePdfInSocialMedia(context,pdf_file);
                }
            },4000);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapPointClick(int position, double latitude, double longitude) {
        try {
//            Toast.makeText(context, ""+latitude, Toast.LENGTH_SHORT).show();
            //Add dialog and Map here
            callMapDialog(activity);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClickViewMore(int position, String date, String from_time, String to_time, String latitude,
                                String longitude, String total_elephant, String herd, String makhna, String tusker,
                                String female, String calf, String division, String range, String section, String beat,
                                String location, String report_img, String remark, String reportType, String report,
                                String divisionId,String updatedBy,String indirectReportType,String duplicateReport) {
        try {

//            Toast.makeText(context, "Working....", Toast.LENGTH_SHORT).show();

            ViewReportFragment.dom_txt.setText(convertDate(date,
                    "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy"
            ));
            ViewReportFragment.from_time_txt.setText(convertDate(from_time,
                    "yyyy-MM-dd HH:mm:ss.SSS","HH:mm"
            ));
            ViewReportFragment.to_time_txt.setText(convertDate(to_time,
                    "yyyy-MM-dd HH:mm:ss.SSS","HH:mm"
            ));

            if (total_elephant==null ||total_elephant.equalsIgnoreCase("null")){
                ViewReportFragment.total_no.setText("NA");
            }else {
                ViewReportFragment.total_no.setText(total_elephant);
            }
            if (herd==null ||herd.equalsIgnoreCase("null")){
                ViewReportFragment.heardtxt.setText("NA");
            }else {
                ViewReportFragment.heardtxt.setText(herd);
            }
            if (makhna==null ||makhna.equalsIgnoreCase("null")){
                ViewReportFragment.maletxt.setText("NA");
            }else {
                ViewReportFragment.maletxt.setText(makhna);
            }
            if (tusker==null ||tusker.equalsIgnoreCase("null")){
                ViewReportFragment.tuskertxt.setText("NA");
            }else {
                ViewReportFragment.tuskertxt.setText(tusker);
            }
            if (female==null ||female.equalsIgnoreCase("null")){
                ViewReportFragment.femaletxt.setText("NA");
            }else {
                ViewReportFragment.femaletxt.setText(female);
            }

            if (calf==null ||calf.equalsIgnoreCase("null")){
                ViewReportFragment.calftxt.setText("NA");
            }else {
                ViewReportFragment.calftxt.setText(calf);
            }

            ViewReportFragment.division_txt.setText(division);
            ViewReportFragment.range_txt.setText(range);
            ViewReportFragment.section_txt.setText(section);
            ViewReportFragment.beat_txt.setText(beat);
            ViewReportFragment.location_txt.setText(location);

            if (remark==null ||remark.equalsIgnoreCase("null")){
                ViewReportFragment.remarktxt.setText("NA");
            }else {
                ViewReportFragment.remarktxt.setText(remark);
            }

            if (remark==null ||remark.equalsIgnoreCase("null")){
                ViewReportFragment.remarktxt.setText("NA");
            }else {
                ViewReportFragment.remarktxt.setText(remark);
            }
            

            ViewReportFragment.reporttxt.setText(report.toUpperCase()+ " REPORT");

            if (latitude==null ||latitude.equalsIgnoreCase("null")){
                ViewReportFragment.accuracy_txt.setText("NA");
            }else {
                ViewReportFragment.accuracy_txt.setText(latitude);
            }
            if (longitude==null ||longitude.equalsIgnoreCase("null")){
                ViewReportFragment.report_through.setText("NA");
            }else {
                ViewReportFragment.report_through.setText(longitude);
            }
            ViewReportFragment.report_through_img.setVisibility(View.VISIBLE);
            //-----check it with mobile and web
            if (reportType.equalsIgnoreCase("Mobile")){
                ViewReportFragment.report_through_img.setImageDrawable(context.getResources().getDrawable(R.drawable.phone_img));
//                Toast.makeText(context, "Mobile", Toast.LENGTH_SHORT).show();
            }else if (reportType.equalsIgnoreCase("Web")){
//                Toast.makeText(context, "Web", Toast.LENGTH_SHORT).show();
                ViewReportFragment.report_through_img.setImageDrawable(context.getResources().getDrawable(R.drawable.web_img));
            }

            //Show report_img
            ViewReportFragment.report_img.setVisibility(View.VISIBLE);

            if (report_img==null){
                Glide.with(context)
                        .load(R.drawable.logo)
                        .error(R.drawable.no_image_found)
                        .into(ViewReportFragment.report_img);

            }else {
                Glide.with(context)
//                        .load("http://203.129.207.133:8080/wildlife/api/v1/uploadController/downloadFile?location="+report_img)
                        .load(RetrofitClient.IMAGE_URL+"report/"+report_img)
                        .error(R.drawable.no_image_found)
                        .into(ViewReportFragment.report_img);
            }

            //For first letter capital
            StringBuilder sb = new StringBuilder(report);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));

            if (duplicateReport.equalsIgnoreCase("d")){  //for duplicate report

                if (report.equalsIgnoreCase("nill") ||report.equalsIgnoreCase("nil")){
                    ViewReportFragment.total_herd_LL.setVisibility(View.GONE);
                    tusker_makhna_LL.setVisibility(View.GONE);
                    female_calf_LL.setVisibility(View.GONE);
                    ViewReportFragment.report_type.setText("Nil (Duplicate)");
                }else {
                    ViewReportFragment.total_herd_LL.setVisibility(View.VISIBLE);
                    tusker_makhna_LL.setVisibility(View.VISIBLE);
                    female_calf_LL.setVisibility(View.VISIBLE);
                    if (indirectReportType.equalsIgnoreCase("")){
                        ViewReportFragment.report_type.setText(sb.toString()+" (Duplicate)");
                    }else {
                        ViewReportFragment.report_type.setText(sb.toString()+" ( "+indirectReportType+" )"+" (Duplicate)");
                    }
                }
            }else {
                //for normal report
                if (report.equalsIgnoreCase("nill") ||report.equalsIgnoreCase("nil")){
                    ViewReportFragment.total_herd_LL.setVisibility(View.GONE);
                    tusker_makhna_LL.setVisibility(View.GONE);
                    female_calf_LL.setVisibility(View.GONE);
                    ViewReportFragment.report_type.setText("Nil");
                }else {
                    ViewReportFragment.total_herd_LL.setVisibility(View.VISIBLE);
                    tusker_makhna_LL.setVisibility(View.VISIBLE);
                    female_calf_LL.setVisibility(View.VISIBLE);
                    if (indirectReportType.equalsIgnoreCase("")){
                        ViewReportFragment.report_type.setText(sb.toString());
                    }else {
                        ViewReportFragment.report_type.setText(sb.toString()+" ( "+indirectReportType+" )");
                    }
                }
            }

            if (updatedBy.equalsIgnoreCase("null")){
                ViewReportFragment.updated_by.setText("NA");
            }else {
                ViewReportFragment.updated_by.setText(updatedBy);
            }

            ViewReportFragment.report_through_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "It is reported from "+reportType, Toast.LENGTH_SHORT).show();
                }
            });

            ViewReportFragment.locate_map_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        if (report_type.equalsIgnoreCase("")){
////                        if (report_type.equalsIgnoreCase("nill")){
//                            Toast.makeText(context, "Not a valid point to plot in map !", Toast.LENGTH_SHORT).show();
//                        }else {
                            if (latitude.equalsIgnoreCase("0.0")){

                                Toast.makeText(context, "Not a valid point to plot in map !", Toast.LENGTH_SHORT).show();

                            }else {
                                Intent intent=new Intent(context, ViewReportMapPointActivity.class);
                                intent.putExtra("lat",latitude);
                                intent.putExtra("lng",longitude);
                                intent.putExtra("divisionId",divisionId);
                                intent.putExtra("reportType",report);
                                intent.putExtra("divisionNm",division);
                                intent.putExtra("rangeNm",range);
                                intent.putExtra("total",total_elephant);
                                intent.putExtra("herd",herd);
                                intent.putExtra("date",date);
                                intent.putExtra("image",report_img);
                                context.startActivity(intent);
                            }

//                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            ViewReportFragment.report_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (report_img!=null){
                        callImageViewDialog(activity,
                                RetrofitClient.IMAGE_URL+"report/"+report_img);
                    }
                }
            });

//            ViewReportFragment.report_share.setVisibility(View.GONE);

            ViewReportFragment.report_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Bitmap bitmap=PermissionUtils.createImageScreenShotForView(ViewReportFragment.reportCV,context);
//                Toast.makeText(context, ""+bitmap, Toast.LENGTH_SHORT).show();
//                    update_clickListener.on_CardViewClick(position,viewReportData_arr.get(position).getSighting_time_from(),
//                            viewReportData_arr.get(position).getSighting_time_to(),bitmap);
                    //for Excel code
                    try {
//
                    CommonMethods commonMethods=new CommonMethods(context);
//
                    ViewReportItemData_obj viewReportItemData_obj=new ViewReportItemData_obj();
                    viewReportItemData_obj.setSighting_date(convertDate(date,
                            "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy"));
                    viewReportItemData_obj.setSighting_time_from(convertDate(from_time,
                            "yyyy-MM-dd HH:mm:ss.SSS","HH:mm"
                    ));
                    viewReportItemData_obj.setSighting_time_to(convertDate(to_time,
                            "yyyy-MM-dd HH:mm:ss.SSS","HH:mm"
                    ));
                    viewReportItemData_obj.setReport_type(report);
                    viewReportItemData_obj.setDivision(division);
                    viewReportItemData_obj.setRange(range);
                    viewReportItemData_obj.setSection(section);
                    viewReportItemData_obj.setBeat(beat);

                    viewReportItemData_obj.setTotal(total_elephant);
                    viewReportItemData_obj.setHeard(herd);
                    viewReportItemData_obj.setTusker(tusker);
                    viewReportItemData_obj.setMukhna(makhna);
                    viewReportItemData_obj.setFemale(female);
                    viewReportItemData_obj.setCalf(calf);
                    viewReportItemData_obj.setLatitude(latitude);
                    viewReportItemData_obj.setLongitudes(longitude);
                    viewReportItemData_obj.setImgAcqlocation(report_img);
                    viewReportItemData_obj.setUpdatedBy(updatedBy);

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String mFileName = "wildlife_"+report.toLowerCase()+"_report_"+timeStamp;
//                    String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                    mFilePath += "/WildLife Excel Report/" + mFileName;
//
                    commonMethods.loadExcel(ViewReportFragment.main_ll,activity,mFileName,
                            viewReportItemData_obj,new ElephantDeathIncidentDataModel(),
                            new IncidentReportDataModel(),"direct_indirect_nil");
//
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void sharePdfInSocialMedia(Context context,String pdffile_nm){
        try {

            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/wildlifePdf";
            File pdf_path= new File(pdffile_nm);//change with your path
//            File pdf_path= new File(directory_path + "/"+pdffile_nm+".pdf");//change with your path

            Toast.makeText(context, "Please wait for sharing in social media...!", Toast.LENGTH_SHORT).show();
            if (pdf_path.exists()) {
                Uri pdfPAthUri= FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdf_path);//used for higher version as uri not supported file provider used
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, pdfPAthUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("application/pdf");
                context.startActivity(Intent.createChooser(intent,"Choose File"));
            }
            else {
                Log.i("DEBUG", "File doesn't exist");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void callMapDialog(Activity context){
        try {
            Dialog mapDialog=new Dialog(context);
            mapDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mapDialog.setContentView(R.layout.mapdialog);
            TextView title=mapDialog.findViewById(R.id.title);

            MapView mapView = mapDialog.findViewById(R.id.map);
            MapsInitializer.initialize(context);

            mapView.onCreate(mapDialog.onSaveInstanceState());
            mapView.onResume();


            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    map = googleMap;
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    // Add a marker  and move the camera
                    LatLng elephant_report = new LatLng(20.2961, 85.8245);


                    map.moveCamera(CameraUpdateFactory.newLatLng(elephant_report));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(elephant_report, 15));
                    map.setMaxZoomPreference(30.0f);

                }
            });

            mapDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {

            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // Add a marker  and move the camera
            LatLng elephant_report = new LatLng(20.2961, 85.8245);


            map.moveCamera(CameraUpdateFactory.newLatLng(elephant_report));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(elephant_report, 15));
            map.setMaxZoomPreference(30.0f);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String convertDate(String dateStr,String givenFormat,String outputFormat){
        SimpleDateFormat input = new SimpleDateFormat(givenFormat);
//        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat output = new SimpleDateFormat(outputFormat);
//        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");

        Date d = null;
        try
        {
            d = input.parse(dateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        date_formatted = output.format(d);
        Log.i("DATE", "" + date_formatted);
        return date_formatted;
    }

    public void callImageViewDialog(Context context,String imgpath){
        try {

            Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.image_view_dialog);

            ImageView report_image,close_img;
            ZoomableImageView report_imgg;
//            report_image=dialog.findViewById(R.id.report_image);
            report_imgg=dialog.findViewById(R.id.report_image);
            close_img=dialog.findViewById(R.id.close_img);

            Glide.with(context)
                    .load(imgpath)
                    .error(R.drawable.no_image_found)
                    .into(report_imgg);


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                }
            });

//            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
