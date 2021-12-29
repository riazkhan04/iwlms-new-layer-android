package com.orsac.android.pccfwildlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableCircleResponse;
import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableDivResponse;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

public class VulnerableItemAdapter extends RecyclerView.Adapter<VulnerableItemAdapter.ViewHolder> {

    Context context;
    ArrayList<VulnerableCircleResponse> vulnerableDataModels;
    CallItemClickListener callItemClickListener;

    public interface CallItemClickListener{

        void onItemClick(RecyclerView recyclerView,
                         int position,ArrayList<VulnerableDivResponse> vulnerableDataModels);
    }

    public VulnerableItemAdapter(Context context, ArrayList<VulnerableCircleResponse> vulnerableDataModels,
                                 CallItemClickListener callItemClickListener) {
        this.context = context;
        this.vulnerableDataModels = vulnerableDataModels;
        this.callItemClickListener = callItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vulnerable_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  VulnerableItemAdapter.ViewHolder holder, int position) {

        try {

                holder.division_txt.setText("Circle : ");
                holder.div_nm.setText(vulnerableDataModels.get(position).getCircleName());
                holder.div_count.setText(""+vulnerableDataModels.get(position).getCount());

                holder.arrow_img.setVisibility(View.VISIBLE);
                holder.round_txt.setVisibility(View.GONE);

                holder.vulnerable_item_cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.vulnerable_other_details_recyclerV.getVisibility()==View.VISIBLE){
                            holder.vulnerable_other_details_recyclerV.setVisibility(View.GONE);
                            holder.arrow_img.setImageResource(R.drawable.arrow_down);
                        }else {
                            holder.vulnerable_other_details_recyclerV.setVisibility(View.VISIBLE);
                            holder.arrow_img.setImageResource(R.drawable.up_arrow_img);

                            callItemClickListener.onItemClick(holder.vulnerable_other_details_recyclerV,
                                    position,vulnerableDataModels.get(position).getDivArr());

                        }

                    }
                });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return vulnerableDataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView vulnerable_other_details_recyclerV,vulnerable_range_recyclerV;
        TextView div_nm,div_count,division_txt,count_txt,round_txt;
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
            vulnerable_item_cv=itemView.findViewById(R.id.vulnerable_item_cv);
            arrow_img=itemView.findViewById(R.id.arrow_img);

        }
    }
}
