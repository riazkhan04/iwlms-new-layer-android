package com.orsac.android.pccfwildlife.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.stetho.Stetho;
import com.google.android.material.snackbar.Snackbar;
import com.orsac.android.pccfwildlife.GetVersionCode;
import com.orsac.android.pccfwildlife.Model.AppVersionModel;
import com.orsac.android.pccfwildlife.MyUtils.CommonMethods;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.MyUtils.PhoneCustomSignalStateListener;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Presenter.SplashPresenter;
import com.orsac.android.pccfwildlife.SQLiteDB.DBhelper;
import com.orsac.android.pccfwildlife.Contract.SplashContract;

import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;

import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.ALL_LAT_LNG_TABLE_NAME;
import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity implements SplashContract.view {

    ProgressDialog progress;
    ProgressBar progress_bar;
    SplashContract.presenter presenter;
    private static final String TAG = SplashScreen.class.getName();
    public static long SLEEP_TIME = 3;
    TextView odisha_txt, wildlife_txt;
    ImageView logo_img, imageView;

    SQLiteDatabase mDb;
    private DBhelper DbHelper;
    public String checkInternet_status = "",latestVersion="";
    LinearLayout text_layout;
    SessionManager session;

    //    public static final String DATABASE_NAME = "pccfwl.db";
    //Circle table creation

    //Division table creation
    public static final String DIV_TABLE_NAME = "Division_Other";
    public static final String DIV_SL_NO = "slno";
    public static final String DIVISION_ID = "Division_Id";
    public static final String DIVISION_NAME = "Division_Name";

    //Range table creation
    public static final String RANGE_TABLE_NAME = "WlRange_Other";
    public static final String RANGE_SL_NO = "slno";
    public static final String RANGE_ID = "Range_ID";
    public static final String RANGE_NAME = "Range_Name";
    public static final String RANGE_DIVISION_ID = "Division_ID";
    public static final String RANGE_DIVISION_NAME = "Division_Name";

    //Section table creation
    public static final String SECTION_TABLE_NAME = "WlSection";
    public static final String SECTION_SL_NO = "slno";
    public static final String SECTION_ID = "Section_ID";
    public static final String SECTION_NAME = "Section_Name";
    public static final String SECTION_DIVISION_ID = "Division_Id";
    public static final String SECTION_RANGE_ID = "Range_ID";
    public static final String SECTION_RANGE_NAME = "Range_Name";

    //Beat  table creation
    public static final String BEAT_TABLE_NAME = "WlBeat";
    public static final String BEAT_SL_NO = "slno";
    public static final String BEAT_ID = "WlBeat_ID";
    public static final String BEAT_NAME = "WlBeat_Name";
    public static final String BEAT_DIVISION_ID = "Division_Id";
    public static final String BEAT_RANGE_ID = "Range_ID";
    public static final String BEAT_SECTION_ID = "Section_ID";
    //    PhoneCustomSignalStateListener signalStateListener;
//    TelephonyManager mTelephonyManager;
    public static int signal_strength=0;
    Handler network_handler;

    //create div_master
    public static final String CREATE_DIV_TABLE = "CREATE TABLE IF NOT EXISTS " + DIV_TABLE_NAME + "(" +
            DIV_SL_NO + " INTEGER PRIMARY KEY, " +
            DIVISION_ID + " TEXT, " +
            DIVISION_NAME + " TEXT)";
    //create range_master
    public static final String CREATE_RANGE_TABLE = "CREATE TABLE IF NOT EXISTS " + RANGE_TABLE_NAME + "(" +
            RANGE_SL_NO + " INTEGER PRIMARY KEY, " +
            RANGE_ID + " TEXT, " +
            RANGE_NAME + " TEXT, " +
            RANGE_DIVISION_ID + " TEXT, " +
            RANGE_DIVISION_NAME + " TEXT)";
    //create section_master
    public static final String CREATE_SECTION_TABLE = "CREATE TABLE IF NOT EXISTS " + SECTION_TABLE_NAME + "(" +
            SECTION_SL_NO + " INTEGER PRIMARY KEY, " +
            SECTION_ID + " TEXT, " +
            SECTION_NAME + " TEXT, " +
            SECTION_DIVISION_ID + " TEXT, " +
            SECTION_RANGE_ID + " TEXT, " +
            SECTION_RANGE_NAME + " TEXT)";
    //create beat master
    public static final String CREATE_BEAT_TABLE = "CREATE TABLE IF NOT EXISTS " + BEAT_TABLE_NAME + "(" +
            BEAT_SL_NO + " INTEGER PRIMARY KEY, " +
            BEAT_ID + " TEXT, " +
            BEAT_NAME + " TEXT, " +
            BEAT_DIVISION_ID + " TEXT, " +
            BEAT_RANGE_ID + " TEXT, " +
            BEAT_SECTION_ID + " TEXT,Section_Name TEXT)";


    ArrayList<Integer> yourListOfImages = new ArrayList<>();
    String versionCode="0";
    int currentVersion;
    ConstraintLayout main_cl;
    CommonMethods commonMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            this.overridePendingTransition(R.anim.left_to_right_anim,
                    R.anim.left_to_right_anim);
            setContentView(R.layout.activity_splash_screen);

            //For unique no
//        try {
//            String m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//            Log.i("android_id",m_androidId);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
            databaseScreening();
            initData();

            yourListOfImages.add(0, R.drawable.splashscreen);
            yourListOfImages.add(1, R.drawable.splashscreenn);
            yourListOfImages.add(2, R.drawable.splashscreennn);

            Random random = new Random(System.currentTimeMillis());
            int position_img = random.nextInt(yourListOfImages.size());

            Glide.with(SplashScreen.this)
                    .load(yourListOfImages.get(position_img))
                    .into(imageView);

//        presenter.loadSplash();




        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * Following methods are used during the development purpose only.
     * Need to be commented out during production build
     */
    public void databaseScreening(){
        try {
            // Create an InitializerBuilder
            Stetho.InitializerBuilder initializerBuilder = Stetho
                    .newInitializerBuilder(this);
            // Enable Chrome DevTools
            initializerBuilder.enableWebKitInspector(Stetho
                    .defaultInspectorModulesProvider(SplashScreen.this));
            // Enable command line interface
            initializerBuilder.enableDumpapp(Stetho
                    .defaultDumperPluginsProvider(SplashScreen.this));
            // Use the InitializerBuilder to generate an Initializer
            Stetho.Initializer initializer = initializerBuilder.build();
            // Initialize Stetho with the Initializer
            Stetho.initialize(initializer);
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * Development tool ends here
         */
    }


    public void initData() {
        try {
            progress_bar = findViewById(R.id.progress_bar);

            odisha_txt = findViewById(R.id.odisha_txt);
            wildlife_txt = findViewById(R.id.wildlife_txt);
            logo_img = findViewById(R.id.logo_img);
            imageView = findViewById(R.id.imageView);
            text_layout = findViewById(R.id.text_layout);
            main_cl = findViewById(R.id.main_cl);
            commonMethods=new CommonMethods(SplashScreen.this);
            session=new SessionManager(SplashScreen.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void show_progress() {
        try {
            progress_bar.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void hide_progress() {
        try {
            progress_bar.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressLint("DefaultLocale")
    public class MyTask extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            presenter.loadSplash();
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
        @Override
        protected Void doInBackground(Void... params) {

//            gpdata();
            DbHelper = new DBhelper(getApplicationContext());
            DbHelper.open();
            DbHelper.close();
            return null;
        }

        protected void onPostExecute(Void result) {

        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    public void showForceUpdateDialog() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(SplashScreen.this, R.style.AlertDialogCustom));

            alertDialogBuilder.setTitle("Force Update");
            alertDialogBuilder.setMessage("A new update is available in playstore.Please update !");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    dialog.cancel();
                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                    finish();
                }
            });
            alertDialogBuilder.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (PermissionUtils.check_InternetConnection(SplashScreen.this) == "true") {
            //For checking of versionCode
//            checkSignalStrength(this);
            getVersionCodeApi(this);
//            GetVersionCode getVersionCode=new GetVersionCode(this);//get version name from playstore
//            getVersionCode.execute();
        }
        else {
            //when offline
            presenter = new SplashPresenter(SplashScreen.this, this,session);

            presenter.loadSplash();

            commonMethods.callSnackBar(main_cl,"Please connect internet connection for any new update!",SplashScreen.this);
        }

    }
    public void getVersionCodeApi(SplashContract.view view){
        PackageInfo info = null;
        try {
            PackageManager manager = SplashScreen.this.getPackageManager();
            info = manager.getPackageInfo(SplashScreen.this.getPackageName(), 0);
            currentVersion = info.versionCode;

        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            RetrofitInterface retrofitInterface = RetrofitClient.getClient("")
                    .create(RetrofitInterface.class);
            PackageInfo finalInfo = info;
            retrofitInterface.getAppVersion().enqueue(new Callback<AppVersionModel>() {
                @Override
                public void onResponse(Call<AppVersionModel> call, Response<AppVersionModel> response) {
                    if (response.isSuccessful()){
                        versionCode=response.body().getVersionCode();
                        try {
                            currentVersion = finalInfo.versionCode;
//                            currentVersion = 9;

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (currentVersion>=Integer.parseInt(versionCode)){
                            presenter = new SplashPresenter(SplashScreen.this, view,session);

                            presenter.loadSplash();
                        }else {
                            showForceUpdateDialog();
                        }

                    }else {
                        if (currentVersion>=Integer.parseInt(versionCode)){
                            presenter = new SplashPresenter(SplashScreen.this, view,session);

                            presenter.loadSplash();
                        }else {
                            showForceUpdateDialog();
                        }

//                        Toast.makeText(SplashScreen.this, "Please try again !", Toast.LENGTH_SHORT).show();
                        commonMethods.callSnackBar(main_cl,"Please try again !",SplashScreen.this);
                    }
                }

                @Override
                public void onFailure(Call<AppVersionModel> call, Throwable t) {
                    if (currentVersion>=Integer.parseInt(versionCode)){
                        presenter = new SplashPresenter(SplashScreen.this, view,session);

                        presenter.loadSplash();
                    }else {
                        showForceUpdateDialog();
                    }
//                    Toast.makeText(SplashScreen.this, "Failed...please try again !", Toast.LENGTH_SHORT).show();
                    commonMethods.callSnackBar(main_cl,"Failed...please try again !",SplashScreen.this);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkSignalStrength(SplashContract.view view){
        try {
//            signalStateListener=new PhoneCustomSignalStateListener();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            }
//            mTelephonyManager.listen(signalStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

            network_handler=new Handler();
            network_handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    if (signal_strength<3){
//                        Toast.makeText(SplashScreen.this, "Signal is very weak !", Toast.LENGTH_SHORT).show();
//                    }else if (signal_strength <10 && signal_strength > 3){
//                        Toast.makeText(SplashScreen.this, "Signal is weak !", Toast.LENGTH_SHORT).show();
//                    }else if (signal_strength <20 && signal_strength >=10){
//                        Toast.makeText(SplashScreen.this, "Signal is lil weak !", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(SplashScreen.this, "Signal is good !", Toast.LENGTH_SHORT).show();
//                    }
                    if (signal_strength<3){
                        //when offline
                        presenter = new SplashPresenter(SplashScreen.this, view,session);

                        presenter.loadSplash();

                        commonMethods.callSnackBar(main_cl,"Very weak signal to get any update !",SplashScreen.this);

                    }else {
                        getVersionCodeApi(view);
                    }
//                    Toast.makeText(SplashScreen.this, ""+signal_strength, Toast.LENGTH_SHORT).show();


                }
            },1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
