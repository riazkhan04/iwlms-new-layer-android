package com.orsac.android.pccfwildlife.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.orsac.android.pccfwildlife.Contract.DashboardContract;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.Services.UpdatingAreaActivity;

import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

public class DashboardPresenter implements DashboardContract.presenter {

    Context context;
    DashboardContract.view dashboardview;
    Activity activity;

    public DashboardPresenter(Context context, DashboardContract.view dashboardview, Activity activity) {
        this.context = context;
        this.dashboardview = dashboardview;
        this.activity = activity;
    }

    @Override
    public void load_Update(String checkInternet_status, Context context) {

        checkInternet_status= PermissionUtils.check_InternetConnection(context);
        if (checkInternet_status.equalsIgnoreCase("true")){
//                Toast.makeText(this, "Internet Connected !", Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show();
//            dashboardview.success("Updating...");
            try {
//                new UpdateAreaAsync().execute();//comment for now 1st june 2021

//                new UpdatingAreaActivity(activity);//directly call 05th june 2021



            }catch (Exception e){

            }

        }
        else {
//            Toast.makeText(activity, "No internet connection !", Toast.LENGTH_SHORT).show();
            dashboardview.failed("No internet connection !");

        }
    }

    @Override
    public void call_logout(Context context, String msg, String dialog_title, SessionManager session) {

        AlertDialog.Builder builder=  new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));

        builder.setMessage(msg)
                .setTitle(dialog_title)
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        setLocale("en");
                        session.setLanguage("en");
                        session.logoutUser(context);
                        activity.finish();

                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingLeftDialogAnimation;
        alertDialog.show();
    }

    public  void setLocale(String lang) {
        try {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public class UpdateAreaAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                new UpdatingAreaActivity(activity);

            } catch (IllegalStateException e) {

            } catch (Exception ee) {

            }

            return null;
        }
    }

}
