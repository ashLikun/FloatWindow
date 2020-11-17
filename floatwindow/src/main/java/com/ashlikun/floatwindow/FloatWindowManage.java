package com.ashlikun.floatwindow;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * window的管理
 */

public class FloatWindowManage {
    protected final Context mContext;
    protected FloatWindow.Builder mBuilder;
    private FloatView mFloatView;
    private boolean isShow;
    //初始化
    private boolean once = true;
    private boolean isRemove = false;
    private int mX, mY;
    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mLayoutParams;


    FloatWindowManage(FloatWindow.Builder b) {
        this.mBuilder = b;
        this.mContext = b.mApplicationContext.getApplicationContext();
        createWindowManager(mContext);
    }

    /**
     * 初始化WindowManager
     *
     * @param context 有可能是Activity，只能用于创建WindowManager   getSystemService
     */
    public void createWindowManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = initLayoutParams();
        if (mFloatView == null) {
            mFloatView = new FloatView(mBuilder.mApplicationContext, this, mBuilder.mView);
        }
    }

    private WindowManager.LayoutParams initLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        if (true) {
//            layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//            layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        }

        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.format = PixelFormat.RGBA_8888;


        // 该类型不需要申请权限
        if (mBuilder.isDesktopWindow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                /*android7.0不能用TYPE_TOAST*/
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
                String packname = mContext.getPackageName();
                PackageManager pm = mContext.getPackageManager();
                boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
                if (permission) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                }
            }
        } else {
            //layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            ///layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            //此处layoutParams.type不建议使用TYPE_TOAST，因为在一些版本较低的系统中会出现拖动异常的问题，虽然它不需要权限
            // 高版本不支持
            //layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }

        //悬浮窗背景明暗度0~1，数值越大背景越暗，只有在flags设置了WindowManager.LayoutParams.FLAG_DIM_BEHIND 这个属性才会生效
//        layoutParams.dimAmount = 0.0f;
        //悬浮窗透明度0~1，数值越大越不透明
        if (mLayoutParams != null) {
            //复制相同的值
            layoutParams.dimAmount = mLayoutParams.dimAmount;
            layoutParams.alpha = mLayoutParams.alpha;
            layoutParams.x = mLayoutParams.x;
            layoutParams.y = mLayoutParams.y;
            layoutParams.width = mLayoutParams.width;
            layoutParams.height = mLayoutParams.height;
            layoutParams.height = mLayoutParams.height;
            layoutParams.gravity = mLayoutParams.gravity;
            mLayoutParams = layoutParams;
        } else {
            mLayoutParams = layoutParams;
            setAlpha(mBuilder.alpha);
            setSize(mBuilder.mWidth, mBuilder.mHeight);
            setGravity(mBuilder.gravity, mBuilder.xOffset, mBuilder.yOffset);
        }
        return layoutParams;
    }

    void removeView() {
        isRemove = true;
        mWindowManager.removeView(mFloatView);
        isShow = false;

        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onDismiss();
        }
    }

    void addView() {
        try {
            mWindowManager.addView(mFloatView, mLayoutParams);
        } catch (Exception e) {
            e.printStackTrace();
            mWindowManager.removeView(mFloatView);
            Util.e("FloatWindowManage addView 失败");
        }
    }

    /**
     * 获取窗口大小
     *
     * @return
     */
    public Point getDisplayPoint() {
        Point point = new Point();
        mWindowManager.getDefaultDisplay().getSize(point);
        return point;
    }

    public void setSize(int width, int height) {
        mLayoutParams.width = width;
        mLayoutParams.height = height;
    }

    public void setAlpha(float alpha) {
        mLayoutParams.alpha = alpha;
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        mLayoutParams.gravity = gravity;
        mLayoutParams.x = mX = xOffset;
        mLayoutParams.y = mY = yOffset;
    }

    /**
     * 这个方法用于不用权限的，不在桌面显示的，只在每个Activity内部显示
     *
     * @param context 可为null
     */
    public void show(Context context) {
        if (!mBuilder.isDesktopWindow) {
            if (!once) {
                mWindowManager.removeView(mFloatView);
            }
            createWindowManager(context);
            once = true;
        }
        show();

    }

    /**
     * 需要权限的显示
     */
    public void show() {
        if (once) {
            addView();
            once = false;
        } else {
            if (isShow) {
                return;
            }
            getView().setVisibility(View.VISIBLE);
        }
        isShow = true;
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onShow();
        }
    }

    public void hide() {
        if (once || !isShow) {
            return;
        }
        getView().setVisibility(View.INVISIBLE);
        isShow = false;
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onHide();
        }
    }

    public boolean isShowing() {
        return isShow;
    }

    public void onBackToDesktop() {
        if (!mBuilder.isDesktopWindow) {
            hide();
        }
    }


    public void destroy() {
        FloatWindow.destroy(mBuilder.mTag);
    }

    public void updateLocation(int x, int y, boolean isSaveData) {
        if (isRemove) {
            return;
        }
        if (x != -1) {
            mLayoutParams.x = mX = x;
        }
        if (y != -1) {
            mLayoutParams.y = mY = y;
        }
        mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
        if (isSaveData) {
            saveLocation(x, y);
        }
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onPositionUpdate(x, y);
        }

    }

    /**
     * 保存悬浮框的位置
     *
     * @param x
     * @param y
     */
    public void saveLocation(int x, int y) {
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onSaveLocation(x, y);
        }
    }

    public int getWindowX() {
        return mX;
    }

    public int getWindowY() {
        return mY;
    }


    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }


    public FloatView getView() {
        return mFloatView;
    }

    public FloatWindow.Builder getBuilder() {
        return mBuilder;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    public WindowManager getWindowManager() {
        return mWindowManager;
    }
}
