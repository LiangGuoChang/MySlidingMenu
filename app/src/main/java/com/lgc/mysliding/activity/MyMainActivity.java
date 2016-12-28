package com.lgc.mysliding.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lgc.mysliding.R;
import com.lgc.mysliding.adapter.MyFSAdapter;
import com.lgc.mysliding.fragment.CorrelateFragment;
import com.lgc.mysliding.fragment.FenceFragment;
import com.lgc.mysliding.fragment.LeftMenuFragment;
import com.lgc.mysliding.fragment.NavigateFragment;
import com.lgc.mysliding.fragment.NeedleFragment;
import com.lgc.mysliding.fragment.TrackFragment;
import com.lgc.mysliding.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class MyMainActivity extends SlidingFragmentActivity implements View.OnClickListener{

    private final static String TAG="MyMainActivity";

    private ViewPager mViewPager;
    private List<Fragment> mFragments=new ArrayList<>();
//    private FragmentPagerAdapter mAdapter;
    private RelativeLayout show_menu;
    private SlidingMenu menu;
    private MyFSAdapter myFSAdapter;
    private String[] titles=new String[]{"探针管理","轨迹查询","电子围栏","目标导航","视频联动分析","轨迹关联分析"};
    private TextView my_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.i(TAG,"oncreate");
        Log.i(TAG,"oncreate");
        setContentView(R.layout.activity_my_main);
        Log.d(TAG,"MyMainActivity---onCreate");

        show_menu = (RelativeLayout) findViewById(R.id.show_menu);
        my_title = (TextView) findViewById(R.id.tv_my_title);
        show_menu.setOnClickListener(this);
        //设置初始标题
        setTitle(titles[0]);
        initLeftMenu();
        initViewPager();

    }

    //初始化 SlidingMune
    private void initLeftMenu(){

        Log.d(TAG,"initLeftMenu");

        //获取 LeftMenuFragment 对象
        LeftMenuFragment leftMenu=new LeftMenuFragment();
        //设置背景 view
        setBehindContentView(R.layout.left_menu_frame);
        //加载 LeftMenuFragment到 left_menu_frame布局中
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.left_menu_frame_id,leftMenu).commit();

        //初始化 SlidingMenu 属性参数
        menu = getSlidingMenu();
        //左边菜单模式
        menu.setMode(SlidingMenu.LEFT);
//        //打开菜单的触摸方式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        //回到主页面的触摸方式
        menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
        //设置阴影部分宽度
        menu.setShadowWidthRes(R.dimen.shadow_width);
        //设置菜单宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        //设置背景淡入淡出效果
        menu.setFadeDegree(0.35f);
        //主页面淡入淡出效果 自定义的
        menu.setOffsetFadeDegree(0.25f);

    }

    //初始化 ViewPager
    private void initViewPager(){
        Log.d(TAG,"initViewPager");

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        NeedleFragment needleFragment=new NeedleFragment();
        TrackFragment trackFragment=new TrackFragment();
        FenceFragment fenceFragment=new FenceFragment();
        NavigateFragment navigateFragment=new NavigateFragment();
        VideoFragment videoFragment=new VideoFragment();
        CorrelateFragment correlateFragment=new CorrelateFragment();
        mFragments.add(needleFragment);
        mFragments.add(trackFragment);
        mFragments.add(fenceFragment);
        mFragments.add(navigateFragment);
        mFragments.add(videoFragment);
        mFragments.add(correlateFragment);

        myFSAdapter = new MyFSAdapter(getSupportFragmentManager());
        myFSAdapter.setFragmentList(mFragments);

        mViewPager.setAdapter(myFSAdapter);
        mViewPager.setCurrentItem(0);
        
        //ViewPager监听事件
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                String title=titles[position];
                setTitle(title);

                if(position==0){
                    //打开菜单的触摸方式
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else if(position>0){
                    //打开菜单的触摸方式
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                }

                Log.d(TAG,"onPageSelected"+"\n"+"position--"+position+"title--"+title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                Log.d(TAG,"onPageScrollStateChanged"+"\n"+"state--"+state);

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.show_menu:
                getSlidingMenu().showMenu();//点击显示菜单
               break;
        }
    }

    //设置标题
    private void setTitle(String title){
        my_title.setText(title);
    }

    //外部调用切换
    public boolean selectViewPager(int selectItem){

        int currentItem=mViewPager.getCurrentItem();
        if(selectItem!=currentItem){
            mViewPager.setCurrentItem(selectItem,false);
        }

        return true;
    }



//    @Override
//    public void onSelectViewPager(int selectItem) {
//        selectViewPager(selectItem);
//    }

}