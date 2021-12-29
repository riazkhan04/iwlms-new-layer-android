package com.orsac.android.pccfwildlife.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.GajaBandhuViewReport;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.ImageMessageGajaBandhuActivity;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.TextMessageGajaBandhuActivity;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.VideoMessageGajaBandhuActivity;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.VoiceMessageGajaBandhuActivity;
import com.orsac.android.pccfwildlife.Model.DashboardItemModel;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SQLiteDB.GajaBandhuDbHelper;
import com.orsac.android.pccfwildlife.Adapters.GajaBandhuAdapter.GajaBandhuItemAdapter;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuReportDataModel;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class GajaBandhuItemFragment extends Fragment implements GajaBandhuItemAdapter.BanasathiItemClickListener {

    Context context;
    RecyclerView banasathi_item_recyclerV;
    GajaBandhuItemAdapter banasathiItemAdapter;
    private ArrayList<DashboardItemModel> dashboardItemModels_arr;
    RecyclerView.LayoutManager layoutManager;
    int sync_value=0;
    int gajabandhu_report_size = 0, total_data_count_max = 0;
    public String synced_Str="To be synced - ",syncing_status="",statusGajaBandhu="Pending";
    SQLiteDatabase db;
    ConstraintLayout main_ll;
    GajaBandhuDbHelper dbHelper;
    LinearLayout progress_ll;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.banasathi_item_fragment_layout,container,false);


        initData(view);

        return view;
    }

    private void initData(View view) {
        try {
            banasathi_item_recyclerV=view.findViewById(R.id.banasathi_item_recyclerV);
            dashboardItemModels_arr=new ArrayList<>();
            main_ll=view.findViewById(R.id.main_ll);
            progress_ll=view.findViewById(R.id.progress_ll);
            dbHelper=new GajaBandhuDbHelper(getActivity());

            callAdapter(4,sync_value);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callAdapter(int item_arr_size,int sync_str) {
        try {
            banasathi_item_recyclerV.setHasFixedSize(true);
            banasathi_item_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview

//        layoutManager = new GridLayoutManager(getActivity(),2);
            layoutManager = new LinearLayoutManager(getActivity());
            banasathi_item_recyclerV.setLayoutManager(layoutManager);

            dashboardItemModels_arr.clear();
            dashboardItemModels_arr.add(new DashboardItemModel(getResources().getString(R.string.text_msg), "0", R.drawable.gajabandhu_text, R.drawable.ic_elephant));
            dashboardItemModels_arr.add(new DashboardItemModel(getResources().getString(R.string.video_msg), "1", R.drawable.gajabandhu_video, R.drawable.ic_elephant));
            dashboardItemModels_arr.add(new DashboardItemModel(getResources().getString(R.string.choose_file), "2", R.drawable.gajabandhu_image, R.drawable.ic_elephant));
            dashboardItemModels_arr.add(new DashboardItemModel(getResources().getString(R.string.voice_msg), "3", R.drawable.gajabandhu_voice, R.drawable.ic_elephant));
            dashboardItemModels_arr.add(new DashboardItemModel(getResources().getString(R.string.view_report), "4", R.drawable.gajabandhureport_view, R.drawable.ic_elephant));
            dashboardItemModels_arr.add(new DashboardItemModel(getResources().getString(R.string.sync_txt), "5", R.drawable.reporting_sync, R.drawable.ic_elephant));

            banasathiItemAdapter = new GajaBandhuItemAdapter(getActivity(), dashboardItemModels_arr,
                    item_arr_size,sync_str,this);
            banasathi_item_recyclerV.setAdapter(banasathiItemAdapter);
            banasathiItemAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void item_clickListener(CardView cardView, int pos, ImageView item_img, ImageView elephant_img, TextView sync_value) {
        try {
            if (pos==0){
                Intent text_intent=new Intent(context, TextMessageGajaBandhuActivity.class);
                startActivity(text_intent);

            }else if (pos==1){

                Intent video_intent=new Intent(context, VideoMessageGajaBandhuActivity.class);
                startActivity(video_intent);

            }else if (pos==2){
                Intent image_intent=new Intent(context, ImageMessageGajaBandhuActivity.class);
                startActivity(image_intent);

            }else if (pos==3){
                Intent audio_intent=new Intent(context, VoiceMessageGajaBandhuActivity.class);
                startActivity(audio_intent);

            }else if (pos==4){
                Intent view_intent=new Intent(context, GajaBandhuViewReport.class);
                startActivity(view_intent);
            }
            else {
                if (PermissionUtils.check_InternetConnection(context) == "true") {

                call_intializeTotaltable();
                int total_data = getdata_countForSync();
                sync_value.setText(total_data+" "+getResources().getString(R.string.data_to_sync));//Sync value
//
                if (total_data == 0) {
                    // --- No data to sync
                    sync_value.setText(getResources().getString(R.string.no_data_to_sync));

                    sync_value.setTextColor(getResources().getColor(R.color.textDark));

                    Animation animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                    sync_value.startAnimation(animation);

                }
                else {
                    dynamicAlertDialog(context,getResources().getString(R.string.do_you_realy_want_to_sync),"Syncing ","yes",sync_value);
                }


                } else {
//                    PermissionUtils.no_internet_Dialog(context, "No Internet Connection !", new ReportingFragment.ReportDataSync());
                    Toast.makeText(context, "No Internet Connection !", Toast.LENGTH_SHORT).show();
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void replaceFragment(int itemId, Fragment fragmentt, String tag) {
        try {
            //initializing the fragment object which is selected
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.middle_ll, fragmentt, tag);
            ft.addToBackStack("" + itemId);
            ft.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getReport_dataSize(String reportType) {
        int count=0;
        Cursor c=null;
        try {
            db = getActivity().openOrCreateDatabase(GajaBandhuDbHelper.GAJABANDHU_DATABASE_NAME, MODE_PRIVATE, null);
            c = db.rawQuery("SELECT * from gajabandhuReport where sync_status='0'", null);
            count = c.getCount();

            db.close();
            c.close();

        }catch (Exception e){
            Log.d("exception_onSize",e.getMessage());
        }
        return count;
    }
    public void call_intializeTotaltable(){
        gajabandhu_report_size = getReport_dataSize("gajabandhu_report");
    }

    public int getdata_countForSync(){
        total_data_count_max=gajabandhu_report_size;
        return total_data_count_max;
    }

    public void dynamicAlertDialog(Context context,String message,String title,String buttonNeed,TextView sync_value){

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));

        if (buttonNeed.equalsIgnoreCase("yes")){
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.sync_txt), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            // TODO: 16-04-2021 Added for sync -----
                            Toast.makeText(getContext(), "Syncing...", Toast.LENGTH_SHORT).show();
                            call_intializeTotaltable();
                            int total_data = getdata_countForSync();
                            sync_value.setText(synced_Str+total_data);//Sync value

                            if (total_data == 0) {
                                Snackbar.make(main_ll, "You don't have any report for Synchronization", Snackbar.LENGTH_LONG).show();

                            } else {

                                new GajaBandhuReportAsync().execute();
                            }

                            //------- end for sync -------------

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();



                        }
                    });
        }
        else {
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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


    @Override
    public void onResume() {
        super.onResume();
        call_intializeTotaltable();
        int total_data = getdata_countForSync();
        sync_value=getdata_countForSync();
        callAdapter( 4,sync_value);

        //for auto sync ------
        if (total_data == 0) {
//            Snackbar.make(main_ll, "You don't have any report to sync", Snackbar.LENGTH_LONG).show();

        } else {
            if (syncing_status.equalsIgnoreCase("processing")){
                Toast.makeText(context, context.getResources().getString(R.string.already_syncing_report), Toast.LENGTH_SHORT).show();
            }else {
                new GajaBandhuReportAsync().execute();
            }

        }
    }

    //For Auto sync of Report
    class GajaBandhuReportAsync extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress_ll.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String success="";
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (PermissionUtils.check_InternetConnection(context) == "true") {
                            callToSyncGajaBandhuReport();
                        }else {
                            Toast.makeText(context, "Please connect internet to sync !", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return success;
        }
    }

    public void callToSyncGajaBandhuReport(){
        try {

            if (gajabandhu_report_size != 0) {

                db = getActivity().openOrCreateDatabase(GajaBandhuDbHelper.GAJABANDHU_DATABASE_NAME, MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * from gajabandhuReport where sync_status='0'", null);
                int count = c.getCount();
                if (count >= 1) {
                    c.moveToFirst();
                    if (c.moveToFirst()) {
                        do {

                            final String surveyKey = c.getString(c.getColumnIndex("slno"));
                            final String type = c.getString(c.getColumnIndex("reportType"));
                            final String userId = c.getString(c.getColumnIndex("userID"));
                            final String latitude = c.getString(c.getColumnIndex("latitude"));
                            final String longitude = c.getString(c.getColumnIndex("longitude"));
                            final String textMsg = c.getString(c.getColumnIndex("textMsg"));
                            final String fileName = c.getString(c.getColumnIndex("audio_video_img_path"));
                            final String folderName = c.getString(c.getColumnIndex("folder_type"));//gajabandhu
                            final String selected_date = c.getString(c.getColumnIndex("date"));//date

                            Toast.makeText(context, "Sending reports...", Toast.LENGTH_SHORT).show();

                            try {
                                syncing_status="processing";
                                RetrofitInterface retrofitInterface= RetrofitClient.getGajaBandhuRequestClient().create(RetrofitInterface.class);

                                RequestBody requestuserId = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),userId);
                                RequestBody requestlatitude = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),latitude);
                                RequestBody requestlongitude = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),longitude);
                                RequestBody requesttextMsg = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),textMsg);
                                RequestBody requestFolderNm = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),folderName);
                                RequestBody requestStatus = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),statusGajaBandhu);
                                RequestBody requestDate = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),selected_date);

                                File sendFile =null;
                                MultipartBody.Part image_multipart_body = null,video_multipart_body = null,audio_multipart_body=null,
                                        no_multipart_body=null ;

                                if (type.equalsIgnoreCase("image")){ //for image msg only
                                    sendFile = new File(fileName);
                                    RequestBody requestFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),sendFile);
                                    RequestBody otherFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),"");

                                    image_multipart_body =
                                            MultipartBody.Part.createFormData("image", sendFile.getName(), requestFile);

                                    video_multipart_body =
                                            MultipartBody.Part.createFormData("videoMessage", "", otherFile);
                                    audio_multipart_body =
                                            MultipartBody.Part.createFormData("audioMessage", "", otherFile);
                                }
                                else  if (type.equalsIgnoreCase("video")){ //for video msg only
                                    sendFile = new File(fileName);
                                    RequestBody requestFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),sendFile);
                                    RequestBody otherFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),"");

                                    video_multipart_body =
                                            MultipartBody.Part.createFormData("videoMessage", sendFile.getName(), requestFile);

                                    image_multipart_body =
                                            MultipartBody.Part.createFormData("image", "", otherFile);

                                    audio_multipart_body =
                                            MultipartBody.Part.createFormData("audioMessage", "", otherFile);
                                }
                                else  if (type.equalsIgnoreCase("voice")){//for voice msg only
                                    sendFile = new File(fileName);
                                    RequestBody requestFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),sendFile);
                                    RequestBody otherFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),"");

                                    audio_multipart_body = MultipartBody.Part.createFormData("audioMessage", sendFile.getName(), requestFile);

                                    image_multipart_body =
                                            MultipartBody.Part.createFormData("image", "", otherFile);
                                    video_multipart_body =
                                            MultipartBody.Part.createFormData("videoMessage", "", otherFile);
                                }

                                Call<GajaBandhuReportDataModel> call;
                                if (fileName.equalsIgnoreCase("")){ //for text msg only

                                    RequestBody requestFile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().toString()),"");
                                    image_multipart_body =
                                            MultipartBody.Part.createFormData("image", "", requestFile);
                                    video_multipart_body =
                                            MultipartBody.Part.createFormData("videoMessage", "", requestFile);
                                    audio_multipart_body =
                                            MultipartBody.Part.createFormData("audioMessage", "", requestFile);

                                    call=retrofitInterface.addGajaBandhuReport(requesttextMsg,requestuserId,requestlatitude,requestlongitude,
                                            image_multipart_body,audio_multipart_body,video_multipart_body,requestFolderNm,requestStatus,requestDate);

                                }else {
                                    call=retrofitInterface.addGajaBandhuReport(requesttextMsg,requestuserId,requestlatitude,requestlongitude,
                                            image_multipart_body,audio_multipart_body,video_multipart_body,requestFolderNm,requestStatus,requestDate);
                                }

                                call.enqueue(new Callback<GajaBandhuReportDataModel>() {
                                    @Override
                                    public void onResponse(Call<GajaBandhuReportDataModel> call, Response<GajaBandhuReportDataModel> response) {

                                        if (response.isSuccessful()){
                                            progress_ll.setVisibility(View.GONE);
                                            syncing_status="";
                                            Toast.makeText(context, context.getResources().getString(R.string.success_sent_to_server), Toast.LENGTH_SHORT).show();
                                            db = getActivity().openOrCreateDatabase(GajaBandhuDbHelper.GAJABANDHU_DATABASE_NAME, MODE_PRIVATE, null);
                                            db.execSQL("update gajabandhuReport set sync_status = '1' where " + "slno= '" + surveyKey + "' ");
                                            db.close();

                                            call_intializeTotaltable();
                                            sync_value=getdata_countForSync();
                                            callAdapter( 4,sync_value);

                                        }else {
                                            progress_ll.setVisibility(View.GONE);
                                            syncing_status="";
                                            if (response.code() == 500) {
                                                Toast.makeText(context, "Internal Server error !", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(context, "Please try again !!", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<GajaBandhuReportDataModel> call, Throwable t) {
                                        progress_ll.setVisibility(View.GONE);
                                        syncing_status="";
                                        Toast.makeText(context, "Failed...Please try again !!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }catch (Exception e){
                                syncing_status="";
                                progress_ll.setVisibility(View.GONE);
                                e.printStackTrace();
                            }

                        } while (c.moveToNext());

                    }
                } else {
                    progress_ll.setVisibility(View.GONE);
                    syncing_status="";
                }
                c.close();
                db.close();
            } else {

            }

        }catch (Exception e){
            progress_ll.setVisibility(View.GONE);
            syncing_status="";
            e.printStackTrace();
        }
    }
}
