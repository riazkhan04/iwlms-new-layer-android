package com.orsac.android.pccfwildlife.Adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final Activity context;
    LinearLayout division_ll,range_ll,total_ll,herd_ll;
    TextView division_nm,range_nm,section_nm,beat_nm,nothing_selected;
    String division_name="",range_name="",total_no="",herd_no="";
    Marker map_marker;
    int index;
    ArrayList<ViewReportItemData_obj> alllatLngs_arr;

    public CustomInfoAdapter(Activity context,String division,String range,
                             String total,String herd,Marker marker){
        this.context = context;
        this.division_name=division;
        this.range_name=range;
        this.total_no=total;
        this.herd_no=herd;
        this.map_marker=marker;
    }
    public CustomInfoAdapter(Activity context,ArrayList<ViewReportItemData_obj> alllatLngs_arr,
                             Marker marker,int index){
        this.context = context;
        this.alllatLngs_arr=alllatLngs_arr;
        this.map_marker=marker;
        this.index=index;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.custom_info_window_marker_layout, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT));

        division_nm =  view.findViewById(R.id.division);
        range_nm = view.findViewById(R.id.range);
        section_nm = view.findViewById(R.id.section);
        beat_nm = view.findViewById(R.id.beat);
        division_ll = view.findViewById(R.id.division_ll);
        range_ll = view.findViewById(R.id.range_ll);
        total_ll = view.findViewById(R.id.section_ll);
        herd_ll = view.findViewById(R.id.beat_ll);
        nothing_selected = view.findViewById(R.id.nothing_selected);
        nothing_selected.setVisibility(View.GONE);
        marker=map_marker;
//        int index = 0;
//        try {
//            index = (int) marker.getTag();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//        if (division_name.equalsIgnoreCase("")||division_name.equalsIgnoreCase("Select Division")){
//            nothing_selected.setVisibility(View.VISIBLE);
//
//            division_ll.setVisibility(View.GONE);
//            range_ll.setVisibility(View.GONE);
////            section_ll.setVisibility(View.GONE);
////            beat_ll.setVisibility(View.GONE);
//            Toast.makeText(context, "Please select to show !", Toast.LENGTH_SHORT).show();
//        }
//        else if (range_name.equalsIgnoreCase("")||range_name.equalsIgnoreCase("Select Range")){
//            nothing_selected.setVisibility(View.GONE);
//            division_nm.setText(alllatLngs_arr.get(index).getDivision());
//
//            division_ll.setVisibility(View.VISIBLE);
//            range_ll.setVisibility(View.VISIBLE);
//            total_ll.setVisibility(View.VISIBLE);
//            herd_ll.setVisibility(View.VISIBLE);
////            section_ll.setVisibility(View.GONE);
////            beat_ll.setVisibility(View.GONE);
//        }
//
//        else if (total_no.equalsIgnoreCase("")){
//            nothing_selected.setVisibility(View.GONE);
//            division_nm.setText(alllatLngs_arr.get(index).getDivision());
//            range_nm.setText(alllatLngs_arr.get(index).getRange());
//
//            division_ll.setVisibility(View.VISIBLE);
//            range_ll.setVisibility(View.VISIBLE);
//            total_ll.setVisibility(View.VISIBLE);
//            herd_ll.setVisibility(View.VISIBLE);
//        }
//        else if (herd_no.equalsIgnoreCase("")){
//            nothing_selected.setVisibility(View.GONE);
//            division_nm.setText(alllatLngs_arr.get(index).getDivision());
//            range_nm.setText(alllatLngs_arr.get(index).getRange());
//            section_nm.setText(alllatLngs_arr.get(index).getTotal());
//
//            division_ll.setVisibility(View.VISIBLE);
//            range_ll.setVisibility(View.VISIBLE);
//            total_ll.setVisibility(View.VISIBLE);
//            herd_ll.setVisibility(View.VISIBLE);
//        }
//        for (int i=0;i<alllatLngs_arr.size();i++){
            division_nm.setText(alllatLngs_arr.get(index).getDivision());
            range_nm.setText(alllatLngs_arr.get(index).getRange());
            section_nm.setText(alllatLngs_arr.get(index).getTotal());
            beat_nm.setText(alllatLngs_arr.get(index).getHeard());
//        }

        division_ll.setVisibility(View.VISIBLE);
        range_ll.setVisibility(View.VISIBLE);
        total_ll.setVisibility(View.VISIBLE);
        herd_ll.setVisibility(View.VISIBLE);



        return view;
    }
}
