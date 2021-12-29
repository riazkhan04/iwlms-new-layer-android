package com.orsac.android.pccfwildlife.Contract;

public interface SplashContract {

    interface view{
        void show_progress();
        void hide_progress();
    }
    interface presenter{
        void loadSplash();
    }
}
