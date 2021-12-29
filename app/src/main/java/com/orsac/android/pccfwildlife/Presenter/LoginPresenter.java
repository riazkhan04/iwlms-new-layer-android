package com.orsac.android.pccfwildlife.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.LoginActivityNewGajaBandhu;
import com.orsac.android.pccfwildlife.Activities.LoginActivity;
import com.orsac.android.pccfwildlife.Activities.SelectionPage;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Contract.LoginContract;
import com.orsac.android.pccfwildlife.Model.LoginModel.LoginData;

import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginContract.presenter{

    Context context;
    LoginContract.view loginview;
    Activity activity;

    public LoginPresenter(Context context,LoginContract.view view) {
        this.context = context;
        this.loginview=view;
//        activity=(LoginActivity)this.context;
        activity=(LoginActivityNewGajaBandhu)this.context;
    }

    @Override
    public void load_login(String username, String password, RetrofitInterface apiInterface) {

        //        executeLoginDetailsData.execute(username,password);
        try {

            loginview.show_progress();
            call_login(username,password,apiInterface);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void call_login(String usernm, String passwrd, RetrofitInterface apiInterface){

        JSONObject jsonObject=new JSONObject();
        try{

            jsonObject.put("login_id", usernm);
            jsonObject.put("password", passwrd);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObject.toString());

         apiInterface.getlogin_api(body).enqueue(new Callback<LoginData>() {
//         apiInterface.getlogin_api(body).enqueue(new Callback<ArrayList<LoginData>>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {

                if (response.isSuccessful()){
                    if (response.body().getUsername().equalsIgnoreCase("")){
                        loginview.failed("Invalid Credentials");
                        loginview.hide_progress();
                    }
                    else {

                        loginview.success("Login Successful");
                        loginview.hide_progress();

                        SessionManager sessionManager=new SessionManager(activity);
                        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.LOGIN_SHARED, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("user_name", response.body().getUsername());
                        editor.putString("token", response.body().getAccessToken());
                        for (int i=0;i<response.body().getRoles().size();i++){
                            editor.putString("roles", response.body().getRoles().get(i));
                            sessionManager.setRoles(""+response.body().getRoles().get(i));
                        }

                        editor.apply();

                        sessionManager.setUserName(response.body().getUsername());
                        sessionManager.setToken(response.body().getAccessToken());
                        sessionManager.setUserID(""+response.body().getId());
                        sessionManager.setEmail(""+response.body().getEmail());
                        sessionManager.setJuridiction(""+response.body().getJuridictionName());
                        sessionManager.setJuridictionID(""+response.body().getJuridictionId());
                        sessionManager.setRoleId(""+response.body().getRoleId());
                        sessionManager.setCircleId(""+response.body().getCircleId());
                        sessionManager.setDivisionId(""+response.body().getDivisionId());
                        sessionManager.setRangeId(""+response.body().getRangeId());
                        sessionManager.setSectionId(""+response.body().getSectionId());
                        sessionManager.setBeatId(""+response.body().getBeatId());


                        Log.i("login_response",response.body().toString());

                        Intent intent = new Intent(context, SelectionPage.class);
//                        Intent intent = new Intent(context, DashboardActivity.class);
                        intent.putExtra("guestReporting","login");
                        context.startActivity(intent);
                        activity.finish();
                    }
                }
                else if (response.code()==400){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        String message = jsonObject.getString("message");
                        if (message.equalsIgnoreCase("invalidUser")){
                            loginview.failed("Invalid username !");
                        }else if (message.equalsIgnoreCase("LoginFailed")){
                            loginview.failed("Invalid password !");
                        }else if (message.equalsIgnoreCase("BadCredentials")){
                            loginview.failed("User not active.please contact admin !");
                        }
                        else {
                            loginview.failed("Login Failed...Please try again !");
                        }
                        loginview.hide_progress();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {

                    loginview.failed("Internal Server Error !");//For 401 status unauthorized when wrong username ,password
                    loginview.hide_progress();
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {

                loginview.failed("Failed...Please try again !");
                loginview.hide_progress();
            }
          });

        }
        catch (Exception e){
            loginview.hide_progress();
        }
    }


}
