package com.orsac.android.pccfwildlife.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPoint;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;
import com.orsac.android.pccfwildlife.Model.YearSpinnerCustomModel;
import com.orsac.android.pccfwildlife.Adapters.CustomSpinnerYearMonthAdapter;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllCircleData;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllDivisionModel;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.RangeDataByDivId;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElephantMapAnalyticsFragment extends Fragment implements OnMapReadyCallback, CustomSpinnerYearMonthAdapter.OnCheckbox_Selected_listener {

    Context context;
    GoogleMap map;
    SessionManager session;
    String divisionId="",roles="",roleID="",circleId="",divId="",rangeId="",division_Nm="";
    AppCompatImageView filter_img,screenshot_img;
    JSONObject geoJsonResponse,state_JsonResponse,rangeJsonResponse,analyticsJsonResponse,circleJsonResponse;
    String divisionValue="", rangeValue="", secValue="", beatValue="",circleValue="",from_dateSelected="",to_dateSelected;
    ArrayList<AllCircleData> circleDataArrayList;
    ArrayList<AllDivisionModel> divisionDataByCircleIdArrayList;
    ArrayList<RangeDataByDivId> rangeDataByDivIdArrayList;
    ArrayAdapter<AllCircleData> circle_dataAdapter;
    ArrayAdapter<AllDivisionModel> div_dataAdapter;
    ArrayAdapter<RangeDataByDivId> range_dataAdapter;
    int selecetedCircle=0,selectedDiv=0,selectedRange=0;
    GeoJsonLayer divisionLayer=null,rangeLayer=null,stateLayer=null,analyticsLayer=null,circleLayer=null;
    LinearLayout circle_ll,division_ll,range_ll,progress_bar_LL,progress_bar_LL_filter,info_window_ll,div_ll,
            rng_ll,section_ll,beat_ll,area_ll,cir_ll;
    TextView satellite_txt,progress_txt,top_tv;
    String mapTypeName="",rangemapTypeName="",map_division_Id="";
    LatLng latLng=null,layer_latlng=null;
    public String circleCode="All",divCode="All", rangeCode="All",secCode="All", beatCode="All",reportType="direct";
    SearchableSpinner division_spinner,range_spinner;
    AppCompatSpinner direct_indirect_spinner,year_spinner;
    Dialog dialog,year_month_dialog;
//    CustomSpinnerAdapter customSpinnerAdapter;
    //ArrayList<YearSpinnerCustomModel> customModelArrayList,new_month_arrList;
    List<String> selected_yearSpinner_list,selected_monthSpinner_list;
    ConstraintLayout main_LL,dialog_main_LL;
    CustomSpinnerYearMonthAdapter customSpinnerYearMonthAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView year_edit,month_txt,submit_txt,divisionTxt,div,rng,section,beat,area,circle_nm;
    RecyclerView year_recyclerV;
    LinearLayout list_ll;
    ImageView close_img;
    ArrayList<YearSpinnerCustomModel> new_year_arrList,new_month_arrList;
    String showText = "",month_text="";
    ArrayList<Marker> markers;
    Marker marker=null;
    String divn_name="",area_sq="",range_name="",circle_name;
    private Bitmap mSnapshot;
    RelativeLayout frameLL;
    TextView screenshot_txt;
    ImageView screenshot;
    Runnable runnable;
    int seconds;
    Handler handler=new Handler();
    File open_file_in_gallery=null;
//    public String map_url="http://14.98.253.214:8087";
    public String map_url="http://164.164.122.69:8080";//geoserver link
    String map_analyticsUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&" +
        "typeName=PCCFWILDLIFE1%3Aelephant_sighting&maxFeatures=2000&outputFormat=application%2Fjson&CQL_FILTER=";



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.elephant_map_path_fragment,container,false);

        try {

            filter_img=view.findViewById(R.id.filter_img);
            screenshot_img=view.findViewById(R.id.screenshot_img);
            progress_bar_LL=view.findViewById(R.id.progress_bar_LL);
            progress_txt=view.findViewById(R.id.progress_txt);
            main_LL=view.findViewById(R.id.main_LL);

            info_window_ll=view.findViewById(R.id.info_window_ll);
            cir_ll=view.findViewById(R.id.cir_ll);
            div_ll=view.findViewById(R.id.div_ll);
            rng_ll=view.findViewById(R.id.rng_ll);
            section_ll=view.findViewById(R.id.section_ll);
            beat_ll=view.findViewById(R.id.beat_ll);
            area_ll=view.findViewById(R.id.area_ll);
            circle_nm=view.findViewById(R.id.circle_nm);
            div=view.findViewById(R.id.div);
            rng=view.findViewById(R.id.rng);
            section=view.findViewById(R.id.section);
            beat=view.findViewById(R.id.beat);
            area=view.findViewById(R.id.area);
            frameLL=view.findViewById(R.id.frameLL);
            screenshot=view.findViewById(R.id.screenshot);
            screenshot_txt=view.findViewById(R.id.screenshot_txt);
            top_tv=view.findViewById(R.id.top_tv);

            divisionDataByCircleIdArrayList=new ArrayList<>();
            rangeDataByDivIdArrayList=new ArrayList<>();

            new_year_arrList=new ArrayList<>();
            new_month_arrList=new ArrayList<>();
            markers=new ArrayList<>();

            selected_yearSpinner_list=new ArrayList<>();
            selected_monthSpinner_list=new ArrayList<>();

            session = new SessionManager(context);
            divisionId=session.getJuridictionID();
            roles=session.getRoles();
            roleID=session.getRoleId();
            circleId=session.getCircleId();
            divId=session.getDivisionId();
            rangeId = session.getRangeId();
            division_Nm = session.getJuridiction();
            from_dateSelected= PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
            to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");

            divisionDataByCircleIdArrayList.clear();
            divisionDataByCircleIdArrayList.add(new AllDivisionModel("-1","Select Division"));

            rangeDataByDivIdArrayList.clear();
            rangeDataByDivIdArrayList.add(new RangeDataByDivId("-1","Select Range"));

            frameLL.setVisibility(View.GONE);

            screenshot_img.setVisibility(View.GONE);//hide for screenshot

            new CallMapStateLayer().execute();

            click_function();

            initSpinnerList();
            initMonthSpinnerList();

            SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);

        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }


    private void click_function() {
        try {

            filter_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    callFilterDialog(getActivity());

                }
            });

            screenshot_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (divisionValue.equalsIgnoreCase("")){
                            Snackbar.make(main_LL, "Please add data to take screenshot !", Snackbar.LENGTH_LONG).show();
                        }
                        else {
                            frameLL.setVisibility(View.VISIBLE);
                            screenshot.setVisibility(View.VISIBLE);
                            //For taking screenshot
                            GoogleMap.SnapshotReadyCallback callback=new GoogleMap.SnapshotReadyCallback() {
                                @Override
                                public void onSnapshotReady(Bitmap bitmap) {
                                    String path="";
                                    try {
                                        path = Environment.getExternalStorageDirectory()
                                                .getAbsolutePath() + "/WildlifeAnalyticsScreenshots/";
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                   screenshot.setImageBitmap(bitmap);
                                    mSnapshot = bitmap;
                                    captureAndStoreImage(bitmap,path);//for storing in file

                                    animationScreenshot();//animation for screenshot
                                }
                            };
                            map.snapshot(callback, mSnapshot);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            screenshot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openScreenshotinGallery(open_file_in_gallery);//open screenshot in gallery
                }
            });

            info_window_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info_window_ll.setVisibility(View.GONE);
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
            LatLng elephant_report = new LatLng(20.2961, 85.8245);


            map.moveCamera(CameraUpdateFactory.newLatLng(elephant_report));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(elephant_report, 15));
            map.setMaxZoomPreference(30.0f);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void callFilterDialog(Context context){
        try {
            dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
            dialog.setContentView(R.layout.filter_dialog);

            ImageView close_img;
            CardView submit_btn,reset_btn;
            close_img=dialog.findViewById(R.id.close_img);
            submit_btn=dialog.findViewById(R.id.submit_CV);
            reset_btn=dialog.findViewById(R.id.reset_CV);
            division_spinner=dialog.findViewById(R.id.division);
            range_spinner=dialog.findViewById(R.id.range);
            dialog_main_LL=dialog.findViewById(R.id.main_ll);
            year_edit=dialog.findViewById(R.id.year_edit);
            month_txt=dialog.findViewById(R.id.month_txt);
            divisionTxt=dialog.findViewById(R.id.divisionTxt);
            progress_bar_LL_filter=dialog.findViewById(R.id.progress_bar_LL);
//            year_spinner=dialog.findViewById(R.id.year_spinner);

            if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")|| roleID.equalsIgnoreCase("6")){

                division_spinner.setVisibility(View.VISIBLE);
                range_spinner.setVisibility(View.VISIBLE);
                divisionTxt.setVisibility(View.GONE);
            }
            else if (roleID.equalsIgnoreCase("2")) { //DFO
                division_spinner.setVisibility(View.GONE);
                range_spinner.setVisibility(View.VISIBLE);
                divCode=divId;
                divisionTxt.setVisibility(View.VISIBLE);
                divisionTxt.setText(division_Nm);
                getAllRangeByDivisionId(divCode,"");
            }
            else if (roleID.equalsIgnoreCase("4")) { //Range
                division_spinner.setVisibility(View.GONE);
                range_spinner.setVisibility(View.GONE);
                divisionTxt.setVisibility(View.GONE);
            }


            if (div_dataAdapter!=null){
                division_spinner.setAdapter(div_dataAdapter);
                division_spinner.setSelection(selectedDiv);
            }else{
                div_dataAdapter = new ArrayAdapter<AllDivisionModel>(context,
                        android.R.layout.simple_spinner_dropdown_item, divisionDataByCircleIdArrayList);
                div_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                division_spinner.setAdapter(div_dataAdapter);
                division_spinner.setSelection(selectedDiv);
            }
            if (range_dataAdapter!=null){
                range_spinner.setAdapter(range_dataAdapter);
                range_spinner.setSelection(selectedRange);
            }else{
                range_dataAdapter = new ArrayAdapter<RangeDataByDivId>(context,
                        android.R.layout.simple_spinner_dropdown_item, rangeDataByDivIdArrayList);
                range_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                range_spinner.setAdapter(range_dataAdapter);
                range_spinner.setSelection(selectedRange);
            }

            if (showText.equalsIgnoreCase("")){
                year_edit.setText("Select Year");
            }
            else {
                year_edit.setText(showText);
            }
            if (month_text.equalsIgnoreCase("")){
                month_txt.setText("Select Month");
            }
            else {
                month_txt.setText(month_text);
            }

            year_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callYearMonthListDialog(context,"year");
                }
            });

            month_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callYearMonthListDialog(context,"month");
                }
            });


            getAllDivision();

            division_spinner.setTitle("Select Division");
            division_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    divisionValue = adapterView.getItemAtPosition(i).toString();
                    divCode=""+divisionDataByCircleIdArrayList.get(i).getDivisionId();


                    if(selectedDiv!=i && i!=0){
                        selectedRange = 0;
                        range_spinner.setSelection(selectedRange);
                        if (divisionLayer!=null){
                            divisionLayer.removeLayerFromMap();
                        }
                        if (rangeLayer!=null){
                            rangeLayer.removeLayerFromMap();
                        }
                        checkDivisionIdForMapBoundary(divCode);
                        getAllRangeByDivisionId(divCode,"");
                    }
                    selectedDiv=i;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            range_spinner.setTitle("Select Range");
            range_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    rangeValue = adapterView.getItemAtPosition(i).toString();
                    rangeCode=""+rangeDataByDivIdArrayList.get(i).getRangeId();


                    if(selectedRange!=i && i!=0) {
                        if (rangeLayer != null) {
                            rangeLayer.removeLayerFromMap();
                        }
                        checkRangeIdForMapRangeBoundary(rangeCode);
                    }
                    selectedRange=i;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;

                    for (int j=0;j<markers.size();j++){
                        markers.get(j).remove();//remove all marker
                    }

                }
            });

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                    if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")|| roleID.equalsIgnoreCase("6")){

                        if (divisionValue.equalsIgnoreCase("")||divisionValue.equalsIgnoreCase("Select Division")){
                            Snackbar.make(dialog_main_LL, "Please select division !", Snackbar.LENGTH_LONG).show();

                        }else {
                            for (int i=0;i<markers.size();i++){
                                markers.get(i).remove();//remove all marker
                            }
                            dialog.dismiss();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
//                        marker.remove();
                            new CallMapAnalytics().execute(divCode,rangeCode);
                        }
                    }
                    else if (roleID.equalsIgnoreCase("2")) { //DFO
                        divCode=divId;

                            for (int i=0;i<markers.size();i++){
                                markers.get(i).remove();//remove all marker
                            }
                            dialog.dismiss();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
//                        marker.remove();
                            new CallMapAnalytics().execute(divCode,rangeCode);

                     }




                 }catch (Exception e){
                   e.printStackTrace();
                  }

                }
            });

            reset_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")|| roleID.equalsIgnoreCase("6")){

                            if (selectedDiv!=0){
                                selectedDiv=0;
                            }
                            if (selectedRange!=0){
                                selectedRange=0;
                            }
                            division_spinner.setSelection(0);
                            range_spinner.setSelection(0);

                            if (divisionLayer!=null){
                                divisionLayer.removeLayerFromMap();
                            }
                            if (rangeLayer!=null){
                                rangeLayer.removeLayerFromMap();
                            }
                            if (!markers.isEmpty()){
                                for (int i=0;i<markers.size();i++){
                                    markers.get(i).remove();//remove all marker if exist
                                }
                            }

                            for (int i = 0; i < new_year_arrList.size(); i++) {
                                if (new_year_arrList.get(i).isChecked()){
                                    new_year_arrList.get(i).setChecked(false);
                                }
                            }

                            for (int i = 0; i < new_month_arrList.size(); i++) {
                                if (new_month_arrList.get(i).isChecked()){
                                    new_month_arrList.get(i).setChecked(false);
                                }
                            }
                            showText="";
                            month_text="";
                            year_edit.setText("Select Year");
                            month_txt.setText("Select Month");
                            customSpinnerYearMonthAdapter.notifyDataSetChanged();
                            div_dataAdapter.notifyDataSetChanged();
                            range_dataAdapter.notifyDataSetChanged();
                        }

                        else if (roleID.equalsIgnoreCase("2")) { //DFO

                            if (selectedRange!=0){
                                selectedRange=0;
                            }
                            range_spinner.setSelection(0);

                            if (rangeLayer!=null){
                                rangeLayer.removeLayerFromMap();
                            }
                            if (!markers.isEmpty()){
                                for (int i=0;i<markers.size();i++){
                                    markers.get(i).remove();//remove all marker if exist
                                }
                            }

                            for (int i = 0; i < new_year_arrList.size(); i++) {
                                if (new_year_arrList.get(i).isChecked()){
                                    new_year_arrList.get(i).setChecked(false);
                                }
                            }

                            for (int i = 0; i < new_month_arrList.size(); i++) {
                                if (new_month_arrList.get(i).isChecked()){
                                    new_month_arrList.get(i).setChecked(false);
                                }
                            }
                            showText="";
                            month_text="";
                            year_edit.setText("Select Year");
                            month_txt.setText("Select Month");
                            customSpinnerYearMonthAdapter.notifyDataSetChanged();
                            div_dataAdapter.notifyDataSetChanged();
                            range_dataAdapter.notifyDataSetChanged();

                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void checkDivisionIdForMapBoundary(String divisionId){
        try {
//            new CallMapDivisionLayer().execute(mapTypeName,divisionId);
            new CallMapDivisionLayer().execute(divisionId);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void oncheckBoxSelected(ArrayList<YearSpinnerCustomModel> checkBoxItemModel,String year_month_type) {
        try {

            if (year_month_type.equalsIgnoreCase("year")){//for year

                for(int i=0;i<checkBoxItemModel.size();i++){
                    if(checkBoxItemModel.get(i).isChecked()){
                        new_year_arrList.add(checkBoxItemModel.get(i));
                    }
                }
            }else {  //for month

                for(int i=0;i<checkBoxItemModel.size();i++){
                    if(checkBoxItemModel.get(i).isChecked()){
                        new_month_arrList.add(checkBoxItemModel.get(i));
                    }
                }
            }

            Log.i("year_select",new_year_arrList.toString());
            Log.i("month_select",new_month_arrList.toString());


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public class CallMapStateLayer extends AsyncTask<String ,String ,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }
            progress_bar_LL.setVisibility(View.VISIBLE);
            progress_txt.setText("Fetching layer...Please wait !");
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
//                GeoJsonLayer layer=new GeoJsonLayer(map,R.raw.state,context);
                    callToGetLatlngFromBoundary(state_JsonResponse);//Get latlng to zoom from state boundary

                    GeoJsonPolygonStyle polyStyle =stateLayer.getDefaultPolygonStyle();
                    polyStyle.setStrokeColor(getResources().getColor(R.color.state_boundary_color));
                    polyStyle.setStrokeWidth(7);
                    stateLayer.addLayerToMap();

                    if (roleID.equalsIgnoreCase("2")){
                        divCode=divId;
                        circleCode=circleId;
                        new CallMapCircleLayer().execute(circleCode);
                        checkDivisionIdForMapBoundary(divCode);
//                        loadAllMapDataAsPerCircleRangeDiv_Date(circleCode,divCode,rangeCode,secCode,beatCode,reportType,from_dateSelected,to_dateSelected);

//                        getMapDatafromCircleDivRange(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected); //For All point i.e.direct and indirect

                    }
                    else if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")||roleID.equalsIgnoreCase("6")) {

//                        loadAllMapDataAsPerCircleRangeDiv_Date(circleCode,divCode,rangeCode,secCode,beatCode,reportType,from_dateSelected,to_dateSelected);

//                        getMapDatafromCircleDivRange(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected); //For All point i.e.direct and indirect

                    }
                    if (progress_bar_LL.getVisibility()==View.VISIBLE){
                        progress_bar_LL.setVisibility(View.GONE);
                    }

                }catch(Exception e) {
                    progress_bar_LL.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                progress_bar_LL.setVisibility(View.GONE);
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
                layer_latlng=new LatLng(y,x);//(latitude,longitude)- It will zoom by taking to this
            }
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(layer_latlng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(layer_latlng, 6));
            map.setMaxZoomPreference(30.0f);

        }catch (Exception e){
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }
            e.printStackTrace();
        }
    }

    public class CallMapDivisionLayer extends AsyncTask<String ,String ,String>{

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
                        circle_name = properties_json.getString("circle");
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
                            cir_ll.setVisibility(View.VISIBLE);
                            circle_nm.setText(circle_name);
                            div.setText(divn_name);
                            area.setText(area_sq +" sq.km");

//                            elephant_ll.setVisibility(View.GONE);
                            rng_ll.setVisibility(View.GONE);
                            section_ll.setVisibility(View.GONE);
                            beat_ll.setVisibility(View.GONE);
                        }
                    });

                    map.moveCamera(CameraUpdateFactory.newLatLng(layer_latlng));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(layer_latlng, 8.5f));
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

    public void checkRangeIdForMapRangeBoundary(String rangeId){
        try {
            if (rangeId.equalsIgnoreCase("24")|| rangeId.equalsIgnoreCase("44")||
                    rangeId.equalsIgnoreCase("64")|| rangeId.equalsIgnoreCase("75")||
                    rangeId.equalsIgnoreCase("89")){
                rangemapTypeName="PCCFWILDLIFE1:balesore_range_boundary";
            }else if (rangeId.equalsIgnoreCase("4")||rangeId.equalsIgnoreCase("12")||
                    rangeId.equalsIgnoreCase("20")||rangeId.equalsIgnoreCase("29")||
                    rangeId.equalsIgnoreCase("49")||rangeId.equalsIgnoreCase("61")||
                    rangeId.equalsIgnoreCase("97")){
                rangemapTypeName="PCCFWILDLIFE1:baliguda_range_boundary";
            }else if (rangeId.equalsIgnoreCase("3")||rangeId.equalsIgnoreCase("7")||
                    rangeId.equalsIgnoreCase("45")||rangeId.equalsIgnoreCase("58")||
                    rangeId.equalsIgnoreCase("62")){
                rangemapTypeName="PCCFWILDLIFE1:bamara_range_boundary";
            }else if (rangeId.equalsIgnoreCase("11")||rangeId.equalsIgnoreCase("13")||
                    rangeId.equalsIgnoreCase("23")||rangeId.equalsIgnoreCase("32")){
                rangemapTypeName="PCCFWILDLIFE1:bhadrak_range_boundary";
            }else if (rangeId.equalsIgnoreCase("16")||rangeId.equalsIgnoreCase("22")||
                    rangeId.equalsIgnoreCase("28")||rangeId.equalsIgnoreCase("40")){
                rangemapTypeName="PCCFWILDLIFE1:chandaka_range_boundary";
            }else if (rangeId.equalsIgnoreCase("5")||rangeId.equalsIgnoreCase("27")||
                    rangeId.equalsIgnoreCase("86")||rangeId.equalsIgnoreCase("88")||
                    rangeId.equalsIgnoreCase("92")){
                rangemapTypeName="PCCFWILDLIFE1:chilika_range_boundary";
            }else if (rangeId.equalsIgnoreCase("15")||rangeId.equalsIgnoreCase("34")||
                    rangeId.equalsIgnoreCase("41")||rangeId.equalsIgnoreCase("50")||
                    rangeId.equalsIgnoreCase("51")||rangeId.equalsIgnoreCase("54")||
                    rangeId.equalsIgnoreCase("67")|| rangeId.equalsIgnoreCase("87")){
                rangemapTypeName="PCCFWILDLIFE1:dhenkanala_range_boundary";
            }else if (rangeId.equalsIgnoreCase("42")||rangeId.equalsIgnoreCase("52")||
                    rangeId.equalsIgnoreCase("66")){
                rangemapTypeName="PCCFWILDLIFE1:hirakud_range_boundary";
            }else if (rangeId.equalsIgnoreCase("17")||rangeId.equalsIgnoreCase("33")||
                    rangeId.equalsIgnoreCase("43")||rangeId.equalsIgnoreCase("48")||
                    rangeId.equalsIgnoreCase("55")||rangeId.equalsIgnoreCase("93")||
                    rangeId.equalsIgnoreCase("94")){
                rangemapTypeName="PCCFWILDLIFE1:kalahandis_range_boundary";
            }else if (rangeId.equalsIgnoreCase("1")||rangeId.equalsIgnoreCase("19")||
                    rangeId.equalsIgnoreCase("30")||rangeId.equalsIgnoreCase("39")){
                rangemapTypeName="PCCFWILDLIFE1:keonjhar_range_boundary";
            }else if (rangeId.equalsIgnoreCase("8")||rangeId.equalsIgnoreCase("9")||
                    rangeId.equalsIgnoreCase("26")||rangeId.equalsIgnoreCase("65")){
                rangemapTypeName="PCCFWILDLIFE1:mahanadi_range_boundary";
            }else if (rangeId.equalsIgnoreCase("71")){
                rangemapTypeName="";//nandankanan not published yet
            }else if (rangeId.equalsIgnoreCase("25")||rangeId.equalsIgnoreCase("31")||
                    rangeId.equalsIgnoreCase("56")||rangeId.equalsIgnoreCase("69")||
                    rangeId.equalsIgnoreCase("70")||rangeId.equalsIgnoreCase("82")||
                    rangeId.equalsIgnoreCase("85")){
                rangemapTypeName="PCCFWILDLIFE1:paralakhemundi_range_boundary";
            }else if (rangeId.equalsIgnoreCase("2")||rangeId.equalsIgnoreCase("6")||
                    rangeId.equalsIgnoreCase("18")||rangeId.equalsIgnoreCase("37")||
                    rangeId.equalsIgnoreCase("60")){
                rangemapTypeName="PCCFWILDLIFE1:puri_range_boundary";
            }else if (rangeId.equalsIgnoreCase("36")||rangeId.equalsIgnoreCase("53")||
                    rangeId.equalsIgnoreCase("63")||rangeId.equalsIgnoreCase("68")||
                    rangeId.equalsIgnoreCase("84")){
                rangemapTypeName="PCCFWILDLIFE1:rajanagar_range_boundary";
            }else if (rangeId.equalsIgnoreCase("47")||rangeId.equalsIgnoreCase("77")||
                    rangeId.equalsIgnoreCase("81")||rangeId.equalsIgnoreCase("83")||
                    rangeId.equalsIgnoreCase("96")){
                rangemapTypeName="PCCFWILDLIFE1:satakosia_range_boundary";
            }else if (rangeId.equalsIgnoreCase("10")||rangeId.equalsIgnoreCase("21")||
                    rangeId.equalsIgnoreCase("38")||rangeId.equalsIgnoreCase("57")||
                    rangeId.equalsIgnoreCase("73")||rangeId.equalsIgnoreCase("91")||
                    rangeId.equalsIgnoreCase("95")){
                rangemapTypeName="PCCFWILDLIFE1:baripada_range_boundary";//not publish yet
            }else if (rangeId.equalsIgnoreCase("14")||rangeId.equalsIgnoreCase("35")||
                    rangeId.equalsIgnoreCase("46")||rangeId.equalsIgnoreCase("72")||
                    rangeId.equalsIgnoreCase("74")||rangeId.equalsIgnoreCase("78")||
                    rangeId.equalsIgnoreCase("79")||rangeId.equalsIgnoreCase("80")||
                    rangeId.equalsIgnoreCase("98")){
                rangemapTypeName="PCCFWILDLIFE1:baripada_range_boundary";//not publish yet
            }else if (rangeId.equalsIgnoreCase("59")||rangeId.equalsIgnoreCase("76")||
                    rangeId.equalsIgnoreCase("90")){
                rangemapTypeName="PCCFWILDLIFE1:sunabeda_range_boundary";
            }

//            new CallMapRangeLayer().execute(rangemapTypeName,rangeId);
            new CallMapRangeLayer().execute(rangeId);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class CallMapRangeLayer extends AsyncTask<String ,String ,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }
            progress_bar_LL.setVisibility(View.VISIBLE);
            progress_txt.setText("Fetching layer...Please wait !");
        }

        @Override
        protected String doInBackground(String... strings) {
            String value="";
            HttpResponse response;
            try {
                String geoServerUrl="";
                if (strings[0].equals("")){
//                if (!strings[0].equals("")){

//                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/wfs?service=WFS&version=1.1.0&request=GetFeature&typeName="
//                            +strings[0]+"&maxFeatures=2000&outputFormat=application/json"+"&CQL_FILTER=range_id='"+strings[1]+"'";

                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
                            "PCCFWILDLIFE1%3Arange_boundary&maxFeatures=50&outputFormat=application%2Fjson";
                }else {
//                     geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
//                            "PCCFWILDLIFE1%3Aall_new_range&maxFeatures=50&outputFormat=application%2Fjson" +
//                            "&CQL_FILTER=range_id='"+strings[1]+"'";

                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
                            "PCCFWILDLIFE1%3Arange_boundary&maxFeatures=50&outputFormat=application%2Fjson" +
                            "&CQL_FILTER=range_id='"+strings[0]+"'";
                }

                rangemapTypeName="";
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(geoServerUrl);
                response = httpClient.execute(httpGet);
                value = EntityUtils.toString(response.getEntity());

            }catch (Exception e){
                if (progress_bar_LL.getVisibility()==View.VISIBLE){
                    progress_bar_LL.setVisibility(View.GONE);
                }
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                rangeJsonResponse=new JSONObject(s);

                try{
                    //for putting GeoJsonLayer on Map with json response coming from geoserver
                    rangeLayer=new GeoJsonLayer(map,rangeJsonResponse);
//                GeoJsonLayer layer=new GeoJsonLayer(map,R.raw.odisha_div,context);

                    callToGetLatLngRangeBoundary(rangeJsonResponse);

                    if (progress_bar_LL.getVisibility()==View.VISIBLE){
                        progress_bar_LL.setVisibility(View.GONE);
                    }


                    GeoJsonPolygonStyle polyStyle =rangeLayer.getDefaultPolygonStyle();
                    polyStyle.setStrokeColor(getResources().getColor(R.color.range_boundary_color));
                    polyStyle.setFillColor(getResources().getColor(R.color.range_boundary_transparent));
                    polyStyle.setStrokeWidth(6);
                    rangeLayer.addLayerToMap();


                }catch(Exception e) {
                    if (progress_bar_LL.getVisibility()==View.VISIBLE){
                        progress_bar_LL.setVisibility(View.GONE);
                    }
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                if (progress_bar_LL.getVisibility()==View.VISIBLE){
                    progress_bar_LL.setVisibility(View.GONE);
                }
                e.printStackTrace();
            }
        }
    }

    public void callToGetLatLngRangeBoundary(JSONObject geoJson){
        try {
            ArrayList<LatLng> listdata = new ArrayList<LatLng>();
            JSONArray feature_jsonArray = rangeJsonResponse.getJSONArray("features");
            JSONObject geometry_json=feature_jsonArray.getJSONObject(0).getJSONObject("geometry");

            JSONArray coordinate_jsonArray = geometry_json.getJSONArray("coordinates");
            JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
            first_jsonArray=coordinate_jsonArray.getJSONArray(0);
            JSONObject properties_json=feature_jsonArray.getJSONObject(0).getJSONObject("properties");
//            String name=properties_json.getString("divn_name");
            try {
                circle_name = properties_json.getString("circle");
                divn_name = properties_json.getString("divn_name");
                range_name = properties_json.getString("rng_name");
                area_sq = properties_json.getString("area_sqkm");
                area_ll.setVisibility(View.VISIBLE);
            }catch (Exception e){
                divn_name = properties_json.getString("division");
                range_name = properties_json.getString("range");
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
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }

            info_window_ll.setVisibility(View.GONE);
            rangeLayer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
                @Override
                public void onFeatureClick(GeoJsonFeature geoJsonFeature) {
                    info_window_ll.setVisibility(View.VISIBLE);

                    top_tv.setText("Layer Info");
                    div_ll.setVisibility(View.VISIBLE);
                    rng_ll.setVisibility(View.VISIBLE);
                    cir_ll.setVisibility(View.VISIBLE);
                    circle_nm.setText(circle_name);
                    div.setText(divn_name);
                    rng.setText(range_name);
                    area.setText(area_sq +" sq.km");

                    section_ll.setVisibility(View.GONE);
                    beat_ll.setVisibility(View.GONE);
                }
            });

            map.moveCamera(CameraUpdateFactory.newLatLng(layer_latlng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(layer_latlng, 9));
            map.setMaxZoomPreference(30.0f);


        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void getAllDivision(){
        try {
            if (PermissionUtils.check_InternetConnection(context) == "true") {

                RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);
                progress_bar_LL_filter.setVisibility(View.VISIBLE);
                retrofitInterface.getAlldivision().enqueue(new Callback<ArrayList<AllDivisionModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<AllDivisionModel>> call, Response<ArrayList<AllDivisionModel>> response) {

                        if (response.isSuccessful()){
                            progress_bar_LL_filter.setVisibility(View.GONE);
                            divisionDataByCircleIdArrayList.clear();
                            divisionDataByCircleIdArrayList.add(new AllDivisionModel("-1","Select Division"));
                            for (int i=0;i<response.body().size();i++) {
                                divisionDataByCircleIdArrayList.add(new AllDivisionModel(response.body().get(i).getDivisionId(),response.body().get(i).getDivisionName()));
                            }

                            div_dataAdapter = new ArrayAdapter<AllDivisionModel>(context,
                                    android.R.layout.simple_spinner_dropdown_item, divisionDataByCircleIdArrayList);
                            div_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            division_spinner.setAdapter(div_dataAdapter);

                            division_spinner.setSelection(selectedDiv);


                        }else {
                            progress_bar_LL_filter.setVisibility(View.GONE);
                            Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<AllDivisionModel>> call, Throwable t) {
                        progress_bar_LL_filter.setVisibility(View.GONE);
                        Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                progress_bar_LL_filter.setVisibility(View.GONE);
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }



        }catch (Exception e){
            progress_bar_LL_filter.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    private void getAllRangeByDivisionId(String DivId,String type) {
        try {

            if (PermissionUtils.check_InternetConnection(context) == "true") {

                RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);
                progress_bar_LL_filter.setVisibility(View.VISIBLE);
                retrofitInterface.getAllRangeByDivid(DivId).enqueue(new Callback<ArrayList<RangeDataByDivId>>() {
                    @Override
                    public void onResponse(Call<ArrayList<RangeDataByDivId>> call, Response<ArrayList<RangeDataByDivId>> response) {

                        if (response.isSuccessful()){

                            progress_bar_LL_filter.setVisibility(View.GONE);
                            rangeDataByDivIdArrayList.clear();
                            rangeDataByDivIdArrayList.add(new RangeDataByDivId("-1","Select Range"));
                            for (int i=0;i<response.body().size();i++) {
                                rangeDataByDivIdArrayList.add(response.body().get(i));
                            }

                            range_dataAdapter = new ArrayAdapter<RangeDataByDivId>(context,
                                    android.R.layout.simple_spinner_dropdown_item, rangeDataByDivIdArrayList);
                            range_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            range_spinner.setAdapter(range_dataAdapter);

                            range_spinner.setSelection(selectedRange);
                        }
                        else {
                            progress_bar_LL_filter.setVisibility(View.GONE);
                            Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<RangeDataByDivId>> call, Throwable t) {
                        progress_bar_LL_filter.setVisibility(View.GONE);
                        Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });


            }else {
                progress_bar_LL_filter.setVisibility(View.GONE);
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            progress_bar_LL_filter.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public class CallMapAnalytics extends AsyncTask<String ,String ,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (progress_bar_LL.getVisibility()==View.VISIBLE){
//                progress_bar_LL.setVisibility(View.GONE);
//            }
            progress_bar_LL.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String value="";
            HttpResponse response;
            try {

//                 map_analyticsUrl="http://14.98.253.214:8087/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&" +
//                        "typeName=PCCFWILDLIFE1%3Aelephant_sighting&maxFeatures=50&outputFormat=application%2Fjson";
                String map_analyticsUrl = createAnalyticsUrl(strings[0],strings[1]);
                map_analyticsUrl = map_analyticsUrl.replace(" ", "%20");
                mapTypeName="";

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(map_analyticsUrl);
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
                analyticsJsonResponse=new JSONObject(s);

                try{
                    //for putting GeoJsonLayer on Map with json response coming from geoserver
                    analyticsLayer=new GeoJsonLayer(map,analyticsJsonResponse);

//                    progress_bar_LL.setVisibility(View.GONE);

                    ArrayList<LatLng> listdata = new ArrayList<LatLng>();
                    JSONArray feature_jsonArray = analyticsJsonResponse.getJSONArray("features");
                    JSONObject geometry_json=null;
                    String analytics_year="";
//                    map.clear();
                    markers.clear();
                    if (feature_jsonArray.length()==0){
                        progress_bar_LL.setVisibility(View.GONE);
                        screenshot_img.setVisibility(View.GONE);//hide for screenshot
                        Snackbar.make(main_LL, "No data found !", Snackbar.LENGTH_LONG).show();

                    }
                    for (int i=0;i<feature_jsonArray.length();i++) {
                        try {
                            geometry_json = feature_jsonArray.getJSONObject(i).getJSONObject("geometry");

                            JSONArray coordinate_jsonArray = geometry_json.getJSONArray("coordinates");

                            double x = Double.parseDouble(coordinate_jsonArray.get(1).toString());
                            double y = Double.parseDouble(coordinate_jsonArray.get(0).toString());

                            listdata.add(new LatLng(y,x));

                            layer_latlng=new LatLng(y,x);//(latitude,longitude)- It will zoom by taking to this latlng

                            JSONObject properties_json=feature_jsonArray.getJSONObject(i).getJSONObject("properties");
                            analytics_year=properties_json.getString("yr");
                            Log.i("yearOnAnalytics",analytics_year);


                            if (analytics_year.equalsIgnoreCase("2018")){
                                marker=map.addMarker(new MarkerOptions().position(new LatLng(x,y))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2018)));

                            }else if (analytics_year.equalsIgnoreCase("2019")){
                                marker=map.addMarker(new MarkerOptions().position(new LatLng(x,y))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2019)));

                            }else if (analytics_year.equalsIgnoreCase("2020")){

                                marker=map.addMarker(new MarkerOptions().position(new LatLng(x,y))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2020)));

                            }
                            markers.add(marker);
                            progress_bar_LL.setVisibility(View.GONE);
                            screenshot_img.setVisibility(View.VISIBLE);//visible for screenshot



                        } catch (Exception e) {
                            e.printStackTrace();
                            screenshot_img.setVisibility(View.GONE);//hide for screenshot
                            progress_bar_LL.setVisibility(View.GONE);
                        }
                    }

                }catch(Exception e) {
                    e.printStackTrace();
                    screenshot_img.setVisibility(View.GONE);//hide for screenshot
                    progress_bar_LL.setVisibility(View.GONE);
                }



            }catch (Exception e){
                e.printStackTrace();
                screenshot_img.setVisibility(View.GONE);//hide for screenshot
                progress_bar_LL.setVisibility(View.GONE);
            }
        }
    }

    private void addPointToMap( GeoJsonPoint point,String year) {
        MarkerOptions markerOptions = new MarkerOptions();
      //  markerOptions=pointStyle.toMarkerOptions();
       try {
        if (year.equalsIgnoreCase("2018")){
//            pointStyle.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2018));
            markerOptions.position(point.getCoordinates())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2018));
            map.addMarker(markerOptions);

        }else if (year.equalsIgnoreCase("2019")){
//            pointStyle.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2019));
            markerOptions.position(point.getCoordinates())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2019));
            map.addMarker(markerOptions);
        }else if (year.equalsIgnoreCase("2020")){
            markerOptions.position(point.getCoordinates())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_2020));
            map.addMarker(markerOptions);
        }

       }catch (Exception e){
           e.printStackTrace();
       }
      // return map.addMarker(markerOptions);

    }

    public void initSpinnerList(){
        try {
            new_year_arrList.clear();
            new_year_arrList.add(new YearSpinnerCustomModel("All",0,"0"));
            new_year_arrList.add(new YearSpinnerCustomModel("2018",1,"1"));
            new_year_arrList.add(new YearSpinnerCustomModel("2019",2,"2"));
            new_year_arrList.add(new YearSpinnerCustomModel("2020",3,"3"));


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initMonthSpinnerList(){
        try {
            new_month_arrList.clear();
            new_month_arrList.add(new YearSpinnerCustomModel("All",0,"0"));
            new_month_arrList.add(new YearSpinnerCustomModel("January",1,"1"));
            new_month_arrList.add(new YearSpinnerCustomModel("February",2,"2"));
            new_month_arrList.add(new YearSpinnerCustomModel("March",3,"3"));
            new_month_arrList.add(new YearSpinnerCustomModel("April",4,"4"));
            new_month_arrList.add(new YearSpinnerCustomModel("May",5,"5"));
            new_month_arrList.add(new YearSpinnerCustomModel("June",6,"6"));
            new_month_arrList.add(new YearSpinnerCustomModel("July",7,"7"));
            new_month_arrList.add(new YearSpinnerCustomModel("August",8,"8"));
            new_month_arrList.add(new YearSpinnerCustomModel("September",9,"9"));
            new_month_arrList.add(new YearSpinnerCustomModel("October",10,"10"));
            new_month_arrList.add(new YearSpinnerCustomModel("November",11,"11"));
            new_month_arrList.add(new YearSpinnerCustomModel("December",12,"12"));


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callYearAdapter(Context context){
        try {
            year_recyclerV.setHasFixedSize(true);
            year_recyclerV.setNestedScrollingEnabled(false);

            layoutManager=new LinearLayoutManager(getActivity());
            year_recyclerV.setLayoutManager(layoutManager);

//            initSpinnerList();

            customSpinnerYearMonthAdapter=new CustomSpinnerYearMonthAdapter(context,new_year_arrList,this,"year");
            year_recyclerV.setAdapter(customSpinnerYearMonthAdapter);
            customSpinnerYearMonthAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callMonthAdapter(Context context){
        try {
            year_recyclerV.setHasFixedSize(true);
            year_recyclerV.setNestedScrollingEnabled(false);

            layoutManager=new LinearLayoutManager(getActivity());
            year_recyclerV.setLayoutManager(layoutManager);

            customSpinnerYearMonthAdapter=new CustomSpinnerYearMonthAdapter(context,new_month_arrList,this,"month");
            year_recyclerV.setAdapter(customSpinnerYearMonthAdapter);
            customSpinnerYearMonthAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callYearMonthListDialog(Context context,String type){
        try {
            year_month_dialog=new Dialog(context);
            year_month_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            year_month_dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
            year_month_dialog.setContentView(R.layout.year_month_list_layout);

            list_ll=year_month_dialog.findViewById(R.id.list_ll);
            submit_txt=year_month_dialog.findViewById(R.id.submit_txt);
            year_recyclerV=year_month_dialog.findViewById(R.id.year_recyclerV);
            close_img=year_month_dialog.findViewById(R.id.close_list_img);

            if (type.equalsIgnoreCase("year")){
                callYearAdapter(context);
            }else {
                callMonthAdapter(context);
            }

            submit_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(type.equalsIgnoreCase("year")) {
                        showText="";
                        for (int i = 0; i < new_year_arrList.size(); i++) {
                            if (i != 0 && new_year_arrList.get(i).isChecked()) {
                                if (showText == "") {
                                    showText = showText + new_year_arrList.get(i).getSelected_str();
                                } else {
                                    showText = showText + "," + new_year_arrList.get(i).getSelected_str();
                                }
                            }
                        }
                        year_edit.setText(showText);
                    }else{
                        month_text="";
                        for (int i = 0; i < new_month_arrList.size(); i++) {
                            if (i != 0 && new_month_arrList.get(i).isChecked()) {
                                if (month_text == "") {
                                    month_text = month_text + new_month_arrList.get(i).getSelected_str();
                                } else {
                                    month_text = month_text + "," + new_month_arrList.get(i).getSelected_str();
                                }
                            }
                        }
                        month_txt.setText(month_text);
                    }

                    year_month_dialog.dismiss();

                }
            });
            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    year_month_dialog.dismiss();
                }
            });

            year_month_dialog.show();
            Window window = year_month_dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String createAnalyticsUrl(String divn_code,String rng_code){
        String url  = map_analyticsUrl;
        try {


            ArrayList<YearSpinnerCustomModel> year = new ArrayList<>();
            ArrayList<YearSpinnerCustomModel> month = new ArrayList<>();

            for (int i =0;i<new_year_arrList.size();i++){
                if(i!=0 && new_year_arrList.get(i).isChecked()){
                    year.add(new_year_arrList.get(i));
                }
            }

            for (int i =0;i<new_month_arrList.size();i++){
                if(i!=0 && new_month_arrList.get(i).isChecked()){
                    month.add(new_month_arrList.get(i));
                }
            }

            if(!year_edit.getText().toString().equalsIgnoreCase("Select year") &&
                    !month_txt.getText().toString().equalsIgnoreCase("Select Month")){

                if(selectedRange != 0 && selectedRange != -1){

                    for(int i=0;i<year.size();i++){

                        if(i != 0){
                            url = url + " OR ";
                        }

                        for (int j =0;j<month.size();j++){

                            if(j != 0){
                                url = url + " OR ";
                            }

                            url = url + "divn_code=" + divn_code + " AND rng_code=" + rangeCode + " AND yr=" +
                                    year.get(i).getSelected_str() + " AND mt=" + month.get(j).getValue();

                        }

                    }
                }else{
                    for(int i=0;i<year.size();i++){

                        if(i != 0){
                            url = url + " OR ";
                        }

                        for (int j =0;j<month.size();j++){

                            if(j != 0){
                                url = url + " OR ";
                            }

                            url = url + "divn_code=" + divn_code  + " AND yr=" +
                                    year.get(i).getSelected_str() + " AND mt=" + month.get(j).getValue();

                        }

                    }
                }
            }else if(!year_edit.getText().toString().equalsIgnoreCase("Select year")){
                if(selectedRange != 0 && selectedRange != -1){
                    for(int i=0;i<year.size();i++){

                        if(i != 0){
                            url = url + " OR ";
                        }

                        url = url + "divn_code=" + divn_code + " AND rng_code=" + rangeCode + " AND yr=" +
                                year.get(i).getSelected_str();

                    }
                }else{
                    for(int i=0;i<year.size();i++){

                        if(i != 0){
                            url = url + " OR ";
                        }

                        url = url + "divn_code=" + divn_code  + " AND yr=" +
                                year.get(i).getSelected_str();

                    }

                }

            }else if(!month_txt.getText().toString().equalsIgnoreCase("Select Month")){
                if(selectedRange != 0 && selectedRange != -1){
                    for (int j =0;j<month.size();j++){

                        if(j != 0){
                            url = url + " OR ";
                        }

                        url = url + "divn_code=" + divn_code + " AND rng_code=" + rangeCode
                                + " AND mt=" + month.get(j).getValue();

                    }
                }else{
                    for (int j =0;j<month.size();j++){

                        if(j != 0){
                            url = url + " OR ";
                        }

                        url = url + "divn_code=" + divn_code
                                + " AND mt=" + month.get(j).getValue();

                    }
                }
            }else if(selectedRange != 0 && selectedRange != -1){
                url = url + "divn_code=" + divn_code + " AND rng_code=" + rng_code;
            }else{
                url = url + "divn_code=" + divn_code;
            }

            System.out.println(url);
            Log.d("CQL_URL",url);


        }catch (Exception e){
            e.printStackTrace();
        }
        return url;

    }
    public void captureAndStoreImage(Bitmap bitmap,String path){
        try {
            // Assume block needs to be inside a Try/Catch block.
//            String path_file = Environment.getExternalStorageDirectory().toString();
            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();
            OutputStream fOut = null;
            Random random=new Random();
//            int counter=random.nextInt(10000);
            long time= System.currentTimeMillis();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File file=null;
           try {
               file = new File(dir, "mapAnalytics_"+timeStamp+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.

               fOut = new FileOutputStream(file);
           }catch (Exception e){
               e.printStackTrace();
           }

//            Bitmap pictureBitmap = getImageBitmap(myurl); // obtaining the Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

            open_file_in_gallery=new File("");
            open_file_in_gallery=file;
//            openScreenshotinGallery(file);

            Toast.makeText(context, "Screenshot saved !", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(context, "Screenshot not saved !", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void animationScreenshot(){
        try {
            seconds=4;
            AlphaAnimation fade = new AlphaAnimation(1, 0);
            fade.setDuration(50);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
//                    screenshot_txt.setVisibility(View.GONE);
////                    screenshot.setVisibility(View.GONE);
//                    frameLL.setVisibility(View.GONE);
                    try {
                        handler.postDelayed(runnable=new Runnable() {
                            @Override
                            public void run() {
                                handler.postDelayed(runnable,1000);
                                if (seconds==0){
                                    screenshot.setVisibility(View.GONE);
                                    frameLL.setVisibility(View.GONE);
                                    handler.removeCallbacks(runnable);
//                                    mSnapshot=null;
                                }
                                seconds--;
                            }
                        },100);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            frameLL.startAnimation(fade);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void openScreenshotinGallery(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public class CallMapCircleLayer extends AsyncTask<String ,String ,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }
            progress_bar_LL.setVisibility(View.VISIBLE);
            progress_txt.setText("Fetching layer...Please wait !");
        }

        @Override
        protected String doInBackground(String... strings) {
            String value="";
            HttpResponse response;
            try {

//                String geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature" +
//                        "&typeName=PCCFWILDLIFE1%circle_boundary%20boundary&maxFeatures=50&outputFormat=application%2Fjson";

                String geoServerUrl="";
                if (strings[0].equals("All") || strings[0].equalsIgnoreCase("-1")){

                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature" +
                            "&typeName=PCCFWILDLIFE1%3Acircle_boundary&maxFeatures=50&outputFormat=application%2Fjson";
                }else {
                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature" +
                            "&typeName=PCCFWILDLIFE1%3Acircle_boundary&maxFeatures=50&outputFormat=application%2Fjson" +
                            "&CQL_FILTER=circle_id='"+strings[0]+"'";
                }

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
                circleJsonResponse=new JSONObject(s);

                try{
                    //for putting GeoJsonLayer on Map with json response coming from geoserver
                    circleLayer=new GeoJsonLayer(map,circleJsonResponse);
//                    callToGetLatlngFromBoundary(circleJsonResponse,6);//Get latlng to zoom from state boundary

                    callToGetLatLngCircleBoundary(circleJsonResponse,5);

                    GeoJsonPolygonStyle polyStyle =circleLayer.getDefaultPolygonStyle();
                    polyStyle.setStrokeColor(getResources().getColor(R.color.circle_boundary_color));
                    polyStyle.setStrokeWidth(5);
//                    if (circleLayer!=null){
//                        circleLayer.removeLayerFromMap();
//                    }else {
//                        try {
//                            circleLayer.removeLayerFromMap();
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
                    circleLayer.addLayerToMap();

                }catch(Exception e) {
                    progress_bar_LL.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                progress_bar_LL.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }
    public void callToGetLatLngCircleBoundary(JSONObject geoJson,float zoom){
        try {
            ArrayList<LatLng> listdata = new ArrayList<LatLng>();
            JSONArray feature_jsonArray = geoJson.getJSONArray("features");
            JSONObject geometry_json=feature_jsonArray.getJSONObject(0).getJSONObject("geometry");

            JSONArray coordinate_jsonArray = geometry_json.getJSONArray("coordinates");
            JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
            first_jsonArray=coordinate_jsonArray.getJSONArray(0);
            JSONObject properties_json=feature_jsonArray.getJSONObject(0).getJSONObject("properties");
//            String name=properties_json.getString("divn_name");
//            try {
//                divn_name = properties_json.getString("divn_name");
//                range_name = properties_json.getString("rng_name");
//                area_sq = properties_json.getString("area_sqkm");
//                area_ll.setVisibility(View.VISIBLE);
//            }catch (Exception e){
//                divn_name = properties_json.getString("division");
//                range_name = properties_json.getString("range");
//                area_ll.setVisibility(View.GONE);
//                e.printStackTrace();
//            }
            second_jsonArr = first_jsonArray.getJSONArray(0);

            for (int j = 0; j < second_jsonArr.length(); j++) {
                third_jsonArr = second_jsonArr.getJSONArray(j);
                String[] coord = third_jsonArr.toString().split(",");
                double x = Double.parseDouble(coord[0].replace("[",""));
                double y = Double.parseDouble(coord[1].replace("]",""));
                listdata.add(new LatLng(y,x));
                layer_latlng=new LatLng(y,x);//(latitude,longitude)- It will zoom by taking to this latlng
            }
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }

//            info_window_ll.setVisibility(View.GONE);
//            circleLayer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
//                @Override
//                public void onFeatureClick(GeoJsonFeature geoJsonFeature) {
//                    info_window_ll.setVisibility(View.VISIBLE);
//
//                    top_tv.setText("Layer Info");
//                    div_ll.setVisibility(View.VISIBLE);
//                    rng_ll.setVisibility(View.VISIBLE);
//                    div.setText(divn_name);
//                    rng.setText(range_name);
//                    area.setText(area_sq +" sq.km");
//
//                    elephant_ll.setVisibility(View.GONE);
//                    section_ll.setVisibility(View.GONE);
//                    beat_ll.setVisibility(View.GONE);
//                }
//            });


            map.moveCamera(CameraUpdateFactory.newLatLng(layer_latlng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(layer_latlng, zoom));
            map.setMaxZoomPreference(30.0f);


        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
