package com.lgc.mysliding.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lgc.mysliding.MainActivity;
import com.lgc.mysliding.R;
import com.lgc.mysliding.adapter.MyFSAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private static final String TAG="MainFragment";
    private String[] titles=new String[]{"探针管理","轨迹查询","电子围栏","目标导航","视频联动分析","轨迹关联分析"};
    private int currentItem=0;
    private List<Fragment> fragmentList=new ArrayList<>();

    private View mView;
    private RelativeLayout show_menu;
    private TextView main_tittle;
    private ViewPager mViewPager;
    private MyFSAdapter myFSAdapter;
    private SlidingMenu menu;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView==null){
            mainActivity = (MainActivity) getActivity();
            mView = inflater.inflate(R.layout.fragment_main, container, false);
            initView();
            initLeftMenu();
            initData();
        }

        return mView;
    }

    //初始化布局
    private void initView(){
        show_menu = (RelativeLayout) mView.findViewById(R.id.main_show_menu);
        main_tittle = (TextView) mView.findViewById(R.id.tv_main_title);
        mViewPager = (ViewPager) mView.findViewById(R.id.main_fragment_viewpager);

        //初始设置标题为 titles[0]
        setTitle(titles[currentItem]);
    }

    //加载 mViewPager
    private void initData(){

        NeedleFragment needleFragment=new NeedleFragment();
        TrackFragment trackFragment=new TrackFragment();
        FenceFragment fenceFragment=new FenceFragment();
        NavigateFragment navigateFragment=new NavigateFragment();
        VideoFragment videoFragment=new VideoFragment();
        CorrelateFragment correlateFragment=new CorrelateFragment();
        fragmentList.add(needleFragment);
        fragmentList.add(trackFragment);
        fragmentList.add(fenceFragment);
        fragmentList.add(navigateFragment);
        fragmentList.add(videoFragment);
        fragmentList.add(correlateFragment);

        myFSAdapter = new MyFSAdapter(getChildFragmentManager());
        myFSAdapter.setFragmentList(fragmentList);

        //设置 mViewPager 初始为 第0个
        mViewPager.setAdapter(myFSAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);
    }

    //初始化 SlidingMenu
    private void initLeftMenu(){

        //获取 LeftMenuFragment 对象
        LeftMenuFragment leftMenu=new LeftMenuFragment();
        //设置背景 view
        mainActivity.setBehindContentView(R.layout.left_menu_frame);
        //加载 LeftMenuFragment到 left_menu_frame布局中
        mainActivity.getSupportFragmentManager()
                .beginTransaction().replace(R.id.left_menu_frame_id,leftMenu).commit();

        //初始化 SlidingMenu 属性参数
        menu = mainActivity.getSlidingMenu();
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

    //设置标题
    private void setTitle(String title){
        main_tittle.setText(title);
    }

    //外部调用的切换 mViewPager
    public boolean selectPager(int position){

        int item=mViewPager.getCurrentItem();
        if (position!=item){
            mViewPager.setCurrentItem(position);
            return true;
        }

        return false;
    }


    // mViewPager滑动时回调这三个方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        currentItem=position;
        String title=titles[position];
        setTitle(title);

        //第0个ViewPager时 全屏划出菜单
        if(position==0){
            menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }else{
            menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
