package com.ashlikun.floatwindow;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建悬浮窗
 *
 * 1：需要自己申请权限
 * 		 <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
 *
 * 		 FloatWindow
 *                 .with(activity)
 *                 .setView(initView())
 *                 .setMoveType(MoveType.SLIDE_LEFT)
 *                 .setMoveStyle(500, new BounceInterpolator())
 *                 .setDesktopWindow(true)
 * 				//监听事件
 *                 .setViewStateListener(mViewStateListener)
 *                 .build();
 * 	2：不需要申请权限
 * 		 .setDesktopWindow(false)
 * 		 且在每个Activity  registerActivityLifecycleCallbacks
 *
 *             @Override
 *             public void onActivityResumed(Activity activity) {
 *                 FloatWindow.get().show(activity);
 *             }
 *
 * 	3：获取任意一个FloatWindowManage    ->FloatWindow.get()或者 FloatWindow.get("aaaa")
 *
 * 	4:销毁
 *
 * 		FloatWindow.get().destroy();
 *
 *
 * 	5:  在移动开始和过程和结束的时候不要改变window大小，这样会卡顿
 *
 */

public class FloatWindow {

    private FloatWindow() {

    }

    private static final String mDefaultTag = "default_float_window_tag";
    private static Map<String, FloatWindowManage> mFloatWindowMap;

    public static FloatWindowManage get() {
        return get(mDefaultTag);
    }

    public static FloatWindowManage get(@NonNull String tag) {
        return mFloatWindowMap == null ? null : mFloatWindowMap.get(tag);
    }

    /**
     * 如果应用内显示这里得传Activity,内部用完会销毁,不会内存泄漏
     *
     * @param context
     * @return
     */
    @MainThread
    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public static void destroy() {
        destroy(mDefaultTag);
    }

    public static void destroy(String tag) {
        if (mFloatWindowMap == null || !mFloatWindowMap.containsKey(tag)) {
            return;
        }
        mFloatWindowMap.get(tag).removeView();
        mFloatWindowMap.remove(tag);
    }

    public static class Builder {
        Context mApplicationContext;
        View mView;
        int mLayoutId;
        //桌面显示
        boolean isDesktopWindow = true;
        //悬浮窗透明度0~1，数值越大越不透明
        float alpha = 1;
        int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int gravity = Gravity.TOP | Gravity.START;
        int xOffset;
        int yOffset;
        int mMoveType = MoveType.SLIDE_ALL;
        int mSlideLeftMargin;
        int mSlideRightMargin;
        long mDuration = 300;
        TimeInterpolator mInterpolator;
        String mTag = mDefaultTag;
        ViewStateListener mViewStateListener;

        private Builder() {

        }

        /**
         * 如果应用内显示这里得传Activity,内部用完会销毁,不会内存泄漏
         *
         * @param context
         * @return
         */
        Builder(Context context) {
            this.mApplicationContext = context.getApplicationContext();
        }

        public Builder setView(@NonNull View view) {
            mView = view;
            return this;
        }

        public Builder setAlpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * 是否需要在左面显示，
         *
         * @param isDesktopWindow true ：需要权限
         * @return
         */
        public Builder setDesktopWindow(boolean isDesktopWindow) {
            this.isDesktopWindow = isDesktopWindow;
            return this;
        }

        public Builder setView(@LayoutRes int layoutId) {
            mLayoutId = layoutId;
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public Builder setY(int y) {
            yOffset = y;
            return this;
        }


        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType) {
            return setMoveType(moveType, 0, 0);
        }


        /**
         * 设置带边距的贴边动画，只有 moveType 为 MoveType.slide，设置边距才有意义，这个方法不标准，后面调整
         *
         * @param moveType         贴边动画 MoveType.slide
         * @param slideLeftMargin  贴边动画左边距，默认为 0
         * @param slideRightMargin 贴边动画右边距，默认为 0
         */
        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType, int slideLeftMargin, int slideRightMargin) {
            mMoveType = moveType;
            mSlideLeftMargin = slideLeftMargin;
            mSlideRightMargin = slideRightMargin;
            return this;
        }

        public Builder setMoveStyle(long duration, @Nullable TimeInterpolator interpolator) {
            mDuration = duration;
            mInterpolator = interpolator;
            return this;
        }

        public Builder setTag(@NonNull String tag) {
            mTag = tag;
            return this;
        }

        /**
         * 监听事件
         */
        public Builder setViewStateListener(ViewStateListener listener) {
            mViewStateListener = listener;
            return this;
        }

        public void build() {
            if (mFloatWindowMap == null) {
                mFloatWindowMap = new HashMap<>();
            }
            if (mFloatWindowMap.containsKey(mTag)) {
                throw new IllegalArgumentException("FloatWindow of this tag has been added, Please set a new tag for the new FloatWindow");
            }
            if (mView == null && mLayoutId == 0) {
                throw new IllegalArgumentException("View has not been set!");
            }
            if (mView == null) {
                mView = Util.inflate(mApplicationContext, mLayoutId);
            }
            FloatWindowManage floatWindowImpl = new FloatWindowManage(this);
            mFloatWindowMap.put(mTag, floatWindowImpl);
        }

    }
}
