package com.orsac.android.pccfwildlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orsac.android.pccfwildlife.Model.DashboardItemModel;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardItemsRecycleAdapter extends RecyclerView.Adapter<DashboardItemsRecycleAdapter.ViewHolder> {

    Context context;
    ArrayList<DashboardItemModel> itemModels_arr;
    DashItemClickListener dashItemClickListener;
    int arr_size=4,sync_size=0;
    String guestFlag="";

    public interface DashItemClickListener{
        void item_clickListener(CardView cardView,int pos,ImageView item_img,ImageView elephant_img,TextView sync_value,
                                CardView directCV,LinearLayout reporting_ll,CardView indirectCV,CardView nilCV,CardView elephant_death_cv,
                                CardView other_incident);
    }

    public DashboardItemsRecycleAdapter(Context context, ArrayList<DashboardItemModel> itemModels_arr, DashItemClickListener dashItemClickListener) {
        this.context = context;
        this.itemModels_arr = itemModels_arr;
        this.dashItemClickListener = dashItemClickListener;
    }

    public DashboardItemsRecycleAdapter(Context context, ArrayList<DashboardItemModel> itemModels_arr, DashItemClickListener dashItemClickListener,
                                        int arr_size) {
        this.context = context;
        this.itemModels_arr = itemModels_arr;
        this.dashItemClickListener = dashItemClickListener;
        this.arr_size = arr_size;

    }
    public DashboardItemsRecycleAdapter(Context context, ArrayList<DashboardItemModel> itemModels_arr,
                                        DashItemClickListener dashItemClickListener, int arr_size,int sync_value,String guestFlag) {
        this.context = context;
        this.itemModels_arr = itemModels_arr;
        this.dashItemClickListener = dashItemClickListener;
        this.arr_size = arr_size;
        this.sync_size = sync_value;
        this.guestFlag = guestFlag;
    }

    @Override
    public DashboardItemsRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_items,parent,false);


        return new DashboardItemsRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DashboardItemsRecycleAdapter.ViewHolder holder, int position) {


            holder.elephreport_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dashItemClickListener.item_clickListener(holder.elephreport_cv,position,holder.item_img,
                                                             holder.elephant_img,holder.sync_value,holder.direct_cv,
                                                             holder.reporting_ll, holder.indirect_cv,holder.nil_cv,
                                                             holder.elephant_death_cv,holder.other_incident_cv);
                }
            });



        holder.item_name.setText(itemModels_arr.get(position).get_item_name());

        holder.elephant_img.setVisibility(View.VISIBLE);

//        if (position==0){
//            holder.elephant_img.setVisibility(View.VISIBLE);
//        }
//        else {
//            holder.elephant_img.setVisibility(View.INVISIBLE);
//        }

        Glide.with(context)
                .load(itemModels_arr.get(position).getItem_image())
                .into(holder.item_img);

        if (guestFlag.equalsIgnoreCase("guest")){

            //for guest
            if (position==2){
                holder.sync_value.setVisibility(View.VISIBLE);

                Animation animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                holder.sync_value.startAnimation(animation);

                if (sync_size==0){
                    holder.sync_value.setText("No data to sync");

                    holder.sync_value.setTextColor(context.getResources().getColor(R.color.textDark));//Sync value

                }else {
                    holder.sync_value.setTextColor(context.getResources().getColor(R.color.lightest_green));//Sync value

                    holder.sync_value.setText("Pending sync - " + sync_size);
                }
            }
        }else {
            //for login
            if (position==3){
                holder.sync_value.setVisibility(View.VISIBLE);

                Animation animation= AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                holder.sync_value.startAnimation(animation);

                if (sync_size==0){
                    holder.sync_value.setText("No data to sync");

                    holder.sync_value.setTextColor(context.getResources().getColor(R.color.textDark));//Sync value

                }else {
                    holder.sync_value.setTextColor(context.getResources().getColor(R.color.lightest_green));//Sync value

                    holder.sync_value.setText("Pending sync - " + sync_size);
                }
            }
        }





    }

    @Override
    public int getItemCount() {
        return itemModels_arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView elephreport_cv,direct_cv,nil_cv,indirect_cv,other_incident_cv,elephant_death_cv;
        ImageView elephant_img,item_img,direct_img,indirect_img;
        TextView item_name,sync_value,direct_report_item,indirect_report_item;
        LinearLayout reporting_ll;

        public ViewHolder(View itemView) {
            super(itemView);

            elephreport_cv=itemView.findViewById(R.id.elephreport);
            elephant_img=itemView.findViewById(R.id.elephant_img);
            item_img=itemView.findViewById(R.id.item_img);
            item_name=itemView.findViewById(R.id.item_name);
            sync_value=itemView.findViewById(R.id.sync_value);
            reporting_ll=itemView.findViewById(R.id.reporting_ll);
            direct_cv=itemView.findViewById(R.id.direct_cv);
            nil_cv=itemView.findViewById(R.id.nil_cv);
            indirect_cv=itemView.findViewById(R.id.indirect_cv);
            other_incident_cv=itemView.findViewById(R.id.other_incident_cv);
            elephant_death_cv=itemView.findViewById(R.id.elephant_death_cv);

        }
    }
}
