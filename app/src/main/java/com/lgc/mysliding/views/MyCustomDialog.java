package com.lgc.mysliding.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lgc.mysliding.R;

/**
 * Created by Administrator on 2017/2/21.
 * 自定义dialog用与添加围栏监控号码
 */
public class MyCustomDialog extends Dialog implements View.OnClickListener {

    private String titleName;
    private onCustomDialogListener mOnCustomDialogListener;
    private EditText et_addPhone;
    private Button btn_savePhone;

    public MyCustomDialog(Context context, String title, onCustomDialogListener onCustomDialogListener) {
        super(context);
        this.titleName=title;
        this.mOnCustomDialogListener=onCustomDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fence_add_phone);
        //设置标题
        setTitle(titleName);
        //获取控件
        et_addPhone = (EditText) findViewById(R.id.et_add_phone);
        btn_savePhone = (Button) findViewById(R.id.btn_save_phone);
        btn_savePhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //保存按钮点击事件
            case R.id.btn_save_phone:
//                mOnCustomDialogListener.getPhone(String.valueOf(et_addPhone.getText()));
//                MyCustomDialog.this.dismiss();
                if (TextUtils.isEmpty(et_addPhone.getText())){
                    Toast.makeText(getContext(),"未输入监控号码",Toast.LENGTH_SHORT).show();
                }else {
                    mOnCustomDialogListener.getPhone(String.valueOf(et_addPhone.getText()));
                    MyCustomDialog.this.dismiss();
                    Toast.makeText(getContext(),"保存成功",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //定义回调接口，获得输入的监控号码
   public interface onCustomDialogListener{
        void getPhone(String phone);
    }
}
