package com.orsac.android.pccfwildlife.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.orsac.android.pccfwildlife.Model.AllReportCountModel;
import com.orsac.android.pccfwildlife.Model.AllReportCountModelInADay;
import com.orsac.android.pccfwildlife.Model.IncidentDataModel;
import com.orsac.android.pccfwildlife.Model.ReportCountItemModel;
import com.orsac.android.pccfwildlife.Model.ViewIncidentReportCount;
import com.orsac.android.pccfwildlife.Activities.IncidentReportViewActivity;
import com.orsac.android.pccfwildlife.Activities.VulnerableElephantDetailsActivity;
import com.orsac.android.pccfwildlife.Adapters.ReportCountAdapter;
import com.orsac.android.pccfwildlife.Contract.ViewReportContract;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllCircleData;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.DivisionDataByCircleId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.RangeDataByDivId;
import com.orsac.android.pccfwildlife.MyUtils.CommonMethods;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

public class DashboardFragment extends Fragment implements ReportCountAdapter.onReportCountClickListener{

    Context context;
//    HIChartView barChart,animal_death_bar_chart,elephant_death_bar_chart;
    ArrayList<String> division_yAxis;
    ArrayList<AllReportCountModel> reportCountModelArrayList;
    ArrayList<ReportCountItemModel> reportModelArrayList;
    ReportCountAdapter reportCountAdapter;
    SQLiteDatabase db;
    ProgressBar progress_bar;
    ScrollView main_ll;
    BottomNavigationView b_nav_view;
    AppCompatImageView filter_img,filter_imgg,up_img;
    RecyclerView report_recyclerV;
    RecyclerView.LayoutManager layoutManager;
    TextView division_select,all_count_txt,direct_count_txt,indirect_count_txt,nil_count_txt,
            all_vulnerable_count_txt;
    SessionManager session;
    ViewReportContract.presenter presenter;
    ViewReportFragment viewReportFragment;

    public HashMap<String, String> divKey;
    public HashMap<String, String> rangeKey;
    public HashMap<String, String> circleKey;
    public HashMap<String, String> secKey;
    public HashMap<String, String> beatkey;
    String divisionValue="", rangeValue="", secValue="", beatValue="",elephant_type="",circleValue="";
    public String secCode="", beatCode="",juridictionNm="",roles="",juridictionID="",roleID="",
            circleId="",divId="",rangeId="";

    ArrayList<IncidentDataModel> incidentDataModels;
    PieChart chart,elephant_piechart;
    SearchableSpinner division_spinner,range_spinner,circle_spinner,section_spinner,beat_spinner;
    AppCompatSpinner direct_indirect_spinner;
    ArrayList<AllCircleData> circleDataArrayList;
    ArrayList<DivisionDataByCircleId> divisionDataByCircleIdArrayList;
    ArrayList<RangeDataByDivId> rangeDataByDivIdArrayList;
    ArrayList<String> animal_array_name = new ArrayList<String>();
    ArrayList<Float> animal_array_value = new ArrayList<Float>();

    ArrayList<String> elephant_array_name = new ArrayList<String>();
    ArrayList<Float> elephant_array_value = new ArrayList<Float>();
    AppCompatButton filter_btn;
    PermissionHelperClass permissionHelperClass;
    public String circleCode="All",divCode="All", rangeCode="All",from_dateSelected="",to_dateSelected="",fromDate_alreadySelected="",toDate_alreadySelected="";
    LinearLayout filter_ll,progress_bar_LL,circle_div_LL,beat_LL,progress_bar_LL_filter;
    CardView submit_CV;
    Dialog filter_dialog;
    TextInputLayout report_type_spinnerTIL;
    TextView fromDate, toDate,elephant_death_count_txt,elephant_death_name,fire_count_txt,
            fire_report_name,title_dialog,divisionTxt,no_data_available_incident_txt,
            no_data_available_elephant_death_txt,date_selected;
    private int mYear, mMonth, mDay;
    String m, d;
    Calendar mcalender = Calendar.getInstance();
    int selecetedCircle=0,selectedDiv=0,selectedRange=0;
    ArrayAdapter<AllCircleData> circle_dataAdapter;
    ArrayAdapter<DivisionDataByCircleId> div_dataAdapter;
    ArrayAdapter<RangeDataByDivId> range_dataAdapter;
    ConstraintLayout main_Constraint_ll,error_ll;
    Button try_again;
    CardView elephant_death_count_CV,fire_count_CV,direct_count_CV,indirect_count_CV,
            nil_count_CV,all_count_CV,all_vulnerable_CV;
    public static String circleStr="",divisionStr="",rangeStr="",dateFrom="",dateTo="";
//    CommonMethods commonMethods;
//    CountDownTimer timer=null;

    public DashboardFragment(BottomNavigationView bottomNavigationView) {
        this.b_nav_view=bottomNavigationView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dashboard_fragment_layout,container,false);

        intitData(view);

        click_function();

//        createHiChartData();

        return view;
    }



    public View intitData(View view){

        try {
        progress_bar=view.findViewById(R.id.progress_bar);
        main_ll=view.findViewById(R.id.main_ll);
        report_recyclerV=view.findViewById(R.id.report_recyclerV);
//        filter_img=view.findViewById(R.id.filter_img);
        filter_imgg=view.findViewById(R.id.filter_imgg);
        up_img=view.findViewById(R.id.up_img);
        division_select=view.findViewById(R.id.division_select);
        all_count_txt=view.findViewById(R.id.all_count_txt);
        all_vulnerable_count_txt=view.findViewById(R.id.all_vulnerable_count_txt);
        direct_count_txt=view.findViewById(R.id.direct_count_txt);
        indirect_count_txt=view.findViewById(R.id.indirect_count_txt);
        nil_count_txt=view.findViewById(R.id.nil_count_txt);
        date_selected=view.findViewById(R.id.date_selected);

        elephant_death_count_txt=view.findViewById(R.id.elephant_death_count_txt);
        elephant_death_name=view.findViewById(R.id.elephant_death_name);
        fire_count_txt=view.findViewById(R.id.fire_count_txt);
        fire_report_name=view.findViewById(R.id.fire_report_name);
        no_data_available_incident_txt=view.findViewById(R.id.no_data_available_incident_txt);
        no_data_available_elephant_death_txt=view.findViewById(R.id.no_data_available_elephant_death_txt);

        division_yAxis=new ArrayList<>();
        reportCountModelArrayList=new ArrayList<>();
        reportModelArrayList=new ArrayList<>();
        circleDataArrayList=new ArrayList<>();
        divisionDataByCircleIdArrayList=new ArrayList<>();
        rangeDataByDivIdArrayList=new ArrayList<>();

        session = new SessionManager(context);
        viewReportFragment=new ViewReportFragment();
        incidentDataModels=new ArrayList<>();
        chart=view.findViewById(R.id.piechart);
        elephant_piechart=view.findViewById(R.id.elephant_piechart);

        filter_ll=view.findViewById(R.id.filter_ll);
        progress_bar_LL=view.findViewById(R.id.progress_bar_LL);
        filter_btn=view.findViewById(R.id.filter_btn);
        main_Constraint_ll=view.findViewById(R.id.main_constraint);
        error_ll=view.findViewById(R.id.error_ll);
        try_again=view.findViewById(R.id.try_again);
        elephant_death_count_CV=view.findViewById(R.id.elephant_death_count_CV);
        fire_count_CV=view.findViewById(R.id.fire_count_CV);
        direct_count_CV=view.findViewById(R.id.direct_count_CV);
        indirect_count_CV=view.findViewById(R.id.indirect_count_CV);
        nil_count_CV=view.findViewById(R.id.nil_count_CV);
        all_count_CV=view.findViewById(R.id.all_count_CV);
        all_vulnerable_CV=view.findViewById(R.id.all_vulnerable_CV);

        //For now chart is gone
        chart.setVisibility(View.GONE);
        no_data_available_incident_txt.setVisibility(View.GONE);
//        commonMethods=new CommonMethods(context);

        juridictionNm=session.getJuridiction();
        juridictionID=session.getJuridictionID();
        roles=session.getRoles();
        roleID=session.getRoleId();
        circleId=session.getCircleId();
        divId=session.getDivisionId();
        rangeId=session.getRangeId();

        from_dateSelected=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
        to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");

//        timer=commonMethods.logoutSessionExpired(1);

            circleDataArrayList.clear();
            circleDataArrayList.add(new AllCircleData("-1","Select Circle"));

            divisionDataByCircleIdArrayList.clear();
            divisionDataByCircleIdArrayList.add(new DivisionDataByCircleId("-1","Select Division"));

            rangeDataByDivIdArrayList.clear();
            rangeDataByDivIdArrayList.add(new RangeDataByDivId("-1","Select Range"));

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

            date_selected.setText("Date : "+
                    PermissionUtils.convertDate(from_dateSelected,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy")+" to "+
                    PermissionUtils.convertDate(to_dateSelected,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy"));



            if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")|| roleID.equalsIgnoreCase("6")){
            division_select.setText("Login with Circle : "+juridictionNm);
            division_select.setVisibility(View.GONE);

            circleStr="";
            divisionStr="";

            callViewincidentReportCount("","","",from_dateSelected,to_dateSelected);//It is to showing count on set data in pie chart
            callViewElephantDeathReportCount("","","",from_dateSelected,to_dateSelected);//It is to showing elephant death count on set data in pie chart

            if (divCode.equalsIgnoreCase("-1")||divCode.equalsIgnoreCase("")){
                callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);
            }
            else {
                callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);
            }

        }
        else if (roleID.equalsIgnoreCase("2")){ //DFO
            division_select.setText("Login with Division : "+juridictionNm);
            division_select.setVisibility(View.VISIBLE);

            circleCode=circleId;
            divCode=divId;

            circleStr=circleCode;
            divisionStr=divCode;

            callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//call report count api

            //Here give div id
            callViewincidentReportCount(circleCode,divCode,"",from_dateSelected,to_dateSelected);//It is to showing count on set data in pie chart
            callViewElephantDeathReportCount(circleCode,divCode,"",from_dateSelected,to_dateSelected);//It is to showing elephant death count on set data in pie chart


        }
        else if (roleID.equalsIgnoreCase("4") || roleID.equalsIgnoreCase("5")){
            division_select.setText("Login with Range : "+juridictionNm);
            division_select.setVisibility(View.VISIBLE);
        }
        else if (roleID.equalsIgnoreCase("7") || roleID.equalsIgnoreCase("8") ||
                roleID.equalsIgnoreCase("9")){
            division_select.setText("Login with area : "+juridictionNm);
            division_select.setVisibility(View.VISIBLE);

        }
        else {
            division_select.setText("Login with Range : "+juridictionNm);

        }


        PermissionUtils.showProgress(context,progress_bar);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PermissionUtils.hideProgress(context,progress_bar);
            }
        },2000);

//        callAllCircleApi();//For Pccf login


//        callReportCountApi("All");//call report count  api


        }catch (Exception e){
            e.printStackTrace();
        }

        return view;

    }


    private void click_function() {
        try {

            filter_imgg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

//                        permissionUtils.loadExcel(getActivity(),"WildlifeExcel");//for excel file

                        showFilterDialog(context,PermissionUtils.getCurrentDateMinusOne("dd-MM-yyyy"),
                                PermissionUtils.getCurrentDate("dd-MM-yyyy"));

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            try_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
              try {
                  error_ll.setVisibility(View.GONE);
                  main_Constraint_ll.setVisibility(View.VISIBLE);

              if (roleID.equalsIgnoreCase("1")||roleID.equalsIgnoreCase("3")|| roleID.equalsIgnoreCase("6")){
              division_select.setText("Login with Circle : "+juridictionNm);
              division_select.setVisibility(View.GONE);


            callViewincidentReportCount("","","",from_dateSelected,to_dateSelected);//It is to showing count on set data in pie chart
            callViewElephantDeathReportCount("","","",from_dateSelected,to_dateSelected);//It is to showing elephant death count on set data in pie chart

            if (divCode.equalsIgnoreCase("-1")||divCode.equalsIgnoreCase("")){
                callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);
            }
            else {
                callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);
            }

         }
          else if (roleID.equalsIgnoreCase("2")){ //DFO
            division_select.setText("Login with Division : "+juridictionNm);
            division_select.setVisibility(View.VISIBLE);

            circleCode=circleId;
            divCode=divId;

            circleStr=circleCode;
            divisionStr=divCode;


            callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//call report count api

            //Here give div id
            callViewincidentReportCount(circleCode,divCode,"",from_dateSelected,to_dateSelected);//It is to showing count on set data in pie chart
            callViewElephantDeathReportCount(circleCode,divCode,"",from_dateSelected,to_dateSelected);//It is to showing elephant death count on set data in pie chart


        }
         else if (roleID.equalsIgnoreCase("4")){
            division_select.setText("Login with Range : "+juridictionNm);
            division_select.setVisibility(View.VISIBLE);

        }
        else {
            division_select.setText("Login with Range : "+juridictionNm);

        }

        }catch (Exception e){
           e.printStackTrace();
          }

         }
       });

            all_count_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateFrom=from_dateSelected;
                    dateTo=to_dateSelected;
                    if (all_count_txt.getText().toString().trim().equalsIgnoreCase("0")){

                        Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();

                    }else {
                        Intent intent=new Intent(context, IncidentReportViewActivity.class);
                        intent.putExtra("reportType","all");
                        startActivity(intent);
                    }
                }
            });

            all_vulnerable_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (all_vulnerable_count_txt.getText().toString().trim().equalsIgnoreCase("0")){

                        Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();

                    }else {
                        Intent intent=new Intent(context, VulnerableElephantDetailsActivity.class);
                        startActivity(intent);
                    }

                }
            });

            direct_count_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateFrom=from_dateSelected;
                    dateTo=to_dateSelected;
                    if (direct_count_txt.getText().toString().trim().equalsIgnoreCase("0")){

                        Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();

                    }else {
                        Intent intent=new Intent(context, IncidentReportViewActivity.class);
                        intent.putExtra("reportType","direct");
                        startActivity(intent);
                    }
                }
            });

            indirect_count_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateFrom=from_dateSelected;
                    dateTo=to_dateSelected;
                    if (indirect_count_txt.getText().toString().trim().equalsIgnoreCase("0")){

                        Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();

                    }else {
                        Intent intent=new Intent(context, IncidentReportViewActivity.class);
                        intent.putExtra("reportType","indirect");
                        startActivity(intent);
                    }
                }
            });

            nil_count_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateFrom=from_dateSelected;
                    dateTo=to_dateSelected;
                    if (nil_count_txt.getText().toString().trim().equalsIgnoreCase("0")){

                        Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();

                    }else {
                        Intent intent=new Intent(context, IncidentReportViewActivity.class);
                        intent.putExtra("reportType","nil");
                        startActivity(intent);
                    }
                }
            });

            elephant_death_count_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateFrom=from_dateSelected;
                    dateTo=to_dateSelected;
                    if (elephant_death_count_txt.getText().toString().trim().equalsIgnoreCase("0")){

                        Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();

                    }else {
                        Intent intent=new Intent(context, IncidentReportViewActivity.class);
                        intent.putExtra("reportType","elephantDeath");
                        startActivity(intent);
                    }
                }
            });

            fire_count_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateFrom=from_dateSelected;
                    dateTo=to_dateSelected;
                    if (fire_count_txt.getText().toString().trim().equalsIgnoreCase("0")){

                        Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();

                    }else {
                        Intent intent=new Intent(context, IncidentReportViewActivity.class);
                        intent.putExtra("reportType","fire");
                        startActivity(intent);
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    public void callChart_wildDepredation(ArrayList<IncidentDataModel> incident_Arr) {
//
////        barChart.plugins = new ArrayList<>(Arrays.asList("drilldown"));
//        HIOptions options = new HIOptions();
//
//        HIChart chart = new HIChart();
////        chart.setType("column");
//
//        //Remove the exporting sign(hamburger) and highchart.com(credit)
//        HIExporting exporting=new HIExporting();
//        exporting.setEnabled(false);
//        HICredits credits=new HICredits();
//        credits.setEnabled(false);
//
//        HITitle title = new HITitle();
//        title.setText("");
//
//        options.setChart(chart);
//        options.setExporting(exporting);
//        options.setCredits(credits);
//
////        title.setText("Browser market shares. January, 2015 to May, 2015");
//        options.setTitle(title);
////
////        HISubtitle subtitle = new HISubtitle();
////        subtitle.setText("Click the columns to view versions. 'Source': <a href=\"http://netmarketshare.com\">netmarketshare.com</a>.");
////        options.setSubtitle(subtitle);
//
//        //For X-axis data show
//        HIXAxis xAxis = new HIXAxis();
//        xAxis.setType("category");
//        xAxis.setLabels(new HILabels());
//        xAxis.getLabels().setRotation(0);
////        xAxis.getLabels().setAlign("right");
//        xAxis.getLabels().setStaggerLines(1);//For line in xaxis
//        options.setXAxis(new ArrayList<HIXAxis>(){{add(xAxis);}});
//
//        //For Y-axis data show
//        HIYAxis yAxis = new HIYAxis();
//        yAxis.setTitle(new HITitle());
//        yAxis.getTitle().setText("No. of incident");
//        yAxis.setLabels(new HILabels());
//
//        yAxis.setMin(0);
////        yAxis.setMax(100);
//        yAxis.setAxisTitle(new HITitle());
//        yAxis.setAlignTicks(true);
//        yAxis.getLabels().setPoint(new HIPoint());
////        yAxis.getLabels().getPoint().setYAxis(categoriesList);
//
//        options.setYAxis(new ArrayList<HIYAxis>(){{add(yAxis);}});
//
//        HILegend legend = new HILegend();
//        legend.setEnabled(false);
//        options.setLegend(legend);
//
//        HIPlotOptions plotOptions = new HIPlotOptions();
//        plotOptions.setSeries(new HISeries());
//
//        options.setPlotOptions(plotOptions);
//
//        HIColumn series1 = new HIColumn();
//        series1.setName("Incident");
//        series1.setColorByPoint(true);
//        series1.setCenterInCategory(true);
//
//        HIDataLabels dataLabels=new HIDataLabels();
//        dataLabels.setEnabled(true);
//        dataLabels.setRotation(0);
////        dataLabels.setColor(HIColor.initWithHexValue("FFFFFF"));
//
//        dataLabels.setAlign("left");
//        dataLabels.setFormat("{point.y:.2f}");
//        dataLabels.setStyle(new HIStyle());
//        dataLabels.getStyle().setFontSize("10px");
//        dataLabels.getStyle().setWhiteSpace("");
////        series1.setDataLabels(new HIDataLabels());
////        series1.getDataLabels().setEnabled(true);
//        series1.setDataLabels(new ArrayList<>(Arrays.asList(dataLabels)));
//        series1.setCenterInCategory(true);
//        HashMap<String, Object> map1 = new HashMap<>();
//
//
//        HashMap[] series1_data = new HashMap[] { map1 };
////        HashMap[] series1_data = new HashMap[] { map1, map2, map3, map4, map5 };
//        series1.setData(new ArrayList<>(Arrays.asList(series1_data)));
//        series1.setDataLabels(new ArrayList<>(Arrays.asList(dataLabels)));
//        options.setSeries(new ArrayList<>(Arrays.asList(series1)));
//
//
//        barChart.setOptions(options);
//
//
//    }
//
//    public void callChart_ElephantDeath() {
////        PermissionUtils.hideProgress(context,progress_bar);
//        HIOptions options = new HIOptions();
//        HIChart chart = new HIChart();
//        chart.setType("bar");
//
//        //Remove the exporting sign(hamburger) and highchart.com(credit)
//        HIExporting exporting=new HIExporting();
//        exporting.setEnabled(false);
//        HICredits credits=new HICredits();
//        credits.setEnabled(false);
//
//        HITitle title = new HITitle();
//        title.setText("");
//
//        options.setChart(chart);
//        options.setExporting(exporting);
//        options.setCredits(credits);
//
//        options.setTitle(title);
//        //For X-axis data show
//
//        HIXAxis xaxis = new HIXAxis();
////        xaxis.setAlternateGridColor(HIColor.initWithHexValue("444444"));
//
////        xaxis.setCategories(getDivisionName());
////        xaxis.getLabels().setStaggerLines(1);//For line in xaxis
//        options.setXAxis(new ArrayList<HIXAxis>(){{add(xaxis);}});
//
//        //For Y-axis data show
//        HIYAxis yAxis = new HIYAxis();
//        yAxis.setMin(0);
////        yAxis.setMax(10);
//        yAxis.setTitle(new HITitle());
//        yAxis.getTitle().setText("No. of Deaths");
//        yAxis.setLabels(new HILabels());
//
//        yAxis.setAxisTitle(new HITitle());
//        yAxis.setAlignTicks(true);
//        yAxis.getLabels().setPoint(new HIPoint());
////        yAxis.getLabels().getPoint().setYAxis(categoriesList);
//
//        options.setYAxis(new ArrayList<HIYAxis>(){{add(yAxis);}});
//
//
//        HILegend legend = new HILegend();
//        legend.setEnabled(false);
//        options.setLegend(legend);
//
//        HIPlotOptions plotOptions = new HIPlotOptions();
//        plotOptions.setSeries(new HISeries());
//
//        options.setPlotOptions(plotOptions);
//
//        HIColumn series1 = new HIColumn();
////        series1.setName("Brands");
//        series1.setColorByPoint(true);//Set color of bar default
////        series1.setCenterInCategory(true);
//
//        HIDataLabels dataLabels=new HIDataLabels();
//        dataLabels.setEnabled(true);
//        dataLabels.setRotation(0);
////        dataLabels.setColor(HIColor.initWithHexValue("FFFFFF"));
//
//        dataLabels.setAlign("left");
////        dataLabels.setFormat("{point.y:.2f}");
//        dataLabels.setStyle(new HIStyle());
//        dataLabels.getStyle().setFontSize("10px");
//        series1.setDataLabels(new ArrayList<>(Arrays.asList(dataLabels)));
////        series1.setCenterInCategory(true);
//
//        HashMap<String, Object> map1 = new HashMap<>();
//        map1.put("name", "Tusker");
//        map1.put("y", 4);
////        map1.put("drilldown", "Microsoft Internet Explorer");
//
//        HashMap<String, Object> map2 = new HashMap<>();
//        map2.put("name", "calf");
//        map2.put("y", 8);
////        map2.put("drilldown", "Chrome");
//
//        HashMap<String, Object> map3 = new HashMap<>();
//        map3.put("name", "Female");
//        map3.put("y", 3);
////        map3.put("drilldown", "Firefox");
//
//        HashMap<String, Object> map4 = new HashMap<>();
//        map4.put("name", "Makhna");
//        map4.put("y", 2);
//
//        HashMap<String, Object> map5 = new HashMap<>();
//        map5.put("name", "Tusker");
//        map5.put("y", 4);
//
//        HashMap<String, Object> map6 = new HashMap<>();
//        map6.put("name", "Herd");
//        map6.put("y", 2);
////        map5.put("drilldown", "Opera");
//
//        HashMap<String, Object> map7 = new HashMap<>();
//        map7.put("name", "Makhna");
//        map7.put("y", 2);
//
//        HashMap<String, Object> map8 = new HashMap<>();
//        map8.put("name", "Calf");
//        map8.put("y", 1);
//
//        HashMap<String, Object> map9 = new HashMap<>();
//        map9.put("name", "Tusker");
//        map9.put("y", 3);
//
//        HashMap<String, Object> map10 = new HashMap<>();
//        map10.put("name", "Calf");
//        map10.put("y", 4);
//
//        options.setSeries(new ArrayList<>(Arrays.asList(series1)));
//
//        elephant_death_bar_chart.setOptions(options);
//
//    }

    boolean isNavigationHide = false;

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide)
            return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * b_nav_view.getHeight()) : 0;
        b_nav_view.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


    private void bindDivision(AppCompatSpinner division_spinner) {
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
    private void bindRange(String divCode,AppCompatSpinner range_spinner) {
        List<String> rangeName = new ArrayList<String>();
        rangeName.add("Select Range");
        rangeKey = new HashMap<>();
        db = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT  DISTINCT Range_ID,Range_Name FROM WlRange_Other where Division_ID='" + divCode + "' order by Range_Name", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                rangeName.add(cursor.getString(cursor.getColumnIndex("Range_Name")));
                rangeKey.put(cursor.getString(cursor.getColumnIndex("Range_Name")), cursor.getString(cursor.getColumnIndex("Range_ID")));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
//        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinneritem, rangeName);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, rangeName);
        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
        range_spinner.setAdapter(dataAdapter);
    }

    public void bindSection(String rangeCode,AppCompatSpinner section_spinner) {
        List<String> secName = new ArrayList<String>();
        secName.add("Select Section");
        secKey = new HashMap<>();
        db = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT  DISTINCT Section_ID,Section_Name FROM WlSection where Range_ID='" + rangeCode + "' order by Section_Name", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                secName.add(cursor.getString(cursor.getColumnIndex("Section_Name")));
                secKey.put(cursor.getString(cursor.getColumnIndex("Section_Name")), cursor.getString(cursor.getColumnIndex("Section_ID")));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, secName);
        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
        section_spinner.setAdapter(dataAdapter);
    }

    public void bindBeat(String secCodee,AppCompatSpinner beat_spinner) {
        List<String> beatName = new ArrayList<String>();
        beatName.add("Select Beat");
        beatkey = new HashMap<>();
        db = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT  DISTINCT WlBeat_ID,WlBeat_Name FROM WlBeat where Section_ID='" + secCode + "' order by WlBeat_Name", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                beatName.add(cursor.getString(cursor.getColumnIndex("WlBeat_Name")));
                beatkey.put(cursor.getString(cursor.getColumnIndex("WlBeat_Name")), cursor.getString(cursor.getColumnIndex("WlBeat_ID")));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, beatName);
        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
        beat_spinner.setAdapter(dataAdapter);
    }

    private void callReportCountApi(String circle,String division,String range,String startDate,String endDate){

        try {
             if (progress_bar_LL.getVisibility()==View.VISIBLE){
                progress_bar_LL.setVisibility(View.GONE);
            }
            progress_bar_LL.setVisibility(View.VISIBLE);
            if (PermissionUtils.check_InternetConnection(context) == "true") {

                if (circle.equalsIgnoreCase("-1")|| circle.equalsIgnoreCase("")){
                    circle="All";
                }
                if (division.equalsIgnoreCase("-1")|| division.equalsIgnoreCase("")){
                    division="All";
                }
                if (range.equalsIgnoreCase("-1") || range.equalsIgnoreCase("")){
                    range="All";
                }

                RetrofitInterface retrofitInterface = RetrofitClient.getReportClient()
                        .create(RetrofitInterface.class);

                retrofitInterface.getAllReportCountIn24hrsss(circle,division,range,startDate,endDate)
                        .enqueue(new Callback<AllReportCountModelInADay>() {
                            @Override
                            public void onResponse(Call<AllReportCountModelInADay> call, Response<AllReportCountModelInADay> response) {
                                if (response.isSuccessful()){

                                    main_Constraint_ll.setVisibility(View.VISIBLE);
                                    error_ll.setVisibility(View.GONE);

                                    progress_bar_LL.setVisibility(View.GONE);
//                    Toast.makeText(context, "Fetching Report Successfully !", Toast.LENGTH_SHORT).show();
                                    report_recyclerV.setHasFixedSize(true);
                                    report_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview

                                    layoutManager = new GridLayoutManager(getActivity(),2);
//                    layoutManager = new LinearLayoutManager(getActivity());
                                    report_recyclerV.setLayoutManager(layoutManager);
                                    String directCount="",allCount="",indirectCount="",elephantDeathCount="",
                                            incidentCount="",nilCount="",vulnerableCount="";
                                    allCount=response.body().getAllReportCount();
                                    directCount=response.body().getDirectReportCount();
                                    indirectCount=response.body().getIndirectReportCount();
                                    elephantDeathCount=response.body().getAllElephantDeathReportCount();
                                    incidentCount=response.body().getAllFireAlertReportCount();
                                    nilCount=response.body().getNillReportCount();
                                    vulnerableCount=response.body().getAllVulnerabilityCount();

                                    reportModelArrayList.clear();
                                    reportModelArrayList.add(new ReportCountItemModel(response.body().getAllReportCount(),"All"));
                                    reportModelArrayList.add(new ReportCountItemModel(response.body().getDirectReportCount(),"Direct Report"));
                                    reportModelArrayList.add(new ReportCountItemModel(response.body().getIndirectReportCount(),"Indirect Report"));
                                    reportModelArrayList.add(new ReportCountItemModel(response.body().getNillReportCount(),"Nil Report"));
                                    reportModelArrayList.add(new ReportCountItemModel(response.body().getAllElephantDeathReportCount(),"Elephant Death"));
                                    reportModelArrayList.add(new ReportCountItemModel(response.body().getAllFireAlertReportCount(),"Incident Alert"));

                                    if (allCount==null){
                                        allCount="0";
                                    }
                                    if (directCount==null){
                                        directCount="0";
                                    }
                                    if (indirectCount==null){
                                        indirectCount="0";
                                    }
                                    if (elephantDeathCount==null){
                                        elephantDeathCount="0";
                                    }
                                    if (incidentCount==null){
                                        incidentCount="0";
                                    }
                                    if (nilCount==null){
                                        nilCount="0";
                                    }
                                    if (vulnerableCount==null){
                                        vulnerableCount="0";
                                    }
                                    all_count_txt.setText(allCount);
                                    direct_count_txt.setText(directCount);
                                    indirect_count_txt.setText(indirectCount);
                                    nil_count_txt.setText(nilCount);

                                    elephant_death_count_txt.setText(elephantDeathCount);
                                    elephant_death_name.setText("Elephant Death");
                                    fire_count_txt.setText(incidentCount);
                                    fire_report_name.setText("Incident Alert");

                                    if (response.body().getAllVulnerabilityCount()==null){
                                        all_vulnerable_count_txt.setText("0");//for now testing with 1
                                    }else {
                                        all_vulnerable_count_txt.setText(vulnerableCount);
                                    }
                                    reportCountAdapter=new ReportCountAdapter(context,reportModelArrayList,DashboardFragment.this::oncount_click);
                                    report_recyclerV.setAdapter(reportCountAdapter);
                                    reportCountAdapter.notifyDataSetChanged();

                                }
                                else {

                                    if (response.code()==500){
                                        main_Constraint_ll.setVisibility(View.GONE);
                                        error_ll.setVisibility(View.VISIBLE);
                                        progress_bar_LL.setVisibility(View.GONE);
                                    }else {
                                        progress_bar_LL.setVisibility(View.GONE);
                                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }

                            @Override
                            public void onFailure(Call<AllReportCountModelInADay> call, Throwable t) {
                                progress_bar_LL.setVisibility(View.GONE);
                                Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                            }
                        });

            }else {
                progress_bar_LL.setVisibility(View.GONE);
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            progress_bar_LL.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    @Override
    public void oncount_click(int position,String count) {
        try {
            dateFrom=from_dateSelected;
            dateTo=to_dateSelected;
            if (position==0){//for All

                if (count.equalsIgnoreCase("0")){
                    Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();
                }else {

//                    from_dateSelected=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
//                    to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");

                    Intent intent=new Intent(context, IncidentReportViewActivity.class);
                    intent.putExtra("reportType","all");
                    startActivity(intent);
                }
            }
            else if (position==1){

                if (count.equalsIgnoreCase("0")){
                    Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();
                }else {
                    Intent intent=new Intent(context, IncidentReportViewActivity.class);
                    intent.putExtra("reportType","direct");
                    startActivity(intent);

                }
            }
            else if (position==2){
                if (count.equalsIgnoreCase("0")){
                    Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();
                }else {

                    Intent intent=new Intent(context, IncidentReportViewActivity.class);
                    intent.putExtra("reportType","indirect");
                    startActivity(intent);
                }
            }
            else if (position==3){

                if (count.equalsIgnoreCase("0")){
                    Snackbar.make(main_ll,"No data to view",Snackbar.LENGTH_SHORT).show();
                }else {

                    Intent intent=new Intent(context, IncidentReportViewActivity.class);
                    intent.putExtra("reportType","nil");
                    startActivity(intent);
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
            ft.replace(R.id.main_monitoring, fragmentt,tag);
//                ft.addToBackStack(""+itemId);
            ft.commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    public void createHiChartData(){
//        try {
//
//            HIOptions options = new HIOptions();
//
//            HIChart chart = new HIChart();
//            chart.setType("bar");
//            options.setChart(chart);
//
//            //Remove the exporting sign(hamburger) and highchart.com(credit)
//            HIExporting exporting = new HIExporting();
//            exporting.setEnabled(false);
//            HICredits credits = new HICredits();
//            credits.setEnabled(false);
//            options.setExporting(exporting);
//            options.setCredits(credits);
//
//            HITitle title = new HITitle();
//            title.setText("");
//            options.setTitle(title);
//
//            HISubtitle subtitle = new HISubtitle();
//            subtitle.setText("");
////        subtitle.setText("<a href=\"http://netmarketshare.com\">netmarketshare.com</a>.");
//            options.setSubtitle(subtitle);
//
//            HIXAxis xAxis = new HIXAxis();
//            xAxis.setType("category");
//            options.setXAxis(new ArrayList<HIXAxis>(){{add(xAxis);}});
//
//            HIYAxis yAxis = new HIYAxis();
//            yAxis.setMin(0);
//            yAxis.setMax(10);
//            yAxis.setTitle(new HITitle());
//            yAxis.getTitle().setText("No. of death");
//            options.setYAxis(new ArrayList<HIYAxis>(){{add(yAxis);}});
//
//            HILegend legend = new HILegend();
//            legend.setEnabled(false);
//            options.setLegend(legend);
//
//            HIPlotOptions plotOptions = new HIPlotOptions();
//            plotOptions.setSeries(new HISeries());
//            plotOptions.getSeries().setDataLabels(new ArrayList(Arrays.asList(new HIDataLabels())));
////        plotOptions.getSeries().getDataLabels().setEnabled(true);
////        plotOptions.getSeries().getDataLabels().setFormat("{point.y:.1f}%");
//            options.setPlotOptions(plotOptions);
//
//            HITooltip tooltip = new HITooltip();
//            tooltip.setHeaderFormat("<span style=\"font-size:11px\">{series.name}</span><br>");
//            tooltip.setPointFormat("<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>");
//            options.setTooltip(tooltip);
//
//            HIColumn series1 = new HIColumn();
//            series1.setName("Chart");
//            series1.setColorByPoint(true);
//
//            HashMap<String, Object> map1 = new HashMap<>();
//            map1.put("name", circleNm1);
//            map1.put("y", Double.parseDouble(circleCount1));
//            map1.put("drilldown", circleNm1);
//
//            HashMap<String, Object> map2 = new HashMap<>();
//            map2.put("name", circleNm2);
//            map2.put("y", Double.parseDouble(circleCount2));
//            map2.put("drilldown", circleNm2);
//
//            HashMap<String, Object> map3 = new HashMap<>();
//            map3.put("name", circleNm3);
//            map3.put("y", Double.parseDouble(circleCount3));
//            map3.put("drilldown", circleNm3);
//
//
//            HashMap[] series1_data = new HashMap[] { map1, map2, map3 };
//            series1.setData(new ArrayList<>(Arrays.asList(series1_data)));
//            options.setSeries(new ArrayList<>(Arrays.asList(series1)));
//
//            HIDrilldown drilldown = new HIDrilldown();
//
//            HIColumn series2 = new HIColumn();
//            series2.setName(circleNm1);
//            series2.setId(circleNm1);
//
//            if (incidentDataModels.size()<=2){
//                Object[] object1 = new Object[] { circlename1, 1};
//                Object[] object2 = new Object[] { circlename2, 2 };
//
//                series2.setData(new ArrayList<>(Arrays.asList(object1, object2)));
//            }
//            else if (incidentDataModels.size()<=3){
//                Object[] object1 = new Object[] { circlename1, Double.parseDouble(circlenewCount1)};
//                Object[] object2 = new Object[] { circlename2, Double.parseDouble(circlenewCount2) };
//                Object[] object3 = new Object[] { circlename3 , Double.parseDouble(circlenewCount3) };
//
//                series2.setData(new ArrayList<>(Arrays.asList(object1, object2, object3)));
//            }
//            else {
//                Object[] object1 = new Object[] { circlename1, Double.parseDouble(circlenewCount1)};
//                Object[] object2 = new Object[] { circlename2, Double.parseDouble(circlenewCount2)};
//                Object[] object3 = new Object[] { circlename3 , Double.parseDouble(circlenewCount3)};
//                Object[] object4 = new Object[] { circlename4 , Double.parseDouble(circlenewCount4)};
//
//                series2.setData(new ArrayList<>(Arrays.asList(object1, object2, object3, object4)));
//            }
//
//            HIColumn series3 = new HIColumn();
//            series3.setName(circleNm2);
//            series3.setId(circleNm2);
//
//            Object[] object7 = new Object[] { circlename1, 2 };
//            Object[] object8 = new Object[] { circlename2, 1 };
////            Object[] object20 = new Object[] { "v40.0", 5 };
//
//            series3.setData(new ArrayList<>(Arrays.asList(object7, object8)));
////            series3.setData(new ArrayList<>(Arrays.asList(object7, object8, object9, object10, object11, object12, object13, object14, object15, object16, object17, object18, object19, object20)));
//
//            HIColumn series4 = new HIColumn();
//            series4.setName(circleNm3);
//            series4.setId(circleNm3);
//
//            Object[] object21 = new Object[] { circlename1, 3 };
//            Object[] object22= new Object[] { circlename2, 1 };
//
//            series4.setData(new ArrayList<>(Arrays.asList(object21, object22)));
////            series4.setData(new ArrayList<>(Arrays.asList(object21, object22, object23, object24, object25, object26, object27, object28)));
//
//            HIColumn series5 = new HIColumn();
//            series5.setName("Safari");
//            series5.setId("Safari");
//
//            Object[] object29 = new Object[] { "v8.0", 2.56 };
//            Object[] object30 = new Object[] { "v7.1", 0.77 };
//            Object[] object31 = new Object[] { "v5.1", 0.42 };
//            Object[] object32 = new Object[] { "v5.0", 0.3 };
//            Object[] object33 = new Object[] { "v6.1", 0.29 };
//            Object[] object34 = new Object[] { "v7.0", 0.26 };
//            Object[] object35 = new Object[] { "v6.2", 0.17 };
//
//            series5.setData(new ArrayList<>(Arrays.asList(object29, object30, object31, object32, object33, object34, object35)));
//
//            HIColumn series6 = new HIColumn();
//            series6.setName("Opera");
//            series6.setId("Opera");
//
//            Object[] object36 = new Object[] { "v12.x", 0.34 };
//            Object[] object37 = new Object[] { "v28", 0.24 };
//            Object[] object38 = new Object[] { "v27", 0.17 };
//            Object[] object39 = new Object[] { "v29", 0.16 };
//
//            series6.setData(new ArrayList<>(Arrays.asList(object36, object37, object38, object39)));
//
//            HIColumn[] seriesList = new HIColumn[] {series2, series3, series4, series5, series6 };
//            drilldown.setSeries(new ArrayList<>(Arrays.asList(seriesList)));
//            options.setDrilldown(drilldown);
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }


    //For Pie Chart
    private void initalializePieChart(String totalCount){
        try {
            // array of images
            ArrayList<Drawable> array_image = new ArrayList<Drawable>();
            array_image.add(ContextCompat.getDrawable(context, R.drawable.phone_img));
            array_image.add(ContextCompat.getDrawable(context, R.drawable.web_img));
            array_image.add(ContextCompat.getDrawable(context, R.drawable.logo));
            array_image.add(ContextCompat.getDrawable(context, R.drawable.ele));
            array_image.add(ContextCompat.getDrawable(context, R.drawable.elephant_marker));

            // array of graph different colors
            ArrayList<Integer> colors = new ArrayList<Integer>();
            colors.add(ContextCompat.getColor(context, R.color.chart_purple));
            colors.add(ContextCompat.getColor(context, R.color.chart_orange));
            colors.add(ContextCompat.getColor(context, R.color.chart_lime));
            colors.add(ContextCompat.getColor(context, R.color.chart_green));
            colors.add(ContextCompat.getColor(context, R.color.chart_blue));
            colors.add(ContextCompat.getColor(context, R.color.chart_red));
            colors.add(ContextCompat.getColor(context, R.color.design_default_color_secondary_variant));
            colors.add(ContextCompat.getColor(context, R.color.textDark));
            colors.add(ContextCompat.getColor(context, R.color.light_grey));
            colors.add(ContextCompat.getColor(context, R.color.app_green));

            // Now adding array of data to the entery set
            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
            // count is the number of values you need to display into graph

            for (int i = 0; i<animal_array_name.size(); i++) {
                entries.add(new PieEntry(animal_array_value.get(i), animal_array_name.get(i)));//It is for value and labels
//                entries.add(new PieEntry(animal_array_value.get(i), array_percent.get(i), array_image.get(i)));//It is for value ,labels and image
//                entries.add(new PieEntry(animal_array_value.get(i), new CustomLabelWithIcon(array_percent.get(i), array_image.get(i))));
            }
            // initializing pie data set
            PieDataSet dataset = new PieDataSet(entries, "");
            // set the data
            PieData data = new PieData(dataset);        // initialize Piedata
            chart.setData(data);
            // colors according to the dataset
            dataset.setColors(colors);
            data.setValueTextSize(13f);
            data.setValueTextColor(Color.WHITE);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (""+value).replace(".00","").replace(".0","");
                }
            });
            // adding legends to the desigred positions
            Legend l = chart.getLegend();
            l.setTextSize(13f);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);//It will show in top ,bottom and center position
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);//It will show in vertical / horizontal
            l.setDrawInside(true);
            l.setTextColor(Color.WHITE);
            l.setEnabled(false);//it remove the label text below
            // calling method to set center text
            chart.setCenterText(generateCenterSpannableText(totalCount.replace(".0",""),""));
            // if no need to add description
            chart.getDescription().setEnabled(false);
            // animation and the center text color
            chart.animateY(2000);//It will take time to animate 1.e.2s
            chart.setEntryLabelColor(Color.WHITE);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // If we need to display center text with textStyle
    private SpannableString generateCenterSpannableText(String totalCount,String type) {
        SpannableString s=null;
        try {
            if (type.equalsIgnoreCase("Elephant Death")){
                s = new SpannableString("TOTAL ELEPHANT\nDEATH\n" +totalCount);
                s.setSpan(new RelativeSizeSpan(2f), 20, s.length(), 0);//It will Make size bigger after 20th letter
                s.setSpan(new StyleSpan(Typeface.BOLD), 20, s.length(), 0);//It will Bold the letter after 20th letter
            }else {
                s = new SpannableString("TOTAL INCIDENT\n" +totalCount);
                s.setSpan(new RelativeSizeSpan(2f), 14, s.length(), 0);//It will Make size bigger after 14th letter
                s.setSpan(new StyleSpan(Typeface.BOLD), 14, s.length(), 0);//It will Bold the letter after 14th letter
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return s;
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
                            final ArrayAdapter<AllCircleData> dataAdapter = new ArrayAdapter<AllCircleData>(context, android.R.layout.simple_spinner_dropdown_item, circleDataArrayList);
                            dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            circle_spinner.setAdapter(dataAdapter);

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


    private void getAllDivisionByCircleId(String CircleId){
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

                            final ArrayAdapter<DivisionDataByCircleId> dataAdapter = new ArrayAdapter<DivisionDataByCircleId>(context,
                                    android.R.layout.simple_spinner_dropdown_item, divisionDataByCircleIdArrayList);
                            dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                            division_spinner.setAdapter(dataAdapter);

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


            }else {
                progress_bar_LL_filter.setVisibility(View.GONE);
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            progress_bar_LL_filter.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    private void getAllRangeByDivisionId(String DivId) {
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

                        final ArrayAdapter<RangeDataByDivId> dataAdapter = new ArrayAdapter<RangeDataByDivId>(context,
                                android.R.layout.simple_spinner_dropdown_item, rangeDataByDivIdArrayList);
                        dataAdapter.setDropDownViewResource(R.layout.spinnerfront);
                        range_spinner.setAdapter(dataAdapter);

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

    private void callViewincidentReportCount(String circleId,String divId,String rangeId,String fromDate,String toDate) {
        try {

            if (circleId.equalsIgnoreCase("All")||circleId.equalsIgnoreCase("-1")){
                circleId="";
            }
            if (divId.equalsIgnoreCase("All")||divId.equalsIgnoreCase("-1")){
                divId="";
            }
            if (rangeId.equalsIgnoreCase("All")||rangeId.equalsIgnoreCase("-1")){
                rangeId="";
            }
            if (PermissionUtils.check_InternetConnection(context) == "true") {

            RetrofitInterface retrofitInterface= RetrofitClient.getIncidentClient().create(RetrofitInterface.class);

            retrofitInterface.getviewAllIncidentReportCount(circleId,divId,rangeId,fromDate,toDate).enqueue(new Callback<ArrayList<ViewIncidentReportCount>>() {
                @Override
                public void onResponse(Call<ArrayList<ViewIncidentReportCount>> call, Response<ArrayList<ViewIncidentReportCount>> response) {
                    if (response.isSuccessful()){
                        double total=0.0;

                        animal_array_name.clear();
                        animal_array_value.clear();
                        for (int i=0;i<response.body().size();i++){

                            animal_array_name.add(response.body().get(i).getName());
                            // array of graph drawing size according to design
                            animal_array_value.add(Float.parseFloat(response.body().get(i).getCountValue()));

                            total=total+Double.parseDouble(response.body().get(i).getCountValue());
                        }
                        if (response.body().isEmpty()){
                            chart.setVisibility(View.GONE);
//                            Toast.makeText(context, "No data to show in chart!", Toast.LENGTH_SHORT).show();
                            chart.clear();
                            no_data_available_incident_txt.setVisibility(View.GONE);
                        }else {
                            chart.setVisibility(View.GONE);
                            no_data_available_incident_txt.setVisibility(View.GONE);
                            //for pie chart
                            initalializePieChart(""+total);
                        }

                    }
                    else {
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ViewIncidentReportCount>> call, Throwable t) {
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

            }else {
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callViewElephantDeathReportCount(String circleId,String divId,String rangeId,String fromDate,String toDate) {
        try {
            if (circleId.equalsIgnoreCase("All")||circleId.equalsIgnoreCase("-1")){
                circleId="";
            }
            if (divId.equalsIgnoreCase("All")||divId.equalsIgnoreCase("-1")){
                divId="";
            }
            if (rangeId.equalsIgnoreCase("All")||rangeId.equalsIgnoreCase("-1")){
                rangeId="";
            }
            if (PermissionUtils.check_InternetConnection(context) == "true") {

                RetrofitInterface retrofitInterface= RetrofitClient.getIncidentClient().create(RetrofitInterface.class);

                retrofitInterface.getviewAllElephantDeathReportCount(circleId,divId,rangeId,fromDate,toDate).enqueue(new Callback<ArrayList<ViewIncidentReportCount>>() {
                    @Override
                    public void onResponse(Call<ArrayList<ViewIncidentReportCount>> call, Response<ArrayList<ViewIncidentReportCount>> response) {
                        if (response.isSuccessful()){
                            double total=0.0;

                            elephant_array_name.clear();
                            elephant_array_value.clear();
                            for (int i=0;i<response.body().size();i++){

                                elephant_array_name.add(response.body().get(i).getName());
                                // array of graph drawing size according to design
                                elephant_array_value.add(Float.parseFloat(response.body().get(i).getCountValue()));

                                total=total+Double.parseDouble(response.body().get(i).getCountValue());
                            }
                            if (response.body().isEmpty()){
                                elephant_piechart.setVisibility(View.GONE);
//                            Toast.makeText(context, "No data to show in chart!", Toast.LENGTH_SHORT).show();
                                elephant_piechart.clear();
                                no_data_available_elephant_death_txt.setVisibility(View.VISIBLE);
                            }else {
                                elephant_piechart.setVisibility(View.VISIBLE);
                                no_data_available_elephant_death_txt.setVisibility(View.GONE);
                                //for pie chart
                                initalializeElephantPieChart(""+total);
                            }

                        }
                        else {
                            Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<ViewIncidentReportCount>> call, Throwable t) {
                        Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });

            }else {
                Toast.makeText(context, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //For Elephant pie chart

    private void initalializeElephantPieChart(String totalCount){
        try {
            // array of images
            ArrayList<Drawable> array_image = new ArrayList<Drawable>();
            array_image.add(ContextCompat.getDrawable(context, R.drawable.phone_img));
            array_image.add(ContextCompat.getDrawable(context, R.drawable.web_img));
            array_image.add(ContextCompat.getDrawable(context, R.drawable.ele));
            array_image.add(ContextCompat.getDrawable(context, R.drawable.ele));
            array_image.add(ContextCompat.getDrawable(context, R.drawable.elephant_marker));

            // array of graph different colors
            ArrayList<Integer> colors = new ArrayList<Integer>();
            colors.add(ContextCompat.getColor(context, R.color.chart_purple));
            colors.add(ContextCompat.getColor(context, R.color.chart_orange));
            colors.add(ContextCompat.getColor(context, R.color.chart_lime));
            colors.add(ContextCompat.getColor(context, R.color.incident_color));
            colors.add(ContextCompat.getColor(context, R.color.elephant_death_color));
            colors.add(ContextCompat.getColor(context, R.color.chart_blue));
            colors.add(ContextCompat.getColor(context, R.color.chart_red));
            colors.add(ContextCompat.getColor(context, R.color.design_default_color_secondary_variant));
            colors.add(ContextCompat.getColor(context, R.color.textDark));
            colors.add(ContextCompat.getColor(context, R.color.light_grey));
            colors.add(ContextCompat.getColor(context, R.color.chart_green));

            // Now adding array of data to the entery set
            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
            // count is the number of values you need to display into graph

            for (int i = 0; i<elephant_array_name.size(); i++) {
                entries.add(new PieEntry(elephant_array_value.get(i), elephant_array_name.get(i)));//It is for value and labels
//                entries.add(new PieEntry(animal_array_value.get(i), array_percent.get(i), array_image.get(i)));//It is for value ,labels and image
//                entries.add(new PieEntry(animal_array_value.get(i), new CustomLabelWithIcon(array_percent.get(i), array_image.get(i))));
            }
            // initializing pie data set
            PieDataSet dataset = new PieDataSet(entries, "");
            // set the data
            PieData data = new PieData(dataset);        // initialize Piedata
            elephant_piechart.setData(data);
            // colors according to the dataset
            dataset.setColors(colors);
            data.setValueTextSize(13f);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (""+value).replace(".00","").replace(".0","");
                }
            });
            data.setValueTextColor(Color.WHITE);
            // adding legends to the desigred positions
            Legend l = elephant_piechart.getLegend();
            l.setTextSize(13f);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);//It will show in top ,bottom and center position
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);//It will show in vertical / horizontal
            l.setDrawInside(true);
            l.setTextColor(Color.WHITE);
            l.setEnabled(false);
            // calling method to set center text
            elephant_piechart.setCenterText(generateCenterSpannableText(totalCount.replace(".0",""),"Elephant Death"));
            // if no need to add description
            elephant_piechart.getDescription().setEnabled(false);
            elephant_piechart.setDrawSlicesUnderHole(false);
            // animation and the center text color
            elephant_piechart.animateY(2000);//It will take time to animate 1.e.2s
            elephant_piechart.setEntryLabelColor(Color.WHITE);


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
            title_dialog=filter_dialog.findViewById(R.id.title);
            circle_spinner=filter_dialog.findViewById(R.id.circle);
            division_spinner=filter_dialog.findViewById(R.id.division);
            range_spinner=filter_dialog.findViewById(R.id.range);
            section_spinner=filter_dialog.findViewById(R.id.section);
            beat_spinner=filter_dialog.findViewById(R.id.beat);
            direct_indirect_spinner=filter_dialog.findViewById(R.id.direct_indirect_spinner);
            report_type_spinnerTIL=filter_dialog.findViewById(R.id.report_type_spinnerTIL);
            fromDate=filter_dialog.findViewById(R.id.fromDate);
            toDate=filter_dialog.findViewById(R.id.toDate);
            submit_CV=filter_dialog.findViewById(R.id.submit_CV);
            circle_div_LL=filter_dialog.findViewById(R.id.circle_div_LL);
            beat_LL=filter_dialog.findViewById(R.id.beat_LL);
            divisionTxt=filter_dialog.findViewById(R.id.divisionTxt);
            progress_bar_LL_filter=filter_dialog.findViewById(R.id.progress_bar_LL);

            title_dialog.setText("Filter Dashboard");

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

            callAllCircleApi();

//            from_dateSelected=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
//            to_dateSelected=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");

            if (roleID.equalsIgnoreCase("2")){//DFO

                circle_div_LL.setVisibility(View.VISIBLE);

                circle_spinner.setVisibility(View.GONE);
                division_spinner.setVisibility(View.GONE);
                divisionTxt.setVisibility(View.VISIBLE);
                divisionTxt.setText(juridictionNm);

                getAllRangeByDivisionId(divId);
                section_spinner.setVisibility(View.GONE);
                beat_LL.setVisibility(View.GONE);
            }else {
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
                    if(i!= selecetedCircle && i!=0) {
                        selectedDiv = 0;
                        selectedRange = 0;
                        division_spinner.setSelection(selectedDiv);
                        range_spinner.setSelection(selectedRange);
                    }

                    selecetedCircle=i;

                    if (!circleValue.equalsIgnoreCase("Select Circle")){
                        division_spinner.setVisibility(View.VISIBLE);
                        getAllDivisionByCircleId(circleCode);
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
                        range_spinner.setSelection(selectedRange);
                        getAllRangeByDivisionId(divCode);
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

            submit_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filter_dialog.dismiss();
                    if (PermissionUtils.check_InternetConnection(context) == "true") {
                        try {
                            date_selected.setText("Date : "+
                                    PermissionUtils.convertDate(from_dateSelected,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy")+" to "+
                                    PermissionUtils.convertDate(to_dateSelected,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy"));

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (roleID.equalsIgnoreCase("2")){ //DFO

                            divCode=divId;
                            circleCode=circleId;

                            circleStr=circleCode;
                            divisionStr=divCode;
                            rangeStr=rangeCode;
                            dateFrom=from_dateSelected;
                            dateTo=to_dateSelected;

                            callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);
                            callViewincidentReportCount(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//It is to showing count on set data in pie chart
                            callViewElephantDeathReportCount(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//It is to showing elephant death count on set data in pie chart



                        }
                        else if (roleID.equalsIgnoreCase("4")){ //Ranger

                            circleCode=circleId;
                            divCode=divId;
                            rangeCode=rangeId;

                            circleStr=circleCode;
                            divisionStr=divCode;
                            rangeStr=rangeCode;
                            dateFrom=from_dateSelected;
                            dateTo=to_dateSelected;

                            callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);
                            callViewincidentReportCount(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//It is to showing count on set data in pie chart
                            callViewElephantDeathReportCount(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//It is to showing elephant death count on set data in pie chart


                        }
                        else {//for PCCF

                            circleStr=circleCode;
                            divisionStr=divCode;
                            rangeStr=rangeCode;
                            dateFrom=from_dateSelected;
                            dateTo=to_dateSelected;

                            callReportCountApi(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);
                            callViewincidentReportCount(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//It is to showing count on set data in pie chart
                            callViewElephantDeathReportCount(circleCode,divCode,rangeCode,from_dateSelected,to_dateSelected);//It is to showing elephant death count on set data in pie chart


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
                            to_dateSelected = year + "-" + m + "-" + d +str_to;

                            toDate_alreadySelected=""+d + "-" + m + "-" + year;

//                            if (CommonMethods.checkDates(from_dateSelected,to_dateSelected)==false){
//                                Toast.makeText(context, "Please select to date more than from date !", Toast.LENGTH_SHORT).show();
//                                textView.setText(fromDate_alreadySelected);
//                                to_dateSelected=from_dateSelected;
//                            }
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
    public void onResume() {
        super.onResume();
//        timer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
//        timer.cancel();
    }

}
