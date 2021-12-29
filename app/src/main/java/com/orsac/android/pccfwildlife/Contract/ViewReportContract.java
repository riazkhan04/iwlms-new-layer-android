package com.orsac.android.pccfwildlife.Contract;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.Adapters.ViewReportRecyclerVNew;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public interface ViewReportContract {
    interface view{
        void show_progress();
        void hide_progress();
        void success(String message);
        void failed(String message);
    }
    interface presenter{
//        void loadViewAllReport_FromCount(String fromDate, String toDate, String userId, ArrayList<ViewReportItemData_obj> view_item_arr,
//                                    RecyclerView view_recyclerV, ViewReportRecyclerV_Adapter.Update_clickListener listener);

//        void loadViewReport(String fromDate, String toDate, ArrayList<ViewReportItemData_obj> view_item_arr, String circle,
//                            String division, String range, String section, String beat,
//                            RecyclerView view_recyclerV, ViewReportRecyclerV_Adapter.Update_clickListener listener,
//                            TextView no_data, String report_type, ScrollView scrollView, int height, int weight, LinearLayout progressLL);

        void loadViewAllReport_FromCount(String fromDate, String toDate, String userId, ArrayList<ViewReportItemData_obj> view_item_arr,
                                         RecyclerView view_recyclerV, ViewReportRecyclerVNew.Update_clickListener listener);


        void loadViewReport(String fromDate, String toDate, ArrayList<ViewReportItemData_obj> view_item_arr, String circle,
                            String division, String range, String section, String beat,
                            RecyclerView view_recyclerV, ViewReportRecyclerVNew.Update_clickListener listener,
                            TextView no_data, String report_type, ScrollView scrollView, int height, int weight,
                            LinearLayout progressLL,TextView date_txt,TextView report_count_txt,TextView updatedBy);

        void loadBottomLayoutData(TextView date, TextView fromTime, TextView toTime, TextView location, TextView divisionTxt,
                                  TextView rangeTxt, TextView sectionTxt, TextView beatTxt, TextView reportType, TextView report,
                                  TextView totalElephant, TextView herdTxt, TextView makhnaTxt, TextView tuskerTxt,
                                  TextView femaleTxt, TextView calfTxt, TextView remark, ScrollView bottomlayout_CV);

    }
}
