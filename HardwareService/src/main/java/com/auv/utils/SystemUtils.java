package com.auv.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.auv.constant.Constants;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 系统工具类
 */
public class SystemUtils {

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService( Activity.TELEPHONY_SERVICE );
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission( ctx, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED) {
                NetworkUtils.getInstance( ctx ).applyRight();
                return "";
            }
            return tm.getDeviceId();
        }
        return null;
    }


    /**
     * 获取手机序列号
     *
     * @return 手机序列号
     */
    @SuppressLint("MissingPermission")
    public static String getSerialNumber() {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
                serial = Build.getSerial();

            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = android.os.Build.SERIAL;

            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {//8.0-
                Class<?> c = Class.forName( "android.os.SystemProperties" );
                Method get = c.getMethod( "get", String.class );
                serial = (String) get.invoke( c, "ro.serialno" );
            } else {

                serial = android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AUVLogUtil.e( "e", "读取设备序列号异常：" + e.toString() );
        }
        return serial;
    }


    /**
     * 申请有关手机信号的权限
     */
    public void applyRight(Context context, String... permissions) {
        for (String permission : permissions) {
            int i = ContextCompat.checkSelfPermission( context, permission );
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions( (Activity) context, permissions, 321 );
            }
        }
    }


    /**
     * 获取已经安装的app
     *
     * @param needSystem 是不是需要系统应用
     */
    public static List<String[]> getInstalledAppList(Context context, boolean needSystem) {

        List<String[]> installedApps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 查询所有已经安装的应用程序
        List<PackageInfo> packageInfos = pm.getInstalledPackages( PackageManager.GET_ACTIVITIES );
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent( Intent.ACTION_MAIN, null );
        resolveIntent.addCategory( Intent.CATEGORY_LAUNCHER );
        // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities( resolveIntent, 0 );
        Set<String> allowPackages = new HashSet<>();
        for (ResolveInfo resolveInfo : resolveinfoList) {
            allowPackages.add( resolveInfo.activityInfo.packageName );
        }

        for (PackageInfo packageInfo : packageInfos) {
            ApplicationInfo app = packageInfo.applicationInfo;
            if (!needSystem) {
                // (app.flags & ApplicationInfo.FLAG_SYSTEM) == 0    表示应用为用户应用
                // (app.flags & ApplicationInfo.FLAG_SYSTEM) == 1    表示应用为系统应用

                // uid > 10000   表示应用为用户应用
                // uid <= 10000    表示应用为系统应用

                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1 && app.uid < 10000) {
                    continue;
                }
            }

            if (allowPackages.contains( app.packageName )) {

                //得到手机上已经安装的应用的名字,即在AndriodMainfest.xml中的app_name。
                String appName = app.loadLabel( context.getPackageManager() ).toString();
                //得到应用所在包的名字,即在AndriodMainfest.xml中的package的值。
                String packageName = app.packageName;
                String versionName = packageInfo.versionName;
                int versionCode = packageInfo.versionCode;
                /*
                    去除包含android目录的应用
                 */
                if (!TextUtils.isEmpty( packageName )) {
                    if (packageName.contains( ".android" ) || packageName.contains( "android." )) {
                        continue;
                    }
                }

                /*
                    判断是否为主屏幕
                 */
                String isHomePackage = Constants.FALSE;
                String homePackage = SystemUtils.getDefaultHome( context );
                if (packageName.equals( homePackage )) {
                    isHomePackage = Constants.TRUE;
                }

                /*
                    判断是否已经打开
                 */

                boolean isRunning = isRunningApp( context, packageName );

                if (isHomePackage.equals( Constants.TRUE )) {
                    isRunning = true;
                }
                String[] appInfo = {
                        appName,                      //app名称       AUV智能柜
                        packageName,                  //app包名       com.avu.hardware
                        versionName,                  //版本名称       3.5.2
                        String.valueOf( versionCode ),  //版本号         34
                        isHomePackage,                 //是否为主屏幕应用   0：不是   1：是
                        isRunning ? Constants.TRUE : Constants.FALSE  //是否为主屏幕应用   0：不是   1：是
                };

                installedApps.add( appInfo );

            }

        }

        return installedApps;
    }

    /**
     * 判断APP是不是在运行
     * <p>
     * 必须添加权限
     * <uses-permission
     * android:name="android.permission.PACKAGE_USAGE_STATS"
     * tools:ignore="ProtectedPermissions" />
     */
    public static boolean isRunningApp(Context context, String packageName) {
        boolean isRunning = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService( Context.USAGE_STATS_SERVICE );
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats( UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now );

                String topActivity = "";

                //取得最近运行的一个app，即当前运行的app
                if (!stats.isEmpty()) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get( i ).getLastTimeUsed() >= stats.get( j ).getLastTimeUsed()) {
                            j = i;
                            topActivity = stats.get( j ).getPackageName();
                            if (topActivity.equals( packageName )) {
                                isRunning = true;
                                break;
                            }
                        }
                    }

                }
            }
        }
        return isRunning;
    }


    /**
     * 获取默认主屏幕应用
     */
    public static String getDefaultHome(Context context) {
        String homePackageName = "";
        final Intent intent = new Intent( Intent.ACTION_MAIN );
        intent.addCategory( Intent.CATEGORY_HOME );
        final ResolveInfo res = context.getPackageManager().resolveActivity( intent, 0 );
        if (res != null) {
            if (res.activityInfo != null) {
                homePackageName = res.activityInfo.packageName;

                if ("android".equals( res.activityInfo.packageName )) {
                    // No default selected
                    AUVLogUtil.d( "getDefaultHome", "resolveActivity--->无默认设置" );

                } else {
                    // res.activityInfo.packageName and res.activityInfo.name gives
                    // you the default app
                    AUVLogUtil.d( "getDefaultHome", "默认桌面为：" + res.activityInfo.packageName + "."
                            + res.activityInfo.name );
                }
            }
        }

        return homePackageName;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context, String packageName) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo( packageName, 0 );
            versionName = pi.versionName;
//            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e( "VersionInfo", "Exception", e );
        }
        return versionName;
    }


}