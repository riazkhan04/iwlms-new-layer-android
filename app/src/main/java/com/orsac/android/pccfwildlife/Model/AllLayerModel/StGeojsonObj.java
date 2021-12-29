package com.orsac.android.pccfwildlife.Model.AllLayerModel;

import com.google.gson.annotations.SerializedName;

public class StGeojsonObj {
    @SerializedName("st_asgeojson")
    private String geojson;
    @SerializedName("st_area")
    private String area;
    @SerializedName("name_e")
    private String layer_name;

    public String getGeojson() {
        return geojson;
    }

    public void setGeojson(String geojson) {
        this.geojson = geojson;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLayer_name() {
        return layer_name;
    }

    public void setLayer_name(String layer_name) {
        this.layer_name = layer_name;
    }
}
