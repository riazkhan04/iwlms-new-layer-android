package com.orsac.android.pccfwildlife.Model.LoginModel;

import com.google.gson.annotations.SerializedName;

public class Login_User_Data {

    @SerializedName("id")
    private int id;
    @SerializedName("user_name")
    public String user_name;
    @SerializedName("user_designation")
    private String user_designation;
    @SerializedName("user_email")
    private String user_email;
    @SerializedName("user_status")
    private String user_status;
    @SerializedName("login_id")
    private String login_id;
    @SerializedName("password")
    private String password;
    @SerializedName("wl_division")
    private String wl_division;
    @SerializedName("user_creation_date")
    private String user_creation_date;
    @SerializedName("wl_range")
    private String wl_range;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("phone")
    private String phone;
    @SerializedName("user_address")
    private String user_address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_designation() {
        return user_designation;
    }

    public void setUser_designation(String user_designation) {
        this.user_designation = user_designation;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWl_division() {
        return wl_division;
    }

    public void setWl_division(String wl_division) {
        this.wl_division = wl_division;
    }

    public String getUser_creation_date() {
        return user_creation_date;
    }

    public void setUser_creation_date(String user_creation_date) {
        this.user_creation_date = user_creation_date;
    }

    public String getWl_range() {
        return wl_range;
    }

    public void setWl_range(String wl_range) {
        this.wl_range = wl_range;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }
}
