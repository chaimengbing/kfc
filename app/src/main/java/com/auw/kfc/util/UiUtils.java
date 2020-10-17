package com.auw.kfc.util;

import android.view.MotionEvent;
import android.view.View;

public class UiUtils {

    public static void setAlphaChange(final View... views) {
        for (View view : views) {
            view.setOnTouchListener( new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.setAlpha( 0.6f );
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.setAlpha( 1.0f );
                            break;
                    }
                    return false;
                }
            } );
        }
    }


}
