package com.auw.kfc.util;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;

/**
 * @author LiChuang
 * @version 1.0
 * @ClassName OpenSerialPortService
 * @description 此方法用来打开SY1串口进行两设备之间的通讯
 * @since 2019/11/29 9:33
 **/
public class OpenSerialPortService implements OnOpenSerialPortListener {

    private Context context;
    private final String TAG = "OpenSerialPortService";
    private SerialPortManager mSerialPortManager;
    //双屏通讯地址
    private static final String SERIAL_PORT = "/dev/ttyS1";
    //波特率
    private static final int BAUD_RATE = 115200;

    public static StringBuffer stringBuffer = new StringBuffer();
    public static String suffix = "《@》";

    public interface OnSerialPortReceiveDataListener {
        void onSerialPortReceiveData(String data);
    }

    public OpenSerialPortService(Context context, OnSerialPortReceiveDataListener listener) {
        this.context = context;
        mSerialPortManager = new SerialPortManager();
        // 打开串口
        boolean openSerialPort = mSerialPortManager.setOnOpenSerialPortListener( this )
                .setOnSerialPortDataListener( new OnSerialPortDataListener() {
                    @Override
                    public void onDataReceived(byte[] bytes) {
                        String data = new String( bytes );
                        stringBuffer.append( data );
                        if (data.contains( suffix )) {

                            data = stringBuffer.toString().replace( suffix, "" );
                            stringBuffer = new StringBuffer();

                            listener.onSerialPortReceiveData( data );
                        }
                    }

                    @Override
                    public void onDataSent(byte[] bytes) {
                        String data = new String( bytes );
                        Log.i( TAG, String.format( "发送\n\n%s", data ) );
                    }
                } )
                .openSerialPort( new File( SERIAL_PORT ), BAUD_RATE );
        Log.i( TAG, "onCreate: openSerialPort = " + openSerialPort );
    }

    @Override
    public void onSuccess(File device) {
//        Toast.makeText(context, String.format("串口 [%s] 打开成功", device.getPath()), Toast.LENGTH_SHORT).show();
    }

    public void closeSerialPort() {
        if (mSerialPortManager != null)
            mSerialPortManager.closeSerialPort();
    }

    /**
     * 串口打开失败
     *
     * @param device 串口
     * @param status status
     */
    @Override
    public void onFail(File device, Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                showDialog( device.getPath(), "没有读写权限" );
                break;
            case OPEN_FAIL:
            default:
                showDialog( device.getPath(), "串口打开失败" );
                break;
        }
    }

    /**
     * 显示提示框
     *
     * @param title   title
     * @param message message
     */
    private void showDialog(String title, String message) {
        new AlertDialog.Builder( context )
                .setTitle( title )
                .setMessage( message )
                .setPositiveButton( "退出", (dialog, id) -> dialog.dismiss() )
                .setCancelable( false )
                .create()
                .show();
    }

    /**
     * 发送数据
     */
    public void onSend(String sendContent) {
        byte[] sendContentBytes = sendContent.getBytes();
        boolean sendBytes = mSerialPortManager.sendBytes( sendContentBytes );
        Log.i( TAG, "onSend: sendBytes = " + sendBytes );
        Log.i( TAG, sendBytes ? "发送成功" : "发送失败" );
    }

}
