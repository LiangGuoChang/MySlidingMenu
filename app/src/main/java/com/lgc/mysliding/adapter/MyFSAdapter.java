package com.lgc.mysliding.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

public class MyFSAdapter extends FragmentStatePagerAdapter{

    private final static String TAG="MyFSAdapter";

    private List<Fragment> fragmentList;
//    private String[] titles;

    public MyFSAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return this.fragmentList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment=null;
        try {
            fragment= (Fragment) super.instantiateItem(container, position);
        }catch (Exception e){
            Log.d(TAG,"MyFSAdapter--instantiateItem"+e.getMessage());
        }

        return fragment;
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    //设置每个fragment的title
//    public void setTitles(String[] titles){
//
//    }
}
