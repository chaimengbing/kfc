package com.auv.model;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.auv.constant.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * @author LiChuang
 * @since 2020/7/10 16:21
 * 设备信息类
 **/
public class DeviceInfo {
    private String deviceId;
    private String sdkVersion;
    private String appVersion;
    private String osVersion;
    private String iotCardId;
    private String flow;
    private String memory;
    private String netType;
    private String dbm;
    private String pingAli;
    private String pingBd;
    private String platFrom;
    private String[] lgdeAndlade;
    private String serialNumber;


    private List<String[]> installedAppArrays;


    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }


    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }


    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }


    public void setIotCardId(String iotCardId) {
        this.iotCardId = iotCardId;
    }


    public void setFlow(String flow) {
        this.flow = flow;
    }


    public void setMemory(String memory) {
        this.memory = memory;
    }


    public void setNetType(String netType) {
        this.netType = netType;
    }


    public void setDbm(String dbm) {
        this.dbm = dbm;
    }


    public void setPingAli(String pingAli) {
        this.pingAli = pingAli;
    }

    public void setPingBd(String pingBd) {
        this.pingBd = pingBd;
    }


    @NonNull
    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put( Constants.IOT_CARD_ID, this.iotCardId );
        jsonObject.put( Constants.SDK_VERSION, this.sdkVersion );
        jsonObject.put( Constants.APP_VERSION, this.appVersion );
        jsonObject.put( Constants.OS_VERSION, this.osVersion );
        jsonObject.put( Constants.MEMORY, this.memory );
        jsonObject.put( Constants.NET_TYPE, netType );
        jsonObject.put( Constants.PING_BD, pingBd );
        jsonObject.put( Constants.PING_ALI, this.pingAli );
        jsonObject.put( Constants.DBM, this.dbm );
        jsonObject.put( Constants.FLOW, this.flow );
        jsonObject.put( Constants.DEVICE_ID, this.deviceId );
        jsonObject.put( Constants.INSTALL_APPS, this.installedAppArrays );
        jsonObject.put( Constants.LATITUDE_LONGITUDE, Arrays.toString( lgdeAndlade ) );
        jsonObject.put( Constants.SERIAL_NUMBER, this.serialNumber );
        return jsonObject.toJSONString();
    }

    public String getPlatFrom() {
        return platFrom;
    }

    public void setPlatFrom(String platFrom) {
        this.platFrom = platFrom;
    }

    public void setInstalledAppArrays(List<String[]> installedAppArrays) {
        this.installedAppArrays = installedAppArrays;
    }

    public String[] getLgdeAndlade() {
        return lgdeAndlade;
    }

    public void setLgdeAndlade(String[] lgdeAndlade) {
        this.lgdeAndlade = lgdeAndlade;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
