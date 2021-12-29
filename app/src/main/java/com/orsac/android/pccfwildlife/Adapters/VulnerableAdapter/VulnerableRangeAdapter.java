package com.orsac.android.pccfwildlife.Adapters.VulnerableAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableRangeResponse;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

public class VulnerableRangeAdapter extends RecyclerView.Adapter<VulnerableRangeAdapter.ViewHolder> {

    Context context;
    ArrayList<VulnerableRangeResponse> vulnerableDataModels;

    public VulnerableRangeAdapter(Context context, ArrayList<VulnerableRangeResponse> vulnerableDataModels) {
        this.context = context;
        this.vulnerableDataModels = vulnerableDataModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vulnerable_div_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  VulnerableRangeAdapter.ViewHolder holder, int position) {

        try {
            holder.division_txt.setText("Range : ");
            holder.div_nm.setText(vulnerableDataModels.get(position).getRangeName());
            holder.div_count.setText("" + vulnerableDataModels.get(position).getCount());

            holder.arrow_img.setVisibility(View.GONE);
            holder.round_txt.setVisibility(View.GONE);
            holder.vulnerable_range_recyclerV.setVisibility(View.VISIBLE);

            holder.side_color_txt.setBackgroundColor(context.getResources().getColor(R.color.incident_color));


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int size=0;
        size=vulnerableDataModels.size();
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView vulnerable_other_details_recyclerV,vulnerable_range_recyclerV;
        TextView div_nm,div_count,division_txt,count_txt,round_txt,side_color_txt;
        CardView vulnerable_item_cv;
        ImageView arrow_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            vulnerable_other_details_recyclerV=itemView.findViewById(R.id.vulnerable_other_details_recyclerV);
            vulnerable_range_recyclerV=itemView.findViewById(R.id.vulnerable_range_recyclerV);
            div_nm=itemView.findViewById(R.id.div_nm);
            div_count=itemView.findViewById(R.id.div_count);
            division_txt=itemView.findViewById(R.id.division_txt);
            count_txt=itemView.findViewById(R.id.count_txt);
            round_txt=itemView.findViewById(R.id.round_txt);
            side_color_txt=itemView.findViewById(R.id.side_color_txt);
            vulnerable_item_cv=itemView.findViewById(R.id.vulnerable_item_cv);
            arrow_img=itemView.findViewById(R.id.arrow_img);

        }
    }
}

