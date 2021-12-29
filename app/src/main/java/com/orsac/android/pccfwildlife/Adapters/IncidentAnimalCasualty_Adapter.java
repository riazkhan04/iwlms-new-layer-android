package com.orsac.android.pccfwildlife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.orsac.android.pccfwildlife.Model.IncidentCheckboxModel.AnimalIncidentItemModel;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

public class IncidentAnimalCasualty_Adapter extends RecyclerView.Adapter<IncidentAnimalCasualty_Adapter.ViewHolder> {

    Context context;
    ArrayList<AnimalIncidentItemModel> animal_incidentArr;
    OnItemAddListener listener;
    String desc="",value="";

    public interface OnItemAddListener{
        void onItemAdd(int position);

        void removeItem(int position);
    }

    public IncidentAnimalCasualty_Adapter(Context context, ArrayList<AnimalIncidentItemModel> animal_incidentArr, OnItemAddListener listener) {
        this.context = context;
        this.animal_incidentArr = animal_incidentArr;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.animal_casuality_item_ll,parent,false);
        return new IncidentAnimalCasualty_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {

            if (position==animal_incidentArr.size()-1){

                holder.add_img.setVisibility(View.VISIBLE);
                holder.cancel_img.setVisibility(View.GONE);
            }
            else {
                holder.add_img.setVisibility(View.GONE);
                holder.cancel_img.setVisibility(View.VISIBLE);
            }

            holder.cancel_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.removeItem(position);
//                    animal_incidentArr.remove(animal_incidentArr.get(position));
//                    notifyDataSetChanged();
                }
            });

            //It is to check the boolean and remove the position
            if (animal_incidentArr.get(position).isShow()){
                holder.desc_txt.setText(animal_incidentArr.get(position).getItem_nm());
                holder.value_txt.setText(animal_incidentArr.get(position).getItem_value());

                holder.multiple_add_ll.setVisibility(View.VISIBLE);

            }
            else {

                holder.multiple_add_ll.setVisibility(View.GONE);
            }

            holder.add_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (holder.desc_txt.getText().toString().equalsIgnoreCase("") ||
                            holder.value_txt.getText().toString().equalsIgnoreCase("")){
                        Toast.makeText(context, "Please add description and value !", Toast.LENGTH_SHORT).show();
                    }else {
                        desc=holder.desc_txt.getText().toString().trim();
                        value=holder.value_txt.getText().toString().trim();

                        animal_incidentArr.get(position).setItem_nm(desc);
                        animal_incidentArr.get(position).setItem_value(value);
                        listener.onItemAdd(position);
                    }

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return animal_incidentArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatCheckBox checkbox;
        TextInputEditText desc_txt,value_txt;
        ImageView add_img,cancel_img;
        LinearLayout multiple_add_ll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox=itemView.findViewById(R.id.checkbox);
            desc_txt=itemView.findViewById(R.id.desc_txt);
            value_txt=itemView.findViewById(R.id.value_txt);
            cancel_img=itemView.findViewById(R.id.cancel_img);
            add_img=itemView.findViewById(R.id.add_img);
            multiple_add_ll=itemView.findViewById(R.id.multiple_add_ll);
        }
    }
}
