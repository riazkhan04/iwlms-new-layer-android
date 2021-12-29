package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllDivision_data {

    @SerializedName("division")
    private ArrayList<Division_model> division_models_arr;

    @SerializedName("range")
    private ArrayList<Range_model> range_models_arr;

    @SerializedName("section")
    private ArrayList<Section_model> section_models_arr;

    @SerializedName("beat")
    private ArrayList<Beat_model> beat_models_arr;

    public ArrayList<Division_model> getDivision_models_arr() {
        return division_models_arr;
    }

    public void setDivision_models_arr(ArrayList<Division_model> division_models_arr) {
        this.division_models_arr = division_models_arr;
    }

    public ArrayList<Range_model> getRange_models_arr() {
        return range_models_arr;
    }

    public void setRange_models_arr(ArrayList<Range_model> range_models_arr) {
        this.range_models_arr = range_models_arr;
    }

    public ArrayList<Section_model> getSection_models_arr() {
        return section_models_arr;
    }

    public void setSection_models_arr(ArrayList<Section_model> section_models_arr) {
        this.section_models_arr = section_models_arr;
    }

    public ArrayList<Beat_model> getBeat_models_arr() {
        return beat_models_arr;
    }

    public void setBeat_models_arr(ArrayList<Beat_model> beat_models_arr) {
        this.beat_models_arr = beat_models_arr;
    }
}
