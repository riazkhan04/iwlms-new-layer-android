package com.orsac.android.pccfwildlife.Model;

import com.google.gson.annotations.SerializedName;

public class EditReportResponseData {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
