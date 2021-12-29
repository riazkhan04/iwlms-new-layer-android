package com.orsac.android.pccfwildlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orsac.android.pccfwildlife.Model.ReportCountItemModel;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ReportCountAdapter extends RecyclerView.Adapter<ReportCountAdapter.ViewHolder> {

    Context context;
    ArrayList<ReportCountItemModel> itemModels_arr;
    onReportCountClickListener countClickListener;

    public interface onReportCountClickListener{
        void oncount_click(int position,String count);
    }

    public ReportCountAdapter(Context context, ArrayList<ReportCountItemModel> itemModels_arr, onReportCountClickListener countClickListener) {
        this.context = context;
        this.itemModels_arr = itemModels_arr;
        this.countClickListener = countClickListener;
    }

    @Override
    public ReportCountAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.report_count_items,parent,false);
        return new ReportCountAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportCountAdapter.ViewHolder holder, int position) {
        try {

            if (position!=4 && position!=5){
                holder.count_txt.setText(itemModels_arr.get(position).getReportNo());

                holder.report_name.setText(itemModels_arr.get(position).getReportName());
                holder.count_CV.setVisibility(View.VISIBLE);
            }else {
                holder.count_txt.setVisibility(View.GONE);
                holder.report_name.setVisibility(View.GONE);
                holder.count_CV.setVisibility(View.GONE);
            }


            if (position==0){
                holder.count_CV.setCardBackgroundColor(context.getResources().getColor(R.color.textDark));
            }
            else if (position==1){
                holder.count_CV.setCardBackgroundColor(context.getResources().getColor(R.color.design_default_color_secondary_variant));
            }else if (position==2){
                holder.count_CV.setCardBackgroundColor(context.getResources().getColor(R.color.lightest_green));
            } else if (position==3){
                holder.count_CV.setCardBackgroundColor(context.getResources().getColor(R.color.blue));
            }else if (position==4){
                holder.count_CV.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }

            holder.count_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    countClickListener.oncount_click(position,itemModels_arr.get(position).getReportNo());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return itemModels_arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView count_txt,report_name;
        CardView count_CV;

        public ViewHolder(View itemView) {
            super(itemView);

            count_txt=itemView.findViewById(R.id.count_txt);
            report_name=itemView.findViewById(R.id.report_name);
            count_CV=itemView.findViewById(R.id.count_CV);

        }
    }
}

