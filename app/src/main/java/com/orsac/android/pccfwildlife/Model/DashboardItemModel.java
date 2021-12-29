package com.orsac.android.pccfwildlife.Model;

public class DashboardItemModel {

    String _item_name;
    String item_id;
    int item_image;
    int elephant_image;

    public DashboardItemModel() {
    }

    public DashboardItemModel(String _item_name, String item_id) {
        this._item_name = _item_name;
        this.item_id = item_id;
    }

    public DashboardItemModel(String _item_name, String item_id, int elephant_image) {
        this._item_name = _item_name;
        this.item_id = item_id;
        this.elephant_image = elephant_image;
    }

    public DashboardItemModel(String _item_name, String item_id, int item_image, int elephant_image) {
        this._item_name = _item_name;
        this.item_id = item_id;
        this.item_image = item_image;
        this.elephant_image = elephant_image;
    }

    public DashboardItemModel(String _item_name) {
        this._item_name = _item_name;
    }

    public String get_item_name() {
        return _item_name;
    }

    public void set_item_name(String _item_name) {
        this._item_name = _item_name;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public int getItem_image() {
        return item_image;
    }

    public void setItem_image(int item_image) {
        this.item_image = item_image;
    }

    public int getElephant_image() {
        return elephant_image;
    }

    public void setElephant_image(int elephant_image) {
        this.elephant_image = elephant_image;
    }
}
