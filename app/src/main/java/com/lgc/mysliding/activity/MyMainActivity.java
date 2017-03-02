package com.lgc.mysliding.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
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
import com.lgc.mysliding.service.AlertMsgService;
import com.lgc.mysliding.views.NoCacheViewPager;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;

import java.util.ArrayList;
import java.util.List;

public class MyMainActivity extends SlidingFragmentActivity implements View.OnClickListener{

    private final static String TAG="MyMainActivity";

    private List<Fragment> mFragments=new ArrayList<>();
//    private FragmentPagerAdapter mAdapter;
    private RelativeLayout show_menu;
    private SlidingMenu menu;
    private MyFSAdapter myFSAdapter;
    private String[] titles=new String[]{"探针管理","轨迹查询","电子围栏","目标导航","视频联动分析","轨迹关联分析"};
    private TextView my_title;
    private String url="http://192.168.1.184:8080/json/detectorInfo.json";
    public ImageView iv_search_mac;
    public ImageView iv_search_trace;
    public ImageView iv_fenceMenu;
//    private ViewPager mViewPager;
//    private MyViewPager my_viewpager;

    private MsgReceiver msgReceiver;
    private AlertMsgService alertMsgService;//获取报警信息服务
    private int allAlertMsgCount=0;//全部报警信息记录
    private NoCacheViewPager noCacheViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_main);
        Log.d(TAG,"MyMainActivity---onCreate");

        //注册广播接收者
        msgReceiver=new MsgReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.lgc.mysliding.reciver.UPDATE_LISTVIEW");
        registerReceiver(msgReceiver,intentFilter);

        show_menu = (RelativeLayout) findViewById(R.id.show_menu);
        my_title = (TextView) findViewById(R.id.tv_my_title);
        iv_search_mac = (ImageView) findViewById(R.id.iv_search_mac);//探针管理的搜索按钮
        iv_search_trace = (ImageView) findViewById(R.id.iv_search_trace);//轨迹查询按钮
        iv_fenceMenu = (ImageView) findViewById(R.id.iv_fence_menu);//电子围栏的菜单按钮
        show_menu.setOnClickListener(this);
        //设置初始标题
        setTitle(titles[0]);
        initLeftMenu();
        initViewPager();


        // 开启logcat输出，方便debug，发布时请关闭
// XGPushConfig.enableDebug(this, true);
// 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
// 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
// 具体可参考详细的开发指南
// 传递的参数为ApplicationContext
        Context context = getApplicationContext();
        XGPushManager.registerPush(context);

// 2.36（不包括）之前的版本需要调用以下2行代码
        Intent service = new Intent(context, XGPushService.class);
        context.startService(service);

        String token=XGPushConfig.getToken(this);
        Log.d("Token","token--"+token);

// 其它常用的API：
// 绑定账号（别名）注册：registerPush(context,account)或registerPush(context,account, XGIOperateCallback)，其中account为APP账号，可以为任意字符串（qq、openid或任意第三方），业务方一定要注意终端与后台保持一致。
// 取消绑定账号（别名）：registerPush(context,"*")，即account="*"为取消绑定，解绑后，该针对该账号的推送将失效
// 反注册（不再接收消息）：unregisterPush(context)
// 设置标签：setTag(context, tagName)
// 删除标签：deleteTag(context, tagName)

        //获取报警信息服务
        alertMsgService=AlertMsgService.getMsgServiceInstance(this);

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

        //添加解决问题的就是这行代码
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    //初始化 ViewPager
    private void initViewPager(){
        Log.d(TAG,"initViewPager");

//        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
//        my_viewpager = (MyViewPager) findViewById(R.id.main_my_viewpager);
//        my_viewpager.setScroll(false);//设置不能滑动

        noCacheViewPager = (NoCacheViewPager) findViewById(R.id.vp_nocache);
        noCacheViewPager.setOffscreenPageLimit(0);
        noCacheViewPager.setScroll(false);//设置不能滑动

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

        noCacheViewPager.setAdapter(myFSAdapter);
        noCacheViewPager.setCurrentItem(0);

        noCacheViewPager.setOnPageChangeListener(new NoCacheViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                String title=titles[position];
                setTitle(title);

                if(position==0){
                    //打开菜单的触摸方式 全屏触摸
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    //显示搜索控件
                    iv_search_mac.setVisibility(View.VISIBLE);
                    iv_search_mac.setClickable(true);
                    //隐藏轨迹查询控件
                    iv_search_trace.setVisibility(View.INVISIBLE);
                    iv_search_trace.setClickable(false);
                    //电子围栏控件
                    iv_fenceMenu.setVisibility(View.INVISIBLE);
                    iv_fenceMenu.setClickable(false);
                }else if(position==1){
                    //打开菜单的触摸方式 边缘触摸
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    //显示轨迹查询控件
                    iv_search_trace.setVisibility(View.VISIBLE);
                    iv_search_trace.setClickable(true);
                    //隐藏搜索控件
                    iv_search_mac.setVisibility(View.INVISIBLE);
                    iv_search_mac.setClickable(false);
                    //电子围栏控件
                    iv_fenceMenu.setVisibility(View.INVISIBLE);
                    iv_fenceMenu.setClickable(false);
                }else if (position == 2){
                    //打开菜单的触摸方式
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    //电子围栏控件
                    iv_fenceMenu.setVisibility(View.VISIBLE);
                    iv_fenceMenu.setClickable(true);
                    //隐藏搜索控件
                    iv_search_mac.setVisibility(View.INVISIBLE);
                    iv_search_mac.setClickable(false);
                    //隐藏轨迹查询控件
                    iv_search_trace.setVisibility(View.INVISIBLE);
                    iv_search_trace.setClickable(false);
                }else if (position > 2){
                    //打开菜单的触摸方式
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    //电子围栏控件
                    iv_fenceMenu.setVisibility(View.INVISIBLE);
                    iv_fenceMenu.setClickable(false);
                    //隐藏搜索控件
                    iv_search_mac.setVisibility(View.INVISIBLE);
                    iv_search_mac.setClickable(false);
                    //隐藏轨迹查询控件
                    iv_search_trace.setVisibility(View.INVISIBLE);
                    iv_search_trace.setClickable(false);
                }

                Log.d(TAG,"onPageSelected"+"\n"+"position--"+position+"title--"+title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        
        //ViewPager监听事件
        /*my_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                String title=titles[position];
                setTitle(title);

                if(position==0){
                    //打开菜单的触摸方式 全屏触摸
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    //显示搜索控件
                    iv_search_mac.setVisibility(View.VISIBLE);
                    iv_search_mac.setClickable(true);
                    //隐藏轨迹查询控件
                    iv_search_trace.setVisibility(View.INVISIBLE);
                    iv_search_trace.setClickable(false);
                    //电子围栏控件
                    iv_fenceMenu.setVisibility(View.INVISIBLE);
                    iv_fenceMenu.setClickable(false);
                }else if(position==1){
                    //打开菜单的触摸方式 边缘触摸
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    //显示轨迹查询控件
                    iv_search_trace.setVisibility(View.VISIBLE);
                    iv_search_trace.setClickable(true);
                    //隐藏搜索控件
                    iv_search_mac.setVisibility(View.INVISIBLE);
                    iv_search_mac.setClickable(false);
                    //电子围栏控件
                    iv_fenceMenu.setVisibility(View.INVISIBLE);
                    iv_fenceMenu.setClickable(false);
                }else if (position == 2){
                    //打开菜单的触摸方式
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    //电子围栏控件
                    iv_fenceMenu.setVisibility(View.VISIBLE);
                    iv_fenceMenu.setClickable(true);
                    //隐藏搜索控件
                    iv_search_mac.setVisibility(View.INVISIBLE);
                    iv_search_mac.setClickable(false);
                    //隐藏轨迹查询控件
                    iv_search_trace.setVisibility(View.INVISIBLE);
                    iv_search_trace.setClickable(false);
                }else if (position > 2){
                    //打开菜单的触摸方式
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    //电子围栏控件
                    iv_fenceMenu.setVisibility(View.INVISIBLE);
                    iv_fenceMenu.setClickable(false);
                    //隐藏搜索控件
                    iv_search_mac.setVisibility(View.INVISIBLE);
                    iv_search_mac.setClickable(false);
                    //隐藏轨迹查询控件
                    iv_search_trace.setVisibility(View.INVISIBLE);
                    iv_search_trace.setClickable(false);
                }

                Log.d(TAG,"onPageSelected"+"\n"+"position--"+position+"title--"+title);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                Log.d(TAG,"onPageScrollStateChanged"+"\n"+"state--"+state);

            }
        });*/

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

        int currentItem=noCacheViewPager.getCurrentItem();
        if(selectItem!=currentItem){
            noCacheViewPager.setCurrentItem(selectItem,false);
        }

        return true;
    }

    public int getAllAlertMsgCount() {
        return allAlertMsgCount;
    }

    public void setAllAlertMsgCount(int allAlertMsgCount) {
        this.allAlertMsgCount = allAlertMsgCount;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        super.onDestroy();
    }


    //    @Override
//    public void onSelectViewPager(int selectItem) {
//        selectViewPager(selectItem);
//    }

    public class MsgReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            setAllAlertMsgCount(alertMsgService.getMsgCount());
        }
    }

}