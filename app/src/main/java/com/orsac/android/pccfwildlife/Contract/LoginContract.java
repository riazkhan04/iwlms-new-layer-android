package com.orsac.android.pccfwildlife.Contract;


import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;

public interface LoginContract {

    interface view{

        void show_progress();
        void hide_progress();
        void success(String message);
        void failed(String message);
    }
    interface presenter{
        void load_login(String username, String passwor, RetrofitInterface apiInterface);

    }
}
