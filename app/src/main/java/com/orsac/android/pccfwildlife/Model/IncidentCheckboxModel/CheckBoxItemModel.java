package com.orsac.android.pccfwildlife.Model.IncidentCheckboxModel;

public class CheckBoxItemModel {

    boolean isChecked;
    String check_box_item_nm;
    String checkbox_item_value="0";
    int position;

    public CheckBoxItemModel(boolean isChecked, String check_box_item_nm, String checkbox_item_value,int position) {
        this.isChecked = isChecked;
        this.check_box_item_nm = check_box_item_nm;
        this.checkbox_item_value = checkbox_item_value;
        this.position = position;
    }


    public CheckBoxItemModel() {
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCheck_box_item_nm() {
        return check_box_item_nm;
    }

    public void setCheck_box_item_nm(String check_box_item_nm) {
        this.check_box_item_nm = check_box_item_nm;
    }

    public String getCheckbox_item_value() {
        return checkbox_item_value;
    }

    public void setCheckbox_item_value(String checkbox_item_value) {
        this.checkbox_item_value = checkbox_item_value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "{" + check_box_item_nm +
                "," + checkbox_item_value +
                '}';
    }
}
