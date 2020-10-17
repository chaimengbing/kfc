package com.auw.kfc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.auw.kfc.R;
import com.auw.kfc.constant.Constant;
import com.auw.kfc.util.OpenSerialPortService;
import com.auw.kfc.util.ScanGunKeyEventHolder;
import com.auw.kfc.util.UiUtils;
import com.auw.kfc.util.Utils;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getName();
    private ImageView scanImageView;
    private OpenSerialPortService openSerialPortService;
    private String receiverData = "";

    private ScanGunKeyEventHolder scanGunKeyEventHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        scanImageView = findViewById( R.id.scan_imageview );
//        UiUtils.setAlphaChange( scanImageView );

//        findViewById( R.id.scan_layout ).setOnClickListener( this );
//        scanImageView.setOnClickListener( this );

        initSerialService();
        initScanEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );
        initSerialService();
        initScanEvent();
    }

    private void initSerialService() {
        if (openSerialPortService == null) {
            openSerialPortService = new OpenSerialPortService( getApplicationContext(), data -> {
                Log.d( TAG, "收到KFC数据：" + data );
                receiverData = data;
                JSONObject receiveData;
                try {
                    receiveData = JSONObject.parseObject( data );
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                for (String key : receiveData.keySet()) {
                    String res = receiveData.getString( Constant.SCAN_RESULT );
                    if (res.equals( Constant.HAVE_MEALS )) {
                        startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) );
                        break;
                    } else if (!res.equals( Constant.HAVE_MEALS )) {
                        if (Constant.SCAN_RESULT.equals( key )) {
                            Intent intent = new Intent( getApplicationContext(), ScanResultActivity.class );
                            intent.putExtra( Constant.SCAN_RESULT, res );
                            startActivity( intent );
                            break;
                        }
                    }

                }
            } );
        }
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
            if (TextUtils.isEmpty( barcode )) {
                return;
            }
            Log.i( TAG, "barcode:" + barcode );
            handQRCode( barcode );
        } );
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


    private Handler receiverHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage( msg );
            if (TextUtils.isEmpty( receiverData )) {
                startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) );
            } else {
                startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) );
            }
            receiverHandler.removeMessages( 0 );
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.scan_layout || view.getId() == R.id.scan_imageview) {
            receiverHandler.sendEmptyMessageAtTime( 0, 500 );
            if (openSerialPortService != null) {
                String command = setSendData( Constant.QUERY_KFC, Constant.QUERY_ORDER );
                openSerialPortService.onSend( command );
            } else {
                startActivity( new Intent( getApplicationContext(), QRCordActivity.class ) );
            }
        }
    }
}
