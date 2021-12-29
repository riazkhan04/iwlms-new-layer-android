package com.orsac.android.pccfwildlife.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.LoginActivityNewGajaBandhu;
import com.orsac.android.pccfwildlife.Fragments.DashboardFragment;
import com.orsac.android.pccfwildlife.Contract.DashboardContract;
import com.orsac.android.pccfwildlife.Fragments.MapSelectionFragment;
import com.orsac.android.pccfwildlife.Fragments.ViewReportFragment;
import com.orsac.android.pccfwildlife.Model.ChangePasswordModel.ChangePasswordRequestModel;
import com.orsac.android.pccfwildlife.Model.ChangePasswordModel.ChangeUserNameModel;
import com.orsac.android.pccfwildlife.Model.ProfileModel.SuccessResponseData;
import com.orsac.android.pccfwildlife.Presenter.DashboardPresenter;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.orsac.android.pccfwildlife.Activities.SplashScreen.CREATE_BEAT_TABLE;
import static com.orsac.android.pccfwildlife.Activities.SplashScreen.CREATE_DIV_TABLE;
import static com.orsac.android.pccfwildlife.Activities.SplashScreen.CREATE_RANGE_TABLE;
import static com.orsac.android.pccfwildlife.Activities.SplashScreen.CREATE_SECTION_TABLE;
import static com.orsac.android.pccfwildlife.Activities.SplashScreen.DIV_TABLE_NAME;
import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardMonitoringActivity extends AppCompatActivity implements DashboardContract.view{
    SessionManager session;
    TextView profileName, desig,version_name;
    public static final String LOGIN_SHARED = "logindetails";
    DrawerLayout drawer;
    ImageView menu,close_img;
    public  static BottomNavigationView bottom_navigation;
    public String token="",checkInternet_status="",value="",division_nm="",userId="",userNm="";
    SQLiteDatabase mDb;
    Cursor c=null;
    ImageView profile_img,back_img,download_report;
    Toolbar toolbar;
    PopupMenu popup;
    DashboardContract.presenter dashboard_presenter;
    LinearLayout main_ll;
    TextView toolbar_txt;
    boolean doubleBackToExitPressedOnce = false;
    AppCompatButton submit;
    TextInputEditText old_password,new_password,confirm_password,old_userid,new_userid,confirm_userid;
    String oldPwd="",newPwd="",confirmPwd="",oldUserId="",newUserId="",confirmUserId="";
    LinearLayout loading_ll;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.dashboard_monitoring);

            initData();

            click_function();

            replaceFragment(R.id.nav_home,new DashboardFragment(bottom_navigation),"dashboard_frag");


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void click_function() {

        try {

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popup = new PopupMenu(DashboardMonitoringActivity.this, profile_img);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.profile_menu_monitoring, popup.getMenu());
//                setForceShowIcon(popup);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.nav_profile:

                                Intent profile_intent=new Intent(DashboardMonitoringActivity.this,ProfileActivity.class);
                                startActivity(profile_intent);
                                break;
                            case R.id.changePwd:
//                                        Toast.makeText(Dashboard_nw.this, "Change password !", Toast.LENGTH_SHORT).show();
                                callChangePasswordDialog(DashboardMonitoringActivity.this);
                                break;
                            case R.id.changeUserId:
//                                        Toast.makeText(Dashboard_nw.this, "Change UserId !", Toast.LENGTH_SHORT).show();
                                callChangeUserIdDialog(DashboardMonitoringActivity.this);
                                break;


                            case R.id.nav_logout:
//                                call_logout(Dashboard_nw.this,"Do you want to logout the app ?","Logout");
                                dashboard_presenter.call_logout(DashboardMonitoringActivity.this,"Do you want to logout the app ?","Logout",session);
                                break;

                            default:
                                break;
                        }
                        return true;

                    }

                });
            }
        });

        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void initData() {

        session = new SessionManager(DashboardMonitoringActivity.this);
        drawer= findViewById(R.id.drawer_layout);
        menu=findViewById(R.id.menu);
        bottom_navigation=findViewById(R.id.bottom_navigation);
        version_name = findViewById(R.id.version_name);
        main_ll = findViewById(R.id.main_ll);
        toolbar = findViewById(R.id.toolbar_new_id);
        profile_img = toolbar.findViewById(R.id.profile_img);
        back_img = toolbar.findViewById(R.id.back_img);
        download_report = toolbar.findViewById(R.id.download_report);
        toolbar_txt = toolbar.findViewById(R.id.toolbar_txt);
        dashboard_presenter=new DashboardPresenter(DashboardMonitoringActivity.this,this,DashboardMonitoringActivity.this);

//        toolbar_txt.setText("Dashboard");
        token=session.getToken();
        division_nm=session.getDivision();
        userId=session.getUserID();
        userNm=session.getUserName();

//        Toast.makeText(this, "---"+division_nm+"==="+userId, Toast.LENGTH_SHORT).show();
        mDb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        //create div_master
        mDb.execSQL(CREATE_DIV_TABLE);
        mDb.execSQL(CREATE_RANGE_TABLE);
        mDb.execSQL(CREATE_SECTION_TABLE);
        mDb.execSQL(CREATE_BEAT_TABLE);

        try {
            c = mDb.rawQuery("SELECT * from " + DIV_TABLE_NAME + "", null);
            int count = c.getCount();
            if (count < 1) {

                //Call service to sync data
//            callUpdate(checkInternet_status,Dashboard_nw.this); //Run for first time only
                dashboard_presenter.load_Update(checkInternet_status,DashboardMonitoringActivity.this);//Run for first time only

            }
        }catch (Exception e){
            c.close();
            mDb.close();
        }

        c.close();
        mDb.close();

        bottom_navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
//                            case R.id.reporting_id://Reporting Fragment
//                                toolbar_txt.setText(R.string.app_name);
//                                replaceFragment(R.id.reporting_id,new ReportingFragment(),"reporting_frag");
//                                break;
                            case R.id.nav_home://Dashboard
                                toolbar_txt.setText("Dashboard");
                                download_report.setVisibility(View.GONE);
                                replaceFragment(R.id.nav_home,new DashboardFragment(bottom_navigation),"dashboard_frag");
                                break;
                            case R.id.view_report://View Report Fragment
                                toolbar_txt.setText("View Report");
                                replaceFragment(R.id.view_report,new ViewReportFragment(bottom_navigation,download_report),"viewReport_frag");
                                break;
                            case R.id.map_view://MapView Fragment
                                toolbar_txt.setText("Map View");
                                download_report.setVisibility(View.GONE);
                                replaceFragment(R.id.map_view,new MapSelectionFragment(),"mapView_frag");
//                                replaceFragment(R.id.map_view,new MapFragment(),"mapView_frag");
                                break;
                        }

                        return true;
                    }


                });
    }

    private void replaceFragment(int itemId, Fragment fragmentt, String tag) {

        //initializing the fragment object which is selected
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_monitoring, fragmentt,tag);
//                ft.addToBackStack(""+itemId);
        ft.commit();
    }

    @Override
    public void show_progress() {

    }

    @Override
    public void hide_progress() {

    }

    @Override
    public void success(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void failed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        Snackbar.make(main_ll, "Please click BACK again to exit", Snackbar.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_monitoring);

//        toolbar_txt.setText(R.string.app_name);
        toolbar_txt.setText("Dashboard");
        if (currentFragment.getTag().equals("dashboard_frag")) {

//            replaceFragment(R.id.reporting_id,new ReportingFragment(),"reporting_frag");
        }
        else if (currentFragment.getTag().equals("reporting_frag")) {

            replaceFragment(R.id.reporting_id,new DashboardFragment(bottom_navigation),"dashboard_frag");
            bottom_navigation.getMenu().getItem(0).setChecked(true);//for selecting 1st item when pressed back
            bottom_navigation.setItemBackground(getResources().getDrawable(R.drawable.bottom_item_selector));

        }
        else if (currentFragment.getTag().equals("viewReport_frag")) {

            download_report.setVisibility(View.GONE);
            replaceFragment(R.id.reporting_id,new DashboardFragment(bottom_navigation),"dashboard_frag");
            bottom_navigation.getMenu().getItem(0).setChecked(true);
            bottom_navigation.setItemBackground(getResources().getDrawable(R.drawable.bottom_item_selector));
        }
        else if (currentFragment.getTag().equals("viewReport_frag_direct")) {


            download_report.setVisibility(View.GONE);
            replaceFragment(R.id.reporting_id,new DashboardFragment(bottom_navigation),"dashboard_frag");
            bottom_navigation.getMenu().getItem(0).setChecked(true);
            bottom_navigation.setItemBackground(getResources().getDrawable(R.drawable.bottom_item_selector));
        }
        else if (currentFragment.getTag().equals("viewReport_frag_indirect")) {


            download_report.setVisibility(View.GONE);
            replaceFragment(R.id.reporting_id,new DashboardFragment(bottom_navigation),"dashboard_frag");
            bottom_navigation.getMenu().getItem(0).setChecked(true);
            bottom_navigation.setItemBackground(getResources().getDrawable(R.drawable.bottom_item_selector));
        }
        else if (currentFragment.getTag().equals("viewReport_frag_nil")) {


            download_report.setVisibility(View.GONE);
            replaceFragment(R.id.reporting_id,new DashboardFragment(bottom_navigation),"dashboard_frag");
            bottom_navigation.getMenu().getItem(0).setChecked(true);
            bottom_navigation.setItemBackground(getResources().getDrawable(R.drawable.bottom_item_selector));
        }
        else if (currentFragment.getTag().equals("mapView_frag")) {

            download_report.setVisibility(View.GONE);
            replaceFragment(R.id.reporting_id,new DashboardFragment(bottom_navigation),"dashboard_frag");
            bottom_navigation.getMenu().getItem(0).setChecked(true);
            bottom_navigation.setItemBackground(getResources().getDrawable(R.drawable.bottom_item_selector));
        }

        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    public void callChangePasswordDialog(Context context){
        try {
            Dialog changePwdDialog=new Dialog(context);
            changePwdDialog.setCancelable(false);
            changePwdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            changePwdDialog.setContentView(R.layout.change_password_dialog);

            close_img=changePwdDialog.findViewById(R.id.close_img);
            submit=changePwdDialog.findViewById(R.id.submit);
            old_password=changePwdDialog.findViewById(R.id.old_password);
            new_password=changePwdDialog.findViewById(R.id.new_password);
            confirm_password=changePwdDialog.findViewById(R.id.confirm_password);
            loading_ll=changePwdDialog.findViewById(R.id.loading_ll);


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changePwdDialog.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oldPwd=old_password.getText().toString().trim();
                    newPwd=new_password.getText().toString().trim();
                    confirmPwd=confirm_password.getText().toString().trim();

                    if (oldPwd.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter old password !", Toast.LENGTH_SHORT).show();
                    }
                    else if (newPwd.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter new password !", Toast.LENGTH_SHORT).show();
                    }
                    else if (confirmPwd.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter confirm password !", Toast.LENGTH_SHORT).show();
                    }
                    else if (newPwd.length()<=7 || confirmPwd.length()<=7){
                        Toast.makeText(context, "Password must be minimum 8 characters !", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if (newPwd.equalsIgnoreCase(confirmPwd)){

                            callChangePasswordApi(userNm,oldPwd,newPwd,DashboardMonitoringActivity.this,
                                    changePwdDialog,loading_ll);
                        }
                        else {
                            Toast.makeText(context, "Please check new and confirm password must be same!", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });



            changePwdDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callChangePasswordApi(String username,String oldPassword,String newPassword,
                                       Context context,Dialog dialog,LinearLayout loading_ll){
        try {

            ChangePasswordRequestModel changePasswordRequestModel=new ChangePasswordRequestModel();
            changePasswordRequestModel.setUsername(username);
            changePasswordRequestModel.setOldPassword(oldPassword);
            changePasswordRequestModel.setNewPassword(newPassword);
            loading_ll.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface= RetrofitClient.getClient("changePassword").create(RetrofitInterface.class);

            retrofitInterface.changePassword(changePasswordRequestModel).enqueue(new Callback<SuccessResponseData>() {
                @Override
                public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {
                    if (response.isSuccessful()){
                        if (response.body().getMessage().equalsIgnoreCase("success")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Successfully Password Changed ! ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            try {
                                session.clear();
                                if (mDb.isOpen()){
                                    mDb.close();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Intent i = new Intent(context, LoginActivityNewGajaBandhu.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            finish();
                        }
                        else if (response.body().getMessage().equalsIgnoreCase("passwordmismatch")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Old Password Mismatched ! ", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                        else {
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "You have some issue with your password..Please check ! ", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                    }
                    else {
                        loading_ll.setVisibility(View.GONE);
                        Toast.makeText(context, "Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                    loading_ll.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            loading_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void callChangeUserIdDialog(Context context){
        try {
            Dialog changeUserIdDialog=new Dialog(context);
            changeUserIdDialog.setCancelable(false);
            changeUserIdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            changeUserIdDialog.setContentView(R.layout.change_userid_layout);

            close_img=changeUserIdDialog.findViewById(R.id.close_img);
            submit=changeUserIdDialog.findViewById(R.id.submit);
            old_userid=changeUserIdDialog.findViewById(R.id.old_userid);
            new_userid=changeUserIdDialog.findViewById(R.id.new_userid);
            confirm_userid=changeUserIdDialog.findViewById(R.id.confirm_userid);
            loading_ll=changeUserIdDialog.findViewById(R.id.loading_ll);


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeUserIdDialog.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oldUserId=old_userid.getText().toString().trim();
                    newUserId=new_userid.getText().toString().trim();
                    confirmUserId=confirm_userid.getText().toString().trim();

                    if (oldUserId.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter old username !", Toast.LENGTH_SHORT).show();
                    }
                    else if (newUserId.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter new username !", Toast.LENGTH_SHORT).show();
                    }
                    else if (confirmUserId.equalsIgnoreCase("")){
                        Toast.makeText(context, "Please enter confirm username !", Toast.LENGTH_SHORT).show();
                    }
                    else if (newUserId.length()<=3 || confirmUserId.length()<=3){
                        Toast.makeText(context, "UserId must be minimum 4 characters !", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if (newUserId.equalsIgnoreCase(confirmUserId)){

                            callChangeUsernameApi(userNm,oldUserId,newUserId,DashboardMonitoringActivity.this,
                                    changeUserIdDialog,loading_ll);
//                            Toast.makeText(context, "Working on  Api !", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(context, "Please check new and confirm username must be same!", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });

            changeUserIdDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callChangeUsernameApi(String username,String oldUsername,String newUsername,
                                       Context context,Dialog dialog,LinearLayout loading_ll){
        try {

            ChangeUserNameModel changeUserNameModel=new ChangeUserNameModel();
            changeUserNameModel.setUsername(username);
            changeUserNameModel.setOldUsername(oldUsername);
            changeUserNameModel.setNewUsername(newUsername);
            loading_ll.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface= RetrofitClient.getClient("changeUsername").create(RetrofitInterface.class);

            retrofitInterface.changeUsername(changeUserNameModel).enqueue(new Callback<SuccessResponseData>() {
                @Override
                public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {
                    if (response.isSuccessful()){
                        if (response.body().getMessage().equalsIgnoreCase("success")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Successfully Username Changed ! ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            try {
                                session.clear();
                                if (mDb.isOpen()){
                                    mDb.close();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Intent i = new Intent(context, LoginActivityNewGajaBandhu.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            finish();
                        }
                        else if (response.body().getMessage().equalsIgnoreCase("notMatched")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Old Username Mismatched ! ", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                        else {
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "You have some issue with your username..Please check ! ", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                    }
                    else {
                        loading_ll.setVisibility(View.GONE);
                        Toast.makeText(context, "Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                    loading_ll.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            loading_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

}
