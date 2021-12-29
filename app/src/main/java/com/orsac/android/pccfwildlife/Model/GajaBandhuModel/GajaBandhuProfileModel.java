package com.orsac.android.pccfwildlife.Model.GajaBandhuModel;

import com.google.gson.annotations.SerializedName;

public class GajaBandhuProfileModel {

    @SerializedName("id")
    public String Id;
    @SerializedName("username")
    public String username;
    @SerializedName("age")
    public String age;
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("division")
    public String division;
    @SerializedName("range")
    public String range;
    @SerializedName("section")
    public String section;
    @SerializedName("beat")
    public String beat;
    @SerializedName("imgPath")
    public String imagePath;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBeat() {
        return beat;
    }

    public void setBeat(String beat) {
        this.beat = beat;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
