package com.auw.kfc.constant;

public class Constant {
    //查询标识
    public static final String QUERY_KFC = "QueryKfc";
    public static final String QUERY_ORDER = "QueryOrder";

    /**
     * KFC扫码结果通知
     * 1、餐品还在制作
     * 2、柜中没有可取的餐品
     * 3、机器/系统故障
     * 4、未能识别当前二维码
     * 5、取餐码已经失效（已经取过餐了）
     */
    //返回结果的key
    public static final String SCAN_RESULT = "scan_result";
    //格子打开成功
    public static final String CELL_OPEN_SUCCESS = "cell_open_success";
    //餐品还在制作
    public static final String MAKEING_MEALS = "makeing_meals";
    //没有餐品
    public static final String NO_MEALS = "no_meals";
    public static final String HAVE_MEALS = "have_meals";
    public static final String CELL_FULL = "cell_full";
    //机器/系统故障
    public static final String HARD_ERROR = "hard_error";
    //未能识别当前二维码
    public static final String QR_CODE_ERROR = "qr_code_error";
    //取餐码已经失效（已经取过餐了）
    public static final String ALREADY_TAKE_MEALS = "already_take_meals";
    //扫码失败
    public static final String SCAN_FAILED = "scan_failed";
    public static final String SCAN_MANY_FAILED = "scan_many_failed";


    /***********************  客户信息 *****************************/
    public final static String HE_ZE = "66462";   //菏泽
    public final static String FRONT = "F";//前门扫码器
    public final static String BACK = "B";//后门扫码器
    public final static String TYPE_SMALL = "small";
    public final static String TYPE_BIG = "big";
}
