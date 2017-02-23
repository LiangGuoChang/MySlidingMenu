package com.lgc.mysliding.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lgc on 2017/2/13.
 * 自定义 ViewPager
 */
public class MyViewPager extends ViewPager{

    private boolean isCanScroll=false;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置是否能做有滑动
     * @param isScroll
     */
    public void setScroll(boolean isScroll){
        this.isCanScroll=isScroll;
    }

//    @Override
//    public void scrollTo(int x, int y) {
//        super.scrollTo(x, y);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll){
            return super.onTouchEvent(ev);
        }else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanScroll){
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }
    }

//    @Override
//    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//        if (v.getClass().getName().equals("com.amap.api.maps2d.MapView")){
//            return true;
//        }
//        return super.canScroll(v, checkV, dx, x, y);
//    }
}
