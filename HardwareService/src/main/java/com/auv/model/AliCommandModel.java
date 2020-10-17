package com.auv.model;

public class AliCommandModel {
//    {"method":"thing.service.operationCell","id":"1971561171","params":
//        {"commandList":"" +
//                "[{\"cellNo\":\"109\",\"opList\":" +
//                "[{\"property\":\"_door\",\"command\":1}]}]"},
//        "version":"1.0.0"}

    private String id;
    private String method;
    private String version;
    private AliParamsModel params;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public AliParamsModel getParams() {
        return params;
    }

    public void setParams(AliParamsModel params) {
        this.params = params;
    }

}
