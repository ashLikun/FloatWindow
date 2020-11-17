package com.ashlikun.floatwindow;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 控制view
 */

public class FloatView extends FrameLayout {
    protected final FloatWindowManage floatWindowManage;
    protected View mView;

    //触摸事件的处理
    protected WindowOnTouchListener touchListener;

    FloatView(Context applicationContext, FloatWindowManage floatWindowManage, View contentView) {
        super(applicationContext);
        this.floatWindowManage = floatWindowManage;
        addContentView(contentView);
        initTouchEvent();
    }

    /**
     * 在这个底层的View里面添加调用者的View
     *
     * @param contentView
     */
    public void addContentView(View contentView) {
        if (mView != contentView) {
            //这里由于一个ViewGroup不能add一个已经有Parent的contentView,所以需要先判断contentView是否有Parent
            //如果有则需要将contentView先移除
            if (contentView.getParent() != null && contentView.getParent() instanceof ViewGroup) {
                ((ViewGroup) contentView.getParent()).removeView(contentView);
                if (contentView.getParent() instanceof FloatView) {
                    ((FloatView) contentView.getParent()).mView = null;
                }
            }
            mView = contentView;
            addView(contentView);
        }
    }

    private void initTouchEvent() {
        switch (floatWindowManage.mBuilder.mMoveType) {
            case MoveType.IN_ACTIVE:
                break;
            default:
                touchListener = new WindowOnTouchListener(floatWindowManage.mBuilder, floatWindowManage);
        }
    }

    /**
     * 解决点击与拖动冲突的关键代码
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchListener != null && touchListener.onInterceptTouchEvent(this, ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchListener != null && touchListener.onTouch(this, event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public View getView() {
        return mView;
    }
}
