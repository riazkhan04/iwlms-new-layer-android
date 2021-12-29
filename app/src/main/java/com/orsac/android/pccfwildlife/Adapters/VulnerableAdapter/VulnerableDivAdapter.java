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

import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableDivResponse;
import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableRangeResponse;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

public class VulnerableDivAdapter extends RecyclerView.Adapter<VulnerableDivAdapter.ViewHolder> {

    Context context;
    ArrayList<VulnerableDivResponse> vulnerableDataModels;
    CallItemClickListener callItemClickListener;

    public interface CallItemClickListener{

        void onItemDivClick(RecyclerView recyclerView,
                         int position,ArrayList<VulnerableRangeResponse> vulnerableDataModels);

    }

    public VulnerableDivAdapter(Context context, ArrayList<VulnerableDivResponse> vulnerableDataModels,
                                 CallItemClickListener callItemClickListener) {
        this.context = context;
        this.vulnerableDataModels = vulnerableDataModels;
        this.callItemClickListener = callItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vulnerable_div_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  VulnerableDivAdapter.ViewHolder holder, int position) {

        try {
                holder.division_txt.setText("Division : ");
                holder.div_nm.setText(vulnerableDataModels.get(position).getDivisionName());
                holder.div_count.setText("" + vulnerableDataModels.get(position).getCount());

                holder.arrow_img.setVisibility(View.VISIBLE);
                holder.vulnerable_division_recyclerV.setVisibility(View.VISIBLE);

                holder.vulnerable_item_cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.vulnerable_range_recyclerV.getVisibility() == View.VISIBLE) {
                            holder.vulnerable_range_recyclerV.setVisibility(View.GONE);
                            holder.arrow_img.setImageResource(R.drawable.arrow_down);
                        } else {
                            holder.vulnerable_range_recyclerV.setVisibility(View.VISIBLE);
                            holder.arrow_img.setImageResource(R.drawable.up_arrow_img);

                            callItemClickListener.onItemDivClick(holder.vulnerable_range_recyclerV,
                                    position, vulnerableDataModels.get(position).getRangeArr());

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

        RecyclerView vulnerable_division_recyclerV,vulnerable_range_recyclerV;
        TextView div_nm,div_count,division_txt,count_txt,round_txt,side_color_txt;
        CardView vulnerable_item_cv;
        ImageView arrow_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            vulnerable_division_recyclerV=itemView.findViewById(R.id.vulnerable_other_details_recyclerV);
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
