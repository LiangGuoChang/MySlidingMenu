package com.lgc.mysliding.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lgc.mysliding.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 自定义下拉刷新，上拉加载 ListView
 */
public class MyRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG="MyRefreshListView";

    //区分当前操作是刷新还是加载更多
    public static final int LV_REFRESH=0;
    public static final int LV_LOAD=1;

    private View headView;//头部view
    private ImageView iv_arrow;//旋转箭头
    private ProgressBar pb_rotate;//头部更新旋转
    private TextView tv_pull_state;//下拉状态
    private TextView refresh_time;//刷新时间
    private TextView timeText;
    private int headViewHeight;//头部布局的高度
    private RotateAnimation pullAnimation;//下拉时箭头动画
    private RotateAnimation downAnimation;//下拉完成时箭头动画

    private View footView;//底部view
    private ProgressBar pb_foot;//底部加载动画
    private TextView foot_text;//底部加载状态
    private int footViewHeight;//底部布局的高度

    // 定义header的四种状态和当前状态
    private static final int NONE = 0;
    private static final int PULL=1;
    private static final int RELEASE=2;
    private static final int REFRESHING=3;
    private int current_state;//设置当前状态


    public MyRefreshListView(Context context) {
        super(context);
        init();
    }

    public MyRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

   public MyRefreshListView(Context context, AttributeSet attrs, int defStyle){
       super(context,attrs,defStyle);
       init();
   }

    //定义外部调用的下拉刷新监听
    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.onRefreshListener=onRefreshListener;
    }

    //定义外部调用的上拉加载监听
    public void setOnLoadListener(OnLoadListener onLoadListener){
        this.loadEnable=true;//设为可以上拉加载
        this.onLoadListener=onLoadListener;
    }

    //外部关闭、开启加载功能
    public void setLoadEnable(boolean mLoadEnable){
        this.loadEnable=mLoadEnable;
    }
    public boolean getLoadEnable(){
        return this.loadEnable;
    }

    //外部调用的下拉刷新结束后回调方法
    public void OnRefreshComplete(){
        String currentTime=getRefreshTime();
        onRefreshComplete(currentTime);
    }

    //外部调用的上拉加载完成的回调方法
    public void OnLoadComplete(){
        isLoading=false;

        Log.d(TAG,"加载完成！");
    }

    //获取刷新的时间
    private String getRefreshTime(){
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime=sdf.format(date);
        return currentTime;
    }

    private void init(){
        //设置滑动监听
        setOnScrollListener(this);
        //头部
        initHeadView();
        //头部箭头动画
        initArrowAnimation();
        //底部
        initFootView();
    }

    //设置头部 view
    private void initHeadView(){
        headView=View.inflate(getContext(), R.layout.refresh_head_listview,null);
        iv_arrow = (ImageView) headView.findViewById(R.id.iv_arrow);
        pb_rotate = (ProgressBar) headView.findViewById(R.id.pb_rotate);
        tv_pull_state = (TextView) headView.findViewById(R.id.tv_pull_state);
        refresh_time = (TextView) headView.findViewById(R.id.tv_refresh_time);
        timeText = (TextView) headView.findViewById(R.id.tv_time_text);
        //测量headView的高度，并保存
        headView.measure(0,0);
        headViewHeight=headView.getMeasuredHeight();

        Log.d(TAG,"头部布局原始宽度::"+headViewHeight);

        int headInitHeight=headView.getPaddingTop();
        Log.d(TAG,"headInitHeight"+headInitHeight);

        //设置 padding 隐藏头部布局
        headView.setPadding(0,-headViewHeight,0,0);
        Log.d(TAG,"设置后::"+headView.getPaddingTop());

        //添加头部布局
        addHeaderView(headView);
    }

    //头部箭头旋转动画
    private void initArrowAnimation(){
        //下拉时箭头动画
        pullAnimation=new RotateAnimation(0,-180,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        pullAnimation.setDuration(300);
        pullAnimation.setFillAfter(true);
        //下拉完成时箭头动画
        downAnimation=new RotateAnimation(-180,0,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(300);
        downAnimation.setFillAfter(true);
    }

    //设置底部 view
    private void initFootView(){
        footView=View.inflate(getContext(),R.layout.refresh_foot_listview,null);
        pb_foot = (ProgressBar) footView.findViewById(R.id.pb_foot);
        foot_text = (TextView) footView.findViewById(R.id.load_text);
        //测量并保存底部布局高度
        footView.measure(0,0);
        footViewHeight=footView.getMeasuredHeight();

        Log.d(TAG,"底部布局原始宽度::"+footViewHeight);

        //设置 padding 隐藏底部布局
//        footView.setPadding(0,-footViewHeight,0,0);
        //添加底部布局
        addFooterView(footView);
    }

    private int firstVisibleItem;//显示的第一个条目的id,只有为0时才下拉刷新
    private boolean isRecorded;
    private OnRefreshListener onRefreshListener;
    private int starY;//按下时的y轴
    private static final int SPACE=100;//区分 Pull和release的大小
    private int mScrollState;//当前滑动状态
    private OnLoadListener onLoadListener;
    private boolean loadEnable;//能否加载更多
    private boolean isLoading=false;//是否正在加载
    private boolean isLoadFull;//是否加载满
    private int itemSize=22;//定义listview显示22条时需要上拉加载

    //实现下拉刷新接口的方法
    private void onRefresh(){
        if (null != onRefreshListener){
            onRefreshListener.onRefresh();

            Log.d(TAG,"onRefresh--正在刷新...");
        }
    }

    //刷新结束后回调
    private void onRefreshComplete(String refreshTime){
        refresh_time.setText(refreshTime);
        current_state=NONE;
        refreshHeadViewByState();

        Log.d(TAG,"onRefreshComplete--刷新完成！");
    }

    //触摸事件判断
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){

            //按下时获取坐标
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG,"MotionEvent.ACTION_DOWN");

                if (firstVisibleItem==0){
                    isRecorded=true;
                    starY= (int) ev.getY();
                    Log.d(TAG,"starY::"+starY);
                }
                break;

            //滑动
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG,"MotionEvent.ACTION_MOVE");
                getCurrState();//获取当前状态

                if (current_state!=REFRESHING){
                    whenMove(ev);
                }
                break;

            //抬起
            case MotionEvent.ACTION_UP:
                Log.d(TAG,"MotionEvent.ACTION_UP");

                if (current_state==PULL){
                    //下拉状态时抬起
                    current_state=NONE;
                    //更新ui
                    refreshHeadViewByState();
                }else if (current_state==RELEASE){
                    //松开状态抬起时,准备刷新
                    current_state=REFRESHING;
                    //更新ui
                    refreshHeadViewByState();
                    //调用接口的更新方法
                    onRefresh();
                }
                isRecorded=false;
                break;
        }

        return super.onTouchEvent(ev);
    }

    //处理滑动手势
    private void whenMove(MotionEvent event){
        if (!isRecorded){
            return;
        }
        //获取滑动偏移量
        int endY= (int) event.getY();
        Log.d(TAG,"滑动时endY--"+endY);
        int deltaY= endY-starY;
        Log.d(TAG,"deltaY::"+deltaY);

        //获取新的padding
        int newPaddingTop=-headViewHeight+deltaY;
        Log.d(TAG,"newPaddingTop::"+newPaddingTop);

        switch (current_state){

            case NONE:
                if (deltaY>0){
                    current_state=PULL;
                    refreshHeadViewByState();
                }
                break;
            case PULL:
                headView.setPadding(0,newPaddingTop/2,0,0);
//                headView.setPadding(0,100,0,0);
                if (mScrollState==SCROLL_STATE_IDLE){
                    // TODO: 2017/1/9 下滑时向左右切换到其它界面时，即取消滑动
                    current_state=NONE;
                    refreshHeadViewByState();
                }
                if (mScrollState==SCROLL_STATE_TOUCH_SCROLL && deltaY > headViewHeight+SPACE){
                    current_state=RELEASE;
                    refreshHeadViewByState();
                }
                break;
            case RELEASE:
                headView.setPadding(0,newPaddingTop/2,0,0);
//                headView.setPadding(0,100,0,0);
                if (deltaY > 0 && deltaY < headViewHeight+SPACE){
                    current_state=PULL;
                    refreshHeadViewByState();
                }else if (deltaY <= 0){
                    current_state=NONE;
                    refreshHeadViewByState();
                }
                break;
        }
    }

    //根据当前下滑状态设置控件
    private void refreshHeadViewByState(){
        switch (current_state){
            case NONE:
                headView.setPadding(0,-headViewHeight,0,0);
                tv_pull_state.setText("下拉刷新");
                tv_pull_state.setVisibility(View.GONE);
                timeText.setVisibility(View.GONE);
                pb_rotate.setVisibility(View.GONE);
                iv_arrow.setVisibility(View.GONE);
                refresh_time.setVisibility(View.GONE);
                iv_arrow.clearAnimation();
                iv_arrow.setImageResource(R.drawable.arrow_pull);
                break;
            case PULL:
                tv_pull_state.setVisibility(View.VISIBLE);
                pb_rotate.setVisibility(View.INVISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                refresh_time.setVisibility(View.VISIBLE);
                timeText.setVisibility(View.VISIBLE);
                tv_pull_state.setText("下拉可以刷新");
                iv_arrow.clearAnimation();
                iv_arrow.setAnimation(downAnimation);
                break;
            case RELEASE:
//                headView.setPadding(0,0,0,0);
                tv_pull_state.setVisibility(View.VISIBLE);
                pb_rotate.setVisibility(View.INVISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                refresh_time.setVisibility(View.VISIBLE);
                timeText.setVisibility(View.VISIBLE);
//                tv_pull_state.setText("下拉可以刷新");
                tv_pull_state.setText("松开可以刷新");
                iv_arrow.clearAnimation();
                iv_arrow.setAnimation(pullAnimation);
                break;
            case REFRESHING:
                headView.setPadding(0,0,0,0);
                iv_arrow.clearAnimation();
                tv_pull_state.setVisibility(View.VISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                refresh_time.setVisibility(View.VISIBLE);
                iv_arrow.setVisibility(View.INVISIBLE);
                pb_rotate.setVisibility(View.VISIBLE);
                timeText.setVisibility(View.VISIBLE);
                tv_pull_state.setText("正在刷新...");
                break;
        }
    }

    //获取当前状态
    private int getCurrState(){
        int state=current_state;
        Log.d(TAG,"当前状态--"+state);
        return state;
    }

    //滑动状态改变
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        this.mScrollState=i;
        //上拉加载
        needLoad(absListView,i);
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        //滑动时，获取当前页面的第一个条目id
        this.firstVisibleItem=i;

        Log.d(TAG,"getCount--"+absListView.getCount());
        Log.d(TAG,"onScroll--"+i2);
    }

    //实现上拉加载的方法
    private void onLoad(){
        if (onLoadListener!=null){
            onLoadListener.onLoad();
            Log.d(TAG,"onLoad-正在加载...");
        }
    }

    /**
     * 根据滑动状态判断是否需要加载更多
     * @param absListView
     * @param scrollState
     */
    private void needLoad(AbsListView absListView,int scrollState){
        if (!loadEnable){
            //如果不能加载则退出
            return;
        }

        try {
            //判断滑动状态，加载更多
            if (scrollState==OnScrollListener.SCROLL_STATE_IDLE
                    && !isLoading
                    && absListView.getLastVisiblePosition()==absListView.getPositionForView(footView)
                    && !isLoadFull){

                Log.d(TAG,"needLoad-加载更多...");

                onLoad();
                isLoading=true;
            }
        }catch (Exception e){
            Log.d(TAG,"needLoad--"+e.getMessage());
        }
    }

    /**
     * 外部调用的根据显示的条目数量来显示 footView
     * @param resultSize
     * 这里假设显示22条时表示还有数据；不足22条时，表示数据已经全部加载
     */
    public void setResultSize(int resultSize){
        if (resultSize==0){
            //0条，即没有数据时也表示加载满了
            isLoadFull=true;
            pb_foot.setVisibility(View.GONE);
            foot_text.setVisibility(View.GONE);

            Log.d(TAG,"setResultSize-无数据-"+resultSize);
        }else if (resultSize >0 && resultSize < itemSize){
            //小于22条时也表示加载满了
            isLoadFull=true;
            pb_foot.setVisibility(View.GONE);
            foot_text.setVisibility(View.GONE);

            Log.d(TAG,"setResultSize-小于22-"+resultSize);
        }else if (resultSize == itemSize){
            //大于等于22条时也表示还有数据，可以加载更多
            isLoadFull=false;
            pb_foot.setVisibility(View.VISIBLE);
            foot_text.setVisibility(View.VISIBLE);

            Log.d(TAG,"setResultSize-大于等于22-"+resultSize);
        }
    }

    /**
     * 定义下拉刷新接口,和接口实现的方法
     */
    public interface OnRefreshListener{
        void onRefresh();
    }

    /**
     * 定义上拉加载更多接口
     */
    public interface OnLoadListener{
        void onLoad();
    }

}
