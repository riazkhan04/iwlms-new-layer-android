package com.orsac.android.pccfwildlife.Adapters;

import android.app.Dialog;
import android.content.Context;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.orsac.android.pccfwildlife.Activities.IncidentReportViewActivity;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.ElephantDeathIncidentDataModel;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.IncidentReportDataModel;
import com.orsac.android.pccfwildlife.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ElephantDeathReportViewAdapter extends RecyclerView.Adapter<ElephantDeathReportViewAdapter.ViewHolder>{

    Context context;
    ArrayList<ElephantDeathIncidentDataModel> viewElephantDeathData_arr;
    ArrayList<IncidentReportDataModel> viewIncidentData_arr;
    ElephantDeathItem_clickListener update_clickListener;
    String date_formatted="", mapTypeName="";
    GoogleMap map;
    JSONObject geoJsonResponse;
    GeoJsonLayer divisionLayer=null;
    LatLng latLng=null,layer_latlng=null;
    String selectType="";
    String divisionId="",total="",herd="",female="",makhna="",calf="",tusker="",remarks="",lat="",longi="",imgPath="",
            dateStr="",fromTime="",toTime="",division="",range="",section="",beat="",location="",
            reportType="",updatedBy="",crop="",human="",injured="",kill="",houseDamage="",remark="",deathReason="";

    public interface ElephantDeathItem_clickListener{
//        void on_CardViewClick(int position, String from_time, String to_time, Bitmap bitmap);

        void onElephantClickViewMore(int position,String date,String latitude,String longitude,
                             String division,String range,String section,String beat,String location,String report_img,
                             String reportType,String divisionId,String updatedBy,String makhna,String female,
                             String calf,String crop,String human,String injured,String kill,
                             String houseDamge,String deathReason);

    }


    public ElephantDeathReportViewAdapter(Context context, ArrayList<ElephantDeathIncidentDataModel> viewElephantDeathData_arr,
                                          ElephantDeathItem_clickListener update_clickListener,String type) {
        this.context = context;
        this.viewElephantDeathData_arr = viewElephantDeathData_arr;
        this.update_clickListener = update_clickListener;
        this.selectType = type;
    }
    public ElephantDeathReportViewAdapter(Context context, ArrayList<IncidentReportDataModel> viewIncidentData_arr,
                                          String type,ElephantDeathItem_clickListener update_clickListener) {
        this.context = context;
        this.viewIncidentData_arr = viewIncidentData_arr;
        this.selectType = type;
        this.update_clickListener = update_clickListener;

    }

    @NonNull
    @Override
    public ElephantDeathReportViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.incident_item_layout,parent,false);
//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.report_view_items,parent,false);

        return new ElephantDeathReportViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElephantDeathReportViewAdapter.ViewHolder holder, int position) {

        try {

            if (selectType.equalsIgnoreCase("elephant_death")){

                //For date
                if (viewElephantDeathData_arr.get(position).getIncidentReportDate()==null ||
                        viewElephantDeathData_arr.get(position).getIncidentReportDate().equalsIgnoreCase("null") ||
                        viewElephantDeathData_arr.get(position).getIncidentReportDate().equalsIgnoreCase("")){
                    holder.dom_txt.setText("NA");
                }else {
                    holder.dom_txt.setText(convertDate(viewElephantDeathData_arr.get(position).getIncidentReportDate(),
                            "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy"
                    ));
                }

                //for division
                if (viewElephantDeathData_arr.get(position).getDivision()==null ||
                        viewElephantDeathData_arr.get(position).getDivision().equalsIgnoreCase("null") ||
                        viewElephantDeathData_arr.get(position).getDivision().equalsIgnoreCase("")){
                    holder.division_txt.setText("NA");
                }else {
                    holder.division_txt.setText(viewElephantDeathData_arr.get(position).getDivision());
                }

                //circle
                if (viewElephantDeathData_arr.get(position).getCircle()==null ||
                        viewElephantDeathData_arr.get(position).getCircle().equalsIgnoreCase("null") ||
                        viewElephantDeathData_arr.get(position).getCircle().equalsIgnoreCase("")){
                    holder.circle_txt.setText("NA");
                }else {
                    holder.circle_txt.setText(viewElephantDeathData_arr.get(position).getCircle());
                }

                //range
                if (viewElephantDeathData_arr.get(position).getRange()==null ||
                        viewElephantDeathData_arr.get(position).getRange().equalsIgnoreCase("null") ||
                        viewElephantDeathData_arr.get(position).getRange().equalsIgnoreCase("")){
                    holder.range_txt.setText("NA");
                }else {
                    holder.range_txt.setText(viewElephantDeathData_arr.get(position).getRange());
                }

                //section
                if (viewElephantDeathData_arr.get(position).getSection()==null ||
                        viewElephantDeathData_arr.get(position).getSection().equalsIgnoreCase("null") ||
                        viewElephantDeathData_arr.get(position).getSection().equalsIgnoreCase("")){
                    holder.section_txt.setText("NA");
                }else {
                    holder.section_txt.setText(viewElephantDeathData_arr.get(position).getSection());
                }
                //beat
                if (viewElephantDeathData_arr.get(position).getBeat()==null ||
                        viewElephantDeathData_arr.get(position).getBeat().equalsIgnoreCase("null") ||
                        viewElephantDeathData_arr.get(position).getBeat().equalsIgnoreCase("")){
                    holder.beat_txt.setText("NA");
                }else {
                    holder.beat_txt.setText(viewElephantDeathData_arr.get(position).getBeat());
                }

                holder.reportType_txt.setText("Elephant Death".toUpperCase());
//                holder.reportType_txt.setBackgroundColor(context.getResources().getColor(R.color.elephant_death_color));


            }else {
            //for incident report

                //For date
                if (viewIncidentData_arr.get(position).getIncidentReportDate()==null ||
                        viewIncidentData_arr.get(position).getIncidentReportDate().equalsIgnoreCase("null") ||
                        viewIncidentData_arr.get(position).getIncidentReportDate().equalsIgnoreCase("")){
                    holder.dom_txt.setText("NA");
                }else {
                    holder.dom_txt.setText(convertDate(viewIncidentData_arr.get(position).getIncidentReportDate(),
                            "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy"
                    ));
                }

                //for division
                if (viewIncidentData_arr.get(position).getDivision()==null ||
                        viewIncidentData_arr.get(position).getDivision().equalsIgnoreCase("null") ||
                        viewIncidentData_arr.get(position).getDivision().equalsIgnoreCase("")){
                    holder.division_txt.setText("NA");
                }else {
                    holder.division_txt.setText(viewIncidentData_arr.get(position).getDivision());
                }

                //circle
                if (viewIncidentData_arr.get(position).getCircle()==null ||
                        viewIncidentData_arr.get(position).getCircle().equalsIgnoreCase("null") ||
                        viewIncidentData_arr.get(position).getCircle().equalsIgnoreCase("")){
                    holder.circle_txt.setText("NA");
                }else {
                    holder.circle_txt.setText(viewIncidentData_arr.get(position).getCircle());
                }

                //range
                if (viewIncidentData_arr.get(position).getRange()==null ||
                        viewIncidentData_arr.get(position).getRange().equalsIgnoreCase("null") ||
                        viewIncidentData_arr.get(position).getRange().equalsIgnoreCase("")){
                    holder.range_txt.setText("NA");
                }else {
                    holder.range_txt.setText(viewIncidentData_arr.get(position).getRange());
                }

                //section
                if (viewIncidentData_arr.get(position).getSection()==null ||
                        viewIncidentData_arr.get(position).getSection().equalsIgnoreCase("null") ||
                        viewIncidentData_arr.get(position).getSection().equalsIgnoreCase("")){
                    holder.section_txt.setText("NA");
                }else {
                    holder.section_txt.setText(viewIncidentData_arr.get(position).getSection());
                }
                //beat
                if (viewIncidentData_arr.get(position).getBeat()==null ||
                        viewIncidentData_arr.get(position).getBeat().equalsIgnoreCase("null") ||
                        viewIncidentData_arr.get(position).getBeat().equalsIgnoreCase("")){
                    holder.beat_txt.setText("NA");
                }else {
                    holder.beat_txt.setText(viewIncidentData_arr.get(position).getBeat());
                }

                if (viewIncidentData_arr.get(position).getIncidentReportType()!=null) {
                    holder.reportType_txt.setText(viewIncidentData_arr.get(position).getIncidentReportType().toUpperCase());
//                    holder.reportType_txt.setBackgroundColor(context.getResources().getColor(R.color.incident_color));
                }
                else {
                    holder.reportType_txt.setVisibility(View.GONE);
                }



            }



        holder.view_more_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (selectType.equalsIgnoreCase("elephant_death")){

                        Animation animation= AnimationUtils.loadAnimation(context,R.anim.bottom_to_top_anim);
                        IncidentReportViewActivity.bottom_scrollV.startAnimation(animation);

                        IncidentReportViewActivity.bottom_scrollV.setVisibility(View.VISIBLE);

                        try {

                            lat=viewElephantDeathData_arr.get(position).getLatitude();
                            longi=viewElephantDeathData_arr.get(position).getLongitude();
                            imgPath=viewElephantDeathData_arr.get(position).getIncidentPhoto();
                            dateStr=viewElephantDeathData_arr.get(position).getIncidentReportDate();
                            division=viewElephantDeathData_arr.get(position).getDivision();
                            range=viewElephantDeathData_arr.get(position).getRange();
                            section=viewElephantDeathData_arr.get(position).getSection();
                            beat=viewElephantDeathData_arr.get(position).getBeat();
                            location=viewElephantDeathData_arr.get(position).getLocation();
                            divisionId=viewElephantDeathData_arr.get(position).getDivisionId();
                            updatedBy=viewElephantDeathData_arr.get(position).getUpdatedBy();
                            makhna=viewElephantDeathData_arr.get(position).getMakhna();
                            female=viewElephantDeathData_arr.get(position).getFemale();
                            calf=viewElephantDeathData_arr.get(position).getCalf();
                            deathReason=viewElephantDeathData_arr.get(position).getDeathReason();

                            if (divisionId==null||divisionId.equalsIgnoreCase("null")||divisionId.equalsIgnoreCase("")){
                                divisionId="NA";
                            }
                            if (total==null||total.equalsIgnoreCase("null")||total.equalsIgnoreCase("")){
                                total="0";
                            }
                            if (herd==null||herd.equalsIgnoreCase("null")||herd.equalsIgnoreCase("")){
                                herd="0";
                            }
                            if (female==null||female.equalsIgnoreCase("null")||female.equalsIgnoreCase("")){
                                female="0";
                            }
                            if (makhna==null||makhna.equalsIgnoreCase("null")||makhna.equalsIgnoreCase("")){
                                makhna="0";
                            }
                            if (calf==null||calf.equalsIgnoreCase("null")||calf.equalsIgnoreCase("")){
                                calf="0";
                            }
                            if (remarks==null||remarks.equalsIgnoreCase("null")||remarks.equalsIgnoreCase("")){
                                remarks="NA";
                            }
                            if (tusker==null||tusker.equalsIgnoreCase("null")||tusker.equalsIgnoreCase("")){
                                tusker="0";
                            }
                            if (lat==null||lat.equalsIgnoreCase("null")||lat.equalsIgnoreCase("")){
                                lat="0.0";
                            }
                            if (longi==null||longi.equalsIgnoreCase("null")||longi.equalsIgnoreCase("")){
                                longi="0.0";
                            }
                            if (imgPath==null||imgPath.equalsIgnoreCase("null")||imgPath.equalsIgnoreCase("")){
                                imgPath="";
                            }
                            if (reportType==null||reportType.equalsIgnoreCase("null")||reportType.equalsIgnoreCase("")){
                                reportType="";
                            }
                            if (dateStr==null||dateStr.equalsIgnoreCase("null")||dateStr.equalsIgnoreCase("")){
                                dateStr="NA";
                            }
                            if (fromTime==null||fromTime.equalsIgnoreCase("null")||fromTime.equalsIgnoreCase("")){
                                fromTime="NA";
                            }
                            if (toTime==null||toTime.equalsIgnoreCase("null")||toTime.equalsIgnoreCase("")){
                                toTime="NA";
                            }
                            if (division==null||division.equalsIgnoreCase("null")||division.equalsIgnoreCase("")){
                                division="NA";
                            }
                            if (range==null||range.equalsIgnoreCase("null")||range.equalsIgnoreCase("")){
                                range="NA";
                            }
                            if (section==null||section.equalsIgnoreCase("null")||section.equalsIgnoreCase("")){
                                section="NA";
                            }
                            if (beat==null||beat.equalsIgnoreCase("null")||beat.equalsIgnoreCase("")){
                                beat="NA";
                            }
                            if (location==null||location.equalsIgnoreCase("null")||location.equalsIgnoreCase("")){
                                location="NA";
                            }
                            if (deathReason==null||deathReason.equalsIgnoreCase("null")||deathReason.equalsIgnoreCase("")){
                                deathReason="NA";
                            }

                            reportType="Elephant Death Report";
                            update_clickListener.onElephantClickViewMore(position,dateStr,
                                    lat,longi,division,range,section,beat, location,imgPath,reportType,
                                    divisionId,updatedBy,makhna,female,calf,crop,human,injured,kill,
                                    houseDamage,deathReason);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        //for normal view report
                        Animation animation= AnimationUtils.loadAnimation(context,R.anim.bottom_to_top_anim);
                        IncidentReportViewActivity.bottom_scrollV.startAnimation(animation);

                        IncidentReportViewActivity.bottom_scrollV.setVisibility(View.VISIBLE);

                        try {

                            lat=viewIncidentData_arr.get(position).getLatitude();
                            longi=viewIncidentData_arr.get(position).getLongitude();
                            imgPath=viewIncidentData_arr.get(position).getImgPath();
                            dateStr=viewIncidentData_arr.get(position).getIncidentReportDate();
                            division=viewIncidentData_arr.get(position).getDivision();
                            range=viewIncidentData_arr.get(position).getRange();
                            section=viewIncidentData_arr.get(position).getSection();
                            beat=viewIncidentData_arr.get(position).getBeat();
                            divisionId=viewIncidentData_arr.get(position).getDivisionId();
                            updatedBy=viewIncidentData_arr.get(position).getUpdatedBy();
                            reportType=viewIncidentData_arr.get(position).getIncidentReportType();
                            makhna=viewIncidentData_arr.get(position).getMakhna();
                            female=viewIncidentData_arr.get(position).getFemale();
                            calf=viewIncidentData_arr.get(position).getCalf();
                            crop=viewIncidentData_arr.get(position).getCrop();
                            human=viewIncidentData_arr.get(position).getHuman();
                            injured=viewIncidentData_arr.get(position).getInjured();
                            kill=viewIncidentData_arr.get(position).getKill();
                            houseDamage=viewIncidentData_arr.get(position).getHouseDamage();
                            location=viewIncidentData_arr.get(position).getIncidentRemarks();

                            if (divisionId==null||divisionId.equalsIgnoreCase("null")||divisionId.equalsIgnoreCase("")){
                                divisionId="NA";
                            }
                            if (total==null||total.equalsIgnoreCase("null")||total.equalsIgnoreCase("")){
                                total="0";
                            }
                            if (herd==null||herd.equalsIgnoreCase("null")||herd.equalsIgnoreCase("")){
                                herd="0";
                            }
                            if (female==null||female.equalsIgnoreCase("null")||female.equalsIgnoreCase("")){
                                female="0";
                            }
                            if (makhna==null||makhna.equalsIgnoreCase("null")||makhna.equalsIgnoreCase("")){
                                makhna="0";
                            }
                            if (calf==null||calf.equalsIgnoreCase("null")||calf.equalsIgnoreCase("")){
                                calf="0";
                            }
                            if (remarks==null||remarks.equalsIgnoreCase("null")||remarks.equalsIgnoreCase("")){
                                remarks="NA";
                            }
                            if (tusker==null||tusker.equalsIgnoreCase("null")||tusker.equalsIgnoreCase("")){
                                tusker="0";
                            }
                            if (lat==null||lat.equalsIgnoreCase("null")||lat.equalsIgnoreCase("")){
                                lat="0.0";
                            }
                            if (longi==null||longi.equalsIgnoreCase("null")||longi.equalsIgnoreCase("")){
                                longi="0.0";
                            }
                            if (imgPath==null||imgPath.equalsIgnoreCase("null")||imgPath.equalsIgnoreCase("")){
                                imgPath="";
                            }
                            if (reportType==null||reportType.equalsIgnoreCase("null")||reportType.equalsIgnoreCase("")){
                                reportType="";
                            }
                            if (dateStr==null||dateStr.equalsIgnoreCase("null")||dateStr.equalsIgnoreCase("")){
                                dateStr="NA";
                            }
                            if (fromTime==null||fromTime.equalsIgnoreCase("null")||fromTime.equalsIgnoreCase("")){
                                fromTime="NA";
                            }
                            if (toTime==null||toTime.equalsIgnoreCase("null")||toTime.equalsIgnoreCase("")){
                                toTime="NA";
                            }
                            if (division==null||division.equalsIgnoreCase("null")||division.equalsIgnoreCase("")){
                                division="NA";
                            }
                            if (range==null||range.equalsIgnoreCase("null")||range.equalsIgnoreCase("")){
                                range="NA";
                            }
                            if (section==null||section.equalsIgnoreCase("null")||section.equalsIgnoreCase("")){
                                section="NA";
                            }
                            if (beat==null||beat.equalsIgnoreCase("null")||beat.equalsIgnoreCase("")){
                                beat="NA";
                            }
                            if (location==null||location.equalsIgnoreCase("null")||location.equalsIgnoreCase("")){
                                location="NA";
                            }

//                            reportType="Incident Report";
                            update_clickListener.onElephantClickViewMore(position,dateStr,
                                    lat,longi,division,range,section,beat, location,imgPath,reportType,
                                    divisionId,updatedBy,makhna,female,calf,crop,human,injured,kill,
                                    houseDamage,deathReason);

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
        int size=0;
        if (selectType.equalsIgnoreCase("elephant_death")){
            size=viewElephantDeathData_arr.size();
        }else {
            size=viewIncidentData_arr.size();
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView location_txt,dom_txt,division_txt,circle_txt,range_txt,section_txt,beat_txt,view_more_txt,reportType_txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location_txt=itemView.findViewById(R.id.location_txt);
            dom_txt=itemView.findViewById(R.id.dom_txt);
            circle_txt=itemView.findViewById(R.id.circle_txt);
            division_txt=itemView.findViewById(R.id.division_txt);
            range_txt=itemView.findViewById(R.id.range_txt);
            section_txt=itemView.findViewById(R.id.section_txt);
            beat_txt=itemView.findViewById(R.id.beat_txt);
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

    public void callMapDialog(Context context){
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

//            for (int i=0;i<latLngs_arr.size();i++){
//
//                LatLng latLng=new LatLng(Double.parseDouble(latLngs_arr.get(i).getLatitude()),
//                        Double.parseDouble(latLngs_arr.get(i).getLongitude()));
//                map.addMarker(new
//                        MarkerOptions().position(latLng)
//                        .title(""+latLngs_arr.get(i))
////                    .title("Elephant Report")
//                        .flat(true)
//                        .rotation(5))
//                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker));
//            }

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
