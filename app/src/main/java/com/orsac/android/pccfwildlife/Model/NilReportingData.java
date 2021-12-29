package com.orsac.android.pccfwildlife.Model;

public class NilReportingData {

    public String txtDate;
    public String txtLocation;
    public String fromtime;
    public String toTime;
    public String txtDivision;
    public String txtRange;
    public String txtSection;
    public String txtBeat;
    public String userId;
    public String remarks;
    public String imei;
    public String txtDeg;
    public String txtMin;
    public String txtSec;
    public String txtDeg1;
    public String txtMin1;
    public String txtSec1;
    public String altitude;
    public String accuracy;

    public NilReportingData(String txtDate, String txtLocation, String fromtime, String toTime, String txtDivision,
                            String txtRange, String txtSection, String txtBeat, String userId, String remarks, String imei,
                            String txtDeg, String txtMin, String txtSec, String txtDeg1, String txtMin1, String txtSec1,
                            String altitude, String accuracy) {
        this.txtDate = txtDate;
        this.txtLocation = txtLocation;
        this.fromtime = fromtime;
        this.toTime = toTime;
        this.txtDivision = txtDivision;
        this.txtRange = txtRange;
        this.txtSection = txtSection;
        this.txtBeat = txtBeat;
        this.userId = userId;
        this.remarks = remarks;
        this.imei = imei;
        this.txtDeg = txtDeg;
        this.txtMin = txtMin;
        this.txtSec = txtSec;
        this.txtDeg1 = txtDeg1;
        this.txtMin1 = txtMin1;
        this.txtSec1 = txtSec1;
        this.altitude = altitude;
        this.accuracy = accuracy;
    }

    public String getTxtDate() {
        return txtDate;
    }

    public void setTxtDate(String txtDate) {
        this.txtDate = txtDate;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFromtime() {
        return fromtime;
    }

    public void setFromtime(String fromtime) {
        this.fromtime = fromtime;
    }

    public String getTxtLocation() {
        return txtLocation;
    }

    public void setTxtLocation(String txtLocation) {
        this.txtLocation = txtLocation;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
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
}
