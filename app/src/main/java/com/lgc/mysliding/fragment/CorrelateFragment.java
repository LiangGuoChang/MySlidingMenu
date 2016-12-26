package com.lgc.mysliding.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lgc.mysliding.R;

public class CorrelateFragment extends Fragment {

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView){
            mView = inflater.inflate(R.layout.fragment_correlate, container, false);
        }
        return mView;
    }

}
