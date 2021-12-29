package com.orsac.android.pccfwildlife.Model;

import com.google.gson.annotations.SerializedName;

public class AllReportCountModel {

    @SerializedName("allReportCount")
    private String allReportCount;

    @SerializedName("nillReportCount")
    private String nillReportCount;

    @SerializedName("allReportCountDirect")
    private String directReportCount;

    @SerializedName("allReportCountInDirect")
    private String indirectReportCount;

    public String getAllReportCount() {
        return allReportCount;
    }

    public void setAllReportCount(String allReportCount) {
        this.allReportCount = allReportCount;
    }

    public String getNillReportCount() {
        return nillReportCount;
    }

    public void setNillReportCount(String nillReportCount) {
        this.nillReportCount = nillReportCount;
    }

    public String getDirectReportCount() {
        return directReportCount;
    }

    public void setDirectReportCount(String directReportCount) {
        this.directReportCount = directReportCount;
    }

    public String getIndirectReportCount() {
        return indirectReportCount;
    }

    public void setIndirectReportCount(String indirectReportCount) {
        this.indirectReportCount = indirectReportCount;
    }
}
