package com.ashlikun.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/11/17　10:17
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：底层view触摸事件的处理
 */
public class WindowOnTouchListener implements View.OnTouchListener {
    private FloatWindowManage floatWindowManage;
    FloatWindow.Builder mBuilder;
    private float downX;
    private float downY;
    private float downRawX;
    private float downRawY;
    private float upX;
    private float upY;
    private int mSlop;
    private boolean isStartMoveCallback;
    private boolean onMove;

    private ValueAnimator mAnimator;
    private TimeInterpolator mDecelerateInterpolator;

    WindowOnTouchListener(FloatWindow.Builder b, FloatWindowManage floatWindowManage) {
        this.mBuilder = b;
        this.floatWindowManage = floatWindowManage;
        mSlop = ViewConfiguration.get(b.mApplicationContext).getScaledTouchSlop();
    }

    public boolean onInterceptTouchEvent(View v, MotionEvent event) {
        boolean isIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onMove = false;
                downX = event.getX();
                downY = event.getY();
                downRawX = event.getRawX();
                downRawY = event.getRawY();
                isStartMoveCallback = false;
                isIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //在一些dpi较高的设备上点击view很容易触发 ACTION_MOVE，所以此处做一个过滤
                isIntercept = (Math.abs(event.getRawX() - downRawX) > mSlop) || (Math.abs(event.getRawY() - downRawY) > mSlop);
                break;
            default:
                break;
        }
        return isIntercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onMove = false;
                downX = event.getX();
                downY = event.getY();
                cancelAnimator();
                break;
            case MotionEvent.ACTION_MOVE:
                int changeX = (int) (event.getRawX() - downX);
                int changeY = (int) (event.getRawY() - downY);
                floatWindowManage.updateLocation(changeX, changeY, true);
                onMove = (Math.abs(upX - downX) > mSlop) || (Math.abs(upY - downY) > mSlop);
                if (onMove && !isStartMoveCallback) {
                    isStartMoveCallback = true;
                    //回调移动开始
                    if (mBuilder.mViewStateListener != null) {
                        mBuilder.mViewStateListener.onMoveStart(floatWindowManage.getView().getView());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getRawX();
                upY = event.getRawY();
                if (isStartMoveCallback) {
                    //回调移动结束
                    if (mBuilder.mViewStateListener != null) {
                        mBuilder.mViewStateListener.onMoveEnd(floatWindowManage.getView().getView());
                    }
                }
                switch (mBuilder.mMoveType) {
                    case MoveType.SLIDE_ALL:
                    case MoveType.SLIDE_LEFT:
                    case MoveType.SLIDE_RIGHT:
                        int startX = floatWindowManage.getX();
                        int endX = 0;
                        if (mBuilder.mMoveType == MoveType.SLIDE_ALL) {
                            endX = (startX * 2 + v.getWidth() > Util.getScreenWidth(mBuilder.mApplicationContext)) ?
                                    Util.getScreenWidth(mBuilder.mApplicationContext) - v.getWidth() - mBuilder.mSlideRightMargin :
                                    mBuilder.mSlideLeftMargin;
                        } else if (mBuilder.mMoveType == MoveType.SLIDE_LEFT) {
                            endX = mBuilder.mSlideLeftMargin;
                        } else {
                            endX = Util.getScreenWidth(mBuilder.mApplicationContext) - v.getWidth() - mBuilder.mSlideRightMargin;
                        }
                        floatWindowManage.saveLocation(endX, -1);
                        mAnimator = ObjectAnimator.ofInt(startX, endX);
                        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int x = (int) animation.getAnimatedValue();
                                floatWindowManage.updateLocation(x, -1, false);

                            }
                        });
                        startAnimator();
                        break;
                    case MoveType.BACK:
                        PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", floatWindowManage.getX(), mBuilder.xOffset);
                        PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", floatWindowManage.getY(), mBuilder.yOffset);
                        floatWindowManage.saveLocation(mBuilder.xOffset, mBuilder.yOffset);
                        mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int x = (int) animation.getAnimatedValue("x");
                                int y = (int) animation.getAnimatedValue("y");
                                floatWindowManage.updateLocation(x, y, false);
                            }
                        });
                        startAnimator();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return onMove;
    }


    public void startAnimator() {
        if (mBuilder.mInterpolator == null) {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = new DecelerateInterpolator();
            }
            mBuilder.mInterpolator = mDecelerateInterpolator;
        }
        mAnimator.setInterpolator(mBuilder.mInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (mBuilder.mViewStateListener != null) {
                    mBuilder.mViewStateListener.onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mBuilder.mDuration).start();
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onMoveAnimStart();
        }
    }

    public void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }


}
