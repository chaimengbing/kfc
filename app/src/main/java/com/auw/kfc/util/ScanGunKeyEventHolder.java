package com.auw.kfc.util;

import android.os.Handler;
import android.view.KeyEvent;

/**
 * 扫码枪事件解析类
 */
public class ScanGunKeyEventHolder {

    //延迟500ms，判断扫码是否完成。
    private final static long MESSAGE_DELAY = 500;
    //扫码内容
    private StringBuffer mStringBufferResult = new StringBuffer();
    //大小写区分
    private boolean mCaps;
    private OnScanSuccessListener mOnScanSuccessListener;
    private Handler mHandler = new Handler();

    private final Runnable mScanningFishedRunnable = this::performScanSuccess;

    //返回扫描结果
    private void performScanSuccess() {
        String barcode = mStringBufferResult.toString();
        if (mOnScanSuccessListener != null)
            mOnScanSuccessListener.onScanSuccess( barcode );
        mStringBufferResult.setLength( 0 );
    }

    //key事件处理
    public boolean analysisKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        //字母大小写判断
        checkLetterStatus( event );
        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            char aChar = getInputCode( mCaps, event.getKeyCode() );

            if (aChar != 0) {
                mStringBufferResult.append( aChar );
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //若为回车键，直接返回
                mHandler.removeCallbacks( mScanningFishedRunnable );
                mHandler.post( mScanningFishedRunnable );
            } else {
                //延迟post，若500ms内，有其他事件
                mHandler.removeCallbacks( mScanningFishedRunnable );
                mHandler.postDelayed( mScanningFishedRunnable, MESSAGE_DELAY );
            }

            return true;
        }
        return false;
    }


    //检查shift键
    private void checkLetterStatus(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                //按着shift键，表示大写
                mCaps = true;
            } else {
                //松开shift键，表示小写
                mCaps = false;
            }
        }
    }


    /**
     * 将keyCode转为char
     *
     * @param caps    是不是大写
     * @param keyCode 按键
     * @return 按键对应的char
     */
    private char getInputCode(boolean caps, int keyCode) {
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            return (char) ((caps ? 'A' : 'a') + keyCode - KeyEvent.KEYCODE_A);
        } else {
            return keyValue( caps, keyCode );
        }
    }

    /**
     * 按键对应的char表
     */
    private char keyValue(boolean caps, int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                return caps ? ')' : '0';
            case KeyEvent.KEYCODE_1:
                return caps ? '!' : '1';
            case KeyEvent.KEYCODE_2:
                return caps ? '@' : '2';
            case KeyEvent.KEYCODE_3:
                return caps ? '#' : '3';
            case KeyEvent.KEYCODE_4:
                return caps ? '$' : '4';
            case KeyEvent.KEYCODE_5:
                return caps ? '%' : '5';
            case KeyEvent.KEYCODE_6:
                return caps ? '^' : '6';
            case KeyEvent.KEYCODE_7:
                return caps ? '&' : '7';
            case KeyEvent.KEYCODE_8:
                return caps ? '*' : '8';
            case KeyEvent.KEYCODE_9:
                return caps ? '(' : '9';
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
                return '-';
            case KeyEvent.KEYCODE_MINUS:
                return mCaps ? '_' : '-';
            case KeyEvent.KEYCODE_EQUALS:
                return '=';
            case KeyEvent.KEYCODE_NUMPAD_ADD:
                return '+';
            case KeyEvent.KEYCODE_GRAVE:
                return caps ? '~' : '`';
            case KeyEvent.KEYCODE_BACKSLASH:
                return caps ? '|' : '\\';
            case KeyEvent.KEYCODE_LEFT_BRACKET:
                return caps ? '{' : '[';
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                return caps ? '}' : ']';
            case KeyEvent.KEYCODE_SEMICOLON:
                return caps ? ':' : ';';
            case KeyEvent.KEYCODE_APOSTROPHE:
                return caps ? '"' : '\'';
            case KeyEvent.KEYCODE_COMMA:
                return caps ? '<' : ',';
            case KeyEvent.KEYCODE_PERIOD:
                return caps ? '>' : '.';
            case KeyEvent.KEYCODE_SLASH:
                return caps ? '?' : '/';
            default:
                return 0;
        }
    }

    public interface OnScanSuccessListener {
        void onScanSuccess(String barcode);
    }

    public void setOnBarCodeCatchListener(OnScanSuccessListener onScanSuccessListener) {
        mOnScanSuccessListener = onScanSuccessListener;
    }

    public void onDestroy() {
        mHandler.removeCallbacks( mScanningFishedRunnable );
        mOnScanSuccessListener = null;
    }

}