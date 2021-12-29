package com.orsac.android.pccfwildlife.Model.AllLayerModel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MapDataResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("post")
    private ArrayList<StGeojsonObj> post;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<StGeojsonObj> getPost() {
        return post;
    }

    public void setPost(ArrayList<StGeojsonObj> post) {
        this.post = post;
    }
}
