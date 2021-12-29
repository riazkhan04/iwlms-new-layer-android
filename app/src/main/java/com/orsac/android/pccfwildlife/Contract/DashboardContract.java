package com.orsac.android.pccfwildlife.Contract;

import android.content.Context;

import com.orsac.android.pccfwildlife.SharedPref.SessionManager;

public interface DashboardContract {

    interface view{
        void show_progress();
        void hide_progress();
        void success(String message);
        void failed(String message);
    }
    interface presenter{
        void load_Update(String checkInternet_status, Context context);
        void call_logout(Context context, String msg, String dialog_title, SessionManager session);

    }
}
