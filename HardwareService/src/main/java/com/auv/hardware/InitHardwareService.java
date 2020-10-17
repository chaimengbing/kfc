package com.auv.hardware;

import android.content.Context;

import com.auv.annotation.PlatformEnum;
import com.auv.constant.Constants;
import com.auv.utils.NetworkUtils;
import com.auv.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @version 1.0
 * @Description 初始化硬件控制层
 **/
public class InitHardwareService {
    public static HardwareService hardwareService;
    public static PlatformEnum platformEnum;

    public static HardwareService getInstance(PlatformEnum platformEnum, Context context) {
        if (platformEnum == null) {
            throw new UnknownError( "实例化失败,请选择控制板类型" );
        }
            /*
            申请SDK必须的权限
         */
        NetworkUtils.getInstance( context ).applyRight();
        Package mPackage = context.getClass().getPackage();
        if (mPackage == null || !Utils.verification( mPackage.getName() )) {
            throw new UnknownError( "非特约客户，验证失败" );
        }

        switch (platformEnum) {
            case STANDARD_BOARD:
                hardwareService = StandardBoardService.getInstance( context );
                break;
            case CAN_BOARD:
                hardwareService = CanBoardService.getInstance( context );
                break;
            case UN_KNOW:
                throw new UnknownError( "实例化失败,控制板类型不正确" );
        }
        InitHardwareService.platformEnum = platformEnum;

        return hardwareService;
    }

}
