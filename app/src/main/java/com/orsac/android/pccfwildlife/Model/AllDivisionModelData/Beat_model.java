package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class Beat_model {
    @SerializedName("beat_id")
    private int beat_id;

    @SerializedName("beat_name")
    private String beat_name;

    @SerializedName("fk_section")
    private int fk_section;

    @SerializedName("section_master")
    private Section_model section_model_arr;

    public int getBeat_id() {
        return beat_id;
    }

    public void setBeat_id(int beat_id) {
        this.beat_id = beat_id;
    }

    public String getBeat_name() {
        return beat_name;
    }

    public void setBeat_name(String beat_name) {
        this.beat_name = beat_name;
    }

    public int getFk_section() {
        return fk_section;
    }

    public void setFk_section(int fk_section) {
        this.fk_section = fk_section;
    }

    public Section_model getSection_model_arr() {
        return section_model_arr;
    }

    public void setSection_model_arr(Section_model section_model_arr) {
        this.section_model_arr = section_model_arr;
    }
}
