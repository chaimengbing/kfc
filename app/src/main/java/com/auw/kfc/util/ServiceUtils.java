package com.auw.kfc.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.auv.utils.AUVLogUtil;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.content.Context.ACTIVITY_SERVICE;

public class ServiceUtils {

    private static final String TAG = "ServiceUtils";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private static ExecutorService workerThreads;

    public static ExecutorService getWorkerThreads() {
        if (workerThreads == null) {
            workerThreads = Executors.newFixedThreadPool( 10 );
        }
        return workerThreads;
    }


    public static void Receive(String file) {
        RandomAccessFile localRandomAccessFile = null;
        try {
            localRandomAccessFile = new RandomAccessFile( file, "r" );
            byte[] arrayOfByte = new byte[1024];
            int readSize = 0;
            while ((readSize = localRandomAccessFile.read( arrayOfByte )) == -1) {

            }
            String response = new String( arrayOfByte ).substring( 0, readSize );
            AUVLogUtil.d( TAG, "ReceiveIccid:" + response );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void send(String file, String cmd) {
        RandomAccessFile localRandomAccessFile = null;
        try {
            localRandomAccessFile = new RandomAccessFile( file, "rw" );
            localRandomAccessFile.writeBytes( cmd + "\r\n" );
            localRandomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 判断手机号是否符合规范
     *
     * @param phoneNo 输入的手机号
     * @return
     */
    public static boolean isPhoneNumber(String phoneNo) {
        if (TextUtils.isEmpty( phoneNo )) {
            return false;
        }
        if (phoneNo.length() == 11) {
            for (int i = 0; i < 11; i++) {
                if (!PhoneNumberUtils.isISODigit( phoneNo.charAt( i ) )) {
                    return false;
                }
            }
            Pattern p = Pattern.compile( "^((13[0-9])" +
                    "|(14[5,7,9])" +
                    "|(15[^4,\\D])" +
                    "|(17[0-9])" +
                    "|(19[0-9])" +
                    "|(16[6])" +
                    "|(18[0-9]))\\d{8}$" );
            Matcher m = p.matcher( phoneNo );
            return m.matches();
        }
        return false;
    }


    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream( strXML.getBytes( "UTF-8" ) );
            org.w3c.dom.Document doc = documentBuilder.parse( stream );
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item( idx );
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put( element.getNodeName(), element.getTextContent() );
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            AUVLogUtil.e( TAG, ex );
        }
        return null;
    }

    /**
     * 检查网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService( Context.CONNECTIVITY_SERVICE );
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

//    private void AnalyseDomain(String host){
//        String IPAddress = "";
//        InetAddress ReturnStr = null;
//        try {
//            ReturnStr = java.net.InetAddress.getByName(host);
//            IPAddress = ReturnStr.getHostAddress();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            //未知主机，域名解析失败
//        }
//        //域名解析成功
//    }

    public static void pingServer(String pingUrl) {
        getWorkerThreads().execute( new Runnable() {
            @Override
            public void run() {
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
                                AUVLogUtil.d( TAG, "pingServer::line:" + line );
                            }
                        }
                    } else {
                        AUVLogUtil.d( TAG, "pingServer::ping fail:" );
                    }

                } catch (Exception e) {
                    Log.e( TAG, e.getLocalizedMessage() );
                } finally {
                    try {
                        if (in != null)
                            in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } );
    }

    private static boolean getCheckResult(String line) {
        Pattern pattern = Pattern.compile( "(ttl=\\d+)", Pattern.CASE_INSENSITIVE );
        Matcher matcher = pattern.matcher( line );
        while (matcher.find()) {
            return true;
        }
        return false;
    }


    /**
     * 判断程序是否在后台运行
     *
     * @param activity
     * @return true 表示在后台运行
     */

    public static boolean isRunBackground(Activity activity) {

        ActivityManager activityManager = (ActivityManager) activity.getApplicationContext().getSystemService( ACTIVITY_SERVICE );
        String packageName = activity.getApplicationContext().getPackageName();
        //获取Android设备中所有正在运行的App
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return true;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals( packageName ) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return false;
            }
        }
        return true;
    }

    //获取存餐验证码
    public static String getOrderNumber() {
        //随机数
        int number = (int) ((Math.random() * 9) * 100);
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd" );
        //订单号
        return dateFormat.format( new Date() ) + number;
    }

    //获取存餐验证码
    public static int getRandomCell(int count) {
        Random rand = new Random();
        int randNum = rand.nextInt( count );
        return randNum;
    }


    //获取四位存餐码
    public static String getFourCode() {
        return getCode( 4 );
    }

    //获取存餐码
    public static String getCode(int length) {
        int len = 1;
        for (int i = 0; i < length - 1; i++) {
            len *= 10;
        }
        //随机数
        int number;
        do {
            number = (int) ((Math.random() * 9 + 1) * len);
        } while (number < 1000);
        return String.valueOf( number );
    }


    /**
     * 流量KB转为MB
     *
     * @param flux
     * @return
     */
    public static String fmFlux(String flux) {
        return new BigDecimal( flux ).divide( new BigDecimal( 1024 ), 2,
                BigDecimal.ROUND_HALF_UP ).toString();
    }

    /**
     * 集合判断是否为空
     *
     * @param collection 使用泛型
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return !isNotEmpty( collection );
    }

    /**
     * 集合判断是否为空
     *
     * @param collection 使用泛型
     * @return
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        if (collection != null) {
            Iterator<T> iterator = collection.iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    if (next != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService( Context.CONNECTIVITY_SERVICE )).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP( wifiInfo.getIpAddress() );//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    /**
     * 判断某个界面是否在前台
     *
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        return isForeground( activity, activity.getClass().getName() );
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty( className ))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks( 1 );
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get( 0 ).topActivity;
            if (className.equals( cpn.getClassName() ))
                return true;
        }
        return false;
    }

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @return
     */
    public static String readFile(String filePath, InputStream inputStream) {
        String readResult = null;
        String encoding = "UTF-8";
        byte[] fileContent;
        try {
            if (StringUtils.isNotEmpty( filePath ) && inputStream == null) {
                //先获取到文件
                File file = new File( filePath );

                if (!file.exists()) {
                    Log.d( TAG, "读取文件不存在" );
                    return null;
                } else if (!file.isFile()) {
                    Log.d( TAG, "请读取正确的文件" );
                    return null;
                } else if (!file.canRead()) {
                    Log.d( TAG, "文件没有读取权限" );
                    return null;
                }
                long fileLength = file.length();
                fileContent = new byte[(int) fileLength];
                FileInputStream fis = new FileInputStream( file );
                fis.read( fileContent );
            } else {
                fileContent = new byte[2048];
                inputStream.read( fileContent );
            }
            readResult = new String( fileContent, encoding );
        } catch (Exception e) {
            Log.d( TAG, "读取文件失败", e );
        }
        return readResult;
    }

    /**
     * 将密码写入本地文件
     *
     * @param filePath
     * @param pass
     */
    public static void writePassWord(String filePath, String pass) {
        File file = new File( filePath );
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream( file );
            fileOutputStream.write( pass.getBytes( "UTF-8" ) );
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
