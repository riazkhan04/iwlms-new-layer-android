package com.orsac.android.pccfwildlife.Model.IncidentCheckboxModel;

public class AnimalIncidentItemModel {

    String item_nm;
    String item_value="0";
    boolean isShow=true;

    public AnimalIncidentItemModel() {
    }

    public AnimalIncidentItemModel(String item_nm, String item_value, boolean isShow) {
        this.item_nm = item_nm;
        this.item_value = item_value;
        this.isShow = isShow;
    }

    public String getItem_nm() {
        return item_nm;
    }

    public void setItem_nm(String item_nm) {
        this.item_nm = item_nm;
    }

    public String getItem_value() {
        return item_value;
    }

    public void setItem_value(String item_value) {
        this.item_value = item_value;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
