package com.auw.kfc.util;

import android.widget.TextView;

import java.security.MessageDigest;
import java.util.UUID;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || "".equals( str ) || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty( str );
    }


    public static String getUUID() {
        return UUID.randomUUID().toString();
    }



    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr) {

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String( a );
        return s;
    }

    public static String EncoderByMd5(String buf) {
        try {
            MessageDigest digist = MessageDigest.getInstance( "MD5" );
            byte[] rs = digist.digest( buf.getBytes( "UTF-8" ) );
            StringBuffer digestHexStr = new StringBuffer();
            for (int i = 0; i < 16; i++) {
                digestHexStr.append( byteHEX( rs[i] ) );
            }
            return digestHexStr.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    public static String byteHEX(byte ib) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0F];
        ob[1] = Digit[ib & 0X0F];
        String s = new String( ob );
        return s;
    }


    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String decode1(String inStr) {
        char[] miyao = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ miyao[i]);
        }
        String s = new String( a );
        return s;
    }

    public static String decode2(String inStr) {
        char[] miyao = {'m', 'n', 'b', 'v', 'c', 'x', 'z', 'l', 'k', 'j', 'h', 'g', 'f', 'd', 's', 'a', 'p', 'o', 'i', 'u', 'y', 't', 'r', 'e', 'w', 'q', '0', '9', '8', '7', '6', '5', '4', '3', '2', '1'};
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ miyao[i]);
        }
        String s = new String( a );
        return s;
    }

    public static String stringToHexString(String strPart) {
        String hexString = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt( i );
            if (ch < 16) {
                String strHex = '0' + Integer.toHexString( ch );
                hexString = hexString + strHex;
            } else {
                String strHex = Integer.toHexString( ch );
                hexString = hexString + strHex;
            }

        }
        return hexString;
    }

    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp
                    + (char) Integer.valueOf( src.substring( i * 2, i * 2 + 2 ),
                    16 ).byteValue();
        }
        return temp;
    }


    public static void main(String[] args) {

        boolean isTemp = true;
        int i = 0;
        while (isTemp) {
            if (i == 10) {
                isTemp = false;
            } else {
                i++;
            }
        }

        System.out.println( i );

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


}