package com.lgc.mysliding.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.lgc.mysliding.R;

public class NeedleFragment extends Fragment {

    private static final String TAG="NeedleFragment";
    private View mView;
    private EditText et_mac;
    private ListView lv_detector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView==null){
            mView = inflater.inflate(R.layout.fragment_needle, container, false);
            initView();
        }
        Log.d(TAG,"onCreateView");
        return mView;
    }

    private void initView(){
        et_mac = (EditText)mView.findViewById(R.id.et_search);
        lv_detector = (ListView) mView.findViewById(R.id.lv_detector);
    }

    @Override
    public void onAttach(Context context) {

        Log.d(TAG,"onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d(TAG,"onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG,"onDestroyView");
        super.onDestroyView();
    }
}
