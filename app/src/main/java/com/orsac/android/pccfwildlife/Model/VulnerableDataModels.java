package com.orsac.android.pccfwildlife.Model;

public class VulnerableDataModels {

    private String divisionNm;
    private String divisionCount;
    private String RangeNm;
    private String RangeCount;

    public VulnerableDataModels(String divisionNm, String divisionCount, String rangeNm, String rangeCount) {
        this.divisionNm = divisionNm;
        this.divisionCount = divisionCount;
        RangeNm = rangeNm;
        RangeCount = rangeCount;
    }

    public VulnerableDataModels() {
    }

    public String getDivisionNm() {
        return divisionNm;
    }

    public void setDivisionNm(String divisionNm) {
        this.divisionNm = divisionNm;
    }

    public String getDivisionCount() {
        return divisionCount;
    }

    public void setDivisionCount(String divisionCount) {
        this.divisionCount = divisionCount;
    }

    public String getRangeNm() {
        return RangeNm;
    }

    public void setRangeNm(String rangeNm) {
        RangeNm = rangeNm;
    }

    public String getRangeCount() {
        return RangeCount;
    }

    public void setRangeCount(String rangeCount) {
        RangeCount = rangeCount;
    }
}
