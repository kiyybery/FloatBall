package com.xyn.floatball;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xyn.floatballsdk.FloatBallManager;
import com.xyn.floatballsdk.floatball.FloatBallCfg;
import com.xyn.floatballsdk.menu.FloatMenuCfg;
import com.xyn.floatballsdk.menu.MenuItem;
import com.xyn.floatballsdk.permission.FloatPermissionManager;
import com.xyn.floatballsdk.utils.BackGroudSeletor;
import com.xyn.floatballsdk.utils.DensityUtil;
import com.xyn.floatballsdk.utils.FileUtil;

public class MainActivity extends AppCompatActivity {

    private FloatBallManager mFloatballManager;
    private FloatPermissionManager mFloatPermissionManager;
    private ActivityLifeCycleListener mActivityLifeCycleListener = new ActivityLifeCycleListener();
    private int resumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean showMenu = true;
        initBall(showMenu);
        if (mFloatballManager.getMenuItemSize() == 0) {
            mFloatballManager.setOnFloatBallClickListener(new FloatBallManager.OnFloatBallClickListener() {
                @Override
                public void onFloatBallClick() {
                    Toast.makeText(getApplicationContext(), "点击了悬浮球", Toast.LENGTH_LONG).show();
                }
            });
        }

        getApplication().registerActivityLifecycleCallbacks(mActivityLifeCycleListener);
    }

    private void initBall(boolean showMenu) {
        int ballSize = DensityUtil.dip2px(this, 45);
        Drawable ballIcon = BackGroudSeletor.getdrawble("iv_meun_more", this);
        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_CENTER);
        //设置悬浮球不半隐藏
        ballCfg.setHideHalfLater(false);
        if (showMenu) {
            //2 需要显示悬浮菜单
            //2.1 初始化悬浮菜单配置，有菜单item的大小和菜单item的个数
            int menuSize = DensityUtil.dip2px(this, 180);
            int menuItemSize = DensityUtil.dip2px(this, 40);
            FloatMenuCfg menuCfg = new FloatMenuCfg(menuSize, menuItemSize);
            //3 生成floatballManager
            mFloatballManager = new FloatBallManager(getApplicationContext(), ballCfg, menuCfg);
            addFloatMenuItem();
        } else {
            mFloatballManager = new FloatBallManager(getApplicationContext(), ballCfg);
        }

        setFloatPermission();
    }

    private void setFloatPermission() {
        // 设置悬浮球权限，用于申请悬浮球权限的，这里用的是别人写好的库，可以自己选择
        //如果不设置permission，则不会弹出悬浮球
        mFloatPermissionManager = new FloatPermissionManager();
        mFloatballManager.setPermission(new FloatBallManager.IFloatBallPermission() {
            @Override
            public boolean onRequestFloatBallPermission() {
                requestFloatBallPermission(MainActivity.this);
                return true;
            }

            @Override
            public boolean hasFloatBallPermission(Context context) {
                return mFloatPermissionManager.checkPermission(context);
            }

            @Override
            public void requestFloatBallPermission(Activity activity) {
                mFloatPermissionManager.applyPermission(activity);
            }

        });
    }

    private void addFloatMenuItem() {
        MenuItem deleteItem = new MenuItem(BackGroudSeletor.getdrawble("iv_delete_file", this)) {
            @Override
            public void action() {
                mFloatballManager.closeMenu();
            }
        };

        MenuItem findItem = new MenuItem(BackGroudSeletor.getdrawble("iv_find_file", this)) {
            @Override
            public void action() {
                Toast.makeText(getApplicationContext(), "查看文件夹", Toast.LENGTH_LONG).show();
                mFloatballManager.closeMenu();
            }
        };

        mFloatballManager.addMenuItem(deleteItem)
                .addMenuItem(findItem)
                .buildMenu();
    }

    private void setFloatballVisible(boolean visible) {
        if (visible) {
            mFloatballManager.show();
        } else {
            mFloatballManager.hide();
        }
    }

    public boolean isApplicationInForeground() {
        return resumed > 0;
    }

    public class ActivityLifeCycleListener implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            ++resumed;
            setFloatballVisible(true);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            --resumed;
            if (!isApplicationInForeground()) {
                setFloatballVisible(false);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }
    }

}
