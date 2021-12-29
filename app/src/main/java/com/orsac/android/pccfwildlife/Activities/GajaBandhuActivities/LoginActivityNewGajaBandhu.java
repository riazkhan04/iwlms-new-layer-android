package com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Activities.SelectionPage;
import com.orsac.android.pccfwildlife.Adapters.LoginSelectionAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class LoginActivityNewGajaBandhu extends AppCompatActivity {

    TabLayout login_tablayout;
    ViewPager login_viewPager;
    LoginSelectionAdapter loginSelectionAdapter;
    TextView password_login,otp_login;
    LinearLayout progress_bar_LL;
    SessionManager sessionManager;
    public static final String LOGIN_SHARED = "logindetails";
    public static final int CAMERA_REQUEST_CODE = 8;
    TextView progress_txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_new_login_layout_for_bana_sathi);

            initData();

            clickFunction();

            callAdapter();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initData() {
        try {
            login_tablayout=findViewById(R.id.login_tab_layout);
            login_viewPager=findViewById(R.id.login_pager);
            password_login=findViewById(R.id.password_login);
            otp_login=findViewById(R.id.otp_login);
            progress_bar_LL=findViewById(R.id.progress_bar_LL);
            progress_txt=findViewById(R.id.progress_txt);

            sessionManager=new SessionManager(LoginActivityNewGajaBandhu.this);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (hasPermissions()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_SHARED, 0);
                    if (!sharedPreferences.getString("user_name", "0").equals("0")) {

//                    Toast.makeText(LoginActivity.this, "Go to Dashboard !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivityNewGajaBandhu.this, SelectionPage.class);
//                    Intent intent = new Intent(LoginActivity.this, Dashboard_nw.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    requestPerms();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void clickFunction() {
        try {

            login_tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    if (tab.getPosition() == 0) {
                        password_login.setBackgroundColor(getResources().getColor(R.color.tranparent_green));
                        otp_login.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    else {
                        otp_login.setBackgroundColor(getResources().getColor(R.color.tranparent_green));
                        password_login.setBackgroundColor(getResources().getColor(R.color.white));
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            password_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login_viewPager.setCurrentItem(0,true);
                    password_login.setBackgroundColor(getResources().getColor(R.color.tranparent_green));
                    otp_login.setBackgroundColor(getResources().getColor(R.color.white));

                }
            });

            otp_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login_viewPager.setCurrentItem(1,true);
                    otp_login.setBackgroundColor(getResources().getColor(R.color.tranparent_green));
                    password_login.setBackgroundColor(getResources().getColor(R.color.white));


                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void callAdapter(){
        try {

            loginSelectionAdapter = new LoginSelectionAdapter(getSupportFragmentManager(),progress_bar_LL,progress_txt);
            login_viewPager.setAdapter(loginSelectionAdapter);
            login_tablayout.setupWithViewPager(login_viewPager);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Permission methods
    private boolean hasPermissions() {
        int res = 0;
        try {
            //string array of permissions,
            String[] permissions = new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION};

            for (String perms : permissions) {
                res = checkCallingOrSelfPermission(perms);
                if (!(res == PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private void requestPerms() {
        try {

        String[] permissions = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, CAMERA_REQUEST_CODE);
        }

        }catch (Exception e){
            e.printStackTrace();
        }
    }




}
