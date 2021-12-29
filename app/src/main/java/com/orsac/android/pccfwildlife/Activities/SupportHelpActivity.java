package com.orsac.android.pccfwildlife.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.orsac.android.pccfwildlife.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SupportHelpActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView toolbar_back_img,refresh_img,toolbar_profile_img;
    TextView toolbar_txt;
    AppCompatSpinner issueType_spinner;
    EditText remark_edit,other_issue_edit;
    Button submit_btn;
    ConstraintLayout main_cl;
    String issue_typeValue="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_help_activity);
        try {

            initData();
            
            clickFunction();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            toolbar = findViewById(R.id.toolbar_new_id);
            toolbar_profile_img = toolbar.findViewById(R.id.profile_img);
            toolbar_back_img = toolbar.findViewById(R.id.back_img);
            refresh_img = toolbar.findViewById(R.id.refresh_img);
            toolbar_txt = toolbar.findViewById(R.id.toolbar_txt);
            issueType_spinner = findViewById(R.id.issueType);
            remark_edit = findViewById(R.id.remark_edit);
            other_issue_edit = findViewById(R.id.other_issue_edit);
            submit_btn = findViewById(R.id.submit_btn);
            main_cl = findViewById(R.id.main_cl);


            toolbar_profile_img.setVisibility(View.GONE);
            toolbar_back_img.setVisibility(View.VISIBLE);
            toolbar_txt.setText("Support");


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void clickFunction() {
        try {

            toolbar_back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    finish();
                }
            });


            issueType_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    issue_typeValue = (String) parent.getItemAtPosition(position);
                    Toast.makeText(SupportHelpActivity.this, issue_typeValue, Toast.LENGTH_SHORT).show();
                    if (issue_typeValue.equals("Others")) {
                        other_issue_edit.setVisibility(View.VISIBLE);
                    } else {
                        other_issue_edit.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (remark_edit.getText().toString().trim().equalsIgnoreCase("")){

                        Snackbar.make(main_cl,"Please enter remark",Snackbar.LENGTH_SHORT).show();
                    }else {

                    }
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
