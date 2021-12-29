package com.orsac.android.pccfwildlife.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.orsac.android.pccfwildlife.Activities.ViewReportMapPointActivity;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ViewReportRecyclerV_Adapter extends RecyclerView.Adapter<ViewReportRecyclerV_Adapter.ViewHolder> {
   Context context;
   ArrayList<ViewReportItemData_obj> viewReportData_arr;
   Update_clickListener update_clickListener;
   String date_formatted="", mapTypeName="";
   GoogleMap map;
   JSONObject geoJsonResponse;
   GeoJsonLayer divisionLayer=null;
    LatLng latLng=null,layer_latlng=null;

   public interface Update_clickListener{
      void on_UpdateClick(int position,String report_id,String report,JSONObject jsonObject);

      void on_CardViewClick(int position,String from_time,String to_time,Bitmap bitmap);

      void onMapPointClick(int position,double latitude,double longitude);
   }

    public ViewReportRecyclerV_Adapter(Context context, ArrayList<ViewReportItemData_obj> viewReportData_arr, Update_clickListener update_clickListener) {
        this.context = context;
        this.viewReportData_arr = viewReportData_arr;
        this.update_clickListener = update_clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_report_item_new,parent,false);
//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.report_view_items,parent,false);

        return new ViewReportRecyclerV_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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

//        //location
        if (viewReportData_arr.get(position).getLocation()==null ||
                viewReportData_arr.get(position).getLocation().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getLocation().equalsIgnoreCase("")){
            holder.location_txt.setText("NA");
        }else {
            holder.location_txt.setText(viewReportData_arr.get(position).getLocation());
        }

        //time from
        if (viewReportData_arr.get(position).getSighting_time_from()==null ||
                viewReportData_arr.get(position).getSighting_time_from().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getSighting_time_from().equalsIgnoreCase("")){
            holder.from_time_txt.setText("NA");
        }else {
            holder.from_time_txt.setText(convertDate(viewReportData_arr.get(position).getSighting_time_from(),
                    "yyyy-MM-dd HH:mm:ss.SSS","HH:mm"
            ));
//            holder.from_time_txt.setText(viewReportData_arr.get(position).getSighting_time_from());
        }

        //to time
        if (viewReportData_arr.get(position).getSighting_time_to()==null ||
                viewReportData_arr.get(position).getSighting_time_to().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getSighting_time_to().equalsIgnoreCase("")){
            holder.to_time_txt.setText("NA");
        }else {
            holder.to_time_txt.setText(convertDate(viewReportData_arr.get(position).getSighting_time_to(),
                    "yyyy-MM-dd HH:mm:ss.SSS","HH:mm"
            ));
//            holder.to_time_txt.setText(viewReportData_arr.get(position).getSighting_time_to());
        }

        //for division
        if (viewReportData_arr.get(position).getDivision()==null ||
                viewReportData_arr.get(position).getDivision().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getDivision().equalsIgnoreCase("")){
            holder.division_txt.setText("NA");
        }else {
            holder.division_txt.setText(viewReportData_arr.get(position).getDivision());
        }

        //For range
        if (viewReportData_arr.get(position).getRange()==null ||
                viewReportData_arr.get(position).getRange().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getRange().equalsIgnoreCase("")){
            holder.range_txt.setText("NA");
        }else {
            holder.range_txt.setText(viewReportData_arr.get(position).getRange());
        }

        //For Section
        if (viewReportData_arr.get(position).getSection()==null ||
                viewReportData_arr.get(position).getSection().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getSection().equalsIgnoreCase("")){
            holder.section_txt.setText("NA");
        }else {
            holder.section_txt.setText(viewReportData_arr.get(position).getSection());
        }

        //For Beat
        if (viewReportData_arr.get(position).getBeat()==null ||
                viewReportData_arr.get(position).getBeat().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getBeat().equalsIgnoreCase("")){
            holder.beat_txt.setText("NA");
        }else {
            holder.beat_txt.setText(viewReportData_arr.get(position).getBeat());
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

//        //For report type
        if (viewReportData_arr.get(position).getReport_type()==null ||
                viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("null")||
                viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("")
        ){
            holder.report_type.setText("NA");
        }else {
            holder.report_type.setText(viewReportData_arr.get(position).getReport_type());
        }

        if (viewReportData_arr.get(position).getRemarks()==null ||
                viewReportData_arr.get(position).getRemarks().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getRemarks().equalsIgnoreCase("")
        ){
            holder.remark.setText("NA");
        }else {
            holder.remark.setText(viewReportData_arr.get(position).getRemarks());
        }

        if (viewReportData_arr.get(position).getReport_type()==null ||
                viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getReport_type().equalsIgnoreCase("")
        ){
            holder.report.setText("NA");
        }else {
            holder.report.setText((viewReportData_arr.get(position).getReport_type() +" Report").toUpperCase());
        }

        if (viewReportData_arr.get(position).getAccuracy()==null ||
                viewReportData_arr.get(position).getAccuracy().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getAccuracy().equalsIgnoreCase("")
        ){
            holder.accuracy_txt.setText("NA");
        }else {
            holder.accuracy_txt.setText(""+viewReportData_arr.get(position).getLatitude());
        }

        if (viewReportData_arr.get(position).getAltitude()==null ||
                viewReportData_arr.get(position).getAltitude().equalsIgnoreCase("null") ||
                viewReportData_arr.get(position).getAltitude().equalsIgnoreCase("")
        ){
            holder.report_through.setText("NA");
        }else {
            holder.report_through.setText(viewReportData_arr.get(position).getLongitudes());
        }



        if (viewReportData_arr.get(position).getReport_through()!=null && viewReportData_arr.get(position).getReport_through().equalsIgnoreCase("mobile")){
            holder.report_through_img.setImageDrawable(context.getResources().getDrawable(R.drawable.phone_img));
        }
        else {
            holder.report_through_img.setImageDrawable(context.getResources().getDrawable(R.drawable.web_img));
        }

        //Show report_img
        holder.report_img.setVisibility(View.VISIBLE);

        if (viewReportData_arr.get(position).getImgAcqlocation()==null){
            Glide.with(context)
                    .load(R.drawable.logo)
                    .into(holder.report_img);

        }else {
            Glide.with(context)
                    .load("http://164.164.122.69:8080/wildlife/api/v1/uploadController/downloadFile?location="+viewReportData_arr.get(position).getImgAcqlocation())
                    .into(holder.report_img);
        }


        holder.update_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("dom",viewReportData_arr.get(position).getSighting_date());
                    jsonObject.put("location_nm",viewReportData_arr.get(position).getLocation());
                    jsonObject.put("from_time",viewReportData_arr.get(position).getSighting_time_from());
                    jsonObject.put("to_time",viewReportData_arr.get(position).getSighting_time_to());
                    jsonObject.put("division",viewReportData_arr.get(position).getDivision());
                    jsonObject.put("range",viewReportData_arr.get(position).getRange());
                    jsonObject.put("section",viewReportData_arr.get(position).getSection());
                    jsonObject.put("beat",viewReportData_arr.get(position).getBeat());
                    jsonObject.put("total_no",viewReportData_arr.get(position).getTotal());
                    jsonObject.put("herd",viewReportData_arr.get(position).getHeard());
                    jsonObject.put("tusker",viewReportData_arr.get(position).getTusker());
                    jsonObject.put("male",viewReportData_arr.get(position).getMukhna());
                    jsonObject.put("female",viewReportData_arr.get(position).getFemale());
                    jsonObject.put("calf",viewReportData_arr.get(position).getCalf());
                    jsonObject.put("lat_deg",viewReportData_arr.get(position).getLat_degree());
                    jsonObject.put("lat_min",viewReportData_arr.get(position).getLat_minute());
                    jsonObject.put("lat_sec",viewReportData_arr.get(position).getLat_seconds());
                    jsonObject.put("lng_deg",viewReportData_arr.get(position).getLong_degree());
                    jsonObject.put("lng_min",viewReportData_arr.get(position).getLong_minute());
                    jsonObject.put("lng_sec",viewReportData_arr.get(position).getLong_seconds());
                    jsonObject.put("report",viewReportData_arr.get(position).getReport());
                    jsonObject.put("report_through",viewReportData_arr.get(position).getReport_through());
                    jsonObject.put("report_type",viewReportData_arr.get(position).getReport_type());
                    jsonObject.put("report_id",viewReportData_arr.get(position).getReport_id());

                }catch (JSONException e){

                }
                update_clickListener.on_UpdateClick(position,viewReportData_arr.get(position).getReport_id(),
                        viewReportData_arr.get(position).getReport(),jsonObject);
            }
        });

        holder.report_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap bitmap=PermissionUtils.createImageScreenShotForView(holder.reportCV,context);
//                Toast.makeText(context, ""+bitmap, Toast.LENGTH_SHORT).show();
                call_PdfDialog(context,"Do you want to share PDF ?","Generate and Share PDF",position,bitmap);

            }
        });

        holder.locate_map_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (viewReportData_arr.get(position).report_type.equalsIgnoreCase("nill")){
                        Toast.makeText(context, "Not a valid point to plot in map !", Toast.LENGTH_SHORT).show();
                    }else {
                        if (viewReportData_arr.get(position).getLatitude().equalsIgnoreCase("0.0")){

                            Toast.makeText(context, "Not a valid point to plot in map !", Toast.LENGTH_SHORT).show();

                        }else {
                            Intent intent=new Intent(context, ViewReportMapPointActivity.class);
                            intent.putExtra("lat",viewReportData_arr.get(position).getLatitude());
                            intent.putExtra("lng",viewReportData_arr.get(position).getLongitudes());
                            intent.putExtra("divisionId",viewReportData_arr.get(position).getDivisionId());
                            intent.putExtra("divisionNm",viewReportData_arr.get(position).getDivision());
                            intent.putExtra("rangeNm",viewReportData_arr.get(position).getRange());
                            intent.putExtra("total",viewReportData_arr.get(position).getTotal());
                            intent.putExtra("herd",viewReportData_arr.get(position).getHeard());
                            intent.putExtra("date",viewReportData_arr.get(position).getSighting_date());
                            intent.putExtra("image",viewReportData_arr.get(position).getImgAcqlocation());
                            context.startActivity(intent);
                        }

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        holder.report_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                callImageViewDialog(context,viewReportData_arr.get(position).getImgAcqlocation());
            }
        });

    }

    @Override
    public int getItemCount() {
        return viewReportData_arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView location_txt,dom_txt,from_time_txt,to_time_txt,report_type,division_txt,range_txt,section_txt,
                beat_txt,total_no,heard,tusker,male,female,calf,report_through,remark,report,accuracy_txt;
        LinearLayout reportingType_layout;
        ImageView update_img,report_img,report_through_img,report_share,locate_map_img;
        CardView reportCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location_txt=itemView.findViewById(R.id.location_txt);
            dom_txt=itemView.findViewById(R.id.dom_txt);
            from_time_txt=itemView.findViewById(R.id.from_time_txt);
            to_time_txt=itemView.findViewById(R.id.to_time_txt);
            division_txt=itemView.findViewById(R.id.division_txt);
            range_txt=itemView.findViewById(R.id.range_txt);
            section_txt=itemView.findViewById(R.id.section_txt);
            beat_txt=itemView.findViewById(R.id.beat_txt);
            total_no=itemView.findViewById(R.id.total_no);
            heard=itemView.findViewById(R.id.heard);
            tusker=itemView.findViewById(R.id.tusker);
            male=itemView.findViewById(R.id.male);
            female=itemView.findViewById(R.id.female);
            calf=itemView.findViewById(R.id.calf);
            report_through=itemView.findViewById(R.id.report_through);
            report_type=itemView.findViewById(R.id.report_type);
            reportingType_layout=itemView.findViewById(R.id.reportingType_layout);
            remark=itemView.findViewById(R.id.remark);
            report=itemView.findViewById(R.id.report);
            accuracy_txt=itemView.findViewById(R.id.accuracy_txt);
            update_img=itemView.findViewById(R.id.update_img);
            report_img=itemView.findViewById(R.id.report_img);
            report_share=itemView.findViewById(R.id.report_share);
            report_through_img=itemView.findViewById(R.id.report_through_img);
            locate_map_img=itemView.findViewById(R.id.locate_map_img);
            reportCV=itemView.findViewById(R.id.reportCV);

        }
    }

    public String convertDate(String dateStr,String givenFormat,String outputFormat){
        SimpleDateFormat input = new SimpleDateFormat(givenFormat);
//        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

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
//            ((MapFragment) context.getFragmentManager()
//                    .findFragmentById(R.id.map)).getMapAsync(this);
//            map.getUiSettings().setZoomControlsEnabled(true);


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

    public void call_PdfDialog(Context context,String msg,String dialog_title,int position,Bitmap bitmap){

        try {

            AlertDialog.Builder builder=  new AlertDialog.Builder(context);

            builder.setMessage(msg)
                    .setTitle(dialog_title)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            update_clickListener.on_CardViewClick(position,viewReportData_arr.get(position).getSighting_time_from(),
                                    viewReportData_arr.get(position).getSighting_time_to(),bitmap);

//                            if (view_item_arr.size()>0){
//
//                                Bitmap bitmap = getBitmapFromView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());
//                                String pdf_path=PermissionUtils.createPdfFromImage(getActivity(),new Date().toString(),"",bitmap);
////                        Toast.makeText(context, ""+pdf_path, Toast.LENGTH_SHORT).show();
//                                Log.i("pdf_path",""+pdf_path);
//                                PermissionUtils.sharePdfInSocialMedia(context,pdf_path);//Share in social media
//                            }else {
//                                Toast.makeText(context, "No data to create pdf !", Toast.LENGTH_SHORT).show();
//                            }

                            dialog.cancel();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();

                        }
                    });
            //Creating dialog box
            AlertDialog alertDialog = builder.create();
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingLeftDialogAnimation;
            alertDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
