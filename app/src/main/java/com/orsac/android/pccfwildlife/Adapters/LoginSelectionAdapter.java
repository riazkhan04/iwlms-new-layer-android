package com.orsac.android.pccfwildlife.Adapters;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.orsac.android.pccfwildlife.Fragments.LoginFragment;
import com.orsac.android.pccfwildlife.Fragments.NewLoginOtpFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginSelectionAdapter extends FragmentPagerAdapter {

    LinearLayout progress_bar_LL;
    TextView progress_txt;

    public LoginSelectionAdapter(FragmentManager fm, LinearLayout progress_bar_LL,TextView progress_txt) {
        super(fm);
        this.progress_bar_LL=progress_bar_LL;
        this.progress_txt=progress_txt;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        try {
            if (position == 0)
            {
                fragment = new LoginFragment(progress_bar_LL,progress_txt);
            }
            else if (position == 1)
            {
                fragment = new NewLoginOtpFragment(progress_bar_LL,progress_txt);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        try {

            if (position == 0)
            {
                title = "IWLMS Login";
            }
            else if (position == 1)
            {
                title = "Gaja Bandhu";
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return title;
    }


}
