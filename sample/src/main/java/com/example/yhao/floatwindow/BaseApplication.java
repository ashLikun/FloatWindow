package com.example.yhao.floatwindow;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ashlikun.floatwindow.FloatWindow;
import com.ashlikun.floatwindow.MoveType;
import com.ashlikun.floatwindow.ViewStateListener;
import com.example.yhao.fixedfloatwindow.R;

/**
 * Created by yhao on 2017/12/18.
 * https://github.com/yhaolpz
 */

public class BaseApplication extends Application {


    private static final String TAG = "FloatWindow";

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            public FrameLayout layout = null;

            public FrameLayout initView() {
                if (layout == null) {
                    layout = new FrameLayout(getApplicationContext());
                    layout.setLayoutParams(new FrameLayout.LayoutParams(200, 200));
                    final ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageResource(R.drawable.icon);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    layout.addView(imageView, new FrameLayout.LayoutParams(-1, -1));
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout.getLayoutParams().width = 400;
                            imageView.requestLayout();
                        }
                    });
                }
                return layout;
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (FloatWindow.get() == null) {
                    FloatWindow
                            .with(activity)
                            .setView(initView())
                            .setMoveType(MoveType.SLIDE_LEFT)
                            .setMoveStyle(500, new BounceInterpolator())
                            .setDesktopWindow(true)
                            .setViewStateListener(mViewStateListener)
                            .build();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(BaseApplication.this, "onClick", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d(TAG, "onShow");
        }

        @Override
        public void onHide() {
            Log.d(TAG, "onHide");
        }

        @Override
        public void onDismiss() {
            Log.d(TAG, "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd");
        }

        @Override
        public void onMoveStart(View contentView) {
            Log.e("aaaaaaaaa", "onMoveStart");
        }

        @Override
        public void onMoveEnd(View contentView) {
            Log.e("aaaaaaaaa", "onMoveEnd");
        }


        @Override
        public void onSaveLocation(int x, int y) {

        }
    };
}
