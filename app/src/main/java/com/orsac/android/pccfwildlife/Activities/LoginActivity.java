package com.orsac.android.pccfwildlife.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.Presenter.LoginPresenter;
import com.orsac.android.pccfwildlife.Contract.LoginContract;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements LoginContract.view {

    LoginContract.presenter loginPresenter;
    Button loginbtn;
    TextInputEditText user_name,password;
    String username="",passwd="",value="",BASE_URL="http://164.164.122.66/pccfmobile/Service1.svc/";
    ProgressBar progressBar;
    public static final String LOGIN_SHARED = "logindetails";
    public static final int CAMERA_REQUEST_CODE = 8675309;
    ExecuteLoginDetailsData checkUrl=null;
    RetrofitInterface apiInterface_signin;
    JSONObject jsonObject = new JSONObject();
    SessionManager sessionManager;
    ScrollView main_sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.left_to_right_anim,
                R.anim.left_to_right_anim);
        setContentView(R.layout.activity_login_nw);

        initData();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username=user_name.getText().toString().trim();
                passwd=password.getText().toString().trim();
                PermissionUtils.hideKeyboard(LoginActivity.this);//hide keyboard

//                Intent intent=new Intent(LoginActivity.this,BanaSathiSelectionLanguageActivity.class);
//                startActivity(intent);

                if (PermissionUtils.check_InternetConnection(LoginActivity.this)=="true") {

                    if (username.equalsIgnoreCase("") || passwd.equalsIgnoreCase("")) {

                        Snackbar.make(main_sv, "Please enter valid username and password !", Snackbar.LENGTH_SHORT).show();
                    } else {

//                    checkUrl = new ExecuteLoginDetailsData();
//                    loginPresenter.load_login(username,passwd,checkUrl);
                      loginPresenter.load_login(username, passwd, apiInterface_signin);
                    }

                }
                else {
//                    Toast.makeText(LoginActivity.this, "Please connect with internet to Login !", Toast.LENGTH_SHORT).show();
                    PermissionUtils.no_internet_Dialog(LoginActivity.this, "", null);
                }

            }
        });

    }

    public void initData(){
        user_name=findViewById(R.id.UserName);
        password=findViewById(R.id.password);
        loginbtn=findViewById(R.id.login);
        progressBar=findViewById(R.id.progress_bar);
        main_sv=findViewById(R.id.main_sv);

        loginPresenter=new LoginPresenter(LoginActivity.this,this);

        sessionManager=new SessionManager(LoginActivity.this);
        apiInterface_signin= RetrofitClient.getClient("signin").create(RetrofitInterface.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermissions()) {
                SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_SHARED, 0);
                if (!sharedPreferences.getString("user_name", "0").equals("0")) {

//                    Toast.makeText(LoginActivity.this, "Go to Dashboard !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, SelectionPage.class);
//                    Intent intent = new Intent(LoginActivity.this, Dashboard_nw.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                requestPerms();
            }
        }

    }

    //Permission methods
    private boolean hasPermissions() {
        int res = 0;
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
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void show_progress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hide_progress() {
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void success(String message) {

//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Snackbar.make(main_sv, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void failed(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Snackbar.make(main_sv, message, Snackbar.LENGTH_SHORT).show();

    }

    public class ExecuteLoginDetailsData extends AsyncTask<String, String, String> {
//        ProgressDialog asyncDialog = new ProgressDialog(Login.this);

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String json = null;
            String userNme = params[0];
            String Password = params[1];

            Log.d("checkingLogin", username);
            try {
                HttpResponse response;
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("uid", userNme);
                jsonObject.accumulate("pwd", Password);
                json = jsonObject.toString();
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(BASE_URL+"post_userauthentication");
                httpPost.setEntity(new StringEntity(json, "UTF-8"));
                httpPost.setHeader("Content-Type", "application/json");
                response = httpClient.execute(httpPost);
                String sresponse = response.getEntity().toString();
                value = EntityUtils.toString(response.getEntity());

                Log.d("login", value);
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());

            }

            return value;
        }

        @Override
        protected void onPreExecute() {
            show_progress();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            try {
                hide_progress();

                JSONArray arr = new JSONArray(aVoid);
                if (arr.length() > 0) {
                    JSONObject jObj = arr.getJSONObject(0);
                    String uname = jObj.getString("uname");
                    String designation = jObj.getString("udesignation");
                    String division = jObj.getString("division");
                    String ranger = jObj.getString("ranger");

                    Log.d("uname", uname);
                    if (uname.equalsIgnoreCase("no")) {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        hide_progress();
                    } else {
                        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_SHARED, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("user_name", jObj.getString("uname"));
                        editor.apply();

                        hide_progress();
//                        Toast.makeText(LoginActivity.this, "Go to Dashboard !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);

                        startActivity(intent);
                        finish();

                    }

                }

            } catch (Exception e) {

                hide_progress();
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }
    }

}
