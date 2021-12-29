package com.orsac.android.pccfwildlife.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orsac.android.pccfwildlife.Fragments.ViewReportFragment;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.ElephantDeathIncidentDataModel;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.IncidentReportDataModel;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Adapters.ViewReportRecyclerVNew;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllCircleData;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.BeatDataBySecId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.DivisionDataByCircleId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.RangeDataByDivId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.SectionDataByRangeId;
import com.orsac.android.pccfwildlife.MyUtils.CommonMethods;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.orsac.android.pccfwildlife.MyUtils.PermissionUtils.convertDate;

public class ViewOwnReportActivity extends AppCompatActivity implements ViewReportRecyclerVNew.Update_clickListener{

    SessionManager session;
    Toolbar toolbar;
    ImageView toolbar_back_img,refresh_img,toolbar_profile_img,download_report;
    TextView toolbar_txt;
    ViewReportRecyclerVNew view_adapter;
    RecyclerView view_recyclerV;
    public String token = "", checkInternet_status = "", divisionNmFrom_pref = "", userId = "",roles="",juridictionID="",
            roleID="",circleId="",divId="",rangeId="",from_dateSelected = "", to_dateSelected = "";
    private int finalfrom, finalto, mYear, mMonth, mDay;
    Calendar mcalender = Calendar.getInstance();
    String m, d;
    ArrayList<ViewReportItemData_obj> view_item_arr;
    public RecyclerView.LayoutManager layoutManager;
    public static String circleCode="All",divCode="All",rangeCode="All",secCode="All",beatCode="All";
    ArrayList<AllCircleData> circleDataArrayList;
    ArrayList<DivisionDataByCircleId> divisionDataByCircleIdArrayList;
    ArrayList<RangeDataByDivId> rangeDataByDivIdArrayList;
    ArrayList<SectionDataByRangeId> sectionDataArrayList;
    ArrayList<BeatDataBySecId> beatDataArrayList;
    PermissionHelperClass permissionHelperClass;
    TextView fromDate, toDate, submit,no_data,progress_txt,report_count_txt,date_txt;
    AppCompatImageView filter_img;
    LinearLayout filter_LL,circle_div_LL,progress_bar_LL,range_LL,beat_LL,date_count_ll;
    CardView submit_CV;
    Dialog filter_dialog;
    AppCompatSpinner direct_indirect_spinner;
    SearchableSpinner division_spinner,circle_spinner,range_spinner,section_spinner,beat_spinner;
    String divisionValue="",division_nm="",reportType="direct",circleValue="",rangeValue="",secValue="",beatValue="",fromDate_alreadySelected="",toDate_alreadySelected="";
    int selecetedCircle=0,selectedDiv=0,selectedRange=0,selectedSection=0,selectedBeat=0;
    ArrayAdapter<AllCircleData> circle_dataAdapter;
    ArrayAdapter<DivisionDataByCircleId> div_dataAdapter;
    ArrayAdapter<RangeDataByDivId> range_dataAdapter;
    ArrayAdapter<SectionDataByRangeId> section_dataAdapter;
    ArrayAdapter<BeatDataBySecId> beat_dataAdapter;
    public static LinearLayout bottom_scrollV;
    public static TextView location_txt,dom_txt,from_time_txt,to_time_txt,report_Type,division_txt,range_txt,section_txt,
            beat_txt,total_no,heardtxt,tuskertxt,maletxt,femaletxt,calftxt,report_through,remarktxt,reporttxt,
            accuracy_txt,updated_by;
    ImageView cancel_img;
    public static ImageView update_img,reportImg,report_through_img,report_share,locate_map_img;
    public static RelativeLayout main_ll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_report_incident_layout);
        try {

            initData();

            clickFunction();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initData() {
        try {
            session = new SessionManager(ViewOwnReportActivity.this);

            token = session.getToken();
            divisionNmFrom_pref = session.getDivision();
            userId = session.getUserID();
            roles = session.getRoles();
            roleID = session.getRoleId();
            circleId = session.getCircleId();
            divId = session.getDivisionId();
            rangeId = session.getRangeId();
            juridictionID = session.getJuridictionID();

//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_monitoring);

            fromDate = findViewById(R.id.fromDate);
            toDate = findViewById(R.id.toDate);
            submit = findViewById(R.id.submit);
            no_data = findViewById(R.id.no_data);
            view_recyclerV = findViewById(R.id.report_recyclerV);
//            progress_bar = findViewById(R.id.progress_bar);
//            scrollView = findViewById(R.id.scrollView);
//            share_all = findViewById(R.id.share_all);
            filter_img = findViewById(R.id.filter_img);
            filter_LL = findViewById(R.id.filter_LL);
            progress_bar_LL=findViewById(R.id.progress_bar_LL);
            progress_txt=findViewById(R.id.progress_txt);

            location_txt=findViewById(R.id.location_txt);
            dom_txt=findViewById(R.id.dom_txt);
            from_time_txt=findViewById(R.id.from_time_txt);
            to_time_txt=findViewById(R.id.to_time_txt);
            division_txt=findViewById(R.id.division_txt);
            range_txt=findViewById(R.id.range_txt);
            section_txt=findViewById(R.id.section_txt);
            beat_txt=findViewById(R.id.beat_txt);
            total_no=findViewById(R.id.total_no);
            heardtxt=findViewById(R.id.heard);
            tuskertxt=findViewById(R.id.tusker);
            maletxt=findViewById(R.id.male);
            femaletxt=findViewById(R.id.female);
            calftxt=findViewById(R.id.calf);
            report_through=findViewById(R.id.report_through);
            report_Type=findViewById(R.id.report_type);
            remarktxt=findViewById(R.id.remark);
            reporttxt=findViewById(R.id.report);
            accuracy_txt=findViewById(R.id.accuracy_txt);
            updated_by=findViewById(R.id.updated_by);
            cancel_img=findViewById(R.id.cancel_img);
            bottom_scrollV=findViewById(R.id.bottom_scrollV);

            update_img=findViewById(R.id.update_img);
            reportImg=findViewById(R.id.report_img);
            report_share=findViewById(R.id.report_share);
            report_through_img=findViewById(R.id.report_through_img);
            locate_map_img=findViewById(R.id.locate_map_img);
            report_count_txt=findViewById(R.id.report_count_txt);
            date_txt=findViewById(R.id.date_txt);
            date_count_ll=findViewById(R.id.date_count_ll);
            main_ll=findViewById(R.id.main_ll);

            toolbar = findViewById(R.id.toolbar_new_id);
            toolbar_profile_img = toolbar.findViewById(R.id.profile_img);
            toolbar_back_img = toolbar.findViewById(R.id.back_img);
            refresh_img = toolbar.findViewById(R.id.refresh_img);
            download_report = toolbar.findViewById(R.id.download_report);
            toolbar_txt = toolbar.findViewById(R.id.toolbar_txt);

            view_item_arr = new ArrayList<>();
            circleDataArrayList=new ArrayList<>();
            divisionDataByCircleIdArrayList=new ArrayList<>();
            rangeDataByDivIdArrayList=new ArrayList<>();
            sectionDataArrayList=new ArrayList<>();
            beatDataArrayList=new ArrayList<>();
            filter_dialog=new Dialog(ViewOwnReportActivity.this);

//            presenter = new ViewReportPresenter(context, this,ViewOwnReportActivity.this);

            view_recyclerV.setHasFixedSize(true);
            view_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview
            layoutManager =  new LinearLayoutManager(ViewOwnReportActivity.this, LinearLayoutManager.VERTICAL, false);//for reverse list make it true
            view_recyclerV.setLayoutManager(layoutManager);

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
            permissionHelperClass=new PermissionHelperClass(ViewOwnReportActivity.this);
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

            toolbar_profile_img.setVisibility(View.GONE);
            toolbar_back_img.setVisibility(View.VISIBLE);
            toolbar_txt.setText("View Report");

            download_report.setVisibility(View.VISIBLE);

            from_dateSelected=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
            to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");

            getReportByUserId(userId,from_dateSelected,to_dateSelected);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clickFunction() {
        try {

            toolbar_back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    finish();
                }
            });


            filter_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Animation animation= AnimationUtils.loadAnimation(ViewOwnReportActivity.this,R.anim.top_to_bottom_anim);
//                    bottom_scrollV.startAnimation(animation);
                    if (bottom_scrollV.getVisibility()==View.VISIBLE){
                        bottom_scrollV.setVisibility(View.GONE);
                    }

                    showFilterDialog(ViewOwnReportActivity.this, PermissionUtils.getCurrentDateMinusOne("dd-MM-yyyy"),
                            PermissionUtils.getCurrentDate("dd-MM-yyyy"));

                }
            });

            cancel_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Animation animation=AnimationUtils.loadAnimation(ViewOwnReportActivity.this,R.anim.top_to_bottom_anim);
                    bottom_scrollV.startAnimation(animation);
                    bottom_scrollV.setVisibility(View.GONE);
                }
            });

            download_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!date_txt.getText().toString().trim().equalsIgnoreCase("")) {
                        CommonMethods commonMethods = new CommonMethods(ViewOwnReportActivity.this);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String mFileName = "wildlife_report_" + timeStamp;

//                        for (int i = 0; i < view_item_arr.size(); i++) {
                            commonMethods.loadAllExcel(main_ll, ViewOwnReportActivity.this,
                                    mFileName, view_item_arr, new ArrayList<ElephantDeathIncidentDataModel>(),
                                    new ArrayList<IncidentReportDataModel>(),"direct_indirect_nil",progress_bar_LL,progress_txt);
//                        }

                    }
                }
            });



        }catch (Exception e){
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
            range_LL=filter_dialog.findViewById(R.id.range_LL);
            beat_LL=filter_dialog.findViewById(R.id.beat_LL);

            circle_div_LL.setVisibility(View.GONE);
            range_LL.setVisibility(View.GONE);
            beat_LL.setVisibility(View.GONE);

//            circleDataArrayList.clear();
//            circleDataArrayList.add(new AllCircleData("-1","Select Circle"));

            if (circle_dataAdapter!=null){
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
                range_dataAdapter = new ArrayAdapter<RangeDataByDivId>(ViewOwnReportActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, rangeDataByDivIdArrayList);
                range_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                range_spinner.setAdapter(range_dataAdapter);
                range_spinner.setSelection(selectedRange);
            }
            if (section_dataAdapter!=null){
                section_spinner.setAdapter(section_dataAdapter);
                section_spinner.setSelection(selectedSection);
            }else{
                section_dataAdapter = new ArrayAdapter<SectionDataByRangeId>(ViewOwnReportActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, sectionDataArrayList);
                section_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                section_spinner.setAdapter(section_dataAdapter);
                section_spinner.setSelection(selectedSection);
            }
            if (beat_dataAdapter!=null){
                beat_spinner.setAdapter(beat_dataAdapter);
                beat_spinner.setSelection(selectedBeat);
            }else{
                beat_dataAdapter = new ArrayAdapter<BeatDataBySecId>(ViewOwnReportActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, beatDataArrayList);
                beat_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                beat_spinner.setAdapter(beat_dataAdapter);
                beat_spinner.setSelection(selectedBeat);
            }

            callAllCircleApi();

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
                    if (PermissionUtils.check_InternetConnection(ViewOwnReportActivity.this) == "true") {

                        if (roleID.equalsIgnoreCase("2")){ //DFO
                            divCode=divId;
                            circleCode=circleId;
//                            presenter.loadViewReport(from_dateSelected, to_dateSelected
//                                    , view_item_arr, circleCode,divCode,rangeCode,secCode,beatCode,view_recyclerV,
//                                    ViewReportFragment.this,no_data,reportType.toLowerCase(),scrollView,
//                                    scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),progress_bar_LL);
                        }
                        else if (roleID.equalsIgnoreCase("4")){ //Ranger
                            circleCode=circleId;
                            divCode=divId;
                            rangeCode=rangeId;

//                            presenter.loadViewReport(from_dateSelected, to_dateSelected
//                                    , view_item_arr, circleCode,divCode,rangeCode,secCode,beatCode,view_recyclerV,
//                                    ViewReportFragment.this,no_data,reportType.toLowerCase(),scrollView,
//                                    scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),progress_bar_LL);
                        }
                        else {
                            //For PCCF/CWLW/ACF
//                            presenter.loadViewReport(from_dateSelected, to_dateSelected
//                                    , view_item_arr, circleCode,divCode,rangeCode,secCode,beatCode,view_recyclerV,
//                                    ViewReportFragment.this,no_data,reportType.toLowerCase(),scrollView,
//                                    scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth(),progress_bar_LL);
                        }

                        getReportByUserId(userId,from_dateSelected,to_dateSelected);
                    }
                    else {
                        Toast.makeText(ViewOwnReportActivity.this, "Please check your internet !", Toast.LENGTH_SHORT).show();
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
            DatePickerDialog dpd = new DatePickerDialog(ViewOwnReportActivity.this,
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
            DatePickerDialog dpd = new DatePickerDialog(ViewOwnReportActivity.this,
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

    private void callAllCircleApi() {
        try {
            if (PermissionUtils.check_InternetConnection(ViewOwnReportActivity.this) == "true") {

                RetrofitInterface retrofitInterface= RetrofitClient.getClient("").create(RetrofitInterface.class);

                retrofitInterface.getAllCircle().enqueue(new Callback<ArrayList<AllCircleData>>() {
                    @Override
                    public void onResponse(Call<ArrayList<AllCircleData>> call, Response<ArrayList<AllCircleData>> response) {

                        if (response.isSuccessful()){
                            circleDataArrayList.clear();
                            circleDataArrayList.add(new AllCircleData("-1","Select Circle"));
                            for (int i=0;i<response.body().size();i++) {

                                circleDataArrayList.add(response.body().get(i));
                            }
                            circle_dataAdapter = new ArrayAdapter<AllCircleData>(ViewOwnReportActivity.this, android.R.layout.simple_spinner_dropdown_item, circleDataArrayList);
                            circle_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            circle_spinner.setAdapter(circle_dataAdapter);

                            circle_spinner.setSelection(selecetedCircle);
                        }else {
                            Toast.makeText(ViewOwnReportActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<AllCircleData>> call, Throwable t) {

                        Toast.makeText(ViewOwnReportActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });

            }else {
                Toast.makeText(ViewOwnReportActivity.this, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getAllDivisionByCircleId(String CircleId,String type){
        try {

            if (type.equalsIgnoreCase("firstTime")){
                divisionDataByCircleIdArrayList.clear();
                divisionDataByCircleIdArrayList.add(new DivisionDataByCircleId("-1","Select Division"));

                final ArrayAdapter<DivisionDataByCircleId> dataAdapter = new ArrayAdapter<DivisionDataByCircleId>(ViewOwnReportActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, divisionDataByCircleIdArrayList);
                dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                division_spinner.setAdapter(dataAdapter);
            }else {
                RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);

                retrofitInterface.getAllDivisionByCircleId(CircleId).enqueue(new Callback<ArrayList<DivisionDataByCircleId>>() {
                    @Override
                    public void onResponse(Call<ArrayList<DivisionDataByCircleId>> call, Response<ArrayList<DivisionDataByCircleId>> response) {

                        if (response.isSuccessful()){

                            divisionDataByCircleIdArrayList.clear();
                            divisionDataByCircleIdArrayList.add(new DivisionDataByCircleId("-1","Select Division"));
                            for (int i=0;i<response.body().size();i++) {
                                divisionDataByCircleIdArrayList.add(response.body().get(i));
                            }

                            div_dataAdapter = new ArrayAdapter<DivisionDataByCircleId>(ViewOwnReportActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, divisionDataByCircleIdArrayList);
                            div_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            division_spinner.setAdapter(div_dataAdapter);

                            division_spinner.setSelection(selectedDiv);

                        }else {
                            Toast.makeText(ViewOwnReportActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<DivisionDataByCircleId>> call, Throwable t) {
                        Toast.makeText(ViewOwnReportActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getAllRangeByDivisionId(String DivId,String type) {
        try {

            if (PermissionUtils.check_InternetConnection(ViewOwnReportActivity.this) == "true") {

                RetrofitInterface retrofitInterface= RetrofitClient.getClient("").create(RetrofitInterface.class);

                retrofitInterface.getAllRangeByDivid(DivId).enqueue(new Callback<ArrayList<RangeDataByDivId>>() {
                    @Override
                    public void onResponse(Call<ArrayList<RangeDataByDivId>> call, Response<ArrayList<RangeDataByDivId>> response) {

                        if (response.isSuccessful()){

                            rangeDataByDivIdArrayList.clear();
                            rangeDataByDivIdArrayList.add(new RangeDataByDivId("-1","Select Range"));
                            for (int i=0;i<response.body().size();i++) {
                                rangeDataByDivIdArrayList.add(response.body().get(i));
                            }

                            range_dataAdapter = new ArrayAdapter<RangeDataByDivId>(ViewOwnReportActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, rangeDataByDivIdArrayList);
                            range_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            range_spinner.setAdapter(range_dataAdapter);

                            range_spinner.setSelection(selectedRange);
                        }
                        else {
                            Toast.makeText(ViewOwnReportActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<RangeDataByDivId>> call, Throwable t) {

                        Toast.makeText(ViewOwnReportActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });



            }else {
                Toast.makeText(ViewOwnReportActivity.this, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllSectionByRangeId(String RangeId,String type) {
        try {

            RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);

            retrofitInterface.getAllSectionByRangeid(RangeId).enqueue(new Callback<ArrayList<SectionDataByRangeId>>() {
                @Override
                public void onResponse(Call<ArrayList<SectionDataByRangeId>> call, Response<ArrayList<SectionDataByRangeId>> response) {

                    if (response.isSuccessful()){

                        sectionDataArrayList.clear();
                        sectionDataArrayList.add(new SectionDataByRangeId("-1","Select Section"));
                        for (int i=0;i<response.body().size();i++) {
                            sectionDataArrayList.add(response.body().get(i));
                        }

                        section_dataAdapter = new ArrayAdapter<SectionDataByRangeId>(ViewOwnReportActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, sectionDataArrayList);
                        section_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                        section_spinner.setAdapter(section_dataAdapter);

                        section_spinner.setSelection(selectedSection);
                    }
                    else {
                        Toast.makeText(ViewOwnReportActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<SectionDataByRangeId>> call, Throwable t) {

                    Toast.makeText(ViewOwnReportActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllBeatBySectionId(String SectionId,String type) {
        try {

            RetrofitInterface retrofitInterface=RetrofitClient.getClient("").create(RetrofitInterface.class);

            retrofitInterface.getAllBeatBySecId(SectionId).enqueue(new Callback<ArrayList<BeatDataBySecId>>() {
                @Override
                public void onResponse(Call<ArrayList<BeatDataBySecId>> call, Response<ArrayList<BeatDataBySecId>> response) {

                    if (response.isSuccessful()){

                        beatDataArrayList.clear();
                        beatDataArrayList.add(new BeatDataBySecId("-1","Select Beat"));
                        for (int i=0;i<response.body().size();i++) {
                            beatDataArrayList.add(response.body().get(i));
                        }

                        beat_dataAdapter = new ArrayAdapter<BeatDataBySecId>(ViewOwnReportActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, beatDataArrayList);
                        beat_dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                        beat_spinner.setAdapter(beat_dataAdapter);

                        beat_spinner.setSelection(selectedBeat);
                    }
                    else {
                        Toast.makeText(ViewOwnReportActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<BeatDataBySecId>> call, Throwable t) {

                    Toast.makeText(ViewOwnReportActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReportByUserId(String userid,String fromDate,String toDate){
        try {

            progress_bar_LL.setVisibility(View.VISIBLE);
            progress_txt.setText("Fetching reports...please wait !");
            RetrofitInterface retrofitInterface=RetrofitClient.getReportClient().create(RetrofitInterface.class);

            retrofitInterface.getviewReportsByUserId(userId,fromDate,toDate).enqueue(new Callback<ArrayList<ViewReportItemData_obj>>() {
                @Override
                public void onResponse(Call<ArrayList<ViewReportItemData_obj>> call, Response<ArrayList<ViewReportItemData_obj>> response) {
                    if (response.isSuccessful()){
                        progress_bar_LL.setVisibility(View.GONE);
                        view_item_arr.clear();
                        for (int i=0;i<response.body().size();i++){

//                        Toast.makeText(context, ""+response.body().get(i), Toast.LENGTH_SHORT).show();
                            view_item_arr.add(response.body().get(i));

                        }
                        if (view_item_arr.size()==0){
//                            Toast.makeText(ViewOwnReportActivity.this, "No data !", Toast.LENGTH_SHORT).show();
                            no_data.setVisibility(View.VISIBLE);
                            view_recyclerV.setVisibility(View.GONE);
                            date_count_ll.setVisibility(View.GONE);
                        }else {
                            date_count_ll.setVisibility(View.VISIBLE);
                            report_count_txt.setText("Count : "+view_item_arr.size());
                            date_txt.setText("Date : "+
                                    PermissionUtils.convertDate(fromDate,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy")+" to "+
                                    PermissionUtils.convertDate(toDate,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy"));

                            no_data.setVisibility(View.INVISIBLE);
                            view_recyclerV.setVisibility(View.VISIBLE);
                            view_adapter=new ViewReportRecyclerVNew(ViewOwnReportActivity.this,view_item_arr,ViewOwnReportActivity.this,
                                    "view_user_report");
                            view_recyclerV.setAdapter(view_adapter);
                            view_adapter.notifyDataSetChanged();
                        }


                    }else {
                        no_data.setVisibility(View.INVISIBLE);
                        progress_bar_LL.setVisibility(View.GONE);
                        if (response.code()==500){
                            date_count_ll.setVisibility(View.GONE);
                            Toast.makeText(ViewOwnReportActivity.this, "Internal Server error !", Toast.LENGTH_SHORT).show();
                        }else {
                            date_count_ll.setVisibility(View.GONE);
                            Toast.makeText(ViewOwnReportActivity.this, "Please try again...!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ViewReportItemData_obj>> call, Throwable t) {
                    progress_bar_LL.setVisibility(View.GONE);
                    no_data.setVisibility(View.INVISIBLE);
                    date_count_ll.setVisibility(View.GONE);
                    Toast.makeText(ViewOwnReportActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });



        }catch (Exception e){
            progress_bar_LL.setVisibility(View.GONE);
            date_count_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    @Override
    public void on_CardViewClick(int position, String from_time, String to_time, Bitmap bitmap) {

    }

    @Override
    public void onMapPointClick(int position, double latitude, double longitude) {

    }

    @Override
    public void onClickViewMore(int position, String date, String from_time, String to_time, String latitude, String longitude,
                                String total_elephant, String herd, String makhna, String tusker, String female, String calf,
                                String division, String range, String section, String beat, String location, String report_img,
                                String remark, String reportType, String report, String divisionId,
                                String updatedBy,String indirectReportType,String duplicateReport) {

        try {

            dom_txt.setText(convertDate(date,
                    "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy"
            ));
            from_time_txt.setText(convertDate(from_time,
                    "yyyy-MM-dd HH:mm:ss.SSS","HH:mm"
            ));
            to_time_txt.setText(convertDate(to_time,
                    "yyyy-MM-dd HH:mm:ss.SSS","HH:mm"
            ));

            if (total_elephant==null ||total_elephant.equalsIgnoreCase("null")){
                total_no.setText("NA");
            }else {
                total_no.setText(total_elephant);
            }
            if (herd==null ||herd.equalsIgnoreCase("null")){
                heardtxt.setText("NA");
            }else {
                heardtxt.setText(herd);
            }
            if (makhna==null ||makhna.equalsIgnoreCase("null")){
                maletxt.setText("NA");
            }else {
                maletxt.setText(makhna);
            }
            if (tusker==null ||tusker.equalsIgnoreCase("null")){
                tuskertxt.setText("NA");
            }else {
                tuskertxt.setText(tusker);
            }
            if (female==null ||female.equalsIgnoreCase("null")){
                femaletxt.setText("NA");
            }else {
                femaletxt.setText(female);
            }

            if (calf==null ||calf.equalsIgnoreCase("null")){
                calftxt.setText("NA");
            }else {
                calftxt.setText(calf);
            }

            division_txt.setText(division);
            range_txt.setText(range);
            section_txt.setText(section);
            beat_txt.setText(beat);
            location_txt.setText(location);

            if (remark==null ||remark.equalsIgnoreCase("null")){
                remarktxt.setText("NA");
            }else {
                remarktxt.setText(remark);
            }
            //for indirect report type select text
            if (indirectReportType.equalsIgnoreCase("")){
                if (report.equalsIgnoreCase("nill") || report.equalsIgnoreCase("nil")){
                    report_Type.setText("Nil");
                }else{
                    report_Type.setText(report);
                }
            }else {
                report_Type.setText(report+" ( "+indirectReportType+" )");
            }
//            report_Type.setText(report);

            if (report==null || report.equalsIgnoreCase("null")){
                reporttxt.setText("Report");
            }else if (report.equalsIgnoreCase("nill") || report.equalsIgnoreCase("nil")){
                reporttxt.setText("NIL");
            }
            else {
                reporttxt.setText(report.toUpperCase());
            }

            if (latitude==null ||latitude.equalsIgnoreCase("null")){
                accuracy_txt.setText("NA");
            }else {
                accuracy_txt.setText(latitude);
            }
            if (longitude==null ||longitude.equalsIgnoreCase("null")){
                report_through.setText("NA");
            }else {
                report_through.setText(longitude);
            }
            if (updatedBy==null ||updatedBy.equalsIgnoreCase("null")){
                updated_by.setText("NA");
            }else {
                updated_by.setText(updatedBy);
            }

            ViewOwnReportActivity.report_through_img.setVisibility(View.VISIBLE);
            //-----check it with mobile and web
            if (reportType.equalsIgnoreCase("Mobile")){
                ViewOwnReportActivity.report_through_img.setImageDrawable(getResources().getDrawable(R.drawable.phone_img));
//                Toast.makeText(context, "Mobile", Toast.LENGTH_SHORT).show();
            }else if (reportType.equalsIgnoreCase("Web")){
//                Toast.makeText(context, "Web", Toast.LENGTH_SHORT).show();
                ViewOwnReportActivity.report_through_img.setImageDrawable(getResources().getDrawable(R.drawable.web_img));
            }

            //Show report_img
            reportImg.setVisibility(View.VISIBLE);

            if (report_img==null){
                Glide.with(ViewOwnReportActivity.this)
                        .load(R.drawable.logo)
                        .error(R.drawable.no_image_found)
                        .into(reportImg);

            }else {
                Glide.with(ViewOwnReportActivity.this)
//                        .load("http://203.129.207.133:4200/wildlife/api/v1/uploadController/downloadFile?location="+report_img)
                        .load(RetrofitClient.IMAGE_URL+"report/"+report_img)
                        .error(R.drawable.no_image_found)
                        .into(reportImg);
            }

            report_through_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ViewOwnReportActivity.this, "It is reported from "+reportType, Toast.LENGTH_SHORT).show();
                }
            });

            locate_map_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        if (reportType.equalsIgnoreCase("nill")){
//                            Toast.makeText(ViewOwnReportActivity.this, "Not a valid point to plot in map !", Toast.LENGTH_SHORT).show();
//                        }else {
                            if (latitude.equalsIgnoreCase("0.0")){

                                Toast.makeText(ViewOwnReportActivity.this, "Not a valid point to plot in map !", Toast.LENGTH_SHORT).show();

                            }else {
                                Intent intent=new Intent(ViewOwnReportActivity.this, ViewReportMapPointActivity.class);
                                intent.putExtra("lat",latitude);
                                intent.putExtra("lng",longitude);
                                intent.putExtra("reportType",report);
                                intent.putExtra("divisionId",divisionId);
                                intent.putExtra("divisionNm",division);
                                intent.putExtra("rangeNm",range);
                                intent.putExtra("total",total_elephant);
                                intent.putExtra("herd",herd);
                                intent.putExtra("date",date);
                                intent.putExtra("image",report_img);
                                startActivity(intent);
                            }

//                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            reportImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (report_img!=null){
                        callImageViewDialog(ViewOwnReportActivity.this,
                                RetrofitClient.IMAGE_URL+"report/"+report_img);
                    }
                }
            });

//            report_share.setVisibility(View.GONE);

            report_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Bitmap bitmap=PermissionUtils.createImageScreenShotForView(reportCV,context);
//                Toast.makeText(context, ""+bitmap, Toast.LENGTH_SHORT).show();
//                    update_clickListener.on_CardViewClick(position,viewReportData_arr.get(position).getSighting_time_from(),
//                            viewReportData_arr.get(position).getSighting_time_to(),bitmap);

                    try {

                        CommonMethods commonMethods=new CommonMethods(ViewOwnReportActivity.this);
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
//                                + (int) System.currentTimeMillis();
//                    String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                    mFilePath += "/WildLife Excel Report/" + mFileName;
//
                        commonMethods.loadExcel(main_ll,ViewOwnReportActivity.this,mFileName,
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

            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
