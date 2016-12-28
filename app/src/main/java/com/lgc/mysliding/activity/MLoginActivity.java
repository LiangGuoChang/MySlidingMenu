package com.lgc.mysliding.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lgc.mysliding.R;

public class MLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LOGIN_RESULT=201;

    private Button btn_1;
    private Button btn_regist;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("Login",MODE_PRIVATE);
        editor = preferences.edit();

        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_regist = (Button) findViewById(R.id.btn_regist);

        btn_1.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.btn_1:

                editor.putBoolean("isLogin",true);
                editor.commit();
                Intent login=new Intent();
                login.putExtra("login", true);
                setResult(LOGIN_RESULT,login);
                finish();
               break;

            case R.id.btn_regist:

                editor.putBoolean("isLogin",false);
                editor.commit();
                Intent logout=new Intent();
                logout.putExtra("login", false);
                setResult(LOGIN_RESULT,logout);
                finish();
                break;

            default:
                break;
        }
    }
}
