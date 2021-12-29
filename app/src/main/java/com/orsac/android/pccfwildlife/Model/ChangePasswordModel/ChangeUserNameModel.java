package com.orsac.android.pccfwildlife.Model.ChangePasswordModel;

import com.google.gson.annotations.SerializedName;

public class ChangeUserNameModel {
    @SerializedName("userName")
    private String username;
    @SerializedName("oldUsername")
    private String oldUsername;
    @SerializedName("newUsername")
    private String newUsername;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
