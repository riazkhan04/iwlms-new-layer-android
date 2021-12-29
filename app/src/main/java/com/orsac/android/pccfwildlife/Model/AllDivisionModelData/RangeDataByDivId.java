package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class RangeDataByDivId {

    @SerializedName("rangeId")
    private String rangeId;

    @SerializedName("rangeName")
    private String rangeName;

    public RangeDataByDivId(String rangeId, String rangeName) {
        this.rangeId = rangeId;
        this.rangeName = rangeName;
    }

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getRangeName() {
        return rangeName;
    }

    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }

    @Override
    public String toString() {
        return rangeName;
    }
}
