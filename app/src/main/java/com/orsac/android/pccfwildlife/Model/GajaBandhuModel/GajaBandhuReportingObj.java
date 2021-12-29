package com.orsac.android.pccfwildlife.Model.GajaBandhuModel;

public class GajaBandhuReportingObj {

    public String date;
    public String userId;
    public String message;
    public String latitude;
    public String longitude;
    public String reportType;
    public String fileName;
    public String folderType;

    public GajaBandhuReportingObj(String date, String userId,
                                  String message, String latitude, String longitude,
                                  String reportType, String fileName, String folderType) {
        this.date = date;
        this.userId = userId;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportType = reportType;
        this.fileName = fileName;
        this.folderType = folderType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
