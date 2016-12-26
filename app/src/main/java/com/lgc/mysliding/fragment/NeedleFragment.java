package com.lgc.mysliding.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lgc.mysliding.R;

public class NeedleFragment extends Fragment implements View.OnClickListener {

    private static final String TAG="NeedleFragment";
    private View mView;
    private TextView textview;
    private Button btn;
    private int item=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView==null){
            mView = inflater.inflate(R.layout.fragment_needle, container, false);

            textview = (TextView) mView.findViewById(R.id.textView);
            btn = (Button) mView.findViewById(R.id.btn_1);
            btn.setOnClickListener(this);
        }

        Log.d(TAG,"onCreateView");
        return mView;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_1:
                if (item==0){
                    textview.setText("hello");
                    item=1;
                }else {
                    textview.setText("探针管理");
                    item=0;
                }
                break;
        }
    }
}
