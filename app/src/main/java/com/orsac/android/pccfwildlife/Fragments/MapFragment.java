package com.orsac.android.pccfwildlife.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;

import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;
import com.orsac.android.pccfwildlife.Maps.DirectionsJSONParser;
import com.orsac.android.pccfwildlife.Activities.LoginActivity;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllCircleData;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.DivisionDataByCircleId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.RangeDataByDivId;
import com.orsac.android.pccfwildlife.Model.AllLatLngModel;
import com.orsac.android.pccfwildlife.Model.AllLayerModel.CircleRequestObj;
import com.orsac.android.pccfwildlife.Model.AllLayerModel.DivisionRequestobj;
import com.orsac.android.pccfwildlife.Model.AllLayerModel.MapDataResponse;
import com.orsac.android.pccfwildlife.Model.AllLayerModel.RangeRequestObj;
import com.orsac.android.pccfwildlife.Model.AllLayerModel.StGeojsonObj;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener ,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Context context;
    GoogleMap map;
    ConstraintLayout main_ll;
    private static final int LOC_PERM_REQ_CODE = 1;
    //meters
    private static final int GEOFENCE_RADIUS = 500;
    //in milli seconds
    private static final int GEOFENCE_EXPIRATION = 6000;
    private GeofencingClient geofencingClient;
    ArrayList<AllLatLngModel> latLngs_arr;
    ArrayList<ViewReportItemData_obj> alllatLngs_arr;
    Location location;
    double latitude=0.0,longitude=0.0;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    public LocationRequest locationRequest;
    LocationSettingsRequest.Builder locationSettingsRequest;
    PendingResult<LocationSettingsResult> pendingResult;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    LatLng current_latLng=new LatLng(0.0,0.0);
    AppCompatImageView filter_img,up_img,map_filter_img,screenshot_img;
    LatLng latLng=null,layer_latlng=null,range_layer_latlng=null;
    String divisionId="",roles="",roleID="",circleId="",divId="",rangeId="",divisionNm="";
    SessionManager session;
    JSONObject geoJsonResponse,state_JsonResponse,rangeJsonResponse,circleJsonResponse;
    String mapTypeName="",rangemapTypeName="";
    SearchableSpinner division_spinner,range_spinner,circle_spinner,section_spinner,beat_spinner;
    AppCompatSpinner direct_indirect_spinner;
    String divisionValue="", rangeValue="", secValue="",circleValue="",from_dateSelected="",to_dateSelected="",fromDate_alreadySelected="",toDate_alreadySelected="";
    ArrayList<AllCircleData> circleDataArrayList;
    ArrayList<DivisionDataByCircleId> divisionDataByCircleIdArrayList;
    ArrayList<RangeDataByDivId> rangeDataByDivIdArrayList;
    GeoJsonLayer divisionLayer=null,rangeLayer=null,stateLayer=null,circleLayer=null;
    LinearLayout circle_ll,division_ll,range_ll,progress_bar_LL;
    TextView satellite_txt,street_txt,progress_txt,title_dialog,divisionTxt;
    LinearLayout filter_LL,map_type_LL,circle_div_LL,beat_LL,progress_bar_LL_filter,info_window_ll,div_ll,rng_ll,
            section_ll,beat_ll,cir_ll,
            area_ll,elephant_ll;
    public String circleCode="All",divCode="All", rangeCode="All",secCode="All", beatCode="All",reportType="direct";
    CardView submit_CV;
    Dialog filter_dialog;
    TextInputLayout report_type_spinnerTIL;
    TextView fromDate, toDate,div,rng,section,beat,area,totalNo,herdNo,circle_nm;
    private int mYear, mMonth, mDay;
    String m, d;
    Calendar mcalender = Calendar.getInstance();
    int selecetedCircle=0,selectedDiv=0,selectedRange=0;
    ArrayAdapter<AllCircleData> circle_dataAdapter;
    ArrayAdapter<DivisionDataByCircleId> div_dataAdapter;
    ArrayAdapter<RangeDataByDivId> range_dataAdapter;
    ArrayList<Marker> markerArr;
    Marker marker=null;
    String divn_name="",area_sq="",range_name="" ,divn_nm="",rng_nm="",total="",herd="",circle_name="";
    Long areaSquare=0l;
    private Bitmap mSnapshot;
    RelativeLayout frameLL;
    TextView screenshot_txt,top_tv;
    ImageView screenshot;
    Runnable runnable;
    int seconds;
    Handler handler=new Handler();
    File open_file_in_gallery=null;
//    public String map_url="http://14.98.253.214:8087";//old geoserver link
    public String mmap_url="http://164.164.122.69:8080";//geoserver link(state layer)
    public String map_url="http://mapserver.odishaforestgis.in/geoserver/";//geoserver link
    public String new_map_url="https://odishaforestgis.in/ofms/index.php/webservice_api/";//geoserver link
    int index = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapview_fragment, container, false);
        try {

            SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);

            main_ll=view.findViewById(R.id.main_ll);
            filter_img=view.findViewById(R.id.filter_img);
            up_img=view.findViewById(R.id.up_img);
            map_filter_img=view.findViewById(R.id.map_img);
            filter_LL=view.findViewById(R.id.filter_LL);
            map_type_LL=view.findViewById(R.id.map_type_LL);
            circle_ll=view.findViewById(R.id.circle_ll);
            division_ll=view.findViewById(R.id.division_ll);
            range_ll=view.findViewById(R.id.range_ll);
            progress_bar_LL=view.findViewById(R.id.progress_bar_LL);
            satellite_txt=view.findViewById(R.id.satellite_txt);
            street_txt=view.findViewById(R.id.street_txt);
            progress_txt=view.findViewById(R.id.progress_txt);

            info_window_ll=view.findViewById(R.id.info_window_ll);
            div_ll=view.findViewById(R.id.div_ll);
            rng_ll=view.findViewById(R.id.rng_ll);
            cir_ll=view.findViewById(R.id.cir_ll);
            section_ll=view.findViewById(R.id.section_ll);
            beat_ll=view.findViewById(R.id.beat_ll);
            area_ll=view.findViewById(R.id.area_ll);
            elephant_ll=view.findViewById(R.id.elephant_ll);
            circle_nm=view.findViewById(R.id.circle_nm);
            div=view.findViewById(R.id.div);
            rng=view.findViewById(R.id.rng);
            section=view.findViewById(R.id.section);
            beat=view.findViewById(R.id.beat);
            area=view.findViewById(R.id.area);
            totalNo=view.findViewById(R.id.total);
            herdNo=view.findViewById(R.id.herd);
            frameLL=view.findViewById(R.id.frameLL);
            screenshot_img=view.findViewById(R.id.screenshot_img);
            screenshot=view.findViewById(R.id.screenshot);
            screenshot_txt=view.findViewById(R.id.screenshot_txt);
            top_tv=view.findViewById(R.id.top_tv);

            geofencingClient = LocationServices.getGeofencingClient(context);
            latLngs_arr=new ArrayList<>();
            alllatLngs_arr=new ArrayList<>();
            circleDataArrayList=new ArrayList<>();
            divisionDataByCircleIdArrayList=new ArrayList<>();
            rangeDataByDivIdArrayList=new ArrayList<>();
            markerArr=new ArrayList<>();

            circleDataArrayList.clear();
            circleDataArrayList.add(new AllCircleData("-1","Select Circle"));

            divisionDataByCircleIdArrayList.clear();
            divisionDataByCircleIdArrayList.add(new DivisionDataByCircleId("-1","Select Division"));

            rangeDataByDivIdArrayList.clear();
            rangeDataByDivIdArrayList.add(new RangeDataByDivId("-1","Select Range"));
            screenshot_img.setVisibility(View.GONE);//hide for screenshot

            getLocation();
            session = new SessionManager(context);
            divisionId=session.getJuridictionID();
            roles=session.getRoles();
            roleID=session.getRoleId();
            circleId=session.getCircleId();
            divId=session.getDivisionId();
            divisionNm=session.getJuridiction();
            rangeId = session.getRangeId();
            from_dateSelected=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
            to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");

            if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")||
                    roleID.equalsIgnoreCase("6")){

                filter_img.setVisibility(View.VISIBLE);

            }else if (roleID.equalsIgnoreCase("2")){ //DFO

                circleCode=circleId;
                divCode=divId;
                filter_img.setVisibility(View.VISIBLE);
                up_img.setVisibility(View.GONE);
                circle_ll.setVisibility(View.GONE);
                division_ll.setVisibility(View.GONE);
                range_ll.setVisibility(View.VISIBLE);

            }
            click_function();
            new CallMapStateLayer().execute();

            //for offline map
//            TileProvider coordTileProvider = new OfflineMapClass(context);
//            map.addTileOverlay(new TileOverlayOptions().tileProvider(coordTileProvider));

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

                    showFilterDialog(context,PermissionUtils.getCurrentDateMinusOne("dd-MM-yyyy"),
                            PermissionUtils.getCurrentDate("dd-MM-yyyy"));
                }
            });


            screenshot_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (divisionValue.equalsIgnoreCase("")){
                            Snackbar.make(main_ll, "Please add data to take screenshot !", Snackbar.LENGTH_LONG).show();
                        }
                        else {
                            frameLL.setVisibility(View.VISIBLE);
                            screenshot.setVisibility(View.VISIBLE);
                            //For taking screenshot
                            GoogleMap.SnapshotReadyCallback callback=new GoogleMap.SnapshotReadyCallback() {
                                @Override
                                public void onSnapshotReady(Bitmap bitmap) {
//                                    String path="";
                                    try {
//                                        if(Build.VERSION.SDK_INT >= 29) {
//
//                                            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                                        }else {
//                                            path = Environment.getExternalStorageDirectory()
//                                                    .getAbsolutePath() + "/WildlifeMapScreenshots/";
//                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    screenshot.setImageBitmap(bitmap);
                                    mSnapshot = bitmap;
                                    captureAndStoreImage(bitmap);//for storing in file
//                                    captureAndStoreImage(bitmap,path);//for storing in file

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
                    if (info_window_ll.getVisibility()==View.VISIBLE){
                        info_window_ll.setVisibility(View.GONE);
                    }else {
                        info_window_ll.setVisibility(View.VISIBLE);
                    }

                }
            });


            map_filter_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (map_type_LL.getVisibility()==View.VISIBLE){
                        map_type_LL.setVisibility(View.GONE);
                    }else {
                        map_type_LL.setVisibility(View.VISIBLE);
                    }

                }
            });

            satellite_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    map_type_LL.setVisibility(View.GONE);
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            });
            street_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    map_type_LL.setVisibility(View.GONE);
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
//        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Add a marker  and move the camera
        LatLng elephant_report = new LatLng(20.2961, 85.8245);

        for (int i=0;i<latLngs_arr.size();i++){

            LatLng latLng=new LatLng(Double.parseDouble(latLngs_arr.get(i).getLatitude()),
                                    Double.parseDouble(latLngs_arr.get(i).getLongitude()));
            map.addMarker(new
                    MarkerOptions().position(latLng)
                    .title(""+latLngs_arr.get(i))
//                    .title("Elephant Report")
                    .flat(true)
                    .rotation(5))
                    .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker));
        }

        map.moveCamera(CameraUpdateFactory.newLatLng(elephant_report));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(elephant_report, 5));
        map.setMaxZoomPreference(30.0f);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void addLocationAlert(double lat, double lng,String loc_name) {
        if (isLocationAccessPermitted()) {
            requestLocationAccessPermission();
        } else {
            String key = "" + lat + "-" + lng;
            Geofence geofence = getGeofence(lat, lng, key);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            geofencingClient.addGeofences(getGeofencingRequest(geofence), getGeofencePendingIntent())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context,
                                        "Location change has been added",
                                        Toast.LENGTH_SHORT).show();
//                                notifyLocationAlert("Location Change",loc_name);//Show notification
                            } else {
                                Toast.makeText(context,
                                        "Location alter could not be added",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private Geofence getGeofence(double lat, double lang, String key) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lang, GEOFENCE_RADIUS)
                .setExpirationDuration(GEOFENCE_EXPIRATION)
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(10000)
                .build();
    }
    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(geofence);
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, LoginActivity.class);
        return PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
    private boolean isLocationAccessPermitted(){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED;
    }
    private void requestLocationAccessPermission(){
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                LOC_PERM_REQ_CODE);
    }

    private void notifyLocationAlert(String locTransitionType, String locationDetails) {

        String CHANNEL_ID = "Elephant Tracking System";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.elephant_img)
                        .setContentTitle(locTransitionType)
                        .setContentText(locationDetails);

        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());
    }

    public double calculateDistance(double lat1,double lat2,double longi1,double longi2){

        lat1=Math.toRadians(lat1);
        lat2=Math.toRadians(lat2);
        longi1=Math.toRadians(longi1);
        longi2=Math.toRadians(longi2);

        // Haversine formula
        double dlon = longi2 - longi1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

    //location code start
    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {
                try {
                    if (PermissionUtils.check_InternetConnection(context).equalsIgnoreCase("false")){
                        mEnableGps(context);
                    }else {
                        mEnableGps(context);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            } else {
                this.canGetLocation = true;
                mEnableGps(context);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //-- For direction url and show direction polyline in map

    public class DownloadTask extends AsyncTask<String,Integer,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);
        }

    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap>> result) {
            try {

            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble(""+point.get("lat"));
                    double lng = Double.parseDouble(""+point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(getResources().getColor(R.color.lightest_green));
                lineOptions.geodesic(true);

            }
// Drawing polyline in the Google Map for the i-th route

            if (lineOptions==null){
                Toast.makeText(context, "You exceed your limit for today !", Toast.LENGTH_SHORT).show();
            }else {
                map.addPolyline(lineOptions);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key="key=AIzaSyB-VJ5Dc0Wf-AzmvNlwd48GuzwBN25s8JQ";

        // Building the parameters to the web service
        String parameters = str_origin +
                        "&" + str_dest +
                        "&" + sensor +
                        "&" + mode +
                        "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    //added new for location
    public void mEnableGps(Context context){
        try {

        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        mLocationSetting();
    }catch (Exception e){
        e.printStackTrace();
    }

    }

    public void mLocationSetting() {
        try {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);

        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        mResult();
    }catch (Exception e){
        e.printStackTrace();
    }

    }
    public void mResult() {
        try {

        pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {

                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                            getActivity().finish();
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }

        });

    }catch (Exception e){
        e.printStackTrace();
    }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            startLocationUpdates();
        }
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            location.setAccuracy(7);

            current_latLng=new LatLng(latitude,longitude);

        } else {
        }

    }catch (Exception e){
        e.printStackTrace();
    }
    }

    @Override
    public void onConnectionSuspended(int i) {
        try {
        googleApiClient.connect();

    }catch (Exception e){
        e.printStackTrace();
    }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Location error","Location error " + connectionResult.getErrorCode());
    }

    public void startLocationUpdates() {
        try {

        // Create the location request
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(50000)
                .setFastestInterval(5000);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                locationRequest, this);
        Log.d("reque", "--->>>>");

    }catch (Exception e){
        e.printStackTrace();
    }

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
        googleApiClient.connect();
    }catch (Exception e){
        e.printStackTrace();
    }

    }

    @Override
    public void onStop() {
        super.onStop();
        try {

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }

    }catch (Exception e){
        e.printStackTrace();
    }

    }

    private void callFilterDialog(Context context){
        try {
            Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
            dialog.setContentView(R.layout.filter_dialog);

            ImageView close_img;
            AppCompatButton submit_btn;
            close_img=dialog.findViewById(R.id.close_img);
            submit_btn=dialog.findViewById(R.id.submit_btn);


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
                }
            });

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                    dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
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

//            if (divisionId.equalsIgnoreCase("-1")||divisionId.equalsIgnoreCase("All")){
//                new CallMapDivisionLayer().execute(divisionId,circleCode);
//            }else {
//                new CallMapDivisionLayer().execute(divisionId,"");
////                checkRangeIdForMapRangeBoundary(rangeCode);
//            }
            new CallMapDivisionLayer().execute(divisionId);

        }catch (Exception e){
            e.printStackTrace();
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
//            if (rangeId.equalsIgnoreCase("-1")||rangeId.equalsIgnoreCase("All")){
//                new CallMapRangeLayer().execute(rangeId,divCode);
//            }else {
//                new CallMapRangeLayer().execute(rangeId);
//            }
//            new CallMapRangeLayer().execute(rangeId);
            callMapApi(rangeId,"getRangeGeojson","range");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //For State layer
    public class CallMapStateLayer extends AsyncTask<String ,String ,String>{

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

                String geoServerUrl=mmap_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
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
                    callToGetLatlngFromBoundary(state_JsonResponse,6);//Get latlng to zoom from state boundary

                    GeoJsonPolygonStyle polyStyle =stateLayer.getDefaultPolygonStyle();
                    polyStyle.setStrokeColor(getResources().getColor(R.color.state_boundary_color));
                    polyStyle.setStrokeWidth(7);
                    stateLayer.addLayerToMap();

                    if (roleID.equalsIgnoreCase("2")){
                        divCode=divId;
                        circleCode=circleId;
//                        new CallMapCircleLayer().execute(circleCode);
                        callMapApi(circleCode,"getCircleGeojson","circle");
//                        checkDivisionIdForMapBoundary(divCode);
                        callMapApi(divCode,"getDivisionGeojson","division");
//                        loadAllMapDataAsPerCircleRangeDiv_Date(circleCode,divCode,rangeCode,secCode,beatCode,reportType,from_dateSelected,to_dateSelected);

                        getMapDatafromCircleDivRange(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected); //For All point i.e.direct and indirect

                    }
                    else if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")||
                            roleID.equalsIgnoreCase("6")) {

//                        loadAllMapDataAsPerCircleRangeDiv_Date(circleCode,divCode,rangeCode,secCode,beatCode,reportType,from_dateSelected,to_dateSelected);

                        getMapDatafromCircleDivRange(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected); //For All point i.e.direct and indirect
//                        new CallMapCircleLayer().execute(circleCode);
//                        callMapApi("getCircleGeojson","data",circleCode);

//                        callAllLayerMap();//hide for now

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
    //For Circle layer
    public class CallMapCircleLayer extends AsyncTask<String ,String ,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (progress_bar_LL.getVisibility()==View.VISIBLE){
//                progress_bar_LL.setVisibility(View.GONE);
//            }
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
                Spanned lessThan= Html.fromHtml("&lt;");
                Spanned greaterThan= Html.fromHtml("&gt;");

                JSONObject circle_json=new JSONObject();
                circle_json.put("circle_id",strings[0]);

                if (strings[0].equals("All") || strings[0].equalsIgnoreCase("-1")){

//                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature" +
//                            "&typeName=PCCFWILDLIFE1%3Acircle_boundary&maxFeatures=50&outputFormat=application%2Fjson";
//                    geoServerUrl=map_url+"odisha/ows?service=WFS&version=1.0.0&request=GetFeature&" +
//                            "typeName=odisha:master_area_circle&maxFeatures=50&outputFormat=application%2Fjson" +
//                           "&CQL_FILTER=NOT%20id=11%20and%20active=1";
                    geoServerUrl=new_map_url+"getCircleGeojson";
                }else {
//                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature" +
//                            "&typeName=PCCFWILDLIFE1%3Acircle_boundary&maxFeatures=50&outputFormat=application%2Fjson" +
//                            "&CQL_FILTER=circle_id='"+strings[0]+"'";
//                    geoServerUrl=map_url+"odisha/ows?service=WFS&version=1.0.0&request=GetFeature&" +
//                            "typeName=odisha:master_area_circle&maxFeatures=50&outputFormat=application%2Fjson" +
//                            "&CQL_FILTER=NOT%20id=11%20and%20active=1%20and%20id='"+strings[0]+"'";
//                            "&CQL_FILTER=id"+lessThan+greaterThan+"11 and active=1 and id='"+strings[0]+"'";
                    geoServerUrl=new_map_url+"getCircleGeojson";
//                            "&CQL_FILTER=id"+lessThan+greaterThan+"11 and active=1 and id='"+strings[0]+"'";
                }

                HttpClient httpClient = new DefaultHttpClient();
                String url=geoServerUrl.replace(" ", "%20");
                Log.i("url",url);
                HttpGet httpGet = new HttpGet(geoServerUrl);
                response = httpClient.execute(httpGet);
                value = EntityUtils.toString(response.getEntity());

            }catch (Exception e){
                e.printStackTrace();
                Log.i("error_url",e.toString());
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

                    if (!circleCode.equalsIgnoreCase("All")&&
                            !circleCode.equalsIgnoreCase("-1")){
                        callToGetLatLngCircleBoundary(circleJsonResponse,5);//for single selection circle zoom
                    }else {
                        callToGetLatLngCircleBoundary(circleJsonResponse,6);//for all circle zoom
                    }

                    GeoJsonPolygonStyle polyStyle =circleLayer.getDefaultPolygonStyle();
                    polyStyle.setStrokeColor(getResources().getColor(R.color.circle_boundary_color));
                    polyStyle.setStrokeWidth(5);
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

    //For Division layer
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
                if (strings[0].equals("-1") ||strings[0].equalsIgnoreCase("All")){
//                     geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/wfs?service=WFS&version=1.1.0&request=GetFeature&typeName="
//                        +strings[0]+"&maxFeatures=2000&outputFormat=application/json";

                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
                            "PCCFWILDLIFE1%3Adivision_boundary&maxFeatures=50&outputFormat=application%2Fjson%20" +
                            "&CQL_FILTER=circle_id='"+strings[1]+"'";
                }else {
//                     geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
//                            "PCCFWILDLIFE1%3Aall_division_renew&maxFeatures=50&outputFormat=application%2Fjson%20" +
//                            "&CQL_FILTER=division_id='"+strings[1]+"'";

//                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
//                            "PCCFWILDLIFE1%3Adivision_boundary&maxFeatures=50&outputFormat=application%2Fjson%20" +
//                            "&CQL_FILTER=division_id='"+strings[0]+"'";
                    geoServerUrl=map_url+"odisha/ows?service=WFS&version=1.0.0&request=GetFeature&" +
                            "typeName=odisha:master_area_division&maxFeatures=50&outputFormat=application%2Fjson%20"
//                            +"&FeatureID="+1+"'";
                            +"&FeatureID="+Integer.parseInt(strings[0])+"'";
                }
                mapTypeName="";

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(geoServerUrl.replace(" ", "%20"));
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
//                        circle_name = properties_json.getString("circle");
//                        divn_name=properties_json.getString("divn_name");
//                        area_sq=properties_json.getString("area_sqkm");

//                        circle_name = properties_json.getString("name_e");
//                        circle_name = ;
                        divn_name=properties_json.getString("name_e");
                        area_sq=properties_json.getString("area");
//                        areaSquare=Long.parseLong(area_sq)/1000000;
//                        area_sq=properties_json.getString("area_sqkm");
                        area_ll.setVisibility(View.VISIBLE);
                    }catch (Exception e){
//                        divn_name=properties_json.getString("division");
                        divn_name=properties_json.getString("name_e");
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
                            div.setText(divn_name);
                            circle_nm.setText(circle_name);
                            String areaSqkm = String.format("%.2f", Double.parseDouble(area_sq)/1000000);
                            area.setText(areaSqkm +" sq.km");

                            elephant_ll.setVisibility(View.GONE);
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

    //For Range layer
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
                if (strings[0].equals("-1") ||strings[0].equalsIgnoreCase("All")){
//                if (!strings[0].equals("")){

//                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/wfs?service=WFS&version=1.1.0&request=GetFeature&typeName="
//                            +strings[0]+"&maxFeatures=2000&outputFormat=application/json"+"&CQL_FILTER=range_id='"+strings[1]+"'";

                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
                            "PCCFWILDLIFE1%3Arange_boundary&maxFeatures=50&outputFormat=application%2Fjson"+
                            "&CQL_FILTER=division_id='"+strings[1]+"'";
                }else {
//                     geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
//                            "PCCFWILDLIFE1%3Aall_new_range&maxFeatures=50&outputFormat=application%2Fjson" +
//                            "&CQL_FILTER=range_id='"+strings[1]+"'";

//                    geoServerUrl=map_url+"/geoserver/PCCFWILDLIFE1/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=" +
//                            "PCCFWILDLIFE1%3Arange_boundary&maxFeatures=50&outputFormat=application%2Fjson" +
//                            "&CQL_FILTER=range_id='"+strings[0]+"'";
                    geoServerUrl=map_url+"odisha/ows?service=WFS&version=1.0.0&request=GetFeature&" +
                            "typeName=odisha:master_area_range&maxFeatures=50&outputFormat=application%2Fjson" +
                            "&FeatureID="+Integer.parseInt(strings[0])+"'";
                }

                rangemapTypeName="";
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(geoServerUrl.replace(" ", "%20"));
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



    //For circle api ------------------
    private void callAllCircleApi() {
        try {
            RetrofitInterface retrofitInterface= RetrofitClient.getClient("").create(RetrofitInterface.class);
            progress_bar_LL_filter.setVisibility(View.VISIBLE);
            retrofitInterface.getAllCircle().enqueue(new Callback<ArrayList<AllCircleData>>() {
                @Override
                public void onResponse(Call<ArrayList<AllCircleData>> call, Response<ArrayList<AllCircleData>> response) {

                    if (response.isSuccessful()){
                        circleDataArrayList.clear();
                        progress_bar_LL_filter.setVisibility(View.GONE);
                        circleDataArrayList.add(new AllCircleData("-1","Select Circle"));
                        for (int i=0;i<response.body().size();i++) {

                            circleDataArrayList.add(response.body().get(i));
                        }
                        circle_dataAdapter = new ArrayAdapter<AllCircleData>(context, android.R.layout.simple_spinner_dropdown_item, circleDataArrayList);
                        circle_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                        circle_spinner.setAdapter(circle_dataAdapter);

                        circle_spinner.setSelection(selecetedCircle);


                    }else {
                        progress_bar_LL_filter.setVisibility(View.GONE);
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ArrayList<AllCircleData>> call, Throwable t) {
                    progress_bar_LL_filter.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            progress_bar_LL_filter.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    private void getAllDivisionByCircleId(String CircleId,String type){
        try {
            if (PermissionUtils.check_InternetConnection(context) == "true") {

                RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);
                progress_bar_LL_filter.setVisibility(View.VISIBLE);
                retrofitInterface.getAllDivisionByCircleId(CircleId).enqueue(new Callback<ArrayList<DivisionDataByCircleId>>() {
                    @Override
                    public void onResponse(Call<ArrayList<DivisionDataByCircleId>> call, Response<ArrayList<DivisionDataByCircleId>> response) {

                        if (response.isSuccessful()){

                            progress_bar_LL_filter.setVisibility(View.GONE);
                            divisionDataByCircleIdArrayList.clear();
                            divisionDataByCircleIdArrayList.add(new DivisionDataByCircleId("-1","Select Division"));
                            for (int i=0;i<response.body().size();i++) {
                                divisionDataByCircleIdArrayList.add(response.body().get(i));
                            }

                            div_dataAdapter = new ArrayAdapter<DivisionDataByCircleId>(context,
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
                    public void onFailure(Call<ArrayList<DivisionDataByCircleId>> call, Throwable t) {
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

    //-----------------

    private LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList){
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }
    private LatLngBounds getPolygonBounds(ArrayList<LatLng> polygonPointsList){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++) {
            builder.include(polygonPointsList.get(i));
        }
        return builder.build();
    }

    public void callToGetLatlngFromBoundary(JSONObject geoJson,float zoom){
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
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(layer_latlng, zoom));
            map.setMaxZoomPreference(30.0f);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callToGetLatLngRangeBoundary(JSONObject geoJson){
        try {
            ArrayList<LatLng> listdata = new ArrayList<LatLng>();
//            JSONArray feature_jsonArray = rangeJsonResponse.getJSONArray("features");
//            JSONObject geometry_json=feature_jsonArray.getJSONObject(0).getJSONObject("geometry");

            JSONArray coordinate_jsonArray = geoJson.getJSONArray("coordinates");

            JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
            first_jsonArray=coordinate_jsonArray.getJSONArray(0);
//            JSONObject properties_json=feature_jsonArray.getJSONObject(0).getJSONObject("properties");
//            String name=properties_json.getString("divn_name");
            try {
//                circle_name = properties_json.getString("circle");
//                divn_name = properties_json.getString("divn_name");
//                range_name = properties_json.getString("name_e");
//                area_sq = properties_json.getString("area");
//                area_sq = properties_json.getString("area_sqkm");
                cir_ll.setVisibility(View.VISIBLE);
                area_ll.setVisibility(View.VISIBLE);
            }catch (Exception e){
//                divn_name = properties_json.getString("division");
//                range_name = properties_json.getString("name_e");
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
                range_layer_latlng=new LatLng(y,x);//(latitude,longitude)- It will zoom by taking to this latlng
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
                    String areaSqkm = String.format("%.2f", Double.parseDouble(area_sq)/1000000);
                    area.setText(areaSqkm +" sq.km");

                    elephant_ll.setVisibility(View.GONE);
                    section_ll.setVisibility(View.GONE);
                    beat_ll.setVisibility(View.GONE);
                }
            });


            map.moveCamera(CameraUpdateFactory.newLatLng(range_layer_latlng));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(range_layer_latlng, 10));
            map.setMaxZoomPreference(30.0f);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Map point Api for direct and indirect
    private void getMapDatafromCircleDivRange(String circle,String division,String range,String startDate,String endDate){
        try {
            alllatLngs_arr.clear();

            if (circleCode.equalsIgnoreCase("-1")) {
                circle = "All";
            } else {
                circle = circleCode;
            }
            if (divCode.equalsIgnoreCase("-1")) {
                division = "All";
            } else {
                division = divCode;
            }
            if (rangeCode.equalsIgnoreCase("-1")) {
                range = "All";
            } else {
                range = rangeCode;
            }

            progress_bar_LL.setVisibility(View.VISIBLE);
            progress_txt.setText("Fetching points...please wait !");
            RetrofitInterface retrofitInterface=RetrofitClient.getReportClient().create(RetrofitInterface.class);

            retrofitInterface.getviewReportLast24ForMap(circle,division,range,startDate,endDate).enqueue(new Callback<ArrayList<ViewReportItemData_obj>>() {
                @Override
                public void onResponse(Call<ArrayList<ViewReportItemData_obj>> call, Response<ArrayList<ViewReportItemData_obj>> response) {

                    if (response.isSuccessful()){
                        progress_bar_LL.setVisibility(View.GONE);
                        if (response.body().isEmpty()){

//                            Toast.makeText(context, "You dont have any point to plot on Map !", Toast.LENGTH_SHORT).show();
                            Snackbar.make(main_ll, "No point to plot on Map !", Snackbar.LENGTH_LONG).show();
                            screenshot_img.setVisibility(View.GONE);
                        }else {
                            try {

                                for (int i = 0; i < response.body().size(); i++) {
                                    alllatLngs_arr.add(response.body().get(i));
                                }

                                markerArr.clear();
                                for (int i = 0; i < response.body().size(); i++) {
                                    if (response.body().get(i).getLatitude().equalsIgnoreCase("0.0")) {

                                    } else {
                                        latLng = new LatLng(Double.parseDouble(response.body().get(i).getLatitude()),
                                                Double.parseDouble(response.body().get(i).getLongitudes()));
                                    }

                                    if ((response.body().get(i).getDuplicateReport()!=null) &&
                                            (response.body().get(i).getDuplicateReport().equalsIgnoreCase("d"))){

                                        if (response.body().get(i).getReport_type().equalsIgnoreCase("Nill") ||
                                                response.body().get(i).getReport_type().equalsIgnoreCase("Nil"))
                                        {
                                            marker = map.addMarker(new MarkerOptions().position(latLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_duplicate)).visible(false));
                                        }else {
                                            marker = map.addMarker(new MarkerOptions().position(latLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_duplicate)).visible(false));//remove duplicate (22nd oct after meeting in orsac)
                                        }

                                    }
                                    else {

                                        if (response.body().get(i).getReport_type().equalsIgnoreCase("direct")) {

                                            marker = map.addMarker(new MarkerOptions().position(latLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker_direct)));

                                        } else if (response.body().get(i).getReport_type().equalsIgnoreCase("indirect")) {

                                            marker = map.addMarker(new MarkerOptions().position(latLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker_indirect)));
                                        }
                                        else if (response.body().get(i).getReport_type().equalsIgnoreCase("Nill") ||
                                                response.body().get(i).getReport_type().equalsIgnoreCase("Nil"))
                                        {
                                            if (latLng != null) {
                                            marker = map.addMarker(new MarkerOptions().position(latLng)
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)).visible(false));
                                            }
                                        }

                                    }
                                    try {
                                        marker.setTag(i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    markerArr.add(marker);

//                                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                                        @Override
//                                        public boolean onMarkerClick(Marker marker) {
////                                            try {
////                                                latLng = marker.getPosition();
////                                                int index = 0;
//                                                try {
//                                                    index = (int) marker.getTag();
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                }
//
//                                                info_window_ll.setVisibility(View.VISIBLE);
//                                                div_ll.setVisibility(View.VISIBLE);
//                                                rng_ll.setVisibility(View.VISIBLE);
//                                                elephant_ll.setVisibility(View.VISIBLE);
//                                                div.setText(alllatLngs_arr.get(index).getDivision());
//                                                rng.setText(alllatLngs_arr.get(index).getRange());
//                                                totalNo.setText(alllatLngs_arr.get(index).getTotal());
//                                                herdNo.setText(alllatLngs_arr.get(index).getHeard());
//
//                                                String report_type=alllatLngs_arr.get(index).getReport_type();
//                                                String _reportType="";
//
//                                                StringBuilder sb = new StringBuilder(report_type);
//                                                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
//                                                _reportType=sb.toString();
//
//                                                if (alllatLngs_arr.get(index).getDuplicateReport()!=null &&
//                                                        alllatLngs_arr.get(index).getDuplicateReport().equalsIgnoreCase("d")){
//
//                                                    top_tv.setVisibility(View.VISIBLE);
//                                                    if (_reportType.equalsIgnoreCase("nill")){
//                                                        top_tv.setText("Nil (Duplicate)");
//                                                    }else {
//                                                        top_tv.setText(_reportType+" (Duplicate)");
//                                                    }
//                                                }else {
//
//                                                    if (report_type.equalsIgnoreCase("nill")){
//                                                        top_tv.setText("Nil");
//                                                    }else {
//                                                        top_tv.setText(_reportType);
//                                                    }
//
//                                                }
//
//
//
//                                                area_ll.setVisibility(View.GONE);
//                                                section_ll.setVisibility(View.GONE);
//                                                beat_ll.setVisibility(View.GONE);
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                            return true;
//                                        }
//                                    });

                                    screenshot_img.setVisibility(View.VISIBLE);//screenshot icon is visible

//                                    map.setInfoWindowAdapter(new CustomInfoAdapter(getActivity(),
//                                            divn_nm,
//                                            rng_nm,
//                                            total,
//                                            herd));
//                                    if (!alllatLngs_arr.get(index).getReport_type().equalsIgnoreCase("Nill")
//                                    || !alllatLngs_arr.get(index).getReport_type().equalsIgnoreCase("Nil")){
//
//                                        map.setInfoWindowAdapter(new CustomInfoAdapter(getActivity(),
//                                                alllatLngs_arr,marker,index));
//                                    }

//                                }
                                    map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                        @Override
                                        public View getInfoWindow(Marker marker) {
                                            //here if we put the code for infowindow then it removes
                                            // space as it show in getInfoContents for layout but not show the info design
                                            return null;
                                        }

                                        @Override
                                        public View getInfoContents(Marker marker) {
                                            View view = getLayoutInflater().inflate(R.layout.custom_info_window_marker_layout, null);
                                            view.setLayoutParams(new ScrollView.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT));


                                            TextView circle_nm,division_nm, range_nm, section_nm, beat_nm,
                                                    nothing_selected,top_title,date_txt;
                                            LinearLayout total_ll, herd_ll,division_ll,range_ll,top_view;
                                            String reportType,_reportType;
                                            ImageView report_image;

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

                                            try {
                                                index = (int) marker.getTag();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            info_window_ll.setVisibility(View.GONE);
                                            reportType=alllatLngs_arr.get(index).getReport_type();
                                            StringBuilder sb = new StringBuilder(reportType);
                                            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                                            _reportType=sb.toString();
                                            if (alllatLngs_arr.get(index).getDuplicateReport()!=null &&
                                                    alllatLngs_arr.get(index).getDuplicateReport().equalsIgnoreCase("d")){
                                                if (!_reportType.equalsIgnoreCase("nill")){
                                                    top_title.setText(_reportType+" (Duplicate)");
                                                }
                                            }else {
                                                if (!reportType.equalsIgnoreCase("nil")){
                                                    top_title.setText(_reportType);
                                                }
                                            }
                                            String reportImg=alllatLngs_arr.get(index).getImgAcqlocation();

                                            if (reportImg==null){
                                                Glide.with(context)
                                                        .load(R.drawable.no_image_found)
                                                        .error(R.drawable.no_image_found)
                                                        .into(report_image);

                                            }else {
                                                Glide.with(context)
                                                        .load(RetrofitClient.IMAGE_URL+"report/"+reportImg)
                                                        .error(R.drawable.no_image_found)
                                                        .into(report_image);
                                            }

//                                            circle_nm.setText(alllatLngs_arr.get(index).getCircle());
                                            division_nm.setText(alllatLngs_arr.get(index).getDivision());
                                            range_nm.setText(alllatLngs_arr.get(index).getRange());
                                            section_nm.setText(alllatLngs_arr.get(index).getTotal());
                                            beat_nm.setText(alllatLngs_arr.get(index).getHeard());
                                            date_txt.setText(PermissionUtils.convertDate(alllatLngs_arr.get(index).getSighting_date(),
                                                    "yyyy-MM-dd hh:mm:sss","dd-MM-yyyy"));
//        }

                                            division_ll.setVisibility(View.VISIBLE);
                                            range_ll.setVisibility(View.VISIBLE);
                                            total_ll.setVisibility(View.VISIBLE);
                                            herd_ll.setVisibility(View.VISIBLE);

                                            return view;
                                        }
                                    });

                                }

                                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6.5f));
                                map.setMaxZoomPreference(35.0f);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }else {
                        progress_bar_LL.setVisibility(View.GONE);
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ViewReportItemData_obj>> call, Throwable t) {
                    progress_bar_LL.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });


        }catch (Exception e){
            progress_bar_LL.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void showFilterDialog(Context context,String from_date,String to_date){
        try {
            filter_dialog=new Dialog(context);
            filter_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            filter_dialog.setCancelable(false);
            filter_dialog.setContentView(R.layout.view_report_filter_dialog);
            filter_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            ImageView close_img;
            close_img=filter_dialog.findViewById(R.id.close_img);
            circle_spinner=filter_dialog.findViewById(R.id.circle);
            title_dialog=filter_dialog.findViewById(R.id.title);
            division_spinner=filter_dialog.findViewById(R.id.division);
            range_spinner=filter_dialog.findViewById(R.id.range);
            section_spinner=filter_dialog.findViewById(R.id.section);
            beat_spinner=filter_dialog.findViewById(R.id.beat);
            filter_LL=filter_dialog.findViewById(R.id.filter_LL);
            direct_indirect_spinner=filter_dialog.findViewById(R.id.direct_indirect_spinner);
            report_type_spinnerTIL=filter_dialog.findViewById(R.id.report_type_spinnerTIL);
            fromDate=filter_dialog.findViewById(R.id.fromDate);
            toDate=filter_dialog.findViewById(R.id.toDate);
            submit_CV=filter_dialog.findViewById(R.id.submit_CV);
            circle_div_LL=filter_dialog.findViewById(R.id.circle_div_LL);
            beat_LL=filter_dialog.findViewById(R.id.beat_LL);
            divisionTxt=filter_dialog.findViewById(R.id.divisionTxt);
            progress_bar_LL_filter=filter_dialog.findViewById(R.id.progress_bar_LL);
            title_dialog.setText("Filter Map");

            if (circle_dataAdapter!=null){
                circle_spinner.setAdapter(circle_dataAdapter);
                circle_spinner.setSelection(selecetedCircle);
            }else{
                circle_dataAdapter = new ArrayAdapter<AllCircleData>(context,
                        android.R.layout.simple_spinner_dropdown_item, circleDataArrayList);
                circle_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                circle_spinner.setAdapter(circle_dataAdapter);
                circle_spinner.setSelection(selecetedCircle);
            }
            if (div_dataAdapter!=null){
                division_spinner.setAdapter(div_dataAdapter);
                division_spinner.setSelection(selectedDiv);
            }else{
                div_dataAdapter = new ArrayAdapter<DivisionDataByCircleId>(context,
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

            callAllCircleApi();

//            from_dateSelected=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
//            to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");

            if (roleID.equalsIgnoreCase("2")){//DFO

                circle_div_LL.setVisibility(View.VISIBLE);

                circle_spinner.setVisibility(View.GONE);
                division_spinner.setVisibility(View.GONE);
                divisionTxt.setVisibility(View.VISIBLE);
                divisionTxt.setText(divisionNm);

                getAllRangeByDivisionId(divId,"");
                section_spinner.setVisibility(View.GONE);
                beat_LL.setVisibility(View.GONE);
            }else {
                divisionTxt.setVisibility(View.GONE);
                circle_div_LL.setVisibility(View.VISIBLE);
                section_spinner.setVisibility(View.GONE);
                beat_LL.setVisibility(View.GONE);
            }

            circle_spinner.setTitle("Select Circle");
            circle_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    circleValue = adapterView.getItemAtPosition(i).toString();
                    circleCode=""+circleDataArrayList.get(i).getCircleId();
                    if(selecetedCircle!=i && i!=0) {
                        selectedDiv = 0;
                        selectedRange = 0;
                        division_spinner.setSelection(selectedDiv);
                        range_spinner.setSelection(selectedRange);

                        if (circleLayer!=null){
                            circleLayer.removeLayerFromMap();
                        }
                        if (divisionLayer!=null){
                            divisionLayer.removeLayerFromMap();
                        }
                        if (rangeLayer!=null){
                            rangeLayer.removeLayerFromMap();
                        }
                        try {
                            if (!circleCode.equalsIgnoreCase("-1") || selecetedCircle!=0){
//                                new CallMapCircleLayer().execute(circleCode);
                                callMapApi(circleCode,"getCircleGeojson","circle");
                                division_spinner.setVisibility(View.VISIBLE);
                                getAllDivisionByCircleId(circleCode,"");
//                                checkDivisionIdForMapBoundary(divCode);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    selecetedCircle=i;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

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
                        callMapApi(divCode,"getDivisionGeojson","division");
//                        checkDivisionIdForMapBoundary(divCode);
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
                    filter_dialog.dismiss();

                }
            });

            fromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selectDate(fromDate, "fromDate");
                }
            });
            toDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selectDate(toDate, "toDate");
                }
            });

            if (fromDate_alreadySelected.equalsIgnoreCase("")){
                fromDate.setText(""+from_date);
            }else {
                fromDate.setText(""+fromDate_alreadySelected);
            }

            if (toDate_alreadySelected.equalsIgnoreCase("")){
                toDate.setText(""+to_date);
            }else {
                toDate.setText(""+toDate_alreadySelected);
            }

//            fromDate.setText(""+from_date);
//
//            toDate.setText(""+to_date);

            submit_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filter_dialog.dismiss();
                    if (PermissionUtils.check_InternetConnection(context) == "true") {

                        if (roleID.equalsIgnoreCase("2")){ //DFO
                            divCode=divId;
                            circleCode=circleId;

                            for (int i=0;i<markerArr.size();i++){
                                markerArr.get(i).remove();//remove all marker
                            }
//                            loadAllMapDataAsPerCircleRangeDiv_Date(circleCode,divCode,rangeCode,secCode,beatCode,
//                                                                    reportType,from_dateSelected,to_dateSelected);
                            getMapDatafromCircleDivRange(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected); //For All point i.e.direct and nil


                        }
                        else if (roleID.equalsIgnoreCase("4")){ //Ranger
                            circleCode=circleId;
                            divCode=divId;
                            rangeCode=rangeId;

                            for (int i=0;i<markerArr.size();i++){
                                markerArr.get(i).remove();//remove all marker
                            }
//                            loadAllMapDataAsPerCircleRangeDiv_Date(circleCode,divCode,rangeCode,secCode,beatCode,
//                                    reportType,from_dateSelected,to_dateSelected);
                            getMapDatafromCircleDivRange(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected); //For All point i.e.direct and nil
                        }
                        else {//for PCCF
                            for (int i=0;i<markerArr.size();i++){
                                markerArr.get(i).remove();//remove all marker
                            }
//                            loadAllMapDataAsPerCircleRangeDiv_Date(circleCode,divCode,rangeCode,secCode,beatCode,
//                                    reportType,from_dateSelected,to_dateSelected);
                            getMapDatafromCircleDivRange(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//For All point i.e.direct and nil

                        }
                    }
                    else {
                        Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            filter_dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void selectDate(TextView textView, String selectType) {

        if (selectType.equals("fromDate")) {
            SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm:ss");
            String str = sdf.format(new Date());
            textView.setError(null);
//                final Calendar calender = Calendar.getInstance();
            mYear = mcalender.get(Calendar.YEAR);
            mMonth = mcalender.get(Calendar.MONTH);
            mDay = mcalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            int mon = monthOfYear + 1;
                            if (mon < 10) {
                                m = "0" + mon;
                            } else {
                                m = String.valueOf(mon);
                            }
                            if (dayOfMonth < 10) {

                                d = "0" + dayOfMonth;
                            } else {
                                d = String.valueOf(dayOfMonth);
                            }
                            textView.setText(d + "-" + m + "-" + year);
//                            from_dateSelected = m + "-" + d + "-" + year + " " + str;
//                            from_dateSelected = year + "-" + m + "-" + d + str;
                            from_dateSelected = year + "-" + m + "-" + d + str;
                            fromDate_alreadySelected=""+d + "-" + m + "-" + year;

                        }
                    }, mYear, mMonth, mDay);

            dpd.getDatePicker().setMaxDate(mcalender.getTimeInMillis());
            dpd.show();
        } else {
            SimpleDateFormat sdf_to = new SimpleDateFormat(" HH:mm:ss");
            String str_to = sdf_to.format(new Date());
            textView.setError(null);
//                final Calendar calender = Calendar.getInstance();
            mYear = mcalender.get(Calendar.YEAR);
            mMonth = mcalender.get(Calendar.MONTH);
            mDay = mcalender.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            int mon = monthOfYear + 1;
                            if (mon < 10) {
                                m = "0" + mon;
                            } else {
                                m = String.valueOf(mon);
                            }
                            if (dayOfMonth < 10) {

                                d = "0" + dayOfMonth;
                            } else {
                                d = String.valueOf(dayOfMonth);
                            }
                            textView.setText(d + "-" + m + "-" + year);
//                            to_dateSelected = year + "-" + m + "-" + d +str_to;
                            to_dateSelected = year + "-" + m + "-" + d +str_to;
                            toDate_alreadySelected=""+d + "-" + m + "-" + year;


                        }
                    }, mYear, mMonth, mDay);

            dpd.getDatePicker().setMaxDate(mcalender.getTimeInMillis());
            dpd.show();
        }

    }

    public void loadAllMapDataAsPerCircleRangeDiv_Date(String circle,String division, String range, String section, String beat,
                               String report_type,String fromDate, String toDate) {

        try {

            if (PermissionUtils.check_InternetConnection(context) == "true") {
                report_type = "direct";

                if (circleCode.equalsIgnoreCase("-1")) {
                    circle = "All";
                } else {
                    circle = circleCode;
                }
                if (divCode.equalsIgnoreCase("-1")) {
                    division = "All";
                } else {
                    division = divCode;
                }
                if (rangeCode.equalsIgnoreCase("-1")) {
                    range = "All";
                } else {
                    range = rangeCode;
                }
                alllatLngs_arr.clear();
                markerArr.clear();

                progress_bar_LL.setVisibility(View.VISIBLE);
                progress_txt.setText("Fetching points...please wait !");
                RetrofitInterface retrofitInterface = RetrofitClient.getReportClient().create(RetrofitInterface.class);
                Call<ArrayList<ViewReportItemData_obj>> call = null;
                if (fromDate.equalsIgnoreCase("") || toDate.equalsIgnoreCase("")) {
                    call = retrofitInterface.get_ViewReport(division, circle, range, section, beat, "", "", report_type);
                } else {
                    call = retrofitInterface.get_ViewReport(division, circle, range, section, beat, fromDate, toDate, report_type);
                }

                call.enqueue(new Callback<ArrayList<ViewReportItemData_obj>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ViewReportItemData_obj>> call, Response<ArrayList<ViewReportItemData_obj>> response) {

                        if (response.isSuccessful()) {
//
                            progress_bar_LL.setVisibility(View.GONE);
                            if (response.body().isEmpty()) {
                                markerArr.removeAll(alllatLngs_arr);
                                if (marker!=null){
                                    marker.remove();
                                    marker.setVisible(false);
                                }

                                Snackbar.make(main_ll, "No point to plot on Map !", Snackbar.LENGTH_LONG).show();

                            } else {

                                for (int i = 0; i < response.body().size(); i++) {

                                    if (response.body().get(i).getReport_type().equalsIgnoreCase("Nill")) {

                                    } else {
                                        alllatLngs_arr.add(response.body().get(i));

                                        latLng = new LatLng(Double.parseDouble(alllatLngs_arr.get(i).getLatitude()),
                                                Double.parseDouble(alllatLngs_arr.get(i).getLongitudes()));

                                        marker=map.addMarker(new
                                                MarkerOptions().position(latLng));
                                        marker.setRotation(5);
                                        marker.setTitle("" + alllatLngs_arr.get(i));
                                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.elephant_marker));
                                        markerArr.add(marker);

                                    }
                                }

                                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                                map.setMaxZoomPreference(30.0f);
//                                map.setInfoWindowAdapter(new CustomInfoAdapter(getActivity()));

                            }

                        } else {
                            progress_bar_LL.setVisibility(View.GONE);
                            if (response.code() == 500) {
                                Toast.makeText(context, "Internal Server error !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Please try again...!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<ViewReportItemData_obj>> call, Throwable t) {
                        Log.i("url", call.request().toString());
                        progress_bar_LL.setVisibility(View.GONE);
                        Toast.makeText(context, "Failed...please try again !", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                progress_bar_LL.setVisibility(View.GONE);
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            progress_bar_LL.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

//    public void captureAndStoreImage(Bitmap bitmap,String path){
    public void captureAndStoreImage(Bitmap bitmap){
        try {
            // Assume block needs to be inside a Try/Catch block.
//            String path_file = Environment.getExternalStorageDirectory().toString();
            String path="";
            OutputStream fOut = null;
            File file=null;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            if(Build.VERSION.SDK_INT >= 29) {
                String imageFileName = "elephantSightingMap_" + timeStamp + "_";
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );

                // Save a file: path for use with ACTION_VIEW intents
                path = file.getAbsolutePath();
            }else {
                path = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/WildlifeMapScreenshots/";

                File dir = new File(path);
                if(!dir.exists())
                    dir.mkdirs();

                file=null;
                try {
                    file = new File(dir, "elephantSightingMap_"+timeStamp+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.

                    fOut = new FileOutputStream(file);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            try {
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
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
//            Uri uri = Uri.fromFile(imageFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri uri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider", imageFile);

            intent.setDataAndType(uri, "image/*");
            startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void callToGetLatLngCircleBoundary(JSONObject geoJson,float zoom){
        try {
            ArrayList<LatLng> listdata = new ArrayList<LatLng>();
//            JSONArray feature_jsonArray = geoJson.getJSONArray("features");
//            JSONObject geometry_json=feature_jsonArray.getJSONObject(0).getJSONObject("geometry");

            JSONArray coordinate_jsonArray = geoJson.getJSONArray("coordinates");
            JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
            first_jsonArray=coordinate_jsonArray.getJSONArray(0);
//            JSONObject properties_json=feature_jsonArray.getJSONObject(0).getJSONObject("properties");
//            String name=properties_json.getString("divn_name");
//            try {
//                circle_name = properties_json.getString("name_e");
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

    public void callMapApi(String id,String layerNm,String type) {
        try {
            progress_bar_LL.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface=RetrofitClient.getMapRequestClient().create(RetrofitInterface.class);

            if (type.equalsIgnoreCase("circle")){

                CircleRequestObj layerRequestObj = new CircleRequestObj(id);
                String request = new Gson().toJson(layerRequestObj);

                // Here the json data is add to a hash map with key data
                Map<String,String> params = new HashMap<String, String>();
                params.put("data", request);

                retrofitInterface.callMapApi(params,layerNm).enqueue(new Callback<MapDataResponse>() {
                    @Override
                    public void onResponse(Call<MapDataResponse> call, Response<MapDataResponse> response) {

                        if (response.isSuccessful()){

//                            JSONObject jsonObject=new JSONObject(response.body().getPost().get(0).getGeojson());
//                            jsonObject.getString("coordinates");
//                            Log.i("Map_response",jsonObject.getString("coordinates"));

                            try{
                                if (response.body().getPost()!=null){

                                    for (int i=0;i<response.body().getPost().size();i++){
                                        circleJsonResponse=new JSONObject(response.body().getPost().get(i).getGeojson());
                                        circle_name=response.body().getPost().get(i).getLayer_name();
                                    }
                                    //for putting GeoJsonLayer on Map with json response coming from geoserver
                                    circleLayer=new GeoJsonLayer(map,circleJsonResponse);
//                    callToGetLatlngFromBoundary(circleJsonResponse,6);//Get latlng to zoom from state boundary

                                    if (!circleCode.equalsIgnoreCase("All")&&
                                            !circleCode.equalsIgnoreCase("-1")){
                                        callToGetLatLngCircleBoundary(circleJsonResponse,5);//for single selection circle zoom
                                    }else {
                                        callToGetLatLngCircleBoundary(circleJsonResponse,6);//for all circle zoom
                                    }

                                    GeoJsonPolygonStyle polyStyle =circleLayer.getDefaultPolygonStyle();
                                    polyStyle.setStrokeColor(getResources().getColor(R.color.circle_boundary_color));
                                    polyStyle.setStrokeWidth(5);
                                    circleLayer.addLayerToMap();
                                }

                            }catch(Exception e) {
                                progress_bar_LL.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                            progress_bar_LL.setVisibility(View.GONE);
                            Log.i("Map_response",response.body().getPost().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<MapDataResponse> call, Throwable t) {
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        progress_bar_LL.setVisibility(View.GONE);
                        Log.i("Map_response",call.toString());
                    }
                });

            }else  if (type.equalsIgnoreCase("division")){

                DivisionRequestobj layerRequestObj = new DivisionRequestobj(id);
                String request = new Gson().toJson(layerRequestObj);

                // Here the json data is add to a hash map with key data
                Map<String,String> params = new HashMap<String, String>();
                params.put("data", request);
                Log.i("Map_response",params.toString());

                retrofitInterface.callMapApi(params,layerNm).enqueue(new Callback<MapDataResponse>() {
                    @Override
                    public void onResponse(Call<MapDataResponse> call, Response<MapDataResponse> response) {

                        if (response.isSuccessful()){

                            try {
                                if (response.body().getPost()!=null){

                                    for (int i=0;i<response.body().getPost().size();i++) {
                                        geoJsonResponse = new JSONObject(response.body().getPost().get(i).getGeojson());
                                        area_sq=response.body().getPost().get(i).getArea();
                                        divn_name=response.body().getPost().get(i).getLayer_name();
                                    }
                                    try{
                                        //for putting GeoJsonLayer on Map with json response coming from geoserver
                                        divisionLayer=new GeoJsonLayer(map,geoJsonResponse);
                                        progress_bar_LL.setVisibility(View.GONE);

                                        ArrayList<LatLng> listdata = new ArrayList<LatLng>();
//                                    JSONArray feature_jsonArray = geoJsonResponse.getJSONArray("features");
//                                    JSONObject geometry_json=null;
//
//                                    for (int i=0;i<feature_jsonArray.length();i++) {
//                                        try {
//                                            geometry_json = feature_jsonArray.getJSONObject(i).getJSONObject("geometry");
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
                                        JSONArray coordinate_jsonArray = geoJsonResponse.getJSONArray("coordinates");
                                        JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
                                        first_jsonArray=coordinate_jsonArray.getJSONArray(0);
//                                    JSONObject properties_json=feature_jsonArray.getJSONObject(0).getJSONObject("properties");

//                                    try {
////                                        divn_name=properties_json.getString("name_e");
//                                        areaSquare=Long.parseLong(area_sq)/1000000;
                                        Log.i("area",""+Double.parseDouble(area_sq)/1000000);

////                        area_sq=properties_json.getString("area_sqkm");
                                        area_ll.setVisibility(View.VISIBLE);
//                                    }catch (Exception e){
////                        divn_name=properties_json.getString("division");
////                                        divn_name=properties_json.getString("name_e");
//                                        area_ll.setVisibility(View.GONE);
//                                        e.printStackTrace();
//                                    }
                                        second_jsonArr = first_jsonArray.getJSONArray(0);
//
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
                                                div.setText(divn_name);
                                                circle_nm.setText(circle_name);
                                                String areaSqkm = String.format("%.2f", Double.parseDouble(area_sq)/1000000);
                                                area.setText(areaSqkm +" sq.km");

                                                elephant_ll.setVisibility(View.GONE);
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

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progress_bar_LL.setVisibility(View.GONE);
                            }


                        }else {
                            Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                                progress_bar_LL.setVisibility(View.GONE);
                            }
                            Log.i("Map_response",response.body().getPost().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<MapDataResponse> call, Throwable t) {
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        if (progress_bar_LL.getVisibility()==View.VISIBLE){
                            progress_bar_LL.setVisibility(View.GONE);
                        }
                        Log.i("Map_response",call.toString());
                    }
                });

            }else  if (type.equalsIgnoreCase("range")){

                RangeRequestObj layerRequestObj = new RangeRequestObj(id);
                String request = new Gson().toJson(layerRequestObj);

                // Here the json data is add to a hash map with key data
                Map<String,String> params = new HashMap<String, String>();
                params.put("data", request);
                Log.i("Map_response",params.toString());

                retrofitInterface.callMapApi(params,layerNm).enqueue(new Callback<MapDataResponse>() {
                    @Override
                    public void onResponse(Call<MapDataResponse> call, Response<MapDataResponse> response) {

                        if (response.isSuccessful()){

                            try{
                                if (response.body().getPost()!=null){

                                    for (int i=0;i<response.body().getPost().size();i++) {
                                        rangeJsonResponse = new JSONObject(response.body().getPost().get(i).getGeojson());
                                        area_sq=response.body().getPost().get(i).getArea();
                                        range_name=response.body().getPost().get(i).getLayer_name();
                                    }
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

                                }

                            }catch(Exception e) {
                                if (progress_bar_LL.getVisibility()==View.VISIBLE){
                                    progress_bar_LL.setVisibility(View.GONE);
                                }
                                e.printStackTrace();
                            }


                        }else {
                            Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                                progress_bar_LL.setVisibility(View.GONE);
                            }
                            Log.i("Map_response",response.body().getPost().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<MapDataResponse> call, Throwable t) {
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        if (progress_bar_LL.getVisibility()==View.VISIBLE){
                            progress_bar_LL.setVisibility(View.GONE);
                        }
                        Log.i("Map_response",call.toString());
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }            Log.i("Map_response",e.toString());
        }
    }

    public void callAllLayerMap(){
        try {
            progress_bar_LL.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface=RetrofitClient.getMapRequestClient().create(RetrofitInterface.class);
            retrofitInterface.callAllLayerMapApi("getGeoJsonCircleAll").enqueue(new Callback<ArrayList<StGeojsonObj>>() {
                @Override
                public void onResponse(Call<ArrayList<StGeojsonObj>> call, Response<ArrayList<StGeojsonObj>> response) {
                    if (response.isSuccessful()){
                        progress_bar_LL.setVisibility(View.GONE);
                        try{
                            if (response.body()!=null){
                                for (int i=0;i<response.body().size();i++){
                                    circleJsonResponse=new JSONObject(response.body().get(i).getGeojson());
//                                    circle_name=response.body().get(i).getLayer_name();
//                                    callAddCircleCoordinates(circleJsonResponse);
                                    circleLayer = new GeoJsonLayer(map, circleJsonResponse);

                                    if (!circleCode.equalsIgnoreCase("All")&&
                                            !circleCode.equalsIgnoreCase("-1")){
                                        callToGetLatLngCircleBoundary(circleJsonResponse,5);//for single selection circle zoom
                                    }else {
                                        callToGetLatLngCircleBoundary(circleJsonResponse,6);//for all circle zoom
                                    }
                                    GeoJsonPolygonStyle polyStyle =circleLayer.getDefaultPolygonStyle();
                                    polyStyle.setStrokeColor(getResources().getColor(R.color.circle_boundary_color));
                                    polyStyle.setStrokeWidth(5);
                                    circleLayer.addLayerToMap();
                                }
                                Log.i("circle",circleJsonResponse.toString());
                                //for putting GeoJsonLayer on Map with json response coming from geoserver
//                                circleLayer = new GeoJsonLayer(map, circleJsonResponse);

//                                if (!circleCode.equalsIgnoreCase("All")&&
//                                        !circleCode.equalsIgnoreCase("-1")){
//                                    callToGetLatLngCircleBoundary(circleJsonResponse,5);//for single selection circle zoom
//                                }else {
//                                    callToGetLatLngCircleBoundary(circleJsonResponse,6);//for all circle zoom
//                                }

//                                GeoJsonPolygonStyle polyStyle =circleLayer.getDefaultPolygonStyle();
//                                polyStyle.setStrokeColor(getResources().getColor(R.color.circle_boundary_color));
//                                polyStyle.setStrokeWidth(5);
//                                circleLayer.addLayerToMap();
                            }

                        }catch(Exception e) {
                            progress_bar_LL.setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                    }else {
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        if (progress_bar_LL.getVisibility()==View.VISIBLE){
                            progress_bar_LL.setVisibility(View.GONE);
                        }
//                        Log.i("Map_response",response.body().getPost().toString());

                    }
                }

                @Override
                public void onFailure(Call<ArrayList<StGeojsonObj>> call, Throwable t) {
                    Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                    if (progress_bar_LL.getVisibility()==View.VISIBLE){
                        progress_bar_LL.setVisibility(View.GONE);
                    }
                    Log.i("Map_response",call.toString());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }
        }
    }

    public void callAllLatLngCircleBoundary(JSONObject geoJson,float zoom){
        try {
            ArrayList<LatLng> listdata = new ArrayList<LatLng>();
//            JSONArray feature_jsonArray = geoJson.getJSONArray("features");
//            JSONObject geometry_json=feature_jsonArray.getJSONObject(0).getJSONObject("geometry");

            JSONArray coordinate_jsonArray = geoJson.getJSONArray("coordinates");
            JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
            first_jsonArray=coordinate_jsonArray.getJSONArray(0);
//            JSONObject properties_json=feature_jsonArray.getJSONObject(0).getJSONObject("properties");
//            String name=properties_json.getString("divn_name");
//            try {
//                circle_name = properties_json.getString("name_e");
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
    public ArrayList<LatLng> callAddCircleCoordinates(JSONObject jsonObject){
        ArrayList<LatLng> listdata = new ArrayList<LatLng>();
        try {
//            JSONArray feature_jsonArray = geoJson.getJSONArray("features");
//            JSONObject geometry_json=feature_jsonArray.getJSONObject(0).getJSONObject("geometry");

            JSONArray coordinate_jsonArray = jsonObject.getJSONArray("coordinates");
            JSONArray first_jsonArray=null,second_jsonArr=null,third_jsonArr=null;
            first_jsonArray=coordinate_jsonArray.getJSONArray(0);

            second_jsonArr = first_jsonArray.getJSONArray(0);

            for (int j = 0; j < second_jsonArr.length(); j++) {
                third_jsonArr = second_jsonArr.getJSONArray(j);
                String[] coord = third_jsonArr.toString().split(",");
                double x = Double.parseDouble(coord[0].replace("[",""));
                double y = Double.parseDouble(coord[1].replace("]",""));
                listdata.add(new LatLng(y,x));
                layer_latlng=new LatLng(y,x);//(latitude,longitude)- It will zoom by taking to this latlng
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        Log.i("circle_arr",listdata.toString());
        return listdata;
    }

}
