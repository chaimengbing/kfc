package com.auv.model;

import com.alibaba.fastjson.JSONObject;
import com.auv.constant.Constants;

public class AliControlDataModel {

// * "seqid":56542345645487
//         * "target": "APP",
//         * "downloadPath": "",
//         * "packageName": "",
//         * "sessionId":"sessionId"

    private String sessionId;
    private String target;
    private String downloadPath;
    private String packageName;
    private int versionCode;
    private String versionName;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
