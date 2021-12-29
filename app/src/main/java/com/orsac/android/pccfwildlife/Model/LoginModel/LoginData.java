package com.orsac.android.pccfwildlife.Model.LoginModel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LoginData {

    @SerializedName("id")
    private String id;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("roles")
    private ArrayList<String> roles;

    @SerializedName("accessToken")
    private String accessToken;
     @SerializedName("tokenType")
    private String tokenType;
     @SerializedName("juridictionName")
    private String juridictionName;
     @SerializedName("juridictionId")
    private String juridictionId;
     @SerializedName("circleId")
    private String circleId;
     @SerializedName("divisionId")
    private String divisionId;
     @SerializedName("rangeId")
    private String rangeId;
     @SerializedName("sectionId")
    private String sectionId;
     @SerializedName("beatId")
    private String beatId;
     @SerializedName("roleId")
    private String roleId;

//    @SerializedName("user")
//    Login_User_Data login_user_data;
//
//    @SerializedName("status")
//    private String status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getJuridictionName() {
        return juridictionName;
    }

    public void setJuridictionName(String juridictionName) {
        this.juridictionName = juridictionName;
    }

    public String getJuridictionId() {
        return juridictionId;
    }

    public void setJuridictionId(String juridictionId) {
        this.juridictionId = juridictionId;
    }

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getBeatId() {
        return beatId;
    }

    public void setBeatId(String beatId) {
        this.beatId = beatId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }


}
