package com.auw.kfc.util;

import androidx.annotation.NonNull;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
    //HmacMD5算法
    public static final String KEY_MD5 = "HmacMD5";
    public static final String AUV_SECRET = "MJcUsNL5j5DIrcAgaeW7dfxSvmBMpmNB";

    /**
     * MD5加密-》获取签名
     *
     * @param data      要签名的内容
     * @param sigSecret 签名秘钥
     * @return
     * @throws Exception
     */
    public static String getMd5BySecret(@NonNull String data, @NonNull String sigSecret) {
        SecretKey secretKey = new SecretKeySpec( sigSecret.getBytes(), KEY_MD5 );
        Mac mac = null;
        try {
            mac = Mac.getInstance( secretKey.getAlgorithm() );
            mac.init( secretKey );
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        assert mac != null;
        byte[] b = mac.doFinal( data.getBytes() );
        return bytesToHexString( b );
    }

    /**
     * 字节转16进制 字母大写
     *
     * @param b MD5加密签名后的byte数组
     * @return
     */
    public static String bytesToHexString(@NonNull byte[] b) {
        StringBuffer sb = new StringBuffer( b.length * 2 );
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append( '0' );
            }
            sb.append( Integer.toHexString( v ) );
        }
        String sig = sb.toString();
        //转为大写
//       return sig.toUpperCase();
        //转为小写
        return sig.toLowerCase();
    }

    /**
     * 判断存餐二维码是不是KFC的
     *
     * @param scanResult
     * @return
     */
    public static boolean isKFCQRCode(String[] scanResult) {
        boolean isKFCCode = false;
        if (scanResult.length > 3) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 3; i++) {
                sb.append( scanResult[i] );
                sb.append( "-" );
            }
            String kfcTemp = sb.toString().substring( 0, sb.length() - 1 );
            String hmacMD5Res = getMd5BySecret( kfcTemp, AUV_SECRET ).substring( 0, 8 );
            if (hmacMD5Res.equals( scanResult[3] )) {
                isKFCCode = true;
            }
        }
        return isKFCCode;
    }


    private static ExecutorService workerThreads;

    public static ExecutorService getWorkerThreads() {
        if (workerThreads == null) {
            workerThreads = Executors.newFixedThreadPool( 3 );
        }
        return workerThreads;
    }

}
