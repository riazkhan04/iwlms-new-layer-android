package com.orsac.android.pccfwildlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orsac.android.pccfwildlife.Model.SelectionReportModel;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SelectionReportAdapter extends RecyclerView.Adapter<SelectionReportAdapter.ViewHolder> {

    Context context;
    ArrayList<SelectionReportModel> itemModels_arr;
    OnItemSelection onItemSelection;
    SessionManager session;

    public interface OnItemSelection{
        void onItem_selected(String item_name,int position,CardView cview);
    }

    public SelectionReportAdapter(Context context, ArrayList<SelectionReportModel> itemModels_arr,OnItemSelection onItemSelection) {
        this.context = context;
        this.itemModels_arr = itemModels_arr;
        this.onItemSelection=onItemSelection;
    }

    @NonNull
    @Override
    public SelectionReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.selection_item_page,parent,false);

        return new SelectionReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        session=new SessionManager(context);

        Glide.with(context)
                .load(itemModels_arr.get(position).getItem_image())
                .into(holder.selection_img);

        holder.selection_txt.setText(itemModels_arr.get(position).get_item_name());

        if (position==0){

            if (session.getRoleId().equalsIgnoreCase("1")||session.getRoleId().equalsIgnoreCase("2")||
                    session.getRoleId().equalsIgnoreCase("3")
                    || session.getRoleId().equalsIgnoreCase("6")||
                    session.getRoleId().equalsIgnoreCase("default")){
                //for diable button
                holder.selection_CV.setCardBackgroundColor(context.getResources().getColor(R.color.tranparent_grey));
//                holder.selection_CV.setEnabled(false);
//                holder.selection_CV.setClickable(false);
            }
            else {
                Animation animation=AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                holder.selection_CV.startAnimation(animation);

            }
            holder.selection_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemSelection.onItem_selected(itemModels_arr.get(position).get_item_name(),
                            position,holder.selection_CV);
                }
            });


        }
        else {

            if (session.getRoleId().equalsIgnoreCase("4") || session.getRoleId().equalsIgnoreCase("5")||
                    session.getRoleId().equalsIgnoreCase("7") || session.getRoleId().equalsIgnoreCase("8") ||
                    session.getRoleId().equalsIgnoreCase("9")||
                    session.getRoleId().equalsIgnoreCase("default")){

                //for diable button
            holder.selection_CV.setCardBackgroundColor(context.getResources().getColor(R.color.tranparent_grey));
//            holder.selection_CV.setEnabled(false);
//            holder.selection_CV.setClickable(false);
            }
            else {
                Animation animation=AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
                holder.selection_CV.startAnimation(animation);

            }

            //for diable button
//            holder.selection_CV.setCardBackgroundColor(context.getResources().getColor(R.color.tranparent_grey));
//            holder.selection_CV.setEnabled(false);
//            holder.selection_CV.setClickable(false);
            //-------------------------------------------

//            Animation animation=AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim);
//            holder.selection_CV.startAnimation(animation);

            holder.selection_CV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemSelection.onItem_selected(itemModels_arr.get(position).get_item_name(),
                            position,holder.selection_CV);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return itemModels_arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView selection_img;
        TextView selection_txt;
        CardView selection_CV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selection_img=itemView.findViewById(R.id.selection_img);
            selection_txt=itemView.findViewById(R.id.selection_txt);
            selection_CV=itemView.findViewById(R.id.selection_CV);
        }
    }
}
