package com.lgc.mysliding.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lgc.mysliding.R;

public class LoginFragment extends Fragment {

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null==mView){
            mView = inflater.inflate(R.layout.fragment_login, container, false);
        }
        return mView;
    }

}
