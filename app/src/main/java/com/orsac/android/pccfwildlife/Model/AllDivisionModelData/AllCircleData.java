package com.orsac.android.pccfwildlife.Model.AllDivisionModelData;

import com.google.gson.annotations.SerializedName;

public class AllCircleData {

    @SerializedName("circleId")
    private String circleId;

    @SerializedName("circleName")
    private String circleName;

    @SerializedName("isActive")
    private String isActive;

    public AllCircleData() {

    }

    public AllCircleData(String circleId, String circleName) {
        this.circleId = circleId;
        this.circleName = circleName;
    }

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return circleName ;
    }
}
