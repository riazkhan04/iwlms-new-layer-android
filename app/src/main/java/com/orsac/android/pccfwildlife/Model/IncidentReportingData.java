package com.orsac.android.pccfwildlife.Model;

public class IncidentReportingData {

    public String txtDivision;
    public String txtRange;
    public String txtSection;
    public String txtBeat;
    public String txtLocation;
    public String txtDate;
    public String txtDeg;
    public String txtMin;
    public String txtSec;
    public String txtDeg1;
    public String txtMin1;
    public String txtSec1;
    public String imageUri;
    public String ImagePath;
    public String incidentType;
    public String incidentjson;
    public String circleId;
    public String userId;
    public String imei;
    public String altitude;
    public String accuracy;
    public String incidentRemark;
    public String deathReason;

    public IncidentReportingData(String txtDivision, String txtRange, String txtSection,
                                 String txtBeat, String txtLocation, String txtDate,
                                 String txtDeg, String txtMin, String txtSec, String txtDeg1,
                                 String txtMin1, String txtSec1, String imageUri, String imagePath,
                                 String incidentType, String incidentjson, String circleId,
                                 String userId, String imei, String altitude, String accuracy,
                                 String incidentRemark, String deathReason) {
        this.txtDivision = txtDivision;
        this.txtRange = txtRange;
        this.txtSection = txtSection;
        this.txtBeat = txtBeat;
        this.txtLocation = txtLocation;
        this.txtDate = txtDate;
        this.txtDeg = txtDeg;
        this.txtMin = txtMin;
        this.txtSec = txtSec;
        this.txtDeg1 = txtDeg1;
        this.txtMin1 = txtMin1;
        this.txtSec1 = txtSec1;
        this.imageUri = imageUri;
        ImagePath = imagePath;
        this.incidentType = incidentType;
        this.incidentjson = incidentjson;
        this.circleId = circleId;
        this.userId = userId;
        this.imei = imei;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.incidentRemark = incidentRemark;
        this.deathReason = deathReason;
    }

    public String getDeathReason() {
        return deathReason;
    }

    public void setDeathReason(String deathReason) {
        this.deathReason = deathReason;
    }

    public String getTxtDivision() {
        return txtDivision;
    }

    public void setTxtDivision(String txtDivision) {
        this.txtDivision = txtDivision;
    }

    public String getTxtRange() {
        return txtRange;
    }

    public void setTxtRange(String txtRange) {
        this.txtRange = txtRange;
    }

    public String getTxtSection() {
        return txtSection;
    }

    public void setTxtSection(String txtSection) {
        this.txtSection = txtSection;
    }

    public String getTxtBeat() {
        return txtBeat;
    }

    public void setTxtBeat(String txtBeat) {
        this.txtBeat = txtBeat;
    }

    public String getTxtLocation() {
        return txtLocation;
    }

    public void setTxtLocation(String txtLocation) {
        this.txtLocation = txtLocation;
    }

    public String getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(String txtDate) {
        this.txtDate = txtDate;
    }

    public String getTxtDeg() {
        return txtDeg;
    }

    public void setTxtDeg(String txtDeg) {
        this.txtDeg = txtDeg;
    }

    public String getTxtMin() {
        return txtMin;
    }

    public void setTxtMin(String txtMin) {
        this.txtMin = txtMin;
    }

    public String getTxtSec() {
        return txtSec;
    }

    public void setTxtSec(String txtSec) {
        this.txtSec = txtSec;
    }

    public String getTxtDeg1() {
        return txtDeg1;
    }

    public void setTxtDeg1(String txtDeg1) {
        this.txtDeg1 = txtDeg1;
    }

    public String getTxtMin1() {
        return txtMin1;
    }

    public void setTxtMin1(String txtMin1) {
        this.txtMin1 = txtMin1;
    }

    public String getTxtSec1() {
        return txtSec1;
    }

    public void setTxtSec1(String txtSec1) {
        this.txtSec1 = txtSec1;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getIncidentjson() {
        return incidentjson;
    }

    public void setIncidentjson(String incidentjson) {
        this.incidentjson = incidentjson;
    }

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getIncidentRemark() {
        return incidentRemark;
    }

    public void setIncidentRemark(String incidentRemark) {
        this.incidentRemark = incidentRemark;
    }
}
