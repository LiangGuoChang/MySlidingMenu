package com.lgc.mysliding.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lgc.mysliding.R;
import com.lgc.mysliding.activity.EnterActivity;
import com.lgc.mysliding.activity.MyMainActivity;

public class LeftMenuFragment extends Fragment implements View.OnClickListener {

    private static final String TAG="LeftMenuFragment";
    private static final int LOGIN_REQUEST=200;
    private static final int LOGIN_RESULT=201;

    private View mView;
    private ImageView iv_head;
    private TextView tv_user_name;
    private TextView tv_user_depart;
    private LinearLayout click_needle;
    private LinearLayout click_track;
    private LinearLayout click_fence;
    private LinearLayout click_navigate;
    private LinearLayout click_video_analyst;
    private LinearLayout click_correlate;
    private LinearLayout click_about;
    private SharedPreferences preferences;
    private SlidingMenu slMenu;
    private MyMainActivity mActivity;
//    private MainActivity mainActivity;
    private boolean logined;
    private LinearLayout feature;
    //    private MainFragment mainFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(null == mView){
            //初始化view
            initView(inflater,container);
            //获取MyMainActivity
            mActivity = (MyMainActivity) getActivity();
            Log.d(TAG,"mActivity--"+ mActivity.getComponentName());
            //获取侧滑菜单
            slMenu = mActivity.getSlidingMenu();
            Log.d(TAG,"menu--"+ slMenu +"--"+ slMenu.isMenuShowing());

            //获取登录与否
            preferences = mActivity.getSharedPreferences("Login", Context.MODE_PRIVATE);
            logined = preferences.getBoolean("isLogin", false);
            //首次打开,根据上次保存的登录信息,更新UI
            updateUI(logined);
        }
        return mView;
    }

    //初始化 view
    private void initView(LayoutInflater inflater, ViewGroup container){

        mView = inflater.inflate(R.layout.layout_menu_left,container,false);
        iv_head = (ImageView) mView.findViewById(R.id.iv_head);//头像
        tv_user_name = (TextView) mView.findViewById(R.id.tv_user_name);//用户名
        tv_user_depart = (TextView) mView.findViewById(R.id.tv_user_depart);//部门

        click_needle = (LinearLayout) mView.findViewById(R.id.click_needle);//探针
        click_track = (LinearLayout) mView.findViewById(R.id.click_track);//轨迹查询
        click_fence = (LinearLayout) mView.findViewById(R.id.click_fence);//电子围栏
        click_navigate = (LinearLayout) mView.findViewById(R.id.click_navigate);//导航
        click_video_analyst = (LinearLayout) mView.findViewById(R.id.click_video_analyst);//视频分析
        click_correlate = (LinearLayout) mView.findViewById(R.id.click_correlate);//轨迹分析
        click_about = (LinearLayout) mView.findViewById(R.id.click_about);//关于
        feature = (LinearLayout) mView.findViewById(R.id.linear_feature);//整个功能栏

        iv_head.setOnClickListener(this);
        click_needle.setOnClickListener(this);
        click_track.setOnClickListener(this);
        click_fence.setOnClickListener(this);
        click_navigate.setOnClickListener(this);
        click_video_analyst.setOnClickListener(this);
        click_correlate.setOnClickListener(this);
        click_about.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.iv_head:
                Intent login=new Intent(getActivity(), EnterActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("item",1);
                login.putExtras(bundle);
                startActivityForResult(login,LOGIN_REQUEST);
                break;
            case R.id.click_needle:
                showFragment(0);
               break;
            case R.id.click_track:
                showFragment(1);
                break;
            case R.id.click_fence:
                showFragment(2);
                break;
            case R.id.click_navigate:
                showFragment(3);
                break;
            case R.id.click_video_analyst:
                showFragment(4);
                break;
            case R.id.click_correlate:
                showFragment(5);
                break;
            case R.id.click_about://关于
                Intent about=new Intent(getActivity(), EnterActivity.class);
                Bundle bundle1=new Bundle();
                bundle1.putInt("item",2);
                about.putExtras(bundle1);
                startActivity(about);
                break;

            default:
                break;
        }
    }

    //点击按钮关闭菜单，显示对应的fragment
    private void showFragment(int item){
        //获取activity
//        SlidingFragmentActivity mActivity= (SlidingFragmentActivity) getActivity();

        //获得内容view
//        View mview= slMenu.getContent();
//        Log.d(TAG,"getContent--"+mview.getId());

        //关闭菜单，返回主页面
        if(slMenu.isMenuShowing()){
            slMenu.showContent();
        }
        //调用MyMainActivity中的切换ViewPager的方法
        boolean select= mActivity.selectViewPager(item);
        Log.d(TAG,"select--"+select);

    }

    //更新页面UI
    private void updateUI(boolean login){

        if(login){
            //登录,还需要更新用户信息

            click_needle.getChildAt(0).setBackgroundResource(R.drawable.needle);
            click_track.getChildAt(0).setBackgroundResource(R.drawable.track);
            click_fence.getChildAt(0).setBackgroundResource(R.drawable.fence);
            click_navigate.getChildAt(0).setBackgroundResource(R.drawable.navigation);
            click_video_analyst.getChildAt(0).setBackgroundResource(R.drawable.video);
            click_correlate.getChildAt(0).setBackgroundResource(R.drawable.correlate);
            click_about.getChildAt(0).setBackgroundResource(R.drawable.about);

            updateText(click_needle,Color.BLACK);
            updateText(click_track,Color.BLACK);
            updateText(click_fence,Color.BLACK);
            updateText(click_navigate,Color.BLACK);
            updateText(click_video_analyst,Color.BLACK);
            updateText(click_correlate,Color.BLACK);
            updateText(click_about,Color.BLACK);

            updateNext(R.drawable.next);

            Log.d(TAG,"更新登录了的UI");

        }else {
            //注销,还需不显示用户信息

            click_needle.getChildAt(0).setBackgroundResource(R.drawable.unneedle);
            click_track.getChildAt(0).setBackgroundResource(R.drawable.untrack);
            click_fence.getChildAt(0).setBackgroundResource(R.drawable.unfence);
            click_navigate.getChildAt(0).setBackgroundResource(R.drawable.unnavigation);
            click_video_analyst.getChildAt(0).setBackgroundResource(R.drawable.unvideo);
            click_correlate.getChildAt(0).setBackgroundResource(R.drawable.uncorrelate);
            click_about.getChildAt(0).setBackgroundResource(R.drawable.unabout);

            updateText(click_needle,Color.GRAY);
            updateText(click_track,Color.GRAY);
            updateText(click_fence,Color.GRAY);
            updateText(click_navigate,Color.GRAY);
            updateText(click_video_analyst,Color.GRAY);
            updateText(click_correlate,Color.GRAY);
            updateText(click_about,Color.GRAY);

            updateNext(R.drawable.next_g);

            Log.d(TAG,"更新注销了的UI");
        }

    }
    private void updateText(ViewGroup viewGroup,int color){
        TextView about= (TextView) viewGroup.getChildAt(1);
        about.setTextColor(color);
    }

    private void updateNext(int resID){
        click_needle.getChildAt(2).setBackgroundResource(resID);
        click_track.getChildAt(2).setBackgroundResource(resID);
        click_fence.getChildAt(2).setBackgroundResource(resID);
        click_navigate.getChildAt(2).setBackgroundResource(resID);
        click_video_analyst.getChildAt(2).setBackgroundResource(resID);
        click_correlate.getChildAt(2).setBackgroundResource(resID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==LOGIN_RESULT){

           boolean login= data.getBooleanExtra("login",false);
            switch (requestCode){
                //更新UI
                case LOGIN_REQUEST:
                    Log.i(TAG,"4545");
                    updateUI(login);
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
