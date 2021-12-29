package com.orsac.android.pccfwildlife.Model.ReportResponse;

import com.google.gson.annotations.SerializedName;

public class ReportSuccess_ResponseObj {
    @SerializedName("code")
    String code;
    @SerializedName("message")
    String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
