package com.orsac.android.pccfwildlife.Model;

import com.google.gson.annotations.SerializedName;

public class AllLatLngModel {

    @SerializedName("latitude")
    public String Latitude;

    @SerializedName("longitude")
    public String Longitude;

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    @Override
    public String toString() {
        return "'" + Latitude + '\'' +
                ",'" + Longitude + '\''
                ;
    }
}
