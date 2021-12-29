package com.orsac.android.pccfwildlife.Model;

public class ReportCountItemModel {

    private String reportNo;
    private String reportName;

    public ReportCountItemModel(String reportNo, String reportName) {
        this.reportNo = reportNo;
        this.reportName = reportName;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
