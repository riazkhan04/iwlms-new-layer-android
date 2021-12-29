package com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orsac.android.pccfwildlife.Contract.DashboardContract;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Fragments.GajaBandhuItemFragment;
import com.orsac.android.pccfwildlife.Presenter.DashboardPresenter;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class GajaBandhuItemActivity extends AppCompatActivity implements DashboardContract.view {

    Toolbar toolbar;
    TextView toolbar_txt,submit_txt,english_txt,odia_txt;
    ImageView back_img,profile_img,language_img,close_img,englishtxt_tick,oditxt_tick;
    PopupMenu popup;
    DashboardContract.presenter dashboard_presenter;
    SessionManager session;
    String languageSelect="",userId="";
    Dialog language_dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bana_sathi_lang_selection_page);


        initData();

        clickFunction();

    }


    private void initData() {
        try {

            toolbar=findViewById(R.id.toolbar_new_id);
            back_img=toolbar.findViewById(R.id.back_img);
            profile_img=toolbar.findViewById(R.id.profile_img);
            language_img=toolbar.findViewById(R.id.language_img);
            toolbar_txt=toolbar.findViewById(R.id.toolbar_txt);
            dashboard_presenter=new DashboardPresenter(GajaBandhuItemActivity.this,this,GajaBandhuItemActivity.this);
            session = new SessionManager(GajaBandhuItemActivity.this);
            toolbar_txt.setText("Gaja Information");

            language_img.setVisibility(View.VISIBLE);

            languageSelect=session.getLanguageSelect();
            userId=session.getGajaBandhu_UserId();

//            setLocale(languageSelect);

            replaceFragment(0,new GajaBandhuItemFragment(),"Banasathi_Frag");

//            profile_img.setVisibility(View.GONE);

            profile_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    popup = new PopupMenu(GajaBandhuItemActivity.this, profile_img);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.profile_gajabandhu_menu, popup.getMenu());
//                setForceShowIcon(popup);
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.nav_profile:

                                    Intent profile_intent=new Intent(GajaBandhuItemActivity.this, ProfileGajaBandhuActivity.class);
                                    startActivity(profile_intent);

                                    break;

                                case R.id.nav_logout:
//                                call_logout(Dashboard_nw.this,"Do you want to logout the app ?","Logout");
                                    dashboard_presenter.call_logout(GajaBandhuItemActivity.this,getResources().getString(R.string.logout_msg),
                                            "Logout",session);
                                    break;

                                default:
                                    break;
                            }
                            return true;

                        }

                    });
                }
            });

            language_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    call_languageDialog(GajaBandhuItemActivity.this);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clickFunction() {
        try {

            back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onBackPressed();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void show_progress() {

    }

    @Override
    public void hide_progress() {

    }

    @Override
    public void success(String message) {

    }

    @Override
    public void failed(String message) {

    }

    //update setLocale code
    public  void setLocale(String lang) {
        try {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void replaceFragment(int itemId, Fragment fragmentt, String tag) {
        try {
            //initializing the fragment object which is selected
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.middle_ll, fragmentt, tag);
            if (itemId == 1) {
                ft.addToBackStack("" + itemId);
            }
            ft.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void call_languageDialog(Context context){
        try {
            language_dialog=new Dialog(context);
            language_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            language_dialog.setCancelable(false);
            language_dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
            language_dialog.setContentView(R.layout.language_dialog_layout);


            submit_txt=language_dialog.findViewById(R.id.submit_txt);
            english_txt=language_dialog.findViewById(R.id.english_txt);
            odia_txt=language_dialog.findViewById(R.id.odia_txt);
            close_img=language_dialog.findViewById(R.id.close_list_img);
            englishtxt_tick=language_dialog.findViewById(R.id.englishtxt_tick);
            oditxt_tick=language_dialog.findViewById(R.id.oditxt_tick);


            odia_txt.setText(getResources().getString(R.string.odia));
//            languageSelect="en";

            if (session.getLanguageSelect().equalsIgnoreCase("en")||
                    session.getLanguageSelect().equalsIgnoreCase("")){
                englishtxt_tick.setVisibility(View.VISIBLE);
                oditxt_tick.setVisibility(View.INVISIBLE);
            }else {
                englishtxt_tick.setVisibility(View.INVISIBLE);
                oditxt_tick.setVisibility(View.VISIBLE);
            }

            english_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    languageSelect="en";

                    if (englishtxt_tick.getVisibility()==View.VISIBLE){
                        englishtxt_tick.setVisibility(View.INVISIBLE);
                        oditxt_tick.setVisibility(View.VISIBLE);
                    }
                    else {
                        englishtxt_tick.setVisibility(View.VISIBLE);
                        oditxt_tick.setVisibility(View.INVISIBLE);
                    }
                }
            });
            odia_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    languageSelect="or_";
                    if (oditxt_tick.getVisibility()==View.VISIBLE){
                        oditxt_tick.setVisibility(View.INVISIBLE);
                        englishtxt_tick.setVisibility(View.VISIBLE);
                    }
                    else {
                        oditxt_tick.setVisibility(View.VISIBLE);
                        englishtxt_tick.setVisibility(View.INVISIBLE);
                    }
                }
            });

            submit_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (oditxt_tick.getVisibility()==View.VISIBLE){
                        languageSelect="or";
                    }else {
                        languageSelect="en";
                    }
                    if (languageSelect.equalsIgnoreCase("")){

                        Toast.makeText(context, "Please select language !", Toast.LENGTH_SHORT).show();
                    }else {
                        language_dialog.dismiss();
                        session.setLanguage(languageSelect);
                        setLocale(languageSelect);

                        Intent intent=new Intent(context, GajaBandhuItemActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
            });


            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language_dialog.dismiss();
                }
            });


            Window window = language_dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            language_dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
