package com.auv.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AUVLogUtil {
    private static Boolean AUV_LOG_SWITCH = true; // 日志文件总开关
    private static Boolean AUV_LOG_WRITE_TO_FILE = true;// 日志写入文件开关
    private static char AUV_LOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息

    private static String deviceName;

    // sd卡中日志文件的最多保存天数
    public static int SDCARD_LOG_FILE_SAVE_DAYS = 10;
    // 本类输出的日志文件名称
    public static String AUV_LOG_NAME = "_auvlog.log";
    // 日志的输出格式
    public static SimpleDateFormat AUV_LOG_SDF = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    // 日志文件格式
    public static SimpleDateFormat AUV_LOG_FILE_SDF = new SimpleDateFormat( "yyyy-MM-dd" );

    // 日志文件在sdcard中的路径
    public static String AUV_LOG_PATH_SDCARD_DIR = "/sdcard/auv/log/";


    public static void w(String tag, Object msg) { // 警告信息
        log( tag, msg.toString(), 'w' );
    }

    public static void e(String tag, Object msg) { // 错误信息
        log( tag, msg.toString(), 'e' );
    }

    public static void d(String tag, Object msg) {// 调试信息
        log( tag, msg.toString(), 'd' );
    }

    public static void i(String tag, Object msg) {//
        log( tag, msg.toString(), 'i' );
    }

    public static void v(String tag, Object msg) {
        log( tag, msg.toString(), 'v' );
    }

    public static void w(String tag, String text) {
        log( tag, text, 'w' );
    }

    public static void e(String tag, String text) {
        log( tag, text, 'e' );
    }

    public static void d(String tag, String text) {
        log( tag, text, 'd' );
    }

    public static void i(String tag, String text) {
        log( tag, text, 'i' );
    }

    public static void v(String tag, String text) {
        log( tag, text, 'v' );
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     */
    private static void log(String tag, String msg, char level) {
//        if (TextUtils.isEmpty( deviceName )) {
//            Log.d( "AUVLogUtils", "log::deviceName IS NULL" );
//            return;
//        }
        if (AUV_LOG_SWITCH) {//日志文件总开关
            if ('e' == level && ('e' == AUV_LOG_TYPE || 'v' == AUV_LOG_TYPE)) { // 输出错误信息
                Log.e( tag, msg );
            } else if ('w' == level && ('w' == AUV_LOG_TYPE || 'v' == AUV_LOG_TYPE)) {
                Log.w( tag, msg );
            } else if ('d' == level && ('d' == AUV_LOG_TYPE || 'v' == AUV_LOG_TYPE)) {
                Log.d( tag, msg );
            } else if ('i' == level && ('d' == AUV_LOG_TYPE || 'v' == AUV_LOG_TYPE)) {
                Log.i( tag, msg );
            } else {
                Log.v( tag, msg );
            }
            if (AUV_LOG_WRITE_TO_FILE)//日志写入文件开关
                writeLogtoFile( String.valueOf( level ), tag, msg );
        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @param logType
     * @param tag
     * @param text
     */
    private static void writeLogtoFile(String logType, String tag, String text) {// 新建或打开日志文件
        Date nowtime = new Date();
        String needWriteFiel = AUV_LOG_FILE_SDF.format( nowtime );
        String needWriteMessage = AUV_LOG_SDF.format( nowtime ) + "    " + logType + "    " + tag + "    " + text;
        File dirsFile = new File( AUV_LOG_PATH_SDCARD_DIR );
        if (!dirsFile.exists()) {
            dirsFile.mkdirs();
        }
        String logName = needWriteFiel + "_" + deviceName + AUV_LOG_NAME;
        //Log.i("创建文件","创建文件");
        File file = new File( dirsFile.toString(), logName );
        if (!file.exists()) {
            try {
                //在指定的文件夹中创建文件
                file.createNewFile();
            } catch (Exception e) {
            }
        }

        try {
            FileWriter filerWriter = new FileWriter( file, true );// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter( filerWriter );
            bufWriter.write( needWriteMessage );
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    /**
//     * 从文件中读取数据
//     *
//     * @return
//     * @throws IOException
//     */
//    public static String readFile() {
//        Date nowtime = new Date();
//        String needWriteFiel = AUV_LOG_FILE_SDF.format( nowtime );
//
//        File dirsFile = new File( AUV_LOG_PATH_SDCARD_DIR );
//        if (!dirsFile.exists()) {
//            dirsFile.mkdirs();
//        }
//        String logName = needWriteFiel + "_" + deviceName + AUV_LOG_NAME;
//        //Log.i("创建文件","创建文件");
//        File file = new File( dirsFile.toString(), logName );
//        StringBuilder sb = new StringBuilder( "" );
//
//        //打开文件输入流
//        FileInputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream( file );
//            byte[] buffer = new byte[1024];
//            int len = inputStream.read( buffer );
//            //读取文件内容
//            while (len > 0) {
//                sb.append( new String( buffer, 0, len ) );
//
//                //继续将数据放到buffer中
//                len = inputStream.read( buffer );
//            }
//            //关闭输入流
//            inputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return sb.toString();
//    }


    /**
     * 删除制定的日志文件
     */
    public static void delFile() {// 删除日志文件
        Log.d( "LogUtils", "delFile::" );
        String needDelFiel = AUV_LOG_FILE_SDF.format( getDateBefore() );

        File mfolder = new File( AUV_LOG_PATH_SDCARD_DIR ); //打开目录文件夹
        if (mfolder.isDirectory()) {
            File[] allLogFile = mfolder.listFiles();
            if (allLogFile != null && allLogFile.length > 0) {
                for (File file : allLogFile) {
                    String fileName = file.getName().substring( 0, needDelFiel.length() );
                    Date date = getDateBefore();
                    Date fileDate = getFileDate( fileName );
                    if (file != null && fileDate != null) {
                        if (fileDate.before( date )) {
                            if (file.exists()) {
                                Log.d( "LogUtils", "delFile::logName:" + file.getName() );
                                file.delete();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 字符串转时间
     *
     * @param fileName
     * @return
     */
    public static Date getFileDate(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return null;
        }
        Date date = null;
        try {
            date = AUV_LOG_FILE_SDF.parse( fileName );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime( nowtime );
        now.set( Calendar.DATE, now.get( Calendar.DATE ) - SDCARD_LOG_FILE_SAVE_DAYS );
        return now.getTime();
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
