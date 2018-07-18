package com.lgc.mysliding.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lgc.mysliding.R;

/**
 * @author LGC
 */
public class GISFragment extends Fragment {


    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null){
            mView = inflater.inflate(R.layout.fragment_gis, container, false);
        }
        return mView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
