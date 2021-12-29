package com.orsac.android.pccfwildlife.Model.GajaBandhuModel;

import com.google.gson.annotations.SerializedName;

public class GajaBandhuReportDataModel {

    @SerializedName("reportId")
    public String reportId;
    @SerializedName("description")
    public String description;
    @SerializedName("imgPath")
    public String imgPath;
    @SerializedName("audioMessagePath")
    public String audioMessagePath;
    @SerializedName("videoMessagePath")
    public String videoMessagePath;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("userId")
    public String userId;
    @SerializedName("division")
    public String division;
    @SerializedName("range")
    public String range;
    @SerializedName("section")
    public String section;
    @SerializedName("beat")
    public String beat;
    @SerializedName("divisionId")
    public String divisionId;
    @SerializedName("rangeId")
    public String rangeId;
    @SerializedName("sectionId")
    public String sectionId;
    @SerializedName("beatId")
    public String beatId;
    @SerializedName("reportingDate")
    public String reportingDate;
    @SerializedName("status")
    public String status;

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getAudioMessagePath() {
        return audioMessagePath;
    }

    public void setAudioMessagePath(String audioMessagePath) {
        this.audioMessagePath = audioMessagePath;
    }

    public String getVideoMessagePath() {
        return videoMessagePath;
    }

    public void setVideoMessagePath(String videoMessagePath) {
        this.videoMessagePath = videoMessagePath;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBeat() {
        return beat;
    }

    public void setBeat(String beat) {
        this.beat = beat;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getBeatId() {
        return beatId;
    }

    public void setBeatId(String beatId) {
        this.beatId = beatId;
    }

    public String getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(String reportingDate) {
        this.reportingDate = reportingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
