package com.orsac.android.pccfwildlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.orsac.android.pccfwildlife.Model.YearSpinnerCustomModel;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomSpinnerYearMonthAdapter extends RecyclerView.Adapter<CustomSpinnerYearMonthAdapter.ViewHolder> {

    Context context;
    ArrayList<YearSpinnerCustomModel> year_month_arrList;
    OnCheckbox_Selected_listener checkbox_selected_listener;
    String type="";

    public interface OnCheckbox_Selected_listener{
        void oncheckBoxSelected(ArrayList<YearSpinnerCustomModel> checkBoxItemModel,String year_month_type);
    }

    public CustomSpinnerYearMonthAdapter(Context context, ArrayList<YearSpinnerCustomModel> year_month_arrList) {
        this.context = context;
        this.year_month_arrList = year_month_arrList;
    }

    public CustomSpinnerYearMonthAdapter(Context context, ArrayList<YearSpinnerCustomModel> year_month_arrList, OnCheckbox_Selected_listener checkbox_selected_listener) {
        this.context = context;
        this.year_month_arrList = year_month_arrList;
        this.checkbox_selected_listener = checkbox_selected_listener;
    }

    public CustomSpinnerYearMonthAdapter(Context context, ArrayList<YearSpinnerCustomModel> year_month_arrList, OnCheckbox_Selected_listener checkbox_selected_listener, String type) {
        this.context = context;
        this.year_month_arrList = year_month_arrList;
        this.checkbox_selected_listener = checkbox_selected_listener;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_year_spinner_analytics,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {

            holder.year_textV.setText(year_month_arrList.get(position).getSelected_str());


            if(year_month_arrList.get(position).isChecked()){
                holder.tick_checkbox.setVisibility(View.VISIBLE);
                holder.blank_checkbox.setVisibility(View.GONE);
            }else{
                holder.tick_checkbox.setVisibility(View.GONE);
                holder.blank_checkbox.setVisibility(View.VISIBLE);
            }

            if (year_month_arrList.get(position).isChecked()){
                for (int i=0;i<year_month_arrList.size();i++) {
                    holder.tick_checkbox.setVisibility(View.VISIBLE);
                    holder.blank_checkbox.setVisibility(View.GONE);
                }
            }

            holder.check_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.blank_checkbox.getVisibility()==View.VISIBLE){

                        if (year_month_arrList.get(position).getPosition()==0){
                            for (int i=0;i<year_month_arrList.size();i++){
                                year_month_arrList.get(i).setChecked(true);
                            }
                        }
                        else {
                            year_month_arrList.get(position).setChecked(true);
                        }
                        notifyDataSetChanged();
                    }
                    else {
                        if (year_month_arrList.get(position).getPosition()==0){
                            for (int i=0;i<year_month_arrList.size();i++){
                                year_month_arrList.get(i).setChecked(false);
                            }
                        }
                        else {
                            year_month_arrList.get(position).setChecked(false);
                        }
                        notifyDataSetChanged();
                    }
                }
            });

//            holder.blank_checkbox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (year_month_arrList.get(position).getPosition()==0){
//                        for (int i=0;i<year_month_arrList.size();i++){
//                            year_month_arrList.get(i).setChecked(true);
//                        }
//                    }
//                    else {
//                        year_month_arrList.get(position).setChecked(true);
//                    }
//                    notifyDataSetChanged();
//                }
//            });
//
//            holder.tick_checkbox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (year_month_arrList.get(position).getPosition()==0){
//                        for (int i=0;i<year_month_arrList.size();i++){
//                            year_month_arrList.get(i).setChecked(false);
//                        }
//                    }
//                    else {
//                        year_month_arrList.get(position).setChecked(false);
//                    }
//                    notifyDataSetChanged();
//                }
//            });

            //checkbox_selected_listener.oncheckBoxSelected(year_month_arrList,type);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return year_month_arrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCheckBox checkbox_yr;
        TextView year_textV;
        ImageView blank_checkbox,tick_checkbox;
        LinearLayout check_ll;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            checkbox_yr=itemView.findViewById(R.id.checkbox_yr);
            year_textV=itemView.findViewById(R.id.year_textV);
            blank_checkbox=itemView.findViewById(R.id.blank_checkbox);
            tick_checkbox=itemView.findViewById(R.id.tick_checkbox);
            check_ll=itemView.findViewById(R.id.check_ll);

        }
    }
}
