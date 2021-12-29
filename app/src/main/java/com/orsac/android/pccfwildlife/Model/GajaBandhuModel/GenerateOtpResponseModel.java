package com.orsac.android.pccfwildlife.Model.GajaBandhuModel;

import com.google.gson.annotations.SerializedName;

public class GenerateOtpResponseModel {

    @SerializedName("message")
    public String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
