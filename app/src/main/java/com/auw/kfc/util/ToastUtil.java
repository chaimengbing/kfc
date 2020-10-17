package com.auw.kfc.util;

import android.text.TextUtils;
import android.widget.Toast;

import com.auw.kfc.KFCApplication;

public class ToastUtil {
    private static Toast mToast;

    public static void showToast(String content) {
        if (TextUtils.isEmpty( content )) {
            return;
        }
        cancelToast();
        mToast = Toast.makeText( KFCApplication.getInstance(), content, Toast.LENGTH_LONG );
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
