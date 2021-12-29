package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class Section_model {

    @SerializedName("section_id")
    private int section_id;

    @SerializedName("section_name")
    private String section_name;

    @SerializedName("fk_range")
    private int fk_range;

    @SerializedName("range_master")
    private Range_model range_model_arr;

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public int getFk_range() {
        return fk_range;
    }

    public void setFk_range(int fk_range) {
        this.fk_range = fk_range;
    }

    public Range_model getRange_model_arr() {
        return range_model_arr;
    }

    public void setRange_model_arr(Range_model range_model_arr) {
        this.range_model_arr = range_model_arr;
    }
}
