package com.orsac.android.pccfwildlife.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.orsac.android.pccfwildlife.Fragments.ReportingFragment;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllDivision_data;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.Services.UpdatingAreaActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.orsac.android.pccfwildlife.SQLiteDB.DBhelper.DATABASE_NAME;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager session;
    TextView profileName, desig,version_name;
    public static final String LOGIN_SHARED = "logindetails";
    DrawerLayout drawer;
    ImageView menu;
    NavigationView navigationView;
    View hView;
    public String token="",checkInternet_status="",value="";
    SQLiteDatabase mDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.left_to_right_anim,
                R.anim.left_to_right_anim);
        setContentView(R.layout.activity_dash_board);

        initData();


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
//        token=session.getToken();
        profileName.setText(session.getUserName());

        version_name.setText("Version- "+PermissionUtils.getVersion_Code_Name(DashboardActivity.this));

//        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();

        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(R.id.nav_home,new ReportingFragment("guest"));

    }

    public void initData() {

        session = new SessionManager(DashboardActivity.this);
        drawer= findViewById(R.id.drawer_layout);
        menu=findViewById(R.id.menu);
        navigationView=findViewById(R.id.nav_view);
        hView= navigationView.getHeaderView(0);
        profileName = hView.findViewById(R.id.profilename);
        version_name = findViewById(R.id.version_name);

        mDb = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
//          mDb.execSQL(CREATE_DIV_TABLE);
//           mDb.execSQL(CREATE_RANGE_TABLE);
//          mDb.execSQL(CREATE_SECTION_TABLE);
//          mDb.execSQL(CREATE_BEAT_TABLE);
        Cursor c = mDb.rawQuery("SELECT * from " + SplashScreen.DIV_TABLE_NAME + "", null);
        int count = c.getCount();
        if (count < 1) {

            //Call service to sync data
            callUpdate(checkInternet_status,DashboardActivity.this); //Run for first time only

        }
        c.close();
        mDb.close();


    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(R.id.nav_home,new ReportingFragment("guest"));

        }
        else if (id == R.id.nav_update_local_data) {

         callUpdate(checkInternet_status,DashboardActivity.this);

        }  else if (id == R.id.nav_outbox) {

//            Intent intent = new Intent(getApplicationContext(), outbox.class);
//            startActivity(intent);
        }
       else if (id == R.id.nav_logout) {

            call_logout(DashboardActivity.this,"Do you want to logout the app ?","Logout");
        }
        drawer.closeDrawer(GravityCompat.START);
            return true;
    }

    private void replaceFragment(int itemId,Fragment fragmentt) {

        //initializing the fragment object which is selected
        switch (itemId) {

            case R.id.nav_home:

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main, fragmentt);
                ft.commit();
                break;
        }
    }

    public void call_logout(Context context,String msg,String dialog_title){
        AlertDialog.Builder builder=  new AlertDialog.Builder(context);

        builder.setMessage(msg)
                .setTitle(dialog_title)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        session.logoutUser(DashboardActivity.this);
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    //Call All division Api

    public void getDivisionApi(){
        RetrofitInterface apiInterface= RetrofitClient.getClient("").create(RetrofitInterface.class);
        apiInterface.get_alldivisions().enqueue(new Callback<AllDivision_data>() {
            @Override
            public void onResponse(Call<AllDivision_data> call, Response<AllDivision_data> response) {

                if (response.isSuccessful()){


                        for (int i=0;i<response.body().getDivision_models_arr().size();i++){
                        Toast.makeText(DashboardActivity.this, ""+response.body().getDivision_models_arr().get(i).getDivision_name(), Toast.LENGTH_SHORT).show();

                        insertDivisionFromApi(response.body().getDivision_models_arr().get(i).getDiv_id(),
                                response.body().getDivision_models_arr().get(i).getDivision_name(),mDb);

                         }
                        for (int i=0;i<response.body().getRange_models_arr().size();i++){
                        Toast.makeText(DashboardActivity.this, ""+response.body().getRange_models_arr().get(i).getRange_name(), Toast.LENGTH_SHORT).show();

                        insertRangeFromApi(response.body().getRange_models_arr().get(i).getRange_id(),
                                response.body().getRange_models_arr().get(i).getRange_name(),
                                response.body().getRange_models_arr().get(i).getDivision_model_obj().getDiv_id(),
                                response.body().getRange_models_arr().get(i).getDivision_model_obj().getDivision_name(),mDb);
                          }
                            for (int i=0;i<response.body().getSection_models_arr().size();i++){
                        Toast.makeText(DashboardActivity.this, ""+response.body().getSection_models_arr().get(i).getSection_name(), Toast.LENGTH_SHORT).show();
//
                                insertSectionFromApi(response.body().getSection_models_arr().get(i).getSection_id(),
                                        response.body().getSection_models_arr().get(i).getSection_name(),
                                        response.body().getSection_models_arr().get(i).getRange_model_arr().getRange_id(),
                                        response.body().getSection_models_arr().get(i).getRange_model_arr().getRange_name(),mDb);
                            }
                           for (int i=0;i<response.body().getBeat_models_arr().size();i++){
                        Toast.makeText(DashboardActivity.this, ""+response.body().getBeat_models_arr().get(i).getBeat_name(), Toast.LENGTH_SHORT).show();

                              insertSectionFromApi(response.body().getBeat_models_arr().get(i).getBeat_id(),
                                response.body().getBeat_models_arr().get(i).getBeat_name(),
                                response.body().getBeat_models_arr().get(i).getSection_model_arr().getSection_id(),
                                response.body().getBeat_models_arr().get(i).getSection_model_arr().getSection_name(),mDb);
                           }

                }else {
                    Toast.makeText(DashboardActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<AllDivision_data> call, Throwable t) {

                Toast.makeText(DashboardActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void insertDivisionFromApi(int id, String divisionName, SQLiteDatabase mDb){
        mDb = DashboardActivity.this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        mDb.execSQL("INSERT INTO  Division_Other (Division_Id,Division_Name)VALUES('"+id+"','"+divisionName+"');");
            Toast.makeText(this, "Division data inserted", Toast.LENGTH_SHORT).show();

        mDb.close();
    }
    public void insertRangeFromApi(int id,String rangeName,int div_id,String division_name,SQLiteDatabase mDb){
        mDb = DashboardActivity.this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        mDb.execSQL("INSERT INTO  WlRange_Other (Range_ID,Range_Name,Division_ID,Division_Name)VALUES('"+id+"','"+rangeName+"','"+div_id+"','"+division_name+"');");
        Toast.makeText(this, "Range data inserted", Toast.LENGTH_SHORT).show();
        mDb.close();
    }
    public void insertSectionFromApi(int id,String sectionName,int range_id,String rangeName,SQLiteDatabase mDb){
        mDb = DashboardActivity.this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        mDb.execSQL("INSERT INTO  WlSection (Section_ID,Section_name,Range_ID,Range_Name)VALUES('"+id+"','"+sectionName+"','"+range_id+"','"+rangeName+"');");
        Toast.makeText(DashboardActivity.this, "Section data inserted", Toast.LENGTH_SHORT).show();
        mDb.close();
    }
    public void insertBeatFromApi(int id,String beatName,int section_id,String sectionName,SQLiteDatabase mDb){

        mDb=DashboardActivity.this.openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        mDb.execSQL("INSERT INTO  WlBeat (WlBeat_ID,WlBeat_Name,Section_ID,Section_Name)VALUES('"+id+"','"+beatName+"','"+section_id+"','"+sectionName+"');");
        Toast.makeText(DashboardActivity.this, "Beat data inserted", Toast.LENGTH_SHORT).show();
        mDb.close();
    }



    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(this,getPackageName() , Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public void callUpdate(String checkInternet_status, Context context){
        checkInternet_status=PermissionUtils.check_InternetConnection(context);
        if (checkInternet_status.equalsIgnoreCase("true")){
//                Toast.makeText(this, "Internet Connected !", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {

//                                Intent intent = new Intent(context, MyService.class);
//                                startService(intent);
                                new UpdatingAreaActivity(DashboardActivity.this);

                            }catch (IllegalStateException e){

                            }catch (Exception ee){

                            }
                        }
                    });
                }
            });

            thread.start();

        }
        else {
            Toast.makeText(this, "No internet connection !", Toast.LENGTH_SHORT).show();

        }
    }

}
