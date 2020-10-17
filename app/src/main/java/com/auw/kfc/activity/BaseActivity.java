package com.auw.kfc.activity;

import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getWindow().setFormat( PixelFormat.TRANSLUCENT );
        requestWindowFeature( Window.FEATURE_NO_TITLE );//这里取消标题设置
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );//这里全屏显示

        // 隐藏底部导航栏 自带的标题
        hideBottomUIMenu();


    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //这里将自带的标题栏隐藏掉
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        fullscreen( false );
        //隐藏底部导航栏
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility( View.GONE );
        } else {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility( uiOptions );
        }
    }


    //是否全屏
    private void fullscreen(boolean enable) {
        if (enable) { //显示状态栏
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes( lp );
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
        } else { //隐藏状态栏
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes( lp );
            getWindow().clearFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
        }
    }

    /**
     * 发送数据的格式集成
     *
     * @param action
     * @param data
     * @return
     */
    public String setSendData(String action, String data) {
        JSONObject jsonObject = new JSONObject( true );
        jsonObject.put( action, data );
        return jsonObject.toJSONString();
    }
}
