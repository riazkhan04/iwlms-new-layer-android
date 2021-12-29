package com.orsac.android.pccfwildlife.Adapters.GajaBandhuAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orsac.android.pccfwildlife.Model.DashboardItemModel;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class GajaBandhuItemAdapter extends RecyclerView.Adapter<GajaBandhuItemAdapter.ViewHolder> {

    Context context;
    ArrayList<DashboardItemModel> itemModels_arr;
    int arr_size=4,sync_size=0;
    BanasathiItemClickListener banasathiItemClickListener;

    public interface BanasathiItemClickListener{
        void item_clickListener(CardView cardView, int pos, ImageView item_img, ImageView elephant_img, TextView sync_value);
    }

    public GajaBandhuItemAdapter(Context context, ArrayList<DashboardItemModel> itemModels_arr, int arr_size, BanasathiItemClickListener banasathiItemClickListener) {
        this.context = context;
        this.itemModels_arr = itemModels_arr;
        this.arr_size = arr_size;
        this.banasathiItemClickListener = banasathiItemClickListener;
    }

    public GajaBandhuItemAdapter(Context context, ArrayList<DashboardItemModel> itemModels_arr, int arr_size, int sync_size, BanasathiItemClickListener banasathiItemClickListener) {
        this.context = context;
        this.itemModels_arr = itemModels_arr;
        this.arr_size = arr_size;
        this.sync_size = sync_size;
        this.banasathiItemClickListener = banasathiItemClickListener;
    }

    @NonNull
    @Override
    public GajaBandhuItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_items,parent,false);

        return new GajaBandhuItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GajaBandhuItemAdapter.ViewHolder holder, int position) {
        try {
            holder.item_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    banasathiItemClickListener.item_clickListener(holder.item_cv,position,holder.item_img,
                            holder.elephant_img,holder.sync_value);

                }
            });
            Glide.with(context)
                    .load(itemModels_arr.get(position).getItem_image())
                    .into(holder.item_img);

            holder.item_name.setText(itemModels_arr.get(position).get_item_name());

            if (position==5){
                holder.sync_value.setVisibility(View.VISIBLE);

                Animation animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                holder.sync_value.startAnimation(animation);

                if (sync_size==0){
                    holder.sync_value.setText(context.getResources().getString(R.string.no_data_to_sync));

                    holder.sync_value.setTextColor(context.getResources().getColor(R.color.textDark));//Sync value

                }else {
                    holder.sync_value.setTextColor(context.getResources().getColor(R.color.lightest_green));//Sync value

                    holder.sync_value.setText(context.getResources().getString(R.string.pending_to_sync)+" - " + sync_size);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return itemModels_arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView item_cv;
        ImageView elephant_img,item_img;
        TextView item_name,sync_value;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_cv=itemView.findViewById(R.id.elephreport);
            elephant_img=itemView.findViewById(R.id.elephant_img);
            item_name=itemView.findViewById(R.id.item_name);
            item_img=itemView.findViewById(R.id.item_img);
            sync_value=itemView.findViewById(R.id.sync_value);
        }
    }
}
