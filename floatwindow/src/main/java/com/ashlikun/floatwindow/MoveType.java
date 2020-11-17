package com.ashlikun.floatwindow;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 移动类型
 */

public class MoveType {
    //不可拖动
    public static final int IN_ACTIVE = 1;
    //可拖动
    public static final int ACTIVE = 2;
    //可拖动，释放后自动贴边 （默认）
    public static final int SLIDE_ALL = 3;
    public static final int SLIDE_LEFT = 5;
    public static final int SLIDE_RIGHT = 6;
    //可拖动，释放后自动回到原位置
    public static final int BACK = 4;

    @IntDef({IN_ACTIVE, ACTIVE, SLIDE_ALL, SLIDE_LEFT, SLIDE_RIGHT, BACK})
    @Retention(RetentionPolicy.SOURCE)
    @interface MOVE_TYPE {
    }
}
