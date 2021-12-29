package com.orsac.android.pccfwildlife.Model;

public class AllLatLngData {

    public String Latitude;
    public String Longitude;

    public AllLatLngData(String latitude, String longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }

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
        return "{" +
                "'" + Latitude + '\'' +
                ",'" + Longitude + '\'' +
                '}';
    }
}
