package com.auv.utils;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class BDMapUtils {

    private static String TAG = "BDMapUtils";
    private static BDMapUtils bdMapUtils;

    public static BDMapUtils getBdMapUtils(Context context) {
        if (bdMapUtils == null) {
            bdMapUtils = new BDMapUtils( context );
        }
        return bdMapUtils;
    }

    private Context context;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();


    private BDMapUtils(Context context) {
        this.context = context;
        initMap();
    }

    public void initMap() {

        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener( myListener );
        }
        mLocationClient = new LocationClient( context );
        mLocationClient.registerLocationListener( myListener );

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode( LocationClientOption.LocationMode.Hight_Accuracy );
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
        option.setCoorType( "bd09ll" );
        option.setScanSpan( 1000 );
        option.setOpenGps( true );
        option.setLocationNotify( true );
        option.setIgnoreKillProcess( false );
        option.SetIgnoreCacheException( false );
        option.setWifiCacheTimeOut( 5 * 60 * 1000 );
        option.setEnableSimulateGps( false );
        option.setNeedNewVersionRgc( true );
        mLocationClient.setLocOption( option );

        mLocationClient.start();
    }


    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double latitude = bdLocation.getLatitude();    //获取纬度信息
            double longitude = bdLocation.getLongitude();    //获取经度信息
            float radius = bdLocation.getRadius();    //获取定位精度，默认值为0.0f

            AUVLogUtil.d( TAG, "onReceiveLocation::latitude:" + latitude + ",longitude:" + longitude );
            String coorType = bdLocation.getCoorType();
            int errorCode = bdLocation.getLocType();
            String la = String.valueOf( latitude );
            String lo = String.valueOf( longitude );
            if (!TextUtils.isEmpty( la ) && !TextUtils.isEmpty( lo ) && !"4.9E-324".equals( la ) && !"4.9E-324".equals( lo )) {
                SharedPreferenceHelper.getInstance( context ).saveString( "latitude", la );
                SharedPreferenceHelper.getInstance( context ).saveString( "longitude", lo );
                if (mLocationClient != null) {
                    mLocationClient.stop();
                    mLocationClient.unRegisterLocationListener( myListener );
                }
            }

        }
    }
}
