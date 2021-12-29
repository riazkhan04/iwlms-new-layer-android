package com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.Adapters.GajaBandhuAdapter.ViewReportGajaBandhuAdapter;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuReportDataModel;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GajaBandhuViewReport extends AppCompatActivity {

    TextView toolbar_txt,no_data;
    Toolbar toolbar;
    ImageView toolbar_profile_img,toolbar_back_img;
    ViewReportGajaBandhuAdapter viewReportGajaBandhuAdapter;
    RecyclerView report_recyclerV;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<GajaBandhuReportDataModel> viewReportArr;
    String userID="";
    ProgressBar progress_bar;
    SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gaja_bandhu_view_report);


        initData();

        clickFunction();

//        userID="7978328829";
        userID=session.getGajaBandhu_UserId();

        callViewReportApi(userID);


    }


    private void initData() {
        try {

            toolbar = findViewById(R.id.toolbar_new_id);
            report_recyclerV = findViewById(R.id.report_recyclerV);
            no_data = findViewById(R.id.no_data);
            progress_bar = findViewById(R.id.progress_bar);
            toolbar_profile_img = toolbar.findViewById(R.id.profile_img);
            toolbar_back_img = toolbar.findViewById(R.id.back_img);
            toolbar_txt = toolbar.findViewById(R.id.toolbar_txt);
            viewReportArr=new ArrayList<>();

            session=new SessionManager(GajaBandhuViewReport.this);

            toolbar_profile_img.setVisibility(View.GONE);
            toolbar_back_img.setVisibility(View.VISIBLE);
            toolbar_txt.setText("View Report");


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clickFunction() {
        try {

            toolbar_back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callViewReportApi(String userId){
        try {
            RetrofitInterface retrofitInterface= RetrofitClient.getGajaBandhuRequestClient().create(RetrofitInterface.class);
            report_recyclerV.setVisibility(View.GONE);
            progress_bar.setVisibility(View.VISIBLE);
            retrofitInterface.getAllReportOfGajaBandhuByUserId(userId).enqueue(new Callback<ArrayList<GajaBandhuReportDataModel>>() {
                @Override
                public void onResponse(Call<ArrayList<GajaBandhuReportDataModel>> call, Response<ArrayList<GajaBandhuReportDataModel>> response) {
                    if (response.isSuccessful()){
                        viewReportArr.clear();
                        progress_bar.setVisibility(View.GONE);
                        report_recyclerV.setVisibility(View.VISIBLE);
                        for (int i=0;i<response.body().size();i++){
                            viewReportArr.add(response.body().get(i));
                            no_data.setVisibility(View.GONE);
                        }
                        if (response.body().size()==0){
                            report_recyclerV.setVisibility(View.GONE);
                            no_data.setVisibility(View.VISIBLE);
                        }
                        callViewReportGajaBandhuAdapter();

                    }else {
                        progress_bar.setVisibility(View.GONE);
                        report_recyclerV.setVisibility(View.GONE);
                        if (response.code()==500){
                            Toast.makeText(GajaBandhuViewReport.this, "Internal server error !", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(GajaBandhuViewReport.this, "please try again!", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<GajaBandhuReportDataModel>> call, Throwable t) {
                    progress_bar.setVisibility(View.GONE);
                    Toast.makeText(GajaBandhuViewReport.this, "Failed...please try again!", Toast.LENGTH_SHORT).show();

                }
            });

        }catch (Exception e){
            progress_bar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void callViewReportGajaBandhuAdapter(){
        try {
            report_recyclerV.setHasFixedSize(true);
            report_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview

//            layoutManager = new LinearLayoutManager(GajaBandhuViewReport.this);//no reverse
            layoutManager = new LinearLayoutManager(GajaBandhuViewReport.this,
                    LinearLayoutManager.VERTICAL, false);//true for reverse layout & false for not reverse
            report_recyclerV.setLayoutManager(layoutManager);

            viewReportGajaBandhuAdapter=new ViewReportGajaBandhuAdapter(viewReportArr,GajaBandhuViewReport.this);
            report_recyclerV.setAdapter(viewReportGajaBandhuAdapter);
            viewReportGajaBandhuAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
