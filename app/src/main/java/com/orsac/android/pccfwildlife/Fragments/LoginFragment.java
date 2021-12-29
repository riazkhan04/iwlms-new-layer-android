package com.orsac.android.pccfwildlife.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.orsac.android.pccfwildlife.Activities.Dashboard_nw;
import com.orsac.android.pccfwildlife.Activities.SelectionPage;
import com.orsac.android.pccfwildlife.Model.ChangePasswordModel.OtpVerifiedModel;
import com.orsac.android.pccfwildlife.Presenter.LoginPresenter;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Contract.LoginContract;
import com.orsac.android.pccfwildlife.Model.ProfileModel.SuccessResponseData;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements LoginContract.view{

    Context context;
    TextView password_login,otp_login,progress_txt;
    LoginContract.presenter loginPresenter;
    Button loginbtn;
    TextInputEditText user_name,password;
    String username="",passwd="",value="",new_pwd="",confirm_pwd="",mobileNo="",new_usernm="",confirm_usernm="";
    public static final String LOGIN_SHARED = "logindetails";
    public static final int CAMERA_REQUEST_CODE = 8;
    RetrofitInterface apiInterface_signin;
    SessionManager sessionManager;
    LinearLayout progress_bar_LL;
    TextView guest_report_txt,forgot_password,title_txt;
    LinearLayout mobile_ll,password_ll,otp_LL,loading_ll,offline_ll,forgot_pwd_ll,username_ll,forgot_username_ll;
    Button submit_mobile_btn,submit_forgot_btn,submit_username_btn;
    ImageView close_img;
    TextInputEditText new_password,confirm_password,phone,new_username,confirm_username;
    OtpTextView otp_view;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public LoginFragment(LinearLayout progress_bar_LL,TextView progress_txt) {
        this.progress_bar_LL=progress_bar_LL;
        this.progress_txt=progress_txt;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.password_login_based_layout,container,false);

        user_name=view.findViewById(R.id.UserName);
        password=view.findViewById(R.id.password);
        loginbtn=view.findViewById(R.id.login);
        guest_report_txt=view.findViewById(R.id.guest_report_txt);
        forgot_password=view.findViewById(R.id.forgot_password);
        offline_ll=view.findViewById(R.id.offline_ll);
        forgot_pwd_ll=view.findViewById(R.id.forgot_pwd_ll);
        forgot_username_ll=view.findViewById(R.id.forgot_username_ll);
        loginPresenter=new LoginPresenter(context,this);
        sessionManager=new SessionManager(context);

        apiInterface_signin= RetrofitClient.getClient("signin").create(RetrofitInterface.class);

        initData(view);


        return view;
    }


    private void initData(View view) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {

                    if (hasPermissions()) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_SHARED, 0);
                        if (!sharedPreferences.getString("user_name", "0").equals("0")) {

                            Intent intent = new Intent(context, SelectionPage.class);
//                    Intent intent = new Intent(LoginActivity.this, Dashboard_nw.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    } else {
                        requestPerms();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }


            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                try {
                    username=user_name.getText().toString().trim();
                    passwd=password.getText().toString().trim();

                    PermissionUtils.hideKeyboard(getActivity());

                    if (PermissionUtils.check_InternetConnection(context)=="true") {

                        if (username.equalsIgnoreCase("") || passwd.equalsIgnoreCase("")) {

                            Toast.makeText(context, "Please enter valid username and password !", Toast.LENGTH_SHORT).show();
                        } else {

                            loginPresenter.load_login(username, passwd, apiInterface_signin);
                        }

                    }
                    else {
//                    Toast.makeText(context, "Please connect with internet to Login !", Toast.LENGTH_SHORT).show();
                        PermissionUtils.no_internet_Dialog(context, "", null);
                    }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

            offline_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "Guest Reporting !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Dashboard_nw.class);
//                    Intent intent = new Intent(LoginActivity.this, Dashboard_nw.class);
                    intent.putExtra("guestReporting","guest");
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            forgot_pwd_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    callForgotPasswordDialog(getActivity(),"Forgot Password","password");//call dialog for forgot password
                }
            });

            forgot_username_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    callForgotPasswordDialog(getActivity(),"Forgot Username","username");//call dialog for forgot username
                }
            });



        }catch (Exception e){
            e.printStackTrace();
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
            res = context.checkCallingOrSelfPermission(perms);
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
//        progressBar.setVisibility(View.VISIBLE);
        progress_bar_LL.setVisibility(View.VISIBLE);
    }

    @Override
    public void hide_progress() {
//        progressBar.setVisibility(View.INVISIBLE);
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

    public void callForgotPasswordDialog(Context context,String title,String type){
        try {
            Dialog dialog=new Dialog(context);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.forgot_password);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            submit_forgot_btn=dialog.findViewById(R.id.submit_forgot_btn);
            submit_username_btn=dialog.findViewById(R.id.submit_username_btn);
            submit_mobile_btn=dialog.findViewById(R.id.submit_mobile_btn);
            close_img=dialog.findViewById(R.id.close_img);
            mobile_ll=dialog.findViewById(R.id.mobile_ll);
            password_ll=dialog.findViewById(R.id.password_ll);
            username_ll=dialog.findViewById(R.id.username_ll);
            otp_LL=dialog.findViewById(R.id.otp_LL);
            loading_ll=dialog.findViewById(R.id.loading_ll);
            new_password=dialog.findViewById(R.id.new_password);
            new_username=dialog.findViewById(R.id.new_username);
            confirm_username=dialog.findViewById(R.id.confirm_username);
            confirm_password=dialog.findViewById(R.id.confirm_password);
            phone=dialog.findViewById(R.id.phone);
            otp_view=dialog.findViewById(R.id.otp_view);
            title_txt=dialog.findViewById(R.id.title);

            title_txt.setText(title);

            otp_LL.setVisibility(View.GONE);
            phone.setEnabled(true);

            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            submit_mobile_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (phone.getText().toString().equalsIgnoreCase("") || phone.getText().length()<=9){
                        Toast.makeText(context, "Please enter valid mobile no. !", Toast.LENGTH_SHORT).show();
                        otp_LL.setVisibility(View.GONE);
                    }else {
                        mobileNo=phone.getText().toString();
//                        Toast.makeText(context, "Please enter otp provided i.e. 12345!", Toast.LENGTH_LONG).show();
                        if (type.equalsIgnoreCase("password")){
                            callgenerateOTP(mobileNo,type);
                        }else {
                            callgenerateOTP(mobileNo,type);
                        }

                    }

                }
            });

            otp_view.setOtpListener(new OTPListener() {
                @Override
                public void onInteractionListener() {

                }

                @Override
                public void onOTPComplete(String otp) {

                    callVerifyOtpApi(mobileNo,otp,type);//call verifyapi to verify otp

                }
            });

            submit_forgot_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new_pwd=new_password.getText().toString();
                    confirm_pwd=confirm_password.getText().toString();
                    if (new_pwd.length()<=7 || confirm_pwd.length()<=7){

                        Toast.makeText(context, "Password must be minimum 8 character !", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        if (new_pwd.equalsIgnoreCase(confirm_pwd)){
                            dialog.dismiss();
                            callForgotPasswordApi(mobileNo,new_pwd,"password");//call forgotpasswordApi
//                        Toast.makeText(context, "Password changed successfully.Please login !", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "New password must be same with confirm password !", Toast.LENGTH_SHORT).show();

                        }
                    }

                }
            });

            //For username
            submit_username_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new_usernm=new_username.getText().toString();
                    confirm_usernm=confirm_username.getText().toString();
                    if (new_usernm.length()<=3 || confirm_usernm.length()<=3){

                        Toast.makeText(context, "Username must be minimum 4 character !", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        if (new_usernm.equalsIgnoreCase(confirm_usernm)){
                            dialog.dismiss();
                            callForgotPasswordApi(mobileNo,new_usernm,"username");//call forgot username

                        }else {
                            Toast.makeText(context, "New Username must be same with confirm username !", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void callgenerateOTP(String mobileNo,String type){
        try {
            loading_ll.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface= RetrofitClient.getClient("generateOtp").create(RetrofitInterface.class);

            retrofitInterface.generateOtp(mobileNo).enqueue(new Callback<SuccessResponseData>() {
                @Override
                public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {
                    if (response.isSuccessful()){
                        if (response.body().getMessage().equalsIgnoreCase("success")){
                            loading_ll.setVisibility(View.GONE);
                            otp_LL.setVisibility(View.VISIBLE);//Otp view is visible
                            otp_view.requestFocusOTP();
                            phone.setEnabled(false);
                            // and otp is sent when success
                        }
                        else if (response.body().getMessage().equalsIgnoreCase("notValid")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "Mobile No is not valid !", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context, "You have some issue with your mobile no. !", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        loading_ll.setVisibility(View.GONE);
                        if (response.code()==500){
                            Toast.makeText(context,"Internal server error. Please try again !", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,"Please try again...", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                    loading_ll.setVisibility(View.GONE);
                    Toast.makeText(context,"Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            loading_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void callVerifyOtpApi(String mobileNo,String otp,String password_username_type){
        try {
            OtpVerifiedModel otpVerifiedModel=new OtpVerifiedModel();
            otpVerifiedModel.setPhoneNo(mobileNo);
            otpVerifiedModel.setOtp(otp);

            loading_ll.setVisibility(View.VISIBLE);
            RetrofitInterface retrofitInterface= RetrofitClient.getClient("generateOtp").create(RetrofitInterface.class);

            retrofitInterface.otpVerified(otpVerifiedModel).enqueue(new Callback<SuccessResponseData>() {
                @Override
                public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {
                    if (response.isSuccessful()){
                        if (response.body().getMessage().equalsIgnoreCase("verified")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context,"Otp Verified !", Toast.LENGTH_SHORT).show();
                            if (password_username_type.equalsIgnoreCase("password")){
                                password_ll.setVisibility(View.VISIBLE);
                                mobile_ll.setVisibility(View.GONE);
                            }else {
                                //for username
                                username_ll.setVisibility(View.VISIBLE);
                                mobile_ll.setVisibility(View.GONE);
                            }

                        }
                        else if (response.body().getMessage().equalsIgnoreCase("invalidOtp")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context,"Invalid Otp !", Toast.LENGTH_SHORT).show();
                            if (password_username_type.equalsIgnoreCase("password")){
                                password_ll.setVisibility(View.GONE);
                                mobile_ll.setVisibility(View.VISIBLE);
                            }else {
                                //for username
                                username_ll.setVisibility(View.GONE);
                                mobile_ll.setVisibility(View.VISIBLE);
                            }

                        }
                        else if (response.body().getMessage().equalsIgnoreCase("invalidMobile")){
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context,"Invalid Mobile No !", Toast.LENGTH_SHORT).show();
                            if (password_username_type.equalsIgnoreCase("password")){
                                password_ll.setVisibility(View.GONE);
                                mobile_ll.setVisibility(View.VISIBLE);
                            }else {
                                //for username
                                username_ll.setVisibility(View.GONE);
                                mobile_ll.setVisibility(View.VISIBLE);
                            }

                        }
                        else {
                            loading_ll.setVisibility(View.GONE);
                            if (password_username_type.equalsIgnoreCase("password")){
                                password_ll.setVisibility(View.GONE);
                                mobile_ll.setVisibility(View.VISIBLE);
                            }else {
                                //for username
                                username_ll.setVisibility(View.GONE);
                                mobile_ll.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(context,"You have some issue with otp .please try again later !", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        loading_ll.setVisibility(View.GONE);
                        if (password_username_type.equalsIgnoreCase("password")){
                            password_ll.setVisibility(View.GONE);
                            mobile_ll.setVisibility(View.VISIBLE);
                        }else {
                            //for username
                            username_ll.setVisibility(View.GONE);
                            mobile_ll.setVisibility(View.VISIBLE);
                        }

                        Toast.makeText(context,"Please try again...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                    if (password_username_type.equalsIgnoreCase("password")){
                        password_ll.setVisibility(View.GONE);
                        mobile_ll.setVisibility(View.VISIBLE);
                    }else {
                        //for username
                        username_ll.setVisibility(View.GONE);
                        mobile_ll.setVisibility(View.VISIBLE);
                    }
                    loading_ll.setVisibility(View.GONE);

                    Toast.makeText(context,"Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            loading_ll.setVisibility(View.GONE);
            if (password_username_type.equalsIgnoreCase("password")){
                password_ll.setVisibility(View.GONE);
                mobile_ll.setVisibility(View.VISIBLE);
            }else {
                //for username
                username_ll.setVisibility(View.GONE);
                mobile_ll.setVisibility(View.VISIBLE);
            }

            e.printStackTrace();
        }
    }

    public void callForgotPasswordApi(String mobileNo,String newPwd,String password_username_type){
        try {
            loading_ll.setVisibility(View.VISIBLE);
            if (password_username_type.equalsIgnoreCase("password")){

                RetrofitInterface retrofitInterface= RetrofitClient.getClient("resetPassword").create(RetrofitInterface.class);

                retrofitInterface.resetPasswordWithMobile(mobileNo,newPwd).enqueue(new Callback<SuccessResponseData>() {
                    @Override
                    public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {
                        if (response.isSuccessful()){
                            if (response.body().getMessage().equalsIgnoreCase("success")){
                                loading_ll.setVisibility(View.GONE);
                                Toast.makeText(context, "Password changed successfully.Please login !", Toast.LENGTH_SHORT).show();

                            }else {
                                loading_ll.setVisibility(View.GONE);
                                Toast.makeText(context,"You have some issue.please try again later !", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context,"Please try again...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                        loading_ll.setVisibility(View.GONE);
                        Toast.makeText(context,"Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                //for username
                RetrofitInterface retrofitInterface= RetrofitClient.getClient("resetUsername").create(RetrofitInterface.class);

                retrofitInterface.resetUsernameWithMobile(mobileNo,newPwd).enqueue(new Callback<SuccessResponseData>() {
                    @Override
                    public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {
                        if (response.isSuccessful()){
                            if (response.body().getMessage().equalsIgnoreCase("success")){
                                loading_ll.setVisibility(View.GONE);
                                Toast.makeText(context, "Username changed successfully.Please login !", Toast.LENGTH_SHORT).show();

                            }else if (response.body().getMessage().equalsIgnoreCase("invalidMobile")){
                                loading_ll.setVisibility(View.GONE);
                                Toast.makeText(context,"Mobile No is invalid .Please check !", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                loading_ll.setVisibility(View.GONE);
                                Toast.makeText(context,"You have some issue.please try again later !", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            loading_ll.setVisibility(View.GONE);
                            Toast.makeText(context,"Please try again...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                        loading_ll.setVisibility(View.GONE);
                        Toast.makeText(context,"Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }catch (Exception e){
            loading_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


}
