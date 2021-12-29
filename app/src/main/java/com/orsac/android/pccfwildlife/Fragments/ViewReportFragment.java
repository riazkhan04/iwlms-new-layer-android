package com.orsac.android.pccfwildlife.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.orsac.android.pccfwildlife.Activities.IncidentReportViewActivity;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.ElephantDeathIncidentDataModel;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.IncidentReportDataModel;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.MyUtils.CommonMethods;
import com.orsac.android.pccfwildlife.Presenter.ViewReportPresenter;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Adapters.ViewReportRecyclerVNew;
import com.orsac.android.pccfwildlife.Contract.ViewReportContract;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllCircleData;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.BeatDataBySecId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.DivisionDataByCircleId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.RangeDataByDivId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.SectionDataByRangeId;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.orsac.android.pccfwildlife.MyUtils.PermissionUtils.getBitmapFromView;
import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class ViewReportFragment extends Fragment implements ViewReportContract.view,ViewReportRecyclerVNew.Update_clickListener{
//public class ViewReportFragment extends Fragment implements ViewReportContract.view,ViewReportRecyclerV_Adapter.Update_clickListener {

    Context context;
    SessionManager session;
    public String token = "", checkInternet_status = "", divisionNmFrom_pref = "", userId = "",roles="",juridictionID="",
            roleID="",circleId="",divId="",rangeId="";
    String choosedFromHour = "", choosedFromMinute = "", choosedToHour = "", choosedToMinute = "";
    private int finalfrom, finalto, mYear, mMonth, mDay;
    String m, d;
    Calendar mcalender = Calendar.getInstance();
    TextView fromDate, toDate, submit,no_data,divisionTxt,report_count_txt,date_txt,progress_txt;
    String from_dateSelected = "", to_dateSelected = "",fromDate_alreadySelected="",toDate_alreadySelected="";
    ArrayList<ViewReportItemData_obj> view_item_arr;
    public RecyclerView.LayoutManager layoutManager;
//    ViewReportRecyclerV_Adapter view_adapter;
    ViewReportRecyclerVNew view_adapter;
    RecyclerView view_recyclerV;
    ViewReportContract.presenter presenter;
    ProgressBar progress_bar;
    BottomNavigationView b_nav_view;
    ScrollView scrollView;
    AppCompatSpinner direct_indirect_spinner;
    ImageView share_all;
    String divisionValue="",division_nm="",reportType="direct",circleValue="",rangeValue="",secValue="",beatValue="";
    public static String circleCode="All",divCode="All",rangeCode="All",secCode="All",beatCode="All";
    public HashMap<String, String> divKey;
    SQLiteDatabase db;
    SearchableSpinner division_spinner,circle_spinner,range_spinner,section_spinner,beat_spinner;
    ArrayList<AllCircleData> circleDataArrayList;
    ArrayList<DivisionDataByCircleId> divisionDataByCircleIdArrayList;
    ArrayList<RangeDataByDivId> rangeDataByDivIdArrayList;
    ArrayList<SectionDataByRangeId> sectionDataArrayList;
    ArrayList<BeatDataBySecId> beatDataArrayList;
    PermissionHelperClass permissionHelperClass;
    AppCompatImageView filter_img;
    LinearLayout filter_LL,circle_div_LL,progress_bar_LL,progress_bar_LL_filter;
    CardView submit_CV;
    Dialog filter_dialog;
    int selecetedCircle=0,selectedDiv=0,selectedRange=0,selectedSection=0,selectedBeat=0;
    ArrayAdapter<AllCircleData> circle_dataAdapter;
    ArrayAdapter<DivisionDataByCircleId> div_dataAdapter;
    ArrayAdapter<RangeDataByDivId> range_dataAdapter;
    ArrayAdapter<SectionDataByRangeId> section_dataAdapter;
    ArrayAdapter<BeatDataBySecId> beat_dataAdapter;
    public static LinearLayout bottom_scrollV;
//    public static ScrollView bottom_scrollV;
    public static TextView location_txt,dom_txt,from_time_txt,to_time_txt,report_type,division_txt,range_txt,section_txt,
            beat_txt,total_no,heardtxt,tuskertxt,maletxt,femaletxt,calftxt,report_through,remarktxt,
        reporttxt,accuracy_txt,updated_by;
    ImageView cancel_img,download_report_img;
    public static ImageView update_img,report_img,report_through_img,report_share,locate_map_img;
//    public static TouchImageView report_imgg;
    public static LinearLayout total_herd_LL,female_calf_LL,tusker_makhna_LL;
    public static RelativeLayout main_ll;


    public ViewReportFragment(BottomNavigationView bottomNavigationView,ImageView download_report_img) {
        this.b_nav_view=bottomNavigationView;
        this.download_report_img=download_report_img;
    }

    public ViewReportFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.report_view_layout, container, false);
        View view = inflater.inflate(R.layout.view_reportlayout_new, container, false);

        try {

            initData(view);

            click_function();
            circleCode="All";
            from_dateSelected=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
            to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");

            if (PermissionUtils.check_InternetConnection(context) == "true") {
                //for default data coming from api
                if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")||roleID.equalsIgnoreCase("6")){

                    progress_txt.setText("Fetching reports...Please wait !");
                    presenter.loadViewReport(PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss"), PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"),
                            view_item_arr, circleCode,divCode,rangeCode,secCode,beatCode,view_recyclerV,
                            ViewReportFragment.this,no_data,"direct",scrollView,
                            scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),progress_bar_LL,
                            date_txt,report_count_txt,updated_by);

                }
                else if (roleID.equalsIgnoreCase("2")){ //DFO
                    divCode=divId;
                    circleCode=circleId;
                    rangeCode="";
                    progress_txt.setText("Fetching reports...Please wait !");
                    presenter.loadViewReport(PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss"), PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss")
                            , view_item_arr,circleCode,divCode,rangeCode,secCode,beatCode,view_recyclerV,
                            ViewReportFragment.this,no_data,"direct",scrollView,
                            scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),progress_bar_LL,
                            date_txt,report_count_txt,updated_by);

                }


            } else {
//            PermissionUtils.no_internet_Dialog(context, "View Report has some Problem!", null);
                PermissionUtils.no_internet_Dialog(context, "No Internet Connection !", null);
            }

            //create path for pdf
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/wildlifePdf";
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    private void initData(View view) {
        try {
            session = new SessionManager(context);

            token = session.getToken();
            divisionNmFrom_pref = session.getDivision();
            userId = session.getUserID();
            roles = session.getRoles();
            roleID = session.getRoleId();
            circleId = session.getCircleId();
            divId = session.getDivisionId();
            rangeId = session.getRangeId();
            juridictionID = session.getJuridictionID();
            division_nm = session.getJuridiction();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_monitoring);

            fromDate = view.findViewById(R.id.fromDate);
            toDate = view.findViewById(R.id.toDate);
            submit = view.findViewById(R.id.submit);
            no_data = view.findViewById(R.id.no_data);
            view_recyclerV = view.findViewById(R.id.report_recyclerV);
            progress_bar = view.findViewById(R.id.progress_bar);
            scrollView = view.findViewById(R.id.scrollView);
            share_all = view.findViewById(R.id.share_all);
            filter_img = view.findViewById(R.id.filter_img);
            filter_LL = view.findViewById(R.id.filter_LL);
            progress_bar_LL=view.findViewById(R.id.progress_bar_LL);

            location_txt=view.findViewById(R.id.location_txt);
            dom_txt=view.findViewById(R.id.dom_txt);
            from_time_txt=view.findViewById(R.id.from_time_txt);
            to_time_txt=view.findViewById(R.id.to_time_txt);
            division_txt=view.findViewById(R.id.division_txt);
            range_txt=view.findViewById(R.id.range_txt);
            section_txt=view.findViewById(R.id.section_txt);
            beat_txt=view.findViewById(R.id.beat_txt);
            total_no=view.findViewById(R.id.total_no);
            heardtxt=view.findViewById(R.id.heard);
            tuskertxt=view.findViewById(R.id.tusker);
            maletxt=view.findViewById(R.id.male);
            femaletxt=view.findViewById(R.id.female);
            calftxt=view.findViewById(R.id.calf);
            report_through=view.findViewById(R.id.report_through);
            report_type=view.findViewById(R.id.report_type);
            remarktxt=view.findViewById(R.id.remark);
            reporttxt=view.findViewById(R.id.report);
            accuracy_txt=view.findViewById(R.id.accuracy_txt);
            updated_by=view.findViewById(R.id.updated_by);
            cancel_img=view.findViewById(R.id.cancel_img);
            bottom_scrollV=view.findViewById(R.id.bottom_scrollV);

            update_img=view.findViewById(R.id.update_img);
            report_img=view.findViewById(R.id.report_img);
//            report_imgg=view.findViewById(R.id.report_img);
            report_share=view.findViewById(R.id.report_share);
            report_through_img=view.findViewById(R.id.report_through_img);
            locate_map_img=view.findViewById(R.id.locate_map_img);

            total_herd_LL=view.findViewById(R.id.total_herd_LL);
            tusker_makhna_LL=view.findViewById(R.id.tusker_makhna_LL);
            female_calf_LL=view.findViewById(R.id.female_calf_LL);
            main_ll=view.findViewById(R.id.main_ll);

            date_txt=view.findViewById(R.id.date_txt);
            report_count_txt=view.findViewById(R.id.report_count_txt);
            progress_txt=view.findViewById(R.id.progress_txt);

            view_item_arr = new ArrayList<>();
            circleDataArrayList=new ArrayList<>();
            divisionDataByCircleIdArrayList=new ArrayList<>();
            rangeDataByDivIdArrayList=new ArrayList<>();
            sectionDataArrayList=new ArrayList<>();
            beatDataArrayList=new ArrayList<>();
            filter_dialog=new Dialog(context);

            presenter = new ViewReportPresenter(context, this,getActivity());

            view_recyclerV.setHasFixedSize(true);
            view_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview
            layoutManager =  new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);//for reverse list make it true
            view_recyclerV.setLayoutManager(layoutManager);

            download_report_img.setVisibility(View.VISIBLE);

            circleDataArrayList.clear();
            circleDataArrayList.add(new AllCircleData("-1","Select Circle"));

            divisionDataByCircleIdArrayList.clear();
            divisionDataByCircleIdArrayList.add(new DivisionDataByCircleId("-1","Select Division"));

            rangeDataByDivIdArrayList.clear();
            rangeDataByDivIdArrayList.add(new RangeDataByDivId("-1","Select Range"));

            sectionDataArrayList.clear();
            sectionDataArrayList.add(new SectionDataByRangeId("-1","Select Section"));

            beatDataArrayList.clear();
            beatDataArrayList.add(new BeatDataBySecId("-1","Select Beat"));
//            bindDivision();
            permissionHelperClass=new PermissionHelperClass(getActivity());
            if (permissionHelperClass.hasStoragePermission()){

            }else {
                permissionHelperClass.requestStoragePermission("viewReport");//request for file storage permission
            }
            //create path for pdf
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/wildlifePdf";
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void click_function() {

        try {

        Animation animation=AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
        share_all.startAnimation(animation);

        share_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    call_PdfDialog(context,"Do you want to share PDF ?","Generate and Share PDF");


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

            filter_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (bottom_scrollV.getVisibility()==View.VISIBLE){
                        bottom_scrollV.setVisibility(View.GONE);
                    }

                    showFilterDialog(context,PermissionUtils.getCurrentDateMinusOne("dd-MM-yyyy"),
                                        PermissionUtils.getCurrentDate("dd-MM-yyyy"));

                }
            });
            cancel_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Animation animation=AnimationUtils.loadAnimation(context,R.anim.top_to_bottom_anim);
                    bottom_scrollV.startAnimation(animation);
                    bottom_scrollV.setVisibility(View.GONE);
                }
            });
            download_report_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!date_txt.getText().toString().trim().equalsIgnoreCase("")) {
                        CommonMethods commonMethods = new CommonMethods(context);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String mFileName = "wildlife_report_" + timeStamp;

//                        for (int i = 0; i < view_item_arr.size(); i++) {

                            commonMethods.loadAllExcel(main_ll, getActivity(),
                                    mFileName, view_item_arr,new ArrayList<ElephantDeathIncidentDataModel>(),
                                    new ArrayList<IncidentReportDataModel>(),"direct_indirect_nil",
                                    progress_bar_LL,progress_txt);

//                        }

                    }
                }
            });

        view_recyclerV.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && b_nav_view.isShown())
                {
                    try {
                        b_nav_view.setVisibility(View.GONE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

//                    Animation animation= AnimationUtils.loadAnimation(context,R.anim.top_to_bottom_anim);
//                    b_nav_view.startAnimation(animation);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    try {

                        b_nav_view.setVisibility(View.VISIBLE);
                        Animation animation= AnimationUtils.loadAnimation(context,R.anim.bottom_to_top_anim);
                        b_nav_view.startAnimation(animation);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });


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
//                            from_dateSelected = year + "-" + m + "-" + d + str+".090+00:00";
//                          Toast.makeText(context, from_dateSelected, Toast.LENGTH_SHORT).show();

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
//                            to_dateSelected = year + "-" + m + "-" + d +str_to;
                            to_dateSelected = year + "-" + m + "-" + d +str_to;
                            toDate_alreadySelected=""+d + "-" + m + "-" + year;
//                            to_dateSelected = year + "-" + m + "-" + d +str_to+".090+00:00";
//                            to_dateSelected = m + "-" + d + "-" + year + " " + str_to;
//                          Toast.makeText(context, to_dateSelected, Toast.LENGTH_SHORT).show();

                        }
                    }, mYear, mMonth, mDay);

            dpd.getDatePicker().setMaxDate(mcalender.getTimeInMillis());
            dpd.show();
        }

    }


    @Override
    public void show_progress() {

//        progress_bar.setVisibility(View.VISIBLE);
        progress_bar_LL.setVisibility(View.VISIBLE);
    }

    @Override
    public void hide_progress() {
//        progress_bar.setVisibility(View.INVISIBLE);
        progress_bar_LL.setVisibility(View.GONE);
    }

    @Override
    public void success(String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failed(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void on_CardViewClick(int position,String from_time,String to_time,Bitmap bitmap) {
        try {

//            PermissionUtils.createPdfFromImage(getActivity(),from_dateSelected, to_dateSelected,bitmap);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapPointClick(int position, double latitude, double longitude) {
        try {

//            Toast.makeText(context, ""+latitude, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClickShowReport(String type){

        try {

            presenter.loadViewReport(from_dateSelected, to_dateSelected, view_item_arr,"All","All","All","All","All", view_recyclerV,
                    ViewReportFragment.this,no_data,type,scrollView,
                    scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),
                    progress_bar_LL ,date_txt,report_count_txt,updated_by);
            view_adapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void bindDivision() {
        List<String> divName = new ArrayList<String>();
        divName.add("Select Division");
        divKey = new HashMap<>();
        db = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT  DISTINCT Division_Id,Division_Name FROM Division_Other order by Division_Name", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                divName.add(cursor.getString(cursor.getColumnIndex("Division_Name")));
                divKey.put(cursor.getString(cursor.getColumnIndex("Division_Name")), cursor.getString(cursor.getColumnIndex("Division_Id")));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, divName);
        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
        division_spinner.setAdapter(dataAdapter);
    }


    private void callAllCircleApi() {
        try {
            if (PermissionUtils.check_InternetConnection(context) == "true") {

                RetrofitInterface retrofitInterface= RetrofitClient.getClient("").create(RetrofitInterface.class);
                progress_bar_LL_filter.setVisibility(View.VISIBLE);
                retrofitInterface.getAllCircle().enqueue(new Callback<ArrayList<AllCircleData>>() {
                    @Override
                    public void onResponse(Call<ArrayList<AllCircleData>> call, Response<ArrayList<AllCircleData>> response) {

                        if (response.isSuccessful()){
                            progress_bar_LL_filter.setVisibility(View.GONE);
                            circleDataArrayList.clear();
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

            }else {
                progress_bar_LL_filter.setVisibility(View.GONE);
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            progress_bar_LL_filter.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    private void getAllDivisionByCircleId(String CircleId,String type){
        try {

            if (type.equalsIgnoreCase("firstTime")){
                divisionDataByCircleIdArrayList.clear();
                divisionDataByCircleIdArrayList.add(new DivisionDataByCircleId("-1","Select Division"));

                final ArrayAdapter<DivisionDataByCircleId> dataAdapter = new ArrayAdapter<DivisionDataByCircleId>(context,
                        android.R.layout.simple_spinner_dropdown_item, divisionDataByCircleIdArrayList);
                dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                division_spinner.setAdapter(dataAdapter);
            }else {
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



        }catch (Exception e){
            progress_bar_LL_filter.setVisibility(View.GONE);
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
            division_spinner=filter_dialog.findViewById(R.id.division);
            range_spinner=filter_dialog.findViewById(R.id.range);
            section_spinner=filter_dialog.findViewById(R.id.section);
            beat_spinner=filter_dialog.findViewById(R.id.beat);
            direct_indirect_spinner=filter_dialog.findViewById(R.id.direct_indirect_spinner);
            fromDate=filter_dialog.findViewById(R.id.fromDate);
            toDate=filter_dialog.findViewById(R.id.toDate);
            submit_CV=filter_dialog.findViewById(R.id.submit_CV);
            circle_div_LL=filter_dialog.findViewById(R.id.circle_div_LL);
            divisionTxt=filter_dialog.findViewById(R.id.divisionTxt);
            progress_bar_LL_filter=filter_dialog.findViewById(R.id.progress_bar_LL);

//            circleDataArrayList.clear();
//            circleDataArrayList.add(new AllCircleData("-1","Select Circle"));

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
            if (section_dataAdapter!=null){
                section_spinner.setAdapter(section_dataAdapter);
                section_spinner.setSelection(selectedSection);
            }else{
                section_dataAdapter = new ArrayAdapter<SectionDataByRangeId>(context,
                        android.R.layout.simple_spinner_dropdown_item, sectionDataArrayList);
                section_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                section_spinner.setAdapter(section_dataAdapter);
                section_spinner.setSelection(selectedSection);
            }
            if (beat_dataAdapter!=null){
                beat_spinner.setAdapter(beat_dataAdapter);
                beat_spinner.setSelection(selectedBeat);
            }else{
                beat_dataAdapter = new ArrayAdapter<BeatDataBySecId>(context,
                        android.R.layout.simple_spinner_dropdown_item, beatDataArrayList);
                beat_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                beat_spinner.setAdapter(beat_dataAdapter);
                beat_spinner.setSelection(selectedBeat);
            }

            callAllCircleApi();

//            from_dateSelected=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
//            to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");


            if (roleID.equalsIgnoreCase("2")){//DFO

                circle_div_LL.setVisibility(View.VISIBLE);

                circle_spinner.setVisibility(View.GONE);
                division_spinner.setVisibility(View.GONE);
                divisionTxt.setVisibility(View.VISIBLE);
                divisionTxt.setText(division_nm);

                getAllRangeByDivisionId(divId,"");
            }else {
                circle_div_LL.setVisibility(View.VISIBLE);

            }

            progress_txt.setText("Fetching reports...Please wait !");

            circle_spinner.setTitle("Select Circle");
            circle_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    circleValue = adapterView.getItemAtPosition(i).toString();
                    circleCode=""+circleDataArrayList.get(i).getCircleId();

                    if(i!= selecetedCircle && i!=0) {
                        selectedDiv = 0;
                        selectedRange = 0;
                        selectedSection = 0;
                        selectedBeat = 0;
                        division_spinner.setSelection(selectedDiv);
                        range_spinner.setSelection(selectedRange);
                        section_spinner.setSelection(selectedSection);
                        beat_spinner.setSelection(selectedBeat);
                    }

                    selecetedCircle=i;
                    if (!circleValue.equalsIgnoreCase("Select Circle")){
                        division_spinner.setVisibility(View.VISIBLE);
                        getAllDivisionByCircleId(circleCode,"");
                    }

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
                        selectedSection = 0;
                        selectedBeat = 0;
                        range_spinner.setSelection(selectedRange);
                        section_spinner.setSelection(selectedSection);
                        beat_spinner.setSelection(selectedBeat);
                        getAllRangeByDivisionId(divCode,"");
                    }
                    selectedDiv=i;
//                    if (!divisionValue.equalsIgnoreCase("Select Division")){
//                        getAllRangeByDivisionId(divCode,"");
//                    }

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
                        selectedSection = 0;
                        selectedBeat = 0;
                        section_spinner.setSelection(selectedSection);
                        beat_spinner.setSelection(selectedBeat);
                        getAllSectionByRangeId(rangeCode,"");
                    }
                    selectedRange=i;
//                    if (!rangeValue.equalsIgnoreCase("Select Range")){
//                        getAllSectionByRangeId(rangeCode,"");
//                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            section_spinner.setTitle("Select Section");
            section_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    secValue = adapterView.getItemAtPosition(i).toString();
                    secCode=""+sectionDataArrayList.get(i).getSecId();

                    if(selectedSection!=i && i!=0) {
                        selectedBeat = 0;
                        beat_spinner.setSelection(selectedBeat);
                        getAllBeatBySectionId(secCode,"");
                    }
                    selectedSection=i;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            beat_spinner.setTitle("Select Beat");
            beat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    beatValue = adapterView.getItemAtPosition(i).toString();
                    beatCode=""+beatDataArrayList.get(i).getBeatId();

                    if(selectedBeat!=i && i!=0) {

                    }
                    selectedBeat=i;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            direct_indirect_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    reportType=adapterView.getSelectedItem().toString();
                    if (reportType.equalsIgnoreCase("Select Report")) {

                    }else {
//                    Toast.makeText(context, ""+reportType, Toast.LENGTH_SHORT).show();
                    }
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
                            presenter.loadViewReport(from_dateSelected, to_dateSelected
                                    , view_item_arr, circleCode,divCode,rangeCode,secCode,beatCode,view_recyclerV,
                                    ViewReportFragment.this,no_data,reportType.toLowerCase(),scrollView,
                                    scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),progress_bar_LL
                                    ,date_txt,report_count_txt,updated_by);
                        }
                        else if (roleID.equalsIgnoreCase("4") || roleID.equalsIgnoreCase("5")||
                        roleID.equalsIgnoreCase("7")||roleID.equalsIgnoreCase("8") || roleID.equalsIgnoreCase("9")){ //Ranger level
                            circleCode=circleId;
                            divCode=divId;
                            rangeCode=rangeId;

                            presenter.loadViewReport(from_dateSelected, to_dateSelected
                                    , view_item_arr, circleCode,divCode,rangeCode,secCode,beatCode,view_recyclerV,
                                    ViewReportFragment.this,no_data,reportType.toLowerCase(),scrollView,
                                    scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),progress_bar_LL
                                    ,date_txt,report_count_txt,updated_by);
                        }
                        else {
                            //For PCCF/CWLW/ACF
                            presenter.loadViewReport(from_dateSelected, to_dateSelected
                                    , view_item_arr, circleCode,divCode,rangeCode,secCode,beatCode,view_recyclerV,
                                    ViewReportFragment.this,no_data,reportType.toLowerCase(),scrollView,
                                    scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),progress_bar_LL
                                    ,date_txt,report_count_txt,updated_by);
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

    private void getAllRangeByDivisionId(String DivId,String type) {
        try {

            if (PermissionUtils.check_InternetConnection(context) == "true") {

                    RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);

                    retrofitInterface.getAllRangeByDivid(DivId).enqueue(new Callback<ArrayList<RangeDataByDivId>>() {
                        @Override
                        public void onResponse(Call<ArrayList<RangeDataByDivId>> call, Response<ArrayList<RangeDataByDivId>> response) {

                            if (response.isSuccessful()){

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
                                Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<RangeDataByDivId>> call, Throwable t) {

                            Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                        }
                    });



            }else {
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllSectionByRangeId(String RangeId,String type) {
        try {

                RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);
                progress_bar_LL_filter.setVisibility(View.VISIBLE);
                retrofitInterface.getAllSectionByRangeid(RangeId).enqueue(new Callback<ArrayList<SectionDataByRangeId>>() {
                    @Override
                    public void onResponse(Call<ArrayList<SectionDataByRangeId>> call, Response<ArrayList<SectionDataByRangeId>> response) {

                        if (response.isSuccessful()){
                            progress_bar_LL_filter.setVisibility(View.GONE);
                            sectionDataArrayList.clear();
                            sectionDataArrayList.add(new SectionDataByRangeId("-1","Select Section"));
                            for (int i=0;i<response.body().size();i++) {
                                sectionDataArrayList.add(response.body().get(i));
                            }

                            section_dataAdapter = new ArrayAdapter<SectionDataByRangeId>(context,
                                    android.R.layout.simple_spinner_dropdown_item, sectionDataArrayList);
                            section_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            section_spinner.setAdapter(section_dataAdapter);

                            section_spinner.setSelection(selectedSection);
                        }
                        else {
                            progress_bar_LL_filter.setVisibility(View.GONE);
                            Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<SectionDataByRangeId>> call, Throwable t) {
                        progress_bar_LL_filter.setVisibility(View.GONE);
                        Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });


        } catch (Exception e) {
            progress_bar_LL_filter.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void getAllBeatBySectionId(String SectionId,String type) {
        try {

                RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);
                progress_bar_LL_filter.setVisibility(View.VISIBLE);
                retrofitInterface.getAllBeatBySecId(SectionId).enqueue(new Callback<ArrayList<BeatDataBySecId>>() {
                    @Override
                    public void onResponse(Call<ArrayList<BeatDataBySecId>> call, Response<ArrayList<BeatDataBySecId>> response) {

                        if (response.isSuccessful()){
                            progress_bar_LL_filter.setVisibility(View.GONE);
                            beatDataArrayList.clear();
                            beatDataArrayList.add(new BeatDataBySecId("-1","Select Beat"));
                            for (int i=0;i<response.body().size();i++) {
                                beatDataArrayList.add(response.body().get(i));
                            }

                            beat_dataAdapter = new ArrayAdapter<BeatDataBySecId>(context,
                                    android.R.layout.simple_spinner_dropdown_item, beatDataArrayList);
                            beat_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            beat_spinner.setAdapter(beat_dataAdapter);

                            beat_spinner.setSelection(selectedBeat);
                        }
                        else {
                            progress_bar_LL_filter.setVisibility(View.GONE);
                            Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<BeatDataBySecId>> call, Throwable t) {
                        progress_bar_LL_filter.setVisibility(View.GONE);
                        Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });


        } catch (Exception e) {
            progress_bar_LL_filter.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void call_PdfDialog(Context context,String msg,String dialog_title){

        try {

            AlertDialog.Builder builder=  new AlertDialog.Builder(context);

            builder.setMessage(msg)
                    .setTitle(dialog_title)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (view_item_arr.size()>0){

                                Bitmap bitmap = getBitmapFromView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());
                                String pdf_path=PermissionUtils.createPdfFromImage(getActivity(),new Date().toString(),"",bitmap);
//                        Toast.makeText(context, ""+pdf_path, Toast.LENGTH_SHORT).show();
                                Log.i("pdf_path",""+pdf_path);
                                PermissionUtils.sharePdfInSocialMedia(context,pdf_path);//Share in social media
                            }else {
                                Toast.makeText(context, "No data to create pdf !", Toast.LENGTH_SHORT).show();
                            }

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

    @Override
    public void onClickViewMore(int position, String date, String from_time, String to_time,
                                String latitude, String longitude, String total_elephant,
                                String herd, String makhna, String tusker, String female,
                                String calf, String division, String range, String section,
                                String beat, String location, String report_img, String remark,
                                String reportType, String report,String divisionId,String updatedBy,
                                String indirectReportType,String duplicateReport) {
        try {

//            Toast.makeText(context, "not working...", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
