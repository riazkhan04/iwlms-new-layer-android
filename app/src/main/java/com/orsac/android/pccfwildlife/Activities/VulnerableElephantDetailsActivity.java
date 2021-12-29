package com.orsac.android.pccfwildlife.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orsac.android.pccfwildlife.Fragments.DashboardFragment;
import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableCircleResponse;
import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableDivResponse;
import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableRangeResponse;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.Adapters.VulnerableAdapter.VulnerableDivAdapter;
import com.orsac.android.pccfwildlife.Adapters.VulnerableAdapter.VulnerableRangeAdapter;
import com.orsac.android.pccfwildlife.Adapters.VulnerableItemAdapter;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VulnerableElephantDetailsActivity extends AppCompatActivity implements
        VulnerableItemAdapter.CallItemClickListener,VulnerableDivAdapter.CallItemClickListener{

    Toolbar toolbar;
    ImageView back;
    TextView toolbar_title;
    RecyclerView vulnerable_recyclerV;
    public RecyclerView.LayoutManager layoutManager;
    ArrayList<VulnerableCircleResponse> vulnerableDataModels;
    VulnerableItemAdapter vulnerableItemAdapter;
    VulnerableDivAdapter vulnerableDivAdapter;
    VulnerableRangeAdapter vulnerableRangeAdapter;
    String circleId="All",divId="All",rangeId="All",startDate="",endDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vulnerable_elephant_details);
        try {

            initData();

            clickFunction();

//            callViewincidentReportCount(circleId,rangeId,rangeId,startDate,endDate);

            if(DashboardFragment.dateFrom.equalsIgnoreCase("")||
                    DashboardFragment.dateTo.equalsIgnoreCase("")){
                startDate=PermissionUtils.getCurrentDateMinusOne("yyyy-MM-dd HH:mm:ss");
                endDate=PermissionUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");
            }else {
                startDate=DashboardFragment.dateFrom;
                endDate=DashboardFragment.dateTo;
            }
            callViewincidentReportCount(DashboardFragment.circleStr,DashboardFragment.divisionStr,DashboardFragment.rangeStr,
                    startDate,endDate);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            toolbar=findViewById(R.id.toolbar_id);
            back=toolbar.findViewById(R.id.back);
            toolbar_title=toolbar.findViewById(R.id.toolbar_title);
            vulnerable_recyclerV=findViewById(R.id.vulnerable_recyclerV);

            vulnerableDataModels=new ArrayList<>();

            toolbar_title.setText("Vulnerable Elephant Details");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clickFunction() {
        try {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onBackPressed();
                }
            });




        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void callVulnerableAdapter(){
        try {
//            vulnerableDataModels.clear();

            vulnerable_recyclerV.setHasFixedSize(true);
            vulnerable_recyclerV.setNestedScrollingEnabled(false);//It stop lagging in recyclerview
            layoutManager =  new LinearLayoutManager(VulnerableElephantDetailsActivity.this,
                    LinearLayoutManager.VERTICAL, false);//for reverse list make it true
            vulnerable_recyclerV.setLayoutManager(layoutManager);

            vulnerableItemAdapter=new VulnerableItemAdapter(VulnerableElephantDetailsActivity.this,
                                vulnerableDataModels, this);
            vulnerable_recyclerV.setAdapter(vulnerableItemAdapter);
            vulnerableItemAdapter.notifyDataSetChanged();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callViewincidentReportCount(String circleId,String divId,String rangeId,
                                             String startDate,String endDate) {
        try {

            if (PermissionUtils.check_InternetConnection(VulnerableElephantDetailsActivity.this) == "true") {

                if (circleId.equalsIgnoreCase("-1") || circleId.equalsIgnoreCase("")) {
                    circleId = "All";
                }
                if (divId.equalsIgnoreCase("-1")|| divId.equalsIgnoreCase("")){
                    divId="All";
                }
                if (rangeId.equalsIgnoreCase("-1") || rangeId.equalsIgnoreCase("")){
                    rangeId="All";
                }
                RetrofitInterface retrofitInterface = RetrofitClient.getReportClient()
                        .create(RetrofitInterface.class);

                retrofitInterface.getVulnerableDetails(circleId,divId,rangeId,startDate,endDate).enqueue(new Callback<ArrayList<VulnerableCircleResponse>>() {
                    @Override
                    public void onResponse(Call<ArrayList<VulnerableCircleResponse>> call, Response<ArrayList<VulnerableCircleResponse>> response) {
                        if (response.isSuccessful()){


                            if (response.body().isEmpty()){
                                Toast.makeText(VulnerableElephantDetailsActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                vulnerableDataModels.clear();
                                for (int i=0;i<response.body().size();i++){
                                    vulnerableDataModels.add(response.body().get(i));
                                }
                                callVulnerableAdapter();
                            }
                        }
                        else {
                            Toast.makeText(VulnerableElephantDetailsActivity.this, "Please try again...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<VulnerableCircleResponse>> call, Throwable t) {
                        Toast.makeText(VulnerableElephantDetailsActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();

                    }
                });


            }else {
//                progress_bar_LL.setVisibility(View.GONE);
                Toast.makeText(VulnerableElephantDetailsActivity.this, "Please check your internet !", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(RecyclerView recyclerView,int position,
                            ArrayList<VulnerableDivResponse> vulnerableDataModels) {
        try {

            //for division
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);//It stop lagging in recyclerview
                layoutManager = new LinearLayoutManager(VulnerableElephantDetailsActivity.this,
                        LinearLayoutManager.VERTICAL, false);//for reverse list make it true
                recyclerView.setLayoutManager(layoutManager);
                vulnerableDivAdapter = new VulnerableDivAdapter(VulnerableElephantDetailsActivity.this,
                        vulnerableDataModels, this );
                recyclerView.setAdapter(vulnerableDivAdapter);
                vulnerableDivAdapter.notifyDataSetChanged();


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onItemDivClick(RecyclerView recyclerView, int postion,
                               ArrayList<VulnerableRangeResponse> vulnerableDataModels) {
        try {
            //for range
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);//It stop lagging in recyclerview
                layoutManager = new LinearLayoutManager(VulnerableElephantDetailsActivity.this,
                        LinearLayoutManager.VERTICAL, false);//for reverse list make it true
                recyclerView.setLayoutManager(layoutManager);
                vulnerableRangeAdapter = new VulnerableRangeAdapter(getApplicationContext(),
                        vulnerableDataModels );
                recyclerView.setAdapter(vulnerableRangeAdapter);
                vulnerableRangeAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
