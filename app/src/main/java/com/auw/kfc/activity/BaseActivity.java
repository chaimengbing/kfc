package com.auw.kfc.activity;

import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.auv.annotation.PlatformEnum;
import com.auv.hardware.HardwareService;
import com.auv.hardware.InitHardwareService;
import com.auv.model.AUVBoardCellInit;
import com.auv.model.AUVErrorCode;
import com.auv.model.AUVErrorRecover;
import com.auv.utils.AUVLogUtil;
import com.auv.utils.AliYunIotUtils;
import com.auv.utils.SharedPreferenceHelper;
import com.auw.kfc.constant.Constant;
import com.auw.kfc.model.MessageEvent;
import com.auw.kfc.util.ServiceUtils;
import com.auw.kfc.util.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();

    public HardwareService hardwareControlService;
    private List<AUVBoardCellInit> boardInfoList;
    public SharedPreferenceHelper sharedPreferenceHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getWindow().setFormat( PixelFormat.TRANSLUCENT );
        requestWindowFeature( Window.FEATURE_NO_TITLE );//这里取消标题设置
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );//这里全屏显示

        // 隐藏底部导航栏 自带的标题
        hideBottomUIMenu();
        initCellState();
        initHardWare();


    }


    /**
     * 获取默认控制板与餐柜信息
     */
    private void initCellState() {
        if (boardInfoList == null) {
            boardInfoList = new ArrayList<>();
        }
        boardInfoList.clear();
        AUVBoardCellInit boardInfo = new AUVBoardCellInit();
        boardInfo.setBoardNo( 1 );
        boardInfo.setCellCount( 10 );
        boardInfoList.add( boardInfo );
    }


    public void initHardWare() {
        if (hardwareControlService != null && hardwareControlService.isStart()) {
            return;
        }
        try {
            hardwareControlService = InitHardwareService.getInstance( PlatformEnum.CAN_BOARD, getApplicationContext() );

            String serialPort = sharedPreferenceHelper.getString( Constant.KEY_SERIAL_PORT, "" );
            if (StringUtils.isNotEmpty( serialPort ) && ServiceUtils.isNotEmpty( boardInfoList )) {
                AUVLogUtil.d( TAG, "initHardWare::boardInfoList:" + boardInfoList.size() + ",serialPort:" + serialPort );
                hardwareControlService.startCouplingTransaction( serialPort, boardInfoList );
                AliYunIotUtils.getInstance().setHardwareService( hardwareControlService );
                hardwareControlService.setOnHardwareEventListener( new HardwareService.ResultListener() {
                    @Override
                    public void feedbackDoorClosedListener(int i) {
                        AUVLogUtil.d( TAG, "initHardWare::feedbackDoorClosed::cellNo:" + i );
                    }

                    @Override
                    public void onException(AUVErrorCode auvErrorCode) {
                        AUVLogUtil.d( TAG, "initHardWare::onException::code:" + auvErrorCode.getErrorCode() + ",errorMessage:" + auvErrorCode.getErrorMessage() );
                    }

                    @Override
                    public void onCommandResult(int cellNo, boolean success, String messageId) {
                        AUVLogUtil.d( TAG, "initHardWare::cellNo:" + cellNo + ",isSuccess:" + success );
                        if (success) {
                            EventBus.getDefault().post( new MessageEvent( MessageEvent.OPEN_CELL_SUCCESS, Integer.toString( cellNo ) ) );
                        } else {
                            EventBus.getDefault().post( new MessageEvent( MessageEvent.OPEN_CELL_FAIL, Integer.toString( cellNo ) ) );
                        }
                    }

                    @Override
                    public void onExceptionRecover(AUVErrorRecover auvErrorRecover) {
                        AUVLogUtil.d( TAG, "initHardWare::onExceptionRecover::code:" + auvErrorRecover.getErrorCode() + ",errorMessage:" + auvErrorRecover.getErrorMessage() );
                    }

                    @Override
                    public void cellStatusChangedListener(int cellNo, com.auv.annotation.SerialPortCommand serialPortCommand, boolean isOpen) {
                        AUVLogUtil.d( TAG, "initHardWare::cellStatusChangedListener::cellNo:" + cellNo + ",isOpen:" + isOpen );
                    }

                } );
            } else {
                AUVLogUtil.d( TAG, "not set serialport" );
            }
        } catch (Exception e) {
            hardwareControlService = null;
            AUVLogUtil.e( TAG, e );
        }

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

    /**
     * 提示框
     *
     * @param message 提示信息
     * @param isLong  是否长显示
     */
    public void showToast(final String message, boolean isLong) {
        runOnUiThread( () -> {
            int time = Toast.LENGTH_SHORT;
            if (isLong) {
                time = Toast.LENGTH_LONG;
            }
            try {
                Toast.makeText( getBaseContext(), message, time ).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } );
    }
}
