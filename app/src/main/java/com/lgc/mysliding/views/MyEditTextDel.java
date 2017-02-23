package com.lgc.mysliding.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.lgc.mysliding.R;

/**
 * Created by Administrator on 2017/2/15.
 * 自定义带有删除功能的EditText
 */
public class MyEditTextDel extends EditText implements TextWatcher {

    //位于控件内右边的清除EditText内容的图片
    private Drawable clearDrawable;

    public MyEditTextDel(Context context) {
        super(context);
        initEditText();
    }

    public MyEditTextDel(Context context, AttributeSet attrs) {
        super(context, attrs,android.R.attr.editTextStyle);
        initEditText();
    }

    public MyEditTextDel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEditText();
    }

    //设置样式
    private void initEditText(){
        //设置背景
        setBackgroundResource(R.drawable.editext_background);
        //设置右边清除的图片
        clearDrawable=getCompoundDrawables()[2];
        clearDrawable=getResources().getDrawable(R.drawable.clear);
        clearDrawable.setBounds(0,0,
                (int) (clearDrawable.getIntrinsicWidth()*0.65),
                (int) (clearDrawable.getIntrinsicHeight()*0.65));

        setClearDrawable();
        addTextChangedListener(this);
    }

    //根据长度来显示和隐藏删除图片
    private void setClearDrawable(){
        if (length()<1){
            setClearDrawableVisible(false);
        }else {
            setClearDrawableVisible(true);
        }
    }

    //设置删除图片显示和隐藏
    protected void setClearDrawableVisible(boolean isVisible){
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1],
                isVisible ? clearDrawable : null,
                getCompoundDrawables()[3]);
    }

    //输入款内容发生变化时调用该方法
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setClearDrawable();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        setClearDrawable();
    }

    //点击删除图片，清除内容
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //手势抬起时
        if (event.getAction()==MotionEvent.ACTION_UP){
            if (getCompoundDrawables()[2]!=null){
                boolean isTouch=(event.getX() > (getWidth()-getTotalPaddingRight()))
                        && (event.getX() < (getWidth()-getPaddingRight()));
                if (isTouch){
                    this.setText("");
                }
                this.setFocusable(true);
                this.setFocusableInTouchMode(true);
                this.requestFocus();
            }
        }
        return super.onTouchEvent(event);
    }
}
