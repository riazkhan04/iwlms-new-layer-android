package com.orsac.android.pccfwildlife.Model.ChangePasswordModel;

import com.google.gson.annotations.SerializedName;

public class OtpVerifiedModel {

    @SerializedName("login_id")
    private String phoneNo;
    @SerializedName("password")
    private String otp;

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
