package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class Range_model {

    @SerializedName("range_id")
    private int range_id;

    @SerializedName("range_name")
    private String range_name;

    @SerializedName("fk_div")
    private int fk_div;

    @SerializedName("division_master")
    private Division_model division_model_obj;

    public int getRange_id() {
        return range_id;
    }

    public void setRange_id(int range_id) {
        this.range_id = range_id;
    }

    public String getRange_name() {
        return range_name;
    }

    public void setRange_name(String range_name) {
        this.range_name = range_name;
    }

    public int getFk_div() {
        return fk_div;
    }

    public void setFk_div(int fk_div) {
        this.fk_div = fk_div;
    }

    public Division_model getDivision_model_obj() {
        return division_model_obj;
    }

    public void setDivision_model_obj(Division_model division_model_obj) {
        this.division_model_obj = division_model_obj;
    }
}
