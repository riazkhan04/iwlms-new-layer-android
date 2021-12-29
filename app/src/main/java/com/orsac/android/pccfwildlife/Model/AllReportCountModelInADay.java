package com.orsac.android.pccfwildlife.Model;

import com.google.gson.annotations.SerializedName;

public class AllReportCountModelInADay {

    @SerializedName("allReportCount")
    private String allReportCount;

    @SerializedName("nillReportCount")
    private String nillReportCount;

    @SerializedName("allReportCountDirect")
    private String directReportCount;

    @SerializedName("allReportCountInDirect")
    private String indirectReportCount;

    @SerializedName("allElephantDeathReportCount")
    private String allElephantDeathReportCount;

     @SerializedName("allFireAlertReportCount")
    private String allFireAlertReportCount;

     @SerializedName("allVulnerabilityCount")
    private String allVulnerabilityCount;

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

    public String getAllElephantDeathReportCount() {
        return allElephantDeathReportCount;
    }

    public void setAllElephantDeathReportCount(String allElephantDeathReportCount) {
        this.allElephantDeathReportCount = allElephantDeathReportCount;
    }

    public String getAllFireAlertReportCount() {
        return allFireAlertReportCount;
    }

    public void setAllFireAlertReportCount(String allFireAlertReportCount) {
        this.allFireAlertReportCount = allFireAlertReportCount;
    }

    public String getAllVulnerabilityCount() {
        return allVulnerabilityCount;
    }

    public void setAllVulnerabilityCount(String allVulnerabilityCount) {
        this.allVulnerabilityCount = allVulnerabilityCount;
    }
}
