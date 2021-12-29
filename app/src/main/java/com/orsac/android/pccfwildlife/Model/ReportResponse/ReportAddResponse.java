package com.orsac.android.pccfwildlife.Model.ReportResponse;

import com.google.gson.annotations.SerializedName;

public class ReportAddResponse {
    @SerializedName("status")
    ReportSuccess_ResponseObj reportSuccess_responseObj;

    @SerializedName("response")
    private String response;

    public ReportSuccess_ResponseObj getReportSuccess_responseObj() {
        return reportSuccess_responseObj;
    }

    public void setReportSuccess_responseObj(ReportSuccess_ResponseObj reportSuccess_responseObj) {
        this.reportSuccess_responseObj = reportSuccess_responseObj;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
