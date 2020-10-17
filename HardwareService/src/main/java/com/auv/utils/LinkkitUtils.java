package com.auv.utils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.apiclient.utils.StringUtils;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttPublishRequest;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttRrpcRegisterRequest;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttRrpcRequest;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcHandle;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.tools.AError;
import com.auv.constant.Constants;
import com.auv.model.Result;

/**
 * 用来连接阿里云服务器的工具
 */
public class LinkkitUtils {

    private final String TAG = getClass().getSimpleName();

    private static LinkkitUtils linkkitUtils;

    //订阅次数
    private int reSubTimes = 1;

    public static LinkkitUtils getInstance() {
        if (linkkitUtils == null) {
            linkkitUtils = new LinkkitUtils();
        }
        return linkkitUtils;
    }

    /**
     * 自定义云端下行消息监听器
     */
    public interface SubStatusListener {

        /**
         * 回掉给调用者订阅结果
         */
        void subStatusListener(boolean success, String msg);

        /**
         * 回掉给调用者从云端接收到的信息
         */
        void downstreamDataListener(MqttRrpcRequest mqttRrpcRequest, JSONObject downstreamData);
    }

    /**
     * 向阿里云服务器发送消息
     *
     * @param info      回调信息
     * @param topicName topic名称
     * @param topicPath topic路径
     */
    public void publish(final String topicName, final String topicPath, String info) {
        //设置pub消息的topic和内容
        MqttPublishRequest request = new MqttPublishRequest();

        /*
            如果是自定义的topic，那么除了topicName其余格式都是相同的
         */

        if (TextUtils.isEmpty( topicPath )) {
            request.topic = AliYunIotUtils.AlI_CUSTOM_TOPIC + topicName;
        } else {
            request.topic = topicPath;
        }

        request.replyTopic = request.topic + Constants.TOPIC_REPLAY;

        request.qos = 0;
        request.payloadObj = info;
        //发送消息并设置成功以后的回调
        LinkKit.getInstance().publish( request, new IConnectSendListener() {
            @Override
            public void onResponse(ARequest aRequest, AResponse aResponse) {
                if (aResponse != null) Log.i( TAG, "onResponse:" + aResponse.getData() );
            }

            @Override
            public void onFailure(ARequest aRequest, AError aError) {
                if (aError != null) Log.e( TAG, "onFailure:" + aError.getCode() + aError.getMsg() );
            }
        } );
    }

    /**
     * 向阿里云服务器发送消息
     *
     * @param info      回调信息
     * @param topicName topic名称
     */
    public void publishByName(final String topicName, String info) {
        this.publish( topicName, "", info );

    }

    /**
     * 向阿里云服务器发送消息
     *
     * @param info      回调信息
     * @param topicPath topic路径
     */
    public void publishByPath(final String topicPath, String info) {
        this.publish( "", topicPath, info );
    }

    /**
     * 订阅自定义的topic
     *
     * @param topicName 自定义topic名称
     */
    private synchronized void subscribe(final String topicName, final String topicPath, final SubStatusListener subStatusListener) {

        final String[] msg = new String[1];

        final MqttRrpcRegisterRequest registerRequest = new MqttRrpcRegisterRequest();

        //设备服务功能响应后缀
        String TOPIC_REPLAY = "_reply";
        if (StringUtils.isEmptyString( topicPath )) {
            // rrpcTopic 替换成用户自己自定义的 RRPC topic
            registerRequest.topic = AliYunIotUtils.AlI_CUSTOM_TOPIC + topicName;
        } else {
            // rrpcTopic 替换成用户自己自定义的 RRPC topic
            registerRequest.topic = topicPath;
        }

        //回复topic
        registerRequest.replyTopic = registerRequest.topic + Constants.TOPIC_REPLAY;


        // 先订阅 rrpcTopic
        // 云端发布消息到 rrpcTopic
        // 收到下行数据 回复云端（rrpcReplyTopic） 具体可参考 Demo 同步服务调用
        LinkKit.getInstance().subscribeRRPC( registerRequest, new IConnectRrpcListener() {

            @Override
            public void onSubscribeSuccess(ARequest aRequest) {
                // 订阅成功
                msg[0] = "订阅“" + registerRequest.topic + "”成功:" + aRequest.toString();
                AUVLogUtil.d( TAG, msg[0] );
                subStatusListener.subStatusListener( true, msg[0] );

            }

            @Override
            public void onSubscribeFailed(ARequest aRequest, AError aError) {
                // 订阅失败
                msg[0] = "订阅“" + registerRequest.topic + "”失败:" + aError.getMsg() + ";" + aError.getSubMsg();
                AUVLogUtil.e( TAG, msg[0] );
                subStatusListener.subStatusListener( false, "订阅“" + registerRequest.topic + "”失败:" + aError.getMsg() + ";" + aError.getSubMsg() );


            }

            @Override
            public void onReceived(ARequest aRequest, IConnectRrpcHandle iConnectRrpcHandle) {
                // 收到云端下行
                String downstreamData = new String( (byte[]) ((MqttRrpcRequest) aRequest).payloadObj );
                AUVLogUtil.d( TAG, "云端下行数据" + downstreamData );
                if (StringUtils.isEmptyString( downstreamData )) {
                    AUVLogUtil.e( TAG, "云端下行指令为空" );
                    return;
                }

                //云端下行json数据
                JSONObject downstreamDataJson = null;

                //转换为json数据
                try {
                    downstreamDataJson = JSONObject.parseObject( downstreamData );
                } catch (Exception e) {
                    e.printStackTrace();
                    AUVLogUtil.e( TAG, "云端数据格式错误" );
                }

                //消息id
                String requestId = "";
                if (downstreamDataJson != null) {
                    requestId = downstreamDataJson.getString( "id" );

                }

                String finalId = requestId;

                ThreadPoolUtil.executorService.execute( () -> {
                    String idMsg = "";
                    // 响应获取成功
                    String topic = ((MqttRrpcRequest) aRequest).topic + Constants.TOPIC_REPLAY;
                    if (TextUtils.isEmpty( finalId )) {
                        idMsg = "云端数据格式错误";
                    } else {
                        idMsg = finalId;
                    }
                    String resultMsg = Result.success( "消息到达" ).put( "id", idMsg ).toJsonString();
                    // 响应获取成功回调
                    publishByPath( topic, resultMsg );

                } );
                //回调下行监听数据
                subStatusListener.downstreamDataListener( (MqttRrpcRequest) aRequest, downstreamDataJson );

            }

            @Override
            public void onResponseSuccess(ARequest aRequest) {
                // RRPC 响应成功
            }

            @Override
            public void onResponseFailed(ARequest aRequest, AError aError) {
                // RRPC 响应失败
            }
        } );

    }

    /**
     * 订阅自定义topic
     *
     * @param topicName         topic名字
     * @param subStatusListener 订阅结果
     */
    public void subscribeByName(final String topicName, final SubStatusListener subStatusListener) {
        subscribe( topicName, "", subStatusListener );
    }

    /**
     * 订阅服务调用topic
     *
     * @param topicPath         topic全路径
     * @param subStatusListener 订阅结果
     */
    public void subscribeByPath(final String topicPath, final SubStatusListener subStatusListener) {
        subscribe( "", topicPath, subStatusListener );
    }


}

