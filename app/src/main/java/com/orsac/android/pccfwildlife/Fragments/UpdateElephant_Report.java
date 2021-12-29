package com.orsac.android.pccfwildlife.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textview.MaterialTextView;
import com.orsac.android.pccfwildlife.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UpdateElephant_Report extends Fragment {

    Context context;
    Button update;
    MaterialTextView dateof;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_elephant_report,container,false);

        initData(view);


        return view;
    }

    public void initData(View view) {
        update=view.findViewById(R.id.loc);
        dateof=view.findViewById(R.id.dateof);

    }


}
