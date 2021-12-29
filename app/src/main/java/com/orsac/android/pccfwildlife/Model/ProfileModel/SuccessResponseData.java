package com.orsac.android.pccfwildlife.Model.ProfileModel;

import com.google.gson.annotations.SerializedName;

public class SuccessResponseData {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
