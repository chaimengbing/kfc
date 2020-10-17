package com.auv.annotation;

/**
 * @author LiChuang
 * @version 1.0
 * @ClassName PlatfromEnum
 * @Description
 **/
public enum PlatformEnum {

    UN_KNOW( Platform.ERROR, Platform.error ),

    STANDARD_BOARD( Platform.STANDARD_BOARD, Platform.standardBoard ),

    CAN_BOARD( Platform.CAN__BOARD, Platform.canBoard ),

    LOCK_CONTROL_BOARD( Platform.LOCK_CONTROL_BOARD, Platform.lockControlBoard );

    public String code;
    public String value;

    PlatformEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getBoardName(String status) {
        for (PlatformEnum taskStatus : values()) {
            if (status.equals( taskStatus.code )) {
                return taskStatus.value;
            }
        }
        return UN_KNOW.value;
    }

}
