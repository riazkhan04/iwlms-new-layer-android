package com.orsac.android.pccfwildlife.MyUtils;

import static com.orsac.android.pccfwildlife.MyUtils.PermissionUtils.convertDate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.orsac.android.pccfwildlife.Activities.LoginActivity;
import com.orsac.android.pccfwildlife.Activities.ViewOwnReportActivity;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.ElephantDeathIncidentDataModel;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.IncidentReportDataModel;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

import net.ozaydin.serkan.easy_csv.EasyCsv;
import net.ozaydin.serkan.easy_csv.FileCallback;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommonMethods {
    Context context;
    public final int WRITE_PERMISSON_REQUEST_CODE = 1;
    Snackbar snackbar ;
    Handler loaderHandler;

    public CommonMethods(Context context) {
        this.context = context;
    }

    public void loadExcel(View view,Activity activity, String path,
                          ViewReportItemData_obj viewReportItemData_obj,
                          ElephantDeathIncidentDataModel deathIncidentDataModel,
                          IncidentReportDataModel incidentReportDataModel,
                          String type){
        try {

            EasyCsv easyCsv = new EasyCsv(activity);

            List<String> headerList = new ArrayList<>();
//            headerList.add("Name.Surname.Age-");
            if (type.equalsIgnoreCase("fire")){
                headerList.add("Date,Report Type,Division,Range,Section,Beat,Latitude,Longitude,ReportedBy,Details,ReportImage!".toUpperCase());
            }else if (type.equalsIgnoreCase("elephant_death")){
                headerList.add("Date,Report Type,Division,Range,Section,Beat,Latitude,Longitude,ReportedBy,Details,DeathReason,ReportImage!".toUpperCase());
            }
            else {
                headerList.add("Date,From_time,To_time,Report Type,Division,Range,Section,Beat,Total,Herd,Tusker,Makhna,Female,Calf,Latitude,Longitude,ReportedBy,ReportImage!".toUpperCase());
            }

            ArrayList<ViewReportItemData_obj> arrayList=new ArrayList<>();
            ArrayList<ElephantDeathIncidentDataModel> death_arrayList=new ArrayList<>();
            ArrayList<IncidentReportDataModel> incidentArrayList=new ArrayList<>();
            List<String> dataList = new ArrayList<>();
            if (viewReportItemData_obj.equals("")){
                death_arrayList.add(deathIncidentDataModel);
            }else if (incidentReportDataModel.equals("")){
                incidentArrayList.add(incidentReportDataModel);
            }
            else {
                arrayList.add(viewReportItemData_obj);
            }
            if (type.equalsIgnoreCase("fire")){
//                for (int i=0;i<arrayList.size();i++) {
                String details="";
                if (incidentReportDataModel.getIncidentReportType().equalsIgnoreCase("Crop Damage")){
                    String crop=incidentReportDataModel.getCrop();
                    if (crop.equalsIgnoreCase("1")){
                        details="Crop - "+crop +" acre";
                    }else {
                        details="Crop - "+crop+" acres";
                    }
                }else if (incidentReportDataModel.getIncidentReportType().equalsIgnoreCase("Human Injury/Kill")){
                    details="Human - "+incidentReportDataModel.getHuman();
                }else if (incidentReportDataModel.getIncidentReportType().equalsIgnoreCase("Cattle/Domestic Animal Kill")){
                    details="( Injured - "+incidentReportDataModel.getInjured()+"  "+
                             " Kill - "+incidentReportDataModel.getKill()+ " )";
                }else if (incidentReportDataModel.getIncidentReportType().equalsIgnoreCase("House Damage")){
                    details="House - "+incidentReportDataModel.getHouseDamage();
                }else if (incidentReportDataModel.getIncidentReportType().equalsIgnoreCase("Elephant on Road")||
                        incidentReportDataModel.getIncidentReportType().equalsIgnoreCase("Elephant Found In Pit/Dug Well")||
                        incidentReportDataModel.getIncidentReportType().equalsIgnoreCase("Elephant Injured/Sick")){
                    details="( Makhna - "+incidentReportDataModel.getMakhna()+"  "+
                            " Female - "+incidentReportDataModel.getFemale()+"  "+
                            " Calf - "+incidentReportDataModel.getCalf()+" )";
                }

                    dataList.add(incidentReportDataModel.getIncidentReportDate() + "~"+ incidentReportDataModel.getIncidentReportType() + "~"
                            + incidentReportDataModel.getDivision() + "~"
                            + incidentReportDataModel.getRange() + "~" + incidentReportDataModel.getSection() + "~" +
                            incidentReportDataModel.getBeat() + "~" + incidentReportDataModel.getLatitude() + "~" +
                            incidentReportDataModel.getLongitude() +"~"+ incidentReportDataModel.getUpdatedBy()
                            +"~"+
                           details
                            +"~"+
                            RetrofitClient.IMAGE_URL+"report/"+incidentReportDataModel.getImgPath()
                            + "!");
//                }
            }else if (type.equalsIgnoreCase("elephant_death")){
                String details="",deathReason="";
                details="( Makhna - "+deathIncidentDataModel.getMakhna()+ "  "+
                        " Female - "+deathIncidentDataModel.getFemale()+"  "+
                        " Calf - "+deathIncidentDataModel.getCalf()+" )";

                deathReason=deathIncidentDataModel.getDeathReason();
                if ((deathReason==null) || deathReason.equalsIgnoreCase("")){
                    deathReason="NA";
                }

                dataList.add(deathIncidentDataModel.getIncidentReportDate() + "~"+ "Elephant Death Report" + "~"
                        + deathIncidentDataModel.getDivision() + "~"
                        + deathIncidentDataModel.getRange() + "~" + deathIncidentDataModel.getSection() + "~" +
                        deathIncidentDataModel.getBeat() + "~" + deathIncidentDataModel.getLatitude() + "~" +
                        deathIncidentDataModel.getLongitude() +"~"+ deathIncidentDataModel.getUpdatedBy()
                        +"~"+
                        details+"~"+deathReason
                        +"~"+
                        RetrofitClient.IMAGE_URL+"report/"+deathIncidentDataModel.getIncidentPhoto()
                        + "!");
            }
            else {
                for (int i=0;i<arrayList.size();i++){
                    dataList.add(viewReportItemData_obj.getSighting_date()+"~"+viewReportItemData_obj.getSighting_time_from()+"~"+
                            viewReportItemData_obj.getSighting_time_to()+"~"+viewReportItemData_obj.getReport_type()+"~"
                            +viewReportItemData_obj.getDivision()+"~"
                            +viewReportItemData_obj.getRange()+"~"+viewReportItemData_obj.getSection()+"~"+
                            viewReportItemData_obj.getBeat()+"~"+viewReportItemData_obj.getTotal()+"~"+
                            viewReportItemData_obj.getHeard()+"~"+viewReportItemData_obj.getTusker()+"~"+
                            viewReportItemData_obj.getMukhna()+"~"+viewReportItemData_obj.getFemale()+"~"+
                            viewReportItemData_obj.getCalf()+"~"+viewReportItemData_obj.getLatitude()+"~"+
                            viewReportItemData_obj.getLongitudes()+"~"+ viewReportItemData_obj.getUpdatedBy()
                            +"~"+ RetrofitClient.IMAGE_URL+"report/"+viewReportItemData_obj.getImgAcqlocation()
                            +"!");
            }

            }

            easyCsv.setSeparatorColumn("~");
            easyCsv.setSeperatorLine("!");

            easyCsv.createCsvFile(path.replace("/","_"), headerList, dataList, WRITE_PERMISSON_REQUEST_CODE, new FileCallback() {
                @Override
                public void onSuccess(File file) {
                    try {
//                        String[] path=file.getPath().split("/wildlife");

//                        File fileNm=new File(path[0]);
//                        PermissionUtils.openFileFolder(file.getPath(),context);
//                        Toast.makeText(activity,"Your report is saved in excel !",Toast.LENGTH_SHORT).show();
                        String path=file.getPath();
                        callSnackbar(view,"Your report successfully saved here "+path,path);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String err) {
                    Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadAllExcel(View view,Activity activity, String path,
                             ArrayList<ViewReportItemData_obj> view_item_arr,
                             ArrayList<ElephantDeathIncidentDataModel> view_elephantDeath_item_arr,
                             ArrayList<IncidentReportDataModel> view_incident_item_arr,
                             String type,View progressView,
                             TextView progress_txt){
        try {

            EasyCsv easyCsv = new EasyCsv(activity);

            progressView.setVisibility(View.VISIBLE);
            progress_txt.setText("Downloading reports...please wait !");

            List<String> headerList = new ArrayList<>();
//            headerList.add("Name.Surname.Age-");
            if (type.equalsIgnoreCase("fire")){
                headerList.add("Date,Report Type,Division,Range,Section,Beat,Latitude,Longitude,ReportedBy,Details,ReportImage!".toUpperCase());
            }else  if (type.equalsIgnoreCase("elephantDeath")){
                headerList.add("Date,Report Type,Division,Range,Section,Beat,Latitude,Longitude,ReportedBy,Details,DeathReason,ReportImage!".toUpperCase());
            }
            else {
                headerList.add("Date,From_time,To_time,Report Type,Division,Range,Section,Beat,Total,Herd,Tusker,Makhna,Female,Calf,Latitude,Longitude,ReportedBy,ReportImage!".toUpperCase());
            }

//            ArrayList<ViewReportItemData_obj> arrayList=new ArrayList<>();
            List<String> dataList = new ArrayList<>();
//            arrayList.add(viewReportItemData_obj);
            if (type.equalsIgnoreCase("fire")){
                for (int i=0;i<view_incident_item_arr.size();i++) {

                    String details="",report_type;
                    report_type=view_incident_item_arr.get(i).getIncidentReportType();
                    if (report_type.equalsIgnoreCase("Crop Damage")){
                        String crop=view_incident_item_arr.get(i).getCrop();
                        if (crop.equalsIgnoreCase("1")){
                            details="Crop - "+crop +" acre";
                        }else {
                            details="Crop - "+crop+" acres";
                        }
                    }else if (report_type.equalsIgnoreCase("Human Injury/Kill")){
                        details="Human - "+view_incident_item_arr.get(i).getHuman();
                    }else if (report_type.equalsIgnoreCase("Cattle/Domestic Animal Kill")){
                        details="( Injured - "+view_incident_item_arr.get(i).getInjured()+"  "+
                                " Kill - "+view_incident_item_arr.get(i).getKill()+" )";
                    }else if (report_type.equalsIgnoreCase("House Damage")){
                        details="House - "+view_incident_item_arr.get(i).getHouseDamage();
                    }else if (report_type.equalsIgnoreCase("Elephant on Road")||
                            report_type.equalsIgnoreCase("Elephant Found In Pit/Dug Well")||
                            report_type.equalsIgnoreCase("Elephant Injured/Sick")){
                        details="( Makhna - "+view_incident_item_arr.get(i).getMakhna()+"  "+
                                " Female - "+view_incident_item_arr.get(i).getFemale()+"  "+
                                " Calf - "+view_incident_item_arr.get(i).getCalf()+" )";
                    }

                    dataList.add(convertDate(view_incident_item_arr.get(i).getIncidentReportDate(),
                            "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy") + "~"+ report_type + "~"
                            + view_incident_item_arr.get(i).getDivision() + "~"
                            + view_incident_item_arr.get(i).getRange() + "~" + view_incident_item_arr.get(i).getSection() + "~" +
                            view_incident_item_arr.get(i).getBeat() + "~" + view_incident_item_arr.get(i).getLatitude() + "~" +
                            view_incident_item_arr.get(i).getLongitude() +"~"+ view_incident_item_arr.get(i).getUpdatedBy()
                            +"~"+
                            details
                            +"~"+
                            RetrofitClient.IMAGE_URL+"report/"+view_incident_item_arr.get(i).getImgPath()
                            + "!");
                }
            }else if (type.equalsIgnoreCase("elephantDeath")){
                for (int i=0;i<view_elephantDeath_item_arr.size();i++) {

                    String details="",deathReason="";
                    details="( Makhna - "+view_elephantDeath_item_arr.get(i).getMakhna()+ "  "+
                            " Female - "+view_elephantDeath_item_arr.get(i).getFemale()+"  "+
                            " Calf - "+view_elephantDeath_item_arr.get(i).getCalf()+" )";

                    deathReason=view_elephantDeath_item_arr.get(i).getDeathReason();
                    if ((deathReason==null) || deathReason.equalsIgnoreCase("")){
                        deathReason="NA";
                    }

                    dataList.add(convertDate(view_elephantDeath_item_arr.get(i).getIncidentReportDate(),
                            "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy") + "~"+ "Elephant Death" + "~"
                            + view_elephantDeath_item_arr.get(i).getDivision() + "~"
                            + view_elephantDeath_item_arr.get(i).getRange() + "~" + view_elephantDeath_item_arr.get(i).getSection() + "~" +
                            view_elephantDeath_item_arr.get(i).getBeat() + "~" + view_elephantDeath_item_arr.get(i).getLatitude() + "~" +
                            view_elephantDeath_item_arr.get(i).getLongitude() +"~"+ view_elephantDeath_item_arr.get(i).getUpdatedBy()
                            +"~"+
                            details +"~"+ deathReason+"~"+
                            RetrofitClient.IMAGE_URL+"report/"+view_elephantDeath_item_arr.get(i).getIncidentPhoto()
                            + "!");
                }

            }
            else {
                for (int i=0;i<view_item_arr.size();i++){
                    String reportype="";
                    if (view_item_arr.get(i).getReport_type().equalsIgnoreCase("nill") ||
                            view_item_arr.get(i).getReport_type().equalsIgnoreCase("nil")){
                        reportype="Nil";
                    }else {
                        //For first letter capital
                        StringBuilder sb = new StringBuilder(view_item_arr.get(i).getReport_type());
                        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                        reportype=sb.toString();
                    }
                    dataList.add(convertDate(view_item_arr.get(i).getSighting_date(),
                            "yyyy-MM-dd HH:mm:ss.SSS","dd-MM-yyyy")+"~"+
                            convertDate(view_item_arr.get(i).getSighting_time_from(),
                                    "yyyy-MM-dd HH:mm:ss.SSS","HH:mm") +"~"+
                            convertDate(view_item_arr.get(i).getSighting_time_to(),
                                    "yyyy-MM-dd HH:mm:ss.SSS","HH:mm") +"~"+reportype+"~"
                            +view_item_arr.get(i).getDivision()+"~"
                            +view_item_arr.get(i).getRange()+"~"+view_item_arr.get(i).getSection()+"~"+
                            view_item_arr.get(i).getBeat()+"~"+view_item_arr.get(i).getTotal()+"~"+
                            view_item_arr.get(i).getHeard()+"~"+view_item_arr.get(i).getTusker()+"~"+
                            view_item_arr.get(i).getMukhna()+"~"+view_item_arr.get(i).getFemale()+"~"+
                            view_item_arr.get(i).getCalf()+"~"+view_item_arr.get(i).getLatitude()+"~"+
                            view_item_arr.get(i).getLongitudes()+"~"+ view_item_arr.get(i).getUpdatedBy()
                            +"~"+ RetrofitClient.IMAGE_URL+"report/"+view_item_arr.get(i).getImgAcqlocation()
                            +"!");
                }

            }

            easyCsv.setSeparatorColumn("~");
            easyCsv.setSeperatorLine("!");

            easyCsv.createCsvFile(path.replace("/","_"), headerList, dataList, WRITE_PERMISSON_REQUEST_CODE, new FileCallback() {
                @Override
                public void onSuccess(File file) {
                    try {
//                        String[] path=file.getPath().split("/wildlife");

//                        File fileNm=new File(path[0]);
//                        PermissionUtils.openFileFolder(file.getPath(),context);
//                        Toast.makeText(activity,"Your report is saved in excel !",Toast.LENGTH_SHORT).show();
                        String path=file.getPath();
                        loaderHandler=new Handler();
                        loaderHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                progressView.setVisibility(View.GONE);
                                callSnackbar(view,"Your report successfully saved here "+path,path);
                            }
                        },3000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFail(String err) {
                    Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean checkDates(String startDate, String endDate){
        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat dfDate  = new SimpleDateFormat("dd-MM-yyyy");
        boolean b = false;
        try {
            //If start date is after the end date
            if(dfDate.parse(startDate).before(dfDate.parse(endDate)))
            {
                b = true;//If start date is before end date
            }
            else b = dfDate.parse(startDate).equals(dfDate.parse(endDate));//If two dates are equal
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }
    public void callSnackbar(View view,String msg,String path){
        try {
            snackbar = Snackbar
                    .make(view, msg, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionUtils.openFileFolder(path,context);//open a file in folder
                            snackbar.dismiss();
                        }
                    });
            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
            textView.setMaxLines(5);
            snackbar.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getDirectionName(int degree, SensorManager sensorManager,
                                   Sensor sensorAccelerometer,Sensor sensorMagneticField,
                                   SensorEventListener listener){
        String compass_direction="";
        try {
//            getBearingDegree((int)degree);

            if((degree>=0 && degree<=22) || (degree>=-22 && degree<=-1)){
//                Toast.makeText(context, "North", Toast.LENGTH_SHORT).show();
                compass_direction="N";
            }
            else if(degree>=23 && degree<=67){
//                Toast.makeText(context, "North-East", Toast.LENGTH_SHORT).show();
                compass_direction="NE";
            }
            else if(degree>=68 && degree<=112){
//                Toast.makeText(context, "East", Toast.LENGTH_SHORT).show();
                compass_direction="E";
            }
            else if(degree>=113 && degree<=157){
//                Toast.makeText(context, "South-East", Toast.LENGTH_SHORT).show();
                compass_direction="SE";
            }
            else if((degree>=158 && degree<=180) || (degree>=-180 && degree<=-158)){
//                Toast.makeText(context, "South", Toast.LENGTH_SHORT).show();
                compass_direction="S";
            }
            else if((degree>=-157 && degree<=-113)){
//                Toast.makeText(context, "South-West", Toast.LENGTH_SHORT).show();
                compass_direction="SW";
            }
            else if((degree>=-112 && degree<=-68)){
//                Toast.makeText(context, "West", Toast.LENGTH_SHORT).show();
                compass_direction="W";
            }
            else if((degree>=-67 && degree<=-23)){
//                Toast.makeText(context, "North-West", Toast.LENGTH_SHORT).show();
                compass_direction="NW";
            }
            sensorManager.unregisterListener(listener,
                    sensorAccelerometer);
            sensorManager.unregisterListener(listener,
                    sensorMagneticField);

        }catch (Exception e){
            e.printStackTrace();
        }
        return compass_direction;
    }
    public int getBearingDegree(int degree){
        int bearingValue=0;
        try {
            if (degree<0){
                bearingValue=360+degree;

            }else if (degree>=0 && degree<=180){
                bearingValue=degree;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return bearingValue;
    }
    public void callSnackBar(View view,String msg,Activity activity){
        try {
            snackbar = Snackbar
                    .make(view, msg, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView textView = snackbarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(activity.getResources().getColor(R.color.white));
//            textView.setMaxLines(5);
            snackbar.setBackgroundTint(activity.getResources().getColor(R.color.light_black));
            snackbar.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public CountDownTimer logoutSessionExpired(int inMinute){
        CountDownTimer timer = null;
        try {
            timer = new CountDownTimer(inMinute *60 * 1000, 800) {

                public void onTick(long millisUntilFinished) {
                    //Some code
                    Toast.makeText(context, millisUntilFinished/1000+" sec left for logout !", Toast.LENGTH_SHORT).show();
                }

                public void onFinish() {
                    //Logout
                    SessionManager sessionManager=new SessionManager(context);
                    sessionManager.logoutUser(context);
                    sessionManager.clear();

//                    Intent intent=new Intent(context, LoginActivityNewGajaBandhu.class);
//                    context.startActivity(intent);
                }
            };

        }catch (Exception e){
            e.printStackTrace();
        }
        return timer;
    }
}
