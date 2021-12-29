package com.orsac.android.pccfwildlife.Model.ChangePasswordModel;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequestModel {
    @SerializedName("username")
    private String username;
    @SerializedName("oldpassword")
    private String oldPassword;
    @SerializedName("newPassword")
    private String newPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
