package com.orsac.android.pccfwildlife.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orsac.android.pccfwildlife.Activities.IncidentReportViewActivity;
import com.orsac.android.pccfwildlife.Activities.ViewOwnReportActivity;
import com.orsac.android.pccfwildlife.Fragments.ViewReportFragment;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewReportRecyclerVNew extends RecyclerView.Adapter<ViewReportRecyclerVNew.ViewHolder>{

    Context context;
    ArrayList<ViewReportItemData_obj> viewReportData_arr;
    ViewReportRecyclerVNew.Update_clickListener update_clickListener;
    String date_formatted="", mapTypeName="";
    String selectType="";
    String divisionId="",total="",herd="",female="",makhna="",calf="",tusker="",remarks="",lat="",longi="",imgPath="",
            dateStr="",fromTime="",toTime="",division="",range="",section="",beat="",location="",reportType="",
            reportThrough="",updated_by="",indirecReportType="",duplicateReport="";

    public interface Update_clickListener{
        void on_CardViewClick(int position, String from_time, String to_time, Bitmap bitmap);

        void onMapPointClick(int position,double latitude,double longitude);

        void onClickViewMore(int position,String date,String from_time,String to_time,String latitude,String longitude,
                             String total_elephant,String herd,String makhna,String tusker,String female,String calf,
                             String division,String range,String section,String beat,String location,String report_img,
                             String remark,String reportType,String report,String divisionId,String updatedBy,
                             String indirectReportType,String duplicateReport);

    }


    public ViewReportRecyclerVNew(Context context, ArrayList<ViewReportItemData_obj> viewReportData_arr,
                                  ViewReportRecyclerVNew.Update_clickListener update_clickListener) {
        this.context = context;
        this.viewReportData_arr = viewReportData_arr;
        this.update_clickListener = update_clickListener;
    }
    public ViewReportRecyclerVNew(Context context, ArrayList<ViewReportItemData_obj> viewReportData_arr,
                                  ViewReportRecyclerVNew.Update_clickListener update_clickListener,String type) {
        this.context = context;
        this.viewReportData_arr = viewReportData_arr;
        this.update_clickListener = update_clickListener;
        this.selectType=type;
    }


    @NonNull
    @Override
    public ViewReportRecyclerVNew.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_report_item_new,parent,false);
//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.report_view_items,parent,false);

        return new ViewReportRecyclerVNew.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewReportRecyclerVNew.ViewHolder holder, int position) {

        try {

        //For date
        if (viewReportData_arr.get(position).getSighting_date()==null ||
                viewReportData_arr.get(position).getSighting_date().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getSighting_date().equalsIgnoreCase("")){
            holder.dom_txt.setText("NA");
        }else {
            holder.dom_txt.setText(convertDate(viewReportData_arr.get(position).getSighting_date(),
                    "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy"
            ));
        }

        //for division
        if (viewReportData_arr.get(position).getDivision()==null ||
                viewReportData_arr.get(position).getDivision().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getDivision().equalsIgnoreCase("")){
            holder.division_txt.setText("NA");
        }else {
            holder.division_txt.setText(viewReportData_arr.get(position).getDivision());
        }


        if (viewReportData_arr.get(position).getTotal()==null ||
                viewReportData_arr.get(position).getTotal().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getTotal().equalsIgnoreCase("")){
            holder.total_no.setText("NA");
        }else {
            holder.total_no.setText(viewReportData_arr.get(position).getTotal());
        }

        if (viewReportData_arr.get(position).getHeard()==null ||
                viewReportData_arr.get(position).getHeard().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getHeard().equalsIgnoreCase("")){
            holder.heard.setText("NA");
        }else {
            holder.heard.setText(viewReportData_arr.get(position).getHeard());
        }

        //For tusker
        if (viewReportData_arr.get(position).getTusker()==null ||
                viewReportData_arr.get(position).getTusker().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getTusker().equalsIgnoreCase("")){
            holder.tusker.setText("NA");
        }else {
            holder.tusker.setText(viewReportData_arr.get(position).getTusker());
        }

        if (viewReportData_arr.get(position).getMukhna()==null ||
                viewReportData_arr.get(position).getMukhna().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getMukhna().equalsIgnoreCase("")){
            holder.male.setText("NA");
        }else {
            holder.male.setText(viewReportData_arr.get(position).getMukhna());
        }

        if (viewReportData_arr.get(position).getFemale()==null ||
                viewReportData_arr.get(position).getFemale().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getFemale().equalsIgnoreCase("")){
            holder.female.setText("NA");
        }else {
            holder.female.setText(viewReportData_arr.get(position).getFemale());
        }

        if (viewReportData_arr.get(position).getCalf()==null ||
                viewReportData_arr.get(position).getCalf().equalsIgnoreCase("null")||
                viewReportData_arr.get(position).getCalf().equalsIgnoreCase("")){
            holder.calf.setText("NA");
        }else {
            holder.calf.setText(viewReportData_arr.get(position).getCalf());
        }

        if (viewReportData_arr.get(position).getReport_type()!=null) {
            if (viewReportData_arr.get(position).getDuplicateReport()!=null) {
                if (viewReportData_arr.get(position).getDuplicateReport().equalsIgnoreCase("d")) {
                    //for duplicate report
                    if (viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("nill") ||
                            viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("nil")) {
                        holder.reportType_txt.setText("NIL REPORT (Duplicate)");
                    } else {
                        holder.reportType_txt.setText(viewReportData_arr.get(position).getReport_type().toUpperCase() + " REPORT (Duplicate)");
                    }
                }
            }
            else {
                //for normal report
                if (viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("nill")||
                        viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("nil")){
                    holder.reportType_txt.setText("NIL REPORT");
                }else{
                    holder.reportType_txt.setText(viewReportData_arr.get(position).getReport_type().toUpperCase()+" REPORT");
                }
            }

//            if (viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("direct")){
//                holder.reportType_txt.setBackgroundColor(context.getResources().getColor(R.color.direct_report_color));
//            }else if (viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("indirect")){
//                holder.reportType_txt.setBackgroundColor(context.getResources().getColor(R.color.indirect_report_color));
//            }else if (viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("nill")){
//                holder.reportType_txt.setBackgroundColor(context.getResources().getColor(R.color.nil_report_color));
//            }
        }
        else {
            holder.reportType_txt.setVisibility(View.GONE);
        }
        holder.view_more_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (selectType.equalsIgnoreCase("direct_indirect_nil")){

                        Animation animation= AnimationUtils.loadAnimation(context,R.anim.bottom_to_top_anim);
                        IncidentReportViewActivity.bottom_scrollV.startAnimation(animation);

                        IncidentReportViewActivity.bottom_scrollV.setVisibility(View.VISIBLE);

                        try {

                            divisionId=viewReportData_arr.get(position).getDivisionId();
                            total=viewReportData_arr.get(position).getTotal();
                            herd=viewReportData_arr.get(position).getHeard();
                            female=viewReportData_arr.get(position).getFemale();
                            makhna=viewReportData_arr.get(position).getMukhna();
                            calf=viewReportData_arr.get(position).getCalf();
                            remarks=viewReportData_arr.get(position).getRemarks();
                            tusker=viewReportData_arr.get(position).getTusker();
                            lat=viewReportData_arr.get(position).getLatitude();
                            longi=viewReportData_arr.get(position).getLongitudes();
                            imgPath=viewReportData_arr.get(position).getImgAcqlocation();
                            dateStr=viewReportData_arr.get(position).getSighting_date();
                            fromTime=viewReportData_arr.get(position).getSighting_time_from();
                            toTime=viewReportData_arr.get(position).getSighting_time_to();
                            reportType=viewReportData_arr.get(position).getReport_type();
                            division=viewReportData_arr.get(position).getDivision();
                            range=viewReportData_arr.get(position).getRange();
                            section=viewReportData_arr.get(position).getSection();
                            beat=viewReportData_arr.get(position).getBeat();
                            location=viewReportData_arr.get(position).getLocation();
                            reportThrough=viewReportData_arr.get(position).getReport_through();
                            updated_by=viewReportData_arr.get(position).getUpdatedBy();
                            indirecReportType=viewReportData_arr.get(position).getIndirectReportType();
                            duplicateReport=viewReportData_arr.get(position).getDuplicateReport();

                            if (divisionId==null||divisionId.equalsIgnoreCase("null")){
                                divisionId="NA";
                            }
                            if (total==null||total.equalsIgnoreCase("null")){
                                total="NA";
                            }
                            if (herd==null||herd.equalsIgnoreCase("null")){
                                herd="NA";
                            }
                            if (female==null||female.equalsIgnoreCase("null")){
                                female="NA";
                            }
                            if (makhna==null||makhna.equalsIgnoreCase("null")){
                                makhna="NA";
                            }
                            if (calf==null||calf.equalsIgnoreCase("null")){
                                calf="NA";
                            }
                            if (remarks==null||remarks.equalsIgnoreCase("null")){
                                remarks="NA";
                            }
                            if (tusker==null||tusker.equalsIgnoreCase("null")){
                                tusker="NA";
                            }
                            if (lat==null||lat.equalsIgnoreCase("null")){
                                lat="0.0";
                            }
                            if (longi==null||longi.equalsIgnoreCase("null")){
                                longi="0.0";
                            }
                            if (imgPath==null||imgPath.equalsIgnoreCase("null")){
                                imgPath="";
                            }
                            if (reportType==null||reportType.equalsIgnoreCase("null")){
                                reportType="";
                            }
                            if (dateStr==null||dateStr.equalsIgnoreCase("null")){
                                dateStr="NA";
                            }
                            if (fromTime==null||fromTime.equalsIgnoreCase("null")){
                                fromTime="NA";
                            }
                            if (toTime==null||toTime.equalsIgnoreCase("null")){
                                toTime="NA";
                            }
                            if (division==null||division.equalsIgnoreCase("null")){
                                division="NA";
                            }
                            if (range==null||range.equalsIgnoreCase("null")){
                                range="NA";
                            }
                            if (section==null||section.equalsIgnoreCase("null")){
                                section="NA";
                            }
                            if (beat==null||beat.equalsIgnoreCase("null")){
                                beat="NA";
                            }
                            if (location==null||location.equalsIgnoreCase("null")){
                                location="NA";
                            }
                            if (updated_by==null || updated_by.equalsIgnoreCase("null")){
                                updated_by="NA";
                            }
                            if (indirecReportType==null||indirecReportType.equalsIgnoreCase("null")){
                                indirecReportType="";
                            }
                            if (duplicateReport==null||duplicateReport.equalsIgnoreCase("null")){
                                duplicateReport="";
                            }

                            //For first letter capital
                            StringBuilder report_type_sb = new StringBuilder(reportType);
                            report_type_sb.setCharAt(0, Character.toUpperCase(report_type_sb.charAt(0)));

                            update_clickListener.onClickViewMore(position,dateStr,fromTime,toTime,
                                    lat,longi, total,herd, makhna,tusker, female,calf,
                                    division,range,section,beat, location,imgPath, remarks,reportThrough,
                                    report_type_sb.toString(),divisionId,updated_by,indirecReportType,duplicateReport);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else if (selectType.equalsIgnoreCase("view_user_report")){
                        Animation animation= AnimationUtils.loadAnimation(context,R.anim.bottom_to_top_anim);
                        ViewOwnReportActivity.bottom_scrollV.startAnimation(animation);

                        ViewOwnReportActivity.bottom_scrollV.setVisibility(View.VISIBLE);

                        try {

                            divisionId=viewReportData_arr.get(position).getDivisionId();
                            total=viewReportData_arr.get(position).getTotal();
                            herd=viewReportData_arr.get(position).getHeard();
                            female=viewReportData_arr.get(position).getFemale();
                            makhna=viewReportData_arr.get(position).getMukhna();
                            calf=viewReportData_arr.get(position).getCalf();
                            remarks=viewReportData_arr.get(position).getRemarks();
                            tusker=viewReportData_arr.get(position).getTusker();
                            lat=viewReportData_arr.get(position).getLatitude();
                            longi=viewReportData_arr.get(position).getLongitudes();
                            imgPath=viewReportData_arr.get(position).getImgAcqlocation();
                            dateStr=viewReportData_arr.get(position).getSighting_date();
                            fromTime=viewReportData_arr.get(position).getSighting_time_from();
                            toTime=viewReportData_arr.get(position).getSighting_time_to();
                            reportType=viewReportData_arr.get(position).getReport_type();
                            division=viewReportData_arr.get(position).getDivision();
                            range=viewReportData_arr.get(position).getRange();
                            section=viewReportData_arr.get(position).getSection();
                            beat=viewReportData_arr.get(position).getBeat();
                            location=viewReportData_arr.get(position).getLocation();
                            reportThrough=viewReportData_arr.get(position).getReport_through();
                            updated_by=viewReportData_arr.get(position).getUpdatedBy();
                            indirecReportType=viewReportData_arr.get(position).getIndirectReportType();
                            duplicateReport=viewReportData_arr.get(position).getDuplicateReport();

                            if (divisionId==null||divisionId.equalsIgnoreCase("null")){
                                divisionId="NA";
                            }
                            if (total==null||total.equalsIgnoreCase("null")){
                                total="NA";
                            }
                            if (herd==null||herd.equalsIgnoreCase("null")){
                                herd="NA";
                            }
                            if (female==null||female.equalsIgnoreCase("null")){
                                female="NA";
                            }
                            if (makhna==null||makhna.equalsIgnoreCase("null")){
                                makhna="NA";
                            }
                            if (calf==null||calf.equalsIgnoreCase("null")){
                                calf="NA";
                            }
                            if (remarks==null||remarks.equalsIgnoreCase("null")){
                                remarks="NA";
                            }
                            if (tusker==null||tusker.equalsIgnoreCase("null")){
                                tusker="NA";
                            }
                            if (lat==null||lat.equalsIgnoreCase("null")){
                                lat="0.0";
                            }
                            if (longi==null||longi.equalsIgnoreCase("null")){
                                longi="0.0";
                            }
                            if (imgPath==null||imgPath.equalsIgnoreCase("null")){
                                imgPath="";
                            }
                            if (reportType==null||reportType.equalsIgnoreCase("null")){
                                reportType="";
                            }
                            if (dateStr==null||dateStr.equalsIgnoreCase("null")){
                                dateStr="NA";
                            }
                            if (fromTime==null||fromTime.equalsIgnoreCase("null")){
                                fromTime="NA";
                            }
                            if (toTime==null||toTime.equalsIgnoreCase("null")){
                                toTime="NA";
                            }
                            if (division==null||division.equalsIgnoreCase("null")){
                                division="NA";
                            }
                            if (range==null||range.equalsIgnoreCase("null")){
                                range="NA";
                            }
                            if (section==null||section.equalsIgnoreCase("null")){
                                section="NA";
                            }
                            if (beat==null||beat.equalsIgnoreCase("null")){
                                beat="NA";
                            }
                            if (location==null||location.equalsIgnoreCase("null")){
                                location="NA";
                            }
                            if (updated_by==null || updated_by.equalsIgnoreCase("null")){
                                updated_by="NA";
                            }
                            if (indirecReportType==null||indirecReportType.equalsIgnoreCase("null")){
                                indirecReportType="";
                            }
                            if (duplicateReport==null||duplicateReport.equalsIgnoreCase("null")){
                                duplicateReport="";
                            }
                            //For first letter capital
                            StringBuilder report_type_sb = new StringBuilder(reportType);
                            report_type_sb.setCharAt(0, Character.toUpperCase(report_type_sb.charAt(0)));

                            update_clickListener.onClickViewMore(position,dateStr,fromTime,toTime,
                                    lat,longi, total,herd, makhna,tusker, female,calf,
                                    division,range,section,beat, location,imgPath, remarks,reportThrough,
                                    report_type_sb.toString(),divisionId,updated_by,indirecReportType,duplicateReport);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    else {
                        //for normal view report
                        Animation animation= AnimationUtils.loadAnimation(context,R.anim.bottom_to_top_anim);
                        ViewReportFragment.bottom_scrollV.startAnimation(animation);

                        ViewReportFragment.bottom_scrollV.setVisibility(View.VISIBLE);

                        try {

                            divisionId=viewReportData_arr.get(position).getDivisionId();
                            total=viewReportData_arr.get(position).getTotal();
                            herd=viewReportData_arr.get(position).getHeard();
                            female=viewReportData_arr.get(position).getFemale();
                            makhna=viewReportData_arr.get(position).getMukhna();
                            calf=viewReportData_arr.get(position).getCalf();
                            remarks=viewReportData_arr.get(position).getRemarks();
                            tusker=viewReportData_arr.get(position).getTusker();
                            lat=viewReportData_arr.get(position).getLatitude();
                            longi=viewReportData_arr.get(position).getLongitudes();
                            imgPath=viewReportData_arr.get(position).getImgAcqlocation();
                            dateStr=viewReportData_arr.get(position).getSighting_date();
                            fromTime=viewReportData_arr.get(position).getSighting_time_from();
                            toTime=viewReportData_arr.get(position).getSighting_time_to();
                            reportType=viewReportData_arr.get(position).getReport_type();
                            division=viewReportData_arr.get(position).getDivision();
                            range=viewReportData_arr.get(position).getRange();
                            section=viewReportData_arr.get(position).getSection();
                            beat=viewReportData_arr.get(position).getBeat();
                            location=viewReportData_arr.get(position).getLocation();
                            reportThrough=viewReportData_arr.get(position).getReport_through();
                            updated_by=viewReportData_arr.get(position).getUpdatedBy();
                            indirecReportType=viewReportData_arr.get(position).getIndirectReportType();
                            duplicateReport=viewReportData_arr.get(position).getDuplicateReport();



                            if (divisionId==null||divisionId.equalsIgnoreCase("null")){
                                divisionId="NA";
                            }
                            if (total==null||total.equalsIgnoreCase("null")){
                                total="NA";
                            }
                            if (herd==null||herd.equalsIgnoreCase("null")){
                                herd="NA";
                            }
                            if (female==null||female.equalsIgnoreCase("null")){
                                female="NA";
                            }
                            if (makhna==null||makhna.equalsIgnoreCase("null")){
                                makhna="NA";
                            }
                            if (calf==null||calf.equalsIgnoreCase("null")){
                                calf="NA";
                            }
                            if (remarks==null||remarks.equalsIgnoreCase("null")){
                                remarks="NA";
                            }
                            if (tusker==null||tusker.equalsIgnoreCase("null")){
                                tusker="NA";
                            }
                            if (lat==null||lat.equalsIgnoreCase("null")){
                                lat="0.0";
                            }
                            if (longi==null||longi.equalsIgnoreCase("null")){
                                longi="0.0";
                            }
                            if (imgPath==null||imgPath.equalsIgnoreCase("null")){
                                imgPath="";
                            }
                            if (reportType==null||reportType.equalsIgnoreCase("null")){
                                reportType="";
                            }
                            if (dateStr==null||dateStr.equalsIgnoreCase("null")){
                                dateStr="NA";
                            }
                            if (fromTime==null||fromTime.equalsIgnoreCase("null")){
                                fromTime="NA";
                            }
                            if (toTime==null||toTime.equalsIgnoreCase("null")){
                                toTime="NA";
                            }
                            if (division==null||division.equalsIgnoreCase("null")){
                                division="NA";
                            }
                            if (range==null||range.equalsIgnoreCase("null")){
                                range="NA";
                            }
                            if (section==null||section.equalsIgnoreCase("null")){
                                section="NA";
                            }
                            if (beat==null||beat.equalsIgnoreCase("null")){
                                beat="NA";
                            }
                            if (location==null||location.equalsIgnoreCase("null")){
                                location="NA";
                            }
                            if (updated_by==null || updated_by.equalsIgnoreCase("null")){
                                updated_by="NA";
                            }
                            if (indirecReportType==null||indirecReportType.equalsIgnoreCase("null")){
                                indirecReportType="";
                            }
                            if (duplicateReport==null||duplicateReport.equalsIgnoreCase("null")){
                                duplicateReport="";
                            }
                            //For first letter capital
                            StringBuilder report_type_sb = new StringBuilder(reportType);
                            report_type_sb.setCharAt(0, Character.toUpperCase(report_type_sb.charAt(0)));


                            update_clickListener.onClickViewMore(position,dateStr,fromTime,toTime,
                                    lat,longi, total,herd, makhna,tusker, female,calf,
                                    division,range,section,beat, location,imgPath, remarks,reportThrough,
                                    report_type_sb.toString(),divisionId,updated_by,indirecReportType,"");

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return viewReportData_arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView location_txt,dom_txt,division_txt,total_no,heard,tusker,male,female,calf,view_more_txt,reportType_txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location_txt=itemView.findViewById(R.id.location_txt);
            dom_txt=itemView.findViewById(R.id.dom_txt);
            division_txt=itemView.findViewById(R.id.division_txt);
            total_no=itemView.findViewById(R.id.total_no);
            heard=itemView.findViewById(R.id.heard);
            tusker=itemView.findViewById(R.id.tusker);
            male=itemView.findViewById(R.id.male);
            female=itemView.findViewById(R.id.female);
            calf=itemView.findViewById(R.id.calf);
            view_more_txt=itemView.findViewById(R.id.view_more_txt);
            reportType_txt=itemView.findViewById(R.id.reportType_txt);

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

    public String showDecimalFormat(String value){
        DecimalFormat df = new DecimalFormat("#.###");
        String altitude_=df.format(Double.parseDouble(value));
        return altitude_;
    }

    public void callImageViewDialog(Context context,String imgpath){
        try {

            Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.image_view_dialog);

            ImageView report_image,close_img;
            report_image=dialog.findViewById(R.id.report_image);
            close_img=dialog.findViewById(R.id.close_img);

            Glide.with(context)
                    .load(imgpath)
                    .into(report_image);


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                }
            });

            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
