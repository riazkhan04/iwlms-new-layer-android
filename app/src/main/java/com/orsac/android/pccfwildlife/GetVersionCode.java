package com.orsac.android.pccfwildlife;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.orsac.android.pccfwildlife.Activities.SplashScreen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetVersionCode extends AsyncTask<Void, String, String> {

    Activity activity;
    float currentVersion=0.0f;

    public GetVersionCode(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String newVersion = null;

        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" +
                    activity.getPackageName()
                    + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newVersion;

    }


    @Override

    protected void onPostExecute(String onlineVersion) {

        super.onPostExecute(onlineVersion);

        PackageManager manager = activity.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        currentVersion = info.versionCode;

        if (onlineVersion != null && !onlineVersion.isEmpty()) {

//            if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
//                //show anything
//            }

        }
        Toast.makeText(activity, "Current version " + currentVersion + "playstore version " + onlineVersion, Toast.LENGTH_SHORT).show();

        Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);

    }
}