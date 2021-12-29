package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class Division_model {

    @SerializedName("div_id")
    private int div_id;

    @SerializedName("division_name")
    private String division_name;

    public int getDiv_id() {
        return div_id;
    }

    public void setDiv_id(int div_id) {
        this.div_id = div_id;
    }

    public String getDivision_name() {
        return division_name;
    }

    public void setDivision_name(String division_name) {
        this.division_name = division_name;
    }
}
