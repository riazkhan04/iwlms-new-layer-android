package com.orsac.android.pccfwildlife.Model;

import com.google.gson.annotations.SerializedName;

public class IncidentDataModel {

    @SerializedName("name")
    private String name;
    @SerializedName("count")
    private String count;
    @SerializedName("id")
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
