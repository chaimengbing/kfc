package com.auw.kfc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.auw.kfc.R;
import com.auw.kfc.constant.Constant;
import com.auw.kfc.util.OpenSerialPortService;
import com.auw.kfc.util.ScanGunKeyEventHolder;
import com.auw.kfc.util.Utils;

import java.lang.ref.WeakReference;


public class QRCordActivity extends BaseActivity {

    private static final String TAG = QRCordActivity.class.getName();
    private ScanGunKeyEventHolder scanGunKeyEventHolder;
    private OpenSerialPortService openSerialPortService;

    private TextView openTips;
    private Button leftButton;
    private int count = 0;

    private CountDownHandler countDownHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_qrcord );
        initScanEvent();
        initSerialService();
        initView();
        initData();

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );
        initScanEvent();
        initSerialService();
        initData();
    }

    private void initData() {
        if (openSerialPortService != null) {
            String command = setSendData( Constant.QUERY_KFC, Constant.QUERY_ORDER );
            openSerialPortService.onSend( command );
        }

        countDownHandler = new CountDownHandler( this );
        Message message = new Message();
        message.obj = 59000;
        countDownHandler.sendMessageDelayed( message, 1000 );
        openTips.setText( getString( R.string.qrcodr_subhead ) + "59S" );
        openTips.setVisibility( View.VISIBLE );
    }

    private void initView() {
        leftButton = findViewById( R.id.bottom_left_button );
        openTips = findViewById( R.id.count_down_textview );
        leftButton.setVisibility( View.GONE );

        findViewById( R.id.bottom_right_button ).setOnClickListener( view -> startActivity( new Intent( getApplicationContext(), HomeActivity.class ) ) );

//        test();
    }

    private void test() {
        /**
         *
         * //格子打开成功
         *     public static final String CELL_OPEN_SUCCESS = "cell_open_success";
         *     //餐品还在制作
         *     public static final String NO_ORDER = "no_order";
         *     //没有餐品
         *     public static final String NO_MEALS = "no_meals";
         *     //机器/系统故障
         *     public static final String HARD_ERROR = "hard_error";
         *     //未能识别当前二维码
         *     public static final String QR_CODE_ERROR = "qr_code_error";
         *     //取餐码已经失效（已经取过餐了）
         *     public static final String ALREADY_TAKE_MEALS = "already_take_meals";
         *     //扫码失败
         *     public static final String SCAN_FAILED = "scan_failed";
         */
        findViewById( R.id.bottom_right_button ).setOnClickListener( view -> {
            int res = count % 8;
            Intent intent = new Intent( getApplicationContext(), ScanResultActivity.class );
            if (res == 0) {
                intent.putExtra( Constant.SCAN_RESULT, Constant.CELL_OPEN_SUCCESS );
            } else if (res == 1) {
                intent.putExtra( Constant.SCAN_RESULT, Constant.MAKEING_MEALS );
            } else if (res == 2) {
                intent.putExtra( Constant.SCAN_RESULT, Constant.HARD_ERROR );
            } else if (res == 3) {
                intent.putExtra( Constant.SCAN_RESULT, Constant.QR_CODE_ERROR );
            } else if (res == 4) {
                intent.putExtra( Constant.SCAN_RESULT, Constant.ALREADY_TAKE_MEALS );
            } else if (res == 5) {
                intent.putExtra( Constant.SCAN_RESULT, Constant.NO_MEALS );
            } else if (res == 6) {
                intent.putExtra( Constant.SCAN_RESULT, Constant.SCAN_FAILED );
            } else if (res == 7) {
                intent.putExtra( Constant.SCAN_RESULT, Constant.SCAN_FAILED );
            }
            startActivity( intent );
            count++;
        } );
    }

    private void initSerialService() {
        if (openSerialPortService == null) {
            openSerialPortService = new OpenSerialPortService( getApplicationContext(), data -> {
                Log.d( TAG, "收到KFC数据：" + data );
                JSONObject receiveData;
                try {
                    receiveData = JSONObject.parseObject( data );
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                for (String key : receiveData.keySet()) {
                    String res = receiveData.getString( Constant.SCAN_RESULT );
                    if (Constant.SCAN_RESULT.equals( key ) && !res.equals( Constant.HAVE_MEALS )) {
                        Intent intent = new Intent( getApplicationContext(), ScanResultActivity.class );
                        intent.putExtra( Constant.SCAN_RESULT, res );
                        startActivity( intent );
                    }
                }
            } );
        }
    }

    public static class CountDownHandler extends Handler {

        public final WeakReference<QRCordActivity> weakReference;

        public CountDownHandler(QRCordActivity activity) {
            weakReference = new WeakReference<>( activity );
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage( msg );
            QRCordActivity activity = weakReference.get();
            int value = (int) msg.obj;
            activity.getTextView().setText( activity.getString( R.string.qrcodr_subhead ) + String.valueOf( value / 1000 ) + "S" );
            msg = Message.obtain();
            msg.obj = value - 1000;
            if (value > 1000) {
                sendMessageDelayed( msg, 1000 );
            } else {
                activity.startActivity( new Intent( activity.getApplicationContext(), HomeActivity.class ) );
            }
        }
    }

    public TextView getTextView() {
        return openTips;
    }

    private void initScanEvent() {
        if (scanGunKeyEventHolder == null) {
            scanGunKeyEventHolder = new ScanGunKeyEventHolder();
            setScanGunKeyEventHolder();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return scanGunKeyEventHolder.analysisKeyEvent( event );
    }


    /**
     * 设置扫码事件监听
     */
    public void setScanGunKeyEventHolder() {
        //设置扫码枪扫描事件
        scanGunKeyEventHolder.setOnBarCodeCatchListener( barcode -> {
            Log.i( TAG, "barcode:" + barcode );
            handQRCode( barcode );
        } );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d( TAG, "onPause::" );
        if (countDownHandler != null) {
            countDownHandler.removeCallbacksAndMessages( null );
            countDownHandler = null;
        }
    }

    private void handQRCode(String barcode) {
        String[] scanResult = barcode.split( "-" );
        if (scanResult != null && scanResult.length > 0 && Utils.isKFCQRCode( scanResult )) {
            //门店编号
            String shop = scanResult[0];
            //订单日期
            String orderDate = scanResult[1];
            //订单序号
            String orderNum = scanResult[2];
            openSerialPortService.onSend( setSendData( Constant.QUERY_KFC, orderNum ) );

        } else {
            Intent intent = new Intent( getApplicationContext(), ScanResultActivity.class );
            intent.putExtra( Constant.SCAN_RESULT, Constant.QR_CODE_ERROR );
            startActivity( intent );

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d( TAG, "onDestroy::" );
        if (countDownHandler != null) {
            countDownHandler.removeCallbacksAndMessages( null );
            countDownHandler = null;
        }
    }

}
