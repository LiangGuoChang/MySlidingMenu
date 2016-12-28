package com.lgc.mysliding.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lgc.mysliding.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final int LOGIN_RESULT=201;

    private View mView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button btn_login;
    private View btn_logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null==mView){
            mView = inflater.inflate(R.layout.fragment_login, container, false);
            preferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
            editor = preferences.edit();
            initView();
        }
        return mView;
    }

    private void initView(){
       btn_login=(Button) mView.findViewById(R.id.btn_login);
        btn_logout = mView.findViewById(R.id.btn_logout);

        btn_login.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                editor.putBoolean("isLogin",true);
                editor.commit();
                Intent login=new Intent();
                login.putExtra("login", true);
                getActivity().setResult(LOGIN_RESULT,login);
                getActivity().finish();
                break;

            case R.id.btn_logout:
                editor.putBoolean("isLogin",false);
                editor.commit();
                Intent logout=new Intent();
                logout.putExtra("login", false);
                getActivity().setResult(LOGIN_RESULT,logout);
                getActivity().finish();
                break;
        }
    }
}
