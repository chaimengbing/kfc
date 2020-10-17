package com.auv.model;

public class AliControlAppModel {

    /**
     * 数据格式
     * {
     * "action": "reboot",
     * "data":{
     * "seqid":56542345645487
     * "target": "APP",
     * "downloadPath": "",
     * "packageName": "",
     * "sessionId":"sessionId"
     * }
     * }
     */

    private String action;
    private AliControlDataModel data;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public AliControlDataModel getData() {
        return data;
    }

    public void setData(AliControlDataModel data) {
        this.data = data;
    }
}
