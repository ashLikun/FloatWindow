[![Release](https://jitpack.io/v/ashLikun/FloatWindow.svg)](https://jitpack.io/#ashLikun/FloatWindow)

FloatWindow项目简介
    
	悬浮框的库


8.链式调用，简洁清爽
## 使用方法

build.gradle文件中添加:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
并且:

```gradle
dependencies {
    implementation 'com.github.ashLikun:FloatWindow:{latest version}'
}
```

## 详细介绍

	1：需要自己申请权限
		 <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
	
		 FloatWindow
                .with(activity)
                .setView(initView())
                .setMoveType(MoveType.SLIDE_LEFT)
                .setMoveStyle(500, new BounceInterpolator())
                .setDesktopWindow(true)
				//监听事件
                .setViewStateListener(mViewStateListener)
                .build();
	2：不需要申请权限
		 .setDesktopWindow(false)
		 且在每个Activity  registerActivityLifecycleCallbacks
		 
            @Override
            public void onActivityResumed(Activity activity) {
                FloatWindow.get().show(activity);
            }
			
	3：获取任意一个FloatWindowManage    ->FloatWindow.get()或者 FloatWindow.get("aaaa")
	
	4:销毁
	
		FloatWindow.get().destroy();
	
	5:如果想实现不同