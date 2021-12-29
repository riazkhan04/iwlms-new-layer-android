package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class AllNewBeatModels {
    @SerializedName("beatName")
    public String beatName;
    @SerializedName("beatId")
    public String beatId;
    @SerializedName("sectionName")
    public String sectionName;
    @SerializedName("sectionId")
    public String sectionId;
    @SerializedName("rangeName")
    public String rangeName;
    @SerializedName("rangeId")
    public String rangeId;
    @SerializedName("divisionName")
    public String divisionName;
    @SerializedName("divisionId")
    public String divisionId;
    @SerializedName("circleName")
    public String circleName;
    @SerializedName("circleId")
    public String circleId;

    public String getBeatName() {
        return beatName;
    }

    public void setBeatName(String beatName) {
        this.beatName = beatName;
    }

    public String getBeatId() {
        return beatId;
    }

    public void setBeatId(String beatId) {
        this.beatId = beatId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getRangeName() {
        return rangeName;
    }

    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

}
