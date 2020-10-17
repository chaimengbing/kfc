package com.auv.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auv.constant.Constants;
import com.auv.listener.OnHttpResponseListener;
import com.auv.model.Result;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LiChuang
 * @since 2020/6/11 11:51
 **/
public class NetworkUtils {

    private final String TAG = getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static NetworkUtils networkUtils;

    private Context context;

    private String pingBd = null, pingAli = null, allowance = null, ioTCardId = null;


    private int delayTime = 5000;
    private int currentDbm = 0;


    public static NetworkUtils getInstance(Context context) {

        if (networkUtils == null) {
            networkUtils = new NetworkUtils( context );
            FileDownloader.setup( context );

        }
        return networkUtils;
    }

    private NetworkUtils(Context context) {
        this.context = context;
    }


    /**
     * 下载apk静默更新并重启
     *
     * @param downloadPath apk下载路径
     * @param packageName  安装apk的包名
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Result updateAndReboot(@NonNull final String downloadPath, @NonNull final String packageName, final int versionCode, @NonNull final String versionName) {
        /*
            验证下载路径是否正确
         */
        if (!Utils.isURL( downloadPath )) {
            return Result.error( Constants.URL_INCORRECT );
        }

        downLoadAuvApp( downloadPath, packageName, versionCode );
        return Result.success();
    }

    /**
     * 打开app
     */
    public void openApp() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage( context.getPackageName() );
        PendingIntent restartIntent = PendingIntent.getActivity( context, 0, intent, PendingIntent.FLAG_ONE_SHOT );
        AlarmManager mgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        if (mgr != null) {
            mgr.set( AlarmManager.RTC, System.currentTimeMillis() + getDelayTime(), restartIntent );
        }
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }


    /**
     * 下载apk并安装
     *
     * @param downloadPath apk下载路径
     * @param packageName  安装apk的包名
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Result downLoadApkAndInstall(@NonNull final String downloadPath, @NonNull final String packageName) {
        /*
            验证下载路径是否正确
         */
        if (!Utils.isURL( downloadPath )) {
            return Result.error( Constants.URL_INCORRECT );
        }

        downLoadAppVersion( downloadPath, packageName );
        return Result.success();
    }


    /**
     * 查询app是否已经安装
     *
     * @param packageName 查询的包名
     */
    public Result queryAppInstall(@NonNull final String packageName, Context context) {
        List<String[]> installedApps = SystemUtils.getInstalledAppList( context, false );
        if (installedApps.size() == 0) {
            return Result.error( Constants.UNINSTALL );
        }

        for (String[] values : installedApps) {
            if (packageName.equals( values[1] )) {
                return Result.success( Constants.INSTALL );
            }
        }
        return Result.error( Constants.UNINSTALL );
    }


    public String getNetType() {
        String netType = null;
        //步骤1：通过Context.getSystemService(Context.CONNECTIVITY_SERVICE)获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        //步骤2：获取ConnectivityManager对象对应的NetworkInfo对象
        //NetworkInfo对象包含网络连接的所有信息
        //步骤3：根据需要取出网络连接信息
        //获取WIFI连接的信息

        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo( ConnectivityManager.TYPE_WIFI );
            if (networkInfo != null) {
                if (networkInfo.isConnected()) {
                    netType = "WIFI";
                }
            }

            //获取移动数据连接的信息
            networkInfo = connMgr.getNetworkInfo( ConnectivityManager.TYPE_MOBILE );
            if (networkInfo != null) {
                if (networkInfo.isConnected()) {
                    netType = "MOBILE";
                }
            }
        }
        return netType;
    }

    /**
     * 检查网络是否连接
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService( Context.CONNECTIVITY_SERVICE );
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } else {
            return false;
        }

    }

    /**
     * 检查网络是否在工作
     *
     * @return
     */
    private boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec( "ping -c 3 www.baidu.com" );
            int exitValue = ipProcess.waitFor();
            Log.i( "Avalible", "Process:" + exitValue );
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean getCheckResult(String line) {
        Pattern pattern = Pattern.compile( "(ttl=\\d+)", Pattern.CASE_INSENSITIVE );
        Matcher matcher = pattern.matcher( line );
        while (matcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 对指定服务器进行抓包
     *
     * @param pingUrl 抓包网址
     */
    public void pingServer(String pingUrl) {
        ThreadPoolUtil.getInstance().getWorkerThreads().execute( () -> {
            BufferedReader in = null;
            try {
                AUVLogUtil.d( TAG, "pingServer::ip address:" + pingUrl );
                // ping ip地址
                Process p = Runtime.getRuntime().exec( "ping -c 1 -w 5 " + pingUrl );
                int status = p.waitFor();
                if (status == 0) {
                    in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );  // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        if (getCheckResult( line )) {
                            String[] pings = line.split( "=" );
                            if (pings != null && pings.length > 0) {
                                line = pings[pings.length - 1];
                                line = line.replace( " ", "" );
                                line = line.replace( "ms", "" );

                            }
                            switch (pingUrl) {
                                case Constants.AUV_PING_BAIDU:
                                    pingBd = line;
                                    break;
                                case Constants.AUV_PING_ALI:
                                    pingAli = line;
                                    break;
                            }
                            AUVLogUtil.d( TAG, "pingServer::line:" + line );
                        }
                    }
                } else {
                    AUVLogUtil.d( TAG, "pingServer::ping fail:" );
                }

            } catch (Exception e) {
                Log.e( TAG, Objects.requireNonNull( e.getLocalizedMessage() ) );
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } );
    }


    /**
     * 获取百度ping值
     */

    public String getPingBd() {
        return pingBd;
    }

    /**
     * 获取阿里ping值
     */
    public String getPingAli() {
        return pingAli;
    }

    /**
     * 获取sim卡iccid
     */
    @SuppressLint("HardwareIds")
    public String getIccid() {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
        String iccid = "N/A";
        String readPhoneState = Manifest.permission.READ_PHONE_STATE;
        if (ActivityCompat.checkSelfPermission( context, readPhoneState ) != PackageManager.PERMISSION_GRANTED) {
            this.applyRight();
            return iccid;
        }

        if (telephonyManager != null) {
            iccid = telephonyManager.getSimSerialNumber();
        }

        AUVLogUtil.d( TAG, "iccid:" + iccid );
        return iccid;
    }


    /**
     * 获取物联卡号流量
     */
    public void queryCard(@NonNull final String iccId) {
        if (TextUtils.isEmpty( iccId )) {
            return;
        }
        AUVLogUtil.d( TAG, "queryCard::iccId:" + iccId );
        Map<String, Object> request = new ConcurrentHashMap<>();
        request.put( "clientid", Constants.AUV_CLIENT_ID );
        request.put( "cardno", iccId );
        String sign = "clientid=" + Constants.AUV_CLIENT_ID + "&cardno=" + iccId;
        String serit = Utils.MD5( sign );
        request.put( "sign", serit.toUpperCase() );
        AUVHttpManager.getInstance( context ).post( request, Constants.AUV_SIM_CARD_URL, 0, new OnHttpResponseListener() {
            @Override
            public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                AUVLogUtil.d( TAG, "queryCard::resultJson:" + resultJson );
                if (!TextUtils.isEmpty( resultJson )) {
                    JSONObject jsonObject = JSONObject.parseObject( resultJson );
                    if ("0".equals( jsonObject.getString( "resultcode" ) )) {
                        JSONArray packagemsg = jsonObject.getJSONArray( "packagemsg" );
                        for (Object object : packagemsg) {
                            JSONObject info = (JSONObject) object;
                            if (Constants.PTYPE.equals( info.getString( "ptype" ) )) {
                                //                  int total = jsonObject.getInteger("total");
                                //                  int used = jsonObject.getInteger("used");
                                String allowanceStr = info.getString( "allowance" );
                                float allowanceInt = Float.parseFloat( allowanceStr );
                                String allowance = String.format( "%.2f", Utils.convertSize( allowanceInt ) );
                                Utils.syncInformation( context, allowance );
                            }
                        }
                        JSONObject cardmsg = jsonObject.getJSONObject( "cardmsg" );
                        //物联卡编号
                        ioTCardId = cardmsg.getString( "cardno" );
                    } else {
                        Utils.syncInformation( context, "" );
                    }
                }
            }

            @Override
            public void onHttpSuccess(int requestCode, String resultData, String resultMsg) {

            }

            @Override
            public void onHttpError(int resultCode, String resultMsg) {

            }
        } );
    }


    /**
     * 申请有关手机信号的权限
     */
    public void applyRight(@NonNull String... permissions) {
        for (String permission : permissions) {
            int i = ContextCompat.checkSelfPermission( context, permission );
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions( (Activity) context, permissions, 321 );
            }
        }
    }

    /**
     * 申请有关手机信号的权限
     */
    public void applyRight() {
        this.applyRight( Constants.PERMISSIONS );
    }


    /**
     * 得到当前的手机蜂窝网络信号强度
     * 获取LTE网络和3G/2G网络的信号强度的方式有一点不同，
     * LTE网络强度是通过解析字符串获取的，
     * 3G/2G网络信号强度是通过API接口函数完成的。
     * asu 与 dbm 之间的换算关系是 dbm=-113 + 2*asu
     */
    public void getCurrentNetDBM() {

        if ("WIFI".equals( getNetType() )) {
            WifiManager wifiManager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
            int numberOfLevels = 5;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            currentDbm = wifiInfo.getRssi();

        } else {

            handler.removeMessages( 0 );
            handler.sendEmptyMessage( 0 );

        }
    }

    private Handler handler = new Handler( Looper.getMainLooper() ) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage( msg );

            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService( Context.TELEPHONY_SERVICE );

            PhoneStateListener mylistener = new PhoneStateListener() {

                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged( signalStrength );
                    String signalInfo = signalStrength.toString();
                    String[] params = signalInfo.split( " " );

                    if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                        //4G网络 最佳范围   >-90dBm 越大越好
                        currentDbm = Integer.parseInt( params[9] );

                    } else if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
                            tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
                            tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
                            tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
                        //3G网络最佳范围  >-90dBm  越大越好  ps:中国移动3G获取不到  返回的无效dbm值是正数（85dbm）
                        //在这个范围的已经确定是3G，但不同运营商的3G有不同的获取方法，故在此需做判断 判断运营商与网络类型的工具类在最下方
                        String yys = getOperatorName( context );//获取当前运营商
                        switch (yys) {
                            case "中国移动":
                                //中国移动3G不可获取，故在此返回0
                                currentDbm = 0;
                                break;
                            case "中国联通":
                                currentDbm = signalStrength.getCdmaDbm();
                                break;
                            case "中国电信":
                                currentDbm = signalStrength.getEvdoDbm();
                                break;
                        }

                    } else {
                        //2G网络最佳范围>-90dBm 越大越好
                        int asu = signalStrength.getGsmSignalStrength();
                        currentDbm = -113 + 2 * asu;
                    }

                }
            };
            //开始监听
            tm.listen( mylistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS );
        }

    };

    /**
     * 获取运营商名字
     *
     * @param context context
     * @return int
     */
    public String getOperatorName(Context context) {
        /*
         * getSimOperatorName()就可以直接获取到运营商的名字
         * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
         * IMSI相关链接：http://baike.baidu.com/item/imsi
         */
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
        // getSimOperatorName就可以直接获取到运营商的名字
        return telephonyManager.getSimOperatorName();
    }


    /**
     * 重启app的方法
     */
    public void rebootApp() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage( context.getPackageName() );
        PendingIntent restartIntent = PendingIntent.getActivity( context, 0, intent, PendingIntent.FLAG_ONE_SHOT );
        AlarmManager mgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        if (mgr != null) {
            mgr.set( AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent ); // 1秒钟后重启应用
            exitAPP();
        }
    }

    /**
     * 退出app的方法
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void exitAPP() {
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
     * 更新app并重启
     *
     * @param path        apk路径
     * @param packageName 安装apk的包名
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateAndRebootApp(String path, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage( context.getPackageName() );
        PendingIntent restartIntent = PendingIntent.getActivity( context, 0, intent, PendingIntent.FLAG_ONE_SHOT );
        AlarmManager mgr = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        if (mgr != null) {
            mgr.set( AlarmManager.RTC, System.currentTimeMillis() + 3000, restartIntent ); // 1秒钟后重启应用
            try {
                installPackage( context, path, packageName );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 安装apk,必须是系统签名后的apk包
     * 必须声明以下权限
     * <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
     * <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
     * <uses-permission android:name="android.permission.INSTALL_PACKAGES" />
     *
     * @param apkPath     apk路径
     * @param packageName 项目包名
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean installPackage(Context context, String apkPath, String packageName) throws IOException {
        AUVLogUtil.d( TAG, "installPackage::packageName:" + packageName );
        File file = new File( apkPath );
        if (file == null || !file.exists()) {
            AUVLogUtil.d( TAG, "installPackage::no apk file" );
            return false;
        }
        AUVLogUtil.d( TAG, "installPackage::installing" );
        InputStream in = new FileInputStream( file );
        PackageInstaller packageInstaller = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            packageInstaller = context.getPackageManager().getPackageInstaller();
        }
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL );
        params.setAppPackageName( packageName );
        // set params
        int sessionId = packageInstaller.createSession( params );
        PackageInstaller.Session session = packageInstaller.openSession( sessionId );
        OutputStream out = session.openWrite( "COSU", 0, -1 );
        byte[] buffer = new byte[65536];
        int c;
        while ((c = in.read( buffer )) != -1) {
            out.write( buffer, 0, c );
        }
        session.fsync( out );
        in.close();
        out.close();

        session.commit( createIntentSender( context, sessionId ) );
        if (packageName.equals( context.getPackageName() )) {
            openApp();
        }
        packageInstaller.registerSessionCallback( new PackageInstaller.SessionCallback() {
            @Override
            public void onCreated(int i) {
                AUVLogUtil.d( TAG, "onCreated::i:" + i );
            }

            @Override
            public void onBadgingChanged(int i) {
                AUVLogUtil.d( TAG, "onBadgingChanged::i:" + i );
            }

            @Override
            public void onActiveChanged(int i, boolean b) {
                AUVLogUtil.d( TAG, "onActiveChanged::i:" + i + ",b:" + b );
            }

            @Override
            public void onProgressChanged(int i, float v) {
                AUVLogUtil.d( TAG, "onProgressChanged::i:" + i );
            }

            @Override
            public void onFinished(int i, boolean b) {
                AUVLogUtil.d( TAG, "onFinished::i:" + i + ",b:" + b );
            }
        } );

        return true;
    }

    private IntentSender createIntentSender(Context context, int sessionId) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                new Intent( Constants.ACTION_INSTALL_COMPLETE ),
                0 );
        return pendingIntent.getIntentSender();
    }

    /**
     * 静默卸载App
     *
     * @param packageName 包名
     * @return 是否卸载成功
     */
    public boolean unInstallApp(String packageName) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = new ProcessBuilder( "pm", "uninstall", packageName ).start();
            successResult = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
            errorResult = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append( s );
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append( s );
            }
        } catch (Exception e) {

        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {

            }
            if (process != null) {
                process.destroy();
            }
        }
        //如果含有“success”单词则认为卸载成功
        return successMsg.toString().equalsIgnoreCase( "success" );
    }

    /**
     * 卸载第三方app
     *
     * @param packageName 需要卸载的app包名
     *                    android:sharedUserId="android.uid.system"
     * @return 卸载结果
     */
    public Result unInstall(String packageName) {

        if (context.getPackageName().equals( packageName )) {
            return Result.error();
        }

        try {
            PackageManager pm = context.getPackageManager();
            Method[] methods = pm != null ? pm.getClass().getDeclaredMethods() : null;
            Method mDel = null;
            for (Method method : methods) {
                if (method.getName().equals( "deletePackage" )) {
                    mDel = method;
                    break;
                }
            }
            mDel.setAccessible( true );
            mDel.invoke( pm, packageName, null, 0 );
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error( e.getMessage() );
        }

    }


    /***  下载APP ***/

    /**
     * 下载新版本
     */
    private void downLoadAppVersion(String downUrl, String packageName) {
        AUVLogUtil.d( TAG, "downLoadAppVersion::downUrl:" + downUrl );
        File file = new File( Constants.ABSOLUTE_APK_DOWNLOAD_PATH );
        if (!file.exists()) {
            file.mkdir();
        }
        String downloadApkName = packageName + Constants.APK_DOWNLOAD_NAME;
        String path = file.getPath() + "/" + downloadApkName;
        File file1 = new File( path );
        if (file1.exists()) {
            file1.delete();
        }

        FileDownloader.getImpl().create( downUrl ).setPath( path )
                .setListener( new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        AUVLogUtil.d( TAG, "pending::task.getId():" + task.getId() );
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                        AUVLogUtil.d( TAG, "progress::task.getId():" + task.getId() );
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        AUVLogUtil.d( TAG, "completed::task.getId():" + task.getId() );
                        try {
                            installPackage( context, path, packageName );
                        } catch (IOException e) {
                            e.printStackTrace();
                            AUVLogUtil.e( TAG, e.getMessage() );
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                        LogUtil.d( TAG, "paused::task.getId():" + task.getId() );
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        AUVLogUtil.d( TAG, "error::task.getId():" + task.getId() );
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
//                        LogUtil.d( TAG, "warn::task.getId():" + task.getId() );
                    }
                } ).start();
    }


    private void downLoadAuvApp(String downUrl, String packageName, int versionCode) {
        AUVLogUtil.d( TAG, "downLoadAuvApp::downUrl:" + downUrl );
        int code = Utils.getAppVersionCode( context );
        if (code >= versionCode) {
            AUVLogUtil.d( TAG, "downLoadAuvApp::APP version is highest" );
            return;
        }
        File file = new File( Constants.ABSOLUTE_APK_DOWNLOAD_PATH );
        if (!file.exists()) {
            file.mkdir();
        }
        String downloadApkName = packageName + Constants.APK_DOWNLOAD_NAME;
        String path = file.getPath() + "/" + downloadApkName;
        File file1 = new File( path );
        if (file1.exists()) {
            file1.delete();
        }

        FileDownloader.getImpl().create( downUrl ).setPath( path )
                .setListener( new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        AUVLogUtil.d( TAG, "pending::task.getId():" + task.getId() );
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                        AUVLogUtil.d( TAG, "progress::task.getId():" + task.getId() );
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        AUVLogUtil.d( TAG, "completed::task.getId():" + task.getId() );
//                        installAPK( new File( SettingConfig.AUV_UPDATE_PATH_SDCARD_DIR + SettingConfig.VERSION_NAME ) );
                        try {
                            //安装完成 3秒后启动应用
//                            openApp();
                            installPackage( context, path, packageName );
                        } catch (IOException e) {
                            e.printStackTrace();
                            AUVLogUtil.e( TAG, e.getMessage() );
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                        LogUtil.d( TAG, "paused::task.getId():" + task.getId() );
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        AUVLogUtil.d( TAG, "error::task.getId():" + task.getId() );
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
//                        LogUtil.d( TAG, "warn::task.getId():" + task.getId() );
                    }
                } ).start();
    }


    public String getCurrentDbm() {
        return String.valueOf( currentDbm );
    }
}

