package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class SectionDataByRangeId {

    @SerializedName("secId")
    private String secId;

    @SerializedName("secName")
    private String secName;

    public SectionDataByRangeId(String secId, String secName) {
        this.secId = secId;
        this.secName = secName;
    }

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    @Override
    public String toString() {
        return secName;
    }
}
