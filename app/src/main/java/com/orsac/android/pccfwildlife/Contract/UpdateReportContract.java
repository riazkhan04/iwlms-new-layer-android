package com.orsac.android.pccfwildlife.Contract;

public interface UpdateReportContract {

    interface view{
        void show_progress();
        void hide_progress();
        void success(String message);
        void failed(String message);
    }
    interface presenter{
        void updateReport();
    }
}
