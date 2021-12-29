package com.orsac.android.pccfwildlife.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.orsac.android.pccfwildlife.Model.IncidentCheckboxModel.CheckBoxItemModel;
import com.orsac.android.pccfwildlife.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

public class IncidentRecyclerAdapter extends RecyclerView.Adapter<IncidentRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<CheckBoxItemModel> incidentArr;
    LinearLayout other_animalLL;
    OnCheckbox_Selected_listener listener;
    String inputNo="0",noOfCasualty="0";

    public interface OnCheckbox_Selected_listener{
        void oncheckBoxSelected(ArrayList<CheckBoxItemModel> checkBoxItemModel);
        void createCheckBoxSelected(ArrayList<CheckBoxItemModel> checkBoxItemModels,RecyclerView recyclerView,int noOfCasuality);
//        void oncheckBoxSelected(ArrayList<String> checkbox_nm, ArrayList<String> checkbox_value,boolean isChecked,int position);
    }

    public IncidentRecyclerAdapter(Context context, ArrayList<CheckBoxItemModel> incidentArr, LinearLayout other_animal_ll,OnCheckbox_Selected_listener listener) {
        this.context = context;
        this.incidentArr = incidentArr;
        this.other_animalLL=other_animal_ll;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.incident_type_item_layout,parent,false);
        return new IncidentRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.checkbox.setText(incidentArr.get(position).getCheck_box_item_nm());

//        if (incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Farm Land") ||
//                incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Forest Land")
//        ){
//
//            holder.death_til.setHint("Acres");
//        }
//        else
//            if (incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Human Injury") ||
//                incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("House Damage")||
//                incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Elephant injured/sick")||
//                incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Elephant found in pit")||
//                incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Elephant on Road")
//        ){
//
//            holder.death_til.setHint("No.");
//        }
        if (incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Crop")){
            holder.death_til.setHint("Acres");//Quantity
        }else {
            holder.death_til.setHint("Number");
        }

//        else {
//            holder.death_til.setHint("Death");
//        }
        if (incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Other")){
            holder.death_til.setVisibility(View.VISIBLE);
            other_animalLL.setVisibility(View.GONE);
//            holder.death_til.setVisibility(View.GONE);
//            other_animalLL.setVisibility(View.GONE);
        }
        else {
            holder.death_til.setVisibility(View.VISIBLE);
            other_animalLL.setVisibility(View.GONE);
        }
        if (incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Animal Casualty")){

            holder.top_ll.setVisibility(View.GONE);
            holder.add_casualty_ll.setVisibility(View.VISIBLE);
        }

        holder.add_casualty_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, noOfCasualty, Toast.LENGTH_SHORT).show();

//                for (int i=0;i<Integer.parseInt(noOfCasualty);i++){

                    listener.createCheckBoxSelected(incidentArr,holder.add_recyclerV,Integer.parseInt(noOfCasualty));
//                }

//                if (holder.multiple_add_ll.getVisibility()==View.GONE) {
//
//                    holder.multiple_add_ll.setVisibility(View.VISIBLE);
//                    holder.top_ll.setVisibility(View.GONE);
//                }
//                else {
////                    holder.multiple_add_ll.setVisibility(View.GONE);
//                    holder.multiple_add_ll.setVisibility(View.VISIBLE);
//                    holder.multiple_add_ll1.setVisibility(View.VISIBLE);
//                    holder.top_ll.setVisibility(View.GONE);
//                }
//
            }
        });


        holder.checkbox.setChecked(incidentArr.get(position).isChecked());

        holder.death_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    if (charSequence.equals("")||charSequence.equals("0")){

                    }
                    else {
                        inputNo=holder.death_txt.getText().toString();

                        if (holder.checkbox.isChecked()){
                            incidentArr.get(position).setChecked(true);
                            if (holder.death_txt.getText().toString().trim().equalsIgnoreCase("")){
                                incidentArr.get(position).setCheckbox_item_value("0");
                            }else {
                                incidentArr.get(position).setCheckbox_item_value(inputNo);
                                if (incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Other")){
                                    incidentArr.get(position).setCheck_box_item_nm(holder.other_txt.getText().toString().trim());
                                }
//                            incidentArr.get(position).setCheckbox_item_value(holder.death_txt.getText().toString().trim());
                            }

                        }else {
                            incidentArr.get(position).setChecked(false);
                        }
//                    notifyDataSetChanged();
                        listener.oncheckBoxSelected(incidentArr);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.other_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {

                    if (charSequence.toString().length()>1){

                    }
                    else {
                        if (holder.checkbox.isChecked()) {
                            incidentArr.get(position).setChecked(true);

                            if (holder.other_txt.getText().toString().trim().equalsIgnoreCase("Other")){
                                incidentArr.get(position).setCheck_box_item_nm("Other");
                            }else {
                                incidentArr.get(position).setCheck_box_item_nm(holder.other_txt.getText().toString().trim());
                            }
                        }
                        else {
                            incidentArr.get(position).setChecked(false);
                        }
                        listener.oncheckBoxSelected(incidentArr);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (holder.other_txt.getText().toString().trim().equalsIgnoreCase("Other")){
                    incidentArr.get(position).setCheck_box_item_nm("Other");
                }else {
                    incidentArr.get(position).setCheck_box_item_nm(holder.other_txt.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                 if (isChecked){

                     if (incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Other")||
                             incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("")){
                         holder.other_til.setVisibility(View.INVISIBLE);
                         holder.other_txt.requestFocus();
//                         incidentArr.get(position).setCheck_box_item_nm(holder.other_txt.getText().toString().trim());
                     }else {
                         holder.other_til.setVisibility(View.INVISIBLE);
                         holder.death_txt.requestFocus();
                     }
                     incidentArr.get(position).setChecked(true);
                     if (holder.death_txt.getText().toString().trim().equalsIgnoreCase("")) {
                         incidentArr.get(position).setCheckbox_item_value("0");
                     } else {
                         incidentArr.get(position).setCheckbox_item_value(inputNo);
//                            incidentArr.get(position).setCheckbox_item_value(holder.death_txt.getText().toString().trim());
                     }

                    }else {
                     if (incidentArr.get(position).getCheck_box_item_nm().equalsIgnoreCase("Other")){
                         holder.other_til.setVisibility(View.INVISIBLE);
                         holder.other_txt.clearFocus();
                         holder.other_txt.setText("");
                     }
                     else {
                         holder.death_txt.setText("");
                         holder.death_txt.clearFocus();

                         holder.other_til.setVisibility(View.INVISIBLE);
                         holder.other_txt.clearFocus();
                         holder.other_txt.setText("");
                     }
                     incidentArr.get(position).setChecked(false);

                    }
//                    notifyDataSetChanged();
                    listener.oncheckBoxSelected(incidentArr);

            }
        });

        holder.checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    incidentArr.get(position).setChecked(true);
                    if (holder.desc_txt.getText().toString().trim().equalsIgnoreCase("")) {
                        incidentArr.get(position).setCheckbox_item_value("0");
                    } else {
                        incidentArr.get(position).setCheckbox_item_value(holder.value_txt.getText().toString().trim());
//                            incidentArr.get(position).setCheckbox_item_value(holder.death_txt.getText().toString().trim());
                    }
                }
                else {
                    incidentArr.get(position).setChecked(false);

                }
                listener.oncheckBoxSelected(incidentArr);
            }
        });

        holder.checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    incidentArr.get(position).setChecked(true);
                    if (holder.desc_txt1.getText().toString().trim().equalsIgnoreCase("")) {
                        incidentArr.get(position).setCheckbox_item_value("0");
                    } else {
                        incidentArr.get(position).setCheckbox_item_value(holder.value_txt.getText().toString().trim());
//                            incidentArr.get(position).setCheckbox_item_value(holder.death_txt.getText().toString().trim());
                    }
                }
                else {
                    incidentArr.get(position).setChecked(false);

                }
                listener.oncheckBoxSelected(incidentArr);
            }
        });

        holder.cancel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.multiple_add_ll.getVisibility()==View.VISIBLE){
                    holder.desc_txt.setText("");
                    holder.value_txt.setText("");
                    holder.desc_txt.requestFocus();
                    holder.checkbox1.setChecked(false);
                    holder.multiple_add_ll.setVisibility(View.GONE);
                }else {


                }

            }
        });
        holder.cancel_img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.multiple_add_ll1.getVisibility()==View.VISIBLE){
                    holder.desc_txt1.setText("");
                    holder.value_txt1.setText("");
                    holder.desc_txt1.requestFocus();
                    holder.checkbox2.setChecked(false);
                    holder.multiple_add_ll1.setVisibility(View.GONE);
                }else {

                }
            }
        });

        holder.noOf_casualty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().equalsIgnoreCase("")||charSequence.toString().equalsIgnoreCase("0")){

                }
                else if (charSequence.toString().equalsIgnoreCase("1")){

                    noOfCasualty="1";

                }else if (charSequence.toString().equalsIgnoreCase("2")){
                    noOfCasualty="2";

                }else if (charSequence.toString().equalsIgnoreCase("3")){
                    noOfCasualty="3";

                }else if (charSequence.toString().equalsIgnoreCase("4")){
                    noOfCasualty="4";

                }else if (charSequence.toString().equalsIgnoreCase("5")){
                    noOfCasualty="5";
                }else {
                    noOfCasualty="0";
                    Toast.makeText(context, "Please type a valid animal no of casualty !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return incidentArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatCheckBox checkbox,checkbox1,checkbox2;
        TextInputLayout death_til,other_til;
        TextInputEditText death_txt,other_txt,desc_txt,value_txt,value_txt1,desc_txt1;
        LinearLayout add_casualty_ll,multiple_add_ll,top_ll,multiple_add_ll1;
        ImageView cancel_img,cancel_img1;
        EditText noOf_casualty;
        RecyclerView add_recyclerV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox=itemView.findViewById(R.id.checkbox);
            checkbox1=itemView.findViewById(R.id.checkbox1);
            checkbox2=itemView.findViewById(R.id.checkbox2);
            death_til=itemView.findViewById(R.id.death_til);
            other_til=itemView.findViewById(R.id.other_til);
            other_txt=itemView.findViewById(R.id.other_txt);
            death_txt=itemView.findViewById(R.id.death_txt);
            top_ll=itemView.findViewById(R.id.top_ll);
            add_casualty_ll=itemView.findViewById(R.id.add_casualty_ll);
            multiple_add_ll=itemView.findViewById(R.id.multiple_add_ll);
            desc_txt=itemView.findViewById(R.id.desc_txt);
            value_txt=itemView.findViewById(R.id.value_txt);
            cancel_img=itemView.findViewById(R.id.cancel_img);
            cancel_img1=itemView.findViewById(R.id.cancel_img1);
            value_txt1=itemView.findViewById(R.id.value_txt1);
            desc_txt1=itemView.findViewById(R.id.desc_txt1);
            multiple_add_ll1=itemView.findViewById(R.id.multiple_add_ll1);
            noOf_casualty=itemView.findViewById(R.id.noOf_casualty);
            add_recyclerV=itemView.findViewById(R.id.add_recyclerV);

        }
    }
}
