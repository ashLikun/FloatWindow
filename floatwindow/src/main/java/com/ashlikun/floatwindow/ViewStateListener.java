package com.ashlikun.floatwindow;

import android.view.View;

/**
 * 提供给外界的接口
 */
public interface ViewStateListener {
    void onPositionUpdate(int x, int y);

    /**
     * 保存数据
     *
     * @param x
     * @param y
     */
    void onSaveLocation(int x, int y);

    void onShow();

    void onHide();

    void onDismiss();

    /**
     * 动画开始和结束
     */
    void onMoveAnimStart();

    void onMoveAnimEnd();

    /**
     * 移动开始和结束
     */
    void onMoveStart(View contentView);

    void onMoveEnd(View contentView);
}
