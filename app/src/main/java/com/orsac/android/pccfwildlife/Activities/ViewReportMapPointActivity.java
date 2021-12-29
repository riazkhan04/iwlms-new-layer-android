package com.orsac.android.pccfwildlife.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;
import com.orsac.android.pccfwildlife.Fragments.MapFragment;
import com.orsac.android.pccfwildlife.Fragments.ViewReportFragment;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewReportMapPointActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    ImageView back_img;
    double latitude,longitude;
    LatLng elephant_report_point;
    TextView satellite_txt,street_txt,div,rng,section,beat,area,totalNo,herdNo,top_tv;
    LinearLayout filter_LL,map_type_LL,info_window_ll,div_ll,rng_ll,section_ll,beat_ll,area_ll,elephant_ll;
    GeoJsonLayer stateLayer=null;
    JSONObject state_JsonResponse;
    LatLng latLng=null, layer_latlng=null;
    String divisionId="",mapTypeName="",reportType="",division="",range="",
            total="",herd="",sighting_date="",reportImg="";
    JSONObject geoJsonResponse;
    GeoJsonLayer divisionLayer=null;
    LinearLayout progress_bar_LL;
    String divn_name="",area_sq="",range_name="";
//    public String map_url="http://14.98.253.214:8087";//old geoserver link
    public String map_url="http://164.164.122.69:8080";//geoserver link



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.mapdialog);

            initData();

            clickFunction();

//            new CallMapStateLayer().execute();

            try {
                latitude=Double.parseDouble(getIntent().getStringExtra("lat"));
                longitude=Double.parseDouble(getIntent().getStringExtra("lng"));
                divisionId=getIntent().getStringExtra("divisionId");
                reportType=getIntent().getStringExtra("reportType");
                division=getIntent().getStringExtra("divisionNm");
                range=getIntent().getStringExtra("rangeNm");
                total=getIntent().getStringExtra("total");
                herd=getIntent().getStringExtra("herd");
                sighting_date=getIntent().getStringExtra("date");
                reportImg=getIntent().getStringExtra("image");

            }catch (Exception e){
                e.printStackTrace();
            }

            if (divisionId.equalsIgnoreCase("")){

            }else {
                checkDivisionIdForMapBoundary(divisionId);
            }




        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);

            back_img=findViewById(R.id.back_img);
            satellite_txt=findViewById(R.id.satellite_txt);
            street_txt=findViewById(R.id.street_txt);
            map_type_LL=findViewById(R.id.map_type_LL);
            progress_bar_LL=findViewById(R.id.progress_bar_LL);

            info_window_ll=findViewById(R.id.info_window_ll);
            div_ll=findViewById(R.id.div_ll);
            rng_ll=findViewById(R.id.rng_ll);
            section_ll=findViewById(R.id.section_ll);
            beat_ll=findViewById(R.id.beat_ll);
            area_ll=findViewById(R.id.area_ll);
            elephant_ll=findViewById(R.id.elephant_ll);
            div=findViewById(R.id.div);
            rng=findViewById(R.id.rng);
            section=findViewById(R.id.section);
            beat=findViewById(R.id.beat);
            area=findViewById(R.id.area);
            totalNo=findViewById(R.id.total);
            herdNo=findViewById(R.id.herd);
            top_tv=findViewById(R.id.top_tv);

            street_txt.setBackgroundColor(getResources().getColor(R.color.tranparent_green));


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void clickFunction() {
        try {

            back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

             info_window_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info_window_ll.setVisibility(View.GONE);
                }
            });

            satellite_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    satellite_txt.setBackgroundResource(R.color.tranparent_green);
                    street_txt.setBackgroundResource(R.color.tranparent_white);
                }
            });
            street_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    street_txt.setBackgroundResource(R.color.tranparent_green);
                    satellite_txt.setBackgroundResource(R.color.tranparent_white);
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {

            map = googleMap;
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            if (latitude==0.0){

            }else {
                elephant_report_point = new LatLng(latitude,longitude);
            }

            if (reportType.equalsIgnoreCase("direct")){

                map.addMarker(new
                        MarkerOptions().position(elephant_report_point)
                        .title(""+elephant_report_point)
                        .flat(true)
                        .rotation(5))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker_direct));
//                map.moveCamera(CameraUpdateFactory.newLatLng(elephant_report_point));
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(elephant_report_point, 12));
//                map.setMaxZoomPreference(30.0f);

            }else if (reportType.equalsIgnoreCase("indirect")){
                map.addMarker(new
                        MarkerOptions().position(elephant_report_point)
                        .title(""+elephant_report_point)
                        .flat(true)
                        .rotation(5))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker_indirect));

            }
            else if (reportType.equalsIgnoreCase("nill")||reportType.equalsIgnoreCase("nil")){
                map.addMarker(new
                        MarkerOptions().position(elephant_report_point)
                        .title(""+elephant_report_point)
                        .flat(true)
                        .rotation(5))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));

            }
            else if (reportType.equalsIgnoreCase("Elephant Death Report")){
                map.addMarker(new
                        MarkerOptions().position(elephant_report_point)
                        .title(""+elephant_report_point)
                        .flat(true)
                        .rotation(5))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker));
            }
            else if (reportType.equalsIgnoreCase("Incident Report")){
                map.addMarker(new
                        MarkerOptions().position(elephant_report_point)
                        .title(""+elephant_report_point)
                        .flat(true)
                        .rotation(5))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker));
            }

//            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//
//                    info_window_ll.setVisibility(View.VISIBLE);
//                    div_ll.setVisibility(View.VISIBLE);
//                    rng_ll.setVisibility(View.VISIBLE);
//                    elephant_ll.setVisibility(View.VISIBLE);
//                    div.setText(division);
//                    rng.setText(range);
//                    totalNo.setText(total);
//                    herdNo.setText(herd);
//
//                    if (total.equalsIgnoreCase("")){
//                        elephant_ll.setVisibility(View.GONE);
//                    }else {
//                        elephant_ll.setVisibility(View.VISIBLE);
//                    }
//
//                    area_ll.setVisibility(View.GONE);
//                    section_ll.setVisibility(View.GONE);
//                    beat_ll.setVisibility(View.GONE);
//
//                    return true;
//                }
//            });
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.custom_info_window_marker_layout, null);
                    view.setLayoutParams(new ScrollView.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT));


                    TextView circle_nm,division_nm, range_nm, section_nm, beat_nm,
                            nothing_selected,top_title,date_txt;
                    LinearLayout total_ll, herd_ll,division_ll,range_ll,top_view;
                    ImageView report_image;
//                    String reportType,_reportType;

                    circle_nm = view.findViewById(R.id.circle);
                    division_nm = view.findViewById(R.id.division);
                    range_nm = view.findViewById(R.id.range);
                    section_nm = view.findViewById(R.id.section);
                    beat_nm = view.findViewById(R.id.beat);
                    top_title = view.findViewById(R.id.top_title);
                    date_txt = view.findViewById(R.id.date_txt);
                    top_view = view.findViewById(R.id.top_view);
                    division_ll = view.findViewById(R.id.division_ll);
                    range_ll = view.findViewById(R.id.range_ll);
                    total_ll = view.findViewById(R.id.section_ll);
                    herd_ll = view.findViewById(R.id.beat_ll);
                    report_image = view.findViewById(R.id.report_image);
                    nothing_selected = view.findViewById(R.id.nothing_selected);
                    nothing_selected.setVisibility(View.GONE);

                    info_window_ll.setVisibility(View.GONE);

//                      circle_nm.setText(alllatLngs_arr.get(index).getCircle());
                    String report_type=reportType;
                    String _reportType="";
//
                    StringBuilder sb = new StringBuilder(report_type);
                    sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                    _reportType=sb.toString();
                    if (report_type.equalsIgnoreCase("Nill")||report_type.equalsIgnoreCase("Nil")){
                        top_title.setText("Nil");
                    }else {
                        top_title.setText(_reportType);
                    }
                    division_nm.setText(division);
                    range_nm.setText(range);
                    if (total.equalsIgnoreCase("0")||
                            total.equalsIgnoreCase("")){
                        total_ll.setVisibility(View.GONE);
                        herd_ll.setVisibility(View.GONE);
                    }else {
                        section_nm.setText(total);
                        beat_nm.setText(herd);
                        total_ll.setVisibility(View.VISIBLE);
                        herd_ll.setVisibility(View.VISIBLE);
                    }

                    if (reportImg==null){
                        Glide.with(ViewReportMapPointActivity.this)
                                .load(R.drawable.no_image_found)
                                .error(R.drawable.no_image_found)
                                .into(report_image);

                    }else {
                        Glide.with(ViewReportMapPointActivity.this)
                                .load(RetrofitClient.IMAGE_URL+"report/"+reportImg)
                                .error(R.drawable.no_image_found)
                                .into(report_image);
                    }

                    date_txt.setText(PermissionUtils.convertDate(sighting_date,
                            "yyyy-MM-dd hh:mm:sss","dd-MM-yyyy"));
//        }
                    division_ll.setVisibility(View.VISIBLE);
                    range_ll.setVisibility(View.VISIBLE);

                    return view;
                }
            });

            map.moveCamera(CameraUpdateFactory.newLatLng(elephant_report_point));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(elephant_report_point, 9));
            map.setMaxZoomPreference(30.0f);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public class CallMapStateLayer extends AsyncTask<String ,String ,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (progress_bar_LL.getVisibility()==View.VISIBLE){
//                progress_bar_LL.setVisibility(View.GONE);
//            }
//            progress_bar_LL.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String value="";
            HttpResponse response;
            try {
                String geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
                        "PCCFWILDLIFE1:odisha_state_boundary&maxFeatures=50&outputFormat=application/json";

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(geoServerUrl);
                response = httpClient.execute(httpGet);
                value = EntityUtils.toString(response.getEntity());

            }catch (Exception e){
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                state_JsonResponse=new JSONObject(s);

                try{
                    //for putting GeoJsonLayer on Map with json response coming from geoserver
                    stateLayer=new GeoJsonLayer(map,state_JsonResponse);
                    callToGetLatlngFromBoundary(state_JsonResponse);//Get latlng to zoom from state boundary

                    GeoJsonPolygonStyle polyStyle =stateLayer.getDefaultPolygonStyle();
                    polyStyle.setStrokeColor(getResources().getColor(R.color.state_boundary_color));
                    polyStyle.setStrokeWidth(7);
                    stateLayer.addLayerToMap();

//                    if (roleID.equalsIgnoreCase("2")){
//                        divCode=divId;
//                        checkDivisionIdForMapBoundary(divCode);
//                    }

                }catch(Exception e) {
//                    progress_bar_LL.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            } catch (JSONException e) {
//                progress_bar_LL.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    public void callToGetLatlngFromBoundary(JSONObject geoJson){
        try {
            ArrayList<LatLng> listdata = new ArrayList<LatLng>();
            JSONArray feature_jsonArray = geoJson.getJSONArray("features");
            JSONObject geometry_json=feature_jsonArray.getJSONObject(0).getJSONObject("geometry");

            JSONArray coordinate_jsonArray = geometry_json.getJSONArray("coordinates");
            JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
            first_jsonArray=coordinate_jsonArray.getJSONArray(0);
            JSONObject properties_json=feature_jsonArray.getJSONObject(0).getJSONObject("properties");
            second_jsonArr = first_jsonArray.getJSONArray(0);

            for (int j = 0; j < second_jsonArr.length()/4; j++) {
                third_jsonArr = second_jsonArr.getJSONArray(j);
                String[] coord = third_jsonArr.toString().split(",");
                double x = Double.parseDouble(coord[0].replace("[",""));
                double y = Double.parseDouble(coord[1].replace("]",""));
                listdata.add(new LatLng(y,x));
                latLng=new LatLng(y,x);//(latitude,longitude)- It will zoom by taking to this
            }
//            if (progress_bar_LL.getVisibility()==View.VISIBLE){
//                progress_bar_LL.setVisibility(View.GONE);
//            }
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            map.setMaxZoomPreference(30.0f);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkDivisionIdForMapBoundary(String divisionId){
        try {
            if (divisionId.equalsIgnoreCase("1")){
                mapTypeName="PCCFWILDLIFE1:balesore_division_boundary";
            }else if (divisionId.equalsIgnoreCase("2")){
                mapTypeName="PCCFWILDLIFE1:baliguda_division_boundary";
            }else if (divisionId.equalsIgnoreCase("3")){
                mapTypeName="PCCFWILDLIFE1:bamara_division_boundary";
            }else if (divisionId.equalsIgnoreCase("4")){
                mapTypeName="PCCFWILDLIFE1:bhadrak_division_boundary";
            }else if (divisionId.equalsIgnoreCase("5")){
                mapTypeName="PCCFWILDLIFE1:chandaka_division_boundary";
            }else if (divisionId.equalsIgnoreCase("6")){
                mapTypeName="PCCFWILDLIFE1:chilika_division_boundary";
            }else if (divisionId.equalsIgnoreCase("7")){
                mapTypeName="PCCFWILDLIFE1:dhenkanala_division_boundary";
            }else if (divisionId.equalsIgnoreCase("8")){
                mapTypeName="PCCFWILDLIFE1:hirakud_division_boundary";
            }else if (divisionId.equalsIgnoreCase("9")){
                mapTypeName="PCCFWILDLIFE1:kalahandis_division_boundary";
            }else if (divisionId.equalsIgnoreCase("10")){
                mapTypeName="PCCFWILDLIFE1:keonjhar_division_boundary";
            }else if (divisionId.equalsIgnoreCase("11")){
                mapTypeName="PCCFWILDLIFE1:mahanadi_division_boundary";
            }else if (divisionId.equalsIgnoreCase("12")){
                mapTypeName="";
            }else if (divisionId.equalsIgnoreCase("13")){
                mapTypeName="PCCFWILDLIFE1:paralakhemundi_division_boundary";
            }else if (divisionId.equalsIgnoreCase("14")){
                mapTypeName="PCCFWILDLIFE1:puri_division_boundary";
            }else if (divisionId.equalsIgnoreCase("15")){
                mapTypeName="PCCFWILDLIFE1:rajanagar_division_boundary";
            }else if (divisionId.equalsIgnoreCase("16")){
                mapTypeName="PCCFWILDLIFE1:satakosia_division_boundary";
            }else if (divisionId.equalsIgnoreCase("17")){
                mapTypeName="PCCFWILDLIFE1:baripada_division_boundary";
            }else if (divisionId.equalsIgnoreCase("18")){
                mapTypeName="PCCFWILDLIFE1:baripada_division_boundary";
            }else if (divisionId.equalsIgnoreCase("19")){
                mapTypeName="PCCFWILDLIFE1:sunabeda_division_boundary";
            }

//            new CallMapDivisionLayer().execute(mapTypeName,divisionId);
             new CallMapDivisionLayer().execute(divisionId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class CallMapDivisionLayer extends AsyncTask<String ,String ,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }
            progress_bar_LL.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... strings) {
            String value="";
            HttpResponse response;
            try {
//                String geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/wfs?service=WFS&version=1.1.0&request=GetFeature&typeName="
//                        +strings[0]+"&maxFeatures=2000&outputFormat=application/json";
                String geoServerUrl="";
                if (!strings[0].equals("")){
//                     geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/wfs?service=WFS&version=1.1.0&request=GetFeature&typeName="
//                        +strings[0]+"&maxFeatures=2000&outputFormat=application/json";

                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
                            "PCCFWILDLIFE1%3Adivision_boundary&maxFeatures=50&outputFormat=application%2Fjson%20" +
                            "&CQL_FILTER=division_id='"+strings[0]+"'";
                }else {
//                     geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
//                            "PCCFWILDLIFE1%3Aall_division_renew&maxFeatures=50&outputFormat=application%2Fjson%20" +
//                            "&CQL_FILTER=division_id='"+strings[1]+"'";

                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
                            "PCCFWILDLIFE1%3Adivision_boundary&maxFeatures=50&outputFormat=application%2Fjson%20" +
                            "&CQL_FILTER=division_id='"+strings[0]+"'";
                }
                mapTypeName="";

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(geoServerUrl);
                response = httpClient.execute(httpGet);
                value = EntityUtils.toString(response.getEntity());

            }catch (Exception e){
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                geoJsonResponse=new JSONObject(s);

                try{
                    //for putting GeoJsonLayer on Map with json response coming from geoserver
                    divisionLayer=new GeoJsonLayer(map,geoJsonResponse);
                    progress_bar_LL.setVisibility(View.GONE);

                    ArrayList<LatLng> listdata = new ArrayList<LatLng>();
                    JSONArray feature_jsonArray = geoJsonResponse.getJSONArray("features");
                    JSONObject geometry_json=null;

                    for (int i=0;i<feature_jsonArray.length();i++) {
                        try {
                            geometry_json = feature_jsonArray.getJSONObject(i).getJSONObject("geometry");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    JSONArray coordinate_jsonArray = geometry_json.getJSONArray("coordinates");
                    JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
                    first_jsonArray=coordinate_jsonArray.getJSONArray(0);
                    JSONObject properties_json=feature_jsonArray.getJSONObject(0).getJSONObject("properties");
//                    String name=properties_json.getString("division");
                    try {
                        divn_name=properties_json.getString("divn_name");
                        area_sq=properties_json.getString("area_sqkm");
                        area_ll.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                        divn_name=properties_json.getString("division");
                        area_ll.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                    second_jsonArr = first_jsonArray.getJSONArray(0);

                    for (int j = 0; j < second_jsonArr.length(); j++) {
                        third_jsonArr = second_jsonArr.getJSONArray(j);
                        String[] coord = third_jsonArr.toString().split(",");
                        double x = Double.parseDouble(coord[0].replace("[",""));
                        double y = Double.parseDouble(coord[1].replace("]",""));
                        listdata.add(new LatLng(y,x));
                        layer_latlng=new LatLng(y,x);//(latitude,longitude)- It will zoom by taking to this latlng
                    }



                    GeoJsonPolygonStyle polyStyle =divisionLayer.getDefaultPolygonStyle();
                    polyStyle.setStrokeColor(getResources().getColor(R.color.black));
                    polyStyle.setStrokeWidth(7);


                    divisionLayer.addLayerToMap();

                    info_window_ll.setVisibility(View.GONE);
                    divisionLayer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                        @Override
                        public void onFeatureClick(GeoJsonFeature geoJsonFeature) {

                            top_tv.setText("Layer Info");
                            info_window_ll.setVisibility(View.VISIBLE);
                            div_ll.setVisibility(View.VISIBLE);
                            div.setText(divn_name);
                            area.setText(area_sq +" sq.km");

                            elephant_ll.setVisibility(View.GONE);
                            rng_ll.setVisibility(View.GONE);
                            section_ll.setVisibility(View.GONE);
                            beat_ll.setVisibility(View.GONE);

                        }
                    });

//                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//
//                            info_window_ll.setVisibility(View.VISIBLE);
//                            div_ll.setVisibility(View.VISIBLE);
//                            rng_ll.setVisibility(View.VISIBLE);
//                            elephant_ll.setVisibility(View.VISIBLE);
//                            div.setText(division);
//                            rng.setText(range);
//                            totalNo.setText(total);
//                            herdNo.setText(herd);
//
//                            if (total.equalsIgnoreCase("")){
//                                elephant_ll.setVisibility(View.GONE);
//                            }else {
//                                elephant_ll.setVisibility(View.VISIBLE);
//                            }
//                            String report_type=reportType;
//                            String _reportType="";
//
//                            StringBuilder sb = new StringBuilder(report_type);
//                            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
//                            _reportType=sb.toString();
//
//                            if (report_type.equalsIgnoreCase("Nill")||report_type.equalsIgnoreCase("Nil")){
//                                top_tv.setText("Nil");
//                            }else {
//                                top_tv.setText(_reportType);
//                            }
//
//                            area_ll.setVisibility(View.GONE);
//                            section_ll.setVisibility(View.GONE);
//                            beat_ll.setVisibility(View.GONE);
//
//                            return true;
//                        }
//                    });


                    map.moveCamera(CameraUpdateFactory.newLatLng(layer_latlng));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(layer_latlng, 8));
                    map.setMaxZoomPreference(30.0f);

                }catch(Exception e) {
                    e.printStackTrace();
                    progress_bar_LL.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                progress_bar_LL.setVisibility(View.GONE);
            }
        }
    }

}
