package com.lgc.mysliding;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lgc.mysliding.fragment.MainFragment;

public class MainActivity extends SlidingFragmentActivity {

    private static final String TAG="MainActivity";
    private Button btn_needle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //获取 MainFragment
        MainFragment mainFragment=new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout,mainFragment).commit();


//        // configure the SlidingMenu
//        SlidingMenu menu = new SlidingMenu(this);
//        menu.setMode(SlidingMenu.LEFT);
//        // 设置触摸屏幕的模式
//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//菜单上一层的触摸模式
//        menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);//菜单页面上的触摸模式
//        //阴影宽度
//        menu.setShadowWidthRes(R.dimen.shadow_width);
////        menu.setShadowDrawable(R.drawable.shadow);//阴影效果
//        // 设置滑动菜单视图的宽度
//        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//        // 设置渐入渐出效果的值
//        menu.setFadeDegree(0.35f);
//
//        //滑动菜单与下面视图滑动速度比 取 0-1（float）
//        menu.setBehindScrollScale(0.5f);
//
//        //把滑动菜单添加进所有的Activity中，可选值SLIDING_CONTENT ， SLIDING_WINDOW
//        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        //设置剩余部分灰色
//        menu.setOffsetFadeDegree(0.15f);
//        //为侧滑菜单设置布局
//        menu.setMenu(R.layout.layout_menu_left);

    }

}
