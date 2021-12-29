package com.orsac.android.pccfwildlife.Presenter;

import android.app.Activity;
import android.content.Context;

import com.orsac.android.pccfwildlife.Activities.Dashboard_nw;
import com.orsac.android.pccfwildlife.Contract.UpdateReportContract;

public class UpdateReport_Presenter implements UpdateReportContract.presenter {

    Context context;
    UpdateReportContract.view update_view;
    Activity activity;

    public UpdateReport_Presenter(Context context, UpdateReportContract.view update_view) {
        this.context = context;
        this.update_view = update_view;
        activity=(Dashboard_nw)this.context;
    }

    @Override
    public void updateReport() {

    }
}
