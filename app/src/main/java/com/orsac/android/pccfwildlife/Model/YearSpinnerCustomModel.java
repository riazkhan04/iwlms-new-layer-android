package com.orsac.android.pccfwildlife.Model;

public class YearSpinnerCustomModel {
    boolean isChecked;
    String selected_str;
    String value;
    int position;

    public YearSpinnerCustomModel(boolean isChecked, String selected_str, String value) {
        this.isChecked = isChecked;
        this.selected_str = selected_str;
        this.value = value;
    }

    public YearSpinnerCustomModel(String selected_str, int position, String value) {
        this.selected_str = selected_str;
        this.position = position;
        this.value = value;
    }

    public YearSpinnerCustomModel(boolean isChecked, String selected_str, int position) {
        this.isChecked = isChecked;
        this.selected_str = selected_str;
        this.position = position;
    }

    public YearSpinnerCustomModel(String selected_str, int position) {
        this.selected_str = selected_str;
        this.position = position;
    }

    public YearSpinnerCustomModel(String selected_str) {
        this.selected_str = selected_str;
    }

    public YearSpinnerCustomModel() {
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSelected_str() {
        return selected_str;
    }

    public void setSelected_str(String selected_str) {
        this.selected_str = selected_str;
    }

    public int getPosition() {
        return position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public String toString() {
        return "{"+selected_str+","+value+"}" ;
    }
}
