package com.auv.listener;


public interface LinkKitConnectListener {

    void onError(int code, String error);

    void onConnectSuccess();
}
