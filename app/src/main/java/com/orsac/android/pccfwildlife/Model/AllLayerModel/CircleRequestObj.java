package com.orsac.android.pccfwildlife.Model.AllLayerModel;

public class CircleRequestObj {
    private String circle_id;

    public CircleRequestObj(String circle_id) {
        this.circle_id = circle_id;
    }

    public String getLayerId() {
        return circle_id;
    }

    public void setLayerId(String circle_id) {
        this.circle_id = circle_id;
    }
}
