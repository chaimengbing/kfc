package com.auv.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.alink.apiclient.utils.StringUtils;
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentNet;
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.MqttConfigure;
import com.aliyun.alink.linksdk.cmp.api.ConnectSDK;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttRrpcRequest;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener;
import com.aliyun.alink.linksdk.id2.Id2ItlsSdk;
import com.aliyun.alink.linksdk.tools.AError;
import com.auv.constant.Constants;
import com.auv.hardware.HardwareService;
import com.auv.listener.LinkKitConnectListener;
import com.auv.model.AliCommand;
import com.auv.model.AliCommandModel;
import com.auv.model.AliControlAppModel;
import com.auv.model.AliControlDataModel;
import com.auv.model.AliOp;
import com.auv.model.IDModel;
import com.auv.model.Result;
import com.auv.standard.hardware.utils.LogUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AliYunIotUtils {

    private static final String TAG = "AliYunIotUtils";
    //设备服务功能响应后缀
    public static final String TOPIC_REPLAY = "_reply";
    public static final String OPERATION_CELL = "thing.service.operationCell";

    //接收命令
    public static final int OPEN = 1;
    public static final int CLOSE = 0;
    public static final String DOOR = "_door";
    public static final String LIGHT = "_light";
    public static final String WARM = "_warm";
    public static final String DISINFECT = "_disinfect";
    public static final String INDICATOR = "_indicator_light";
    public static final String MULTIOPERATION = "_mul";

    public static final String ansy = "ansyResult";

    /*
   阿里定义好的topic路径
*/
    public static String AlI_SERVICE_TOPIC = "/sys/" + AliYunIotUtils.getInstance().getProductKey() + "/" + AliYunIotUtils.getInstance().getDeviceName() + "/thing/service/";

    /*
       自定义topic
     */
    public static String AlI_CUSTOM_TOPIC;
    private OnSyncCellInfoListener onSyncCellInfoListener;


    private static AliYunIotUtils aliYunIotUtils;
    private Context context;
    private LinkKitConnectListener linkKitConnectListener;
    private String productKey;
    private String deviceName;
    private String deviceSecret;

    private IotConnectListener iotConnectListener;

    private boolean isConnected = false;
    public int reConnectionCount = 1;
    private Future isInitDoneFuture;
    private HardwareService hardwareService;
    private Timer nofityMessageTimer;

    private AliYunIotUtils(Context context) {
        this.context = context;
    }

    private AliYunIotUtils() {
    }

    public String getProductKey() {
        return productKey;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceSecret() {
        return deviceSecret;
    }


    public static AliYunIotUtils getInstance() {
        if (aliYunIotUtils == null) {
            aliYunIotUtils = new AliYunIotUtils();

        }
        return aliYunIotUtils;
    }

    public void setLinkKitConnectListener(LinkKitConnectListener linkKitConnectListener) {
        this.linkKitConnectListener = linkKitConnectListener;
    }


    public void init(Context context, String deviceName, String productKey, String deviceSecret) {
        this.context = context;
        this.deviceName = deviceName;
        this.productKey = productKey;
        this.deviceSecret = deviceSecret;
        AlI_SERVICE_TOPIC = "/sys/" + productKey + "/" + deviceName + "/thing/service/";
//        TOPIC_OPERATION_CELL = "/sys/" + productKey + "/" + deviceName + "/thing/service/operationCell";
        AlI_CUSTOM_TOPIC = "/" + productKey + "/" + deviceName + "/user/";
    }

    public void disConnect() {
        LinkKit.getInstance().unRegisterOnPushListener( notifyListener );
        LinkKit.getInstance().deinit();

        if (nofityMessageTimer != null) {
            nofityMessageTimer.cancel();
            nofityMessageTimer = null;
        }
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }


    public void connectAliYun() {
        if (isInitDoneFuture != null) {
            isInitDoneFuture.cancel( true );
            isInitDoneFuture = null;
        }
        disConnect();
        //设置超时时间
        MqttConfigure.setKeepAliveInterval( 30 );
        //开启重连
        MqttConfigure.automaticReconnect = true;
        isInitDoneFuture = ThreadPoolUtil.callBackFailThreadPool.scheduleAtFixedRate( () -> {
            AUVLogUtil.d( TAG, "正在进行第 " + reConnectionCount + " 次iot平台连接" );
            InitManager.init( context, productKey, deviceName, deviceSecret, deviceSecret, new ILinkKitConnectListener() {
                @Override
                public void onError(AError aError) {
                    isConnected = false;
                    reConnectionCount++;
                    if (reConnectionCount >= 60) {
                        cancelFuture();
                    }
                    AUVLogUtil.d( TAG, "onError 初始化失败！" + "onError() called with: aError = [" + aError.getMsg() + "]" );
                    if (linkKitConnectListener != null) {
                        linkKitConnectListener.onError( aError.getCode(), aError.getMsg() );
                    }
                }

                @Override
                public void onInitDone(Object data) {
                    AUVLogUtil.d( TAG, "onInitDone() called with: data = [" + data + "]" );
                    connectSuccess();
                }
            } );

        }, 0, 1, TimeUnit.MINUTES );

        LinkKit.getInstance().registerOnPushListener( notifyListener );
    }

    /**
     * 订阅
     */
    private void subscription(@NonNull final String topic) {
        LinkkitUtils.getInstance().subscribeByPath( topic, new LinkkitUtils.SubStatusListener() {
            @Override
            public void subStatusListener(boolean success, String msg) {
                AUVLogUtil.i( TAG, "\"" + topic + "\",订阅 ：" + (success ? "成功" : "失败") );
            }

            @Override
            public void downstreamDataListener(MqttRrpcRequest mqttRrpcRequest, JSONObject downstreamData) {
            }
        } );

    }

    private void connectSuccess() {
        cancelFuture();
        syncDeviceInfo();
        if (linkKitConnectListener != null) {
            linkKitConnectListener.onConnectSuccess();
        }
        subscription( Constants.opCell() );
        subscription( Constants.action() );
    }

    IConnectNotifyListener notifyListener = new IConnectNotifyListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onNotify(String connectId, String topic, AMessage aMessage) {
            // 云端下行数据回调
            // connectId 连接类型 topic 下行 topic; aMessage 下行数据
            // 数据解析如下：
            String pushData = new String( (byte[]) aMessage.data );
            AUVLogUtil.d( TAG, "onNotify::pushData:" + pushData );
            handPushData( connectId, pushData, topic );
//            handCloudCommand( topic, pushData );
            // pushData 示例  {"method":"thing.service.test_service","id":"123374967","params":{"vv":60},"version":"1.0.0"}
            // method 服务类型； params 下推数据内容
        }

        @Override
        public boolean shouldHandle(String connectId, String topic) {
            // 选择是否不处理某个 topic 的下行数据
            // 如果不处理某个topic，则onNotify不会收到对应topic的下行数据
            AUVLogUtil.d( TAG, "shouldHandle::connectId:" + connectId );
            return true;
        }

        @Override
        public void onConnectStateChange(String connectId, ConnectState connectState) {
            // 对应连接类型的连接状态变化回调，具体连接状态参考 SDK ConnectState
            String connect = "";
            isConnected = false;
            if (connectState == ConnectState.CONNECTED) {
                connect = "CONNECTED";
                isConnected = true;
                if (iotConnectListener != null) {
                    iotConnectListener.IotConnectResult( true );
                }
            } else if (connectState == ConnectState.DISCONNECTED) {
                connect = "DISCONNECTED";
                if (iotConnectListener != null)
                    iotConnectListener.IotConnectResult( false );
            } else if (connectState == ConnectState.CONNECTING) {
                connect = "CONNECTING";
            } else if (connectState == ConnectState.CONNECTFAIL) {
                connect = "CONNECTFAIL";
                if (iotConnectListener != null)
                    iotConnectListener.IotConnectResult( false );
            }
            AUVLogUtil.d( TAG, "onConnectStateChange::connectId:" + connectId + ",connectState:" + connect );
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void handPushData(String connectId, String pushData, String topic) {
        AUVLogUtil.d( TAG, "handPushData::connectId:" + connectId );
        JSONObject jsonDownstreamData = null;
        String requestId;
        String fields = Constants.ID;
        try {
            jsonDownstreamData = JSONObject.parseObject( pushData );
            requestId = jsonDownstreamData.getString( Constants.ID );
            if (TextUtils.isEmpty( requestId )) {
                JSONObject dataJSONObject = jsonDownstreamData.getJSONObject( Constants.DATA );
                requestId = dataJSONObject.getString( Constants.SESSION_ID );
                fields = Constants.SESSION_ID;
            }

        } catch (Exception e) {
            AUVLogUtil.e( TAG, e.getMessage() );
            requestId = Constants.DATA_INCORRECT;
        }

               /*
                   消息到达回复
                */
        replyMessage( fields, requestId, Constants.replayConstructor( topic ) );

        if (jsonDownstreamData != null) {
            if (ConnectSDK.getInstance().getPersistentConnectId().equals( connectId )) {
                if (!TextUtils.isEmpty( topic )) {
                    if (Constants.opCell().equals( topic )) {
//                        handCloudCommand( topic,pushData );
                        operationCell( jsonDownstreamData );
                    } else if (Constants.action().equals( topic )) {
                        operationApp( topic, jsonDownstreamData );
                    }
                } else {
                    AUVLogUtil.d( TAG, "下行topic为空" );
                }
            } else {
                AUVLogUtil.d( TAG, "下行topic未连接" );
            }
        }
    }

    /**
     * 远程控制app
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void operationApp(String topic, JSONObject downstreamData) {
        AUVLogUtil.d( TAG, "operationApp::topic:" + topic );
        // 执行结果
        Result result = Result.success();

        AliControlAppModel aliControlAppModel = JSONUtils.parseObject( downstreamData.toJSONString(), AliControlAppModel.class );

        try {

            String action = aliControlAppModel.getAction();
            AliControlDataModel aliControlDataModel = aliControlAppModel.getData();
            String target = aliControlDataModel.getTarget();


            if (Constants.TARGET_APP.equals( target )) {
                String downloadPath = aliControlDataModel.getDownloadPath();
                String packageName = aliControlDataModel.getPackageName();
                int versionCode = aliControlDataModel.getVersionCode();
                String versionName = aliControlDataModel.getVersionName();
                //如果是操作APP
                switch (action) {
                    case Constants.ACTION_ITL:
                        result = NetworkUtils.getInstance( context ).downLoadApkAndInstall( downloadPath, packageName );
                        break;
                    case Constants.ACTION_RBT:
                        Utils.rebootApp( context, packageName );
                        break;
                    case Constants.ACTION_UDT:
                        result = NetworkUtils.getInstance( context ).updateAndReboot( downloadPath, packageName, versionCode, versionName );
                        break;
                    case Constants.ACTION_UITL:
                        NetworkUtils.getInstance( context ).unInstall( packageName );
                        break;
                    case Constants.ACTION_OPEN:
                        result = Utils.openApp( context, packageName );
                        break;
                    case Constants.ACTION_CLOSE:
                        result = Utils.closeOtherApp( context, packageName );
                        break;
                    case Constants.ACTION_QUERY:
                        result = NetworkUtils.getInstance( context ).queryAppInstall( packageName, context );
                        break;
                    case Constants.ACTION_SYNC_DEVICE_INFO:
                        Utils.syncInfo( context );
                        break;
                    case Constants.ACTION_SYNC_CELL_INFO:
                        //TODO 同步格子信息
                        if (onSyncCellInfoListener != null){
                            onSyncCellInfoListener.syncCellInfoListener();
                        }
                        break;
                    default:
                        result = Result.error( Constants.UNKONW_ACTION );
                        break;

                }
            } else if (Constants.TARGET_DVC.equals( target )) {
                //如果是操作设备
                switch (action) {
                    case Constants.ACTION_RBT:
                        Utils.rebootDevice();
                        break;
                    case Constants.CURRENT_LOG:
                        UploadLogUtils.getInstance().uploadCurrentLog( context );
                        break;
                    case Constants.ALL_LOG:
                        UploadLogUtils.getInstance().uploadAllLog( context );
                        break;
                    default:
                        result = Result.error( Constants.UNKONW_ACTION );
                        break;
                }
            } else {
                result = Result.error( Constants.UNKONW_TARGET );
            }
        } catch (Exception e) {
            AUVLogUtil.e( TAG, e );
            result = Result.error( e.getMessage() );
        }
        AUVLogUtil.d( TAG, "operationApp::result:" + result.getMsg() );
        downstreamData.fluentPutAll( result );
        AUVLogUtil.d( TAG, downstreamData.toJSONString() );
        LinkkitUtils.getInstance().publishByPath( Constants.replayConstructor( topic ), downstreamData.toJSONString() );

    }


    /**
     * 远程操作格口
     */
    private void operationCell(JSONObject downstreamData) {
        AUVLogUtil.d( TAG, "operationCell::downstreamData:" + downstreamData );
        //下发的参数
        JSONObject info = downstreamData.getJSONObject( "params" );
        if (info == null) {
            AUVLogUtil.d( TAG, " 参数为空" );
            return;

        }
        if (hardwareService == null) {
            AUVLogUtil.d( TAG, "hardwareService is null" );
            return;
        }
        //命令信息
        String commandList = info.getString( "commandList" );
        JSONArray CommandList = JSONObject.parseArray( commandList );
        for (int i = 0; i < CommandList.size(); i++) {
            Integer cellNo = CommandList.getJSONObject( i ).getInteger( "cellNo" );
            if (cellNo == null) {
                cellNo = 0;
            }
            JSONArray opList = CommandList.getJSONObject( i ).getJSONArray( "opList" );
            for (int a = 0; a < opList.size(); a++) {
                Integer command = opList.getJSONObject( a ).getInteger( "command" );
                String property = opList.getJSONObject( a ).getString( "property" );
                if (command == null || StringUtils.isEmptyString( property )) {
                    AUVLogUtil.d( TAG, "命令串格式错误" );
                    return;

                } else {

                    switch (property) {
                        //门
                        case Constants.DOOR:
                            if (command == Constants.OPEN) {
                                hardwareService.openDoor( cellNo );
                            }
                            break;
                        //开灯
                        case Constants.LIGHT:
                            if (command == Constants.OPEN) {
                                hardwareService.openLight( cellNo );
                            } else if (command == Constants.CLOSE) {
                                hardwareService.closeLight( cellNo );
                            }
                            break;
                        //加热
                        case Constants.WARM:
                            if (command == Constants.OPEN) {
                                hardwareService.openHeating( cellNo );
                            } else if (command == Constants.CLOSE) {
                                hardwareService.closeHeating( cellNo );
                            }
                            break;
                        //消毒灯
                        case Constants.DISINFECT:
                            if (command == Constants.OPEN) {
                                hardwareService.openDisinfect( cellNo );
                            } else if (command == Constants.CLOSE) {
                                hardwareService.closeDisinfect( cellNo );
                            }
                            break;
                        //指示灯
                        case Constants.INDICATOR:
                            break;
                    }

                }
            }
        }
    }


    /**
     * 消息下行回复
     *
     * @param fields 回复消息字段
     * @param value  回复消息字段值
     * @param topic  回复消息topic
     */
    private void replyMessage(@NonNull final String fields, @NonNull final String value, @NonNull final String topic) {
        ThreadPoolUtil.executorService.execute( () -> {
            IDModel idModel = new IDModel();
            idModel.setId( value );
            idModel.setCode( 200 );
            idModel.setMsg( Constants.MSG_ARRIVE );
            String resultMsg = JSONUtils.toJSONString( idModel );
            // 响应获取成功回调
            LinkkitUtils.getInstance().publishByPath( topic, resultMsg );

        } );
    }

    private void handCloudCommand(String topic, String pushData) {
        AUVLogUtil.d( TAG, "handCloudCommand::topic:" + topic + ",pushData:" + pushData );
        AliCommandModel aliCommandModel = JSONUtils.parseObject( pushData, AliCommandModel.class );
        if (aliCommandModel != null && aliCommandModel.getParams() != null) {
            if (OPERATION_CELL.equals( aliCommandModel.getMethod() )) {
                sendMessageToCloud( topic, aliCommandModel.getId() );
                handCell( aliCommandModel.getParams().getCommandList() );
            }
        } else {
            AUVLogUtil.d( TAG, "handCloudCommand::aliCommandModel is null" );
        }
    }

    private void sendMessageToCloud(String topic, String id) {
        IDModel idModel = new IDModel();
        idModel.setId( id );
        String idString = JSONUtils.toJSONString( idModel );
        LinkkitUtils.getInstance().publish( "", topic + Constants.TOPIC_REPLAY, idString );
    }

    private void handCell(String commandList) {
        List<AliCommand> aliCommands = JSONUtils.parseArray( commandList, AliCommand.class );
        if (aliCommands != null) {
            for (AliCommand aliCommand : aliCommands) {
                List<AliOp> aliOps = aliCommand.getOpList();
                if (aliOps != null) {
                    for (AliOp aliOp : aliOps) {
                        if (!TextUtils.isEmpty( aliOp.getProperty() )) {
                            if (aliOp.getCommand() == OPEN) {
                                openCommand( aliOp.getProperty(), aliCommand.getCellNo() );
                            } else {
                                closeCommand( aliOp.getProperty(), aliCommand.getCellNo() );
                            }
                        }
                    }
                }
            }
        } else {
            AUVLogUtil.d( TAG, "handCloudCommand::aliCommands is null" );
        }
    }

    private void openCommand(String property, int cellNo) {
        if (hardwareService == null) {
            AUVLogUtil.d( TAG, "hardwareService is null" );
            return;
        }
        AUVLogUtil.d( TAG, "openCommand::" + property + ",cellNo:" + cellNo );
        if (DOOR.equals( property )) {
            hardwareService.openDoor( cellNo );
        } else if (DISINFECT.equals( property )) {
            hardwareService.openDisinfect( cellNo );
        } else if (WARM.equals( property )) {
            hardwareService.openHeating( cellNo );
        } else if (LIGHT.equals( property )) {
            hardwareService.openLight( cellNo );
        } else if (INDICATOR.equals( property )) {
            hardwareService.openIndicatorLight( cellNo );
        }
    }

    private void closeCommand(String property, int cellNo) {
        if (hardwareService == null) {
            AUVLogUtil.d( TAG, "hardwareService is null" );
            return;
        }
        AUVLogUtil.d( TAG, "closeCommand::" + property + ",cellNo:" + cellNo );
        if (DISINFECT.equals( property )) {
            hardwareService.closeDisinfect( cellNo );
        } else if (WARM.equals( property )) {
            hardwareService.closeHeating( cellNo );
        } else if (LIGHT.equals( property )) {
            hardwareService.closeLight( cellNo );
        } else if (INDICATOR.equals( property )) {
            hardwareService.closeIndicatorLight( cellNo );
        }
    }


    public void setHardwareService(HardwareService hardwareService) {
        this.hardwareService = hardwareService;
    }


    /**
     * 取消重连Future
     */
    private void cancelFuture() {
        if (isInitDoneFuture != null) {
            isInitDoneFuture.cancel( true );
            isInitDoneFuture = null;
        }
    }


    public void setIotConnectListener(IotConnectListener iotConnectListener) {
        this.iotConnectListener = iotConnectListener;
    }

    private void syncDeviceInfo() {
        AUVLogUtil.d( TAG, "syncDeviceInfo::" );
        if (nofityMessageTimer != null) {
            nofityMessageTimer.cancel();
            nofityMessageTimer = null;
        }
        nofityMessageTimer = new Timer();
        nofityMessageTimer.schedule( new TimerTask() {
            @Override
            public void run() {
                Utils.syncInfo( context );
            }
        }, 0, (Constants.SYNC_SDK_INFO_TIME * 1000) );

    }

    public boolean isConnected() {
        return isConnected;
    }


    /**
     * 连接结果
     */
    public interface IotConnectListener {
        void IotConnectResult(boolean success);
    }

    /**
     * 同步格口信息
     *
     * @param onSyncCellInfoListener 格口信息监听器
     */
    public void setOnSyncCellInfoListener(OnSyncCellInfoListener onSyncCellInfoListener) {
        this.onSyncCellInfoListener = onSyncCellInfoListener;
    }


    /**
     * 同步格口信息监听接口
     */
    public interface OnSyncCellInfoListener {
        void syncCellInfoListener();
    }

}
