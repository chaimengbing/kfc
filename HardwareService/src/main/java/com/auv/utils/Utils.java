package com.auv.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Debug;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.auv.annotation.PackageAstrict;
import com.auv.constant.Constants;
import com.auv.hardware.InitHardwareService;
import com.auv.listener.OnHttpResponseListener;
import com.auv.model.DeviceInfo;
import com.auv.model.Result;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.content.Context.ACTIVITY_SERVICE;

public class Utils {

    private static Map<String, String> packageVerify;
    private final static String TAG = "Utils";

    static {
        packageVerify = new ConcurrentHashMap<>();

        packageVerify.put( PackageAstrict.Common.COM, PackageAstrict.Common.COM );

        packageVerify.put( PackageAstrict.AUV.Second.SECOND_PGE1, PackageAstrict.AUV.Second.SECOND_PGE1 );
        packageVerify.put( PackageAstrict.AUV.Second.SECOND_PGE2, PackageAstrict.AUV.Second.SECOND_PGE2 );
        packageVerify.put( PackageAstrict.KBS.Second.SECOND_PGE, PackageAstrict.KBS.Second.SECOND_PGE );

        packageVerify.put( PackageAstrict.AUV.Third.THIRD_PGE1, PackageAstrict.AUV.Third.THIRD_PGE1 );
        packageVerify.put( PackageAstrict.AUV.Third.THIRD_PGE2, PackageAstrict.AUV.Third.THIRD_PGE2 );
        packageVerify.put( PackageAstrict.KBS.Third.THIRD_PGE, PackageAstrict.KBS.Third.THIRD_PGE );
    }


    /**
     * 获取版本号
     */
    public static String getSDKVersion() {
        return Constants.SDK_VERSION_NAME;
    }

    /**
     * 获app版本号
     */
    public static int getAppVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo( context.getPackageName(), 0 );
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return code;
    }

    /**
     * 获app版本号
     */
    public static String getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo( context.getPackageName(), 0 );
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    /**
     * 转换内存类型
     */
    public static float convertSize(float size) {
        //定义GB的计算常量
        int GB = 1024 * 1024 * 1024;
        //定义MB的计算常量
        int MB = 1024 * 1024;
        //定义KB的计算常量
        int KB = 1024;
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            return (size / (float) GB);
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            return (size / (float) MB);
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            return (size / (float) KB);
        } else {
            return size;
        }
    }


    /**
     * 字节数组转为字符串
     *
     * @param bytes 字节数组
     * @return String
     */
    public static String bytes2HexStr(byte[] bytes) {
        final char chHex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        StringBuilder sb = new StringBuilder( bytes.length * 2 );

        for (byte b : bytes) {
            sb.append( chHex[(b & 0xf0) >>> 4] );
            sb.append( chHex[(b & 0x0f)] );
            sb.append( " " );
        }
        return sb.toString();
    }

    /**
     * 校验URL
     */
    public static boolean isURL(String str) {
        //转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"  //https、http、ftp、rtsp、mms
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return str.matches( regex );
    }

    /**
     * 判断文件是否存在,不存在则创建
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     */
    public static void checkFileExist(@NonNull String filePath, String fileName) {
        if (!TextUtils.isEmpty( filePath )) {
            File filePathExist = new File( filePath );
            if (!filePathExist.exists()) {
                AUVLogUtil.i( "checkFileExist", filePathExist.getPath() + " , mkdirs()：" + filePathExist.mkdirs() );
            }

            if (!TextUtils.isEmpty( fileName )) {
                File fileExist = new File( filePath + "/" + fileName );
                if (!fileExist.exists()) {
                    try {
                        AUVLogUtil.i( "checkFileExist", fileExist.getPath() + " , createNewFile()：" + fileExist.createNewFile() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * 判断文件是否存在,不存在则创建
     *
     * @param filePath 文件路径
     */
    public static void checkFileExist(@NonNull String filePath) {
        Utils.checkFileExist( filePath, "" );
    }


    /**
     * 时间戳  精确到秒
     */
    public static long timestamp() {
        return (System.currentTimeMillis() / 1000);
    }


    public static boolean verification(String pakge) {
        if (TextUtils.isEmpty( pakge )) {
            return false;
        }

        String[] pakges = pakge.split( "\\." );

        if (pakges.length < 3) {
            return false;
        }

        for (String value : pakges) {
            if (TextUtils.isEmpty( packageVerify.get( value ) )) {
                return false;
            }
        }

        return true;
    }

    public static String ByteConversionGBMBKB(int KSize) {
        //定义GB的计算常量
        int GB = 1024 * 1024 * 1024;
        //定义MB的计算常量
        int MB = 1024 * 1024;
        //定义KB的计算常量
        int KB = 1024;
        if (KSize / GB >= 1)//如果当前Byte的值大于等于1GB
            return (Math.round( KSize / (float) GB )) + "GB";//将其转换成GB
        else if (KSize / MB >= 1)//如果当前Byte的值大于等于1MB
            return (Math.round( KSize / (float) MB )) + "MB";//将其转换成MB
        else if (KSize / KB >= 1)//如果当前Byte的值大于等于1KB
            return (Math.round( KSize / (float) KB )) + "KB";//将其转换成KGB
        else
            return KSize + "Byte";//显示Byte值
    }

    /**
     * 监测内存情况
     */
    public static void checkMemory(Context context) {
        if (context == null)
            return;
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService( ACTIVITY_SERVICE );
            ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
            assert activityManager != null;
            activityManager.getMemoryInfo( info );
            //最大分配内存获取方法1
            int maxMemory = activityManager.getMemoryClass();

            //最大分配内存获取方法2
            float maxMemory2 = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));

            //当前分配的总内存
            float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));

            //剩余内存
            float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));

//            AUVLogUtil.d( TAG, "最大分配内存：" + maxMemory + " m" );
//            AUVLogUtil.d( TAG, "最大分配内存：" + maxMemory2 + " m" );
//            AUVLogUtil.d( TAG, "当前分配的总内存：" + totalMemory + " m" );
//            AUVLogUtil.d( TAG, "剩余内存：" + freeMemory + " m" );
//            AUVLogUtil.d( TAG, "系统剩余内存:" + (info.availMem >> 10) + "k" );
//            AUVLogUtil.d( TAG, "系统是否处于低内存运行：" + info.lowMemory );
//            AUVLogUtil.d( TAG, "当系统剩余内存低于" + info.threshold + "时就看成低内存运行" );
//            AUVLogUtil.d( TAG, "app占用内存 ： " + getRunningAppProcessInfo() );
//            AUVLogUtil.d( TAG, "app占用内存 ： " + getMemory() );
        } catch (Exception e) {
            AUVLogUtil.e( "MemoryMonitor", e.getMessage() );

        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getMemory() {
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo( memoryInfo );
        // dalvikPrivateClean + nativePrivateClean + otherPrivateClean;
        int totalPrivateClean = memoryInfo.getTotalPrivateClean();
        // dalvikPrivateDirty + nativePrivateDirty + otherPrivateDirty;
        int totalPrivateDirty = memoryInfo.getTotalPrivateDirty();
        // dalvikPss + nativePss + otherPss;
        int totalPss = memoryInfo.getTotalPss();
        // dalvikSharedClean + nativeSharedClean + otherSharedClean;
        int totalSharedClean = memoryInfo.getTotalSharedClean();
        // dalvikSharedDirty + nativeSharedDirty + otherSharedDirty;
        int totalSharedDirty = memoryInfo.getTotalSharedDirty();
        // dalvikSwappablePss + nativeSwappablePss + otherSwappablePss;
        int totalSwappablePss = memoryInfo.getTotalSwappablePss();
        int total = totalPrivateClean + totalPrivateDirty + totalPss + totalSharedClean + totalSharedDirty + totalSwappablePss;
        return Utils.ByteConversionGBMBKB( total );
    }

    /**
     * 获取app运行占用内存
     */
    public static String getRunningAppProcessInfo(Context context) {
        int totalMemory = 0;
        ActivityManager activityManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        //获得系bai统里正在运du行的所有进程zhi      
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
            // 进程ID号         
            int pid = runningAppProcessInfo.pid;
            // 用户ID  
            int uid = runningAppProcessInfo.uid;
            // 进程名          
            String processName = runningAppProcessInfo.processName;
            // 占用dao的内存         
            int[] pids = new int[]{pid};
            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo( pids );
            int memorySize = memoryInfo[0].dalvikPrivateDirty;
            totalMemory += memorySize;
        }
        return String.format( "%.2f", convertSize( totalMemory ) );
    }

    /**
     * 32位MD5加密
     */
    public static String MD5(String sourceStr) {
        return MD5( sourceStr, true );
    }


    /**
     * md5加密
     *
     * @param sourceStr 加密字段
     * @param is32      是否为32位
     */
    public static String MD5(@NonNull String sourceStr, boolean is32) {
        String result = "";//通过result返回结果值
        try {
            MessageDigest md = MessageDigest.getInstance( "MD5" );//1.初始化MessageDigest信息摘要对象,并指定为MD5不分大小写都可以
            md.update( sourceStr.getBytes() );//2.传入需要计算的字符串更新摘要信息，传入的为字节数组byte[],将字符串转换为字节数组使用getBytes()方法完成
            byte[] b = md.digest();//3.计算信息摘要digest()方法,返回值为字节数组
            int i;//定义整型
            //声明StringBuffer对象
            StringBuilder buf = new StringBuilder( "" );
            for (byte value : b) {
                i = value;//将首个元素赋值给i
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append( "0" );//前面补0
                buf.append( Integer.toHexString( i ) );//转换成16进制编码
            }
            result = buf.toString();//转换成字符串

            if (!is32) {
                result = buf.toString().substring( 8, 24 );
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;//返回结果
    }

    /**
     * 同步 SDK信息
     */
    public static void syncInfo(Context context) {
        //检查内存情况
        checkMemory( context );
        //  ping百度
        NetworkUtils.getInstance( context ).pingServer( Constants.AUV_PING_BAIDU );
        //ping阿里
        NetworkUtils.getInstance( context ).pingServer( Constants.AUV_PING_ALI );
        NetworkUtils.getInstance( context ).getCurrentNetDBM();

        String iccid = NetworkUtils.getInstance( context ).getIccid();
        if (TextUtils.isEmpty( iccid )) {
            syncInformation( context, "" );
        } else {
            NetworkUtils.getInstance( context ).queryCard( iccid );
        }
    }


    /**
     * 同步 SDK信息
     */
    public static Result syncInformation(Context context, String flow) {
        AUVLogUtil.d( "Utils", "syncInformation::" );

        //获取SDK版本号
        String sdkVersion = getSDKVersion();
        // 获取App版本号
        String appVersion = getAppVersion( context );
        // 获取流量卡号
        String ioTCardId;
        String iotId = SharedPreferenceHelper.getInstance( context ).getString( Constants.IOT_CARD_ID, "" );
        if (TextUtils.isEmpty( iotId )) {
            ioTCardId = NetworkUtils.getInstance( context ).getIccid();
        } else {
            ioTCardId = iotId;
        }
        //app内存占用
        String memory = getRunningAppProcessInfo( context );
        //系统版本号
        String osVersion = SystemUtils.getSystemVersion();
        // 获取网络类型
        String netType = NetworkUtils.getInstance( context ).getNetType();
        // 获取信号强度
        String dbm = NetworkUtils.getInstance( context ).getCurrentDbm();
        //阿里云ping值
        String pingAli = NetworkUtils.getInstance( context ).getPingAli();
        //百度ping值
        String pingBD = NetworkUtils.getInstance( context ).getPingBd();
        //设备已经安装的app
        List<String[]> installedApps = SystemUtils.getInstalledAppList( context, false );

        String snNum = SystemUtils.getSerialNumber();

        String latude = SharedPreferenceHelper.getInstance( context ).getString( "latitude", "" );
        String lotude = SharedPreferenceHelper.getInstance( context ).getString( "longitude", "" );
        String[] laLong = new String[2];
        laLong[0] = lotude;
        laLong[1] = latude;


        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setIotCardId( ioTCardId );
        deviceInfo.setSdkVersion( sdkVersion );
        deviceInfo.setAppVersion( appVersion );
        deviceInfo.setOsVersion( osVersion );
        deviceInfo.setMemory( memory );
        deviceInfo.setNetType( netType );
        deviceInfo.setPingAli( pingAli );
        deviceInfo.setPingBd( pingBD );
        deviceInfo.setDbm( dbm );
        deviceInfo.setFlow( flow );
        deviceInfo.setDeviceId( AliYunIotUtils.getInstance().getDeviceName() );
        deviceInfo.setInstalledAppArrays( installedApps );
        deviceInfo.setSerialNumber( snNum );
        deviceInfo.setLgdeAndlade( laLong );
        if (InitHardwareService.platformEnum != null && !TextUtils.isEmpty( InitHardwareService.platformEnum.code )) {
            deviceInfo.setPlatFrom( InitHardwareService.platformEnum.code );
        }


        AUVLogUtil.d( TAG, deviceInfo.toJsonString() );
        AUVHttpManager.getInstance( context ).postAuv( Constants.SYNC_SDK_INFO_URL, deviceInfo.toJsonString(), 0, new OnHttpResponseListener() {
            @Override
            public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                AUVLogUtil.d( TAG, "syncInformation::resultJson:" + resultJson );
            }

            @Override
            public void onHttpSuccess(int requestCode, String resultData, String resultMsg) {

            }

            @Override
            public void onHttpError(int resultCode, String resultMsg) {

            }
        } );
        return Result.success();
    }


    /**
     * 判断自己的应用程序是否为默认桌面
     */
    public static boolean isDefaultHome(Context context) {
        if (context == null) {
            return false;
        }
        Intent intent = new Intent( Intent.ACTION_MAIN );//Intent.ACTION_VIEW
        intent.addCategory( "android.intent.category.HOME" );
        intent.addCategory( "android.intent.category.DEFAULT" );
        PackageManager pm = context.getPackageManager();
        ResolveInfo info = pm.resolveActivity( intent, PackageManager.MATCH_DEFAULT_ONLY );
        boolean isDefault = context.getPackageName().equals( info.activityInfo.packageName );
        return isDefault;
    }

    /**
     * 获取string,为null则返回""
     *
     * @param tv
     * @return
     */
    public static String get(TextView tv) {
        if (tv == null || tv.getText() == null) {
            return "";
        }
        return tv.getText().toString();
    }

    /**
     * 获取string,为null则返回""
     *
     * @param object
     * @return
     */
    public static String get(Object object) {
        return object == null ? "" : object.toString();
    }

    /**
     * 获取string,为null则返回""
     *
     * @param cs
     * @return
     */
    public static String get(CharSequence cs) {
        return cs == null ? "" : cs.toString();
    }

    /**
     * 获取去掉前后空格后的string,为null则返回""
     *
     * @param tv
     * @return
     */
    public static String trim(TextView tv) {
        return trim( get( tv ) );
    }

    /**
     * 获取去掉前后空格后的string,为null则返回""
     *
     * @param object
     * @return
     */
    public static String trim(Object object) {
        return trim( get( object ) );
    }

    /**
     * 获取去掉前后空格后的string,为null则返回""
     *
     * @param cs
     * @return
     */
    public static String trim(CharSequence cs) {
        return get( cs );
    }

    /**
     * 获取去掉前后空格后的string,为null则返回""
     *
     * @param s
     * @return
     */
    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }


    /***********************   操作APP    ********************************************/
    /**
     * 执行 shell 命令
     *
     * @param cmd
     */
    public static void shellExec(String cmd) {
        Runtime mRuntime = Runtime.getRuntime();
        Process mProcess = null;
        BufferedReader mReader = null;
        StringBuffer mRespBuff = null;
        try {
            //Process中封装了返回的结果和执行错误的结果
            mProcess = mRuntime.exec( cmd );
            mReader = new BufferedReader( new InputStreamReader( mProcess.getInputStream() ) );
            mRespBuff = new StringBuffer();
            char[] buff = new char[1024];
            int ch = 0;
            while ((ch = mReader.read( buff )) != -1) {
                mRespBuff.append( buff, 0, ch );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mProcess != null) {
                    mProcess.destroy();
                }
                if (mReader != null) {
                    mReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mRespBuff != null) {
                mRespBuff = null;
            }
            if (mRuntime != null) {
                mRuntime.gc();
            }
        }
    }


    /**
     * 重启设备
     * 1.apk必须有系统签名
     * 2.必须声明权限   <uses-permission android:name="android.permission.REBOOT"/>
     */
    public static void rebootDevice() {
        shellExec( Constants.REBOOT );
    }


    /**
     * 重启app的方法
     */
    public static void rebootApp(Context context, String packageName) {
        restartApp( context, packageName );
        exitSelfAPP( context );
    }

    /**
     * 退出app的方法
     */
    public static void exitSelfAPP(Context context) {
        exitAPP( context, "" );
    }


    /**
     * 退出app的方法
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void exitAPP(Context context, String packageName) {
        if (!TextUtils.isEmpty( packageName )) {
            context = getPackageContext( context, packageName );
        }

        ActivityManager activityManager = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        List<ActivityManager.AppTask> appTaskList;
        if (activityManager != null) {
            appTaskList = activityManager.getAppTasks();
            for (ActivityManager.AppTask appTask : appTaskList) {
                appTask.finishAndRemoveTask();
            }
        }
    }

    /**
     * 获取打开app的上下文
     *
     * @param packageName 打开APP的包名
     */
    private static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals( packageName )) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext( packageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE );
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }


    /**
     * 重启本app
     */
    public static void restartApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage( packageName );
        if (intent != null) {
            PendingIntent restartIntent = PendingIntent.getActivity( context, 0, intent, PendingIntent.FLAG_ONE_SHOT );
            AlarmManager mgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
            if (mgr != null) {
                mgr.set( AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent ); // 1秒钟后重启应用
            }
        }
    }


    /**
     * 退出app
     *
     * @param packageName 需要关闭APP的包名
     *                    1.必须添加权限<uses-permission android:name="android.permission.FORCE_STOP_PACKAGES" />
     *                    2.必须进行系统签名
     */
    public static Result closeOtherApp(Context context, String packageName) {
        if (!checkPackInfo( context, packageName )) {
            AUVLogUtil.e( TAG, Constants.UNINSTALL );
            return Result.error( Constants.UNINSTALL );
        }

        ActivityManager am = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
        try {
            Method forceStopPackage;
            if (am == null) {
                AUVLogUtil.e( TAG, Constants.UNINSTALL );
                return Result.error( Constants.UNINSTALL );
            }
            forceStopPackage = am.getClass().getDeclaredMethod( "forceStopPackage", String.class );
            forceStopPackage.setAccessible( true );
            forceStopPackage.invoke( am, packageName );
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return Result.error( e.getMessage() );
        }
        return Result.success();

    }

    /**
     * 检查包是否存在
     *
     * @param packageName 需要检查的包名
     */
    public static boolean checkPackInfo(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo( packageName, 0 );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 打开某个app
     *
     * @param packageName 需要打开app的包名
     */
    public static Result openApp(Context context, String packageName) {
        if (!checkPackInfo( context, packageName )) {
            return Result.error( Constants.UNINSTALL );
        }

        Intent intent = getAppOpenIntentByPackageName( context, packageName );
        if (intent == null) {
            AUVLogUtil.e( TAG, Constants.UNINSTALL );
            return Result.error( Constants.UNINSTALL );
        }
        context.startActivity( intent );
        return Result.success();
    }

    /**
     * 卸载第三方app
     *
     * @param packageName 需要卸载的app包名
     * @return 卸载结果
     */
    public static Result unInstall(Context context, String packageName) {

        try {
            PackageManager pm = context.getPackageManager();
            Method[] methods = pm != null ? pm.getClass().getDeclaredMethods() : null;
            Method mDel = null;
            if (methods != null && methods.length > 0) {
                for (Method method : methods) {
                    if (method.getName().equals( "deletePackage" )) {
                        mDel = method;
                        break;
                    }
                }
            }
            if (mDel != null) {
                mDel.setAccessible( true );
                mDel.invoke( pm, packageName, null, 0 );
            }
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error( e.getMessage() );
        }
    }

    /**
     * 获取需要打卡APP的intent
     *
     * @param packageName 需要打开的app
     */
    @Nullable
    private static Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        //Activity完整名
        String mainAct = null;
        //根据包名寻找
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent( Intent.ACTION_MAIN );
        intent.addCategory( Intent.CATEGORY_LAUNCHER );
        intent.setFlags( Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK );
        @SuppressLint("WrongConstant")
        List<ResolveInfo> list = pkgMag.queryIntentActivities( intent, PackageManager.GET_ACTIVITIES );
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get( i );
            if (info.activityInfo.packageName.equals( packageName )) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty( mainAct )) {
            return null;
        }
        intent.setComponent( new ComponentName( packageName, mainAct ) );
        return intent;
    }


}
