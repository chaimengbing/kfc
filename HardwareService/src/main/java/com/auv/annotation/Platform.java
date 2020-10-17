package com.auv.annotation;

/**
 * @author LiChuang
 * @version 1.0
 * @ClassName Platform
 * @Description TODO 类描述
 * @since 2020/2/4 15:11
 **/
public @interface Platform {

    /**
     * 板子标识
     */
    String ERROR = "0";

    String STANDARD_BOARD = "1";

    String CAN__BOARD = "2";

    String LOCK_CONTROL_BOARD = "3";



    /**
     * 板子名称
     */
    String error = "未知板";

    String standardBoard = "标准版+转发版";

    String canBoard = "CAN板";

    String lockControlBoard = "锁控板";

}
