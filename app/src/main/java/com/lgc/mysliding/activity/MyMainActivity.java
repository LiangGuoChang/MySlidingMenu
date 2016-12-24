package com.lgc.mysliding.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lgc.mysliding.R;
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
    private List<Fragment> mFragments=new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private ImageView iv_show_menu;
    private SlidingMenu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_main);

        Log.d(TAG,"MyMainActivity---onCreate");

        iv_show_menu = (ImageView) findViewById(R.id.iv_show_menu);
        iv_show_menu.setOnClickListener(this);

        //初始化 SlidingMune
        initLeftMenu();
        //初始化 ViewPager
        initViewPager();

    }

    //初始化 SlidingMune
    private void initLeftMenu(){

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
//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
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

        //初始化 adapter // TODO: 2016/12/22 需要做优化
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        
        //ViewPager监听事件
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d(TAG,"onPageScrolled"+"\n"+"position--"+position
//                        +"\n"+"positionOffset--"+positionOffset
//                        +"\n"+"positionOffsetPixels--"+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

                if(position==0){
                    //打开菜单的触摸方式
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else if(position>0){
                    //打开菜单的触摸方式
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                }

                Log.d(TAG,"onPageSelected"+"\n"+"position--"+position);

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
            case R.id.iv_show_menu:
                getSlidingMenu().showMenu();//点击显示菜单
               break;
        }
    }

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