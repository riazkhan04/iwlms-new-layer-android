package com.orsac.android.pccfwildlife.Model.ProfileModel;

import com.google.gson.annotations.SerializedName;

public class ProfileData {

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("middleName")
    private String middleName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("userDesignation")
    private String userDesignation;

    @SerializedName("juridicitionName")
    private String juridicitionName;

    @SerializedName("userPhoneNumber")
    private String userPhoneNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("userAddress")
    private String userAddress;

    @SerializedName("imgPath")
    private String imgPath;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserDesignation() {
        return userDesignation;
    }

    public void setUserDesignation(String userDesignation) {
        this.userDesignation = userDesignation;
    }

    public String getJuridicitionName() {
        return juridicitionName;
    }

    public void setJuridicitionName(String juridicitionName) {
        this.juridicitionName = juridicitionName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
