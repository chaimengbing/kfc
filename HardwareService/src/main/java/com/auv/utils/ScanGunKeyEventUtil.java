package com.auv.utils;

import android.os.Handler;
import android.view.KeyEvent;

/**
 * 扫码枪事件解析类
 */
public class ScanGunKeyEventUtil {

    //延迟500ms，判断扫码是否完成。
    private final static long MESSAGE_DELAY = 500;
    //扫码内容
    private StringBuffer mStringBufferResult = new StringBuffer();
    //大小写区分
    private boolean isShift;
    private OnScanSuccessListener mOnScanSuccessListener;
    private Handler mHandler = new Handler();


    private final Runnable mScanningFishedRunnable = this::performScanSuccess;


    private static ScanGunKeyEventUtil scanGunKeyEventUtil;

    public static ScanGunKeyEventUtil getInstance() {
        if (scanGunKeyEventUtil == null) {
            scanGunKeyEventUtil = new ScanGunKeyEventUtil();
        }

        return scanGunKeyEventUtil;
    }

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

            char aChar = getInputCode( event );

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
            //按着shift键，表示大写
            //松开shift键，表示小写
            isShift = event.getAction() == KeyEvent.ACTION_DOWN;
        }
    }


    //获取扫描内容
    private char getInputCode(KeyEvent event) {

        int keyCode = event.getKeyCode();

        //其他符号
        switch (keyCode) {

            //数字键10个 + 符号10个
            case KeyEvent.KEYCODE_0:
                return isShift ? ')' : '0';
            case KeyEvent.KEYCODE_1:
                return isShift ? '!' : '1';
            case KeyEvent.KEYCODE_2:
                return isShift ? '@' : '2';
            case KeyEvent.KEYCODE_3:
                return isShift ? '#' : '3';
            case KeyEvent.KEYCODE_4:
                return isShift ? '$' : '4';
            case KeyEvent.KEYCODE_5:
                return isShift ? '%' : '5';
            case KeyEvent.KEYCODE_6:
                return isShift ? '^' : '6';
            case KeyEvent.KEYCODE_7:
                return isShift ? '&' : '7';
            case KeyEvent.KEYCODE_8:
                return isShift ? '*' : '8';
            case KeyEvent.KEYCODE_9:
                return isShift ? '(' : '9';

            //字母键26个小写 + 26个大写
            case KeyEvent.KEYCODE_A:
                return isShift ? 'A' : 'a';
            case KeyEvent.KEYCODE_B:
                return isShift ? 'B' : 'b';
            case KeyEvent.KEYCODE_C:
                return isShift ? 'C' : 'c';
            case KeyEvent.KEYCODE_D:
                return isShift ? 'D' : 'd';
            case KeyEvent.KEYCODE_E:
                return isShift ? 'E' : 'e';
            case KeyEvent.KEYCODE_F:
                return isShift ? 'F' : 'f';
            case KeyEvent.KEYCODE_G:
                return isShift ? 'G' : 'g';
            case KeyEvent.KEYCODE_H:
                return isShift ? 'H' : 'h';
            case KeyEvent.KEYCODE_I:
                return isShift ? 'I' : 'i';
            case KeyEvent.KEYCODE_J:
                return isShift ? 'J' : 'j';
            case KeyEvent.KEYCODE_K:
                return isShift ? 'K' : 'k';
            case KeyEvent.KEYCODE_L:
                return isShift ? 'L' : 'l';
            case KeyEvent.KEYCODE_M:
                return isShift ? 'M' : 'm';
            case KeyEvent.KEYCODE_N:
                return isShift ? 'N' : 'n';
            case KeyEvent.KEYCODE_O:
                return isShift ? 'O' : 'o';
            case KeyEvent.KEYCODE_P:
                return isShift ? 'P' : 'p';
            case KeyEvent.KEYCODE_Q:
                return isShift ? 'Q' : 'q';
            case KeyEvent.KEYCODE_R:
                return isShift ? 'R' : 'r';
            case KeyEvent.KEYCODE_S:
                return isShift ? 'S' : 's';
            case KeyEvent.KEYCODE_T:
                return isShift ? 'T' : 't';
            case KeyEvent.KEYCODE_U:
                return isShift ? 'U' : 'u';
            case KeyEvent.KEYCODE_V:
                return isShift ? 'V' : 'v';
            case KeyEvent.KEYCODE_W:
                return isShift ? 'W' : 'w';
            case KeyEvent.KEYCODE_X:
                return isShift ? 'X' : 'x';
            case KeyEvent.KEYCODE_Y:
                return isShift ? 'Y' : 'y';
            case KeyEvent.KEYCODE_Z:
                return isShift ? 'Z' : 'z';

            //符号键11个 + 11个
            case KeyEvent.KEYCODE_COMMA:
                return isShift ? '<' : ',';
            case KeyEvent.KEYCODE_PERIOD:
                return isShift ? '>' : '.';
            case KeyEvent.KEYCODE_SLASH:
                return isShift ? '?' : '/';
            case KeyEvent.KEYCODE_BACKSLASH:
                return isShift ? '|' : '\\';
            case KeyEvent.KEYCODE_APOSTROPHE:
                return isShift ? '\"' : '\'';
            case KeyEvent.KEYCODE_SEMICOLON:
                return isShift ? ':' : ';';
            case KeyEvent.KEYCODE_LEFT_BRACKET:
                return isShift ? '{' : '[';
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                return isShift ? '}' : ']';
            case KeyEvent.KEYCODE_GRAVE:
                return isShift ? '~' : '`';
            case KeyEvent.KEYCODE_EQUALS:
                return isShift ? '+' : '=';
            case KeyEvent.KEYCODE_MINUS:
                return isShift ? '_' : '-';
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