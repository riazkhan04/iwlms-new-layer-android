package com.orsac.android.pccfwildlife.Model;

import com.google.gson.annotations.SerializedName;

public class AppVersionModel {
    @SerializedName("versionCode")
    String versionCode;

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
