package com.lgc.mysliding.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lgc.mysliding.R;
import com.lgc.mysliding.fragment.AboutFragment;
import com.lgc.mysliding.fragment.LoginFragment;

public class EnterActivity extends Activity {

    private static final String TAG="EnterActivity";

    private String enter;
    private int item=1;
    private FragmentManager fragmentManager;
    private LoginFragment loginFragment;
    private AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        Intent intent=this.getIntent();
        savedInstanceState=intent.getExtras();
        item = savedInstanceState.getInt("item");
        Log.d(TAG,"EnterActivity--item--"+ item);

        fragmentManager = getFragmentManager();

        selectFragment(item);
    }

    //选择fragment 1代表LoginFragment 2代表AboutFragment
    private void selectFragment(int item){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        //先将所有的Fragment隐藏
        hideFragments(transaction);
        switch (item){
            case 1:
                if (loginFragment==null){
                    loginFragment=new LoginFragment();
                    transaction.add(R.id.enter_frame_id,loginFragment);
                }else {
                    transaction.show(loginFragment);
                }
                break;
            case 2:
                if (aboutFragment==null){
                    aboutFragment=new AboutFragment();
                    transaction.add(R.id.enter_frame_id,aboutFragment);
                }else {
                    transaction.show(aboutFragment);
                }
                break;
        }

        transaction.commit();
    }

    //隐藏所有的Fragment
    private void hideFragments(FragmentTransaction transaction){
        if (null != loginFragment) {
            transaction.hide(loginFragment);
        }
        if (null!=aboutFragment){
            transaction.hide(aboutFragment);
        }
    }
}
