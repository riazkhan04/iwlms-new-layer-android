package com.orsac.android.pccfwildlife.Model.ReportResponse;

import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;

import java.util.ArrayList;

public class ViewReportResponse {

//    @SerializedName("status")
//    ReportSuccess_ResponseObj reportSuccess_responseObj;

//    @SerializedName("response")
    ArrayList<ViewReportItemData_obj> viewReportItemData_arr;

//    public ReportSuccess_ResponseObj getReportSuccess_responseObj() {
//        return reportSuccess_responseObj;
//    }
//
//    public void setReportSuccess_responseObj(ReportSuccess_ResponseObj reportSuccess_responseObj) {
//        this.reportSuccess_responseObj = reportSuccess_responseObj;
//    }

    public ArrayList<ViewReportItemData_obj> getViewReportItemData_arr() {
        return viewReportItemData_arr;
    }

    public void setViewReportItemData_arr(ArrayList<ViewReportItemData_obj> viewReportItemData_arr) {
        this.viewReportItemData_arr = viewReportItemData_arr;
    }
}
