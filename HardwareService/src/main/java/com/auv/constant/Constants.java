package com.auv.constant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Environment;

import com.auv.utils.AliYunIotUtils;

import java.text.SimpleDateFormat;

public class Constants {

    public final static String SDK_VERSION_NAME = "5.7.15";

    public final static String TRUE = "1";

    public final static String FALSE = "0";


    /**
     * 设备相关
     */
    //重启
    public final static String ACTION_RBT = "reboot";
    //安装
    public final static String ACTION_ITL = "install";
    //更新
    public final static String ACTION_UDT = "update";
    //卸载
    public final static String ACTION_UITL = "unInstall";
    //关闭app
    public final static String ACTION_CLOSE = "close";

    //打开app
    public final static String ACTION_OPEN = "open";
    //针对于app进行操作
    public final static String TARGET_APP = "APP";
    //针对于设备（平板电脑）进行操作
    public final static String TARGET_DVC = "DEVICE";
    //查询
    public final static String ACTION_QUERY = "query";
    //同步设备信息
    public final static String ACTION_SYNC_DEVICE_INFO = "sync_device_info";
    //上传当天日志
    public final static String CURRENT_LOG = "upload_current_log";
    //上传所有日志
    public final static String ALL_LOG = "upload_all_log";
    //同步格口信息
    public final static String ACTION_SYNC_CELL_INFO = "sync_cell_info";


    /**
     * 阿里相关
     */

    //设备服务功能响应后缀
    public final static String TOPIC_REPLAY = "_reply";


    /*
     连接中
     */
    public final static int CONNECTING = 100;

    /*
         已经连接
     */
    public final static int CONNECTED = 520;

    /*
         连接失败
     */
    public final static int CONNECTFAIL = 999;

    /*
         取消连接
     */
    public final static int DISCONNECTED = 987;


    /*
         未知
     */
    public final static int UNKOWN = -1;


    /*
      远程操作柜子
   */
    public final static String TP_NAME_OP_CELL = "operationCell";

    /*
      通用消息接口

     */
    public final static String TP_NAME_OP_COM = "commonMsg";


    /*
      异步返回结果
     */
    public final static String ANSY = "ansyResult";


    public static String replayConstructor(String topic) {
        return topic + AliYunIotUtils.TOPIC_REPLAY;
    }

    /*
              远程操作app

             */
    public static final String TP_NAME_OP_ACTION = "action";


    /*
     远程操作app
   */
    public static String action() {
        return AliYunIotUtils.AlI_CUSTOM_TOPIC + TP_NAME_OP_ACTION;
    }

    /*
     远程操作柜子
  */
    public static String opCell() {
        return AliYunIotUtils.AlI_SERVICE_TOPIC + TP_NAME_OP_CELL;
    }


    /*
     通用消息接口
   */
    public static String commonMsg() {
        return AliYunIotUtils.AlI_CUSTOM_TOPIC + TP_NAME_OP_COM;
    }


    public final static String EMPTY_LOG = "日志为空";
    public final static String DEVICE_ID_EMPTY = "设备Id为空";
    public final static String SUCCESS = "操作成功";
    public final static String FAIL = "操作失败";
    public final static String UNKONW_TARGET = "\"target\"类型未知";
    public final static String UNKONW_ACTION = "\"action\"类型未知";
    public final static String URL_INCORRECT = "URL格式错误";
    public final static String DATA_INCORRECT = "数据格式错误";
    public final static String INSTALL = "应用已安装";
    public final static String UNINSTALL = "应用未安装";
    public final static String MSG_ARRIVE = "消息到达";
    public final static String NOT_GUEST_USER = "非特约客户，验证失败";
    public final static String INIT_FAILURE = "实例化失败,控制板类型不正确";


    /*
          下载文件路径
        */
    public final static String FILE_DOWNLOAD_PATH = "/auv/download/";

    /*
        apk下载路径(相对路径)
     */
    public final static String RELATIVE_APK_DOWNLOAD_PATH = FILE_DOWNLOAD_PATH + "apk/";

    /*
        apk下载路径(绝对路径)
     */
    public final static String ABSOLUTE_APK_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + FILE_DOWNLOAD_PATH + "apk/";

    /*
         apk下载安装后缀名
      */
    public final static String APK_DOWNLOAD_NAME = ".apk";

    /*
        ping百度网址
     */
    public final static String AUV_PING_BAIDU = "www.baidu.com";

    /*
        ping阿里云网址
     */
    public final static String AUV_PING_ALI = "a1vzk7IR7Ez.iot-as-mqtt.cn-shanghai.aliyuncs.com";


    /*
        重启设备指令
     */
    public final static String REBOOT = "reboot";

    /*
        安装apkflag
     */
    public final static String ACTION_INSTALL_COMPLETE = "cm.android.intent.action.INSTALL_COMPLETE";

    /*
        目前项目中需要动态申请的所有权限
     */
    public final static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.MODIFY_PHONE_STATE
    };

        /*
            同步sdk信息的时间
            单位 :  秒
         */

    public final static int SYNC_SDK_INFO_TIME = 600;

          /*
            同步sdk信息的接口地址
         */

    public final static String SYNC_SDK_INFO_URL = "http://iot-console.58auv.com/device/dataAcquisition.json";

    /*
        流量查询秘钥
     */
    public final static String AUV_CLIENT_ID = "4028808f6c23f896016c40ccad0640b4";

    /*
        流量查询接口
     */
    public final static String AUV_SIM_CARD_URL = "http://cmp.zdm2m.com/smsApi.do?querycard";
    public final static String PTYPE = "流量套餐";


    //接收命令
    public final static String DOOR = "_door";
    public final static String LIGHT = "_light";
    public final static String WARM = "_warm";
    public final static String DISINFECT = "_disinfect";
    public final static String INDICATOR = "_indicator_light";

    //开
    public final static int OPEN = 1;
    //关
    public final static int CLOSE = 0;


    //日志上传
    public final static String STS_SERVER_URL = "https://plat.58auv.com/androidSTS";

    public final static String ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";

    public final static String BUCKET_NAME = "auv-android-log";

    // 日志文件在sdcard中的路径
    @SuppressLint("SdCardPath")
    public final static String AUV_LOG_PATH_SDCARD_DIR = "/sdcard/auv/log/";

    // 本类输出的日志文件名称
    public final static String AUV_LOG_NAME = "_auvlog.log";

    // 日志的输出格式
    @SuppressLint("SimpleDateFormat")
    public final static SimpleDateFormat AUV_LOG_SDF = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    // 日志文件格式
    @SuppressLint("SimpleDateFormat")
    public final static SimpleDateFormat AUV_LOG_FILE_SDF = new SimpleDateFormat( "yyyy-MM-dd" );


    public final static String ID = "id";
    public final static String ACTION = "action";
    public final static String TARGET = "target";
    public final static String DOWNLOAD_PATH = "downloadPath";
    public final static String DATA = "data";
    public final static String SESSION_ID = "sessionId";
    public final static String PACKAGE_NAME = "packageName";
    public final static String VERSION_CODE = "versionCode";
    public final static String VERSION_NAME = "versionName";
    public final static String DEVICE_ID = "deviceId";
    public final static String PLAT_FROM = "platFrom";
    public final static String SDK_VERSION = "sdkVersion";
    public final static String APP_VERSION = "appVersion";
    public final static String OS_VERSION = "osVersion";
    public final static String IOT_CARD_ID = "iotCardId";
    public final static String FLOW = "flow";
    public final static String MEMORY = "memory";
    public final static String NET_TYPE = "netType";
    public final static String DBM = "dbm";
    public final static String PING_ALI = "pingAli";
    public final static String PING_BD = "pingBd";
    public final static String INSTALL_APPS = "installedAppArrays";
    public final static String LATITUDE_LONGITUDE = "lgdeAndlade";
    public final static String SERIAL_NUMBER = "serialNumber";

}
