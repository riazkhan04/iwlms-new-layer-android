package com.orsac.android.pccfwildlife.Model;

import com.google.gson.annotations.SerializedName;

public class ViewIncidentReportCount {

    @SerializedName("name")
    private String name;
    @SerializedName("countValue")
    private String countValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountValue() {
        return countValue;
    }

    public void setCountValue(String countValue) {
        this.countValue = countValue;
    }
}
