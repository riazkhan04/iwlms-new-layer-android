package com.orsac.android.pccfwildlife.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.GajaBandhuItemActivity;
import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.LoginActivityNewGajaBandhu;
import com.orsac.android.pccfwildlife.Activities.LoginActivity;
import com.orsac.android.pccfwildlife.Activities.SplashScreen;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Contract.SplashContract;


public class SplashPresenter implements SplashContract.presenter {

    Context context;
    private static final String TAG = SplashPresenter.class.getName();
    Activity activity;
    SplashContract.view splashview;
    SessionManager session;


    public SplashPresenter(Context context, SplashContract.view view,SessionManager session) {
        this.context = context;
        activity = (SplashScreen) this.context;
        this.splashview = view;
        this.session=session;
    }

    @Override
    public void loadSplash() {
        splashview.show_progress();

        // new IntentLauncher().start();  It slow down the splash sceen to load

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashview.hide_progress();

                if (session.getGajaBandhu_UserId().equalsIgnoreCase("") &&
                        session.getUserID().equalsIgnoreCase("")){

                    context.startActivity(new Intent(context, LoginActivityNewGajaBandhu.class));
//                context.startActivity(new Intent(context, LoginActivity.class));
//                            activity.overridePendingTransition(R.anim.left_to_right_anim, R.anim.left_to_right_anim);
                    activity.finish();
                }else if (!session.getGajaBandhu_UserId().equalsIgnoreCase("")){
                    context.startActivity(new Intent(context, GajaBandhuItemActivity.class));
                    activity.finish();
                }else {
                    context.startActivity(new Intent(context, LoginActivityNewGajaBandhu.class));
                    activity.finish();
                }

            }
        },2000);

    }

    private class IntentLauncher extends Thread {
        private IntentLauncher() {
        }

        public void run() {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Thread.sleep(SplashScreen.SLEEP_TIME * 1000);
                        splashview.hide_progress();
                        context.startActivity(new Intent(context, LoginActivity.class));
//                            activity.overridePendingTransition(R.anim.left_to_right_anim, R.anim.left_to_right_anim);
                        activity.finish();

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

        }
    }


}
