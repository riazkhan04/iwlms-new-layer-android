package com.orsac.android.pccfwildlife.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.orsac.android.pccfwildlife.Adapters.MapViewPagerAdapter;
import com.orsac.android.pccfwildlife.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class MapSelectionFragment extends Fragment {

    Context context;
    TabLayout map_tablayout;
    ViewPager map_viewPager;
    MapViewPagerAdapter mapViewPagerAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.map_tab_layout,container,false);

        initData(view);

        callAdapter();

        return view;
    }

    private void initData(View view) {
        try {
            map_tablayout=view.findViewById(R.id.map_tabs);
            map_viewPager=view.findViewById(R.id.map_viewPager);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void callAdapter(){
        try {

            mapViewPagerAdapter = new MapViewPagerAdapter(getChildFragmentManager());
            map_viewPager.setAdapter(mapViewPagerAdapter);
            map_tablayout.setupWithViewPager(map_viewPager);


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
