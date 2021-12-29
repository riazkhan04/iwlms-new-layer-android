package com.orsac.android.pccfwildlife.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.GajaBandhuItemActivity;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.LoginActivityNewGajaBandhu;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.RegisterGajaBandhuActivity;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GenerateOtpResponseModel;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewLoginOtpFragment extends Fragment {

    Context context;
    Button submit;
    LinearLayout otp_LL;
    TextInputEditText phone_edit;
    TextInputLayout phone_til;
    LinearLayout progress_bar_LL,resend_LL;
    TextView resend_txt,submit_txt,english_txt,odia_txt,progress_txt;
    OtpTextView otpTextView;
    Handler handler;
    Runnable runnable;
    int seconds,delayTime=1000;
    boolean stopHandler=false;
    String phoneNo="",languageSelect="",responseOtp="",otp_typed="";
    Dialog language_dialog;
    ImageView close_img,englishtxt_tick,oditxt_tick,back_img;
    SessionManager session;
    ConstraintLayout main_ll;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public NewLoginOtpFragment(LinearLayout progress_bar_LL,TextView progress_txt) {
        this.progress_bar_LL = progress_bar_LL;
        this.progress_txt = progress_txt;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.otp_based_login,container,false);

        initData(view);

        clickFunction();

        return view;
    }

    private void initData(View view) {
        try {

            submit=view.findViewById(R.id.submit);
            otp_LL=view.findViewById(R.id.otp_LL);
            phone_edit=view.findViewById(R.id.phone);
            phone_til=view.findViewById(R.id.phone_til);
            resend_LL=view.findViewById(R.id.resend_LL);
            resend_txt=view.findViewById(R.id.resend_txt);
            otpTextView=view.findViewById(R.id.otp_view);
            main_ll=view.findViewById(R.id.main_ll);
//            back_img=view.findViewById(R.id.back_img);

            otp_LL.setVisibility(View.GONE);
            resend_LL.setVisibility(View.GONE);
            session=new SessionManager(context);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clickFunction() {
        try {
            phone_til.setEnabled(true);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (submit.getText().toString().trim().equalsIgnoreCase("Submit")){

                        if (phone_edit.getText().length()<=9){

                            Toast.makeText(context, "Please check your mobile No.", Toast.LENGTH_SHORT).show();
                            phone_edit.requestFocus();
                        }else if (phone_edit.getText().length()==10){

                            PermissionUtils.hideKeyboard(getActivity());
                            callGenerateOTP(phone_edit.getText().toString().trim(),"already_login");//first time


                        }
                        else {
                            submit.setText("Submit");
                            otp_LL.setVisibility(View.INVISIBLE);
                            phone_til.setVisibility(View.VISIBLE);
                        }

                    }
                    else  if (submit.getText().toString().trim().equalsIgnoreCase("Submit OTP")){

                        if (responseOtp.equalsIgnoreCase(otp_typed)){
                            call_languageDialog(context);
                        }
                        else {
                            callSnackBarOnTop("Otp is not valid . Please check !");
                        }
//                        call_languageDialog(context);

                    }


                }
            });

            otpTextView.getOtpListener();
            otpTextView.requestFocusOTP();

            otpTextView.setOtpListener(new OTPListener() {
                @Override
                public void onInteractionListener() {

                }

                @Override
                public void onOTPComplete(String otp) {
//                    Toast.makeText(context, "The OTP is " + otp,  Toast.LENGTH_SHORT).show();

                    otp_typed=otp;
                    if (responseOtp.equalsIgnoreCase(otp)){
                        call_languageDialog(context);
                    }
                    else {
                        callSnackBarOnTop("Otp is not valid . Please check !");
                    }



                }
            });

            resend_LL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (resend_txt.getText().toString().trim().equalsIgnoreCase("Resend OTP")){

                        otpTextView.setOTP("");
//                        phone_til.setVisibility(View.VISIBLE);
//                        otp_LL.setVisibility(View.GONE);
//                        resend_LL.setVisibility(View.GONE);
//                        submit.setText("Submit");
//                        Toast.makeText(context, ""+phoneNo, Toast.LENGTH_SHORT).show();
                        callGenerateOTP(phoneNo,"");
                    }

                }
            });

//            back_img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    login_viewPager.setCurrentItem(0,true);
//                    password_login.setBackgroundColor(getResources().getColor(R.color.tranparent_green));
//                    otp_login.setBackgroundColor(getResources().getColor(R.color.white));
//                }
//            });




        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callTimer(int seconds){
        try {
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;
                    int secs = seconds % 60;

                    String time
                            = String
                            .format(Locale.getDefault(),
                                    "%02d:%02d",
                                    minutes, secs);

                    resend_txt.setText(time);
                }
            },1000);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  void setLocale(String lang) {
        try {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void call_languageDialog(Context context){
        try {
            language_dialog=new Dialog(context);
            language_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            language_dialog.setCancelable(false);
            language_dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
            language_dialog.setContentView(R.layout.language_dialog_layout);


            submit_txt=language_dialog.findViewById(R.id.submit_txt);
            english_txt=language_dialog.findViewById(R.id.english_txt);
            odia_txt=language_dialog.findViewById(R.id.odia_txt);
            close_img=language_dialog.findViewById(R.id.close_list_img);
            englishtxt_tick=language_dialog.findViewById(R.id.englishtxt_tick);
            oditxt_tick=language_dialog.findViewById(R.id.oditxt_tick);


            odia_txt.setText(getResources().getString(R.string.odia));
//            languageSelect="en";

            if (session.getLanguageSelect().equalsIgnoreCase("en")||
                    session.getLanguageSelect().equalsIgnoreCase("")){
                englishtxt_tick.setVisibility(View.VISIBLE);
                oditxt_tick.setVisibility(View.INVISIBLE);
            }else {
                englishtxt_tick.setVisibility(View.INVISIBLE);
                oditxt_tick.setVisibility(View.VISIBLE);
            }

            english_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    languageSelect="en";

                    if (englishtxt_tick.getVisibility()==View.VISIBLE){
                        englishtxt_tick.setVisibility(View.INVISIBLE);
                        oditxt_tick.setVisibility(View.VISIBLE);
                    }
                    else {
                        englishtxt_tick.setVisibility(View.VISIBLE);
                        oditxt_tick.setVisibility(View.INVISIBLE);
                    }
                }
            });
            odia_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    languageSelect="or_";
                    if (oditxt_tick.getVisibility()==View.VISIBLE){
                        oditxt_tick.setVisibility(View.INVISIBLE);
                        englishtxt_tick.setVisibility(View.VISIBLE);
                    }
                    else {
                        oditxt_tick.setVisibility(View.VISIBLE);
                        englishtxt_tick.setVisibility(View.INVISIBLE);
                    }
                }
            });

            submit_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (oditxt_tick.getVisibility()==View.VISIBLE){
                        languageSelect="or";
                    }else {
                        languageSelect="en";
                    }
                    if (languageSelect.equalsIgnoreCase("")){

                        Toast.makeText(context, "Please select language !", Toast.LENGTH_SHORT).show();
                    }else {
                        language_dialog.dismiss();
                        session.setLanguage(languageSelect);
                        setLocale(languageSelect);

                        Intent intent=new Intent(context, RegisterGajaBandhuActivity.class);
                        intent.putExtra("phoneNo",phoneNo);
                        startActivity(intent);
                        getActivity().finish();

                    }
                }
            });


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language_dialog.dismiss();
                }
            });


            Window window = language_dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            language_dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callGenerateOTP(String phoneNo,String type){
        try {
            RetrofitInterface retrofitInterface= RetrofitClient.getGajaBandhuRequestClient()
                    .create(RetrofitInterface.class);

            progress_bar_LL.setVisibility(View.VISIBLE);
            if (type.equalsIgnoreCase("already_login")){
                progress_txt.setText("Please wait...while validating!");
            }else {
                progress_txt.setText("Sending otp. Please wait...!");
            }

            retrofitInterface.call_generateOtp(phoneNo).enqueue(new Callback<GenerateOtpResponseModel>() {
                @Override
                public void onResponse(Call<GenerateOtpResponseModel> call, Response<GenerateOtpResponseModel> response) {
                    if (response.isSuccessful()){
                        progress_bar_LL.setVisibility(View.GONE);

//                        Log.i("otp",response.body().getMessage());
                        responseOtp=response.body().getMessage();

                        if (response.body().getMessage().equalsIgnoreCase("number is already registered !!!")){
                            //directly go to dashboard
                            session.setGajaBandhu_UserId(phoneNo);
                            Intent intent=new Intent(getActivity(), GajaBandhuItemActivity.class); // Dashboard
                            startActivity(intent);
                            getActivity().finish();

                        }else {
                            //Show otp view method
//                            Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            callForOtpViewAndTimerOn();
                        }


                    }else {
                        progress_bar_LL.setVisibility(View.GONE);
                        Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<GenerateOtpResponseModel> call, Throwable t) {
                    progress_bar_LL.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            progress_bar_LL.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void callForOtpViewAndTimerOn(){
        try {
            phoneNo=phone_edit.getText().toString().trim();
            otp_LL.setVisibility(View.VISIBLE);
            phone_til.setVisibility(View.VISIBLE);//Change invisible to visible
            phone_til.setEnabled(false);
            otpTextView.requestFocusOTP();
            resend_LL.setVisibility(View.VISIBLE);
            seconds=60;
            stopHandler=false;
            submit.setText("Submit OTP");

            handler=new Handler();
            handler.postDelayed(runnable=new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(runnable,delayTime);//It run after 1 sec delay
                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;
                    int secs = seconds % 60;

                    if (!stopHandler){
                        String time
                                = String
                                .format(Locale.getDefault(),
                                        "%02d:%02d",
                                        minutes, secs);
                        resend_LL.setVisibility(View.VISIBLE);
                        resend_txt.setText(time);
                        seconds--;
                    }

                    if (seconds==0){
                        resend_txt.setText("Resend OTP");
                        stopHandler=true;
                        handler.removeCallbacks(runnable);
                    }

                }
            },100);//run with this dealy when on click


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callSnackBarOnTop(String message){
        try {
            Snackbar snackbar=Snackbar.make(main_ll, message, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
//            params.gravity = Gravity.TOP;
            snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
            view.setLayoutParams(params);
            snackbar.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

